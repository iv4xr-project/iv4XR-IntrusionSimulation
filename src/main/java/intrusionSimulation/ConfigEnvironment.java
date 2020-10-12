package intrusionSimulation;

public class ConfigEnvironment {

	public transient String hostMAEV = "localhost";
	public transient int portMAEV = 8080;
	
	public transient String hostEXSU = "localhost";
	public transient int portEXSU = 6080;

	public String level_name = "";
	public String level_path = "";

	public ConfigEnvironment(){}

	public ConfigEnvironment(String levelName){
		this.level_name = levelName;
	}

	public ConfigEnvironment(String levelName, String levelFolder){
		this.level_name = levelName;
		this.level_path = levelFolder;
	}
}
