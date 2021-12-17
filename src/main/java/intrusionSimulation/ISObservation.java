package intrusionSimulation;

import eu.iv4xr.framework.mainConcepts.WorldModel;

/**
 * WorldModel for the Intrusion Simulation SUT.
 * Direct extension of the iv4xr base WorldModel.
 *
 * The point of view of the intruder is always used.
 */
public class ISObservation extends WorldModel {
	/**
	 * Whether the intruder is detected by one of the cameras or one of the
	 * guards.
	 */
	public boolean isDetected = false;
}
