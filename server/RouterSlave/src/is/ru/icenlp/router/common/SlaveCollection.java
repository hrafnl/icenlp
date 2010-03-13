package is.ru.icenlp.router.common;

import java.security.MessageDigest;
import java.util.HashMap;

public class SlaveCollection implements ISlaveCollection {
	private HashMap<String, ISlave> slaveCollection;

	private SlaveCollection() {
		this.slaveCollection = new HashMap<String, ISlave>();
		System.out.println("[SlaveCollection]: singleton object created.");
	}

	private static class SingletonHolder {
		private static final SlaveCollection INSTANCE = new SlaveCollection();
	}

	public static SlaveCollection getInstance() {
		return SingletonHolder.INSTANCE;
	}

	@Override
	public synchronized void addSlave(ISlave slave) {
		String md5 = this.getMD5(slave.getHost());
		slave.setMD5(md5);
		this.slaveCollection.put(md5, slave);
		System.out
				.println("[SlaveCollection]: added new slave with md5 " + md5);
	}

	@Override
	// TODO: implement more clever pick.
	public ISlave getSlave() {
		if (this.slaveCollection.size() == 0)
			return null;
		else {

			String selected = null;
			int lowest = 0;

			System.out
					.println("[SlaveCollection]: Request for a slave. Current number of slaves: "
							+ this.slaveCollection.size());
			for (String k : this.slaveCollection.keySet()) {
				ISlave s = this.slaveCollection.get(k);
				if (selected == null || s.getLoad() < lowest) {
					selected = s.getMD5();
					lowest = s.getLoad();
				}
			}
			return this.slaveCollection.get(selected);
		}
	}

	@Override
	public String getMD5(String host) {
		String res = "";
		Double randNumber = Math.random() + System.currentTimeMillis();

		try {
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(randNumber.toString().getBytes());
			byte[] md5 = algorithm.digest();
			String tmp = "";
			for (int i = 0; i < md5.length; i++) {
				tmp = (Integer.toHexString(0xFF & md5[i]));
				if (tmp.length() == 1) {
					res += "0" + tmp;
				} else {
					res += tmp;
				}
			}
		} catch (Exception ex) {
		}

		return res;
	}

	@Override
	public synchronized void removeSlave(String md5) {
		this.slaveCollection.remove(md5);
		System.out.println("[SlaveCollection]: removing slave with md5 " + md5
				+ ". Current number of slaves: " + this.slaveCollection.size());
	}
}