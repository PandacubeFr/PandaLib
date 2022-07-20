package fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt;

import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import fr.pandacube.lib.reflect.Reflect.ReflectMethod;
import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.NMSReflect.ClassMapping;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;

public class CompoundTag extends ReflectWrapper implements Tag {
	public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.nbt.CompoundTag"));
	private static final ReflectMethod<?> putBoolean = wrapEx(() -> MAPPING.mojMethod("putBoolean", String.class, boolean.class));
	private static final ReflectMethod<?> putByte = wrapEx(() -> MAPPING.mojMethod("putByte", String.class, byte.class));
	private static final ReflectMethod<?> putByteArray = wrapEx(() -> MAPPING.mojMethod("putByteArray", String.class, byte[].class));
	private static final ReflectMethod<?> putByteArray_List = wrapEx(() -> MAPPING.mojMethod("putByteArray", String.class, List.class));
	private static final ReflectMethod<?> putDouble = wrapEx(() -> MAPPING.mojMethod("putDouble", String.class, double.class));
	private static final ReflectMethod<?> putFloat = wrapEx(() -> MAPPING.mojMethod("putFloat", String.class, float.class));
	private static final ReflectMethod<?> putInt = wrapEx(() -> MAPPING.mojMethod("putInt", String.class, int.class));
	private static final ReflectMethod<?> putIntArray = wrapEx(() -> MAPPING.mojMethod("putIntArray", String.class, int[].class));
	private static final ReflectMethod<?> putIntArray_List = wrapEx(() -> MAPPING.mojMethod("putIntArray", String.class, List.class));
	private static final ReflectMethod<?> putString = wrapEx(() -> MAPPING.mojMethod("putString", String.class, String.class));
	private static final ReflectMethod<?> putUUID = wrapEx(() -> MAPPING.mojMethod("putUUID", String.class, UUID.class));
	private static final ReflectMethod<?> putLong = wrapEx(() -> MAPPING.mojMethod("putLong", String.class, long.class));
	private static final ReflectMethod<?> putLongArray = wrapEx(() -> MAPPING.mojMethod("putLongArray", String.class, long[].class));
	private static final ReflectMethod<?> putLongArray_List = wrapEx(() -> MAPPING.mojMethod("putLongArray", String.class, List.class));
	private static final ReflectMethod<?> putShort = wrapEx(() -> MAPPING.mojMethod("putShort", String.class, short.class));
	private static final ReflectMethod<?> put = wrapEx(() -> MAPPING.mojMethod("put", String.class, Tag.MAPPING));

	private static final ReflectMethod<?> getTagType = wrapEx(() -> MAPPING.mojMethod("getTagType", String.class));
	private static final ReflectMethod<?> getByte = wrapEx(() -> MAPPING.mojMethod("getByte", String.class));
	private static final ReflectMethod<?> getShort = wrapEx(() -> MAPPING.mojMethod("getShort", String.class));
	private static final ReflectMethod<?> getInt = wrapEx(() -> MAPPING.mojMethod("getInt", String.class));
	private static final ReflectMethod<?> getLong = wrapEx(() -> MAPPING.mojMethod("getLong", String.class));
	private static final ReflectMethod<?> getFloat = wrapEx(() -> MAPPING.mojMethod("getFloat", String.class));
	private static final ReflectMethod<?> getDouble = wrapEx(() -> MAPPING.mojMethod("getDouble", String.class));
	private static final ReflectMethod<?> getString = wrapEx(() -> MAPPING.mojMethod("getString", String.class));
	private static final ReflectMethod<?> getByteArray = wrapEx(() -> MAPPING.mojMethod("getByteArray", String.class));
	private static final ReflectMethod<?> getIntArray = wrapEx(() -> MAPPING.mojMethod("getIntArray", String.class));
	private static final ReflectMethod<?> getLongArray = wrapEx(() -> MAPPING.mojMethod("getLongArray", String.class));
	private static final ReflectMethod<?> getCompound = wrapEx(() -> MAPPING.mojMethod("getCompound", String.class));
	private static final ReflectMethod<?> getBoolean = wrapEx(() -> MAPPING.mojMethod("getBoolean", String.class));

	private static final ReflectMethod<?> get = wrapEx(() -> MAPPING.mojMethod("get", String.class));
	private static final ReflectMethod<?> getAllKeys = wrapEx(() -> MAPPING.mojMethod("getAllKeys"));
	private static final ReflectMethod<?> entries = wrapEx(() -> MAPPING.mojMethod("entries"));
	private static final ReflectMethod<?> size = wrapEx(() -> MAPPING.mojMethod("size"));
	private static final ReflectMethod<?> contains = wrapEx(() -> MAPPING.mojMethod("contains", String.class));

	public CompoundTag(Object nms) {
		super(nms);
	}
	
