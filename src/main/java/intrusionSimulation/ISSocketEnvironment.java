package intrusionSimulation;

import nl.uu.cs.aplib.mainConcepts.Environment;

/**
 * Abstract class that represents a TCP Socket connector
 * to the Intrusion Simulation environment, regardless of
 * the simulation engine used.
 */
public abstract class ISSocketEnvironment extends Environment {

    /**
     * Send a request through the TCP Socket to the Intrusion Simulation,
     * and receive the associated response.
     *
     * @param request The request to the SUT.
     * @param <T> The type of the expected response object.
     * @return The response from the SUT.
     */
    public abstract <T> T getISResponse(ISRequest<T> request);

    /**
     * Close the TCP connection to the Intrusion Simulation environment.
     *
     * @return Whether the socket is closed.
     */
    public abstract boolean closeSocket();
}
