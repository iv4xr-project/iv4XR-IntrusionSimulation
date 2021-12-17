package intrusionSimulation;

/**
 *
 */
public class ConfigEnvironment {
	/**
	 * Whether to use SE-Star (lighter simulator for training) or MAEV as
	 * the Intrusion Simulation SUT.
	 */
	public transient boolean useSeStar = true;


	/**
	 * Configuration of MAEV's server host and port
	 */
	public transient String hostMAEV = "localhost";
	public transient int portMAEV = 8080;

	/**
	 * Configuration of MAEV's Exsu server host and port
	 */
	public transient String hostEXSU = "localhost";
	public transient int portEXSU = 6080;

	/**
	 * Configuration of SE-Star's server host and port
	 */
	public transient String hostSeStar = "localhost";
	public transient int portSeStar = 6112;

	public String level_name = "";
	public String level_path = "";
	/**
	 * Re-centering X coordinate for MAEV
	 */
	public static final transient double CENTER_X = 137923.45;
	/**
	 * Re-centering Y coordinate for MAEV
	 */
	public static final transient double CENTER_Y = 441352.15;
	/**
	 * Default Z coordinate for SE-Star. In practice, the ground altitude of
	 * the navmesh.
	 */
	public static final transient double SE_STAR_Z = 0.9;

	public ConfigEnvironment(){}

	public ConfigEnvironment(String levelName){
		this.level_name = levelName;
	}

	public ConfigEnvironment(String levelName, String levelFolder){
		this.level_name = levelName;
		this.level_path = levelFolder;
	}
}
