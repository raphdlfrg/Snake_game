package tsp.pro3600.appli;

import org.apache.logging.log4j.Level;

import static tsp.pro3600.common.Log.COMM;
import static tsp.pro3600.common.Log.GEN;

import java.io.IOException;
import java.nio.ByteBuffer;

import tsp.pro3600.common.Log;
import tsp.pro3600.tcp.TcpSocket;


/**
 * Client TCP qui envoie des entiers en boucle.
 * @author Eric Lallet.
 *
 */
public class AppliClientTcpV2 {
	/**
	 * nombre d'argument du main
	 */
	private static final int NBARGS = 5;
	/**
	 * position du nom de la machine du serveur dans  argv[].
	 */
	private static  final int HOSTARG = 0;
	/**
	 * position du port du serveur dans argv[].
	 */
	private static  final int PORTARG = 1;
	
	/**
	 * position de la valeur à envoyer dans  argv[].
	 */
	private static  final int VALARG = 2;
	
	/**
	 * position du nombre de valeurs à envoyer dans  argv[].
	 */
	private static  final int NBVALARG = 3;
	
	/**
	 * position du timer dans  argv[].
	 */
	private static  final int TIMERARG = 4;
	

	public static void main(String[] argv) {


		if (argv.length != NBARGS) {
			System.out.println("usage: java Client <machine> <port> <val> <nbval> <timer>");
			return;
		}

		// monter le log à niveau plus haut que trace pour ne plus tracer toutes les méthodes du package tcp.
		Log.setLevel(COMM, Level.TRACE);
		Log.setLevel(GEN, Level.INFO);

		
		int val = Integer.parseInt(argv[VALARG]);
		int nbval = Integer.parseInt(argv[NBVALARG]);
		int timer = Integer.parseInt(argv[TIMERARG]);
		
		
		ByteBuffer outBuffer;  // Buffer exactement dimmensioné pour envoyer l'entier.


		outBuffer = ByteBuffer.allocate(Integer.BYTES);
		

		// val dans le buffer.
		outBuffer.putInt(val);
		outBuffer.flip();



		try (TcpSocket client = new TcpSocket(argv[HOSTARG], Integer.parseInt(argv[PORTARG]));) 
		{
			int tour = 0;
			while (tour < nbval) {
				tour++;
				client.sendBuffer(outBuffer);
				GEN.info("envoi " + tour +  "  de la valeur" + val );
				outBuffer.rewind(); // buffer pret pour être ré-envoyé
				Thread.sleep(timer * 1000);
			}
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


}
