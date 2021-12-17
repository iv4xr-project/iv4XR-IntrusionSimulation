package intrusionSimulation;

import eu.iv4xr.framework.spatial.Vec3;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

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
		var obs = env.sendRequest(ISRequest.command(ISAgentCommand.doNothing(1)));
		Assertions.assertNotNull(obs);

		System.out.println("AgentId: " + obs.agentId);
		System.out.println("AgentPos: " + obs.position);

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
		var obs = env.sendRequest(ISRequest.command(ISAgentCommand.moveToCommand(1, new Vec3(2.0f, 8.0f, 0.0f))));
		Assertions.assertNotNull(obs);

		System.out.println("AgentId: " + obs.agentId);
		System.out.println("AgentPos: " + obs.position);
		
		var obs2 = env.sendRequest(ISRequest.command(ISAgentCommand.moveToCommand(1, new Vec3(2.0f, 3.0f, 0.0f))));
		Assertions.assertNotNull(obs2);

		System.out.println("AgentId: " + obs2.agentId);
		System.out.println("AgentPos: " + obs2.position);

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
		var obs = env.sendRequest(ISRequest.command(ISAgentCommand.moveToCommand(1, new Vec3(8.0f, 5.0f, 0.0f))));
		Assertions.assertNotNull(obs);

		System.out.println("AgentId: " + obs.agentId);
		System.out.println("AgentPos: " + obs.position);

		var obs2 = env.sendRequest(ISRequest.command(ISAgentCommand.moveToCommand(1, new Vec3(10.0f, 9.0f, 0.0f))));
		Assertions.assertNotNull(obs2);

		System.out.println("AgentId: " + obs2.agentId);
		System.out.println("AgentPos: " + obs2.position);

		var obs3 = env.sendRequest(ISRequest.command(ISAgentCommand.doNothing(1)));
		Assertions.assertNotNull(obs3);

		System.out.println("AgentId: " + obs3.agentId);
		System.out.println("AgentPos: " + obs3.position);
		
		if (!env.closeSocket())
			System.out.println("Server refuses to close the socket exchange");

		if(!envExsu.close())
			System.out.println("Server refuses to close the socket exchange");
	}

}