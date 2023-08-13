package fr.pandacube.lib.paper.geometry;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class GeometryUtil {
	
	/**
	 * Value equal to <code>{@link Math#PI}</code>.
	 */
	public static final double PI   = Math.PI;
	
	/**
	 * Value equal to <code>{@link Math#PI} / 2</code>.
	 */
	public static final double PId2 = PI/2;
	
	/**
	 * Value equal to <code>{@link Math#PI} * 2</code>.
	 */
	public static final double PIx2 = PI*2;
	
	
	
	
	
	/*
	 * Player geometry
	 */
	
	
	/**
	 * The visual height of a Minecraft player skin, when he is standing up and not sneaking,
	 * from the ground where the player is standing on, to the above of the first layer of the head skin.
	 * It doesn't correspond to the player hit box height.<br/>
	 * <br/>
	 * The value is provided in Minecraft Wiki.
	 */
	public static final double PLAYER_SKIN_HEIGHT = 1.85;
	/**
	 * Value provided by net.minecraft.world.entity.player.Player#getStandingEyeHeight
	 */
	public static final double PLAYER_EYE_HEIGHT = 1.62;
	/**
	 * The visual height of a Minecraft player skin, when he is standing up and sneaking,
	 * from the ground where the player is standing on, to the above of the first layer of the head skin.
	 * It may not correspond to the player hit box height.<br/>
	 * <br/>
	 * The current value is the height of the player's hit box when sneaking. Even if this
	 * is close to the real value (tested in game), this is not the exact value.
	 */
	public static final double PLAYER_SKIN_HEIGHT_SNEAK = 1.50;
	/**
	 * Value provided by net.minecraft.world.entity.player.Player#getStandingEyeHeight
	 */
	public static final double PLAYER_EYE_HEIGHT_SNEAK = 1.27;
	public static final double PLAYER_SKIN_PIXEL_SIZE = PLAYER_SKIN_HEIGHT / 32;
	public static final double PLAYER_HEAD_ROTATION_HEIGHT = PLAYER_SKIN_PIXEL_SIZE * 24;
	public static final double PLAYER_HEAD_ROTATION_HEIGHT_SNEAK = PLAYER_HEAD_ROTATION_HEIGHT - (PLAYER_SKIN_HEIGHT - PLAYER_SKIN_HEIGHT_SNEAK);
	public static final double PLAYER_HEAD_SIZE = PLAYER_SKIN_PIXEL_SIZE * 8;
	
	
	
	
	
	
	
	
	

	
	
	
	
	/**
	 * Get the {@link Location}s of the 8 vertex of the player head<br/>
	 * This method only work if the player is standing up
	 * (not dead, not gliding, not sleeping).
	 * @param playerLocation the location of the player, generally provided by {@link Player#getLocation()}
	 * @param isSneaking if the player is sneaking. Generally {@link Player#isSneaking()}
	 * @return an array of 8 {@link Location}s with x, y, and z values filled (yaw and pitch are ignored).
	 * <pre>
	 * return[0] // top front left
	 * return[1] // top front right
	 * return[2] // bottom front left
	 * return[3] // bottom front right
	 * return[4] // top back left
	 * return[5] // top back right
	 * return[6] // bottom back left
	 * return[7] // bottom back right
	 * </pre>
	 */
	public static Location[] getPlayerHeadGeometry(Location playerLocation, boolean isSneaking) {
		Location[] headAnglesPoints = new Location[8];
		
		Location playerHeadRotationLocation = playerLocation.clone()
				.add(0, isSneaking ? PLAYER_HEAD_ROTATION_HEIGHT_SNEAK : PLAYER_HEAD_ROTATION_HEIGHT, 0);
		
		DirectionalVector frontDirection = new DirectionalVector(playerHeadRotationLocation);
		Vector frontHalfVector = frontDirection.toVector().multiply(PLAYER_HEAD_SIZE/2);
		Vector backHalfDirection = frontDirection.getBackDirection().toVector().multiply(PLAYER_HEAD_SIZE/2);
		Vector leftHalfVector = frontDirection.getLeftDirection().toVector().multiply(PLAYER_HEAD_SIZE/2);
		Vector rightHalfVector = frontDirection.getRightDirection().toVector().multiply(PLAYER_HEAD_SIZE/2);
		Vector topVector = frontDirection.getTopDirection().toVector().multiply(PLAYER_HEAD_SIZE);

		Location bottomFrontMiddle = playerHeadRotationLocation.clone().add(frontHalfVector);
		Location bottomBackMiddle = playerHeadRotationLocation.clone().add(backHalfDirection);
		
		Location topFrontMiddle = bottomFrontMiddle.clone().add(topVector);
		Location topBackMiddle = bottomBackMiddle.clone().add(topVector);
		
		headAnglesPoints[0] = topFrontMiddle.clone().add(leftHalfVector);
		headAnglesPoints[1] = topFrontMiddle.clone().add(rightHalfVector);
		headAnglesPoints[2] = bottomFrontMiddle.clone().add(leftHalfVector);
		headAnglesPoints[3] = bottomFrontMiddle.clone().add(rightHalfVector);
		headAnglesPoints[4] = topBackMiddle.clone().add(leftHalfVector);
		headAnglesPoints[5] = topBackMiddle.clone().add(rightHalfVector);
		headAnglesPoints[6] = bottomBackMiddle.clone().add(leftHalfVector);
		headAnglesPoints[7] = bottomBackMiddle.clone().add(rightHalfVector);
		
		return headAnglesPoints;
	}
	
	
	
	
	
	/**
	 * Check if the path from <i>start</i> location to <i>end</i> pass through
	 * the axis aligned bounding box defined by <i>min</i> and <i>max</i>.
	 */
    public static boolean hasIntersection(Vector start, Vector end, Vector min, Vector max) {
        final double epsilon = 0.0001f;
 
        Vector d = end.clone().subtract(start).multiply(0.5);
        Vector e = max.clone().subtract(min).multiply(0.5);
        Vector c = start.clone().add(d).subtract(min.clone().add(max).multiply(0.5));
        Vector ad = d.clone();
        ad.setX(Math.abs(ad.getX()));
        ad.setY(Math.abs(ad.getY()));
        ad.setZ(Math.abs(ad.getZ()));

        return !(
				Math.abs(c.getX()) > e.getX() + ad.getX()
				|| Math.abs(c.getY()) > e.getY() + ad.getY()
				|| Math.abs(c.getZ()) > e.getX() + ad.getZ()
				|| Math.abs(d.getY() * c.getZ() - d.getZ() * c.getY()) > e.getY() * ad.getZ() + e.getZ() * ad.getY() + epsilon
				|| Math.abs(d.getZ() * c.getX() - d.getX() * c.getZ()) > e.getZ() * ad.getX() + e.getX() * ad.getZ() + epsilon
				|| Math.abs(d.getX() * c.getY() - d.getY() * c.getX()) > e.getX() * ad.getY() + e.getY() * ad.getX() + epsilon
		);
    }


}
