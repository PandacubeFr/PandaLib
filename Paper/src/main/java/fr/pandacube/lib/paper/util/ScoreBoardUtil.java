package fr.pandacube.lib.paper.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import fr.pandacube.lib.core.chat.Chat;
import fr.pandacube.lib.core.chat.ChatUtil;
import net.md_5.bungee.api.ChatColor;

public class ScoreBoardUtil {
	/**
	 * Met à jour la Sidebar d'un Scoreboard donné
	 *
	 * @param scBrd Le Scoreboard à mettre à jour (ne doit pas être null)
	 * @param title Le titre de la Sidebar, limité à 32 caractères
	 * @param lines Les lignes qui doivent être affichés. Si un éléments du
	 *        tableau est null, il sera compté comme une chaine vide. Toutes les
	 *        lignes seront rognés aux 40 premiers caractères
	 */
	@Deprecated
	public static void updateScoreboardSidebar(Scoreboard scBrd, String title, String[] lines) {
		updateScoreboardSidebar(scBrd, Chat.legacyText(title), lines);
	}

	/**
	 * Met à jour la Sidebar d'un Scoreboard donné
	 *
	 * @param scBrd Le Scoreboard à mettre à jour (ne doit pas être null)
	 * @param title Le titre de la Sidebar
	 * @param lines Les lignes qui doivent être affichés. Si un éléments du
	 *        tableau est null, il sera compté comme une chaine vide. Toutes les
	 *        lignes seront rognés aux 40 premiers caractères
	 */
	public static void updateScoreboardSidebar(Scoreboard scBrd, Chat title, String[] lines) {
		if (scBrd == null) throw new IllegalArgumentException("scBrd doit être non null");
		if (lines == null) lines = new String[0];
		
		Objective obj = scBrd.getObjective("sidebar_autogen");
		if (obj != null && !obj.getCriteria().equalsIgnoreCase("dummy")) {
			obj.unregister();
			obj = null;
		}
		
		if (obj == null)
			obj = scBrd.registerNewObjective("sidebar_autogen", "dummy", title.getAdv());
		else {
			if (!title.getAdv().equals(obj.displayName()))
				obj.displayName(title.getAdv());
			if (!DisplaySlot.SIDEBAR.equals(obj.getDisplaySlot()))
				obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
		
		
		filterLines(lines);
		
		List<String> listLines = Arrays.asList(lines);
		
		// remove lines that are not in the array
		Objective fObj = obj;
		scBrd.getEntries().stream()
				.filter(e -> !listLines.contains(e))
				.filter(e -> fObj.getScore(e).isScoreSet())
				.forEach(scBrd::resetScores);

		// add/update others lines
		int boardPos = lines.length;
		for (String line : lines) {
			if (line == null) line = "";

			Score score = obj.getScore(line);
			
			if (score.getScore() != boardPos)
				score.setScore(boardPos);
			
			boardPos--;
		}
	}

	/**
	 * Met à jour la Sidebar d'un Scoreboard donné
	 *
	 * @param scBrd Le Scoreboard à mettre à jour
	 * @param title Le titre de la Sidebar, limité à 32 caractères
	 * @param lines Les lignes qui doivent être affichés. Si un éléments du
	 *        tableau est null, il sera compté comme une chaine vide. Toutes les
	 *        lignes seront rognés aux 40 premiers caractères
	 */
	public static void updateScoreboardSidebar(Scoreboard scBrd, String title, List<String> lines) {
		updateScoreboardSidebar(scBrd, title, lines.toArray(new String[lines.size()]));
	}
	
	
	
	
	@SuppressWarnings("deprecation")
	private static void filterLines(String[] lines) {
		List<String> previous = new ArrayList<>();
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i] == null ? "" : ChatUtil.truncateAtLengthWithoutReset(lines[i], 40);
			if (previous.contains(line)) {
				for (ChatColor c : ChatColor.values()) {
					line = ChatUtil.truncateAtLengthWithoutReset(lines[i], 38) + c;
					if (!previous.contains(line)) {
						break;
					}
				}
			}
			lines[i] = line;
			previous.add(lines[i]);
		}
	}
	

}
