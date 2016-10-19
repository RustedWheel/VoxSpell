import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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
import javax.swing.filechooser.FileNameExtensionFilter;
import utility.FileContentReader;

import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Font;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class MainMenu extends JPanel {

	private JButton quiz = new JButton("New Spelling Quiz");
	private JButton review = new JButton("Review Mistakes");
	private JButton statistics = new JButton("View Statistics");
	private JButton clear = new JButton("Clear Statistics");
	private JButton settings;
	private JButton changeSpeed = new JButton("Change Speaker Speed");private Gui _frame;	
	private JButton exit;
	private JButton changeVoice = new JButton("Change Speaker Voice");
	private JButton SelectFile = new JButton("Change Spelling List");
	private FileContentReader reader = new FileContentReader();
	private JButton btnHelp;
	@SuppressWarnings("rawtypes")
	private JComboBox selectLV;
	@SuppressWarnings("rawtypes")
	private JComboBox reviewLV;
	@SuppressWarnings("rawtypes")
	private JComboBox selectVoices;
	private int _level = 1;
	private String _defaultFile = "NZCER-spelling-lists.txt";
	private String _filePath = null;
	private int _maxLevel = 0;
	private JSlider voiceSpeed;

	private final JFileChooser fileSelector = new JFileChooser();
	private int _minLevel;
	
	/*
	 * Reused A2 code
	 */
	public MainMenu(Gui frame) {
		_frame = frame;
		GridLayout layout = new GridLayout(2, 2);
		JPanel menu = new JPanel();
		menu.setLayout(layout);
		JPanel welcomeScreen = new JPanel();
		welcomeScreen.setBackground(new Color(248, 248, 255));
		
		if(frame.getFilePath()==null){
			updateSelectLevel(_defaultFile);
		} else {
			updateSelectLevel(frame.getFilePath());
		}

		quiz.setFont(new Font("SansSerif", Font.PLAIN, 16));

		quiz.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				selectLV.setSelectedItem("Level " + _level);
				
				if(selectLV.getItemCount() == 0){
					JOptionPane.showMessageDialog(null,"The spelling list does not exist or is empty, please put the default spelling list in the resource directory or select another list in the settings");
				} else {
					// Asks the user to select a level to start at once they click Start Quiz
					int response = JOptionPane.showConfirmDialog(null, selectLV, "Please select a level", JOptionPane.OK_CANCEL_OPTION);

					if (response == JOptionPane.OK_OPTION) {
						
						String file;

						if (_filePath == null) {
							file = _defaultFile;
						} else {
							file = _filePath;
						}

						// Starts a new quiz and hides the main menu
						Quiz _quiz = new Quiz(Quiz.quizType.QUIZ, _frame, _level, _maxLevel, file);
						_quiz.startQuiz();
						_frame.getContentPane().removeAll();
						_frame.getContentPane().add(_quiz);
						_frame.revalidate();
						_frame.repaint();

					}

				}
			}

		});
		review.setFont(new Font("SansSerif", Font.PLAIN, 16));
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
						
						Quiz _quiz = new Quiz(Quiz.quizType.REVIEW, _frame, _level, _maxLevel, null);
						_quiz.startQuiz();
						_frame.getContentPane().removeAll();
						_frame.getContentPane().add(_quiz);
						_frame.revalidate();
						_frame.repaint();
					}
				}

			}

		});
		statistics.setFont(new Font("SansSerif", Font.PLAIN, 16));
		statistics.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Shows the statistics and hides the main menu
				Statistics _statistics = new Statistics(_frame);
				_statistics.showStats();
				_frame.getContentPane().removeAll();
				_frame.setSize(900, 400);
				_frame.getContentPane().add(_statistics);
				_frame.revalidate();
				_frame.repaint();
			}

		});
		clear.setFont(new Font("SansSerif", Font.PLAIN, 16));
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
		
		selectVoices = _frame.getSpeaker().getVoiceBox();
		
		menu.add(quiz);
		menu.add(review);
		menu.add(statistics);
		menu.add(clear);
		menu.setBackground(new Color(220,221,225));
		JPanel options = new JPanel();
		options.setBackground(new Color(220,221,225));

		exit = new JButton("Exit",new ImageIcon(MainMenu.class.getResource("/img/exit.png")));
		exit.setFont(new Font("SansSerif", Font.PLAIN, 16));
		exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}

		});

		voiceSpeed = _frame.getSpeaker().getVoiceSlider();

		final Object[] Options = {changeVoice, changeSpeed, SelectFile};
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
		fileSelector.setFileFilter(filter);
		
		SelectFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				int result = fileSelector.showOpenDialog(SelectFile);
				if (result == JFileChooser.APPROVE_OPTION) {
					clearStatistics();
				    File selectedFile = fileSelector.getSelectedFile();
				    _filePath = selectedFile.getAbsolutePath();
				    updateSelectLevel(_filePath);
				    _frame.setFilePath(_filePath);
				}
				
			}

		});

		// Adds an ActionListener to the change voice button to display a JOptionPane asking the user to select a voice
		changeVoice.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				int response = JOptionPane.showConfirmDialog(null, selectVoices, "Please select a voice to use", JOptionPane.OK_CANCEL_OPTION);
				if (response == JOptionPane.OK_OPTION) {
					_frame.getSpeaker().setVoice(_frame.getSpeaker().getSelectVoice());
					ArrayList<String> example = new ArrayList<String>();
					example.add("Hello there");
					_frame.getSpeaker().textToSpeech(example);
				}
			}

		});

		// Adds an ActionListener to the change speed button to display a JOptionPane asking the user to select a speed
		changeSpeed.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int response = JOptionPane.showConfirmDialog(null, voiceSpeed, "Please select a speed to use", JOptionPane.OK_CANCEL_OPTION);
				if (response == JOptionPane.OK_OPTION) {
					_frame.getSpeaker().setSelectedSpeed("(Parameter.set 'Duration_Stretch " + _frame.getSpeaker().getSpeed() +")");
				}
			}

		});
		
		settings = new JButton("Settings",new ImageIcon(MainMenu.class.getResource("/img/setting.png")));
		
		settings.setFont(new Font("SansSerif", Font.PLAIN, 16));
		
		// Adds an ActionListener to the change voice button to display a JOptionPane message allowing the user to change voice or speed
		settings.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, Options, "Settings", JOptionPane.INFORMATION_MESSAGE);
			}

		});
		 
		btnHelp = new JButton("Help",new ImageIcon(MainMenu.class.getResource("/img/help.png")));
		btnHelp.setFont(new Font("SansSerif", Font.PLAIN, 16));
		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Press \"New Quiz\" to start a new quiz\nPress \"Review\" to review previously failed words\nPress \"View Statistics\" to view your current statistics\nPress \"Clear Statistics\" to clear all current statistics\nPress \"Settings\" to change the text to speech voice or speed or the spelling list", "Help", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		welcomeScreen.setLayout(new BorderLayout(0, 0));
		
		//Creates the VoxSpekk logo
		JLabel logoLabel = new JLabel();
		logoLabel.setBounds(0, 21, 488, 256);
		welcomeScreen.add(logoLabel, BorderLayout.CENTER);
		ImageIcon oldLogo = new ImageIcon(MainMenu.class.getResource("/img/VoxSpell.png"));
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
		
		options.add(btnHelp);
		options.add(settings);
		options.add(exit);
		setLayout(new BorderLayout());
		add(welcomeScreen, BorderLayout.NORTH);
		add(menu, BorderLayout.CENTER);
		add(options, BorderLayout.SOUTH);

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
	 * This method scans all of the levels inside a wordlist and also saves the maximum level inside the
	 * wordlist
	 * 
	 * Original code by David
	 * 
	 * @param wordlist The wordlist to scan the levels from
	 * @return An ArrayList of levels inside the wordlist
	 */
	public ArrayList<String> scanLevels(String wordlist){
		ArrayList<String> all = reader.readList(new File(wordlist));
		ArrayList<String> levels = new ArrayList<String>();
		int i = 0;
		for(String content: all){
			if(content.startsWith("%Level")){
				if(i == 0){
					_minLevel = Integer.parseInt(content.split(" ")[1]);
				}
				levels.add("Level " + content.split(" ")[1]);
				if (Integer.parseInt(content.split(" ")[1]) > _maxLevel) {
					_maxLevel = Integer.parseInt(content.split(" ")[1]);
				}
				i++;
			}
		}
		_level = _minLevel;
		return levels;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
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
	
	/**
	 * This method scans all of the levels inside the failed list
	 * 
	 * Original code by David
	 * 
	 * @param null
	 * @return An ArrayList of levels inside the failed list
	 */
	public ArrayList<String> reviewLevels(){
		ArrayList<String> all = reader.readList(new File(".failed"));
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
	
	
	/**
	 * Updates the levels within the review JComboBox
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void updateReviewLevel(){

		ArrayList<String> failedList = reviewLevels();
		
		reviewLV = new JComboBox(failedList.toArray());
		
		// Adds an ActionListener to the JComboBox to extract the level and save it in the _level field
		reviewLV.addActionListener(selectLV.getActionListeners()[0]);
	}
}