package intrusionSimulation;

import eu.iv4xr.framework.mainConcepts.WorldEntity;
import eu.iv4xr.framework.spatial.Vec3;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SeStarSocketEnvironment extends ISSocketEnvironment {

    private static final Logger LOGGER = Logger.getLogger(SeStarSocketEnvironment.class.getName());
    public static final Level logLevel = Level.FINEST; // Use info to see the message logs

    private static class SpatialElement {
        int id;
        Vec3 position;
        Vec3 velocity;
        int[] detectedIds;

        public SpatialElement(int id, Vec3 position, Vec3 velocity, int[] detectedIds) {
            this.id = id;
            this.position = position;
            this.velocity = velocity;
            this.detectedIds = detectedIds;
        }
    }

    //initialise socket and input output streams
    Socket socket;

    int intruderId = -1;
    Map<Integer, String> entityFullNamesById = new HashMap<>();

    // constructor to put ip address and port
    public SeStarSocketEnvironment(String address, int port)
    {
        int maxWaitTime = 20000;
        System.out.println(String.format("Trying to connect with client on %s:%s (will time-out after %s seconds).", address, port, maxWaitTime/1000));
        long startTime = System.nanoTime();

        while (!socketReady() && millisElapsed(startTime) < maxWaitTime)
        {
            // establish a connection
            try {
                SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(address, port));
                this.socket = socketChannel.socket();
                System.out.println("Just connected to " + socket.getRemoteSocketAddress());
            } catch(IOException u)
            {
                System.out.println(u);
            }
        }

        if(socketReady()) {
            System.out.println(String.format("Connected with server on %s:%s", address, port));
        }
        else {
            System.out.println(String.format("Could not establish a connection with server."));
        }
    }

    /**
     * @return true if the socket and input output streams are not null
     */
    private boolean socketReady(){
        return socket != null;
    }

    /**
     * @param startTimeNano the start time in long
     * @return the elapsed time from the start time converted to milliseconds
     */
    private float millisElapsed(long startTimeNano){
        return (System.nanoTime() - startTimeNano) / 1000000f;
    }

    public boolean closeSocket()
    {
        try {
            if (socket != null)
                socket.close();

            System.out.println(String.format("Disconnected from the host."));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println(String.format("Could not disconnect from the host by closing the socket."));
            return false;
        }

        return true;
    }

    private ISObservation computeObservation() {
        try {
            // 1. Set the intruder ID
            ISObservation observation = new ISObservation();
            observation.agentId = entityFullNamesById.get(intruderId);
            // 2. Get position, velocity and detections through the Ground Truth
            SeStarMessage getGroundTruth = SeStarMessage.outgoingMessage(
                    SeStarMessage.MessageType.GET_GROUND_TRUTH_LITE
            );
            getGroundTruth.putBool(false); // do not use sense for detections
            getGroundTruth.toChannel(socket.getChannel());
            SeStarMessage dataGroundTruth;
            do {
                dataGroundTruth = SeStarMessage.fromChannel(socket.getChannel());
            } while (dataGroundTruth.type != SeStarMessage.MessageType.DATA_GROUND_TRUTH_LITE);
            boolean isIntruderDetected = false;
            Map<Integer, SpatialElement> spatialElementById = new HashMap<>();
            int spatialElementNumber = dataGroundTruth.body.getInt();
            for (int i = 0; i < spatialElementNumber; i++) {
                int entityId = dataGroundTruth.body.getInt();
                LOGGER.log(logLevel, "Adding SpatialElemement of Entity #" + entityId);
                // Position
                float px = (float) dataGroundTruth.body.getDouble();
                float py = (float) dataGroundTruth.body.getDouble();
                float pz = (float) dataGroundTruth.body.getDouble();
                // Orientation as quaternion (not used)
                double qx = dataGroundTruth.body.getDouble();
                double qy = dataGroundTruth.body.getDouble();
                double qz = dataGroundTruth.body.getDouble();
                double qw = dataGroundTruth.body.getDouble();
                // Velocity
                float vx = (float) dataGroundTruth.body.getDouble();
                float vy = (float) dataGroundTruth.body.getDouble();
                float vz = (float) dataGroundTruth.body.getDouble();
                // detected ids
                int detectionNumber = dataGroundTruth.body.getInt();
                int[] detectedIds = new int[detectionNumber];
                for (int d = 0; d < detectionNumber; d++) {
                    detectedIds[d] = dataGroundTruth.body.getInt();
                    if (entityId != intruderId && detectedIds[d] == intruderId) {
                        isIntruderDetected = true;
                    }
                }
                SpatialElement spatialElement = new SpatialElement(
                        entityId, new Vec3(px, py, pz), new Vec3(vx, vy, vz), detectedIds
                );
                spatialElementById.put(entityId, spatialElement);
            }
            SpatialElement intruderSpatialElement = spatialElementById.get(intruderId);
            observation.position = intruderSpatialElement.position;
            observation.velocity = intruderSpatialElement.velocity;
            for (int detectedId : intruderSpatialElement.detectedIds) {
                SpatialElement detectedSpatialElement = spatialElementById.get(detectedId);
                WorldEntity detectedEntity = new WorldEntity(entityFullNamesById.get(detectedId), "", true);
                detectedEntity.position = detectedSpatialElement.position;
                detectedEntity.velocity = detectedSpatialElement.velocity;
                observation.elements.put(detectedEntity.id, detectedEntity);
            }
            observation.isDetected = isIntruderDetected;
            // 3. Timestamp with the simulated time
            SeStarMessage getTimeMessage = SeStarMessage.outgoingMessage(SeStarMessage.MessageType.GET_SIMULATION_TIME);
            getTimeMessage.toChannel(socket.getChannel());
            SeStarMessage dataTimeMessage = null;
            do {
                dataTimeMessage = SeStarMessage.fromChannel(socket.getChannel());
            } while (dataTimeMessage.type != SeStarMessage.MessageType.DATA_SIMULATION_TIME);
            observation.timestamp = (long) (1000 * dataTimeMessage.body.getDouble());
            return observation;
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
            return null;
        }
    }

    /**
     * @param cmd representing the command to send to the real environment.
     * @return an object that the real environment sends back as the result of the
     * command, if any.
     */
    @Override
    protected Object sendCommand_(EnvOperation cmd) {
        try {
            switch (cmd.command) {
                case "reset":
                    intruderId = -1;
                    entityFullNamesById.clear();
                    // Pause the simulator
                    SeStarMessage.outgoingMessage(SeStarMessage.MessageType.SIMULATION_PAUSE).toChannel(socket.getChannel());
                    // Delete all entities
                    SeStarMessage.outgoingMessage(SeStarMessage.MessageType.DELETE_ALL_ENTITIES).toChannel(socket.getChannel());
                    // Set all the quotas to 0 (will trigger respawning the entities
                    String[] spawners = {
                            "Intruder Spawner", "Camera Spawner 3", "Camera Spawner 4",
                            "Camera Spawner 5", "Camera Spawner 6", "Guard Spawner 7",
                            "Guard Spawner 8", "Camera Spawner 9", "Camera Spawner 10",
                            "Camera Spawner 11", "Camera Spawner 12", "Camera Spawner 13",
                            "Camera Spawner 14", "Camera Spawner 15", "Camera Spawner 16",
                            "Camera Spawner 17", "Camera Spawner 18", "Guard Spawner 19",
                            "Guard Spawner 20", "Guard Spawner 21", "Guard Spawner 22"
                    };
                    for (String spawner : spawners) {
                        SeStarMessage changeVariableMessage = SeStarMessage.outgoingMessage(
                                SeStarMessage.MessageType.SMARTOBJECT_FROM_NAME_CHANGE_VAR
                        );
                        changeVariableMessage.putString(spawner);
                        changeVariableMessage.putString("quota");
                        changeVariableMessage.body.putFloat(1.0f);
                        changeVariableMessage.toChannel(socket.getChannel());
                    }
                    SeStarMessage advanceSteps = SeStarMessage.outgoingMessage(SeStarMessage.MessageType.ADVANCE_STEPS);
                    advanceSteps.body.putInt(2);
                    advanceSteps.toChannel(socket.getChannel());
                    // Fetch all World Entities
                    ArrayList<SeStarMessage> createEntities = new ArrayList<>();
                    SeStarMessage notifyAdvanceSteps = null;
                    do {
                        SeStarMessage incomingMessage = SeStarMessage.fromChannel(socket.getChannel());
                        if (incomingMessage.type == SeStarMessage.MessageType.NOTIFY_CREATE_SYNTHETIC_ENTITY) {
                            createEntities.add(incomingMessage);
                        } else if (incomingMessage.type == SeStarMessage.MessageType.NOTIFY_ADVANCE_STEPS) {
                            notifyAdvanceSteps = incomingMessage;
                        }
                    } while (notifyAdvanceSteps == null);
                    // Identify the intruder agent
                    for (SeStarMessage createEntity : createEntities) {
                        int id = createEntity.body.getInt();
                        short gender = createEntity.body.getShort();
                        short age = createEntity.body.getShort();
                        String model = createEntity.getString();
                        String fullName = model + "#" + id;
                        entityFullNamesById.put(id, fullName);
                        LOGGER.log(logLevel,"New entity : " + fullName);
                        if (model.equals("Intruder")) {
                            intruderId = id;
                        }
                    }
                    // Build the initial observation
                    // LegacyObservation observation = computeObservation();
                    // Play the simulator
                    SeStarMessage.outgoingMessage(SeStarMessage.MessageType.SIMULATION_PLAY).toChannel(socket.getChannel());
                    return true;
                case "goto":
                    SeStarMessage gotoMessage = SeStarMessage.outgoingMessage(SeStarMessage.MessageType.ENTITY_GOTO);
                    gotoMessage.body.putInt(intruderId);
                    ISAgentCommand.MoveToArgument moveToArgs = (ISAgentCommand.MoveToArgument) cmd.arg;
                    gotoMessage.body.putDouble(moveToArgs.x);
                    gotoMessage.body.putDouble(moveToArgs.y);
                    gotoMessage.body.putDouble(ConfigEnvironment.SE_STAR_Z);
                    gotoMessage.putBool(false); // no replan on success
                    gotoMessage.putBool(false); // no reaction to brain stimuli
                    gotoMessage.putBool(false); // cartesian coordinates
                    gotoMessage.toChannel(socket.getChannel());
                    return computeObservation();
                case "donothing":
                    return computeObservation();
                case "simulation_time":
                    SeStarMessage getTimeMessage = SeStarMessage.outgoingMessage(SeStarMessage.MessageType.GET_SIMULATION_TIME);
                    getTimeMessage.toChannel(socket.getChannel());
                    SeStarMessage dataTimeMessage = null;
                    do {
                        dataTimeMessage = SeStarMessage.fromChannel(socket.getChannel());
                    } while (dataTimeMessage.type != SeStarMessage.MessageType.DATA_SIMULATION_TIME);
                    return dataTimeMessage.body.getDouble();
                case "set_time_factor":
                    SeStarMessage setTimerProperties = SeStarMessage.outgoingMessage(SeStarMessage.MessageType.SET_TIMER_PROPERTIES);
                    setTimerProperties.body.putFloat((float) ((Double) cmd.arg).doubleValue());
                    setTimerProperties.body.putFloat(1.0f);
                    setTimerProperties.toChannel(socket.getChannel());
                default:
                    return null;
            }
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
            return null;
        }
    }

    /**
     * This method provides a higher level wrapper over Environment.sendCommand. It
     * calls Environment.sendCommand which in turn will call ISSocketEnvironement.sendCommand_
     * It will also cast the json back to type T.
     * @param request
     * @param <T> any response type
     * @return response
     */
    @SuppressWarnings("unchecked")
    public <T> T getISResponse(ISRequest<T> request) {
        String invokerId = "APlib";
        String targetId = "SEStar";

        switch (request.cmd) {
            case AGENTCOMMAND:
                ISAgentCommand agentCommand = (ISAgentCommand) request.arg;
                switch (agentCommand.cmd) {
                    case DONOTHING -> {
                        return (T) sendCommand(invokerId, targetId, "donothing", agentCommand.arg);
                    }
                    case MOVETO -> {
                        return (T) sendCommand(invokerId, targetId, "goto", agentCommand.arg);
                    }

                }
            case RESTART:
                return (T) sendCommand(invokerId, targetId, "reset", request.arg);
            case SIMULATION_TIME:
                return (T) sendCommand(invokerId, targetId, "simulation_time", request.arg);
            case SET_TIME_FACTOR:
                return (T) sendCommand(invokerId, targetId, "set_time_factor", request.arg);
            default:
                return null;
        }
    }

    /**
     * Close the socket and input output streams
     */
    public boolean close() {

        // try to disconnect
        //boolean success = getISResponse(ISRequest.disconnect());
        boolean success = true;

        if(success){
            try {
                if (socket != null)
                    socket.close();

                System.out.println(String.format("Disconnected from the host."));
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.out.println(String.format("Could not disconnect from the host by closing the socket."));
                return false;
            }
        }
        else {
            System.out.println(String.format("Client does not respond to a disconnection request."));
        }
        return success;
    }

}
