package fr.pandacube.lib.paper.util;

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
	 * It doesn't correspond to the player hitbox height.<br/>
	 * <br/>
	 * <code>1.88</code> is an approximated value, estimated by ingame tests.
	 */
	public static final double PLAYER_SKIN_HEIGHT = 1.88;
	/**
	 * Value provided by Craftbukkit's {@code CraftPlayer#getEyeHeight(boolean)} source code
	 */
	public static final double PLAYER_EYE_HEIGHT = 1.62;
	/**
	 * The visual height of a Minecraft player skin, when he is standing up and sneaking,
	 * from the ground where the player is standing on, to the above of the first layer of the head skin.
	 * It may not correspond to the player hitbox height.<br/>
	 * <br/>
	 * The current value is the height of the player's hitbox when sneaking. Even if this
	 * is close to the real value (tested in game), this is not the exact value.
	 */
	public static final double PLAYER_SKIN_HEIGHT_SNEAK = 1.65;
	/**
	 * Value provided by Craftbukkit's {@code CraftPlayer#getEyeHeight(boolean)} source code
	 */
	public static final double PLAYER_EYE_HEIGHT_SNEAK = 1.54;
	public static final double PLAYER_SKIN_PIXEL_SIZE = PLAYER_SKIN_HEIGHT / 32;
	public static final double PLAYER_HEAD_ROTATION_HEIGHT = PLAYER_SKIN_PIXEL_SIZE * 24;
	public static final double PLAYER_HEAD_ROTATION_HEIGHT_SNEAK = PLAYER_HEAD_ROTATION_HEIGHT - (PLAYER_SKIN_HEIGHT - PLAYER_SKIN_HEIGHT_SNEAK);
	public static final double PLAYER_HEAD_SIZE = PLAYER_SKIN_PIXEL_SIZE * 8;
	
	
	
	
	
	
	
	
	

	
	
	
	
	/**
	 * Get the {@link Location}s of the 8 vertex of the player head<br/>
	 * This method only work if the player is standing up
	 * (not dead, not glyding, not sleeping).
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
 
        if (Math.abs(c.getX()) > e.getX() + ad.getX()){
            return false;
        }
 
        if (Math.abs(c.getY()) > e.getY() + ad.getY()){
            return false;
        }
 
        if (Math.abs(c.getZ()) > e.getX() + ad.getZ()){
            return false;
        }
 
        if (Math.abs(d.getY() * c.getZ() - d.getZ() * c.getY()) > e.getY() * ad.getZ() + e.getZ() * ad.getY() + epsilon){
            return false;
        }
 
        if (Math.abs(d.getZ() * c.getX() - d.getX() * c.getZ()) > e.getZ() * ad.getX() + e.getX() * ad.getZ() + epsilon){
            return false;
        }

        if (Math.abs(d.getX() * c.getY() - d.getY() * c.getX()) > e.getX() * ad.getY() + e.getY() * ad.getX() + epsilon){
            return false;
        }

        return true;
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * This vector consider Minecraft X Y Z axis orientation,
	 * but consider standard (not Minecraft) radian values for yaw and pitch.<br/>
	 * The length of this Vector (based on {@link #x}, {@link #y} and {@link #z} values)
	 * Is always 1.
	 * 
	 * <pre>Yaw :
	 * North (-z) = -PI/2
	 * East  (+x) = 0
	 * South (+z) = PI/2
	 * West  (-x) = ±PI
	 * 
	 * Pitch :
	 * Up   (+y) = PI/2
	 * Down (-y) = -PI/2</pre>
	 */
	public static class DirectionalVector {
		/**
		 * The X cartesian coordinate of this {@link DirectionalVector}.
		 * It correspond to the X (west to east) axis in a Minecraft world.
		 */
		public final double x;
		
		/**
		 * The Y cartesian coordinate of this {@link DirectionalVector}.
		 * It correspond to the Y (bottom to top) axis in a Minecraft world.
		 */
		public final double y;
		
		/**
		 * The Z cartesian coordinate of this {@link DirectionalVector}.
		 * It correspond to the Z (north to south) axis in a Minecraft world.
		 */
		public final double z;
		
		/**
		 * The azimuthal angle φ (phi) of this {@link DirectionalVector}, in radian.
		 * It correspond with Minecraft world as follow :
		 * <pre>Yaw :
		 * North (-z) = -PI/2
		 * East  (+x) = 0
		 * South (+z) = PI/2
		 * West  (-x) = ±PI</pre>
		 */
		public final double yaw;

		/**
		 * The polar angle θ (theta) of this {@link DirectionalVector}, in radian.
		 * It correspond with Minecraft world as follow :
		 * <pre>Pitch :
		 * Down (-y) = -PI/2
		 * Up   (+y) = PI/2</pre>
		 */
		public final double pitch;
		
		/**
		 * Initialize this {@link DirectionalVector} with the yaw and pitch
		 * contained in the provided {@link Location}.
		 * {@link Location#getYaw()} and {@link Location#getPitch()} values are automatically
		 * converted to conform {@link #yaw} and {@link #pitch} specification.
		 */
		public DirectionalVector(Location l) {
			this(
					Math.toRadians(((l.getYaw()+90)%360) > 180 ? ((l.getYaw()+90)%360)-360 : ((l.getYaw()+90)%360)),
					-Math.toRadians(l.getPitch())
					);
			/*              MC     : +90    : %360   : >180 -> -360
			 * South (+z) = 0, 360 : 90-450 : 90     : 90         : PI/2
			 * West  (-x) = 90     : 180    : 180    : ±180       : ±PI
			 * North (-z) = 180    : 270    : 270    : -90        : -PI/2
			 * East  (+x) = 270    : 360    : 0-360  : 0          : 0
			 */
		}
		

		
		/**
		 * @param v the vector representing the direction. If v.getX() and v.getZ() are 0,
		 * the yaw will be 0. This may have inconsistence if the vector is calculated
		 * from a {@link Location}'s yaw and pitch. In this case, prefer using
		 * {@link #DirectionalVector(Location)}. The {@link Vector} is
		 * normalized if necessary (does not modify the provided {@link Vector}).
		 */
		public DirectionalVector(Vector v) {
			this(v.getX(), v.getY(), v.getZ());
			// this((v = v.clone().normalize()).getX(), v.getY(), v.getZ());
		}
		
		

		private DirectionalVector(double x, double y, double z) {
			double vectSize = Math.sqrt(x*x + y*y + z*z);
			this.x = x/vectSize;
			this.y = y/vectSize;
			this.z = z/vectSize;

	        if (x == 0.0 && z == 0.0) {
	        	pitch = y > 0.0 ? PId2 : -PId2;
	        	yaw = 0;
	        }
	        else {
		        yaw = Math.atan2(z, x);
		        pitch = Math.atan(y / Math.sqrt(x*x + z*z));
	        }
		}
		
		private DirectionalVector(double x, double y, double z, double yaw, double pitch) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.yaw = yaw;
			this.pitch = pitch;
		}
		
		private DirectionalVector(double yaw, double pitch) {
			this.yaw = yaw;
			this.pitch = pitch;

	        y = Math.sin(pitch);
	        
	        double cosPitch = Math.cos(pitch);
	        x = cosPitch * Math.cos(yaw);
	        z = cosPitch * Math.sin(yaw);
			
		}
		
		
		
		
		public Vector toVector() {
			return new Vector(x, y, z);
		}
		
		
		/**
		 * Set the yaw and the pitch of the provided {@link Location}
		 * with the values inside the current {@link DirectionalVector}
		 * after conversion of these values
		 */
		public void putIntoLocation(Location l) {
			/*              std   : -PI/2         : <0 ? +2PI : MC
			 * South (+z) = PI/2  : 0             : 0         : 0, 360
			 * West  (-x) = ±PI   : -3PI/2 - PI/2 : PI/2      : 90
			 * North (-z) = -PI/2 : -PI           : PI        : 180
			 * East  (+x) = 0     : -PI/2         : 3PI/2     : 270
			 */
			l.setYaw((float)Math.toDegrees(yaw < PId2 ? yaw + PIx2 - PId2 : yaw - PId2));
			l.setPitch((float)Math.toDegrees(-pitch));
		}
		
		
		
		
		public DirectionalVector getOpposite() {
			return new DirectionalVector(
					-x,
					-y,
					-z,
					(yaw > 0 ? (yaw - PI) : (yaw + PI)),
					-pitch
					);
		}
		
		/**
		 * If the current direction is the player face direction,
		 * this method return the direction of the back of the head.
		 * This is an alias of {@link #getOpposite()}
		 */
		public DirectionalVector getBackDirection() {
			return getOpposite();
		}
		
		/**
		 * If the current direction is the player face direction,
		 * this method return the direction of the bottom of the head.
		 */
		public DirectionalVector getBottomDirection() {
			return new DirectionalVector(
					(pitch > 0 ? yaw : (yaw > 0 ? (yaw - PI) : (yaw + PI))),
					(pitch > 0 ? (pitch - PId2) : (-PId2 - pitch))
					);
		}
		
		/**
		 * If the current direction is the player face direction,
		 * this method return the direction of the top of the head.
		 */
		public DirectionalVector getTopDirection() {
			return new DirectionalVector(
					(pitch < 0 ? yaw : (yaw > 0 ? (yaw - PI) : (yaw + PI))),
					(pitch < 0 ? (pitch + PId2) : (PId2 - pitch))
					);
		}
		

		
		/**
		 * If the current direction is the player face direction,
		 * this method return the direction of the left of the head.
		 */
		public DirectionalVector getLeftDirection() {
			return new DirectionalVector(
					yaw > -PId2 ? (yaw - PId2) : (yaw - PId2 + PIx2),
					0
					);
		}
		

		
		/**
		 * If the current direction is the player face direction,
		 * this method return the direction of the right of the head.
		 */
		public DirectionalVector getRightDirection() {
			return new DirectionalVector(
					yaw < PId2 ? (yaw + PId2) : (yaw + PId2 - PIx2),
					0
					);
		}
		
		
		
		
	}
	
	
	
	
	
}
