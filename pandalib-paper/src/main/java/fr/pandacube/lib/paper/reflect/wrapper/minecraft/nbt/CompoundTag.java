package fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectConstructor;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class CompoundTag extends ReflectWrapper implements Tag {
	public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.nbt.CompoundTag"));
	public static final ReflectConstructor<?> CONSTRUCTOR = wrapEx(() -> REFLECT.constructor());
	private static final ReflectMethod<?> putBoolean = wrapEx(() -> REFLECT.method("putBoolean", String.class, boolean.class));
	private static final ReflectMethod<?> putByte = wrapEx(() -> REFLECT.method("putByte", String.class, byte.class));
	private static final ReflectMethod<?> putByteArray = wrapEx(() -> REFLECT.method("putByteArray", String.class, byte[].class));
	private static final ReflectMethod<?> putDouble = wrapEx(() -> REFLECT.method("putDouble", String.class, double.class));
	private static final ReflectMethod<?> putFloat = wrapEx(() -> REFLECT.method("putFloat", String.class, float.class));
	private static final ReflectMethod<?> putInt = wrapEx(() -> REFLECT.method("putInt", String.class, int.class));
	private static final ReflectMethod<?> putIntArray = wrapEx(() -> REFLECT.method("putIntArray", String.class, int[].class));
	private static final ReflectMethod<?> putString = wrapEx(() -> REFLECT.method("putString", String.class, String.class));
	private static final ReflectMethod<?> putLong = wrapEx(() -> REFLECT.method("putLong", String.class, long.class));
	private static final ReflectMethod<?> putLongArray = wrapEx(() -> REFLECT.method("putLongArray", String.class, long[].class));
	private static final ReflectMethod<?> putShort = wrapEx(() -> REFLECT.method("putShort", String.class, short.class));
	private static final ReflectMethod<?> put = wrapEx(() -> REFLECT.method("put", String.class, Tag.REFLECT.get()));

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
	private static final ReflectMethod<?> getList = wrapEx(() -> REFLECT.method("getList", String.class));

	private static final ReflectMethod<?> get = wrapEx(() -> REFLECT.method("get", String.class));
	private static final ReflectMethod<?> keySet = wrapEx(() -> REFLECT.method("keySet"));
	private static final ReflectMethod<?> entrySet = wrapEx(() -> REFLECT.method("entrySet"));
	private static final ReflectMethod<?> size = wrapEx(() -> REFLECT.method("size"));
	private static final ReflectMethod<?> contains = wrapEx(() -> REFLECT.method("contains", String.class));

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
	@SuppressWarnings("unchecked")
	public Optional<Byte> getByte(String key) {
		return (Optional<Byte>) wrapReflectEx(() -> getByte.invoke(__getRuntimeInstance(), key));
	}
	@SuppressWarnings("unchecked")
	public Optional<Short> getShort(String key) {
		return (Optional<Short>) wrapReflectEx(() -> getShort.invoke(__getRuntimeInstance(), key));
	}
	@SuppressWarnings("unchecked")
	public Optional<Integer> getInt(String key) {
		return (Optional<Integer>) wrapReflectEx(() -> getInt.invoke(__getRuntimeInstance(), key));
	}
	@SuppressWarnings("unchecked")
	public Optional<Long> getLong(String key) {
		return (Optional<Long>) wrapReflectEx(() -> getLong.invoke(__getRuntimeInstance(), key));
	}
	@SuppressWarnings("unchecked")
	public Optional<Float> getFloat(String key) {
		return (Optional<Float>) wrapReflectEx(() -> getFloat.invoke(__getRuntimeInstance(), key));
	}
	@SuppressWarnings("unchecked")
	public Optional<Double> getDouble(String key) {
		return (Optional<Double>) wrapReflectEx(() -> getDouble.invoke(__getRuntimeInstance(), key));
	}
	@SuppressWarnings("unchecked")
	public Optional<String> getString(String key) {
		return (Optional<String>) wrapReflectEx(() -> getString.invoke(__getRuntimeInstance(), key));
	}
	@SuppressWarnings("unchecked")
	public Optional<byte[]> getByteArray(String key) {
		return (Optional<byte[]>) wrapReflectEx(() -> getByteArray.invoke(__getRuntimeInstance(), key));
	}
	@SuppressWarnings("unchecked")
	public Optional<int[]> getIntArray(String key) {
		return (Optional<int[]>) wrapReflectEx(() -> getIntArray.invoke(__getRuntimeInstance(), key));
	}
	@SuppressWarnings("unchecked")
	public Optional<long[]> getLongArray(String key) {
		return (Optional<long[]>) wrapReflectEx(() -> getLongArray.invoke(__getRuntimeInstance(), key));
	}
	public Optional<CompoundTag> getCompound(String key) {
		return ((Optional<?>) wrapReflectEx(() -> getCompound.invoke(__getRuntimeInstance(), key)))
				.map(u -> wrap(u, CompoundTag.class));
	}
	@SuppressWarnings("unchecked")
	public Optional<Boolean> getBoolean(String key) {
		return (Optional<Boolean>) wrapReflectEx(() -> getBoolean.invoke(__getRuntimeInstance(), key));
	}
	public Optional<ListTag> getList(String key) {
		return ((Optional<?>) wrapReflectEx(() -> getList.invoke(__getRuntimeInstance(), key)))
				.map(u -> wrap(u, ListTag.class));
	}
	public Tag get(String key) {
		return wrap(wrapReflectEx(() -> get.invoke(__getRuntimeInstance(), key)), Tag.class);
	}
	@SuppressWarnings("unchecked")
	public Set<String> keySet() {
		return (Set<String>) wrapReflectEx(() -> keySet.invoke(__getRuntimeInstance()));
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

}
