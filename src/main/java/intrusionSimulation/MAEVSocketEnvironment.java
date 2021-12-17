package intrusionSimulation;

import com.google.gson.*;
import communication.adapters.MAEVEntityAdapter;
import eu.iv4xr.framework.mainConcepts.WorldEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.math.RoundingMode;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;

/**
 * Implementation of ISSocketEnvironment with MAEV as a simulation engine.
 * It establishes the TCP connection to MAEV and manages the interoperability
 * with JSON messages.
 */
public class MAEVSocketEnvironment extends ISSocketEnvironment {
	// Initialise socket and input output streams
	Socket socket;
	PrintWriter toServer;
	BufferedReader fromServer;

	// Transient modifiers should be excluded, otherwise they will be sent with json
	private static Gson gson = new GsonBuilder()
		.serializeNulls()
		.excludeFieldsWithModifiers(Modifier.TRANSIENT)
		.registerTypeAdapter(Double.class, (JsonSerializer<Double>) (src, typeOfSrc, context) -> {
					DecimalFormat df = new DecimalFormat("#.######");
					df.setRoundingMode(RoundingMode.CEILING);
					return new JsonPrimitive(Double.parseDouble(df.format(src)));
				})
		.registerTypeHierarchyAdapter(WorldEntity.class, new MAEVEntityAdapter())
		.create();

	/**
	 * Constructor. Set the IP and port of MAEV and connect as a client.
	 *
	 * @param address IP address of MAEV.
	 * @param port Listening port of MAEV.
	 */
	public MAEVSocketEnvironment(String address, int port)
	{
		int maxWaitTime = 20000;
		System.out.println(String.format("Trying to connect with client on %s:%s (will time-out after %s seconds).", address, port, maxWaitTime/1000));
		long startTime = System.nanoTime();

		while (!socketReady() && millisElapsed(startTime) < maxWaitTime)
		{
			// establish a connection
			try {
				socket = new Socket(address, port);
				System.out.println("Just connected to " + socket.getRemoteSocketAddress()); 
				 
				toServer = new PrintWriter(socket.getOutputStream(), true);
				fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			}
			catch(UnknownHostException u)
			{
				System.out.println(u);
			}
			catch(IOException i)
			{
				System.out.println(i);
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
	 * Check whether the connection is established.
	 *
	 * @return true if the socket and input output streams are not null
	 */
	private boolean socketReady(){
		return socket != null && toServer != null && fromServer != null;
	}

	/**
	 * Check how many milliseconds have elapsed since a start time.
	 *
	 * @param startTimeNano the start time in long
	 * @return the elapsed time from the start time converted to milliseconds
	 */
	private float millisElapsed(long startTimeNano){
		return (System.nanoTime() - startTimeNano) / 1000000f;
	}

	/**
	 * Close the TCP connection to the MAEV Intrusion Simulation environment.
	 *
	 * @return Whether the socket is closed.
	 */
	public boolean closeSocket()
	{
		try {
			if (toServer != null)
				toServer.close();
			if (fromServer != null)
				fromServer.close();
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
	
	/**
	 * Close the socket and input output streams
	 *
	 * 	@return Whether the socket is closed.
	 */
	public boolean close() {

		// try to disconnect
		boolean success = getISResponse(ISRequest.disconnect());

		if(success){
			try {
				if (toServer != null)
					toServer.close();
				if (fromServer != null)
					fromServer.close();
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

	/**
	 * Send a command to the MAEV environment and get the associated response. This
	 * private method manage the low-level stuff.
	 *
	 * @param cmd representing the command to send to the real environment.
	 * @return an object that the real environment sends back as the result of the
	 * command, if any.
	 */
	@Override
	protected Object sendCommand_(EnvOperation cmd) {
		// The Environment super class uses sendCommand_ to send the message object
		String message = (String) cmd.arg;
		
		JsonObject jsonObject = new JsonParser().parse(message).getAsJsonObject();
		String type = "";
		boolean exsuMessage = false;
		String reqType = jsonObject.getAsJsonObject().get("cmd").getAsString();
		
		if (reqType.equals("DISCONNECT")) {
			exsuMessage = true;
			type = "Close";
		}
		else if (reqType.equals("START")) {
			exsuMessage = true;
			type = "Play";
		}
		else if (reqType.equals("PAUSE")) {
			exsuMessage = true;
			type = "Pause";
		}
		else if (reqType.equals("RESTART")) {
			exsuMessage = true;
			type = "Stop";
		}

		if (exsuMessage)
			message = "{\"Service\":\"Simulation\",\"Command\":\"" + type + "\"}";

		switch (cmd.command) {
			case "debug":
				return printMessage(message);
			case "request":
				try {
					// write to the socket
					toServer.println(message);
					System.out.println("message sent: " + message);

					String messageReceived = fromServer.readLine();
					System.out.println("message received: " + messageReceived);
					var observation = gson.fromJson(messageReceived, ISObservation.class);
					// We recenter the positions on (0, 0) for better interpretability
					observation.position.x -= ConfigEnvironment.CENTER_X;
					observation.position.y -= ConfigEnvironment.CENTER_Y;
					for (var entity : observation.elements.values()) {
						entity.position.x -= ConfigEnvironment.CENTER_X;
						entity.position.y -= ConfigEnvironment.CENTER_Y;
					}
				} catch (IOException ex) {
					System.out.println("I/O error: " + ex.getMessage());
					return null;
				}
		}
		throw new IllegalArgumentException();
	}

	private String printMessage(String message){
		System.out.println("SENDING:" + message);
		return null;
	}

	/**
	 * Send a request through the TCP Socket to the MAEV Intrusion Simulation,
	 * and receive the associated response.
	 *
	 * @param request The request to the SUT.
	 * @param <T> The type of the expected response object.
	 * @return The response from the SUT.
	 */
	public <T> T getISResponse(ISRequest<T> request) {
		// We handle MAEV's coordinate system
		if (request.cmd == ISRequestType.AGENTCOMMAND) {
			var agentCommand = (ISAgentCommand) request.arg;
			if (agentCommand.cmd == ISAgentCommandType.MOVETO) {
				var moveToArgument = (ISAgentCommand.MoveToArgument) agentCommand.arg;
				moveToArgument.x += ConfigEnvironment.CENTER_X;
				moveToArgument.y += ConfigEnvironment.CENTER_Y;
			}
		}
		T result = (T) sendCommand("APlib", "IS", "request", gson.toJson(request));
		/*if(!close())
			System.out.println(String.format("An error occurred while closing the connection"));*/
		return result;
	}
}