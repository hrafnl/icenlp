package is.iclt.icenlp.router.common;

public interface ISlaveCollection {
	public void addSlave(ISlave slave);

	public void removeSlave(String md5);

	public ISlave getSlave();

	public String getMD5(String host);
}
