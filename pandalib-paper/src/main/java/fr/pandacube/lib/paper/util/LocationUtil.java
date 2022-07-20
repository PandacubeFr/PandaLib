package fr.pandacube.lib.paper.util;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitTask;

import fr.pandacube.lib.util.RandomUtil;
import fr.pandacube.lib.paper.PandaLibPaper;

public class LocationUtil {
	
	public static String conciseToString(Location loc) {
		String world = loc.getWorld() == null ? "null" : loc.getWorld().getName();
		return "(" + world + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ")";
	}
	/**
	 * Return a random secure location in the provided world, inside the current
	 * WorldBorder. Will be on the surface, for non-nether world, or below the roof of the nether world
	 * @param w the world in which to pick a location
	 * @param extraSecureCheck provides extra checks to determine location security
	 */
	public static CompletableFuture<Location> getRandomSecureLocation(World w, Predicate<Location> extraSecureCheck) {

		WorldBorder wb = w.getWorldBorder();

		Location minWorld = wb.getCenter().clone().add(-wb.getSize()/2, 0, -wb.getSize()/2);
		Location maxWorld = wb.getCenter().clone().add(wb.getSize()/2, 0, wb.getSize()/2);
		
		return getRandomSecureLocation(w, minWorld, maxWorld, extraSecureCheck);
		
	}
	
	
	
	

	private static final int maxTryBeforeCancelRandomLocation = 75;
	public static CompletableFuture<Location> getRandomSecureLocation(World w, Location min, Location max, Predicate<Location> extraSecureCheck) {

		CompletableFuture<Location> future = new CompletableFuture<>();

		AtomicReference<BukkitTask> t = new AtomicReference<>();
		AtomicInteger count = new AtomicInteger(0);
		
		t.set(Bukkit.getScheduler().runTaskTimer(PandaLibPaper.getPlugin(), () -> {
			
			count.incrementAndGet();
			if (count.get() > maxTryBeforeCancelRandomLocation) {
				future.complete(null);
				t.get().cancel();
			}
			
			// generate a random (x,z) coordinate
			Location ret = new Location(w,
					RandomUtil.nextIntBetween(min.getBlockX(), max.getBlockX()) + 0.5,
					w.getMaxHeight() - 1,
					RandomUtil.nextIntBetween(min.getBlockZ(), max.getBlockZ()) + 0.5);
			
			// find a secure y value
			ret = getSecureLocationOrNull(ret);
			
			if (ret == null)
				// there is no secure y position for the provided (x,z) values
				return;
			
			if (extraSecureCheck != null && !extraSecureCheck.test(ret))
				return; // extra checks didn’t validate the location
			
			//if (checkCubo && PandacubePaper.getPlugin().cuboManager != null)
			//	if (PandacubePaper.getPlugin().cuboManager.getCuboFromLocation(ret) != null)
			//		return; // il y a un cubo à l'endroit aléatoire sélectionnée

			// tout est bon
			future.complete(ret);
			t.get().cancel();
			
		}, 1, 1));
		
		return future;
	}
	
	
	
	
	

	/**
	 * 
	 * @param l the source location
	 * @return a secure location with the same X and Z coordinate as the
	 * provided location, but Y modified to ensure security for player
	 * who will be teleported to this location.
	 * May return null if it is impossible to securize find a secure location.
	 */
	public static Location getSecureLocationOrNull(Location l) {
		l = l.clone();
		l.setY(l.getWorld().getEnvironment() == Environment.NETHER ? 126 : 256);
		Block b = l.getBlock();

		while (b.getY() >= 0 && !currPosSafe(b))
			b = b.getRelative(BlockFace.DOWN);
		
		return currPosSafe(b) ? b.getLocation().add(0.5, 0, 0.5) : null;
		
	}
	
	public static boolean currPosSafe(Block b) {
		return b.getY() >= b.getWorld().getMinHeight() + 1 && b.getY() <= b.getWorld().getMaxHeight()
				&& isSecureFloor(b.getRelative(BlockFace.DOWN))
				&& isAir(b)
				&& isAir(b.getRelative(BlockFace.UP));
	}
	
	public static boolean isAir(Block b) { return b.getType() == Material.AIR; }
	public static boolean isSecureFloor(Block b) { return !isAir(b) && !dangerousBlocks.contains(b.getType()); }
	
	public static final Set<Material> dangerousBlocks = EnumSet.of(
			Material.LAVA,
			Material.WATER,
			Material.COBWEB,
			Material.MAGMA_BLOCK,
			Material.CAMPFIRE,
			Material.SOUL_CAMPFIRE,
			Material.FIRE,
			Material.SOUL_FIRE,
			Material.WITHER_ROSE,
			Material.END_PORTAL,
			Material.NETHER_PORTAL,
			Material.END_GATEWAY
	);
	
	
	
	

	/**
	 * Check if the {@link Location} l is inside the cuboïd formed by the 2 others
	 * Locations min and max.
	 * @return true if l is inside the cuboid min-max
	 */
	public static boolean isIn(Location l, Location min, Location max) {
		return (l.getWorld().equals(min.getWorld()) && l.getWorld().equals(max.getWorld()) && l.getX() >= min.getX()
				&& l.getX() <= max.getX() && l.getY() >= min.getY() && l.getY() <= max.getY() && l.getZ() >= min.getZ()
				&& l.getZ() <= max.getZ());
	}

	
	
	
	
	
	/**
	 * Return a new location based on the linear interpolation between p0 and p1, according to the value c.
	 * @param c between 0 and 1. If 0, it returns p0 and if 1, returns p1. Other finite numbers are allowed, but the returned location wont be part of the {@code [p0;p1]} segment.
	 * @return The location, linearly interpolated between p0 and p1 with the value c. The yaw and pitch in the returned location are those of p0.
	 * @throws IllegalArgumentException if the provided locations are not in the same world.
	 */
	public static Location lerp(Location p0, Location p1, float c) {
		return p0.clone().add(p1.clone().subtract(p0).multiply(c));
	}
	
	

}
