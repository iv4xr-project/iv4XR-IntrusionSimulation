package intrusionSimulation;

import helperclasses.datastructures.Vec3;

public class ISAgentCommand {

    public ISAgentCommandType cmd;
    public String agentId;
    public String targetId;
    public Object arg;
    
    private ISAgentCommand(String agentId, String targetId, ISAgentCommandType cmd, Object arg) {

        this.agentId = agentId;
        this.targetId = targetId;
        this.cmd = cmd;
        this.arg = arg;
    }
    
    /**
     * The agent does not do anything. This can be used to just observe.
     */
    public static ISAgentCommand doNothing(String agent) {
        return new ISAgentCommand(agent, agent, ISAgentCommandType.DONOTHING, null);
    }
    
    /**
     * Moves an agent in a defined location.
     */
    public static ISAgentCommand moveToCommand(String agent, Vec3 position) {
        return new ISAgentCommand(agent, agent, ISAgentCommandType.MOVETO, position);
    }
}
