package intrusionSimulation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ExsuEnvironmentTest {

	public Process server;

	public void ExsuTestServer() {}

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
	public void startSimulationTest()
	{
		var env = new ExsuEnvironment();
		boolean res = env.getISResponse(ISRequest.startSimulation());
		Assertions.assertTrue(res);
		
		if(!env.closeSocket())
			System.out.println("Server refuses to close the socket exchange");
	}

	@Test
	public void pauseSimulationTest()
	{
		var env = new ExsuEnvironment();
		boolean res = env.getISResponse(ISRequest.pauseSimulation());
		Assertions.assertTrue(res);
		
		if(!env.closeSocket())
			System.out.println("Server refuses to close the socket exchange");
	}

	@Test
	public void restartSimulationTest()
	{
		var env = new ExsuEnvironment();
		boolean res = env.getISResponse(ISRequest.restartSimulation());
		Assertions.assertTrue(res);
		
		if(!env.closeSocket())
			System.out.println("Server refuses to close the socket exchange");
	}
	
	@Test
	public void disconnectTest()
	{
		var env = new ExsuEnvironment();
		boolean res = env.getISResponse(ISRequest.disconnect());
		Assertions.assertTrue(res);
		
		if(!env.closeSocket())
			System.out.println("Server refuses to close the socket exchange");
	}
}