	public void putBoolean(String key, boolean value) {
		wrapReflectEx(() -> putBoolean.invoke(__getRuntimeInstance(), key, value));
	}
	public void putByte(String key, byte value) {
		wrapReflectEx(() -> putByte.invoke(__getRuntimeInstance(), key, value));
	}
	public void putByteArray(String key, byte[] value) {
		wrapReflectEx(() -> putByteArray.invoke(__getRuntimeInstance(), key, value));
	}
	public void putDouble(String key, double value) {
		wrapReflectEx(() -> putDouble.invoke(__getRuntimeInstance(), key, value));
	}
	public void putFloat(String key, float value) {
		wrapReflectEx(() -> putFloat.invoke(__getRuntimeInstance(), key, value));
	}
	public void putInt(String key, int value) {
		wrapReflectEx(() -> putInt.invoke(__getRuntimeInstance(), key, value));
	}
	public void putIntArray(String key, int[] value) {
		wrapReflectEx(() -> putIntArray.invoke(__getRuntimeInstance(), key, value));
	}
	public void putString(String key, String value) {
		wrapReflectEx(() -> putString.invoke(__getRuntimeInstance(), key, value));
	}
	public void putUUID(String key, UUID value) {
		wrapReflectEx(() -> putUUID.invoke(__getRuntimeInstance(), key, value));
	}
	public void putLong(String key, long value) {
		wrapReflectEx(() -> putLong.invoke(__getRuntimeInstance(), key, value));
	}
	public void putLongArray(String key, long[] value) {
		wrapReflectEx(() -> putLongArray.invoke(__getRuntimeInstance(), key, value));
	}
	public void putShort(String key, short value) {
		wrapReflectEx(() -> putShort.invoke(__getRuntimeInstance(), key, value));
	}
	public void put(String key, Tag value) {
		wrapReflectEx(() -> put.invoke(__getRuntimeInstance(), key, unwrap(value)));
	}
	public byte getTagType(String key) {
		return (byte) wrapReflectEx(() -> getTagType.invoke(__getRuntimeInstance(), key));
	}
	public byte getByte(String key) {
		return (byte) wrapReflectEx(() -> getByte.invoke(__getRuntimeInstance(), key));
	}
	public short getShort(String key) {
		return (short) wrapReflectEx(() -> getShort.invoke(__getRuntimeInstance(), key));
	}
	public int getInt(String key) {
		return (int) wrapReflectEx(() -> getInt.invoke(__getRuntimeInstance(), key));
	}
	public long getLong(String key) {
		return (long) wrapReflectEx(() -> getLong.invoke(__getRuntimeInstance(), key));
	}
	public float getFloat(String key) {
		return (float) wrapReflectEx(() -> getFloat.invoke(__getRuntimeInstance(), key));
	}
	public double getDouble(String key) {
		return (double) wrapReflectEx(() -> getDouble.invoke(__getRuntimeInstance(), key));
	}
	public String getString(String key) {
		return (String) wrapReflectEx(() -> getString.invoke(__getRuntimeInstance(), key));
	}
	public byte[] getByteArray(String key) {
		return (byte[]) wrapReflectEx(() -> getByteArray.invoke(__getRuntimeInstance(), key));
	}
	public int[] getIntArray(String key) {
		return (int[]) wrapReflectEx(() -> getIntArray.invoke(__getRuntimeInstance(), key));
	}
	public long[] getLongArray(String key) {
		return (long[]) wrapReflectEx(() -> getLongArray.invoke(__getRuntimeInstance(), key));
	}
	public CompoundTag getCompound(String key) {
		return wrap(wrapReflectEx(() -> getCompound.invoke(__getRuntimeInstance(), key)), CompoundTag.class);
	}
	public boolean getBoolean(String key) {
		return (boolean) wrapReflectEx(() -> getBoolean.invoke(__getRuntimeInstance(), key));
	}
	public Tag get(String key) {
		return wrap(wrapReflectEx(() -> get.invoke(__getRuntimeInstance(), key)), Tag.class);
	}
	@SuppressWarnings("unchecked")
	public Set<String> getAllKeys() {
		return (Set<String>) wrapReflectEx(() -> getAllKeys.invoke(__getRuntimeInstance()));
	}

	/**
	 * The values in the returned Map are not wrapped.
	 */
	@SuppressWarnings("unchecked")
	public Map<String, ?> entries() {
		// we cannot easily wrap every value of the map without being able to synch the returned map with the wrapped map
		return (Map<String, ?>) wrapReflectEx(() -> entries.invoke(__getRuntimeInstance()));
	}
	public int size() {
		return (int) wrapReflectEx(() -> size.invoke(__getRuntimeInstance()));
	}
	public boolean contains(String key) {
		return (boolean) wrapReflectEx(() -> contains.invoke(__getRuntimeInstance(), key));
	}

}
