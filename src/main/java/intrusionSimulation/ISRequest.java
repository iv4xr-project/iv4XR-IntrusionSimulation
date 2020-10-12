package intrusionSimulation;

import world.LegacyObservation;

/**
 * Request class provides utility functions that handles ResponseType casting
 */
public class ISRequest<ResponseType>  {

	/**
	 * Java can not determine the class of ResponseType at runtime.
	 * In this case, storing an instance of Class<ResponseType> to cast the response object is seen as good practice.
	 */
	public transient final Class<ResponseType> responseType;

	public ISRequestType cmd;
	public Object arg;

	/**
	 * This constructor is based on the sendCommand method from JsonEnvironment
	 */
	private ISRequest(Class<ResponseType> responseType, ISRequestType cmd, Object arg) {
		// convert the command to string
		this.responseType = responseType;

		this.cmd = cmd;
		this.arg = arg;
	}

	/**
	 * This constructor is based on the sendCommand method from JsonEnvironment
	 */
	private ISRequest(Class<ResponseType> responseType, ISRequestType cmd) {
		// convert the command to string
		this.responseType = responseType;

		this.cmd = cmd;
		this.arg = null;
	}

	/**
	 * Disconnect
	 * @return succes
	 */
	public static ISRequest<Boolean> disconnect() {
		return new ISRequest<>(Boolean.class, ISRequestType.DISCONNECT);
	}

	/**
	 * Pause simulation
	 * @return succes
	 */
	public static ISRequest<Boolean> pauseSimulation() {
		return new ISRequest<>(Boolean.class, ISRequestType.PAUSE);
	}

	/**
	 * Start simulation
	 * @return succes
	 */
	public static ISRequest<Boolean> startSimulation() {
		return new ISRequest<>(Boolean.class, ISRequestType.START);
	}

	/**
	 * Restart simulation
	 * @return succes
	 */
	public static ISRequest<Boolean> restartSimulation() {
	return new ISRequest<>(Boolean.class, ISRequestType.RESTART);
	}

	/**
	 * Request an observation after executing the sent Command
	 */
	public static ISRequest<LegacyObservation> command(ISAgentCommand c) {
	return new ISRequest<>(LegacyObservation.class, ISRequestType.AGENTCOMMAND, c);
	}
}
