package fr.pandacube.lib.util.network;

import java.net.Inet4Address;

/**
 * Represents an IPv4 address.
 */
public class IPv4Address {


    private final int address;

    /**
     * Creates an IPv4 from the binary content of the provided integer.
     * @param address the integer used for the 32 bits of the address.
     */
    public IPv4Address(int address) {
        this.address = address;
    }


    /**
     * Creates an IPv4 from the binary content of the provided bytes.
     * @param address the 4 bytes used for the address. The byte at index 0 contains the most significant bits of the
     *                address.
     */
    public IPv4Address(byte[] address) {
        this((checkIPv4ByteArray(address)[0] << 24)
                | (address[1] << 16)
                | (address[2] << 8)
                |  address[3]);
    }


    /**
     * Creates an IPv4 from the 4 provided integers.
     * @param b0 the most significant byte of the address.
     *           Only the 8 least significant bits of this integer will be used.
     * @param b1 the second most significant byte of the address.
     *           Only the 8 least significant bits of this integer will be used.
     * @param b2 the third most significant byte of the address.
     *           Only the 8 least significant bits of this integer will be used.
     * @param b3 the least significant byte of the address.
     *           Only the 8 least significant bits of this integer will be used.
     */
    public IPv4Address(int b0, int b1, int b2, int b3) {
        this(((b0 & 0xFF) << 24)
           | ((b1 & 0xFF) << 16)
           | ((b2 & 0xFF) << 8)
           | ((b3 & 0xFF)));
    }


    /**
     * Creates an IPv4 from the provided {@link java.net.Inet4Address}.
     * @param address the {@link java.net.Inet4Address}.
     */
    public IPv4Address(Inet4Address address) {
        this(address.getAddress());
    }


    private static byte[] checkIPv4ByteArray(byte[] address) {
        if (address == null || address.length != 4)
            throw new IllegalArgumentException("address must not be null and be of length 4.");
        return address;
    }


    /**
     * Gets the bytes of this IPv4 address.
     * <p>
     * The byte at index 0 contains the most significant bits of the address.
     * @return the bytes of this IPv4 address.
     */
    public byte[] getByteArray() {
        return new byte[] {
                (byte) (address >> 24 & 0xFF),
                (byte) (address >> 16 & 0xFF),
                (byte) (address >>  8 & 0xFF),
                (byte) (address       & 0xFF)
        };
    }


    /**
     * Gets the bytes of this IPv4 address represented as integers.
     * <p>
     * The byte at index 0 contains the most significant bits of the address.
     * @return the bytes of this IPv4 address represented as integers.
     */
    public int[] getIntArray() {
        return new int[] {
                address >> 24 & 0xFF,
                address >> 16 & 0xFF,
                address >>  8 & 0xFF,
                address       & 0xFF
        };
    }

    /**
     * Creates a new IPv4 address with only the network bits sets according to this IPv4 address.
     * @param networkBits the bits count of the network part of the returned address. Must be between 0 and 32 inclusive.
     * @return a new IPv4 address.
     */
    public IPv4Address asNetworkAddress(int networkBits) {
        if (networkBits < 0 || networkBits > 32)
            throw new IllegalArgumentException("invalid network bits count: " + networkBits);
        int shift = 32 - networkBits;
        return new IPv4Address(address >> shift << shift);
    }

    /**
     * Creates a new IPv4 address with only the host bits sets according to this IPv4 address.
     * @param networkBits the bits count of the network part of the returned address. Must be between 0 and 32 inclusive.
     * @return a new IPv4 address.
     */
    public IPv4Address asHostAddress(int networkBits) {
        if (networkBits < 0 || networkBits > 32)
            throw new IllegalArgumentException("invalid network bits count: " + networkBits);
        return new IPv4Address(address << networkBits >> networkBits);
    }


    @Override
    public String toString() {
        int[] Parts = getIntArray();
        return Parts[0] + "." + Parts[1] + "." + Parts[2] + "." + Parts[3];
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof IPv4Address other && address == other.address);
    }

    @Override
    public int hashCode() {
        return address;
    }
}
