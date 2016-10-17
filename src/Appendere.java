package utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Appendere {

	FileContentReader reader = new FileContentReader();
	
	public Appendere(){}
	
	/**
	 * This method appends a level and the score for that level to the results file
	 * 
	 * Modified code from A2
	 * 
	 * @param level
	 * @param score
	 */
	public void appendList(int level, int score) {

		BufferedWriter bw = null;

		try {
			// Opens the .results file for appending
			bw = new BufferedWriter(new FileWriter(".results", true));

			// Writes the level and score to the results file
			bw.write("Level" + level + " " + score);
			bw.newLine();

		} catch (IOException e) {

		} finally {
			try {
				bw.close();
			} catch (IOException e) {

			}
		}

	}

	/**
	 * This method appends a word to the failed file if the word is not already
	 * on the failed file
	 * 
	 * Modified code from A2
	 * 
	 * @param currentWord the word to append to the failed file
	 */
	public void appendFailed(String currentWord, int level) {
		ArrayList<String> failed = reader.readList(new File(".failed"));

		//The current word contains both the word as well as the level it is from
		currentWord = currentWord + "	" + level;
		// If the failed list does not contain the word to be added, then it is
		// added
		if (!failed.contains(currentWord)) {

			BufferedWriter bw = null;

			try {
				bw = new BufferedWriter(new FileWriter(".failed", true));
				bw.write(currentWord);
				bw.newLine();
				bw.close();
			} catch (IOException e) {

			}

		}

	}

	/**
	 * This method removes a word from the failed list if it exists there, it is
	 * intended to be called once the user correctly spells a word
	 * 
	 * Reused A2 code
	 * 
	 * @param currentWord The word to remove from the failed file
	 */
	public void removeWord(String currentWord) {

		ArrayList<String> failed = reader.readList(new File(".failed"));

		BufferedWriter bw = null;

		try {
			bw = new BufferedWriter(new FileWriter(".failed"));

			for (String word : failed) {
				if (!word.equals(currentWord)) {
					bw.write(word);
					bw.newLine();
				}
			}

			bw.close();
		} catch (IOException e) {

		}

	}

}
