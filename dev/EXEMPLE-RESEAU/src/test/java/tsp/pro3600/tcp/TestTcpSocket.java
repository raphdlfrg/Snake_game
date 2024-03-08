//CHECKSTYLE:OFF
package tsp.pro3600.tcp;


import org.apache.logging.log4j.Level;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.Disabled;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static tsp.pro3600.common.Log.TEST;

import tsp.csc4509.tcpservices.TcpTestTools;
import tsp.pro3600.common.Log;
import tsp.pro3600.tcp.TcpSocket;

public class TestTcpSocket {
	/**
	 * taille des ByteBuffer.
	 */
	private static final int BUFFERSIZE = 1024;
	/**
	 * Taille du fichier DATATESTFILE. Pour tester l'envoi et la réception
	 * en plusieurs sendBuffer()/receiveBuffer(), cette taille doit être
	 * plus grande que BUFFERSIZE.
	 */
	private static final int DATATESTFILESIZE = (int) (2.5 * BUFFERSIZE);

	/**
	 * Réreference sur l'instance de l'outil de test. Cet outil fournit
	 * la possibilité de créer des serveurs ou des clients à placer en face
	 * de des clients ou serveurs à tester.
	 */
	private static TcpTestTools tcpTestTools;

	/**
	 * numéro du port utilisé par le serveur lancé par le TcpTestTools.
	 */
	private static final int TESTSERVERORT = 40001; 



	@BeforeAll
	static public void lanceTestTools() {
		tcpTestTools = new TcpTestTools();
		Log.setLevel(TEST, Level.WARN);
	}

	// test unitaire de la connexion d'un client
	@Test
	public void testTcpSocketClient() throws Exception {

		TEST.info("(TestTcpSocket::testTcpSocketClient] ->  test de création d'un client");

		// lancement d'un serveur de test  TCP
		tcpTestTools.serverStart(TESTSERVERORT);

		// on laisse 5 secondes pour que la connexion s'établisse.
		tcpTestTools.alarmStart(5000);

		// connexion du client
		TcpSocket client = new TcpSocket("localhost", TESTSERVERORT);
		// si aucune exception n'est levé, le test OK.

		client.close();
		tcpTestTools.alarmStop();
		tcpTestTools.serverStop();


	}

	// test unitaire de la méthode sendBuffer()
	// principe du test:
	// On demande à la bibliothèque de test d'ouvrir un serveur qui va sauver tout ce qu'il reçoit dans un fichier.
	// On connecte notre client à tester.
	// On envoie tout le contenu d'un fichier au serveur.
	// On compare le fichier créé par le serveur avec le fichier émis par le client.
	@Test
	public void testSendBuffer(@TempDir Path tempDir) throws Exception {
		final String RECEIVEFILENAME = tempDir.resolve("recu.bin").toString();
		final String SENDFILENAME = tempDir.resolve("envoye.bin").toString();

		TEST.info("[TestTcpSocket::testSendBuffer] -> test d'envoie d'un message avec un client");

		// création d'un fichier aléatoire contenant les données à envoyer.
		createDataTestFile(SENDFILENAME, 0);

		// lancement d'un serveur de test TCP
		tcpTestTools.serverReceiveFileStart(TESTSERVERORT, RECEIVEFILENAME);

		// création d'un client
		TcpSocket client = new TcpSocket("localhost", TESTSERVERORT);

		FileInputStream fin = new FileInputStream(SENDFILENAME);
		FileChannel fcin = fin.getChannel();
		ByteBuffer buffer = ByteBuffer.allocate(BUFFERSIZE);
		int lu;
		do {
			buffer.clear();
			lu = fcin.read(buffer);
			buffer.flip();
			client.sendBuffer(buffer);
		} while (lu == BUFFERSIZE);
		client.close(); // attention, la lecture de l'autre coté ne débloque que
		// si le buffer est plein, ou la connexion fermée.
		// il faut donc fermer le client pour que le serveur termine sa lecture
		// et sauve la fin du fichier.

		Thread.sleep(1000); // on laisse un peu de temps au serveur
		// pour qu'il termine sa réception avant de le stopper.

		tcpTestTools.serverReceiveFileStop();

		assertTrue( compareFile(RECEIVEFILENAME, SENDFILENAME),"Données reçus identiques aux données envoyées");
		fin.close();
	}


