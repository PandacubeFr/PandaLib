package fr.pandacube.java.util.network_api.server;

public class ThreadNAPIExecutionHandler implements NAPIExecutionHandler {

	@Override
	public void handleRun(Runnable executor) {
		new Thread(executor).start();
	}

}
