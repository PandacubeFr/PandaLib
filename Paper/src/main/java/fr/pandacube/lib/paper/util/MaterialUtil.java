package fr.pandacube.lib.paper.util;

import org.bukkit.Material;
import org.bukkit.block.data.type.Sign;
import org.bukkit.block.data.type.WallSign;

public class MaterialUtil {
	
	public static boolean isSign(Material m) {
		return WallSign.class.equals(m.data) || Sign.class.equals(m.data);
	}

}