	@Test
	public void testReceiveBuffer(@TempDir Path tempDir) throws Exception {
		final String RECEIVEFILENAME = tempDir.resolve("recu.bin").toString();
		final String SENDFILENAME = tempDir.resolve("envoye.bin").toString();

		TEST.info("[TestTcpSocket::testReceiveBuffer] -> test de la méthode receiveBuffer()");

		// création d'un fichier aléatoire contenant les données à envoyer.
		createDataTestFile(SENDFILENAME, 0);
		// lancement d'un serveur de test TCP
		tcpTestTools.serverSendFileStart(TESTSERVERORT, SENDFILENAME);

		// création d'un client
		TcpSocket client = new TcpSocket("localhost", TESTSERVERORT);

		FileOutputStream fout = new FileOutputStream(RECEIVEFILENAME);
		FileChannel fcout = fout.getChannel();
		ByteBuffer buffer = ByteBuffer.allocate(BUFFERSIZE);
		int lu; 
		do {
			buffer.clear();
			lu = client.receiveBufferSync(buffer); // test de la méthode
			if (lu > 0) {
				buffer.flip();
				fcout.write(buffer);
			}
		} while (lu > 0);
		client.close();
		fcout.close();
		fout.close();


		tcpTestTools.serverReceiveFileStop();

		assertTrue(compareFile(RECEIVEFILENAME, SENDFILENAME),"Données reçus identiques aux données envoyées");
	}


	
	


	/**
	 * Compare le contenu de deux fichiers et retourne true s'ils sont identiques.
	 * @param fileName1
	 * 					nom du premier fichier.
	 * @param fileName2
	 * 					nom du second fichier.
	 * @return
	 * 					true si les deux fichiers ont le même contenu, false sinon.
	 * @throws Exception
	 * 					toutes les exceptions possibles.
	 */
	private static  boolean compareFile(final String fileName1, final String fileName2) throws Exception {

		try (	FileInputStream fin1 = new FileInputStream(fileName1);
				FileInputStream fin2 = new FileInputStream(fileName2);
				FileChannel fcin1 = fin1.getChannel();
				FileChannel fcin2 = fin2.getChannel();
				) {
			ByteBuffer buffer1 = ByteBuffer.allocate(BUFFERSIZE);
			ByteBuffer buffer2 = ByteBuffer.allocate(BUFFERSIZE);
			int lu1, lu2;
			do {
				buffer1.clear();
				buffer2.clear();
				lu1 = fcin1.read(buffer1);
				lu2 = fcin2.read(buffer2);
				if (lu1 != lu2) {

					return false;
				}
				buffer1.flip();
				buffer2.flip();
				for (int ind=0; ind < lu1; ind++) {
					if (buffer1.get() != buffer2.get()) {

						return false;
					}
				}

			} while (lu1 == BUFFERSIZE);


			return true;
		} // les close des fichiers est automatique avec la syntaxe try () { }
	}


	/**
	 * Remplit un fichier de bytes aléatoires. Le fichier est plus grand que les ByteBuffer utilisés
	 * pour les tests afin d'être sûr que plusieurs tours de boucles soient nécessaires pour le
	 * lire.
	 * @param fileName
	 *            nom du fichier à remplir.
	 * @param randomSeed
	 *            graine du générateur de la série des nombres pseudo-aléatoires mis
	 *            dans le fichier. 
	 */
	private static void createDataTestFile(final String fileName, final int randomSeed) {
		byte [] randomBytes = new byte[DATATESTFILESIZE];
		// générateur de nombres pseudo-aléatoires. Attention pour rendre les
		// tests reproductibles, il faut fixer le début de la suite pour régénérer
		// le même fichier à chaque lancement des tests.
		Random random = new Random(randomSeed);

		// tableau remplis d'octets aléatoires
		random.nextBytes(randomBytes);


		ByteBuffer buffer = ByteBuffer.wrap(randomBytes);
		// après un wrap() le curseur position est déjà à 0. Pas de flip() à faire.

		try (FileOutputStream fout = new FileOutputStream(fileName);
				FileChannel fcout = fout.getChannel();) {

			fcout.write(buffer);

		} catch (FileNotFoundException fnfe) {
			System.out.println(fnfe);
		} catch (IOException ioe) {
			System.out.println(ioe);
		}
		// le close des fichiers est automatique avec la syntaxe try () { }
	}

}
