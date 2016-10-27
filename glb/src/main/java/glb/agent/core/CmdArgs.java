package glb.agent.core;

import org.kohsuke.args4j.Option;

class CmdArgs {

	@Option(required=true, name = "-y", aliases="--yaml", usage="yaml configuration file")
	private String yamlFile;
	
	String getYAMLFile() {
		return yamlFile;
	}
}
