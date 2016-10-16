package fr.pandacube.java.util.measurement;

import java.text.DecimalFormat;
import java.util.Arrays;

public class DistanceUtil {

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
		for (int i = 0; i < precision; i++)
			precisionFormat += "0";
		DecimalFormat df = new DecimalFormat(precisionFormat);

		double dist = meterDist / choosenUnit.multiplicator;

		return df.format(dist) + choosenUnit.unitStr;
	}

	public static String distanceToString(double meterDist, int precision) {
		return distanceToString(meterDist, precision, DistanceUnit.M, DistanceUnit.KM);
	}

	public enum DistanceUnit {
		NM(0.000000001, "nm"), µM(0.000001, "µm"), MM(0.001, "mm"), CM(0.01, "cm"), M(1, "m"), KM(1000, "km");

		private final double multiplicator;
		private final String unitStr;

		private DistanceUnit(double mult, String s) {
			multiplicator = mult;
			unitStr = s;
		}
	}

}
