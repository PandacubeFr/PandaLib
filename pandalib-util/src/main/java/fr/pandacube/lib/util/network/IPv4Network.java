package fr.pandacube.lib.util.network;

/**
 * Represents an IPv4 network address (an IP address and a network mask).
 */
public class IPv4Network {

    private final IPv4Address networkAddress;
    private final int bits;

    /**
     * Creates a new IPv4 network address.
     * @param networkAddress the address.
     * @param bits the bits count of the network part of the address. Must be between 0 and 32 inclusive.
     */
    public IPv4Network(IPv4Address networkAddress, int bits) {
        if (networkAddress == null)
            throw new IllegalArgumentException("networkAddress cannot be null.");
        if (bits < 0 || bits > 32)
            throw new IllegalArgumentException("invalid network bits count: " + bits);
        this.networkAddress = networkAddress.asNetworkAddress(bits);
        this.bits = bits;
    }


    /**
     * Telles if the provided {@link IPv4Address} is part of this network.
     * @param address the provided IPv4 address.
     * @return true if the provided {@link IPv4Address} is part of this network, false otherwise.
     */
    public boolean isInNetwork(IPv4Address address) {
        return address.asNetworkAddress(bits).equals(networkAddress);
    }
}
