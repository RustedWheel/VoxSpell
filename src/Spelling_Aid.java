import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.Font;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class Spelling_Aid extends JFrame {

	private JButton quiz = new JButton("New Spelling Quiz");
	private JButton review = new JButton("Review Mistakes");
	private JButton statistics = new JButton("View Statistics");
	private JButton clear = new JButton("Clear Statistics");
	private Quiz _quiz;
	private Statistics _statistics;
	@SuppressWarnings("rawtypes")
	protected JComboBox selectLV;
	private JComboBox reviewLV;
	@SuppressWarnings("rawtypes")
	private JComboBox selectVoices;
	private int _level = 1;
	private ArrayList<String> _availableVoices = new ArrayList<String>();
	private String _voicePath = "/usr/share/festival/voices";
	private String _defaultFile = "NZCER-spelling-lists.txt";
	private String _filePath = null;
	private JButton exit = new JButton("Exit");
	private JButton changeVoice = new JButton("Change Speaker Voice");
	private JButton SelectFile = new JButton("Change Spelling List");
	protected String _selectedVoice;
	private String _voice;
	protected int maxLevel = 0;
	private int _maxSpeed = 20;
	private int _minSpeed = 5;
	private int _initSpeed = 15;
	private JSlider voiceSpeed = new JSlider(JSlider.HORIZONTAL,_minSpeed,_maxSpeed,_initSpeed);
	private double _speed;
	private String _selectedSpeed;
	private JButton settings = new JButton("Settings");
	private JButton changeSpeed = new JButton("Change Speaker Speed");
	private String _defaultSpeed = "(Parameter.set 'Duration_Stretch 1.0)";
	private String _repeatSpeed = "(Parameter.set 'Duration_Stretch 1.5)";
	private final JButton btnHelp = new JButton("Help");
	private final JFileChooser fileSelector = new JFileChooser();
	private int _minLevel;

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
	 * Modified A2 code
	 * 
	 * @param texts The list of strings to be spoken in order
	 */
	public void textToSpeech(ArrayList<String> texts) {

		BufferedWriter bw = null;

		try {

			// Creates a new scm file to write the tts speech on
			bw = new BufferedWriter(new FileWriter(".text.scm", true));

			// If the user has selected a voice, then write the voice into the scm file
			if (_voice != null) {
				bw.write(_voice);
				bw.newLine();
			}

			// For every line, specify the speed for the tts to speak at. Which is 1x for "Correct" and "Incorrect"
			// text. Otherwise it is at the speed defined by the user
			// Original code by Hunter
			for (String text : texts) {

				if ((text.equals("Correct") || text.equals("Incorrect, please try again") || text.equals("Incorrect") || text.equals("Please spell the word, "))) {
					bw.write(_defaultSpeed);
					bw.newLine();

				} else if (text.contains("repeat")) {
					bw.write(_repeatSpeed);
					bw.newLine();
					String[] textArray = text.split(" ");
					text = textArray[1];
					
				} else if (_selectedSpeed != null) {
					bw.write(_selectedSpeed);
					bw.newLine();

				}
				
				bw.write("(SayText \"" + text + "\")");
				bw.newLine();


			}


		} catch (IOException e) {

		} finally {
			try {
				bw.close();
			} catch (IOException e) {

			}
		}

		// Starts a new worker instance and executes it
		Speaker worker = new Speaker();
		worker.execute();

	}

	/**
	 * This method reads out a word and then the letters of that word
	 * individually
	 * 
	 * Reused code from A2
	 * 
	 * @param word the word to split into individual characters and read out
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
	 * Reused code from A2
	 * 
	 * @param command The command to be executed using bash
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	/*
	 * Reused A2 code
	 */
	public Spelling_Aid() {
		super("Spelling Aid");
		getContentPane().setBackground(new Color(248, 248, 255));
		this.setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setSize(400, 400);
		GridLayout layout = new GridLayout(2, 2);

		JPanel menu = new JPanel();
		menu.setLayout(layout);
		/*menu.setBounds(0, 204, 400, 158);*/
		JPanel welcomeScreen = new JPanel();
		welcomeScreen.setBackground(new Color(248, 248, 255));
		
		updateSelectLevel(_defaultFile);

		quiz.setFont(new Font("SansSerif", Font.PLAIN, 14));

		quiz.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				selectLV.setSelectedItem("Level " + _level);
				
				if(selectLV.getItemCount() == 0){
					JOptionPane.showMessageDialog(null,"The spelling list does not exist or is empty, please put the default spelling list in the working directory or select another list in the settings");
				} else {
					// Asks the user to select a level to start at once they click Start Quiz
					int response = JOptionPane.showConfirmDialog(null, selectLV, "Please select a level", JOptionPane.OK_CANCEL_OPTION);

					if (response == JOptionPane.OK_OPTION) {
						
						String file;
						
						if(_filePath == null){
							file = _defaultFile;
						} else {
							file = _filePath;
						}
						
						// Starts a new quiz and hides the main menu
						_quiz = new Quiz(Quiz.quizType.QUIZ, Spelling_Aid.this, _level, file);
						setVisible(false);
						_quiz.startQuiz();
				}
				
				}
			}

		});
		review.setFont(new Font("SansSerif", Font.PLAIN, 14));
		review.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateReviewLevel();
				
				if(reviewLV.getItemCount() == 0){
					JOptionPane.showMessageDialog(null,"The failed list is empty, please attempt the quiz");
				} else {
					
					// Asks the user to select a level to review, then starts a new review and hides the main menu
					int response = JOptionPane.showConfirmDialog(null, reviewLV, "Please select a level", JOptionPane.OK_CANCEL_OPTION);

					if (response == JOptionPane.OK_OPTION) {
						
						_quiz = new Quiz(Quiz.quizType.REVIEW, Spelling_Aid.this, _level, null);
						setVisible(false);
						_quiz.startQuiz();
					}
				}

			}

		});
		statistics.setFont(new Font("SansSerif", Font.PLAIN, 14));
		statistics.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Shows the statistics and hides the main menu
				_statistics = new Statistics(Spelling_Aid.this);
				setVisible(false);
				_statistics.showStats();
			}

		});
		clear.setFont(new Font("SansSerif", Font.PLAIN, 14));
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
		
		

		// Adds the list of available voices in /usr/share/festival/english to an Array
		// and puts it in a JComboBox
		// Original code by David
		String[] subDirectories = listDirectories(_voicePath + "/english");
		for(String voices : subDirectories){
			switch (voices) {
			case "kal_diphone":
				voices = "CMU US KAL voice";
				break;
			case "rab_diphone":
				voices = "CSTR UK RAB voice";
				break;
			case "akl_nz_jdt_diphone":
				voices = "Auckland NZ JDT voice";
				break;
			}
			_availableVoices.add(voices);
			
		}

		selectVoices = new JComboBox(_availableVoices.toArray());
		
		// Adds an ActionListener to the JComboBox to save the selected voice into the _selectedVoice field
		selectVoices.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JComboBox voice = (JComboBox) evt.getSource();
				String selectedvoice = (String) voice.getSelectedItem();
				_selectedVoice = selectedvoice;
			}
		});
		

		menu.add(quiz);
		menu.add(review);
		menu.add(statistics);
		menu.add(clear);

		JPanel options = new JPanel();
		options.setBackground(new Color(214, 217, 223));

		exit.setFont(new Font("SansSerif", Font.PLAIN, 14));


		exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}

		});

		// Configure a JSlider to change the speed of the speaker, from 0.5x speed to 2x speed
		// Original code by David/Hunter
		voiceSpeed.setMinorTickSpacing(1);
		voiceSpeed.setPaintTicks(true);
		voiceSpeed.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				if(!source.getValueIsAdjusting()){
					// _speed can range from (25-20)/10 which is 0.5 so 2x tts speed to 
					// (25-5)/10 which is 2, so 0.5x tts speed. This allows the slider to increase
					// the tts speed when slid right, as opposed to the opposite
					_speed = (25 - (double)source.getValue() )/ 10;
				}
			}

		});

		final Object[] Options = {changeVoice, changeSpeed, SelectFile };
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
		fileSelector.setFileFilter(filter);
		
		SelectFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				int result = fileSelector.showOpenDialog(SelectFile);
				if (result == JFileChooser.APPROVE_OPTION) {
				    File selectedFile = fileSelector.getSelectedFile();
				    _filePath = selectedFile.getAbsolutePath();
				    updateSelectLevel(_filePath);
				}
				
			}

		});

		// Adds an ActionListener to the change voice button to display a JOptionPane asking the user to select a voice
		changeVoice.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				int response = JOptionPane.showConfirmDialog(null, selectVoices, "Please select a voice to use", JOptionPane.OK_CANCEL_OPTION);
				if (response == JOptionPane.OK_OPTION) {
					setVoice(_selectedVoice);
				}
			}

		});

		// Adds an ActionListener to the change speed button to display a JOptionPane asking the user to select a speed
		changeSpeed.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				int response = JOptionPane.showConfirmDialog(null, voiceSpeed, "Please select a speed to use", JOptionPane.OK_CANCEL_OPTION);
				if (response == JOptionPane.OK_OPTION) {
					_selectedSpeed = "(Parameter.set 'Duration_Stretch " + _speed +")";
				}
			}

		});
		
		settings.setFont(new Font("SansSerif", Font.PLAIN, 14));

		// Adds an ActionListener to the change voice button to display a JOptionPane message allowing the user to change voice or speed
		settings.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, Options, "Settings", JOptionPane.INFORMATION_MESSAGE);
			}

		});
		
		btnHelp.setFont(new Font("SansSerif", Font.PLAIN, 14));
		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Press \"New Quiz\" to start a new quiz\nPress \"Review\" to review previously failed words\nPress \"View Statistics\" to view your current statistics\nPress \"Clear Statistics\" to clear all current statistics\nPress \"Settings\" to change the text to speech voice or speed or the spelling list", "Help", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		welcomeScreen.setLayout(new BorderLayout(0, 0));
		
		
		JLabel logoLabel = new JLabel();
		logoLabel.setBounds(0, 21, 388, 156);
		/*getContentPane().add(logoLabel);*/
		welcomeScreen.add(logoLabel, BorderLayout.CENTER);
		ImageIcon oldLogo = new ImageIcon(Spelling_Aid.class.getResource("/img/VoxSpell.png"));
		Image img = oldLogo.getImage();
		Image logo = img.getScaledInstance(logoLabel.getWidth(), logoLabel.getHeight(), Image.SCALE_SMOOTH);
		ImageIcon newLogo = new ImageIcon(logo);
		logoLabel.setIcon(newLogo);
		
		JLabel Welcome = new JLabel("Welcome to the VoxSpell Spelling Aid!");
	    Welcome.setBounds(54, 182, 294, 18);
		Welcome.setHorizontalAlignment(SwingConstants.CENTER);
		Welcome.setFont(new Font("SansSerif", Font.PLAIN, 15));
		/*getContentPane().add(Welcome);*/
		welcomeScreen.add(Welcome, BorderLayout.SOUTH);
		setLocationRelativeTo(null);
		
		options.add(btnHelp);
		options.add(settings);
		options.add(exit);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(welcomeScreen, BorderLayout.NORTH);
		getContentPane().add(menu, BorderLayout.CENTER);
		getContentPane().add(options, BorderLayout.SOUTH);

		// Clears the statistics when first started up, so past data is not saved
		/*clearStatistics();*/
	}

	/**
	 * This method deletes the existing results and failed files and then
	 * creates new ones that are empty
	 * 
	 * Reused code from A2
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
		ArrayList<String> failed = readList(new File(".failed"));

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

		public Speaker() {
		}

		@Override
		protected Void doInBackground() throws Exception {

			// Calls a bash command to run festival with the scheme file
			Spelling_Aid.bashCommand("festival -b .text.scm");

			return null;
		}

		// If the quiz is complete, then disable the submit button, otherwise
		// re-enable the submit button
		protected void done() {

			Spelling_Aid.bashCommand("rm -f .text.scm");

			if (_quiz.attempts == 2 || _quiz.correct) {
				_quiz.submit.setEnabled(false);
			} else {
				_quiz.submit.setEnabled(true);
			}
			
			// The user is allowed to hear a repeat of the word if they have not attempted the word yet
			// and they have also not repeated it before
			if (_quiz.attempts == 0 && _quiz.repeated == false) {
				_quiz.repeat.setEnabled(true);
			} else {
				_quiz.repeat.setEnabled(false);
			}
		}

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

	/**
	 * This method sets the voice field into a string that is able to be directly entered into a festival scm file
	 * 
	 * Original code by David
	 *
	 * @param voice The voice to set
	 */
	public void setVoice(String voice){
		if(voice != null){
			switch (voice) {
			case "CMU US KAL voice":
				voice = "kal_diphone";
				break;
			case "CSTR UK RAB voice":
				voice = "rab_diphone";
				break;
			case "Auckland NZ JDT voice":
				voice = "akl_nz_jdt_diphone";
				break;
			}
			_voice = "(voice_" + voice + ")";
		}
	}

	/**
	 * This method scans all of the levels inside a wordlist and also saves the maximum level inside the
	 * wordlist
	 * 
	 * Original code by David
	 * 
	 * @param wordlist The wordlist to scan the levels from
	 * @return An ArrayList of levels inside the wordlist
	 */
	public ArrayList<String> scanLevels(String wordlist){
		ArrayList<String> all = readList(new File(wordlist));
		ArrayList<String> levels = new ArrayList<String>();
		int i = 0;
		for(String content: all){
			if(content.startsWith("%Level")){
				if(i == 0){
					_minLevel = Integer.parseInt(content.split(" ")[1]);
				}
				levels.add("Level " + content.split(" ")[1]);
				if (Integer.parseInt(content.split(" ")[1]) > maxLevel) {
					maxLevel = Integer.parseInt(content.split(" ")[1]);
				}
				i++;
			}
		}
		_level = _minLevel;
		return levels;
	}
	
	public void updateSelectLevel(String file){
		// Finds out all of the levels in the word list and stores in a JComboBox
		// Original code by David
		selectLV = new JComboBox(scanLevels(file).toArray());
		
		// Adds an ActionListener to the JComboBox to extract the level and save it in the _level field
		selectLV.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JComboBox lv = (JComboBox) evt.getSource();
				String selectedlv = (String) lv.getSelectedItem();
				String[] level = selectedlv.split(" ");
				_level = Integer.parseInt(level[1]);
			}
		});
	}
	
	public ArrayList<String> reviewLevels(){
		ArrayList<String> all = readList(new File(".failed"));
		ArrayList<Integer> levels = new ArrayList<Integer>();
		ArrayList<String> list = new ArrayList<String>();

		for(String content: all){
			int level = Integer.parseInt(content.split("	")[1]);
			if(!levels.contains(level)){
				levels.add(level);
			}	
		}

		Collections.sort(levels);
		
		for(int level : levels){
			list.add("Level " + level);
		}
		
		if(!levels.isEmpty()){
		  _level = levels.get(0);
		}
		return list;
	}
	
	public void updateReviewLevel(){

		ArrayList<String> failedList = reviewLevels();
		
		reviewLV = new JComboBox(failedList.toArray());
		
		// Adds an ActionListener to the JComboBox to extract the level and save it in the _level field
		reviewLV.addActionListener(selectLV.getActionListeners()[0]);
	}
}