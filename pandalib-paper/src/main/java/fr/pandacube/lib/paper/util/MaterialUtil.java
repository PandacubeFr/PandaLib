package fr.pandacube.lib.paper.util;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.HangingSign;
import org.bukkit.block.data.type.Sign;
import org.bukkit.block.data.type.WallSign;

import java.util.Set;

/**
 * Utility class around {@link Material}.
 */
public class MaterialUtil {

	private static final Set<Class<? extends BlockData>> signBlockDataTypes = Set.of(WallSign.class, Sign.class, HangingSign.class);

	/**
	 * Tells if the provided {@link Material} is a sign, a wall sign or a hanging sign.
	 * @param m the material.
	 * @return true if the material is a kind of sign.
	 */
	public static boolean isSign(Material m) {
		return signBlockDataTypes.contains(m.data);
	}

	private MaterialUtil() {}

}
