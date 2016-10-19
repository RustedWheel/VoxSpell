import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import utility.Appendere;
import utility.FileContentReader;

@SuppressWarnings("serial")
public class Quiz extends JPanel{

	public enum quizType {
		QUIZ, REVIEW;
	}

	private quizType _type;
	private Gui frame;
	private Appendere appendere  = new Appendere();
	private JTextField input = new JTextField();
	private JButton restart = new JButton("Restart");
	protected JButton submit = new JButton("Submit");
	private JButton close = new JButton("Main Menu");
	private JButton changeVoice = new JButton("Change Voice",new ImageIcon(Quiz.class.getResource("/img/voice.png")));
	private JButton nextLevel = new JButton("Next level");
	private JButton videoReward = new JButton("Video Reward");
	private JButton Reward = new JButton("Rewards");
	private JTextPane output = new JTextPane();;
	private String currentWord = "";
	protected int attempts;
	private ArrayList<String> words = new ArrayList<String>();
	private ArrayList<String> previousWords;
	private ArrayList<String> incorrectWords = new ArrayList<String>();
	private int size;
	private int testNum;
	private int numberCorrect;
	private ArrayList<String> previousCorrect = new ArrayList<String>();
	protected JButton repeat = new JButton("Repeat");
	private String _file;
	private JButton Audio = new JButton("Audio Reward");
	private int _level;
	private JLabel levelStats = new JLabel();
	protected boolean correct;
	private JDialog dialog;
	FileContentReader reader = new FileContentReader();
	private int _maxLevel;	
	private boolean _finished = false;
	@SuppressWarnings("rawtypes")
	protected JComboBox selectVoices;
	private static DecimalFormat df = new DecimalFormat("#.##");

	public Quiz(quizType type, Gui guiFrame, int level, int maxLevel, String file) {
		_type = type;
		frame = guiFrame;
		_level = level;
		_file = file;
		_maxLevel = maxLevel;
		
	}

	/**
	 * This method creates the JFrame that contains the quiz GUI if it does not
	 * already exist, prompts the user for a wordlist and then reads that list
	 * and starts the quiz. An error message is shown if the wordlist file is
	 * not called "wordlist" when in quiz mode or if the wordlist files are
	 * empty
	 * 
	 * Modified A2 code
	 */
	public void startQuiz() {

		if (this.getComponentCount() == 0) {
			setUp();
		}

		switch (_type) {
		case QUIZ:

			words = reader.readLevel(new File(_file), _level);

			break;

		case REVIEW:
			// reads the failed file and stores the words as a list
			ArrayList<String> allFailed = reader.readList(new File(".failed"));
			words.clear();

			for (String word : allFailed) {
				String[] split = word.split("\t");
				if (Integer.parseInt(split[1]) == _level) {
					words.add(split[0]);
				}
			}

			break;
		}

		// Displays an error message if the file is empty
		if (words.isEmpty()) {
			switch (_type) {
			case QUIZ:
				JOptionPane.showMessageDialog(new JFrame(), "Error, no words in level " + _level, "Error",
						JOptionPane.ERROR_MESSAGE);
				
				break;

			case REVIEW:
				JOptionPane.showMessageDialog(new JFrame(), "Error, no failed words saved for level " + _level, "Error",
						JOptionPane.ERROR_MESSAGE);
				
				/*frame.dispose();*/
				break;
			}
		} else {
			if (_type == quizType.REVIEW) {
				appendToOutput("Welcome to the review!\n\n",new Color(52, 80, 101),true);
			} else {
				// Prints the level of the quiz
				appendToOutput("Welcome to level " + _level + " of the quiz!\n\n",new Color(52, 80, 101),true);
			}

			/*frame.setVisible(true);*/

			// Determines the number of words to be quizzed, which is either
			// 10 or the number of words in the list, if the list has less than 10
			// words
			size = words.size() < 10 ? words.size() : 10;
			previousWords = new ArrayList<String>();

			numberCorrect = 0;
			testNum = 0;
			updateLevelResult();
			testNum = 1;
			test();

		}

	}

