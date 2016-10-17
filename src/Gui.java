import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

@SuppressWarnings("serial")
public class Gui extends JFrame{

	protected Speaker speaker = new Speaker();
	private String _filePath;

	public Gui() {
		super("VoxSpell");
		setSize(500, 525);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBackground(new Color(248, 248, 255));
		setLocationRelativeTo(null);
		add(new MainMenu(this));
			
	}
	
	public String getFilePath(){
		return _filePath;
	}
	
	public void setFilePath(String path){
		_filePath = path;
	}
	
	public static void main(String[] Args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Gui frame = new Gui();
				frame.setResizable(false);
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

}
