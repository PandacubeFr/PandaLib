package fr.pandacube.lib.paper.modules;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import fr.pandacube.lib.chat.Chat;
import fr.pandacube.lib.chat.ChatColorGradient;
import fr.pandacube.lib.chat.ChatConfig.PandaTheme;
import fr.pandacube.lib.paper.PandaLibPaper;
import fr.pandacube.lib.paper.players.PaperOffPlayer;
import fr.pandacube.lib.paper.players.PaperOnlinePlayer;
import fr.pandacube.lib.paper.scheduler.SchedulerUtil;
import fr.pandacube.lib.paper.util.AutoUpdatedBossBar;
import fr.pandacube.lib.paper.util.AutoUpdatedBossBar.BarUpdater;
import fr.pandacube.lib.players.standalone.AbstractPlayerManager;
import fr.pandacube.lib.util.MemoryUtil;
import fr.pandacube.lib.util.MemoryUtil.MemoryUnit;
import fr.pandacube.lib.util.TimeUtil;
import fr.pandacube.lib.util.log.Log;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.bossbar.BossBar.Overlay;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static fr.pandacube.lib.chat.ChatStatic.chat;
import static fr.pandacube.lib.chat.ChatStatic.failureText;
import static fr.pandacube.lib.chat.ChatStatic.infoText;
import static fr.pandacube.lib.chat.ChatStatic.successText;
import static fr.pandacube.lib.chat.ChatStatic.text;

public class PerformanceAnalysisManager implements Listener {

	private static PerformanceAnalysisManager instance;

	public static synchronized PerformanceAnalysisManager getInstance() {
		if (instance == null)
			instance = new PerformanceAnalysisManager();
		return instance;
	}


	private static final int NB_TICK_HISTORY = 20 * 60 * 60; // 60 secondes ;
	
	private final Plugin plugin = PandaLibPaper.getPlugin();
	private long firstRecord = 0;
	
	private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

	private long tickStartNanoTime = System.nanoTime();
	private long tickStartCPUTime = 0;
	private long tickEndNanoTime = System.nanoTime();
	private long lastInterTPSDuration = 0;

	
	
	
	private final LinkedList<Long> tpsTimes = new LinkedList<>();
	private final LinkedList<Long> tpsDurations = new LinkedList<>();
	private final LinkedList<Long> tpsCPUTimes = new LinkedList<>();
	private final LinkedList<Long> interTPSDurations = new LinkedList<>();




	public final AutoUpdatedBossBar tpsBar;
	public final AutoUpdatedBossBar memoryBar;
	private final List<Player> barPlayers = new ArrayList<>();
	private final List<BossBar> relatedBossBars = new ArrayList<>();
	
	
	public final ChatColorGradient tps1sGradient = new ChatColorGradient()
			.add(0, NamedTextColor.BLACK)
			.add(1, NamedTextColor.DARK_RED)
			.add(5, NamedTextColor.RED)
			.add(10, NamedTextColor.GOLD)
			.add(14, NamedTextColor.YELLOW)
			.add(19, PandaTheme.CHAT_GREEN_1_NORMAL)
			.add(21, PandaTheme.CHAT_GREEN_1_NORMAL)
			.add(26, NamedTextColor.BLUE);


	public final ChatColorGradient tps10sGradient = new ChatColorGradient()
			.add(0, NamedTextColor.DARK_RED)
			.add(5, NamedTextColor.RED)
			.add(10, NamedTextColor.GOLD)
			.add(14, NamedTextColor.YELLOW)
			.add(19, PandaTheme.CHAT_GREEN_1_NORMAL);


	public final ChatColorGradient tps1mGradient = new ChatColorGradient()
			.add(0, NamedTextColor.DARK_RED)
			.add(8, NamedTextColor.RED)
			.add(14, NamedTextColor.GOLD)
			.add(17, NamedTextColor.YELLOW)
			.add(19, PandaTheme.CHAT_GREEN_1_NORMAL);

