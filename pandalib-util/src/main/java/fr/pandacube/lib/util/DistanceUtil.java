package fr.pandacube.lib.util;

import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * This class contains various methods to manipulate and display distances.
 */
public class DistanceUtil {

	/**
	 * Generate a string representation of the provided distance, with a specific decimal precision and one of the
	 * specified metric prefix
	 * @param meterDist the distance to display, in meter
	 * @param precision the number of digit to display after the decimal separator
	 * @param desiredUnits the prefered unit prefix to use for convertion.
	 * @return a string representation of the provided distance
	 */
	public static String distanceToString(double meterDist, int precision, DistanceUnit... desiredUnits) {

		Arrays.sort(desiredUnits);

		DistanceUnit choosenUnit = desiredUnits[0]; // la plus petite unitée
		for (DistanceUnit unit : desiredUnits) {
			if (meterDist / unit.multiplicator < 1) continue;
			choosenUnit = unit;
		}

		if (choosenUnit != desiredUnits[0] && precision <= 2) precision = 2;

		String precisionFormat = "##0";
		if (precision > 0) precisionFormat += ".";
		precisionFormat += "0".repeat(precision);

		DecimalFormat df = new DecimalFormat(precisionFormat);

		double dist = meterDist / choosenUnit.multiplicator;

		return df.format(dist) + choosenUnit.unitStr;
	}

	/**
	 *
	 * Generate a string representation of the provided distance, with a specific decimal precision, and using either
	 * {@code km} or {@code m} as a unit.
	 * <p>
	 * Calling this method is equivalent to {@code distanceToString(meterDist, precision, DistanceUnit.M, DistanceUnit.KM)}.
	 * @param meterDist the distance to display, in meter
	 * @param precision the number of digit to display after the decimal separator
	 * @return a string representation of the provided distance
	 */
	public static String distanceToString(double meterDist, int precision) {
		return distanceToString(meterDist, precision, DistanceUnit.M, DistanceUnit.KM);
	}

	/**
	 * Enumeration of comonly used distance metric unit
	 */
	public enum DistanceUnit {

		/**
		 * Nanometer unit. One billionth of a meter. 10<sup>-9</sup> = 0.000000001m.
		 */
		NM(0.000000001, "nm"),

		/**
		 * Micrometer unit. One millionth of a meter. 10<sup>-6</sup> = 0.000001m.
		 */
		UM(0.000001, "µm"),

		/**
		 * Millimeter unit. One thousandth of a meter. 10<sup>-3</sup> = 0.001m.
		 */
		MM(0.001, "mm"),

		/**
		 * Centimeter unit. One hundredth of a meter. 10<sup>-2</sup> = 0.01m
		 */
		CM(0.01, "cm"),

		/**
		 * Meter unit. One meter. 10<sup>0</sup> = 1m.
		 */
		M(1, "m"),

		/**
		 * Kilometer unit. One thousand meter. 10<sup>3</sup> = 1000m.
		 */
		KM(1000, "km");

		/**
		 * The value of this unit in meter.
		 */
		public final double multiplicator;

		/**
		 * String representation of the unit symbol.
		 */
		public final String unitStr;

		DistanceUnit(double mult, String s) {
			multiplicator = mult;
			unitStr = s;
		}
	}

}
