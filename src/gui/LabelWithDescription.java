package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import utilities.ConfigManager;

public class LabelWithDescription extends JPanel{
	String lastName, firstName, award, description;
	JLabel lblName, lblImage, lblAward;
	JTextArea txtAreaDecription;
	public LabelWithDescription(Font montserrat, Color foreColor) {
		Font fontnames = montserrat.deriveFont(Font.BOLD, (int)(ConfigManager.namesSize));
		Font fontDesc = montserrat.deriveFont(Font.BOLD, (int)(ConfigManager.namesSize-5));
		lblName = new JLabel ("", SwingConstants.CENTER);
		lblName.setFont(fontnames);
		lblName.setOpaque(false);
		lblName.setForeground(foreColor);
		lblAward = new JLabel ("", SwingConstants.CENTER);
		lblAward.setFont(fontnames);
		lblAward.setOpaque(false);
		lblAward.setForeground(foreColor);
		txtAreaDecription = new JTextArea("");
		txtAreaDecription.setEditable(false);
		txtAreaDecription.setOpaque(false);
		txtAreaDecription.setForeground(foreColor);
		txtAreaDecription.setFont(fontDesc);
		txtAreaDecription.setLineWrap(true);
		txtAreaDecription.setWrapStyleWord(true);
		this.setOpaque(false);
		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth =1;
		gc.gridheight = 1;
		gc.weightx = 100.0;
		gc.weighty = 100.0;
		gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.NORTH;
		gc.fill = GridBagConstraints.HORIZONTAL;
		this.add(lblName, gc);
		gc.gridy = 1;
		this.add(lblAward, gc);
		gc.gridy = 2;
		gc.insets = new Insets(4, 4, 4, 4);
		this.add(txtAreaDecription, gc);

	}

	public void setForegroundCol(Color foreColor) {
		lblName.setForeground(foreColor);
		lblAward.setForeground(foreColor);
		txtAreaDecription.setForeground(foreColor);
	}
}
