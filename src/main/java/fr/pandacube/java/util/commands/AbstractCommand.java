package fr.pandacube.java.util.commands;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AbstractCommand {
	
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

	/**
	 * <i>Prends en charge les tokens avec des espaces, mais retourne les
	 * propositions complètes</i>
	 * 
	 * @param token
	 * @param allProposal
	 * @return
	 */
	public static List<String> getTabProposalFromToken(String token, Collection<String> allProposal) {
		return allProposal.stream()
				.filter(s -> s != null && s.toLowerCase().startsWith(token.toLowerCase()))
				.sorted()
				.collect(Collectors.toList());
	}
	
	
	
}
