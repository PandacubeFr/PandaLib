package fr.pandacube.util.network.packet.bytebuffer;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ByteBuffer implements Cloneable {

	private java.nio.ByteBuffer buff;
	private Charset charset;

	public ByteBuffer(Charset c) {
		this(16, c);
	}

	public ByteBuffer(int initSize, Charset c) {
		buff = java.nio.ByteBuffer.allocate(initSize);
		charset = c;
	}

	public ByteBuffer(byte[] data, Charset c) {
		buff = java.nio.ByteBuffer.wrap(Arrays.copyOf(data, data.length));
		charset = c;
	}

	private void askForBufferExtension(int needed) {
		while (buff.remaining() < needed) {
			java.nio.ByteBuffer newBuff = java.nio.ByteBuffer.wrap(Arrays.copyOf(buff.array(), buff.array().length * 2));
			newBuff.position(buff.position());
			buff = newBuff;
		}
	}

	@Override
	public ByteBuffer clone() {
		return new ByteBuffer(Arrays.copyOf(buff.array(), buff.array().length), charset);
	}

	/**
	 * @see java.nio.ByteBuffer#get()
	 */
	public byte getByte() {
		return buff.get();
	}

	/**
	 * @see java.nio.ByteBuffer#get(byte[])
	 */
	public byte[] getByteArray(byte[] b) {
		buff.get(b);
		return b;
	}
	
	/**
	 * Return the next byte array wich is preceded with his size as integer,
	 * or null if the founded size is negative.
	 * @return
	 */
	public byte[] getSizedByteArray() {
		int size = getInt();
		if (size < 0) return null;
		return getByteArray(new byte[size]);
	}

	/**
	 * @see java.nio.ByteBuffer#getChar()
	 */
	public char getChar() {
		return buff.getChar();
	}

	/**
	 * @see java.nio.ByteBuffer#getShort()
	 */
	public short getShort() {
		return buff.getShort();
	}

	/**
	 * @see java.nio.ByteBuffer#getInt()
	 */
	public int getInt() {
		return buff.getInt();
	}

	/**
	 * @see java.nio.ByteBuffer#getLong()
	 */
	public long getLong() {
		return buff.getLong();
	}

	/**
	 * @see java.nio.ByteBuffer#getFloat()
	 */
	public float getFloat() {
		return buff.getFloat();
	}

	/**
	 * @see java.nio.ByteBuffer#getDouble()
	 */
	public double getDouble() {
		return buff.getDouble();
	}

	/**
	 * @see java.nio.ByteBuffer#put(byte)
	 */
	public ByteBuffer putByte(byte b) {
		askForBufferExtension(Byte.BYTES);
		buff.put(b);
		return this;
	}

	/**
	 * @see java.nio.ByteBuffer#put(byte[])
	 */
	public ByteBuffer putByteArray(byte[] b) {
		askForBufferExtension(b.length * Byte.BYTES);
		buff.put(b);
		return this;
	}
	
	public ByteBuffer putSizedByteArray(byte[] b) {
		if (b == null) {
			return putInt(-1);
		}
		putInt(b.length);
		return putByteArray(b);
	}

	/**
	 * @see java.nio.ByteBuffer#putChar(char)
	 */
	public ByteBuffer putChar(char value) {
		askForBufferExtension(Character.BYTES);
		buff.putChar(value);
		return this;
	}

	/**
	 * @see java.nio.ByteBuffer#putShort(short)
	 */
	public ByteBuffer putShort(short value) {
		askForBufferExtension(Short.BYTES);
		buff.putShort(value);
		return this;
	}

	/**
	 * @see java.nio.ByteBuffer#putInt(int)
	 */
	public ByteBuffer putInt(int value) {
		askForBufferExtension(Integer.BYTES);
		buff.putInt(value);
		return this;
	}

	/**
	 * @see java.nio.ByteBuffer#putLong(long)
	 */
	public ByteBuffer putLong(long value) {
		askForBufferExtension(Long.BYTES);
		buff.putLong(value);
		return this;
	}

	/**
	 * @see java.nio.ByteBuffer#putFloat(float)
	 */
	public ByteBuffer putFloat(float value) {
		askForBufferExtension(Float.BYTES);
		buff.putFloat(value);
		return this;
	}

	/**
	 * @see java.nio.ByteBuffer#putDouble(double)
	 */
	public ByteBuffer putDouble(double value) {
		askForBufferExtension(Double.BYTES);
		buff.putDouble(value);
		return this;
	}

	/**
	 * @see java.nio.ByteBuffer#position()
	 */
	public int getPosition() {
		return buff.position();
	}

	/**
	 * @see java.nio.ByteBuffer#position(int)
	 */
	public void setPosition(int p) {
		buff.position(p);
	}

	/**
	 * @see java.nio.ByteBuffer#capacity()
	 */
	public int capacity() {
		return buff.capacity();
	}

	/**
	 * 
	 * @param s null String are supported
	 * @return
	 */
	public ByteBuffer putString(String s) {
		if (s == null) {
			return putInt(-1);
		}
		return putSizedByteArray(s.getBytes(charset));
	}

	/**
	 * returned string can be null
	 * @return
	 */
	public String getString() {
		byte[] binaryString = getSizedByteArray();
		return (binaryString == null) ? null : new String(binaryString, charset);
	}

	/**
	 * The objet will be serialized and the data put in the current buffer
	 *
	 * @param obj the object to serialize. Can't be null.
	 * @return the current buffer
	 */
	public ByteBuffer putObject(ByteSerializable obj) {
		obj.serializeToByteBuffer(this);
		return this;
	}

	/**
	 * Ask to object passed as argument to deserialize data in buffer and fill
	 * the object content. ByteSerializable object are never null.
	 *
	 * @param <T>
	 * @param clazz the class wich will be instanciated with his no-argument Constructor
	 * 	before filled by using {@link ByteSerializable#deserializeFromByteBuffer(ByteBuffer)}
	 * @return obj a reference to the filled object
	 */
	public <T extends ByteSerializable> T getObject(Class<T> clazz) {
		try {
			T obj = clazz.newInstance();
			obj.deserializeFromByteBuffer(this);
			return obj;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("A ByteSerializable must have a no-argument Constructor", e);
		}
	}

	/**
	 * 
	 * @param list The list itself can be null, but not the values.
	 * @return
	 */
	public ByteBuffer putListObject(List<ByteSerializable> list) {
		if (list.stream().anyMatch(e -> e == null))
			throw new IllegalArgumentException("List of object can't contains any null value");
		putInt(list.size());
		for (ByteSerializable obj : list)
			putObject(obj);
		return this;
	}

	/**
	 * 
	 * @param list The list can be null, and any String can be null too.
	 * @return
	 */
	public ByteBuffer putListOfString(List<String> list) {
		if (list == null) {
			return putInt(-1);
		}
		putInt(list.size());
		for (String str : list)
			putString(str);
		return this;
	}

	/**
	 * 
	 * @param clazz
	 * @return Can be null. If not, there is no null element inside.
	 */
	public <T extends ByteSerializable> List<T> getListObject(Class<T> clazz) {
		int size = getInt();
		if (size < 0)
			return null;
		List<T> list = new ArrayList<>();
		for (int i = 0; i < size; i++)
			list.add(getObject(clazz));
		return list;
	}

	/**
	 * @return a List of String. The list can be null, and any element can be null too.
	 */
	public <T extends ByteSerializable> List<String> getListOfString() {
		int size = getInt();
		if (size < 0)
			return null;
		List<String> list = new ArrayList<>();
		for (int i = 0; i < size; i++)
			list.add(getString());
		return list;
	}

	/**
	 * @see java.nio.ByteBuffer#array()
	 */
	public byte[] array() {
		return buff.array();
	}

}
