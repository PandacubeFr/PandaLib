package fr.pandacube.lib.util;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * This class provides methods for manipulating enums.
 */
public class EnumUtil {

	/**
	 * List all enum constants which are in the specified enum class.
	 *
	 * @param enumType the enum class.
	 * @param separator a string which will be used as a separator
	 * @return a string representation of the enum class.
	 * @param <T> the type of the enum.
	 */
	public static <T extends Enum<T>> String enumList(Class<T> enumType, String separator) {
		return Arrays.stream(enumType.getEnumConstants())
				.map(Enum::name)
				.collect(Collectors.joining(separator));
	}

	/**
	 * List all enum constants which are in the specified enum class.
	 * It is equivalent to call {@link #enumList(Class, String)} with the second parameter <code>", "</code>.
	 *
	 * @param enumType the enum class.
	 * @return a string representation of the enum class.
	 * @param <T> the type of the enum.
	 */
	public static <T extends Enum<T>> String enumList(Class<T> enumType) {
		return enumList(enumType, ", ");
	}

	/**
	 * Search for a specific enum entry in the provided enum type, using the case-insensitive search string.
	 *
	 * @param enumType the class of the enum in which to search
	 * @param search the case-insensitive name of the enum value to return.
	 * @return the element found in the enum, or null if not found.
	 * @param <T> the type of the enum.
	 */
	public static <T extends Enum<T>> T searchEnum(Class<T> enumType, String search) {
		for (T el : enumType.getEnumConstants()) {
			if (el.name().equalsIgnoreCase(search)) {
				return el;
			}
		}
		return null;
	}

	/**
	 * Search for a specific enum entry in the provided enum type, using the case-insensitive search string.
	 * unlike {@link #searchEnum(Class, String)}, this method does not statically check the enum type, in case it is not
	 * known at compilation time.
	 * <p>
	 * For a statically checked enum type, uses {@link #searchEnum(Class, String)} instead.
	 *
	 * @param enumType the class of the enum in which to search
	 * @param search the case-insensitive name of the enum value to return.
	 * @return the element found in the enum, or null if not found or if the provided type is not an enum.
	 */
	public static Enum<?> searchUncheckedEnum(Class<?> enumType, String search) {
		if (!enumType.isEnum())
			return null;
		for (Enum<?> el : (Enum<?>[]) enumType.getEnumConstants()) {
			if (el.name().equalsIgnoreCase(search)) {
				return el;
			}
		}
		return null;
	}

	/**
	 * Pick a random value from an enum type.
	 *
	 * @param enumType the class of the enum in which to pick the value from
	 * @return one of the enum value, or null if the provided enum is empty.
	 * @param <T> the type of the enum.
	 */
	public static <T extends Enum<T>> T randomValue(Class<T> enumType) {
		return RandomUtil.arrayElement(enumType.getEnumConstants());
	}

	private EnumUtil() {}

}
