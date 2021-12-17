package intrusionSimulation;

/**
 * High-level wrapper of an Intrusion Simulation System Under Test,
 * regardless of the simulation engine used.
 */
public class IntrusionSimulationEnvironment {
	// Configuration parameters
	private final static ConfigEnvironment CONFIG = new ConfigEnvironment();
	// TCP Socket Connector
	private final ISSocketEnvironment environment;
	public boolean useSeStar;

	/**
	 * Constructor. Initialize the environment from the given configuration, and
	 * establish the TCP Socket connection.
	 *
	 * @param config Configuration of the Intrusion Simulation SUT.
	 */
	public IntrusionSimulationEnvironment(ConfigEnvironment config) {
		useSeStar = config.useSeStar;
		if (config.useSeStar) {
			environment = new SeStarSocketEnvironment(config.hostSeStar, config.portSeStar);
		} else {
			environment = new MAEVSocketEnvironment(config.hostMAEV, config.portMAEV);
		}
	}

	/**
	 * Constructor with the default configuration.
	 */
	public IntrusionSimulationEnvironment() {
		this(CONFIG);
	}

	/**
	 * Send a request through the TCP Socket to the Intrusion Simulation,
	 * and receive the associated response.
	 *
	 * @param request The request to the SUT.
	 * @param <T> The type of the expected response object.
	 * @return The response from the SUT.
	 */
	public <T> T sendRequest(ISRequest<T> request) {
		return (T) environment.getISResponse(request);
	}

	/**
	 * Close the TCP connection to the Intrusion Simulation environment.
	 *
	 * @return Whether the socket is closed.
	 */
	public boolean closeSocket() {
		return environment.closeSocket();
	}
}
