import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
/*import java.io.File;
import java.io.FileInputStream;*/

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/*import sun.audio.AudioData;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import sun.audio.ContinuousAudioDataStream;*/
import utility.DeleteGeneratedReward;

@SuppressWarnings("serial")
public class Gui extends JFrame{

	private Speaker speaker = new Speaker();
	private String _filePath;

	public Gui() {
		super("VoxSpell");
		setSize(550, 575);
/*		startBGMusic();*/
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBackground(new Color(248, 248, 255));
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowListener() {

	        @Override
	        public void windowClosing(WindowEvent e) {
	        	int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Confirm exit", JOptionPane.CANCEL_OPTION);
				if (response == JOptionPane.OK_OPTION) {
					DeleteGeneratedReward.delete();
					System.exit(0);
				}
	        }

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

	    });
		setLocationRelativeTo(null);
		add(new MainMenu(this));
	}
	
	public String getFilePath(){
		return _filePath;
	}
	
	public void setFilePath(String path){
		_filePath = path;
	}
	
	public Speaker getSpeaker(){
		return speaker;
	}
	
/*	public void startBGMusic(){
	    AudioPlayer myBackgroundPlayer = AudioPlayer.player;
	    ContinuousAudioDataStream myLoop = null;
	    try {
	    	AudioStream myBackgroundMusic = new AudioStream(getClass().getResourceAsStream("bgm.mp3"));
	          AudioData myData = myBackgroundMusic.getData();
	          myLoop = new ContinuousAudioDataStream(myData);
	    }catch(Exception error){
	        System.out.println("File Not Found");
	        System.out.println(error);
	    }
	    myBackgroundPlayer.start(myLoop);  
	}*/
	
	public static void main(String[] Args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Gui frame = new Gui();
				frame.setResizable(false);
				try {
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
					/*"com.sun.java.swing.plaf.gtk.GTKLookAndFeel"*/
					/*"com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"*/
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

}
