package tsp.pro3600.tcp;

import static tsp.pro3600.common.Log.COMM;

import java.io.IOException;

public class TcpServerAsync extends TcpServer {

	public TcpServerAsync(final int port) throws IOException {
		super(port);
		getListenChannel().configureBlocking(false);
	}

	

	public TcpSocketAsync acceptClientAsync() throws IOException {

		COMM.trace("TcpSserveur::accept.");	

		return new TcpSocketAsync(getListenChannel().accept());

	}
	

}
