package intrusionSimulation;

import java.io.*;
import java.lang.reflect.Modifier;
import java.net.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import communication.adapters.EntityAdapter;
import communication.adapters.EntityTypeAdapter;
import nl.uu.cs.aplib.mainConcepts.Environment;
import world.LegacyEntity;
import world.LegacyEntityType;

public class ISSocketEnvironment extends Environment {

	//initialise socket and input output streams
	Socket socket;
	PrintWriter toServer;
	BufferedReader fromServer;

	// transient modifiers should be excluded, otherwise they will be send with json
	private static Gson gson = new GsonBuilder()
		.serializeNulls()
		.excludeFieldsWithModifiers(Modifier.TRANSIENT)
		.registerTypeAdapter(LegacyEntityType.class, new EntityTypeAdapter())
		.registerTypeHierarchyAdapter(LegacyEntity.class, new EntityAdapter())
		.create();

	public void run() {
		try {
			int serverPort = 8080;
			InetAddress host = InetAddress.getByName("localhost"); 
			System.out.println("Connecting to server on host: " + host + " on port: " + serverPort);

			Socket socket = new Socket(host, serverPort); 
			//Socket socket = new Socket("127.0.0.1", serverPort);
			System.out.println("Just connected to " + socket.getRemoteSocketAddress()); 

			toServer = new PrintWriter(socket.getOutputStream(),true);
			fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			//toServer.println("Hello from " + socket.getLocalSocketAddress()); 
			//String line = fromServer.readLine();
			//System.out.println("Client received: " + line + " from Server");
			
			toServer.close();
			fromServer.close();
			socket.close();
		}
		catch(UnknownHostException ex) {
			ex.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	// constructor to put ip address and port
	public ISSocketEnvironment(String address, int port)
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
	 * @return true if the socket and input output streams are not null
	 */
	private boolean socketReady(){
		return socket != null && toServer != null && fromServer != null;
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
					System.out.println("write to the socket:" + message);
					
                    String messageReceived = fromServer.readLine();
                    System.out.println("messageReceived: " + messageReceived);
                    		
					// read from the socket
					return messageReceived;
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
	 * This method provides a higher level wrapper over Environment.sendCommand. It
	 * calls Environment.sendCommand which in turn will call ISSocketEnvironement.sendCommand_
	 * It will also cast the json back to type T.
	 * @param request
	 * @param <T> any response type
	 * @return response
	 */
	public <T> T getISResponse(ISRequest<T> request) {
		String message = (String) sendCommand("APlib", "IS", "request", gson.toJson(request));
		/*if(!close())
			System.out.println(String.format("An error occurred while closing the connection"));*/
		return (T) gson.fromJson(message, request.responseType);
	}
}