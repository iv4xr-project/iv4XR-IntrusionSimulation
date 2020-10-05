package intrusionSimulation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;

import agents.LabRecruitsTestAgent;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;
import world.BeliefState;

public class ServerTest {

	private static IntrusionSimulationEnvironmentTest intrusionSimulationEnvironmentTestServer;

	@BeforeAll
	static void start() {
		intrusionSimulationEnvironmentTestServer = new IntrusionSimulationEnvironmentTest();
	}

	@AfterAll
	static void close() {
		if(intrusionSimulationEnvironmentTestServer != null)
			intrusionSimulationEnvironmentTestServer.close();
	}

	@Test
	public void moveTo() {
		var config = new ConfigEnvironment();

		IntrusionSimulationEnvironment env = new IntrusionSimulationEnvironment(config);

		// create the agent
		var agent = new LabRecruitsTestAgent("agent")
				. attachState(new BeliefState())
				. attachEnvironment(env);

		GoalStructure goal = new GoalStructure();// = goal("goal").toSolve().withTactic(TacticLib.observe()).lift();

		agent.setGoal(goal);

		Assertions.assertTrue(goal.getStatus().inProgress());

		Assertions.assertTrue(env.startSimulation());

		while (goal.getStatus().inProgress())
		{
			agent.update();
		}

		goal.printGoalStructureStatus();

		Assertions.assertFalse(goal.getStatus().inProgress());

		env.close();
	}

	/*public static void main(String[] args) throws UnknownHostException {

		//var config = new ConfigEnvironment();

		//IntrusionSimulationEnvironment env = new IntrusionSimulationEnvironment(config);

		AgentCommand agentCommand = null;

		IntrusionSimulationEnvironment env = new IntrusionSimulationEnvironment(agentCommand);
	}*/
	
}
