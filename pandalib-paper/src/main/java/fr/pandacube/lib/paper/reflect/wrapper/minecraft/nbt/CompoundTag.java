package fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectConstructor;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class CompoundTag extends ReflectWrapper implements Tag {
	public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.nbt.CompoundTag"));
	public static final ReflectConstructor<?> CONSTRUCTOR = wrapEx(() -> REFLECT.constructor());
	private static final ReflectMethod<?> putBoolean = wrapEx(() -> REFLECT.method("putBoolean", String.class, boolean.class));
	private static final ReflectMethod<?> putByte = wrapEx(() -> REFLECT.method("putByte", String.class, byte.class));
	private static final ReflectMethod<?> putByteArray = wrapEx(() -> REFLECT.method("putByteArray", String.class, byte[].class));
	private static final ReflectMethod<?> putByteArray_List = wrapEx(() -> REFLECT.method("putByteArray", String.class, List.class));
	private static final ReflectMethod<?> putDouble = wrapEx(() -> REFLECT.method("putDouble", String.class, double.class));
	private static final ReflectMethod<?> putFloat = wrapEx(() -> REFLECT.method("putFloat", String.class, float.class));
	private static final ReflectMethod<?> putInt = wrapEx(() -> REFLECT.method("putInt", String.class, int.class));
	private static final ReflectMethod<?> putIntArray = wrapEx(() -> REFLECT.method("putIntArray", String.class, int[].class));
	private static final ReflectMethod<?> putIntArray_List = wrapEx(() -> REFLECT.method("putIntArray", String.class, List.class));
	private static final ReflectMethod<?> putString = wrapEx(() -> REFLECT.method("putString", String.class, String.class));
	private static final ReflectMethod<?> putUUID = wrapEx(() -> REFLECT.method("putUUID", String.class, UUID.class));
	private static final ReflectMethod<?> putLong = wrapEx(() -> REFLECT.method("putLong", String.class, long.class));
	private static final ReflectMethod<?> putLongArray = wrapEx(() -> REFLECT.method("putLongArray", String.class, long[].class));
	private static final ReflectMethod<?> putLongArray_List = wrapEx(() -> REFLECT.method("putLongArray", String.class, List.class));
	private static final ReflectMethod<?> putShort = wrapEx(() -> REFLECT.method("putShort", String.class, short.class));
	private static final ReflectMethod<?> put = wrapEx(() -> REFLECT.method("put", String.class, Tag.REFLECT.get()));

	private static final ReflectMethod<?> getTagType = wrapEx(() -> REFLECT.method("getTagType", String.class));
	private static final ReflectMethod<?> getByte = wrapEx(() -> REFLECT.method("getByte", String.class));
	private static final ReflectMethod<?> getShort = wrapEx(() -> REFLECT.method("getShort", String.class));
	private static final ReflectMethod<?> getInt = wrapEx(() -> REFLECT.method("getInt", String.class));
	private static final ReflectMethod<?> getLong = wrapEx(() -> REFLECT.method("getLong", String.class));
	private static final ReflectMethod<?> getFloat = wrapEx(() -> REFLECT.method("getFloat", String.class));
	private static final ReflectMethod<?> getDouble = wrapEx(() -> REFLECT.method("getDouble", String.class));
	private static final ReflectMethod<?> getString = wrapEx(() -> REFLECT.method("getString", String.class));
	private static final ReflectMethod<?> getByteArray = wrapEx(() -> REFLECT.method("getByteArray", String.class));
	private static final ReflectMethod<?> getIntArray = wrapEx(() -> REFLECT.method("getIntArray", String.class));
	private static final ReflectMethod<?> getLongArray = wrapEx(() -> REFLECT.method("getLongArray", String.class));
	private static final ReflectMethod<?> getCompound = wrapEx(() -> REFLECT.method("getCompound", String.class));
	private static final ReflectMethod<?> getBoolean = wrapEx(() -> REFLECT.method("getBoolean", String.class));
	private static final ReflectMethod<?> getList = wrapEx(() -> REFLECT.method("getList", String.class, int.class));

	private static final ReflectMethod<?> get = wrapEx(() -> REFLECT.method("get", String.class));
	private static final ReflectMethod<?> getAllKeys = wrapEx(() -> REFLECT.method("getAllKeys"));
	private static final ReflectMethod<?> entrySet = wrapEx(() -> REFLECT.method("entrySet"));
	private static final ReflectMethod<?> size = wrapEx(() -> REFLECT.method("size"));
	private static final ReflectMethod<?> contains = wrapEx(() -> REFLECT.method("contains", String.class));
	private static final ReflectMethod<?> containsStringInt = wrapEx(() -> REFLECT.method("contains", String.class, int.class));

	public CompoundTag() {
		this(wrapReflectEx(() -> CONSTRUCTOR.instantiate()));
	}

	protected CompoundTag(Object nms) {
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
	public void putByteArray(String key, List<Byte> value) {
		wrapReflectEx(() -> putByteArray_List.invoke(__getRuntimeInstance(), key, value));
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
	public void putIntArray(String key, List<Integer> value) {
		wrapReflectEx(() -> putIntArray_List.invoke(__getRuntimeInstance(), key, value));
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
	public void putLongArray(String key, List<Long> value) {
		wrapReflectEx(() -> putLongArray_List.invoke(__getRuntimeInstance(), key, value));
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
	public ListTag getList(String key, int type) {
		return wrap(wrapReflectEx(() -> getList.invoke(__getRuntimeInstance(), key, type)), ListTag.class);
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
	public Set<Map.Entry<String, ?>> entrySet() {
		// we cannot easily wrap every value of the map without being able to synchronize the returned map with the wrapped map
		return (Set<Map.Entry<String, ?>>) wrapReflectEx(() -> entrySet.invoke(__getRuntimeInstance()));
	}
	public int size() {
		return (int) wrapReflectEx(() -> size.invoke(__getRuntimeInstance()));
	}
	public boolean contains(String key) {
		return (boolean) wrapReflectEx(() -> contains.invoke(__getRuntimeInstance(), key));
	}
	public boolean contains(String key, int type) {
		return (boolean) wrapReflectEx(() -> containsStringInt.invoke(__getRuntimeInstance(), key, type));
	}

}