	public final ChatColorGradient memoryUsageGradient = new ChatColorGradient()
			.add(.60f, PandaTheme.CHAT_GREEN_1_NORMAL)
			.add(.70f, NamedTextColor.YELLOW)
			.add(.80f, NamedTextColor.GOLD)
			.add(.90f, NamedTextColor.RED)
			.add(.95f , NamedTextColor.DARK_RED);
	
	
	private PerformanceAnalysisManager() {
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
		
		BossBar bossBar = BossBar.bossBar(text("TPS Serveur"), 0, Color.GREEN, Overlay.NOTCHED_20);
		tpsBar = new AutoUpdatedBossBar(bossBar, new TPSBossBarUpdater());
		tpsBar.scheduleUpdateTimeSyncThreadAsync(1000, 100);
		
		BossBar bossMemBar = BossBar.bossBar(text("Mémoire Serveur"), 0, Color.GREEN, Overlay.NOTCHED_10);
		memoryBar = new AutoUpdatedBossBar(bossMemBar, new MemoryBossBarUpdater());
		memoryBar.scheduleUpdateTimeSyncThreadAsync(1000, 100);
		
	}

	public boolean barsContainsPlayer(Player p) {
		return barPlayers.contains(p);
	}
	
	public synchronized void addPlayerToBars(Player p) {
		barPlayers.add(p);
		p.showBossBar(tpsBar.bar);
		p.showBossBar(memoryBar.bar);
		for (BossBar bar : relatedBossBars)
			p.showBossBar(bar);
	}
	
	public synchronized void removePlayerToBars(Player p) {
		p.hideBossBar(tpsBar.bar);
		p.hideBossBar(memoryBar.bar);
		for (BossBar bar : relatedBossBars)
			p.hideBossBar(bar);
		barPlayers.remove(p);
	}
	
	public synchronized void addBossBar(BossBar bar) {
		if (relatedBossBars.contains(bar))
			return;
		relatedBossBars.add(bar);
		for (Player p : barPlayers)
			p.showBossBar(bar);
	}
	
	public synchronized void removeBossBar(BossBar bar) {
		if (!relatedBossBars.contains(bar))
			return;
		relatedBossBars.remove(bar);
		for (Player p : barPlayers)
			p.hideBossBar(bar);
	}
	
	public synchronized void cancelInternalBossBar() {
		tpsBar.cancel();
		memoryBar.cancel();
	}
	
	
	
	
	
	@EventHandler
	public synchronized void onTickStart(ServerTickStartEvent event) {
		tickStartNanoTime = System.nanoTime();
		tickStartCPUTime = threadMXBean.isThreadCpuTimeSupported() ? threadMXBean.getCurrentThreadCpuTime() : 0;
		
		lastInterTPSDuration = firstRecord == 0 ? 0 : (tickStartNanoTime - tickEndNanoTime);
	}
	
