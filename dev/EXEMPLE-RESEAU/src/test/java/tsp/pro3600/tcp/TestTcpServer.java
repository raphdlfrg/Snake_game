//CHECKSTYLE:OFF
package tsp.pro3600.tcp;

import static tsp.pro3600.common.Log.TEST;

import org.apache.logging.log4j.Level;



import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import tsp.csc4509.tcpservices.TcpTestTools;
import tsp.pro3600.common.Log;
import tsp.pro3600.tcp.TcpServer;
import tsp.pro3600.tcp.TcpSocket;

public class TestTcpServer {
	/**
	 * port du serveur lancé par les tests.
	 */
	private static final int SERVERPORT = 40002;
	
	/**
	 * Réreference sur l'instance de l'outil de test. Cet outil fournit
	 * la possibilité de créer des serveurs ou des clients à placer en face
	 * de des clients ou serveurs à tester.
	 */
	static private TcpTestTools tcpTestTools;

	@BeforeAll
	static public void lanceTestTools() {
		tcpTestTools = new TcpTestTools();
		Log.setLevel(TEST, Level.WARN);

	}


	// test unitaire de l'accept du serveur.
	@Test
	public void testAccept() throws Exception {

		TEST.info("TestTcpServer::testAccept -> test d'acceptation d'un client");


		// Ici test unitaire du constructeur du serveur
		TcpServer serveur = new TcpServer(SERVERPORT);

		// si aucune exception n'est levé, le test OK.

		tcpTestTools.alarmStart(5000); // on laisse 5 secondes pour que la connexion s'établisse
		tcpTestTools.clienStart(SERVERPORT); // on demande à un client de se connecter à notre serveur

		// Ici test unitaire de la méthode acceptClient()
		TcpSocket client = serveur.acceptClient();
		// on n'arrive ici que si il n'y pas eu d'exception. C'est donc un succès.


		tcpTestTools.alarmStop(); // connexion ok. On stope l'alarme.

		client.close();

		// Ici test unitaire de la méthode close() du serveur
		serveur.close();

		// si aucune exception n'a été lévée, le test est OK.
	}

}
