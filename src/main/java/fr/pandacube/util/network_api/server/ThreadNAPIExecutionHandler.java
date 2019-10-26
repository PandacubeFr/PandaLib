package fr.pandacube.util.network_api.server;

public class ThreadNAPIExecutionHandler implements NAPIExecutionHandler {

	@Override
	public void handleRun(Runnable executor) {
		Thread t = new Thread(executor);
		t.setDaemon(true);
		t.start();
	}

}