	@EventHandler
	public synchronized void onTickEnd(ServerTickEndEvent event) {
		tickEndNanoTime = System.nanoTime();
		long tickEndCPUTime = threadMXBean.isThreadCpuTimeSupported() ? threadMXBean.getCurrentThreadCpuTime() : 0;
		
		if (firstRecord == 0) firstRecord = System.currentTimeMillis();

		tpsTimes.add(System.currentTimeMillis());
		tpsDurations.add(tickEndNanoTime - tickStartNanoTime);
		tpsCPUTimes.add(tickEndCPUTime - tickStartCPUTime);
		interTPSDurations.add(lastInterTPSDuration);

		while (tpsTimes.size() > NB_TICK_HISTORY + 1)
			tpsTimes.poll();
		while (tpsDurations.size() > NB_TICK_HISTORY + 1)
			tpsDurations.poll();
		while (tpsCPUTimes.size() > NB_TICK_HISTORY + 1)
			tpsCPUTimes.poll();
		while (interTPSDurations.size() > NB_TICK_HISTORY + 1)
			interTPSDurations.poll();
	}
	
	
	
	

	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
			@SuppressWarnings("unchecked")
			AbstractPlayerManager<PaperOnlinePlayer, PaperOffPlayer> playerManager = (AbstractPlayerManager<PaperOnlinePlayer, PaperOffPlayer>) AbstractPlayerManager.getInstance();
			if (playerManager == null)
				return;
			PaperOffPlayer offP = playerManager.getOffline(event.getPlayer().getUniqueId());
			try {
				if ("true".equals(offP.getConfig("system.bar", "false"))) {
					SchedulerUtil.runOnServerThread(() -> addPlayerToBars(event.getPlayer()));
				}
			} catch (Exception e) {
				Log.severe("Cannot get player config", e);
			}
		});
		
	}


	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		removePlayerToBars(event.getPlayer());
	}

	
	private final long maxMem = Runtime.getRuntime().maxMemory();
	
	
	private class MemoryBossBarUpdater implements BarUpdater {
		@Override
		public void update(AutoUpdatedBossBar bar) {
			long allocMem = Runtime.getRuntime().totalMemory();
			long freeMem = Runtime.getRuntime().freeMemory();
			long usedMem = allocMem - freeMem;
			
			double progress = usedMem / (double)maxMem;
			progress = (progress < 0) ? 0 : (progress > 1) ? 1 : progress;
			
			Color barColor = (progress >= 0.85) ? Color.RED
					: (progress >= 0.65) ? Color.YELLOW
					: Color.GREEN;
			
			TextColor usedColor = memoryUsageGradient.pickColorAt((float)progress);
			
			Chat display = infoText("Mémoire : ")
					.then(text("Util:" + MemoryUtil.humanReadableSize(usedMem, MemoryUnit.MB, false)
							+ "/" + MemoryUtil.humanReadableSize(maxMem, MemoryUnit.MB, false)
							)
							.color(usedColor)
					)
					.thenText(" Allouée:" + MemoryUtil.humanReadableSize(allocMem, MemoryUnit.MB, false));
			
			bar.setColor(barColor);
			bar.setProgress(progress);
			bar.setTitle(display);
		}
	}
	
	
	
	private class TPSBossBarUpdater implements BarUpdater {
		@Override
		public void update(AutoUpdatedBossBar bar) {
			synchronized (PerformanceAnalysisManager.this) {
				float tps1s = getTPS(1000);
				
				Color barColor = (tps1s >= 25) ? Color.WHITE
						: (tps1s >= 12) ? Color.GREEN
						: (tps1s >= 6) ? Color.YELLOW
						: Color.RED;
				double barProgress = Double.isNaN(tps1s) ? 0 : tps1s/20d;
				
				Chat title;
				if (alteredTPSTitle != null) {
					title = infoText("TPS : ").then(alteredTPSTitle);
				}
				else {
					
					String tps1sDisplay = Double.isNaN(tps1s) ? "N/A" : (Math.round(tps1s)) + "";
					
					
					int[] tpsHistory = getTPSHistory();
					
					// keep the legacy text when generating the bar to save space when converting to component
					StringBuilder s = new StringBuilder();
					TextColor prevC = null;
					for (int i = 58; i >= 0; i--) {
						int t = tpsHistory[i];
						TextColor newC = tps1sGradient.pickColorAt(t);
						if (!newC.equals(prevC)) {
							s.append(text("|").color(newC).getLegacyText());
							prevC = newC;
						}
						else {
							s.append("|");
						}
					}
					
					
					
					// tick time measurement
					Chat timings;
					int nbTick1s = getTPS1s();
					if (nbTick1s == 0) {
						// we have a lag spike, so we need to display the time since lagging
						long lagDurationSec = System.nanoTime() - tickEndNanoTime;
						timings = text("(")
								.thenFailure("lag:" + displayRound10(lagDurationSec / (double) 1_000_000_000) + "s")
								.thenText(")");
					}
					else {
						float avgTickDuration1s = getAvgNano(tpsDurations, nbTick1s)/1_000_000;
						
						float avgTickCPUTime1s = getAvgNano(tpsCPUTimes, nbTick1s)/1_000_000;
						TextColor avgTickCPUTime1sColor = (avgTickDuration1s < 46 || avgTickCPUTime1s < 20) ? PandaTheme.CHAT_GREEN_1_NORMAL
								: (avgTickCPUTime1s < 30) ? NamedTextColor.YELLOW
								: (avgTickCPUTime1s < 40) ? NamedTextColor.GOLD
								: (avgTickCPUTime1s < 50) ? NamedTextColor.RED
								: NamedTextColor.DARK_RED;
						
						float avgTickWaitingTime1s = avgTickDuration1s - avgTickCPUTime1s;
						TextColor avgTickWaitingTime1sColor = (avgTickDuration1s < 46 || avgTickWaitingTime1s < 20) ? PandaTheme.CHAT_GREEN_1_NORMAL
								: (avgTickWaitingTime1s < 30) ? NamedTextColor.YELLOW
								: (avgTickWaitingTime1s < 40) ? NamedTextColor.GOLD
								: (avgTickWaitingTime1s < 50) ? NamedTextColor.RED
								: NamedTextColor.DARK_RED;
						
						
						
						float avgInterTickDuration1s = getAvgNano(interTPSDurations, nbTick1s)/1_000_000;
						TextColor avgInterTickDuration1sColor = (avgInterTickDuration1s > 10) ? PandaTheme.CHAT_GREEN_1_NORMAL
								: (avgInterTickDuration1s > 4) ? NamedTextColor.YELLOW
								: (avgTickDuration1s < 46) ? NamedTextColor.GOLD
								: NamedTextColor.RED;
						
						timings = text("(R/W/S:")
								.then(text(Math.round(avgTickCPUTime1s)).color(avgTickCPUTime1sColor))
								.thenText("/")
								.then(text(Math.round(avgTickWaitingTime1s)).color(avgTickWaitingTime1sColor))
								.thenText("/")
								.then(text(Math.round(avgInterTickDuration1s)).color(avgInterTickDuration1sColor))
								.thenText("ms)");
					}
					
					title = infoText("TPS [")
							.thenLegacyText(s.toString())
							.thenText("] ")
							.then(text(tps1sDisplay + "/" + getTargetTickRate() + " ").color(tps1sGradient.pickColorAt(tps1s)))
							.then(timings);
				}
				

				bar.setTitle(title);
				bar.setColor(barColor);
				bar.setProgress(Math.max(0, Math.min(1, barProgress)));
				
			}
			
		}
	}
	
	private Chat alteredTPSTitle = null;
	
	public synchronized void setAlteredTPSTitle(Chat title) {
		alteredTPSTitle = title;
	}
	
	
	
	
	
	
	
	// special case where the getTPS method always returns a whole number when retrieving the TPS for 1 sec
	public int getTPS1s() {
		return (int) getTPS(1_000);
	}

	/**
	 * 
	 * @param nbTicks number of ticks when the avg value is computed from history
	 * @return the avg number of TPS in the interval
	 */
	public synchronized float getAvgNano(List<Long> data, int nbTicks) {
		if (data.isEmpty())
			return 0;

		if (nbTicks > data.size()) nbTicks = data.size();

		long sum = 0;
		for (int i = data.size() - nbTicks; i < data.size(); i++)
			sum += data.get(i);

		return sum / (float) nbTicks;
	}

	/**
	 * 
	 * @param nbMillis number of milliseconds when the avg TPS is computed from history
	 * @return the avg number of TPS in the interval
	 */
	public synchronized float getTPS(long nbMillis) {
		if (tpsTimes.isEmpty())
			return 0;

		long currentMillis = System.currentTimeMillis();

		if (currentMillis - nbMillis < firstRecord) nbMillis = currentMillis - firstRecord;

		int count = 0;
		for (Long v : tpsTimes) {
			if (v > currentMillis - nbMillis) count++;
		}

		return count * (1000 / (float) nbMillis);
	}
	
	
	public synchronized int[] getTPSHistory() {
		int[] history = new int[60];

		long currentSec = System.currentTimeMillis() / 1000;

		for (Long v : tpsTimes) {
			int sec = (int) (currentSec - v/1000) - 1;
			if (sec < 0 || sec >= 60)
				continue;
			history[sec]++;
		}
		
		return history;
		
	}



	public static int getTargetTickRate() {
		return Math.round(Bukkit.getServerTickManager().getTickRate());
	}
	
	
	
	
	
	public static void gc(CommandSender sender) {
		long t1 = System.currentTimeMillis();
		long alloc1 = Runtime.getRuntime().totalMemory();
		System.gc();
		long t2 = System.currentTimeMillis();
		long alloc2 = Runtime.getRuntime().totalMemory();
		long released = alloc1 - alloc2;
		Chat releasedMemoryMessage = released > 0
				? successText(MemoryUtil.humanReadableSize(released) + " of memory released for the OS.")
				: released < 0
				? failureText(MemoryUtil.humanReadableSize(-released) + " of memory taken from the OS.")
				: chat();
		
		Chat finalMessage = successText("GC completed in " + TimeUtil.durationToString(t2 - t1, true) + ". ")
				.then(releasedMemoryMessage);
		if (sender != null)
			sender.sendMessage(finalMessage);
		if (!(sender instanceof ConsoleCommandSender))
			Log.info(finalMessage.getLegacyText());
	}

	public static String displayRound10(double val) {
		long v = (long) Math.ceil(val * 10);
		return "" + (v / 10f);
	}

}
