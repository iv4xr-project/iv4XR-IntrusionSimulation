package intrusionSimulation;

import helperclasses.datastructures.Vec3;
import world.LabWorldModel;
import world.LegacyObservation;

public class IntrusionSimulationEnvironment extends ISSocketEnvironment {

	public IntrusionSimulationEnvironment(ConfigEnvironment config) {
		super(config.hostMAEV, config.portMAEV);
	}

	private static ConfigEnvironment CONFIG = new ConfigEnvironment();

	public IntrusionSimulationEnvironment() {
		super(CONFIG.hostMAEV, CONFIG.portMAEV);
	}

	public IntrusionSimulationEnvironment(ISAgentCommand command) {
		super(CONFIG.hostMAEV, CONFIG.portMAEV);
		LabWorldModel lwm = sendAgentCommand(command);
	};

	public LabWorldModel sendAgentCommand(ISAgentCommand c) {
		LegacyObservation obs = getISResponse(ISRequest.command(c));
		var wom = LegacyObservation.toWorldModel(obs);
		return wom;
	}

	public LabWorldModel moveTo(String agentId, Vec3 agentPosition, Vec3 target)
	{
		return sendAgentCommand(ISAgentCommand.moveToCommand(agentId, agentPosition));
	}

	public LabWorldModel observe(String agentId) {
		return sendAgentCommand(ISAgentCommand.doNothing(agentId));
	}
}
