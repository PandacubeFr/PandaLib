package fr.pandacube.lib.core.commands;

import java.util.Arrays;

import fr.pandacube.lib.core.chat.ChatStatic;

public class AbstractCommand extends ChatStatic {
	
	public final String commandName;
	
	public AbstractCommand(String cmdName) {
		commandName = cmdName;
	}
	

	/**
	 * <p>
	 * Concatène les chaines de caractères passés dans <code>args</code> (avec
	 * <code>" "</code> comme séparateur), en ommettant
	 * celles qui se trouvent avant <code>index</code>.<br/>
	 * Par exemple :
	 * </p>
	 * <code>
	 * getLastParams(new String[] {"test", "bouya", "chaka", "bukkit"}, 1);
	 * </code>
	 * <p>
	 * retournera la chaine "bouya chaka bukkit"
	 * 
	 * @param args liste des arguments d'une commandes.<br/>
	 *        Le premier élément est l'argument qui suit le nom de la commande.
	 *        Usuellement, ce paramètre correspond au paramètre
	 *        <code>args</code> de la méthode onCommand
	 * @param index
	 * @return
	 */
	public static String getLastParams(String[] args, int index) {
		if (index < 0 || index >= args.length) return null;
		return String.join(" ", Arrays.copyOfRange(args, index, args.length));
	}
	
	
	
}
