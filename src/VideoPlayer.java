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
import utility.ImageResizer;


public class VideoPlayer {
	
    private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
    private JFrame frame;
    private ImageIcon muteIcon, unmuteIcon, playIcon, exitIcon, pauseIcon;
    private JButton play, stop, mute;
    private JProgressBar progress = new JProgressBar();
    private ImageResizer resizer = new ImageResizer();
    
/*
    ffmpeg -i video.mp4 -i audio.wav -c:v copy -c:a aac -strict experimental output.mp4*/

    public VideoPlayer(String filename) {
    
        frame = new JFrame("Level up Reward");

        mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
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
				frame.dispose();
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
        
        frame.setLocation(100, 100);
        frame.setSize(1050, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // play the video specified in the input
        video.playMedia(filename);
        timer.start();
    }
    
    private void setUpGUI(){
        
        frame.add(mediaPlayerComponent);
        
        muteIcon = resizer.Resize(new ImageIcon(VideoPlayer.class.getResource("/img/Mute.png")), 30, 25);
        unmuteIcon = resizer.Resize(new ImageIcon(VideoPlayer.class.getResource("/img/Unmute.png")), 30, 25);
        playIcon = resizer.Resize(new ImageIcon(VideoPlayer.class.getResource("/img/Play.png")), 30, 25);
        exitIcon = resizer.Resize(new ImageIcon(VideoPlayer.class.getResource("/img/Stop.png")), 30, 25);
        pauseIcon = resizer.Resize(new ImageIcon(VideoPlayer.class.getResource("/img/Pause.png")), 30, 25);
        
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
        
        frame.add(menu, BorderLayout.SOUTH);
    }
}