	/**
	 * This method randomly selects a word from the wordlist that has not
	 * already been tested in the current quiz uses textToSpeech to speak out
	 * the word for the user to spell
	 * 
	 * Reused A2 code
	 */
	private void test() {

		if (testNum <= size) {
			correct = false;
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

			appendToOutput("Please spell word " + testNum + " of " + size + "\n\n",new Color(51, 47, 47),true);

			// If the word contains an apostrophe then the user is told that
			if (currentWord.contains("'")) {
				appendToOutput("The one with an apostrophe." + "\n\n",new Color(51, 47, 47),false);
			}

			// Speaks the word selected
			previousCorrect.add("Please spell the word, ");
			previousCorrect.add(currentWord);
			frame.getSpeaker().textToSpeech(previousCorrect);
			previousCorrect.clear();

		} else {
			// Once the quiz is done, then the restart button is enabled
			frame.getSpeaker().textToSpeech(previousCorrect);
			previousCorrect.clear();
			_finished = true;

			if (_type == quizType.QUIZ) {
				// The user is given an option to see the words that they failed if they do not pass a level
				// Original code by Hunter
				if (numberCorrect < 9) {
					int response = JOptionPane.showConfirmDialog(new JFrame(),
							"You have gotten " + numberCorrect
							+ " words correct out of 10, would you like to see the words that you spelled incorrectly?",
							"Failure", JOptionPane.YES_NO_OPTION);

					if (response == JOptionPane.YES_OPTION) {

						JTextArea wrongWords = new JTextArea();

						for (String word : incorrectWords) {
							wrongWords.append(word + "\n");
						}

						wrongWords.setEditable(false);

						JScrollPane words = new JScrollPane(wrongWords);

						JOptionPane.showMessageDialog(null, words,
								"Your failed words", JOptionPane.INFORMATION_MESSAGE);
					}

					appendere.appendList(_level, numberCorrect);
					appendToOutput("\nQuiz complete.\nPress Restart to start another quiz\nPress Main menu to exit\n\n",new Color(51, 47, 47),false);

				} else {

					appendere.appendList(_level, numberCorrect);

					// If they pass the level then the user can move on to the next level or play a video reward
					// If they are on the last level, then they are able to play the bonus video reward
					Reward.setEnabled(true);
					if (_level < _maxLevel) {
						JOptionPane.showMessageDialog(new JFrame(),
								"You have gotten " + numberCorrect
								+ " words correct out of 10, you may choose to play a video or audio reward, or choose the next level to proceed",
								"Pass", JOptionPane.INFORMATION_MESSAGE);
						nextLevel.setEnabled(true);
						appendToOutput("\nQuiz complete\nPress Restart to start another quiz on the current level\nPress Next Level to select the next level to proceed\nPress Main menu to exit\n",new Color(51, 47, 47),false);
					} else {
						JOptionPane.showMessageDialog(new JFrame(),
								"You have gotten " + numberCorrect
								+ " words correct out of 10, you may choose to play the bonus video reward! You have passed the final level, congratulations!",
								"Pass", JOptionPane.INFORMATION_MESSAGE);
						appendToOutput("\nQuiz complete\nYou have unlocked the bonus video reward\nPress Restart to start another quiz on the current level\nPress Main Menu to exit\n\n",new Color(51, 47, 47),false);
					}
					
				}

			}
			restart.setEnabled(true);
		}

	}

