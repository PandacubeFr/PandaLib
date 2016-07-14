package fr.pandacube.java.util.network.packet.bytebuffer;

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
		if (buff.remaining() >= needed) return;
		java.nio.ByteBuffer newBuff = java.nio.ByteBuffer.wrap(Arrays.copyOf(buff.array(), buff.array().length * 2));
		newBuff.position(buff.position());
		buff = newBuff;
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
	public byte[] getBytes(byte[] b) {
		buff.get(b);
		return b;
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
	public ByteBuffer putBytes(byte[] b) {
		askForBufferExtension(b.length * Byte.BYTES);
		buff.put(b);
		return this;
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

	public ByteBuffer putString(String s) {
		byte[] charBytes = s.getBytes(charset);
		putInt(charBytes.length);
		putBytes(charBytes);
		return this;
	}

	public String getString() {
		return new String(getBytes(new byte[getInt()]), charset);
	}

	/**
	 * The objet will be serialized and the data put in the current buffer
	 *
	 * @param obj the object to serialize
	 * @return the current buffer
	 */
	public ByteBuffer putObject(ByteSerializable obj) {
		obj.serializeToByteBuffer(this);
		return this;
	}

	/**
	 * Ask to object passed as argument to deserialize data in buffer and fill
	 * the object content
	 *
	 * @param <T>
	 * @param obj the objet to fill with his method
	 *        {@link ByteSerializable#deserializeFromByteBuffer(ByteBuffer)}
	 * @return obj a reference to the same object
	 */
	public <T extends ByteSerializable> T getObject(Class<T> clazz) {
		try {
			T obj = clazz.newInstance();
			obj.deserializeFromByteBuffer(this);
			return obj;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public ByteBuffer putListObject(List<ByteSerializable> list) {
		putInt(list.size());
		for (ByteSerializable obj : list)
			putObject(obj);
		return this;
	}

	public <T extends ByteSerializable> List<T> getListObject(Class<T> clazz) {
		List<T> list = new ArrayList<T>();
		int size = getInt();
		for (int i = 0; i < size; i++)
			list.add(getObject(clazz));
		return list;
	}

	/**
	 * @see java.nio.ByteBuffer#array()
	 */
	public byte[] array() {
		return buff.array();
	}

}
