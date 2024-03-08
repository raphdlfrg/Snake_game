package tsp.pro3600.appli;

import org.apache.logging.log4j.Level;

import static tsp.pro3600.common.Log.COMM;
import static tsp.pro3600.common.Log.GEN;

import java.io.IOException;

import tsp.pro3600.common.Log;
import tsp.pro3600.tcp.TcpServer;
import tsp.pro3600.tcp.TcpSocket;

/**
 * Serveur TCP qui renvoie en echo tout ce que lui envoie le premier client qui se connecte. 
 * @author Eric Lallet.
 *
 */
public class AppliServerTcp {

	/**
	 * nombre d'arguments attendus par le main.
	 */
	private static final int NBARGS = 1;
	/**
	 * position du numéro du port dans argv[].
	 */
	private static  final int PORTARG = 0;

	/**
	 * 
	 * @param argv
	 *        argv[0] = port du serveur.
	 */
	public static void main(final String[] argv) {


		if (argv.length != NBARGS) {
			System.out.println("usage: java Serveur <port>");
			return;
		}

		// monter le log à niveau plus haut que trace pour ne plus tracer toutes les méthodes du package tcp.
		Log.setLevel(COMM, Level.TRACE);
		Log.setLevel(GEN, Level.INFO);

		try (TcpServer serveur = new TcpServer(Integer.parseInt(argv[PORTARG]));
				TcpSocket client = serveur.acceptClient(); ) {

			GEN.info("AppliServerTcp: lancement de echo" );
			client.echo();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
}
