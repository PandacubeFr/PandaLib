package fr.pandacube.lib.paper.util;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

/**
 * Permet de gérer les entités qui se transportent les uns les autres
 *
 * Ce groupement d'entité est appelé une "pile d'entité".
 *
 * Par exemple, un cheval et son monteur représente à eux deux une pile
 * d'entité, dont
 * l'élement tout en bas est le cheval
 */
public class EntityStackUtil {

	/**
	 * Déplace une pile d'entité vers l'endroit défini
	 *
	 * @param e Une entité faisant parti de la pile d'entité à téléporter
	 * @param l La position vers lequel envoyer toute la pile
	 */
	public static void teleportStack(Entity e, Location l) {

		// on se place sur l'entité tout en bas de la pile
		Entity entTemp = e;
		while (entTemp.getVehicle() != null)
			entTemp = entTemp.getVehicle();
		
		/* la possibilité d'avoir plusieurs passagers sur un entité rend le code
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
