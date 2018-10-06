package fr.pandacube.java.external_tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {
	
	
	static class Row {
		int rowid;
		String date, user, uuid;
		int wid, x, y, z, type, amount, action;
		boolean rolled_back;
	}
	
	
	
	static class Chest {
		final int wid, x, y, z, hashCode;
		List<Row> sources = new ArrayList<>();
		int actualCount = 0;
		boolean alreadyRolledBack = false;
		public Chest(int w, int wx, int wy, int wz) {
			wid = w;
			x = wx;
			y = wy;
			z = wz;
			hashCode = Objects.hash(wid, x, y, z);
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == null || !(o instanceof Chest))
				return false;
			Chest c = (Chest) o;
			return wid == c.wid && x == c.x && y == c.y && z == c.z;
		}
		
		@Override
		public int hashCode() {
			return hashCode;
		}
		
		@Override
		public String toString() {
			return "world=" + wid + " - /tppos " + x + " " + y + " " + z + " - rolledBack=" + alreadyRolledBack + " - amount=" + actualCount;
		}
		
		public String toCSVString() {
			return wid + "," + x + "," + y + "," + z + "," + alreadyRolledBack + "," + actualCount;
		}
	}
	
	
	
	
	public static void main(String args[]) throws Exception {
		
		/*CSVParser parser = new CSVParser(new FileReader("co_survie_container.csv"), CSVFormat.DEFAULT);
		
		
		List<Row> rows = StreamSupport.stream(parser.spliterator(), false).map(r -> {
			Row ret = new Row();
			ret.rowid = Integer.parseInt(r.get(0));
			ret.date = r.get(1);
			ret.user = r.get(2);
			ret.uuid = r.get(3);
			ret.wid = Integer.parseInt(r.get(4));
			ret.x = Integer.parseInt(r.get(5));
			ret.y = Integer.parseInt(r.get(6));
			ret.z = Integer.parseInt(r.get(7));
			ret.type = Integer.parseInt(r.get(8));
			ret.amount = Integer.parseInt(r.get(9));
			ret.action = Integer.parseInt(r.get(10));
			ret.rolled_back = r.get(11).equals("1");
			return ret;
		}).collect(Collectors.toList());
		
		rows.sort(Comparator.comparingInt(r -> r.rowid));
		
		List<Chest> chests = new ArrayList<>();
		
		
		
		for (Row row : rows) {
			Chest c = new Chest(row.wid, row.x, row.y, row.z);
			int idx = chests.indexOf(c);
			c = (idx >= 0) ? chests.get(idx) : c;
			if (idx < 0) {
				chests.add(c);
				idx = chests.size() - 1;
			}
			
			c.sources.add(row);
			
			if (row.rolled_back)
				c.alreadyRolledBack = true;
			
			if (!c.alreadyRolledBack) {
				c.actualCount += (row.action == 1) ? row.amount : -row.amount;
				if (c.actualCount == 0) {
					chests.remove(idx);
				}
			}
			
		}
		
		
		
		chests.sort(Comparator.comparingInt(c -> c.actualCount));
		
		chests.forEach(c -> System.out.println(c.toCSVString()));
		*/
		
	}
}