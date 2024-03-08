package tsp.pro3600.tcp;


import static tsp.pro3600.common.Log.COMM;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.ServerSocketChannel;


/**
 * Classe qui crée un serveur en attende de connexion et qui fournit une méthode pour construire une instance de TcpSocket connectée à chaque client accepté.
 * @author Eric Lallet.
 *
 */
public class TcpServer  implements AutoCloseable {
	/**
	 * le canal d'écoute du serveur.
	 */
	private ServerSocketChannel listenChannel;


	
	/**
	 * Constructeur qui crée un serveur TCP en écoute sur le port passé en paramètre.
	 * @param port
	 *            numéro du port d'écoute.
	 * @throws IOException
	 *            toutes les exceptions d'entrées/sorties.
	 */
	public TcpServer(final int port) throws IOException {

		COMM.trace("{}", () -> "TcpServer: constructeur du serveur en écoute sur le port " + port );	

		listenChannel = ServerSocketChannel.open();

		listenChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		listenChannel.bind(new InetSocketAddress(port));

	}

	
	public ServerSocketChannel getListenChannel() {
		return listenChannel;
	}


	/**
	 * Accepte les nouveaux clients et crée une instance de TcpScoket à partir de cette nouvelle connexion.
	 * @return
	 *         la référence sur l'instance de TcpSocket créée.
	 * @throws IOException
	 *          toutes les exceptions d'entrées/sorties.
	 */
	public TcpSocket acceptClient() throws IOException {

		COMM.trace("TcpSserveur::accept.");	

		return new TcpSocket(listenChannel.accept());


	}



	@Override
	public void close() throws Exception {

		COMM.trace("TcpSserveur::close.");	


		listenChannel.close();

	}

}
