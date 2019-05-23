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

public class LabelAward extends JPanel{
	String lastName, firstName, award, description;
	JLabel lblName, lblImage, lblAward, lblDescription;
	public LabelAward(Font montserrat, Color foreColor) {
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
		lblDescription = new JLabel("", SwingConstants.CENTER);
		lblDescription.setFont(fontnames);
		lblDescription.setOpaque(false);
		lblDescription.setForeground(foreColor);
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
		gc.fill = GridBagConstraints.NONE;
		this.add(lblName, gc);
		gc.gridy = 1;
		this.add(lblAward, gc);
		gc.gridy = 2;
		this.add(lblDescription, gc);

	}
	
	public void addDescription() {
		this.remove(lblName);
		this.remove(lblAward);
		this.remove(lblDescription);
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth =1;
		gc.gridheight = 1;
		gc.weightx = 100.0;
		gc.weighty = 100.0;
		gc.insets = new Insets(5, 0, 0, 0);
		gc.anchor = GridBagConstraints.NORTH;
		gc.fill = GridBagConstraints.NONE;
		this.add(lblName, gc);
		gc.insets = new Insets(0, 0, 0, 0);
		gc.gridy = 1;
		this.add(lblAward, gc);
		gc.gridy = 2;
		gc.insets = new Insets(0, 0, 5, 0);
		this.add(lblDescription, gc);
	}

	public void removeDescription() {
		this.remove(lblName);
		this.remove(lblAward);
		this.remove(lblDescription);
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth =1;
		gc.gridheight = 1;
		gc.weightx = 100.0;
		gc.weighty = 100.0;
		gc.insets = new Insets(10, 0, 0, 0);
		gc.anchor = GridBagConstraints.NORTH;
		gc.fill = GridBagConstraints.NONE;
		this.add(lblName, gc);
		gc.insets = new Insets(0, 0, 10, 0);
		gc.gridy = 1;
		this.add(lblAward, gc);
	}

	public void setForegroundCol(Color foreColor) {
		lblName.setForeground(foreColor);
		lblAward.setForeground(foreColor);
		lblDescription.setForeground(foreColor);
	}
}
