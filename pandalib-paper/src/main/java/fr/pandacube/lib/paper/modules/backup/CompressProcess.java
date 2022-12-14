package fr.pandacube.lib.paper.modules.backup;

import fr.pandacube.lib.chat.Chat;
import fr.pandacube.lib.paper.PandaLibPaper;
import fr.pandacube.lib.paper.modules.PerformanceAnalysisManager;
import fr.pandacube.lib.paper.util.AutoUpdatedBossBar;
import fr.pandacube.lib.util.FileUtils;
import fr.pandacube.lib.util.Log;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.bossbar.BossBar.Overlay;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.text.DateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.function.BiPredicate;

public abstract class CompressProcess implements Comparable<CompressProcess>, Runnable {
	protected final BackupManager backupManager;
	public final Type type;
	public final String name;
	
	
	
	private ZipCompressor compressor = null;
	
	protected CompressProcess(BackupManager bm, final Type t, final String n) {
		backupManager = bm;
		type = t;
		name = n;
	}
	
	@Override
	public int compareTo(final CompressProcess process) {
		return Long.compare(getNext(), process.getNext());
	}
	
	
	public abstract BiPredicate<File, String> getFilenameFilter();
	
	public abstract File getSourceDir();
	
	protected abstract void onCompressStart();
	
	protected abstract void onCompressEnd(boolean success);

	protected abstract File getTargetDir();

	protected abstract String getDisplayName();
	
	@Override
	public void run() {
		backupManager.compressRunning.set(this);

		try {
			BiPredicate<File, String> filter = getFilenameFilter();
			File sourceDir = getSourceDir();

			if (!sourceDir.exists()) {
				Log.warning("[Backup] Unable to compress " + ChatColor.GRAY + getDisplayName() + ChatColor.RESET + ": source directory " + sourceDir + " doesn’t exist");
				return;
			}

			File targetDir = getTargetDir();
			File target = new File(targetDir, getDateFileName() + ".zip");


			BossBar bossBar = BossBar.bossBar(Chat.text("Archivage"), 0, Color.YELLOW, Overlay.NOTCHED_20);
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

			onCompressStart();

			Bukkit.getScheduler().runTaskAsynchronously(PandaLibPaper.getPlugin(), () -> {
				Log.info("[Backup] Starting for " + ChatColor.GRAY + getDisplayName() + ChatColor.RESET + " ...");

				compressor = new ZipCompressor(sourceDir, target, 9, filter);

				PerformanceAnalysisManager.getInstance().addBossBar(bossBar);

				boolean success = false;
				try {
					compressor.compress();

					success = true;

					Log.info("[Backup] Finished for " + ChatColor.GRAY + getDisplayName() + ChatColor.RESET);

					backupManager.persist.updateDirtyStatusAfterCompress(type, name);

					displayDirtynessStatus();

					try {
						type.backupCleaner(backupManager.config).cleanupArchives(targetDir, getDisplayName());
					} catch (Exception e) {
						Log.severe(e);
					}
				}
				catch (final Exception e) {
					Log.severe("[Backup] Failed: " + sourceDir + " -> " + target, e);

					FileUtils.delete(target);
					if (target.exists())
						Log.warning("unable to delete: " + target);
				} finally {

					backupManager.compressRunning.set(null);
					boolean successF = success;
					Bukkit.getScheduler().runTask(PandaLibPaper.getPlugin(), () -> onCompressEnd(successF));

					try {
						Thread.sleep(2000);
					} catch(InterruptedException e) {
						Thread.currentThread().interrupt();
					}

					PerformanceAnalysisManager.getInstance().removeBossBar(bossBar);
				}
			});
		} finally {
			backupManager.compressRunning.set(null);
		}

	}
	
	

	public void displayDirtynessStatus() {
		if (hasNextScheduled() && type == Type.WORLDS) {
			Log.info("[Backup] " + ChatColor.GRAY + getDisplayName() + ChatColor.RESET + " is dirty. Next backup on "
					+ DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(new Date(getNext())));
		}
		else if (hasNextScheduled()) {
			Log.info("[Backup] " + ChatColor.GRAY + getDisplayName() + ChatColor.RESET + " next backup on "
					+ DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(new Date(getNext())));
		}
		else {
			Log.info("[Backup] " + ChatColor.GRAY + getDisplayName() + ChatColor.RESET + " is clean. Next backup not scheduled.");
		}
	}
	
	
	static DateTimeFormatter dateFileNameFormatter = new DateTimeFormatterBuilder()
			.appendValue(ChronoField.YEAR, 4)
			.appendValue(ChronoField.MONTH_OF_YEAR, 2)
			.appendValue(ChronoField.DAY_OF_MONTH, 2)
			.appendLiteral('-')
			.appendValue(ChronoField.HOUR_OF_DAY, 2)
			.appendValue(ChronoField.MINUTE_OF_HOUR, 2)
			.appendValue(ChronoField.SECOND_OF_MINUTE, 2)
			.toFormatter();


	private String getDateFileName() {
		return dateFileNameFormatter.format(ZonedDateTime.now());
	}


	public void logProgress() {
		if (compressor == null)
			return;
		Log.info("[Backup] " + ChatColor.GRAY + getDisplayName() + ChatColor.RESET + ": " + compressor.getState().getLegacyText());
	}
	
	
	
	public boolean couldRunNow() {
		if (!type.backupEnabled(backupManager.config))
			return false;
		if (!backupManager.persist.isDirty(type, name))
			return false;
		if (getNext() > System.currentTimeMillis())
			return false;
		return true;
	}
	
	
	
	
	public long getNext() {
		if (!hasNextScheduled())
			return Long.MAX_VALUE;
		return backupManager.getNextCompress(backupManager.persist.isDirtySince(type, name));
	}
	
	public boolean hasNextScheduled() {
		return backupManager.persist.isDirty(type, name);
	}
	
}
