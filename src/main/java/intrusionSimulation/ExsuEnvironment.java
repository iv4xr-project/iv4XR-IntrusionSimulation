package intrusionSimulation;

/**
 * Specific MAEV entry point to start, pause or restart the simulation.
 */
public class ExsuEnvironment extends MAEVSocketEnvironment {

	public ExsuEnvironment(ConfigEnvironment config) {
		super(config.hostEXSU, config.portEXSU);
	}

	private static ConfigEnvironment CONFIG = new ConfigEnvironment();

	public ExsuEnvironment() {
		super(CONFIG.hostEXSU, CONFIG.portEXSU);
	}

	/**
	 * Start the environment simulation, return a Boolean.
	 */
	public Boolean startSimulation() {
		return getISResponse(ISRequest.startSimulation());
	}

	/**
	 * Pause the environment simulation, return a Boolean.
	 */
	public Boolean pauseSimulation() {
		return getISResponse(ISRequest.pauseSimulation());
	}

	/**
	 * Restart the environment simulation, return a Boolean.
	 */
	public Boolean restartSimulation() {
		return getISResponse(ISRequest.restartSimulation());
	}

}
