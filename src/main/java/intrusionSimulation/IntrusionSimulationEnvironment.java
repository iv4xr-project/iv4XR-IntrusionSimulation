package intrusionSimulation;

import communication.agent.AgentCommand;
import helperclasses.datastructures.Vec3;
import world.LabWorldModel;
import world.LegacyObservation;

public class IntrusionSimulationEnvironment extends ISSocketEnvironment {

	public IntrusionSimulationEnvironment(ConfigEnvironment config) {
		super(config.host, config.port);
		/*
		 * if (!startSimulation()) System.out.println("Error starting simulation");
		 */
	}

	private static ConfigEnvironment CONFIG = new ConfigEnvironment();

	public IntrusionSimulationEnvironment() {
		super(CONFIG.host, CONFIG.port);
	}

	public IntrusionSimulationEnvironment(AgentCommand command) {
		super(CONFIG.host, CONFIG.port);
		LabWorldModel lwm = sendAgentCommand(command);
	};

	public LabWorldModel sendAgentCommand(AgentCommand c) {
		LegacyObservation obs = getResponse(ISRequest.command(c));
		var wom = LegacyObservation.toWorldModel(obs);
		return wom;
	}

	/**
	 * The agent will move to a distance toward the target
	 * 
	 * @params target
	 * @params agentId: the id of the game-entity controlled by the agent
	 * @params agentPosition: current agent position
	 * @return the observation following the action
	 */
	public LabWorldModel moveToward(Vec3 target, Vec3 agentPosition, String agentId) {
		// define the max distance the agent wants to move ahead between updates
		float maxDist = 2f;

		// Calculate where the agent wants to move to
		Vec3 targetDirection = Vec3.subtract(target, agentPosition);
		targetDirection.normalize();

		// Check if we can move the full distance ahead
		double dist = target.distance(agentPosition);
		if (dist < maxDist) {
			targetDirection.multiply(dist);
		} else {
			targetDirection.multiply(maxDist);
		}
		// add the agent own position to the current coordinates
		targetDirection.add(agentPosition);

		// send the command
		return sendAgentCommand(AgentCommand.moveTowardCommand(agentId, targetDirection, false));
	}

	/**
	 * Send a doNothing command to the environment simulation, return an
	 * Observation.
	 * 
	 * @param agentId
	 * @return
	 */
	public LabWorldModel observe(String agentId) {
		return sendAgentCommand(AgentCommand.doNothing(agentId));
	}

	/**
	 * Send an interact command to the environment simulation, return an
	 * Observation.
	 * 
	 * @param agentId
	 * @param target
	 * @return
	 */
	public LabWorldModel interactWith(String agentId, String target) {
		return sendAgentCommand(AgentCommand.interactCommand(agentId, target));
	}

	/**
	 * Start the environment simulation, return a Boolean.
	 */
	public Boolean startSimulation() {
		return getResponse(ISRequest.startSimulation());
	}

	/**
	 * Pause the environment simulation, return a Boolean.
	 */
	public Boolean pauseSimulation() {
		return getResponse(ISRequest.pauseSimulation());
	}

	/**
	 * Restart the environment simulation, return a Boolean.
	 */
	public Boolean restartSimulation() {
		return getResponse(ISRequest.restartSimulation());
	}

}
