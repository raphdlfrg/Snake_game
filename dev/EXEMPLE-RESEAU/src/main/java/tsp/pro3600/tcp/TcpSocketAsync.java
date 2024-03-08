package tsp.pro3600.tcp;

import static tsp.pro3600.common.Log.COMM;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class TcpSocketAsync extends TcpSocket{

	/**
	 * Buffer contenant les données en cours de réceptions
	 */
	private ByteBuffer inBuffer; 
	

	/**
	 * Constructeur utilisé par les serveurs pour créer une instance TcpAsync à partir
	 * d'une connexion obtenue suite à un accept.
	 * @param rwChan 
	 *              le canal déjà connecté suite à l'accept.
	 * @throws IOException 
	 */
	public TcpSocketAsync(final SocketChannel rwChan) throws IOException {
		super(rwChan);
		COMM.trace("TcpAsync: constructeur à partir d'un canal créé par un accept de serveur");
		rwChan.configureBlocking(false);
	}
	
	/**
	 * Méthode qui aloue ou réinitialise le buffer de réception. Doit être appelée toutes les fois
	 * que l'on veut recevoir un nouveau message.
	 * @param size
	 *           taille du message attendu.
	 */
	public void startNewReception(final int size) {
		if ((inBuffer != null) && (inBuffer.capacity() == size)) {
			// le buffer alloué est déjà à la bonne taille: on le garde, on fait juste un clear.
			inBuffer.clear();
		}
		else {
			inBuffer = ByteBuffer.allocate(size);
		}
	}
	
	/**
	 * Reçoit des données sur la connexion TCP et les ajoute au inBuffer de réception.
	 * La méthode remplit le buffer à la suite d'éventuelle données déjà présente.
	 * Cette méthode est conçue pour la réception de messages  en mode asynchrone.
	
	 * @return
	 *             le nombre d'octets ajoutés dans le buffer, ou -1 en cas déconnexion.
	 * @throws IOException
	 *            toutes les exceptions d'entrées/sorties.
	 */
	public int receiveBufferAsync() throws IOException {
		COMM.trace("TcpAsync::receiveBufferAsync.");
		if (inBuffer == null) {
			throw new IOException("InBuffer not initialized");
		}
		
		return getRwChan().read(inBuffer);
			
	
	}
	
	/**
	 * getter sur le buffer pour consulter le message reçu.
	 * @return
	 *        le buffer de réception.
	 */
	public ByteBuffer getInBuffer() {
		return inBuffer;
	}

	/**
	 * Indique si le buffer de réception est plein (et que donc le message est intégralement reçu
	 * @return
	 *          true si buffer est plein, false sinon.
	 */
	public boolean inBufferIsFull() {
		return ! inBuffer.hasRemaining();
	}
	
}
