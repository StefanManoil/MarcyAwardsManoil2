package gui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JCheckBox;

import dataStructures.AwardInfo;
import dataStructures.Student;
import utilities.ConfigManager;
import utilities.DatabaseManager;

public class UpdateAwardTypes extends JFrame implements ListSelectionListener{
	 //a hashmap with an entry for each award, and an associated object AwardInfo(name/desc/yearByYear). 
	 //The key corresponds to the award field in the table award_type, basically the name of the award e.g. honor roll 
	 private HashMap<String, AwardInfo> awardsInfo ;
	 ImageIcon marcyLogo;
	 Font ncaa;
	 Font montserrat;
	 
	 //gui elements
	 JLabel lblLogo1, lblLogo2, lblAward, lblDescription, lblTitle, lblAwards,lblLeft, lblRight;
	 JButton btnAdd, btnEdit, btnDelete;
	 JTextArea txtInstructions, txtDescription;
	 JTextField txtAward;
	 JPanel buttonsPanel;
	 JCheckBox chkYBY;
	 JList<String> awardsList;
	 //String [] awardsKeys;//used to make the JList
	 DefaultListModel listModel;//used to make the JList
	 JScrollPane scrollAwardList;
	 
	 public UpdateAwardTypes(ImageIcon marcyLogo, Font ncaa, Font montserrat) {
		 this.marcyLogo = marcyLogo;
		 this.ncaa = ncaa;
		 this.montserrat = montserrat;
		 //init must be called before running any statement, otherwise connections and database will not work.
		 //it checks if the awards database and the awards and award_type tables exit, and it creates them if not 
		 DatabaseManager.init();
		 listModel = new DefaultListModel();
		 
		 lblLogo1 = new JLabel(marcyLogo);
	     lblLogo2 = new JLabel(marcyLogo);
	     lblTitle = new JLabel("ADD/EDIT/DELETE AWARD TYPES");
	     lblLeft = new JLabel();
	     lblRight = new JLabel();
	     Font fontTitle = ncaa;
	     fontTitle = fontTitle.deriveFont(Font.BOLD, (int)(ConfigManager.titleSize/2));
	     lblTitle.setFont(fontTitle);
	     lblTitle.setOpaque(true);
	     lblTitle.setBackground(ConfigManager.background);
	     lblTitle.setForeground(ConfigManager.title);
	     lblTitle.setHorizontalAlignment(JLabel.CENTER);
	     
	     Font fontInstructions = montserrat;
	     fontInstructions = fontInstructions.deriveFont(Font.BOLD, (int)(ConfigManager.descSize/2));
	     txtInstructions = new JTextArea(" - To add a new award type: select New Award in the Awards list, fill out the information, and press Add.\n"
	     		+ " - To edit an existing award: select the award in the Awards list, update the information, and press Edit.\n"
	     		+ " - To delete an existing award: select the award in the Awards list, and press Delete.\n"
	     		+ " - !!! If the award to be deleted has been already selected for some students, it cannot be deleted.\n");
	     txtInstructions.setEditable(false);
	     txtInstructions.setBackground(ConfigManager.background);
	     txtInstructions.setForeground(ConfigManager.description);
	     txtInstructions.setFont(fontInstructions);
	     
	     Font fontLabels = montserrat;
	     fontLabels = fontLabels.deriveFont(Font.BOLD, (int)(ConfigManager.titleSize/4));
	     
	     lblAwards = new JLabel(" Awards: ");
	     lblAwards.setOpaque(true);
	     lblAwards.setBackground(ConfigManager.background);
	     lblAwards.setForeground(ConfigManager.motto);
	     lblAwards.setFont(fontLabels);
	     
	     lblAward = new JLabel(" Award - Maximum 30 characters including spaces, type ^ instead of ' e.g. Principal^s: ");
	     lblAward.setOpaque(true);
	     lblAward.setBackground(ConfigManager.background);
	     lblAward.setForeground(ConfigManager.motto);
	     lblAward.setFont(fontLabels);
	     
	     txtAward = new JTextField(30);
	     txtAward.setFont(fontInstructions);
	     
	     lblDescription = new JLabel(" Description - Maximum 1200 characters including spaces, , type ^ instead of ' : ");
	     lblDescription.setOpaque(true);
	     lblDescription.setBackground(ConfigManager.background);
	     lblDescription.setForeground(ConfigManager.motto);
	     lblDescription.setFont(fontLabels);
	     
	     txtDescription = new JTextArea(2,2);
	     txtDescription.setFont(fontInstructions);
	     JScrollPane scrollDescription = new JScrollPane(txtDescription, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	        
	     chkYBY = new JCheckBox("Year By Year:", false);
	     chkYBY.setOpaque(true);
	     chkYBY.setBackground(ConfigManager.background);
	     chkYBY.setForeground(ConfigManager.motto);
	     chkYBY.setFont(fontLabels);
	     
	     btnAdd = new JButton ("ADD");
	     btnAdd.setFont(fontLabels);
	     btnAdd.addActionListener(a -> addNewAward());
	     btnEdit = new JButton ("EDIT");
	     btnEdit.setFont(fontLabels);
	     btnEdit.addActionListener(a -> editAward());
	     btnDelete = new JButton ("DELETE");
	     btnDelete.setFont(fontLabels);
	     btnDelete.addActionListener(a -> deleteAward());
	     buttonsPanel = new JPanel();
	     buttonsPanel.add(btnAdd);
	     buttonsPanel.add(btnEdit);
	     buttonsPanel.add(btnDelete);
	     buttonsPanel.setBackground(ConfigManager.background);
	     
	     awardsInfo = DatabaseManager.pullAwardsInfo();
		 Object [] awardsAsObjects = awardsInfo.keySet().toArray();
		 String temp [] = new String[awardsAsObjects.length];
		 for (int i=0; i<temp.length;i++) {
			 temp[i] = (String)awardsAsObjects[i];
		 }
		 Arrays.sort(temp);
		 listModel.addElement("New Award"); 	
		 for (int i=0; i<awardsAsObjects.length;i++) {
			 listModel.addElement(((String)temp[i]));
		 }	 
	     awardsList = new JList<String>(listModel);
	     scrollAwardList = new JScrollPane(awardsList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	     awardsList.setFont(fontInstructions);
	     awardsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	     awardsList.addListSelectionListener(this);
	     awardsList.setSelectedIndex(0);
	     
	     setLayout(new GridBagLayout());
	     GridBagConstraints gc = new GridBagConstraints();
         gc.gridx = 0;
         gc.gridy = 0;
         gc.gridwidth =1;
         gc.gridheight = 1;
         gc.weightx = 100.0;
         gc.weighty = 100.0;
         gc.insets = new Insets(0, 0, 0, 0);
         gc.anchor = GridBagConstraints.NORTHWEST;
         gc.fill = GridBagConstraints.NONE;
         gc.insets = new Insets(10,10,10,10);
         getContentPane().add(lblLogo1, gc);   
	     
         gc.gridx = 1;
         gc.gridwidth = 2;
         gc.fill = GridBagConstraints.BOTH;
         gc.anchor = GridBagConstraints.NORTH;
         getContentPane().add(lblTitle, gc);
         
         gc.gridx = 3;
         gc.gridwidth = 1;
         gc.fill = GridBagConstraints.NONE;
         gc.anchor = GridBagConstraints.NORTHEAST;
         getContentPane().add(lblLogo2, gc);
         
         gc.gridx = 0;
         gc.gridy = 1;
         gc.gridwidth = 4;
         gc.fill = GridBagConstraints.BOTH;
         gc.anchor = GridBagConstraints.NORTH;
         getContentPane().add(txtInstructions, gc);
         
         gc.gridx = 0;
         gc.gridy = 2;
         gc.gridwidth = 1;
         gc.fill = GridBagConstraints.NONE;
         gc.anchor = GridBagConstraints.WEST;
         getContentPane().add(lblLeft, gc);
         
         gc.gridx = 1;
         gc.fill = GridBagConstraints.BOTH;
         gc.anchor = GridBagConstraints.NORTH;
         getContentPane().add(lblAwards, gc);
         
         gc.gridx = 2;
         gc.fill = GridBagConstraints.BOTH;
         gc.anchor = GridBagConstraints.NORTH;
         getContentPane().add(lblAward, gc);
         
         gc.gridx = 3;
         gc.fill = GridBagConstraints.NONE;
         gc.anchor = GridBagConstraints.EAST;
         getContentPane().add(lblRight, gc);
         
         gc.gridx = 1;
         gc.gridy = 3;
         gc.gridheight = 4;
         gc.fill = GridBagConstraints.BOTH;
         gc.anchor = GridBagConstraints.NORTH;
         getContentPane().add(scrollAwardList, gc);
         
         gc.gridx = 2;
         gc.gridwidth = 1;
         gc.gridheight = 1;
         gc.fill = GridBagConstraints.NONE;
         gc.anchor = GridBagConstraints.WEST;
         getContentPane().add(txtAward, gc);
         
         gc.gridy = 4;
         getContentPane().add(lblDescription, gc);
         
         gc.gridy = 5;
         gc.gridwidth = 1;
         gc.fill = GridBagConstraints.BOTH;
         getContentPane().add(scrollDescription, gc);
         
         gc.gridy = 6;
         gc.gridwidth = 1;
         getContentPane().add(chkYBY, gc);
         
         gc.gridy = 7;
         gc.gridx = 1;
         gc.gridwidth = 2;
         getContentPane().add(buttonsPanel, gc);
         
	     getContentPane().setBackground(ConfigManager.background);
	        
	    
	     //setLocationRelativeTo(null);
	     setSize(800, 400);
	     pack();
         Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
         this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
	     //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	     setVisible(true);
		 txtAward.requestFocus();
	 }
	 
	 public void valueChanged(ListSelectionEvent e) {
	        if (!e.getValueIsAdjusting()) {
	          JList list = (JList) e.getSource();
	          int selections[] = list.getSelectedIndices();
	          int index = selections[0];
			 //if new award was selected, reset the textfields, and disable btnEdit, btnDelete
			 
			 if (index == 0) { //new award
				 txtAward.setText("");
				 txtAward.requestFocus();
				 txtDescription.setText("");
				 chkYBY.setSelected(false);
				 btnEdit.setEnabled(false);
				 btnDelete.setEnabled(false);
				 btnAdd.setEnabled(true);
			 }
			 //update the textFields with appropriate info, and disable btnAdd
			 else {
				 txtAward.setText(awardsInfo.get(listModel.get(index)).getAward());
				 //txtAward.setText(awardsInfo.get(awardsKeys[index]).getAward());
				 txtDescription.setText(awardsInfo.get(listModel.get(index)).getDescription()); 
				 //txtDescription.setText(awardsInfo.get(awardsKeys[index]).getDescription());            
				 chkYBY.setSelected(awardsInfo.get(listModel.get(index)).isYearByYear());
				 //chkYBY.setSelected(awardsInfo.get(awardsKeys[index]).isYearByYear());
				 btnAdd.setEnabled(false);
				 btnEdit.setEnabled(true);
				 btnDelete.setEnabled(true);
			 }
	        }
	 }
	 
	 public void refreshList() {
		 //System.out.println("in refresh List");
		 awardsList.removeListSelectionListener(this);
		 listModel.clear();
		 awardsInfo = DatabaseManager.pullAwardsInfo();
		 Object [] awardsAsObjects = awardsInfo.keySet().toArray();
		 String temp [] = new String[awardsAsObjects.length];
		 for (int i=0; i<temp.length;i++) {
			 temp[i] = (String)awardsAsObjects[i];
		 }
		 Arrays.sort(temp);
		 listModel.addElement("New Award"); 	
		 for (int i=0; i<awardsAsObjects.length;i++) {
			 listModel.addElement((String)temp[i]);
		 }	 
		 awardsList.setModel(listModel);
		 awardsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		 awardsList.addListSelectionListener(this);
		 awardsList.setSelectedIndex(0);
		 txtAward.requestFocus();
	 }
	 
	 public void addNewAward() {
		 String newAward, newDescription;
		 int lastID = 0;
		 int newYBY;
		 if (txtAward.getText().length()>30) {//check if the award name has more than 30 characters
			 JOptionPane.showMessageDialog(null, "The award name can have maximum 30 characters!!!", "WARNING",
                     JOptionPane.INFORMATION_MESSAGE);
			 txtAward.requestFocus();
			 return;
		 }
		 else if(txtAward.getText().trim().length()==0) {//check if the award name is an empty String
			 JOptionPane.showMessageDialog(null, "The award name can't be empty!!!", "WARNING",
                     JOptionPane.INFORMATION_MESSAGE);
			 txtAward.requestFocus();
			 return;
		 }
		//check if the award name already exists
		 else if (awardsInfo.get(txtAward.getText())!=null) {
			 JOptionPane.showMessageDialog(null, "An award with the name "+ txtAward.getText()+" has already been created!!!", "",
	             JOptionPane.INFORMATION_MESSAGE);
			 txtAward.requestFocus();
			 return;
		 }
		 else if (txtDescription.getText().length()>1200) {//check if the award description has more than 30 characters
			 JOptionPane.showMessageDialog(null, "The award description can have maximum 1200 characters!!!", "WARNING",
	                JOptionPane.INFORMATION_MESSAGE);
			 txtDescription.requestFocus();
			 return;
		 }
		 else {
			 //award less than 30 chars, description less than 1200 chars, does not exist - add to database
			 String stringYBY = chkYBY.isSelected() ? "will": "will not";
			 newAward = txtAward.getText().trim().replace("^","'");
			 newDescription = txtDescription.getText().trim().replace("'", "^");
			 newYBY = chkYBY.isSelected() ? 1: 0;
			 DatabaseManager.addNewAwardInAward_Type(newAward, newDescription, newYBY);
			 awardsList.removeListSelectionListener(this);
			 refreshList();		 
		 }
	    
	 }
	 
	public void deleteAward() {
		 int selections[] = awardsList.getSelectedIndices();
         int index = selections[0];
		 String awardToDelete = awardsInfo.get(listModel.get(index)).getAward().replace("'", "^");
		 ArrayList<Student> studentsWithThisAward = DatabaseManager.loadStudentsForACertainAward(awardToDelete);
		 if (studentsWithThisAward.size()!=0) {
			 JOptionPane.showMessageDialog(null, "This award is already assigned to students, and cannot be deleted!", "ATTENTION",
                     JOptionPane.INFORMATION_MESSAGE);
			 txtAward.requestFocus();
		 }
		 else {
			 DatabaseManager.deleteAwardFromAward_Type(awardToDelete);
			 refreshList();
		 }
		 return;
	 }
		 
	 public void editAward() {
		 int selections[] = awardsList.getSelectedIndices();
         int index = selections[0];
         String awardToEdit = awardsInfo.get(listModel.get(index)).getAward().replaceAll("'", "^");
		 String newAward;
		 int lastID = 0;
		 String newAwardName = txtAward.getText().trim().replace("'", "^");;
		 String newDescription = txtDescription.getText().trim().replace("'", "^");
		 int newYBY = chkYBY.isSelected() ? 1: 0;
		 if (newAwardName.length()>30) {//check if the award name has more than 30 characters
			 JOptionPane.showMessageDialog(null, "The award name can have maximum 30 characters!!!", "ATTENTION",
                     JOptionPane.INFORMATION_MESSAGE);
			 txtAward.requestFocus();
			 return;
		 } 
		 else if(txtAward.getText().trim().length()==0) {//check if the award name is an empty String
			 JOptionPane.showMessageDialog(null, "The award name can't be empty!!!", "WARNING",
                 JOptionPane.INFORMATION_MESSAGE);
			 txtAward.requestFocus();
			 return;
		 }
		 //check if the award name already exists
		 else if ((!awardToEdit.equals(newAwardName)) && awardsInfo.get(txtAward.getText())!=null)  {//check if the award name already exists
				JOptionPane.showMessageDialog(null, "An award with the name "+ txtAward.getText()+" has already been created!!!", "ATTENTION",
			        JOptionPane.INFORMATION_MESSAGE);
				txtAward.requestFocus();
				return;
		 }
		 else if (newDescription.length()>1200) {//check if the award description has more than 30 characters
			JOptionPane.showMessageDialog(null, "The award description can have maximum 1200 characters!!!", "ATTENTION",
	            JOptionPane.INFORMATION_MESSAGE);
			txtDescription.requestFocus();
			return;
		 }
		 
		 ArrayList<Student> studentsWithThisAward = DatabaseManager.loadStudentsForACertainAward(awardToEdit);
		 if (studentsWithThisAward.size()==0) {//award not assigned to students yet, can be changed no problem
			 DatabaseManager.editAwardInAward_Type(awardToEdit, newAwardName, newDescription, newYBY);
		 }
		 else {//award assigned to students but award name not modified
			 if (newAwardName.equals(awardToEdit)) {
				 DatabaseManager.editAwardInAward_Type(awardToEdit, newAwardName, newDescription, newYBY);
			 }
			 else {//both award_type and awards need to be updated
				 int option = JOptionPane.showConfirmDialog(null,
                         "This award is already assigned to students. Do you wish to continue?",
                         "ATTENTION", 
                         JOptionPane.YES_NO_OPTION);
				 if (option == JOptionPane.NO_OPTION){
					 txtAward.requestFocus();
					 return;
				 }
				 else {//update both award_type and awards
					 DatabaseManager.editAwardInAward_Type(awardToEdit, newAwardName, newDescription, newYBY);
					 DatabaseManager.editAwardInAwards(awardToEdit, newAwardName);
				 }
			 }
		 }
		 refreshList();
	 }
}