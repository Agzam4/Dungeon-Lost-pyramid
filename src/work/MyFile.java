package work;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MyFile {
	
	public static final String USER_DIRECTORY = System.getProperty("user.dir");
	
	/**
	 * Checks the file for the presence off
	 * @param file - path to String (File)
	 * @return boolean
	 */
	public static boolean checkFolder(String file) {
		return checkFile(new File(file));
	}
	
	/**
	 * Checks the file for the presence of
	 * @param file - path to file (File)
	 * @return boolean
	 */
	public static boolean checkFile(File file) {
		return file.exists();
	}
	
	/**
	 * Creates a folder
	 * @param folder - path to folder
	 * @throws IOException
	 */
	public static void createFolder(String folder) throws IOException {
		Files.createDirectory(Paths.get(folder));
	}
	
	/**
	 * Reading text from resource-file
	 * @param path - path to resource
	 * @return text from file (if exists), but else return null
	 */
	public static String readFileInResource(String path) {
		System.out.println("[MyFile] Reading file in resource: " + path);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				MyFile.class.getClass().getResourceAsStream(path)));
		
		String line;
		String txt = "";
		try {
			while ((line = reader.readLine()) != null) {
				txt += line + "\n";
			}
		} catch (IOException e) {
			return null;
		}
		return txt;
	}
	
	/**
	 * Reading text from file
	 * @param path - path to file
	 * @return text from file (if exists), but else return null
	 */
	public static String readFile(String path) {
		String string = "";
		try {
			byte[] all = Files.readAllBytes(Paths.get(path));
			string = new String(all);
			return string;
		} catch (IOException e) {
			return null;
		}
	}
	
	
	public static String protectedFilePath = USER_DIRECTORY + "/";
	
	/**
	 * Reading text from file, but if file not exists, reading file from resource
	 * @param path - path to file
	 * @return text from file
	 */
	public static String readProtectedFile(String path) {
		try {
			return new String(Files.readAllBytes(Paths.get(path)));
		} catch (IOException e) {
			return readFileInResource(protectedFilePath + "/" + path);
		}
	}
	
	/**
	 * Writing text to file
	 * @param filename - path to file
	 * @param text - text to write
	 * @throws IOException
	 */
	public static void writeFile(String filename, String text) throws IOException {
		try (FileWriter writer = new FileWriter(filename)) {
			writer.write(text);
			writer.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
