import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;


public class VideoPlayer {
	
    private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
    private JFrame Videoframe;
    private ImageIcon muteIcon, unmuteIcon, playIcon, exitIcon, pauseIcon;
    private JButton play, stop, mute;
    private JProgressBar progress = new JProgressBar();

    public VideoPlayer(String filename) {
    
    	Videoframe = new JFrame("Level up Reward");
/*        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);*/
		/*frame.addWindowListener(new WindowListener() {

	        @Override
	        public void windowClosing(WindowEvent e) {
	        	frame.dispose();
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

		});*/
        
        mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        mediaPlayerComponent.setSize(600, 600);
        final EmbeddedMediaPlayer video = mediaPlayerComponent.getMediaPlayer();
        setUpGUI();
        
        play.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (play.getIcon().equals(pauseIcon)) {
					video.setPause(true);
					play.setIcon(playIcon);
				} else {
					video.setPause(false);
					play.setIcon(pauseIcon);
				}
				
			}
        	
        });
        
        stop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				video.stop();
				Videoframe.dispose();
			}
        	
        });
        
        mute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (mute.getIcon().equals(muteIcon)) {
					mute.setIcon(unmuteIcon);
				} else {
					mute.setIcon(muteIcon);
				}
				video.mute();
			}
		});
        
        // update the progress bar every 50 ms with the current time/total time percent
        Timer timer = new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				long time = video.getTime();
				long totalTime = video.getLength();
				if (totalTime != 0) {
				if (time == totalTime) {
					play.setEnabled(false);
				}
				progress.setValue((int) ((time*100)/totalTime));
				}
			}
		});
        
        Videoframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Videoframe.setLocation(100, 100);
        Videoframe.setSize(1050, 600);
        Videoframe.setVisible(true);

        // play the video specified in the input
        video.playMedia(filename);
        timer.start();
    }

	private void setUpGUI(){
        
		Videoframe.add(mediaPlayerComponent);
        
        muteIcon = new ImageIcon(VideoPlayer.class.getResource("/img/Mute.png"));
        unmuteIcon = new ImageIcon(VideoPlayer.class.getResource("/img/Unmute.png"));
        playIcon = new ImageIcon(VideoPlayer.class.getResource("/img/Play.png"));
        exitIcon = new ImageIcon(VideoPlayer.class.getResource("/img/Stop.png"));
        pauseIcon = new ImageIcon(VideoPlayer.class.getResource("/img/Pause.png"));
        
        mute = new JButton(muteIcon);
		mute.setPreferredSize(new Dimension(60,50));
		play = new JButton(pauseIcon);
		play.setPreferredSize(new Dimension(60,50));
        stop = new JButton(exitIcon);
		stop.setPreferredSize(new Dimension(60,50));
        
        JPanel playerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        playerPanel.add(play);
        playerPanel.add(stop);
        playerPanel.add(mute);
        
        JPanel menu = new JPanel();
        
        menu.setLayout(new GridLayout(2,1));
        
        menu.add(progress);
        menu.add(playerPanel);
        
        Videoframe.add(menu, BorderLayout.SOUTH);
    }
}