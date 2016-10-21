import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JSlider;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import utility.BashCommand;
import utility.FileContentReader;

public class Speaker {

	private FileContentReader reader = new FileContentReader();
	private String _selectedSpeed;
	private String _voice;
	private String _defaultSpeed = "(Parameter.set 'Duration_Stretch 1.0)";
	private String _repeatSpeed = "(Parameter.set 'Duration_Stretch 1.5)";
	private ArrayList<String> _availableVoices = new ArrayList<String>();
	private String _voicePath = "/usr/share/festival/voices";
	@SuppressWarnings("rawtypes")
	private JComboBox selectVoices;
	private String _selectedVoice;
	private int _maxSpeed = 20;
	private int _minSpeed = 5;
	private int _initSpeed = 15;
	private JSlider voiceSpeed = new JSlider(JSlider.HORIZONTAL,_minSpeed,_maxSpeed,_initSpeed);
	private double _speed;
	private Quiz _quiz;

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Speaker(){
		
		// Adds the list of available voices in /usr/share/festival/english to an Array
		// and puts it in a JComboBox
		// Original code by David
		String[] subDirectories = reader.listDirectories(_voicePath + "/english");
		for(String voices : subDirectories){
			switch (voices) {
			case "kal_diphone":
				voices = "American voice";
				break;
			case "rab_diphone":
				voices = "British voice";
				break;
			case "akl_nz_jdt_diphone":
				voices = "New Zealand voice";
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
					text = text.replaceAll("repeat", "");
					
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
		SpeakerWorker worker = new SpeakerWorker();
		worker.execute();

	}
	


	public void setSelectedSpeed(String speed){
		_selectedSpeed = speed;
	}
	
	public JComboBox<?> getVoiceBox(){
		return selectVoices;
	}
	
	public JSlider getVoiceSlider(){
		return voiceSpeed;
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
			case "American voice":
				voice = "kal_diphone";
				break;
			case "British voice":
				voice = "rab_diphone";
				break;
			case "New Zealand voice":
				voice = "akl_nz_jdt_diphone";
				break;
			}
			
			_voice = "(voice_" + voice + ")";
		}
	}
	
	public String getSelectVoice(){
		return _selectedVoice;
	}
	
	public double getSpeed(){
		return _speed;
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
	
	public void setQuiz(Quiz quiz){
		_quiz = quiz;
	}
	
	class SpeakerWorker extends SwingWorker<Void, Void> {

		public SpeakerWorker() {
		}

		@Override
		protected Void doInBackground() throws Exception {

			// Calls a bash command to run festival with the scheme file
			BashCommand.bashCommand("festival -b .text.scm");

			return null;
		}

		// If the quiz is complete, then disable the submit button, otherwise
		// re-enable the submit button
		protected void done() {

			BashCommand.bashCommand("rm -f .text.scm");
			
			if(_quiz != null){
				if(_quiz.attempts != 2 && _quiz.isQuizFinished() == false){
					_quiz.submit.setEnabled(true);
					_quiz.repeat.setEnabled(true);
				} else {
					_quiz.submit.setEnabled(false);
					_quiz.repeat.setEnabled(false);
				}
			}
		}

	}
	
}
