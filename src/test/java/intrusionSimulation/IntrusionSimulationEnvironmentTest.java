package intrusionSimulation;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import helperclasses.datastructures.Vec3;

public class IntrusionSimulationEnvironmentTest {

	public Process server;

	public void IntrusionSimulationTestServer() {}

	public void close() {
		if (server != null)
		{
			try {
				server.destroy();
				server.waitFor();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void observeTest()
	{
		var env = new IntrusionSimulationEnvironment();
		var obs = env.getISResponse(ISRequest.command(ISAgentCommand.doNothing("agent")));
		Assertions.assertNotNull(obs);

		System.out.println("AgentId: " + obs.agentID);
		System.out.println("AgentPos: " + obs.agentPosition);
		
		if (!env.closeSocket())
			System.out.println("Server refuses to close the socket exchange");
	}

	@Test
	public void moveTowardTest()
	{
		var env = new IntrusionSimulationEnvironment();
		var obs = env.getISResponse(ISRequest.command(ISAgentCommand.moveToCommand("agent", new Vec3(1.0, 0, 0))));
		Assertions.assertNotNull(obs);

		System.out.println("AgentId: " + obs.agentID);
		System.out.println("AgentPos: " + obs.agentPosition);
		
		try {
			TimeUnit.SECONDS.sleep(5);
		}
		catch(InterruptedException e) {	}
		
		var obs2 = env.getISResponse(ISRequest.command(ISAgentCommand.moveToCommand("agent2", new Vec3(1.0, 1.0, 0))));
		System.out.println("AgentId: " + obs.agentID);
		System.out.println("AgentPos: " + obs.agentPosition);
		
		if (!env.closeSocket())
			System.out.println("Server refuses to close the socket exchange");
	}
}