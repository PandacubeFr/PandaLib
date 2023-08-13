package fr.pandacube.lib.paper.geometry;

import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * This vector considers Minecraft X Y Z axis orientation,
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
public class DirectionalVector {
    /**
     * The X cartesian coordinate of this {@link DirectionalVector}.
     * It corresponds to the X (west to east) axis in a Minecraft world.
     */
    public final double x;

    /**
     * The Y cartesian coordinate of this {@link DirectionalVector}.
     * It corresponds to the Y (bottom to top) axis in a Minecraft world.
     */
    public final double y;

    /**
     * The Z cartesian coordinate of this {@link DirectionalVector}.
     * It corresponds to the Z (north to south) axis in a Minecraft world.
     */
    public final double z;

    /**
     * The azimuthal angle φ (phi) of this {@link DirectionalVector}, in radian.
     * It corresponds with Minecraft world as follows :
     * <pre>Yaw :
     * North (-z) = -PI/2
     * East  (+x) = 0
     * South (+z) = PI/2
     * West  (-x) = ±PI</pre>
     */
    public final double yaw;

    /**
     * The polar angle θ (theta) of this {@link DirectionalVector}, in radian.
     * It corresponds with Minecraft world as follows :
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
                Math.toRadians(((l.getYaw() + 90) % 360) > 180 ? ((l.getYaw() + 90) % 360) - 360 : ((l.getYaw() + 90) % 360)),
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
     *          the yaw will be 0. This may have inconsistency if the vector is calculated
     *          from a {@link Location}'s yaw and pitch. In this case, prefer using
     *          {@link #DirectionalVector(Location)}. The {@link Vector} is
     *          normalized if necessary (does not modify the provided {@link Vector}).
     */
    public DirectionalVector(Vector v) {
        this(v.getX(), v.getY(), v.getZ());
        // this((v = v.clone().normalize()).getX(), v.getY(), v.getZ());
    }


    private DirectionalVector(double x, double y, double z) {
        double vecSize = Math.sqrt(x * x + y * y + z * z);
        this.x = x / vecSize;
        this.y = y / vecSize;
        this.z = z / vecSize;

        if (x == 0.0 && z == 0.0) {
            pitch = y > 0.0 ? GeometryUtil.PId2 : -GeometryUtil.PId2;
            yaw = 0;
        } else {
            yaw = Math.atan2(z, x);
            pitch = Math.atan(y / Math.sqrt(x * x + z * z));
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
        l.setYaw((float) Math.toDegrees(yaw < GeometryUtil.PId2 ? yaw + GeometryUtil.PIx2 - GeometryUtil.PId2 : yaw - GeometryUtil.PId2));
        l.setPitch((float) Math.toDegrees(-pitch));
    }


    public DirectionalVector getOpposite() {
        return new DirectionalVector(
                -x,
                -y,
                -z,
                (yaw > 0 ? (yaw - GeometryUtil.PI) : (yaw + GeometryUtil.PI)),
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
                (pitch > 0 ? yaw : (yaw > 0 ? (yaw - GeometryUtil.PI) : (yaw + GeometryUtil.PI))),
                (pitch > 0 ? (pitch - GeometryUtil.PId2) : (-GeometryUtil.PId2 - pitch))
        );
    }

    /**
     * If the current direction is the player face direction,
     * this method return the direction of the top of the head.
     */
    public DirectionalVector getTopDirection() {
        return new DirectionalVector(
                (pitch < 0 ? yaw : (yaw > 0 ? (yaw - GeometryUtil.PI) : (yaw + GeometryUtil.PI))),
                (pitch < 0 ? (pitch + GeometryUtil.PId2) : (GeometryUtil.PId2 - pitch))
        );
    }


    /**
     * If the current direction is the player face direction,
     * this method return the direction of the left of the head.
     */
    public DirectionalVector getLeftDirection() {
        return new DirectionalVector(
                yaw > -GeometryUtil.PId2 ? (yaw - GeometryUtil.PId2) : (yaw - GeometryUtil.PId2 + GeometryUtil.PIx2),
                0
        );
    }


    /**
     * If the current direction is the player face direction,
     * this method return the direction of the right of the head.
     */
    public DirectionalVector getRightDirection() {
        return new DirectionalVector(
                yaw < GeometryUtil.PId2 ? (yaw + GeometryUtil.PId2) : (yaw + GeometryUtil.PId2 - GeometryUtil.PIx2),
                0
        );
    }


}
