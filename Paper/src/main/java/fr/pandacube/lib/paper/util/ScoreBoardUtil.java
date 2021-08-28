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
	 * Update the sidebar of the provided scoreboard, with the given title and lines.
	 *
	 * @param scBrd the scoreboard
	 * @param title the title of the sidebar
	 * @param lines the lines that have to be displayed. Null values are treated as empty lines.
	 *              The lines support legacy formatting only, and will be truncated to 40 characters.
	 *              Lines present multiple times will have hidden characters appended to make them different.
	 *              Vanilla Java Edition clients only display the 15 first lines.
	 * @implNote The implementation makes sure that the minimum amount of data is transmitted to the client,
	 *           to reduce bandwith usage and avoid the sidebar flickering.
	 *           <ul>
	 *           <li>If a provided line is already present in the sidebar, and at the same line number, it will not be updated.
	 *           <li>If a provided line is already present but at another position, only the score (i.e. the line number) is updated.
	 *           <li>If a provided line was not present before, it is added as a new score entry in the scoreboard.
	 *           <li>If a line that was already present is not in the provided lines, it is removed from the scoreboard.
	 *           <li>The title is only updated if it has actually changed.
	 */
	public static void updateScoreboardSidebar(Scoreboard scBrd, Chat title, String[] lines) {
		if (scBrd == null) throw new IllegalArgumentException("scBrd doit être non null");
		if (lines == null) lines = new String[0];
		
		Objective obj = scBrd.getObjective("sidebar_autogen");
		if (obj != null && !obj.getCriteria().equalsIgnoreCase("dummy")) {
			// objective present but with wrong criteria, removing it
			obj.unregister();
			obj = null;
		}
		
		
		if (obj == null) {
			obj = scBrd.registerNewObjective("sidebar_autogen", "dummy", title.getAdv());
			obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
		else {
			// only update title if needed
			if (!title.getAdv().equals(obj.displayName()))
				obj.displayName(title.getAdv());
			// fix display slot if someone else changed it
			if (DisplaySlot.SIDEBAR != obj.getDisplaySlot())
				obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
		
		
		filterLines(lines);
		
		List<String> listLines = Arrays.asList(lines);
		
		// remove lines from the scoreboard that are not in the provided array
		Objective fObj = obj;
		scBrd.getEntries().stream()
				.filter(e -> !listLines.contains(e))
				.filter(e -> fObj.getScore(e).isScoreSet())
				.forEach(scBrd::resetScores);

		// add and update others lines
		int boardPos = lines.length;
		for (String line : lines) {
			Score score = obj.getScore(line);
			
			if (score.getScore() != boardPos)
				score.setScore(boardPos);
			
			boardPos--;
		}
	}

	/**
	 * Update the sidebar of the provided scoreboard, with the given title and lines.
	 *
	 * @param scBrd the scoreboard
	 * @param title the title of the sidebar
	 * @param lines the lines that have to be displayed. Null values are treated as empty lines.
	 *              The lines support legacy formatting only, and will be truncated to 40 characters.
	 *              Lines present multiple times will have hidden characters appended to make them different.
	 *              Vanilla Java Edition clients only display the 15 first lines.
	 * @implNote The implementation makes sure that the minimum amount of data is transmitted to the client,
	 *           to reduce bandwith usage and avoid the sidebar flickering.
	 *           <ul>
	 *           <li>If a provided line is already present in the sidebar, and at the same line number, it will not be updated.
	 *           <li>If a provided line is already present but at another position, only the score (i.e. the line number) is updated.
	 *           <li>If a provided line was not present before, it is added as a new score entry in the scoreboard.
	 *           <li>If a line that was already present is not in the provided lines, it is removed from the scoreboard.
	 *           <li>The title is only updated if it has actually changed.
	 */
	public static void updateScoreboardSidebar(Scoreboard scBrd, Chat title, List<String> lines) {
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
