package fr.pandacube.lib.core.net;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ByteBuffer implements Cloneable {

	public static final Charset NETWORK_CHARSET = Charset.forName("UTF-8");

	private java.nio.ByteBuffer buff;

	public ByteBuffer() {
		this(16);
	}

	public ByteBuffer(int initSize) {
		buff = java.nio.ByteBuffer.allocate(initSize);
	}

	/**
	 * Create a ByteBuffer that is initially <b>backed</b> by the provided byte array.
	 * The position of this buffer will be 0.
	 * If this ByteBuffer needs a biffer array, the provided array is replaced by a new one,
	 * making the provided array not related to this ByteBuffer anymore.
	 * @param data array of byte that serve as a backend for this ByteBuffer.
	 */
	public ByteBuffer(byte[] data) {
		buff = java.nio.ByteBuffer.wrap(data);
	}

	private void askForBufferExtension(int needed) {
		while (buff.remaining() < needed) {
			java.nio.ByteBuffer newBuff = java.nio.ByteBuffer.wrap(Arrays.copyOf(buff.array(), buff.array().length * 2));
			newBuff.position(buff.position());
			buff = newBuff;
		}
	}

	/**
	 * This clone method also clone the underlying array.
	 */
	@Override
	public ByteBuffer clone() {
		return new ByteBuffer(Arrays.copyOf(buff.array(), buff.array().length));
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
		return putSizedByteArray(s.getBytes(NETWORK_CHARSET));
	}

	/**
	 * returned string can be null
	 * @return
	 */
	public String getString() {
		byte[] binaryString = getSizedByteArray();
		return (binaryString == null) ? null : new String(binaryString, NETWORK_CHARSET);
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
	 * @return a List of String. The list can be null, and any element can be null too.
	 */
	public List<String> getListOfString() {
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
