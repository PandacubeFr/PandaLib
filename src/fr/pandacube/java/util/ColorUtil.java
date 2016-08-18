package fr.pandacube.java.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ColorUtil {
	
	/*
	 * Rainbow
	 */
	private static List<Color> rainbowColors = new ArrayList<>();
	
	/**
	 * Get a rainbow color
	 * @param variation from 0 (include) to 1 (exclude).
	 * 0 is red, 1/6 is yellow, 2/6 is green, 3/6 is cyan, 4/6 is blue, 5/6 is magenta
	 * @return
	 */
	public static Color getRainbowColor(double variation) {
		synchronized (rainbowColors) {
			if (rainbowColors.isEmpty()) {
				for (int g=0; g<255; g++) rainbowColors.add(new Color(255, g, 0));
				for (int r=255; r>0; r--) rainbowColors.add(new Color(r, 255, 0));
				for (int b=0; b<255; b++) rainbowColors.add(new Color(0, 255, b));
				for (int g=255; g>0; g--) rainbowColors.add(new Color(0, g, 255));
				for (int r=0; r<255; r++) rainbowColors.add(new Color(r, 0, 255));
				for (int b=255; b>0; b--) rainbowColors.add(new Color(255, 0, b));
			}
		}
		
		while (variation >= 1) variation -= 1d;
		while (variation < 0)  variation += 1d;
		
		return rainbowColors.get((int)(variation * rainbowColors.size()));
	}
	
	
	
	
	
	
	
	
	
	
	

}
