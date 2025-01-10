package fr.pandacube.lib.paper.backup;

import fr.pandacube.lib.chat.Chat;
import fr.pandacube.lib.core.backup.BackupProcess;
import fr.pandacube.lib.paper.PandaLibPaper;
import fr.pandacube.lib.paper.modules.PerformanceAnalysisManager;
import fr.pandacube.lib.paper.util.AutoUpdatedBossBar;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.bossbar.BossBar.Overlay;
import org.bukkit.Bukkit;

/**
 * A backup process with specific logic around Paper server.
 */
public abstract class PaperBackupProcess extends BackupProcess {


	private BossBar bossBar;

	/**
	 * Instantiates a new backup process.
	 * @param bm the associated backup manager.
	 * @param id the process identifier.
	 */
	protected PaperBackupProcess(PaperBackupManager bm, String id) {
		super(bm, id);
	}

	@Override
	public PaperBackupManager getBackupManager() {
		return (PaperBackupManager) super.getBackupManager();
	}

	@Override
	protected void onBackupStart() {
		bossBar = BossBar.bossBar(Chat.text("Archivage"), 0, Color.YELLOW, Overlay.NOTCHED_20);
		AutoUpdatedBossBar auBossBar = new AutoUpdatedBossBar(bossBar, (bar) -> {
			bar.setTitle(Chat.infoText("Archivage ")
					.thenData(getDisplayName())
					.thenText(" : ")
					.then(compressor == null
							? Chat.text("Démarrage...")
							: compressor.getState()
					)
			);
			bar.setProgress(compressor == null ? 0 : compressor.getProgress());
		});
		auBossBar.scheduleUpdateTimeSyncThreadAsync(100, 100);
		PerformanceAnalysisManager.getInstance().addBossBar(bossBar);
	}

	@Override
	protected void onBackupEnd(boolean success) {
		Bukkit.getScheduler().runTaskLater(PandaLibPaper.getPlugin(), () -> {
			PerformanceAnalysisManager.getInstance().removeBossBar(bossBar);
			bossBar = null;
		}, 40);
	}

	
}
