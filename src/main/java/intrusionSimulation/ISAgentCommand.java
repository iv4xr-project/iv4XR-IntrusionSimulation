package intrusionSimulation;

import helperclasses.datastructures.Vec3;

public class ISAgentCommand {

	public ISAgentCommandType cmd;
	public int agentId;
	public int targetId;
	public Object arg;

	private ISAgentCommand(int agentId, int targetId, ISAgentCommandType cmd, Object arg) {

		this.agentId = agentId;
		this.targetId = targetId;
		this.cmd = cmd;
		this.arg = arg;
	}

	/**
	 * The agent does not do anything. This can be used to just observe.
	 */
	public static ISAgentCommand doNothing(int agent) {
		return new ISAgentCommand(agent, agent, ISAgentCommandType.DONOTHING, null);
	}

	/**
	 * Moves an agent in a defined location.
	 */
	public static ISAgentCommand moveToCommand(int agent, Vec3 position) {
		return new ISAgentCommand(agent, agent, ISAgentCommandType.MOVETO, position);
	}
}
