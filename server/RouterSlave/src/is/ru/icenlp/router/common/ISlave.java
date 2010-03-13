package is.ru.icenlp.router.common;

import java.io.IOException;

public interface ISlave {
	public String transle(String text) throws IOException;

	public void decreaseLoad();

	public void increseLoad();

	public int getLoad();

	public void setMD5(String md5);

	public String getMD5();

	public String getHost();

	public void startPingCheck();

	public void deleteThisSlave();
}
