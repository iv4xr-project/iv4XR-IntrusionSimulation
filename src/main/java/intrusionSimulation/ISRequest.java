package intrusionSimulation;

/**
 * Request class provides utility functions that handles ResponseType casting
 */
public class ISRequest<ResponseType>  {

	/**
	 * Java can not determine the class of ResponseType at runtime.
	 * In this case, storing an instance of Class<ResponseType> to cast
	 * the response object is seen as good practice.
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
	 * Disconnect from the SUT
	 *
	 * @return success
	 */
	public static ISRequest<Boolean> disconnect() {
		return new ISRequest<>(Boolean.class, ISRequestType.DISCONNECT);
	}

	/**
	 * Pause simulation
	 *
	 * @return success
	 */
	public static ISRequest<Boolean> pauseSimulation() {
		return new ISRequest<>(Boolean.class, ISRequestType.PAUSE);
	}

	/**
	 * Start simulation
	 *
	 * @return success
	 */
	public static ISRequest<Boolean> startSimulation() {
		return new ISRequest<>(Boolean.class, ISRequestType.START);
	}

	/**
	 * Restart simulation
	 *
	 * @return success
	 */
	public static ISRequest<Boolean> restartSimulation() {
	return new ISRequest<>(Boolean.class, ISRequestType.RESTART);
	}

	/**
	 * Get the current time in the simulation clock.
	 *
	 * @return current time as (double) seconds elapsed since epoch, with regard to the
	 * simulation clock's localtime.
	 */
	public static ISRequest<Double> getSimulationTime() { return new ISRequest<>(Double.class, ISRequestType.SIMULATION_TIME); }

	/**
	 * Set the time acceleration factor of the simulation.
	 *
	 * @param factor time acceleration factor with regard to real time.
	 * @return Whether the time factor is set.
	 */
	public static ISRequest<Boolean> setTimeFactor(double factor) { return new ISRequest<>(Boolean.class, ISRequestType.SET_TIME_FACTOR, factor); }

	/**
	 * Send an agent command and retrieve the agent's current observation.
	 *
	 * @return agent observation.
	 */
	public static ISRequest<ISObservation> command(ISAgentCommand c) {
		return new ISRequest<>(ISObservation.class, ISRequestType.AGENTCOMMAND, c);
	}
}