	/**
	 * This method creates the JFrame to display the quiz on and also sets up
	 * the buttons with their ActionListeners
	 * 
	 * Modified A2 code
	 */
	private void setUp() {

        setLayout(new BorderLayout());
		frame.getSpeaker().setQuiz(this);
		levelStats.setFont(new Font("SansSerif", Font.PLAIN, 16));
		EmptyBorder border = new EmptyBorder(new Insets(10,10,10,10));
		output.setBorder(border);
		output.setMargin(new Insets(5,5,5,5));

		// The quiz JFrame is disposed and the main menu is unhidden once the
		// user
		// chooses to go back
		close.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int choice = JOptionPane.showConfirmDialog(null, "Leave the current quiz and return to the main menu?",
						"Exit quiz", JOptionPane.YES_NO_OPTION);
				if (choice == JOptionPane.YES_OPTION) {
					frame.getContentPane().removeAll();
					MainMenu menu = new MainMenu(frame);
					frame.getContentPane().add(menu);
					frame.revalidate();
					frame.repaint();
				}
			}

		});

		submit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				submit.setEnabled(false);
				repeat.setEnabled(false);
				// Checks that the user's input in the JTextField is spelled
				// correctly
				correct = spellcheck(input.getText().toLowerCase());

				if (correct) {
					previousCorrect.add("Correct");
					appendToOutput("Correct ✓\n\n",new Color(44,115,58),false);
					/* _spelling_Aid.appendList(currentWord, attempts, true); */
				} else {
					if (attempts == 1) {
						// If they have one failed attempt, then they are
						// allowed to spell the word again
						ArrayList<String> text = new ArrayList<String>();
						text.add("Incorrect, please try again");
						appendToOutput("Incorrect, please try again X\n\n",new Color(148,48,48),false);
						text.add(currentWord);
						text.add(currentWord);
						frame.getSpeaker().textToSpeech(text);

					} else {
						// Once they fail two times, the word is considered
						// failed
						previousCorrect.add("Incorrect");

						appendToOutput("Incorrect X\n\n",new Color(148,48,48),false);
						appendere.appendFailed(currentWord, _level);

						// If the user is in review mode, they are given an
						// opportunity to hear the
						// word being spelled out and then allowed to spell it
						// again
						if (_type == quizType.REVIEW) {
							frame.getSpeaker().textToSpeech(previousCorrect);
							previousCorrect.clear();

							int choice = JOptionPane.showConfirmDialog(null,
									"Would you like to hear the spelling of the word and try again?", "Retry?",
									JOptionPane.YES_NO_OPTION);

							if (choice == JOptionPane.YES_OPTION) {

								frame.getSpeaker().spellOut(currentWord);

								String retry = JOptionPane.showInputDialog("Please spell the word again");

								if (retry != null) {

									correct = spellcheck(retry.toLowerCase());

									if (correct) {
										previousCorrect.add("Correct");
										appendToOutput("Correct ✓\n\n",new Color(44,115,58),false);

									} else {

										previousCorrect.add("Incorrect");
										appendToOutput("Incorrect X\n\n",new Color(148,48,48),false);

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
					appendere.removeWord(currentWord + "\t" + _level);
					numberCorrect++;
				}

				// Clears the JTextField
				input.setText("");

				// Goes to the next word once the user gets the current word
				// correct
				// or fails twice
				if (correct || attempts == 2) {
					updateLevelResult();
					testNum++;
					if (!correct) {
						incorrectWords.add(currentWord);
					}
					test();
				}

			}

		});

		restart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Starts a new quiz and disables the restart button
				incorrectWords.clear();
				output.setText("");
				startQuiz();
				nextLevel.setEnabled(false);
				Reward.setEnabled(false);
				restart.setEnabled(false);
				_finished = false;
			}

		});
		
		selectVoices = frame.getSpeaker().getVoiceBox();
		changeVoice.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				int response = JOptionPane.showConfirmDialog(null, selectVoices, "Please select a voice to use", JOptionPane.OK_CANCEL_OPTION);
				if (response == JOptionPane.OK_OPTION) {
					frame.getSpeaker().setVoice(frame.getSpeaker().getSelectVoice());
				}
			}

		});
		
		// Sets up the listeners for reward and next level buttons if the quiz type is quiz
		if (_type == quizType.QUIZ) {
			
			videoReward.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					
					if (_level < _maxLevel) {
						@SuppressWarnings("unused")
						VideoPlayer video = new VideoPlayer("big_buck_bunny_1_minute.avi");
					} else {
						// The bonus video is played if the level is the final level
						@SuppressWarnings("unused")
						VideoPlayer video = new VideoPlayer("bonus_reward.avi");
					}
					/*frame.setVisible(false);*/
					Reward.setEnabled(false);
					dialog.setVisible(false);
				}

			});
			
			Audio.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					
					@SuppressWarnings("unused")
					VideoPlayer video = new VideoPlayer("audioReward.avi");
					
					Reward.setEnabled(false);
					dialog.setVisible(false);
				}

			});
			
			
			final Object[] RewardOptions = {videoReward, Audio};
			JOptionPane messagePane = new JOptionPane(
				    RewardOptions,
		            JOptionPane.INFORMATION_MESSAGE);
			dialog = messagePane.createDialog(null, "Rewards");
			
			Reward.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					dialog.setVisible(true);
				}

			});
			
			//Select the next level to go to
			nextLevel.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					output.setText("");
					nextLevel.setEnabled(false);
					Reward.setEnabled(false);
					restart.setEnabled(false);
					_finished = false;
					incorrectWords.clear();
					_level++;
					startQuiz();
					
				}

			});
			
		}

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(new Color(220,221,225));
		JPanel options = new JPanel();

		JScrollPane scroll = new JScrollPane(output);

		input.setPreferredSize(new Dimension(250, 30));
		submit.setEnabled(false);

		JPanel quizOptions = new JPanel();
		quizOptions.setBackground(new Color(220,221,225));
		options.setBackground(new Color(220,221,225));
		
		quizOptions.add(submit);

		// Plays the word once more once clicked
		repeat.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				repeat.setEnabled(false);
				submit.setEnabled(false);
				ArrayList<String> text = new ArrayList<String>();
				text.add("repeat " + currentWord);
				frame.getSpeaker().textToSpeech(text);
			}

		});

		repeat.setEnabled(false);
		
		quizOptions.add(repeat);
		panel.add(input, BorderLayout.CENTER);
		panel.add(quizOptions, BorderLayout.EAST);
		options.add(close, JPanel.LEFT_ALIGNMENT);
		options.add(changeVoice);
		options.add(restart, JPanel.RIGHT_ALIGNMENT);
		
		// If the quiz type is quiz, then the user's stats for the level are shown
		// Original code by David
		// If the quiz type is quiz, then the options for the quiz include a next level button and
		// video reward button
		// by Hunter
		if (_type == quizType.QUIZ) {
			panel.add(levelStats, BorderLayout.NORTH);
			options.add(nextLevel);
			options.add(Reward);
			/*options.add(videoReward);*/
		}

		nextLevel.setEnabled(false);
		/*Reward.setEnabled(false);*/
		restart.setEnabled(false);

		add(panel, BorderLayout.NORTH);
		add(scroll, BorderLayout.CENTER);
		add(options, BorderLayout.SOUTH);

		// Sets the submit button as the default one so that the enter button can be used to submit
		frame.getRootPane().setDefaultButton(submit);

	}

	/**
	 * This method returns a boolean that represents if the user correctly
	 * spelled a word, and also increments the number of attempts that the user
	 * has used
	 * 
	 * Reused A2 code
	 * 
	 * @param text The user's attempt at the current word
	 * @return
	 */
	protected boolean spellcheck(String text) {
		attempts++;
		return text.toLowerCase().equals(currentWord.toLowerCase());
	}

	/**
	 * This method updates the text on the quiz window showing the user's stats for the level
	 * Original code by David
	 */
	private void updateLevelResult() {
		if (_type.equals(quizType.QUIZ)) {
			String feedback = "Level " + _level + ":  " + "Correct - " + numberCorrect + "/" + testNum + "  Incorrect - "
					+ (testNum - numberCorrect) + "/" + testNum;
			if(testNum != 0 ){
				feedback = feedback + "  Accuracy - " + df.format(((double) numberCorrect / testNum) * 100) + "%";
			} else {
				feedback = feedback + "  Accuracy - " + "0.00%";
			}
			
			levelStats.setText(feedback);
		}
	}
	
	/**
	 * This method appends text onto the output
	 */
	private void appendToOutput(String msg, Color c, boolean bold){
		output.setEditable(true);
		
		StyleContext style = StyleContext.getDefaultStyleContext();
		AttributeSet set = style.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
		
		set = style.addAttribute(set,StyleConstants.FontFamily,"SansSerif");
		set = style.addAttribute(set,StyleConstants.Alignment,StyleConstants.ALIGN_LEFT);
		
		if(bold == true){
			set = style.addAttribute(set,StyleConstants.Bold,new Boolean(true));
		} else {
			set = style.addAttribute(set,StyleConstants.Bold,new Boolean(false));
		}
		
		if(msg.contains("Welcome") && (msg.contains("level") || (msg.contains("review")))){
			set = style.addAttribute(set,StyleConstants.FontSize,18);
		} else {
			set = style.addAttribute(set,StyleConstants.FontSize,16);
		}
		
		int length = output.getDocument().getLength();
		output.setCaretPosition(length);
		output.setCharacterAttributes(set, false);
		output.replaceSelection(msg);
		
		output.setEditable(false);
	}
	
	public boolean isQuizFinished(){
		return _finished;
	}
}
