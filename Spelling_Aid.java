import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

@SuppressWarnings("serial")
public class Spelling_Aid extends JFrame {

	private JButton quiz = new JButton("New Spelling Quiz");
	private JButton review = new JButton("Review Mistakes");
	private JButton statistics = new JButton("View Statistics");
	private JButton clear = new JButton("Clear Statistics");
	private JTextArea txtOutput = new JTextArea(10, 20);
	private Quiz _quiz;
	private Statistics _statistics;
	private String[] levels = { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5", "Level 6", "Level 7", "Level 8",
			"Level 9", "Level 10", "Level 11" };
	private JComboBox selectLV = new JComboBox(levels);
	private JComboBox selectVoices;
	private int _level = 1;
	private ArrayList<String> _availableVoices = new ArrayList<String>();
	private String _voicePath = "/usr/share/festival/voices";
	private JButton exit = new JButton("Exit");
	private JButton changeVoice = new JButton("Change voice");
	protected String _selectedVoice;

	public static void main(String[] Args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Spelling_Aid frame = new Spelling_Aid();
				try {
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
					SwingUtilities.updateComponentTreeUI(frame);
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
						| UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}
				// Creates the frame and displays it
				frame.setVisible(true);
			}
		});
	}

	/**
	 * This method turns a list of words to be spoken into speech by calling
	 * festival using bash
	 * 
	 * @param texts
	 *            The list of strings to be spoken in order
	 */
	public void textToSpeech(ArrayList<String> texts) {

		ArrayList<String> commands = new ArrayList<String>();

		// Creates a list of commands for the SwingWorker to execute
		for (String text : texts) {
			commands.add("echo " + "\"" + text + "\" | festival --tts");
			System.out.println(text);
		}
		// Creates a new SwingWorker and executes the commands
		Speaker worker = new Speaker(commands);
		worker.execute();

	}

	/**
	 * This method reads out a word and then the letters of that word
	 * individually
	 * 
	 * @param word
	 *            the word to split into individual characters and read out
	 */
	public void spellOut(String word) {

		char[] letters = word.toCharArray();

		ArrayList<String> texts = new ArrayList<String>();

		texts.add(word);

		for (int i = 0; i < letters.length; i++) {
			texts.add(letters[i] + "");
		}

		textToSpeech(texts);

	}

	/**
	 * This method executes a single bash command through the use of a Process
	 * 
	 * @param command
	 *            The command to be executed using bash
	 */
	public static void bashCommand(String command) {

		ProcessBuilder a = new ProcessBuilder("bash", "-c", command);
		a.redirectErrorStream(true);

		try {
			Process b = a.start();
			b.waitFor();
		} catch (IOException | InterruptedException e) {
		}
	}

	/**
	 * This method reads each line in a file and stores each line into an
	 * ArrayList of strings and returns the ArrayList
	 * 
	 * @param file
	 *            The name of the file to be read
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

	public ArrayList<String> readLevel(File file, int level) {

		ArrayList<String> results = new ArrayList<String>();

		int next = level + 1;

		String levelString = "%Level " + level;
		String nextLevel = "%Level " + next;

		try {

			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			String line;
			while ((line = br.readLine()) != null && !line.equals(levelString)) {
			}

			line = br.readLine();

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

	public Spelling_Aid() {
		super("Spelling Aid");
		setSize(400, 400);
		GridLayout layout = new GridLayout(2, 2);

		JPanel menu = new JPanel();
		menu.setLayout(layout);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				bashCommand("> ~/.festivalrc");
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

		selectLV.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JComboBox lv = (JComboBox) evt.getSource();
				String selectedlv = (String) lv.getSelectedItem();
				String[] level = selectedlv.split(" ");
				_level = Integer.parseInt(level[1]);
			}
		});

		quiz.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				int response = JOptionPane.showConfirmDialog(null, selectLV, "Please select a level", JOptionPane.OK_CANCEL_OPTION);

				if (response == JOptionPane.OK_OPTION) {
					// Starts a new quiz and hides the main menu
					_quiz = new Quiz(Quiz.quizType.QUIZ, Spelling_Aid.this, _level);
					setVisible(false);
					_quiz.startQuiz();
				}
			}

		});
		review.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Starts a new review and hides the main menu
				_quiz = new Quiz(Quiz.quizType.REVIEW, Spelling_Aid.this, 0);
				setVisible(false);
				_quiz.startQuiz();

			}

		});
		statistics.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Shows the statistics and hides the main menu
				_statistics = new Statistics(Spelling_Aid.this);
				setVisible(false);
				_statistics.showStats();
			}

		});
		clear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// Prompts the user if they are sure that they want to clear
				// their statistics
				int choice = JOptionPane.showConfirmDialog(null, "Are you sure you wish to clear all statistics?",
						"Clear statistics", JOptionPane.YES_NO_OPTION);

				if (choice == JOptionPane.YES_OPTION) {
					// If they choose yes then their statistics are cleared and
					// a message is displayed telling them so
					clearStatistics();

					JOptionPane.showMessageDialog(new JFrame(), "Successfully cleared statistics", "Cleared Statistics",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					// If "no" is chosen then that is shown to them
					JOptionPane.showMessageDialog(new JFrame(), "Did not clear statistics", "Statistics",
							JOptionPane.INFORMATION_MESSAGE);
				}

			}

		});

		for(String directory : listDirectories(_voicePath)){
			String[] subDirectories = listDirectories(_voicePath + "/" + directory);
			for(String voices : subDirectories){
				_availableVoices.add(voices);
			}
		}

		selectVoices = new JComboBox(_availableVoices.toArray());

		selectVoices.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JComboBox voice = (JComboBox) evt.getSource();
				String selectedvoice = (String) voice.getSelectedItem();
				_selectedVoice = selectedvoice;
			}
		});

		txtOutput.setText(
				"Welcome to the Spelling Aid!\n\nPress \"New Quiz\" to start a new quiz\nPress \"Review\" to review previously failed words\nPress \"View Statistics\" to view your current statistics\nPress \"Clear Statistics\" to clear all current statistics\nPress \"Change voice\" to change the text to speech voice");
		txtOutput.setEditable(false);

		menu.add(quiz);
		menu.add(review);
		menu.add(statistics);
		menu.add(clear);

		JPanel options = new JPanel();


		exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}

		});

		changeVoice.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int response = JOptionPane.showConfirmDialog(null, selectVoices, "Please select a voice to use", JOptionPane.OK_CANCEL_OPTION);
				if (response == JOptionPane.OK_OPTION) {
					setVoice(_selectedVoice);
				}
			}

		});
		options.add(changeVoice);
		options.add(exit);

		add(txtOutput, BorderLayout.NORTH);
		add(menu);
		add(options, BorderLayout.SOUTH);

		setResizable(false);
		setLocationRelativeTo(null);

	}

	/**
	 * This method deletes the existing results and failed files and then
	 * creates new ones that are empty
	 */
	protected void clearStatistics() {

		File file = new File(".results");

		file.delete();
		try {
			file.createNewFile();
		} catch (IOException e) {

		}

		file = new File(".failed");

		file.delete();
		try {
			file.createNewFile();
		} catch (IOException e) {

		}

	}

	/**
	 * This method appends a word and the grade for that word to the results
	 * file
	 * 
	 * @param currentWord
	 *            The word to append
	 * @param attempts
	 *            How many attempts the user had on the currentWord
	 * @param correct
	 *            A boolean value representing if the user correctly spelled the
	 *            word
	 */
	public void appendList(int level, int score) {

		BufferedWriter bw = null;

		try {
			// Opens the .results file for appending
			bw = new BufferedWriter(new FileWriter(".results", true));

			/*
			 * // If they answered correctly in 1 attempt then that word is
			 * mastered if (attempts == 1) { bw.write(currentWord + " mastered"
			 * ); bw.newLine(); // If they answered correctly in 2 attempts then
			 * that word is faulted } else if (attempts == 2 && correct) {
			 * bw.write(currentWord + " faulted"); bw.newLine(); // If they
			 * failed both attempts then that word is failed, and also added //
			 * to the failed list } else { bw.write(currentWord + " failed");
			 * bw.newLine(); appendFailed(currentWord); }
			 */

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
	 * @param currentWord
	 *            The word to append to the failed file
	 */
	public void appendFailed(String currentWord, int level) {
		ArrayList<String> failed = readList(new File(".failed"));

		// If the failed list does not contain the word to be added, then it is
		// added
		if (!failed.contains(currentWord)) {

			BufferedWriter bw = null;

			currentWord = currentWord + "	" + level;
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
	 * @param currentWord
	 *            The word to remove from the failed file
	 */
	public void removeWord(String currentWord) {

		ArrayList<String> failed = readList(new File(".failed"));

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

	/**
	 * This is a private SwingWorker class that executes a list of bash commands
	 * sequentially on a worker thread
	 * 
	 * @author Hunter
	 *
	 */
	class Speaker extends SwingWorker<Void, Void> {

		private ArrayList<String> _commands;

		public Speaker(ArrayList<String> commands) {
			_commands = commands;
		}

		@Override
		protected Void doInBackground() throws Exception {
			for (String command : _commands) {
				Spelling_Aid.bashCommand(command);
			}
			return null;
		}

		// If the quiz is complete, then disable the submit button, otherwise
		// re-enable the submit button
		protected void done() {
			if (_quiz.attempts != 2) {
				_quiz.submit.setEnabled(true);
			} else {
				_quiz.submit.setEnabled(false);
			}
		}

	}

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

	public void setVoice(String voice){
		String command = null;

		command = "echo " + "\"(set! voice_default '" + "voice_" + voice + ")\""  + "> ~/.festivalrc";

		bashCommand(command);
	}
}