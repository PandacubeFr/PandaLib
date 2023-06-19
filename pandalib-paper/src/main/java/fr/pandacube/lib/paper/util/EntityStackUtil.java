package fr.pandacube.lib.paper.util;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

/**
 * Utility class to handle stacks of entities. A stack an entity is when an entity is mounting onto another one.
 * For instance, a player mounting a horse. We also say that the horse is the vehicle of the player.
 */
public class EntityStackUtil {

	/**
	 * Teleport a stack of entity, all at once.
	 *
	 * @param e An entity that is part of the stack to teleport.
	 * @param l The location where to send the entity stack.
	 * @deprecated This method has not been tested since a long time ago.
	 */
	@Deprecated
	public static void teleportStack(Entity e, Location l) {

		// on se place sur l'entité tout en bas de la pile
		Entity entTemp = e;
		while (entTemp.getVehicle() != null)
			entTemp = entTemp.getVehicle();
		
		/* La possibilité d'avoir plusieurs passagers sur une entité rend le code
		 * commenté qui suit invalide. On le remplace temporairement (voire
		 * définitivement si ça suffit) par le code encore en dessous
		List<Entity> stack = new ArrayList<>();
		do {
			stack.add(entTemp);
			entTemp = entTemp.getPassenger();
		} while (entTemp != null);

		if (stack.size() == 1) {
			stack.get(0).teleport(l);
			return;
		}

		stack.get(0).eject();
		stack.get(0).teleport(l);
		stack.get(0).setPassenger(stack.get(1));
		*/
		
		entTemp.teleport(l); // entTemp est l'entité le plus en bas de la "pile" d'entité
	}

}
