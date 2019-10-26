package fr.pandacube.util.network.server;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import fr.pandacube.util.network.server.TCPServer.TCPServerClientConnection;

public class BandwidthCalculation {

	private List<PacketStat> packetHistory = new LinkedList<>();

	public synchronized void addPacket(TCPServerClientConnection co, boolean in, long size) {
		packetHistory.add(new PacketStat(co, in, size));
	}

	/**
	 * Get the instant bandwith in byte/s
	 *
	 * @param input true if getting input bw, false if getting output, null if
	 *        getting input + output
	 * @param co
	 * @return
	 */
	public synchronized long getBandWidth(Boolean input, TCPServerClientConnection co) {
		long currentTime = System.currentTimeMillis();
		Iterator<PacketStat> it = packetHistory.iterator();
		long sum = 0;
		while (it.hasNext()) {
			PacketStat el = it.next();
			if (el.time < currentTime - 1000) {
				it.remove();
				continue;
			}
			if (input != null && el.input != input.booleanValue()) continue;
			if (co != null && !co.equals(el.connection)) continue;
			sum += el.packetSize;
		}
		return sum;
	}

	private class PacketStat {
		public final long time;
		public final long packetSize;
		public final boolean input;
		public final TCPServerClientConnection connection;

		public PacketStat(TCPServerClientConnection co, boolean input, long size) {
			time = System.currentTimeMillis();
			packetSize = size;
			this.input = input;
			connection = co;
		}
	}

}
