package utility;

import java.awt.Image;

import javax.swing.ImageIcon;


public class ImageResizer {
	
	public ImageResizer() {
	}

	public ImageIcon Resize(ImageIcon icon, int width, int height){
		
		Image img = icon.getImage();
        Image logo = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		ImageIcon newIcon = new ImageIcon(logo);
		
		return newIcon;
	}
}
