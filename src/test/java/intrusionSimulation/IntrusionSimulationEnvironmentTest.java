package intrusionSimulation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import communication.agent.AgentCommand;
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
	public void disconnectTest()
	{
		var env = new IntrusionSimulationEnvironment();
		boolean res = env.getResponse(ISRequest.disconnect());
		Assertions.assertTrue(res);
	}

	@Test
	public void startSimulationTest()
	{
		var env = new IntrusionSimulationEnvironment();
		boolean res = env.getResponse(ISRequest.startSimulation());
		Assertions.assertTrue(res);
	}

	@Test
	public void pauseSimulationTest()
	{
		var env = new IntrusionSimulationEnvironment();
		boolean res = env.getResponse(ISRequest.pauseSimulation());
		Assertions.assertTrue(res);
	}

	@Test
	public void restartSimulationTest()
	{
		var env = new IntrusionSimulationEnvironment();
		boolean res = env.getResponse(ISRequest.restartSimulation());
		Assertions.assertTrue(res);
	}

	@Test
	public void observeTest()
	{
		var env = new IntrusionSimulationEnvironment();
		var obs = env.getResponse(ISRequest.command(AgentCommand.doNothing("agent")));
		Assertions.assertNotNull(obs);

		System.out.println("AgentId: " + obs.agentID);
		System.out.println("AgentPos: " + obs.agentPosition);

		boolean res = env.getResponse(ISRequest.disconnect());
		Assertions.assertTrue(res);
	}

	@Test
	public void moveTowardTest()
	{
		var env = new IntrusionSimulationEnvironment();
		var obs = env.getResponse(ISRequest.command(AgentCommand.moveTowardCommand("agent", new Vec3(1.0, 0, 0), false)));
		Assertions.assertNotNull(obs);

		System.out.println("AgentId: " + obs.agentID);
		System.out.println("AgentPos: " + obs.agentPosition);

		boolean res = env.getResponse(ISRequest.disconnect());
		Assertions.assertTrue(res);
	}

	@Test
	public void interactWithTest()
	{
		var env = new IntrusionSimulationEnvironment();
		var obs = env.getResponse(ISRequest.command(AgentCommand.interactCommand("agent", "target")));
		Assertions.assertNotNull(obs);

		System.out.println("AgentId: " + obs.agentID);
		System.out.println("AgentPos: " + obs.agentPosition);

		boolean res = env.getResponse(ISRequest.disconnect());
		Assertions.assertTrue(res);
	}
}