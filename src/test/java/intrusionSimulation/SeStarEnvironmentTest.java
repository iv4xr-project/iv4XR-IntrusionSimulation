package intrusionSimulation;

import eu.iv4xr.framework.mainConcepts.WorldEntity;
import eu.iv4xr.framework.spatial.Vec3;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class SeStarEnvironmentTest {

    /**
     * Connect to a SE-Star Intrusion Simulation, send a command to the intruder
     * and check the evolution of its World Object Model.
     */
    @Test
    public void connectTest() {
        var config = new ConfigEnvironment();
        config.useSeStar = true;
        var env = new IntrusionSimulationEnvironment(config);

        int numberOfSteps = 30;

        env.sendRequest(ISRequest.restartSimulation());

        var destination = new Vec3(-25.45f , 266.85f, 0.9f); // Inside camera FOV
        var obs = (ISObservation) env.sendRequest(
                ISRequest.command(ISAgentCommand.moveToCommand(0, destination))
        );
        for (int s = 0; s < numberOfSteps; s++){
            // Test of time precision
            double simulationTime = env.sendRequest(ISRequest.getSimulationTime());
            long seconds = (long) simulationTime;
            long nanos = (long) ((simulationTime - seconds) * 1e9);
            LocalDateTime time = Instant.ofEpochSecond(seconds, nanos).atZone(ZoneId.systemDefault()).toLocalDateTime();
            System.out.println("Step " + s + ": Current simulation time: " + time);
            // Updating the WOM
            obs = env.sendRequest(ISRequest.command(ISAgentCommand.doNothing(0)));
            System.out.println("Intruder position: " + obs.position);
            String detections = "[";
            for (WorldEntity entity : obs.elements.values()) {
                detections += entity.id + " at " + entity.position + ", ";
            }
            detections += "]";
            System.out.println("Detections: " + detections);
            System.out.println("Is detected: " + obs.isDetected);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        if (!env.closeSocket())
            System.out.println("Server refuses to close the socket exchange");

    }

}
