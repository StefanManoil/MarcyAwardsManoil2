package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class ImagePanel extends JPanel{
	private ImageIcon marcyLogo;
	public ImagePanel(ImageIcon marcyLogo) {
		this.marcyLogo = marcyLogo;
	}
		
	@Override
	public void paint(Graphics g) {
		  super.paint(g);
	      // Declare and initialize a Graphics2D object
	      Graphics2D g2 = (Graphics2D) g;
			g2.drawImage(marcyLogo.getImage(), 50, 50, 400, 400, this);
			g2.drawImage(marcyLogo.getImage(), 3350, 50, 400, 400, this);
	}
	
}
