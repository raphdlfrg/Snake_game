package tsp.pro3600.appli;

import org.apache.logging.log4j.Level;

import static tsp.pro3600.common.Log.COMM;
import static tsp.pro3600.common.Log.GEN;

import java.io.IOException;
import java.nio.ByteBuffer;

import tsp.pro3600.common.Log;
import tsp.pro3600.tcp.TcpSocket;


/**
 * Client TCP qui envoie le contenu d'une chaîne de caractères et la valeur d'un entier.
 * @author Eric Lallet.
 *
 */
public class AppliClientTcp {
	/**
	 * nombre d'argument du main
	 */
	private static final int NBARGS = 2;
	/**
	 * position du nom de la machine du serveur dans  argv[].
	 */
	private static  final int HOSTARG = 0;
	/**
	 * position du port du serveur dans argv[].
	 */
	private static  final int PORTARG = 1;

	public static void main(String[] argv) {


		if (argv.length != NBARGS) {
			System.out.println("usage: java Client <machine> <port> ");
			return;
		}

		// monter le log à niveau plus haut que trace pour ne plus tracer toutes les méthodes du package tcp.
		Log.setLevel(COMM, Level.TRACE);
		Log.setLevel(GEN, Level.INFO);

		String message = "Mon message exemple";
		String reponse;
		int lus, ecrits;


		ByteBuffer inBuffer;  // Buffer assez grand pour envoyer la chaîne puis l'entier
		ByteBuffer inBuffer2;  // Buffer exactement dimmensioné pour recevoir la chaîne.
		ByteBuffer outBuffer;  // Buffer exactement dimmensioné pour recevoir l'entier.


		outBuffer = ByteBuffer.allocate(message.length());
		inBuffer = ByteBuffer.allocate(message.length());
		inBuffer2 =  ByteBuffer.allocate(Integer.BYTES);

		// chaîne dans le buffer.
		outBuffer.put(message.getBytes());
		outBuffer.flip();



		try (TcpSocket client = new TcpSocket(argv[HOSTARG], Integer.parseInt(argv[PORTARG]));) 
		{
			// Envoi de la chaine.
			ecrits = client.sendBuffer(outBuffer);

			// Réception de la réponse
			lus = client.receiveBufferSync(inBuffer);

			reponse = new String(inBuffer.array());

			GEN.info("{}", () -> "AppliClientTcp: chaîne émise = " + message );
			GEN.info("{}", () -> "AppliClientTcp: chaîne reçu = " + reponse );



			outBuffer.clear();
			outBuffer.putInt(42);
			outBuffer.flip();

			ecrits = client.sendBuffer(outBuffer);
			lus = client.receiveBufferSync(inBuffer2);


			inBuffer2.flip();
			int value = inBuffer2.getInt();
			GEN.info("AppliClientTcp: entier émis = 42 ");
			GEN.info("{}", () -> "AppliClientTcp: entier reçu = " + value );


		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


}
