package is.iclt.icenlp.router.common;

import java.io.IOException;
import java.net.Socket;

public class SlavePingThread implements Runnable {

	private Socket socket;
	private ISlave parent;

	public SlavePingThread(Socket s, ISlave parent) {
		this.socket = s;
		this.parent = parent;
	}

	@Override
	public void run() {
		while (true) {
			try {

				try {
					// send empty packet to slave.
					this.socket.getOutputStream().write(new byte[512]);
				} catch (IOException e) {
					System.out
							.println("[SlavePingThread]: unable to send ping to slave with md5 "
									+ this.parent.getMD5());
					this.parent.deleteThisSlave();
					break;
				}

				// Sleep in for seconds.
				Thread.sleep(3000);
			}

			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
