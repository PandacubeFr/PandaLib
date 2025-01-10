package fr.pandacube.lib.paper.util;

import fr.pandacube.lib.paper.PandaLibPaper;
import fr.pandacube.lib.util.RandomUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitTask;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

/**
 * Utility class related to {@link Location}.
 */
public class LocationUtil {

	/**
	 * Gets a concise {@link String} representation of a {@link Location}.
	 * <p>
	 * The format is {@code (worldName, 12, 45, -1304)}. The coordinates are those of the containing block (the values
	 * are cast to int).
	 * @param loc the location.
	 * @return a short string representation of the location.
	 */
	public static String conciseToString(Location loc) {
		String world = loc.getWorld() == null ? "null" : loc.getWorld().getName();
		return "(" + world + ", " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")";
	}
	/**
	 * Return a random secure location in the provided world, inside the current WorldBorder of the world.
	 * Will be on the surface, for non-nether world, or below the roof of the nether world.
	 * @param w the world in which to pick a location.
	 * @param extraSecureCheck provides extra checks to determine location security.
	 * @return a future that will provide a random secure location.
	 */
	public static CompletableFuture<Location> getRandomSecureLocation(World w, Predicate<Location> extraSecureCheck) {

		WorldBorder wb = w.getWorldBorder();

		Location minWorld = wb.getCenter().clone().add(-wb.getSize()/2, 0, -wb.getSize()/2);
		Location maxWorld = wb.getCenter().clone().add(wb.getSize()/2, 0, wb.getSize()/2);
		
		return getRandomSecureLocation(w, minWorld, maxWorld, extraSecureCheck);
		
	}
	
	
	
	

	private static final int maxTryBeforeCancelRandomLocation = 75;

	/**
	 * Return a random secure location in the provided world, inside the bounding box defined by min and max.
	 * Will be on the surface (the height limits of the bounding box are ignored), for non-nether world, or below the
	 * roof of the nether world.
	 * @param w the world in which to pick a location.
	 * @param min the min of the bounding box.
	 * @param max the max of the bounding box.
	 * @param extraSecureCheck provides extra checks to determine location security.
	 * @return a future that will provide a random secure location.
	 */
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
					RandomUtil.rand.nextInt(min.getBlockX(), max.getBlockX()) + 0.5,
					w.getMaxHeight() - 1,
					RandomUtil.rand.nextInt(min.getBlockZ(), max.getBlockZ()) + 0.5);
			
			// find a secure y value
			ret = getSecureLocationOrNull(ret);
			
			if (ret == null)
				// there is no secure y position for the provided (x,z) values
				return;
			
			if (extraSecureCheck != null && !extraSecureCheck.test(ret))
				return; // extra checks didn't validate the location

			// all good
			future.complete(ret);
			t.get().cancel();
			
		}, 1, 1));
		
		return future;
	}
	
	
	
	
	

	/**
	 * Try to get a secure location with the same X and Z coordinate as the
	 * provided location, but Y modified to ensure security for player
	 * who will be teleported to this location.
	 * May return null if it is impossible to find a secure location.
	 * @param l the source location
	 * @return a secure location, or null if not found around the provided location.
	 */
	public static Location getSecureLocationOrNull(Location l) {
		l = l.clone();
		l.setY(l.getWorld().getEnvironment() == Environment.NETHER ? 126 : l.getWorld().getMaxHeight());
		Block b = l.getBlock();

		while (b.getY() >= 0 && !currPosSafe(b))
			b = b.getRelative(BlockFace.DOWN);
		
		return currPosSafe(b) ? b.getLocation().add(0.5, 0, 0.5) : null;
		
	}

	/**
	 * Tells if the provided block is a safe block to spawn/be teleported in.
	 * More specifically, this block and its block above is air, and the block below is a non-lethal solid block.
	 * @param b the block to test.
	 * @return true if the provided block is a safe block to spawn/be teleported in, false otherwise.
	 */
	public static boolean currPosSafe(Block b) {
		return b.getY() >= b.getWorld().getMinHeight() + 1 && b.getY() <= b.getWorld().getMaxHeight()
				&& isSecureFloor(b.getRelative(BlockFace.DOWN))
				&& isAir(b)
				&& isAir(b.getRelative(BlockFace.UP));
	}
	
	private static boolean isAir(Block b) { return b.getType() == Material.AIR; }
	private static boolean isSecureFloor(Block b) { return !isAir(b) && !dangerousBlocks.contains(b.getType()); }
	
	private static final Set<Material> dangerousBlocks = Set.of(
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
	 * Return a new location based on the linear interpolation between p0 and p1, according to the value c.
	 * @param p0 the first location.
	 * @param p1 the second location.
	 * @param c between 0 and 1. If 0, it returns p0 and if 1, returns p1. All finite numbers are allowed.
	 * @return The location, linearly interpolated between p0 and p1 with the value c. The yaw and pitch in the returned location are those of p0.
	 * @throws IllegalArgumentException if the provided locations are not in the same world.
	 */
	public static Location lerp(Location p0, Location p1, float c) {
		return p0.clone().add(p1.clone().subtract(p0).multiply(c));
	}


	private LocationUtil() {}
	

}
