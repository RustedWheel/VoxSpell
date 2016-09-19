import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;


public class VideoPlayer {
	
    private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private Quiz _quiz;

    public VideoPlayer(Quiz quiz) {
    	
    	_quiz = quiz;
    
        final JFrame frame = new JFrame("Level up Reward");

        mediaPlayerComponent = new EmbeddedMediaPlayerComponent();

        final EmbeddedMediaPlayer video = mediaPlayerComponent.getMediaPlayer();
        
        frame.add(mediaPlayerComponent);

        final JButton pause = new JButton("Pause");
        JButton stop = new JButton("Stop");
        final JProgressBar progress = new JProgressBar();
        
        JPanel pauseStop = new JPanel();
        
        pauseStop.add(pause, JPanel.LEFT_ALIGNMENT);
        pauseStop.add(stop, JPanel.RIGHT_ALIGNMENT);
        
        JPanel menu = new JPanel();
        
        menu.setLayout(new GridLayout(2,1));
        
        menu.add(progress);
        menu.add(pauseStop);
        
        frame.add(menu, BorderLayout.SOUTH);
        
        pause.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (pause.getText().equals("Pause")) {
					video.setPause(true);
					pause.setText("Resume");
				} else {
					video.setPause(false);
					pause.setText("Pause");
				}
			}
        	
        });
        
        stop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				video.stop();
				frame.dispose();
				_quiz.frame.setVisible(true);
			}
        	
        });
        
        Timer timer = new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				long time = video.getTime();
				long totalTime = video.getLength();
				if (totalTime != 0) {
				if (time == totalTime) {
					pause.setEnabled(false);
				}
				progress.setValue((int) ((time*100)/totalTime));
				}
			}
		});
        
        frame.setLocation(100, 100);
        frame.setSize(1050, 600);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);

        String filename = "big_buck_bunny_1_minute.avi";
        //String filename = "test.mp4";
        video.playMedia(filename);
        timer.start();
    }

    /*public static void main(final String[] args) {
        
        NativeLibrary.addSearchPath(
            RuntimeUtil.getLibVlcLibraryName(), "/Applications/vlc-2.0.0/VLC.app/Contents/MacOS/lib"
        );
        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //new VideoPlayer(args);
            }
        });
    }*/
}