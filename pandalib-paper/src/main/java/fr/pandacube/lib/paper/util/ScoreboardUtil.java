package fr.pandacube.lib.paper.util;

import fr.pandacube.lib.chat.Chat;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;

/**
 * Utility class to manipulate scoreboards.
 */
public class ScoreboardUtil {
	


	/**
	 * Update the sidebar of the provided scoreboard, with the given title and lines.
	 * 
	 * @param scBrd the scoreboard
	 * @param title the title of the sidebar
	 * @param lines the lines that have to be displayed. Null values are treated as empty lines.
	 *              The lines support legacy formatting only, and will be truncated to 40 characters.
	 *              Lines present multiple times will have hidden characters appended to make them different.
	 *              Vanilla Java Edition clients only display the 15 first lines.
	 */
	public static void updateScoreboardSidebar(Scoreboard scBrd, Component title, Component[] lines) {
		
		Objective obj = scBrd.getObjective("sidebar_autogen");
		if (obj != null && !obj.getTrackedCriteria().equals(Criteria.DUMMY)) {
			// objective present but with wrong criteria, removing it
			obj.unregister();
			obj = null;
		}
		
		
		if (obj == null) {
			obj = scBrd.registerNewObjective("sidebar_autogen", Criteria.DUMMY, title);
			obj.setDisplaySlot(DisplaySlot.SIDEBAR);
			obj.numberFormat(NumberFormat.blank());
		}
		else {
			// only update title if needed
			if (!title.equals(obj.displayName())) {
				obj.displayName(title);
			}
			// fix display slot if someone else changed it
			if (obj.getDisplaySlot() != DisplaySlot.SIDEBAR) {
				obj.setDisplaySlot(DisplaySlot.SIDEBAR);
			}
		}

		@SuppressWarnings("deprecation")
		ChatColor[] colors = ChatColor.values();
		
		/*
		 * Scanning lines from last to first, keeping only the 15 first lines
		 */
		int score = 1, i = 0;
		for (int lineIndex = Math.min(lines.length, 15) - 1; lineIndex >= 0; i++, score++, lineIndex--) {
			String teamId = "sidebar_team" + score;
			String sbEntry = colors[i].toString();
			Team tLine = scBrd.getTeam(teamId);
			if (tLine == null) {
				tLine = scBrd.registerNewTeam(teamId);
			}
			if (!tLine.hasEntry(sbEntry)) {
				tLine.addEntry(sbEntry);
			}

			if (!tLine.prefix().equals(lines[lineIndex])) {
				tLine.prefix(lines[lineIndex]);
			}

			Score scoreEntry = obj.getScore(sbEntry);
			// scoreEntry.customName(lines[lineIndex]); // not for now x)
			if (scoreEntry.getScore() != score) {
				scoreEntry.setScore(score);
			}
		}
		
		// clean older data when we are reducing the number of line displayed
		for (; i < colors.length; i++, score++) {
			String teamId = "sidebar_team" + score;
			String sbEntry = colors[i].toString();

			if (obj.getScore(sbEntry).isScoreSet()) {
				scBrd.resetScores(sbEntry);
			}

			Team tLine = scBrd.getTeam(teamId);
			if (tLine != null && !tLine.prefix().equals(Component.empty())) {
				tLine.prefix(Component.empty());
			}
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
	 */
	public static void updateScoreboardSidebar(Scoreboard scBrd, Chat title, Chat[] lines) {
		Component[] cmpLines = new Component[lines.length];
		for (int i = 0; i < lines.length; i++) {
			cmpLines[i] = lines[i].getAdv();
		}
		updateScoreboardSidebar(scBrd, title.getAdv(), cmpLines);
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
	 */
	public static void updateScoreboardSidebar(Scoreboard scBrd, Chat title, List<Chat> lines) {
		Component[] cmpLines = new Component[lines.size()];
		for (int i = 0; i < cmpLines.length; i++) {
			cmpLines[i] = lines.get(i).getAdv();
		}
		updateScoreboardSidebar(scBrd, title.getAdv(), cmpLines);
	}
	
	

}
