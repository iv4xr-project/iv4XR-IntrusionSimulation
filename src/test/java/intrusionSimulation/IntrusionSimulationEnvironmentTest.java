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
		try {
			TimeUnit.SECONDS.sleep(2);
		}
		catch(InterruptedException e) {	}

		var envExsu = new ExsuEnvironment();
		boolean res = envExsu.getISResponse(ISRequest.startSimulation());
		Assertions.assertTrue(res);

		var env = new IntrusionSimulationEnvironment();
		var obs = env.getISResponse(ISRequest.command(ISAgentCommand.doNothing(1)));
		Assertions.assertNotNull(obs);

		System.out.println("AgentId: " + obs.agentID);
		System.out.println("AgentPos: " + obs.agentPosition);

		if (!env.closeSocket())
			System.out.println("Server refuses to close the socket exchange");

		if(!envExsu.close())
			System.out.println("Server refuses to close the socket exchange");
	}

	@Test
	public void moveToTest()
	{
		try {
			TimeUnit.SECONDS.sleep(2);
		}
		catch(InterruptedException e) {	}

		var envExsu = new ExsuEnvironment();
		boolean res = envExsu.getISResponse(ISRequest.startSimulation());
		Assertions.assertTrue(res);

		var env = new IntrusionSimulationEnvironment();
		var obs = env.getISResponse(ISRequest.command(ISAgentCommand.moveToCommand(1, new Vec3(2.0, 8.0, 0.0))));
		Assertions.assertNotNull(obs);

		System.out.println("AgentId: " + obs.agentID);
		System.out.println("AgentPos: " + obs.agentPosition);
		
		var obs2 = env.getISResponse(ISRequest.command(ISAgentCommand.moveToCommand(1, new Vec3(2.0, 3.0, 0.0))));
		Assertions.assertNotNull(obs2);

		System.out.println("AgentId: " + obs2.agentID);
		System.out.println("AgentPos: " + obs2.agentPosition);

		if (!env.closeSocket())
			System.out.println("Server refuses to close the socket exchange");

		if(!envExsu.close())
			System.out.println("Server refuses to close the socket exchange");
	}

	@Test
	public void moveToAndObserveTest()
	{
		try {
			TimeUnit.SECONDS.sleep(2);
		}
		catch(InterruptedException e) {	}

		var envExsu = new ExsuEnvironment();
		boolean res = envExsu.getISResponse(ISRequest.startSimulation());
		Assertions.assertTrue(res);

		var env = new IntrusionSimulationEnvironment();
		var obs = env.getISResponse(ISRequest.command(ISAgentCommand.moveToCommand(1, new Vec3(8.0, 5.0, 0.0))));
		Assertions.assertNotNull(obs);

		System.out.println("AgentId: " + obs.agentID);
		System.out.println("AgentPos: " + obs.agentPosition);

		var obs2 = env.getISResponse(ISRequest.command(ISAgentCommand.moveToCommand(1, new Vec3(10.0, 9.0, 0.0))));
		Assertions.assertNotNull(obs2);

		System.out.println("AgentId: " + obs2.agentID);
		System.out.println("AgentPos: " + obs2.agentPosition);

		var obs3 = env.getISResponse(ISRequest.command(ISAgentCommand.doNothing(1)));
		Assertions.assertNotNull(obs3);

		System.out.println("AgentId: " + obs3.agentID);
		System.out.println("AgentPos: " + obs3.agentPosition);
		
		if (!env.closeSocket())
			System.out.println("Server refuses to close the socket exchange");

		if(!envExsu.close())
			System.out.println("Server refuses to close the socket exchange");
	}

}