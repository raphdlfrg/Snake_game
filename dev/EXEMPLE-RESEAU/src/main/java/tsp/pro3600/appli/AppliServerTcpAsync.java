package tsp.pro3600.appli;

import org.apache.logging.log4j.Level;

import static tsp.pro3600.common.Log.COMM;
import static tsp.pro3600.common.Log.GEN;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import tsp.pro3600.common.Log;
import tsp.pro3600.tcp.TcpServerAsync;
import tsp.pro3600.tcp.TcpSocketAsync;

/**
 * Serveur TCP asynchrone
 * @author Eric Lallet.
 *
 */
public class AppliServerTcpAsync {

	/**
	 * nombre d'arguments attendus par le main.
	 */
	private static final int NBARGS = 1;
	/**
	 * position du numéro du port dans argv[].
	 */
	private static  final int PORTARG = 0;


	
	private static final int NBVAL = 10;
	private static final int MESSSIZE = NBVAL * Integer.BYTES;


	/**
	 * Affiche le contenu du buffer contenant les entiers.
	 * @param buffer
	 *          le buffer contenant les entiers.
	 */
	static void displayBuff(final ByteBuffer buffer) {
		buffer.flip();
		while (buffer.hasRemaining()) {
			System.out.print(buffer.getInt() + " ");
		}
		System.out.println();
	}

	/**
	 * 
	 * @param argv
	 *        argv[0] = port du serveur.
	 */
	public static void main(final String[] argv) {

		// Map de tous les clients connectés
		Map<SelectionKey, TcpSocketAsync> clients = new HashMap<SelectionKey, TcpSocketAsync>();
		if (argv.length != NBARGS) {
			System.out.println("usage: java Serveur <port>");
			return;
		}

		// monter le log à niveau plus haut que trace pour ne plus tracer toutes les méthodes du package tcp.
		Log.setLevel(COMM, Level.TRACE);
		Log.setLevel(GEN, Level.INFO);

		try (Selector selector = Selector.open();
				TcpServerAsync serveur = new TcpServerAsync(Integer.parseInt(argv[PORTARG]))) {

			// listenChannel du serveur enregistré auprés du selector pour surveiller les accept()
			serveur.getListenChannel().register(selector, SelectionKey.OP_ACCEPT);

			COMM.info("Server is ready");
			while (true) {
				selector.select();
				Set<SelectionKey> readyKeys = selector.selectedKeys();

				COMM.info("{}", () -> "Server: Nb Ready keys:" + readyKeys.size());

				Iterator<SelectionKey> it = readyKeys.iterator();
				while (it.hasNext()) {
					SelectionKey curKey = it.next();
					if (curKey.isAcceptable()) {
						// connexion d'un nouveau client
						TcpSocketAsync client = serveur.acceptClientAsync();
						SelectionKey newKey = client.getRwChan().register(selector, SelectionKey.OP_READ);
						clients.put(newKey, client);
						client.startNewReception(MESSSIZE);
					}
					else {
						if (curKey.isReadable()) {
							// réception d'un messahe, ou déconnexion d'un client
							TcpSocketAsync client = clients.get(curKey);
							if (client.receiveBufferAsync() != -1) {
								// réception de données
								if (client.inBufferIsFull()) {
									// message complet
									System.out.println("réception complète pour " + curKey);
									displayBuff(client.getInBuffer());
									// on réinintialise le buffer du client pour la réception du message suivant
									client.startNewReception(MESSSIZE);

								} // else: rien à faire, si ce n'est attendre la réception suivante pour continuer de recevoir le message
							} else {
								// déconnexion du client. 
								// Éventuellement il faudrait ajouter le traitement des données reçues avant déconnexion
								// mais pas encore traitées (pas fait dans le code ci dessous.
								client.close();
								clients.remove(curKey); // retrait de la liste des clients
								curKey.cancel(); // retrait des clefs enregistrées auprès du selector
								
							}
						}

					}
					// on retire la clef traité pour ne pas la retrouver au prochain tour
					readyKeys.remove(curKey);
				}
			}


		} // il faudrait traiter et récupérer certaines exceptions dans la boucle pour ne pas tuer
		  // le serveur à la moindre exception: un client mal intentionné peut tuer le serveur facilement.
		catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
	}

}
