package intrusionSimulation;

import eu.iv4xr.framework.spatial.Vec3;

/**
 * Generic representation of commands sent to the Intrusion Simulation entities.
 */
public class ISAgentCommand {

	/**
	 * Type of the command.
	 */
	public ISAgentCommandType cmd;
	/**
	 * Identifier of the controlled agent.
	 */
	public int agentId;
	/**
	 * Identifier of the command's target. Currently, irrelevant for the
	 * implemented commands (MOVETO and DONOTHING)
	 */
	public int targetId;
	/**
	 * Arguments of the command.
	 */
	public Object arg;

	private ISAgentCommand(int agentId, int targetId, ISAgentCommandType cmd, Object arg) {
		this.agentId = agentId;
		this.targetId = targetId;
		this.cmd = cmd;
		this.arg = arg;
	}

	/**
	 * Arguments of the DONOTHING command.
	 */
	public static class DoNothingArgument {
		/**
		 * Waiting time in seconds.
		 */
		public double t; // waiting time
		/**
		 * Priority, MAEV-specific. A command with higher priority overrides
		 * the current one.
		 */
		public double p; // priority

		/**
		 * Constructor. Sets the waiting time to 1 second and the priority to 1.
		 */
		public DoNothingArgument() {
			this.t = 1.0;
			this.p = 1.0;
		}
	}

	/**
	 * Arguments of the MOVETO command.
	 */
	public static class MoveToArgument {
		/**
		 * x, y, z coordinates of the destination.
		 */
		public double x;
		public double y;
		public double z;
		/**
		 * Priority, MAEV-specific. A command with higher priority overrides
		 * the current one.
		 */
		public double p; // priority

		/**
		 * Constructor. Sets the priority to 2, in order to override the
		 * DONOTHING command.
		 *
		 * @param position position of the destination
		 */
		public MoveToArgument(Vec3 position) {
			this.x = position.x;
			this.y = position.y;
			this.z = position.z;
			this.p = 2.0;
		}
	}


	/**
	 * The agent does not do anything. This can be used to just observe.
	 */
	public static ISAgentCommand doNothing(int agent) {
		return new ISAgentCommand(agent, 1, ISAgentCommandType.DONOTHING, new DoNothingArgument());
	}

	/**
	 * Moves an agent in a defined location.
	 */
	public static ISAgentCommand moveToCommand(int agent, Vec3 position) {
		return new ISAgentCommand(agent, 1, ISAgentCommandType.MOVETO, new MoveToArgument(position));
	}
}
