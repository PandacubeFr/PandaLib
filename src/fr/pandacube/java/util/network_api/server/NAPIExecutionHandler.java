package fr.pandacube.java.util.network_api.server;

/**
 * Interface permettant de gérer l'exécution asynchrone d'un PacketExecutor.
 * @author Marc Baloup
 *
 */
@FunctionalInterface
public interface NAPIExecutionHandler {
	
	public void handleRun(Runnable executor);

}
