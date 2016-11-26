package fr.pandacube.java.util.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

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
		List<String> ret = new ArrayList<>();

		for (String s : allProposal)
			if (s != null && s.toLowerCase().startsWith(token.toLowerCase())) ret.add(s);

		if (ret.isEmpty()) ret.addAll(allProposal);
		
		ret.removeIf(s -> s == null);
		ret.sort(null); // String implents Comparable

		return ret;
	}
	
	
	
	public static final TabProposal TAB_NO_PROPOSAL = () -> Collections.emptyList();

	public static TabProposal TAB_PROPOSAL(Collection<String> proposals) { 
		return () -> proposals;
	}
	
	public static TabProposal TAB_INTEGERS(int startIncluded, int endIncluded) {
		List<String> proposals = new ArrayList<>(endIncluded - startIncluded + 1);
		for (int i = startIncluded; i <= endIncluded; i++) {
			proposals.add(Integer.toString(i));
		}
		return () -> proposals;
	}
	
	public static TabProposal TAB_PROPOSAL_LAST_PARAMS(String[] args, int index, Collection<String> proposals) { 
		String lastParamToken = getLastParams(args, index);
		String[] splittedToken = lastParamToken.split(" ", -1);	
		int currentTokenPosition = splittedToken.length - 1;
		String[] previousTokens = Arrays.copyOf(splittedToken, currentTokenPosition);
		
		List<String> currentTokenProposal = new ArrayList<>();
		for (String p : proposals) {
			String[] splittedProposal = p.split(" ", -1);
			if (splittedProposal.length <= currentTokenPosition)
				continue;
			if (!Arrays.equals(Arrays.copyOf(splittedToken, currentTokenPosition), previousTokens))
				continue;
			if (splittedProposal[currentTokenPosition].isEmpty())
				continue;
			
			currentTokenProposal.add(splittedProposal[currentTokenPosition]);
		}
		
		return () -> currentTokenProposal;
	}
	
	@FunctionalInterface
	public interface TabProposal {
		public abstract Collection<String> getProposal();
	}
	
	
	
	
	/**
	 * Throw an instance of this exception to indicate to the plugin command handler
	 * that the user has missused the command. The message, if provided, must indicate
	 * the reason of the mussusage of the command. It will be displayed on the screen
	 * with eventually indication of how to use the command (help command for example).
	 * If a {@link Throwable} cause is provided, it will be relayed to the plugin {@link Logger}.
	 * 
	 */
	public static class BadCommandUsage extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public BadCommandUsage() {
			super();
		}
		
		public BadCommandUsage(Throwable cause) {
			super(cause);
		}

		public BadCommandUsage(String message) {
			super(message);
		}
		
		public BadCommandUsage(String message, Throwable cause) {
			super(message, cause);
		}
	}
	
	
	
}
