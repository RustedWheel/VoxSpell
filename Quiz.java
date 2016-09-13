import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Quiz {

	public enum quizType {
		QUIZ, REVIEW;
	}

	private quizType _type;
	private Spelling_Aid _spelling_Aid;
	private JTextField input = new JTextField();
	private JButton restart = new JButton("Restart");
	protected JButton submit = new JButton("Submit");
	private JButton close = new JButton("Main menu");
	private JButton nextLevel = new JButton("Next level");
	private JButton videoReward = new JButton("Reward");
	private JTextArea output = new JTextArea();
	private String currentWord = "";
	protected int attempts;
	private ArrayList<String> words = new ArrayList<String>();
	private ArrayList<String> previousWords;
	private int size;
	private int testNum;
	protected JFrame frame;
	private int numberCorrect;
	private ArrayList<String> previousCorrect = new ArrayList<String>();

	private int _level;
	private JLabel levelStats = new JLabel();

	public Quiz(quizType type, Spelling_Aid spelling_Aid, int level) {

		_type = type;
		_spelling_Aid = spelling_Aid;
		_level = level;

	}

	/**
	 * This method creates the JFrame that contains the quiz GUI if it does not
	 * already exist, prompts the user for a wordlist and then reads that list
	 * and starts the quiz. An error message is shown if the wordlist file is
	 * not called "wordlist" when in quiz mode or if the wordlist files are
	 * empty
	 */
	public void startQuiz() {

		if (frame == null) {
			setUp();
		}

		switch (_type) {
		case QUIZ:

			words = _spelling_Aid.readLevel(new File("NZCER-spelling-lists.txt"), _level);

			break;

		case REVIEW:
			// reads the failed file and stores the words as a list
			words = _spelling_Aid.readList(new File(".failed"));
			break;
		}

		// Displays an error message if the file is empty
		if (words.isEmpty()) {
			switch (_type) {
			case QUIZ:
				JOptionPane.showMessageDialog(new JFrame(), "Error, wordlist is empty", "Error",
						JOptionPane.ERROR_MESSAGE);
				_spelling_Aid.setVisible(true);
				frame.dispose();
				break;

			case REVIEW:
				JOptionPane.showMessageDialog(new JFrame(), "Error, no words in failed list", "Error",
						JOptionPane.ERROR_MESSAGE);
				_spelling_Aid.setVisible(true);
				frame.dispose();
				break;
			}
		} else {
			if (_type == quizType.REVIEW) {
				output.setText("Welcome to the review!\n\n");
			} else {
				output.setText("Welcome to level " + _level + " of the quiz!\n\n");
			}

			frame.setVisible(true);

			// Determines the number of words to be quizzed, which is either
			// 3 or the number of words in the list, if the list has less than 3
			// words
			size = 10;
			previousWords = new ArrayList<String>();

			numberCorrect = 0;
			testNum = 1;

			test();

		}

	}

	/**
	 * This method randomly selects a word from the wordlist that has not
	 * already been tested in the current quiz uses textToSpeech to speak out
	 * the word for the user to spell
	 */
	private void test() {

		if (testNum <= size) {
			attempts = 0;

			Random rand = new Random();
			int wordNumber = (Math.abs(rand.nextInt()) % words.size());

			currentWord = words.get(wordNumber);

			while (previousWords.contains(currentWord)) {
				wordNumber = (Math.abs(rand.nextInt()) % words.size());
				currentWord = words.get(wordNumber);
			}
			// Adds the current word to the list of quizzed words, so that it
			// cannot
			// be selected again
			previousWords.add(currentWord);

			output.append("Please spell word " + testNum + " of " + size + "\n");

			if (currentWord.contains("'")) {
				output.append("The one with an apostrophe." + "\n");
			}

			// Speaks the word selected
			previousCorrect.add(currentWord);
			_spelling_Aid.textToSpeech(previousCorrect);
			previousCorrect.clear();

		} else {
			// Once the quiz is done, then the restart button is enabled
			_spelling_Aid.textToSpeech(previousCorrect);
			previousCorrect.clear();

			if (numberCorrect < 9) {
				JOptionPane.showMessageDialog(new JFrame(),
						"You have gotten " + numberCorrect
								+ " words correct out of 10, please press restart to try again",
						"Failure", JOptionPane.ERROR_MESSAGE);

				_spelling_Aid.appendList(_level, numberCorrect);

			} else {
				JOptionPane.showMessageDialog(new JFrame(),
						"You have gotten " + numberCorrect
								+ " words correct out of 10, you may choose to play a video reward, or proceed directly to the next level",
						"Pass", JOptionPane.INFORMATION_MESSAGE);

				_spelling_Aid.appendList(_level, numberCorrect);

				videoReward.setEnabled(true);
				nextLevel.setEnabled(true);
			}

			updateLevelResult(LevelStats());

			restart.setEnabled(true);
			output.append("\nQuiz complete.\nPress Restart to start another quiz\nPress Main menu to exit\n");
		}

	}

	/**
	 * This method creates the JFrame to display the quiz on and also sets up
	 * the buttons with their ActionListeners
	 */
	private void setUp() {

		if (_type == quizType.QUIZ) {
			frame = new JFrame("Quiz");
		} else {
			frame = new JFrame("Review");
		}

		frame.setSize(400, 450);
		frame.setLocationRelativeTo(null);

		// The quiz JFrame is disposed and the main menu is unhidden once the
		// user
		// chooses to go back
		close.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				_spelling_Aid.setVisible(true);
			}

		});

		submit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				submit.setEnabled(false);
				// Checks that the user's input in the JTextField is spelled
				// correctly
				boolean correct = spellcheck(input.getText().toLowerCase());

				if (correct) {
					previousCorrect.add("Correct");
					output.append("Correct\n");
					/* _spelling_Aid.appendList(currentWord, attempts, true); */
				} else {
					if (attempts == 1) {
						// If they have one failed attempt, then they are
						// allowed to spell the word again
						ArrayList<String> text = new ArrayList<String>();
						text.add("Incorrect, please try again");
						output.append("Incorrect, please try again\n");
						text.add(currentWord);
						text.add(currentWord);
						_spelling_Aid.textToSpeech(text);

					} else {
						// Once they fail two times, the word is considered
						// failed
						previousCorrect.add("Incorrect");

						output.append("Incorrect\n");
						_spelling_Aid.appendFailed(currentWord, _level);

						// If the user is in review mode, they are given an
						// opportunity to hear the
						// word being spelled out and then allowed to spell it
						// again
						if (_type == quizType.REVIEW) {
							_spelling_Aid.textToSpeech(previousCorrect);
							previousCorrect.clear();

							int choice = JOptionPane.showConfirmDialog(null,
									"Would you like to hear the spelling of the word and try again?", "Retry?",
									JOptionPane.YES_NO_OPTION);

							if (choice == JOptionPane.YES_OPTION) {

								_spelling_Aid.spellOut(currentWord);

								String retry = JOptionPane.showInputDialog("Please spell the word again");

								if (retry != null) {

									correct = spellcheck(retry.toLowerCase());

									if (correct) {
										previousCorrect.add("Correct");
										output.append("Correct\n");

									} else {

										previousCorrect.add("Incorrect");
										output.append("Incorrect\n");

									}

									attempts--;

								}
							}
						}

					}
				}

				// If the user correctly spells a word, it is removed from their
				// failed list
				if (correct) {
					_spelling_Aid.removeWord(currentWord);
					numberCorrect++;
				}

				// Clears the JTextField
				input.setText("");

				// Goes to the next word once the user gets the current word
				// correct
				// or fails twice
				if (correct || attempts == 2) {
					testNum++;
					test();
				}
			}

		});

		restart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Starts a new quiz and disables the restart button
				startQuiz();
				nextLevel.setEnabled(false);
				videoReward.setEnabled(false);
				restart.setEnabled(false);
			}

		});

		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		frame.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				// unhides the main menu when the x button is pressed
				_spelling_Aid.setVisible(true);
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

		});

		videoReward.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				videoReward.setEnabled(false);
				VideoPlayer video = new VideoPlayer(Quiz.this);
				frame.setVisible(false);
			}

		});

		nextLevel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				nextLevel.setEnabled(false);
				videoReward.setEnabled(false);
				restart.setEnabled(false);

				_level++;
				updateLevelResult(LevelStats());
				startQuiz();
			}

		});

		frame.setResizable(false);

		JPanel panel = new JPanel(new BorderLayout());
		JPanel options = new JPanel();

		// Disables editing of the JTextArea
		output.setEditable(false);
		output.setLineWrap(true);

		JScrollPane scroll = new JScrollPane(output);

		input.setPreferredSize(new Dimension(250, 30));

		submit.setEnabled(false);

		if (_type.equals(quizType.QUIZ)) {
			panel.add(levelStats, BorderLayout.NORTH);
			updateLevelResult(LevelStats());
		}

		panel.add(input, BorderLayout.CENTER);
		panel.add(submit, BorderLayout.EAST);

		options.add(close, JPanel.LEFT_ALIGNMENT);
		options.add(restart, JPanel.RIGHT_ALIGNMENT);
		options.add(nextLevel);
		options.add(videoReward);

		nextLevel.setEnabled(false);
		videoReward.setEnabled(false);
		restart.setEnabled(false);

		frame.add(panel, BorderLayout.NORTH);
		frame.add(scroll, BorderLayout.CENTER);
		frame.add(options, BorderLayout.SOUTH);

		/*
		 * List<String> commandLine = new ArrayList<String>();
		 * commandLine.add("festival"); commandLine.add("(voice.list)");
		 * commandLine.add("(quit)");
		 * 
		 * ProcessBuilder builder = new ProcessBuilder(commandLine);
		 * builder.redirectErrorStream(true);
		 * 
		 * try { Process b = builder.start(); OutputStream os =
		 * b.getOutputStream(); InputStream is = b.getInputStream();
		 * InputStreamReader isr = new InputStreamReader(is); BufferedReader br
		 * = new BufferedReader(isr);
		 * 
		 * String line; while ((line = br.readLine()) != null) {
		 * output.append(line + "\n"); }
		 * 
		 * b.waitFor(); if(b.exitValue() == 0) { BufferedWriter bw = new
		 * BufferedWriter(new OutputStreamWriter(os));
		 * bw.write("SELECT * FROM clients;"); bw.newLine(); bw.flush();
		 * output.append("Content of " + dir + ":\n"); String line; while ((line
		 * = br.readLine()) != null) { output.append("	" + line + "\n"); }
		 * output.append("\n"); } else { String message = br.readLine();
		 * JOptionPane.showMessageDialog(new JFrame(), message, "Message",
		 * JOptionPane.INFORMATION_MESSAGE); }
		 * 
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

	}

	/**
	 * This method returns a boolean that represents if the user correctly
	 * spelled a word, and also increments the number of attempts that the user
	 * has used
	 * 
	 * @param text
	 *            The user's attempt at the current word
	 * @return
	 */
	protected boolean spellcheck(String text) {
		attempts++;
		return text.equals(currentWord.toLowerCase());
	}

	private int[] LevelStats() {

		ArrayList<String> results = _spelling_Aid.readList(new File(".results"));
		int[] levelResult = new int[3];

		for (String result : results) {
			String[] split = result.split(" ");

			if (split[0].equals("Level" + _level)) {
				
				int score = Integer.parseInt(split[1]);
				
				if(score >= 9){
					levelResult[0]++;
				} else {
					levelResult[1]++;
				}
				
				levelResult[2] = levelResult[2] + score;
/*				switch (score) {
				case :
					levelResult[0]++;
					break;

				case "failed":
					levelResult[1]++;
					break;
				}*/

			}
		}

		return levelResult;
	}

	private void updateLevelResult(int[] levelResult) {
		if (_type.equals(quizType.QUIZ)) {
			int total = levelResult[0] + levelResult[1];
			double average = 0;
			
			if(total > 0){
				average = (double) levelResult[2] / total;
			}
			
			levelStats.setText("Level " + _level + ":  " + "mastered - " + levelResult[0] + "/" + total + "  failed - "
					+ levelResult[1] + "/" + total + "  Average Score - " + average);
		}
	}

}
