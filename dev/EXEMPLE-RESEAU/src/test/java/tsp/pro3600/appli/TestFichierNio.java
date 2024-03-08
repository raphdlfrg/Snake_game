package tsp.pro3600.appli;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tsp.pro3600.appli.FichierNio;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class TestFichierNio {
	/**
	 * taille des ByteBuffer.
	 */
	private static final int BUFFERSIZE = 1024;
	private static final String TESTFILENAME = "./data/entiers0-9.data";

	@Test
	public void testWriteValues(@TempDir Path tempDir) throws Exception { // JUnit va créer un répertoire temporaire le temps du test.
		// nom du fichier temporaire pour le test.
		String tempFileName = tempDir.resolve("test.bin").toString();

		FileOutputStream fout = new FileOutputStream(tempFileName);
		FichierNio.writeValues(fout, 10);
		fout.close();
		assertTrue(compareFile(tempFileName, TESTFILENAME), "test de WriteValue: le fichier écrit identique au modèle");

		fout = new FileOutputStream(tempFileName);
		FichierNio.writeValues(fout, 15);
		fout.close();
		assertTrue(!compareFile(tempFileName, TESTFILENAME), "test de WriteValue: le fichier écrit différent du modèle" );
	}


	@Test
	public void testReadValues() throws Exception {
		FileInputStream fin = new FileInputStream(TESTFILENAME);
		List<Integer> list = FichierNio.readValues(fin);
		fin.close();
		for (int ind = 0; ind < 10; ind++) {
			assertTrue( list.get(ind) == ind, "Test les élements de la liste");
		}
		fin.close();
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

}
