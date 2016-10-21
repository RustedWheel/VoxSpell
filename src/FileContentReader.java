package utility;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

public class FileContentReader {

	public FileContentReader(){}
	
	/**
	 * This method reads each line in a file and stores each line into an
	 * ArrayList of strings and returns the ArrayList
	 * 
	 * Reused code from A2
	 * 
	 * @param file The name of the file to be read
	 * @return An ArrayList containing the lines of a file in sequential order
	 */
	public ArrayList<String> readList(File file) {

		ArrayList<String> results = new ArrayList<String>();

		try {

			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			String line;
			while ((line = br.readLine()) != null) {
				results.add(line);
			}
			br.close();
			fr.close();

		} catch (IOException e1) {

		}

		return results;

	}
	
	/**
	 * This method reads all the words inside a level from a wordlist
	 * 
	 * Original code by David
	 * 
	 * @param file The wordlist containing all the levels and words
	 * @param level The level from which words are to be found
	 * @return An ArrayList containing all the words in a level
	 */
	public ArrayList<String> readLevel(File file, int level) {

		ArrayList<String> results = new ArrayList<String>();

		int next = level + 1;

		String levelString = "%Level " + level;
		String nextLevel = "%Level " + next;

		try {

			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			String line;
			// Do nothing while the level header is not found
			while ((line = br.readLine()) != null && !line.equals(levelString)) {
			}

			line = br.readLine();

			// Add the words to the ArrayList once the level is found, up to when the header for the next level is found
			// Or when the list ends
			while (line != null) {
				if (line.equals(nextLevel)) {
					break;
				}
				results.add(line);
				line = br.readLine();
			}

			br.close();
			fr.close();

		} catch (IOException e1) {

		}

		return results;

	}
	
	public ArrayList<String> readFailed(int level) {

		ArrayList<String> failedWords = new ArrayList<String>();

		try {

			FileReader fr = new FileReader(".failed");
			BufferedReader br = new BufferedReader(fr);

			String line;

			// Add the words to the ArrayList once the level is found, up to when the header for the next level is found
			// Or when the list ends
			while ((line = br.readLine()) != null) {
				String[] word = line.split("	");

				if(Integer.parseInt(word[1]) == level){
					failedWords.add(word[0]);
				}
			}

			br.close();
			fr.close();

		} catch (IOException e1) {

		}

		return failedWords;

	}
	
	/**
	 * This method returns a String array containing the names of every directory inside a directory
	 * 
	 * Original code by David
	 * 
	 * @param directory
	 * @return A list of folders inside a directory
	 */
	public String[] listDirectories(String directory){

		File file = new File(directory);
		String[] subDirectories = file.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});

		return subDirectories;
	}
}
