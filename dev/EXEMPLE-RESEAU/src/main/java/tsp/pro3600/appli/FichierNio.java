package tsp.pro3600.appli;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe pour tester JAVA NIO avec les fichiers: écrit dans un fichier argv[0] les entiers de 
 * 0 à argv[1] (exclus) et les relit pour les placer dans une ArrayList.
 * @author Eric Lallet.
 *
 */
public final class FichierNio {
	/**
	 * number of expected arguments of main method.
	 */
	private static final int NBARGS = 2;

	/**
	 * position of the destination filename in argv[].
	 */
	private static  final int FILENAMEARG = 0;
	
	/**
	 * position of the max value  in argv[].
	 */
	private static  final int MAXVALUEARG = 1;
	
	/**
	 * size of ByteBuffer.
	 */
	static final int BUFFSIZE = 1024;
	
	/**
	 * 
	 */
	private FichierNio() {
		
	}
	
	/**
	 * 
	 * @param argv
	 *          argv[0] contient le nom de fichier et argv[1] l'entier limite.
	 */
	public static void main(final String[] argv) {
		if (argv.length != NBARGS) {
			System.err.println("Usage de FichierNio: nomDuFichier valeurMax");
			return;
		}
		List<Integer> valeurs = null;
		
		try (FileOutputStream fout = new FileOutputStream(argv[FILENAMEARG])) {
			int max = Integer.parseInt(argv[MAXVALUEARG]);
			writeValues(fout, max);
			fout.close();
		} catch (FileNotFoundException fnfe) {
			System.out.println(fnfe);
		} catch (IOException ioe) {
			System.out.println(ioe);
		}
		
		try (FileInputStream fin = new FileInputStream(argv[FILENAMEARG])) {
			valeurs = readValues(fin);
		} catch (FileNotFoundException fnfe) {
			System.out.println(fnfe);
		} catch (IOException ioe) {
			System.out.println(ioe);
		}
		
		for (Integer val: valeurs) {
			System.out.println(val);
		}
	}

	/**
	 * Écrit les entiers de 0 à max-1 dans le fichier fout.
	 * @param fout
	 *			fichier ouvert en écriture où doivent être écrit les entiers.
	 * @param max
	 * 			limite (non incluse) des valeurs à écrire dans le fichier.
	 * @return 
	 * 			le nombre d'octets écrits dans le fichier.
	 * @throws IOException
	 * 			toutes les exceptions provoquées par les erreurs d'entrées/sorties.
	 */
	public static int writeValues(final FileOutputStream fout, final int max) throws IOException {
		FileChannel fcout = fout.getChannel();
		ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * max);
		for (int val = 0; val < max; val++) {
			buffer.putInt(val);
		}
		buffer.flip();
		return fcout.write(buffer);
	}
	
	/**
	* Charge dans une liste tous les entiers contenus dans le fichier fin.
	* @param fin
	*       fichier ouvert en lecture où sont les entiers à lire.
	* @return
	*       Une liste qui contient tous les entiers du fichier dans le même ordre que le fichier.
	* @throws IOException
	*       toutes les exceptions provoquées par les erreurs d'entrées/sorties.
	*/
	public static List<Integer> readValues(final FileInputStream fin) throws IOException {
		int lu = 0;
		FileChannel fcin = fin.getChannel();
		List<Integer> valeurs = new ArrayList<Integer>();
		ByteBuffer buffer = ByteBuffer.allocate(BUFFSIZE);
		do {
			buffer.clear();
			fcin.read(buffer);
			buffer.flip();
			for (int ind = 0; ind < buffer.limit() / (Integer.BYTES); ind++) {
				valeurs.add(buffer.getInt());
			}
		} while (lu == BUFFSIZE);
		
		
		return valeurs;
	}
	
	
	
}
