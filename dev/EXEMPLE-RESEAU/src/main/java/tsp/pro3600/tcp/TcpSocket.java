package tsp.pro3600.tcp;


import static tsp.pro3600.common.Log.COMM;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Classe qui
 *  1. établit la connexion d'un client ; 
 *  2. fournit les méthodes d'échange de données sur un canal TCP connecté (par un client ou un serveur).
 * 
 * @author Eric Lallet.
 *
 */
public class TcpSocket implements AutoCloseable {

	/**
	 * un canal TCP connecté (soit par la connexion coté client, soit par l'accept coté serveur)
	 */
	private SocketChannel rwChan = null;


	/**
	 * Constructeur utilisé par les serveurs pour créer une instance TcpSocket à partir d'une connexion obtenue suite à un accept.
	 * @param rwChan 
	 *              le canal déjà connecté suite à l'accept.
	 */
	public TcpSocket(final SocketChannel rwChan) {
		COMM.trace("TcpSocket: constructeur à partir d'un canal créé par un accept de serveur");	

		this.rwChan = rwChan;
	}

	/**
	 * Constructeur utilisé par le client pour ouvrir une connexion vers un serveur, et créer un instance TcpSocket.
	 * @param serverHost
	 * 	                le nom de la machine où tourne le serveur.
	 * @param serverPort
	 *                  le numéro du port TCP utilisé par le serveur.
	 * @throws IOException
	 *                  toutes les exceptions d'entrées/sorties.
	 */
	public TcpSocket(final String serverHost, final int serverPort) throws IOException {

		COMM.trace("{}", () -> "TcpSocket: constructeur d'un client: " + serverHost + ":" + serverPort);	

		InetSocketAddress rcvAddress = new InetSocketAddress(InetAddress.getByName(serverHost), serverPort);
		this.rwChan = SocketChannel.open(rcvAddress);
	}
	
	
	
	public SocketChannel getRwChan() {
		return rwChan;
	}

	
	/**
	 * Envoie les données contenues dans le buffer sur la connexion TCP.
	 * @param buffer
	 *              le buffer contenant les données à envoyer.
	 * @return
	 *              le nombre d'octets envoyés.
	 * @throws IOException
	 *                  toutes les exceptions d'entrées/sorties.
	 */
	public int sendBuffer(final ByteBuffer buffer) throws IOException {
		COMM.trace("TcpSocket::sendBuffer");	

		return (rwChan.write(buffer));
	}



	/**
	 * Envoie un entier sur la connexion TCP.
	 * @param size
	 *            la valeur de l'entier à envoyer.
	 * @return
	 *            le nombre d'octets envoyés.
	 * @throws IOException
	 *            toutes les exceptions d'entrées/sorties.
	 */
	public int sendSize(final int size) throws IOException {

		COMM.trace("{}", () ->  "TcpSocket::sendSize: " + size);	

		ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
		buffer.putInt(size);
		buffer.flip();
		return sendBuffer(buffer);
	}

	/**
	 * Reçoit des données sur la connexion TCP jusqu'à la fermeture la connexion entrante ou le remplissage du buffer passé en paramètre.
	 * La méthode remplit le buffer à partir du premier octet de celui-ci, en écrasant éventuellement des données déjà présente.
	 * Cette méthode est conçue pour la réception de messages d'une taille déterminée (celle du buffer) en mode synchrone.
	 * @param buffer
	 *             le buffer où stocker les données.
	 * @return
	 *             le nombre d'octets placés dans le buffer.
	 * @throws IOException
	 *            toutes les exceptions d'entrées/sorties.
	 */
	public int receiveBufferSync(final ByteBuffer buffer) throws IOException {

		COMM.trace("TcpSocket::receiveBufferSync.");	


		int lus, total = 0;
		buffer.clear();
		while ((lus = rwChan.read(buffer)) > 0) {
			total += lus;
		}

		return total;

	}


	
	/**
	 * Reçoit un entier sur la connexion TCP.
	 * @return
	 *         la valeur de l'entier reçu.
	 * @throws IOException
	 *             le nombre d'octets placés dans le buffer.
	 */
	public int receiveSize() throws IOException {

		COMM.trace("TcpSocket::receiveSize.");	


		ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
		receiveBufferSync(buffer);
		buffer.flip();
		return buffer.getInt();
	}



	@Override
	public void close() throws IOException {

		COMM.trace("TcpSocket::close.");	

		this.rwChan.close();
	}


	/**
	 * Renvoie tous les octets reçus sur la connexion TCP tant que celle-ci reste ouverte. 
	 * @return
	 *        le nombre total d'octets reçus.
	 * @throws IOException
	 *        toutes les exceptions d'entrées/sorties.
	 */
	public int echo() throws IOException {

		COMM.trace("TcpSocket::echo.");	

		int lus, total = 0;
		ByteBuffer buffer = ByteBuffer.allocate(1024);

		do {
			buffer.clear();
			lus = rwChan.read(buffer);
			if (lus <= 0) {
				break;
			}

			total += lus;
			buffer.flip();
			if (lus == 4) {
				buffer.getInt();
				buffer.rewind();
			}
			rwChan.write(buffer);

		} while (lus > 0);

		return total;
	}

}
