package aws.util;

import glb.agent.core.dc.ServerStatus;

public class AWSStatus {

	public static ServerStatus getServerSatus(int code) {
		int num = code%100;
		
		switch(num) {
		case 0: return ServerStatus.PENDING;
		case 16: return ServerStatus.RUNNING;
		case 32: return ServerStatus.SHUTTING_DOWN;
		case 48: return ServerStatus.TERMINATED;
		case 64: return ServerStatus.STOPPING;
		case 80: return ServerStatus.STOPPED;
		default: return ServerStatus.STOPPED;
		}
	}
}
