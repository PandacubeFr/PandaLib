package fr.pandacube.lib.paper.geometry;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

/**
 * Utility class related to geometry and Minecraft.
 */
public class GeometryUtil {

	/**
	 * Value equal to <code>{@link Math#PI}</code>.
	 */
	static final double PI   = Math.PI;

	/**
	 * Value equal to <code>{@link Math#PI} / 2</code>.
	 */
	static final double PId2 = PI/2;

	/**
	 * Value equal to <code>{@link Math#PI} * 2</code>.
	 */
	static final double PIx2 = PI*2;
	
	
	
	
	
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
	/**
	 * The size of a skin pixel.
	 */
	public static final double PLAYER_SKIN_PIXEL_SIZE = PLAYER_SKIN_HEIGHT / 32;
	/**
	 * The height of the center of rotation of the head, that is the distance between neck and the player's foot.
	 */
	public static final double PLAYER_HEAD_ROTATION_HEIGHT = PLAYER_SKIN_PIXEL_SIZE * 24;
	/**
	 * The height of the center of rotation of the head, that is the distance between neck and the player's foot, but when the player is sneaking.
	 */
	public static final double PLAYER_HEAD_ROTATION_HEIGHT_SNEAK = PLAYER_HEAD_ROTATION_HEIGHT - (PLAYER_SKIN_HEIGHT - PLAYER_SKIN_HEIGHT_SNEAK);
	/**
	 * The size of the first layer of the players head.
	 */
	public static final double PLAYER_HEAD_SIZE = PLAYER_SKIN_PIXEL_SIZE * 8;
	
	
	
	
	
	
	
	

	
	
	
	
	/**
	 * Get the {@link Location}s of the 8 vertex of the player head<br/>
	 * This method only work if the player is standing up
	 * (not dead, not gliding, not sleeping, not swimming).
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
	 * @param start the start of the path.
	 * @param end the end of the path.
	 * @param min the min of the bounding box.
	 * @param max the max of the bounding box.
	 * @return true if the path intersects the bounding box.
	 * @deprecated use {@link BoundingBox#rayTrace(Vector, Vector, double)} instead.
	 */
	@Deprecated
    public static boolean hasIntersection(Vector start, Vector end, Vector min, Vector max) {
        RayTraceResult res = BoundingBox.of(min, max).rayTrace(start, end.clone().subtract(start), end.distance(start));
		return res != null;
    }



	private GeometryUtil() {}


}
