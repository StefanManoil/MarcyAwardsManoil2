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

import javax.swing.DefaultComboBoxModel;
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
import javax.swing.JComboBox;

import dataStructures.AwardInfo;
import dataStructures.Student;
import utilities.ConfigManager;
import utilities.DatabaseManager;

public class UpdateStudents extends JFrame implements ListSelectionListener{
	 //a hashmap with an entry for each award, and an associated object AwardInfo(name/desc/yearByYear). 
	 //The key corresponds to the award field in the table award_type, basically the name of the award e.g. honor roll 
	 private HashMap<String, AwardInfo> awardsInfo ;
	 //an ArrayList containing all the awards awarded to a specific student. 
	 private ArrayList<Student> awardsForStudent;
	 private ImageIcon marcyLogo;
	 private Font ncaa;
	 private Font montserrat;
	 
	 //gui elements
	 private JLabel lblLogo1, lblLogo2, lblAward, lblFirstName, lblLastName, lblTitle, lblAwardsForName,lblLeft, lblRight, lblGrade, lblYear, lblAwardDesc;
	 private JButton btnAdd, btnEdit, btnDelete, btnSearch, btnClear;
	 private JTextArea txtInstructions, txtDescription;
	 private JTextField txtFirstName, txtLastName, txtYear, txtAwardDesc;
	 private JPanel buttonsPanel;
	 private JList<String> awardsForThisStudent;
	 private JComboBox awards, grade;
     private DefaultComboBoxModel<String> awardsModel;
 	 private DefaultComboBoxModel<String> gradeModel;
     private DefaultListModel listModel;//used to make the JList
     private JScrollPane scrollAwardList;
	 
	 public UpdateStudents(ImageIcon marcyLogo, Font ncaa, Font montserrat) {
		 this.marcyLogo = marcyLogo;
		 this.ncaa = ncaa;
		 this.montserrat = montserrat;
		 //init must be called before running any statement, otherwise connections and database will not work.
		 //it checks if the awards database and the awards and award_type tables exit, and it creates them if not 
		 DatabaseManager.init();
		 listModel = new DefaultListModel();
		 
		 lblLogo1 = new JLabel(marcyLogo);
	     lblLogo2 = new JLabel(marcyLogo);
	     lblTitle = new JLabel("ADD/EDIT/DELETE STUDENTS");
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
	     txtInstructions = new JTextArea("Follow these steps to add/edit/delete students names and awards:\n"
	     		+ " - To ADD a new student:\n"
	    		+ "           1. Select New Award in the Awards list, fill out the information, and press Add.\n"
	     		+ "           2. The AWARDS FOR STUDENT list should update accordingly.\n"
	    		+ "           3. Continue adding students/awards by filling out info and pressing ADD.\n"
	     		+ " - To EDIT the info for a student-award already entered:\n"
	     		+ "           1. Search the student by entering the students first name and/or last name,\n"
	     		+ "           2. Select the student-award in the AWARDS FOR STUDENT: list,\n"
	     		+ "           3. Update the info - name, year, grade, award, and press Edit. The AWARDS FOR STUDENT list should update accordingly.\n"
	     		+ " - To DELETE the info for a student-award already entered:\n"
	     		+ "           1. Search the student by entering the students first name and/or last name,\n"
	     		+ "           2. Select the student-award in the Students-Awards list,\n"
	     		+ "           3. Press Delete. The AWARDS FOR STUDENT list should update accordingly.");
	     txtInstructions.setEditable(false);
	     txtInstructions.setBackground(ConfigManager.background);
	     txtInstructions.setForeground(ConfigManager.description);
	     txtInstructions.setFont(fontInstructions);
	     
	     Font fontLabels = montserrat;
	     fontLabels = fontLabels.deriveFont(Font.BOLD, (int)(ConfigManager.titleSize/4));
	     
	     lblFirstName = new JLabel(" FIRST NAME - Maximum 50 characters including spaces: ");
	     lblFirstName.setOpaque(true);
	     lblFirstName.setBackground(ConfigManager.background);
	     lblFirstName.setForeground(ConfigManager.motto);
	     lblFirstName.setFont(fontLabels);
	     
	     txtFirstName = new JTextField(30);
	     txtFirstName.setFont(fontInstructions);
	     
	     lblLastName = new JLabel(" LAST NAME - Maximum 50 characters including spaces: ");
	     lblLastName.setOpaque(true);
	     lblLastName.setBackground(ConfigManager.background);
	     lblLastName.setForeground(ConfigManager.motto);
	     lblLastName.setFont(fontLabels);
	     
	     txtLastName = new JTextField(30);
	     txtLastName.setFont(fontInstructions);
	     
	     btnSearch = new JButton ("SEARCH");
	     btnSearch.setFont(fontLabels);
	     btnSearch.addActionListener(a -> searchStudent());
	     
	     btnClear = new JButton ("CLEAR");
	     btnClear.setFont(fontLabels);
	     btnClear.addActionListener(a -> clearNames());
	     
	     lblAwardsForName = new JLabel(" AWARDS FOR STUDENT ");
	     lblAwardsForName.setOpaque(true);
	     lblAwardsForName.setBackground(ConfigManager.background);
	     lblAwardsForName.setForeground(ConfigManager.motto);
	     lblAwardsForName.setFont(fontLabels);
	     
	     listModel.addElement("New Award"); 	
	     awardsForThisStudent = new JList<String>(listModel);
	     awardsForThisStudent.setVisibleRowCount(4);
	     scrollAwardList = new JScrollPane(awardsForThisStudent, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	     awardsForThisStudent.setFont(fontInstructions);
	     awardsForThisStudent.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	     awardsForThisStudent.setSelectedIndex(0);
	     awardsForThisStudent.addListSelectionListener(this);
	     
	     lblAward = new JLabel("AWARD:");
	     lblAward.setOpaque(true);
	     lblAward.setBackground(ConfigManager.background);
	     lblAward.setForeground(ConfigManager.motto);
	     lblAward.setFont(fontLabels);
	     
	     awardsInfo = DatabaseManager.pullAwardsInfo();
	     awardsModel = new DefaultComboBoxModel<String>();
	     Object [] awardsAsObjects = awardsInfo.keySet().toArray();
		 String temp [] = new String[awardsAsObjects.length];
		 for (int i=0; i<temp.length;i++) {
			 temp[i] = (String)awardsAsObjects[i];
		 }
		 Arrays.sort(temp);
		 for (int i=0; i<awardsAsObjects.length;i++) {
			 awardsModel.addElement((String)temp[i]);
		 }	 
	     awards = new JComboBox<String>(awardsModel);
	     awards.setFont(fontInstructions);
	     awards.setSelectedItem("Honor Roll");
	     awards.addItemListener(e-> {
	    	 String studentAward = (String)awards.getSelectedItem();
	    	 if (studentAward.equals("Highest Mark Per Subject")) {
	    		 txtAwardDesc.setEditable(true);
	    		 txtAwardDesc.requestFocus();
	    	 }
	     });
	     
	     lblGrade = new JLabel("GRADE:");
	     lblGrade.setOpaque(true);
	     lblGrade.setBackground(ConfigManager.background);
	     lblGrade.setForeground(ConfigManager.motto);
	     lblGrade.setFont(fontLabels);
	     
	     gradeModel = new DefaultComboBoxModel<String>(new String[] {"9", "10", "11", "12"});
	     grade = new JComboBox<String>(gradeModel);
	     grade.setFont(fontInstructions);
	     grade.setSelectedIndex(0);
	     
	     lblYear = new JLabel("YEAR:");
	     lblYear.setOpaque(true);
	     lblYear.setBackground(ConfigManager.background);
	     lblYear.setForeground(ConfigManager.motto);
	     lblYear.setFont(fontLabels);
	     
	     txtYear = new JTextField(4);
	     txtYear.setFont(fontInstructions);
	     
	     lblAwardDesc = new JLabel("Course and Mark (for Highest Mark Only):");
	     lblAwardDesc.setOpaque(true);
	     lblAwardDesc.setBackground(ConfigManager.background);
	     lblAwardDesc.setForeground(ConfigManager.motto);
	     lblAwardDesc.setFont(fontLabels);
	     
	     txtAwardDesc = new JTextField(30);
	     txtAwardDesc.setEditable(false);
	     txtAwardDesc.setFont(fontInstructions);
	     
	     btnAdd = new JButton ("ADD");
	     btnAdd.setFont(fontLabels);
	     btnAdd.addActionListener(a -> addNewStudent());
	     btnEdit = new JButton ("EDIT");
	     btnEdit.setFont(fontLabels);
	     btnEdit.addActionListener(a -> editStudent());
	     btnDelete = new JButton ("DELETE");
	     btnDelete.setFont(fontLabels);
	     btnDelete.addActionListener(a -> deleteStudentAward());
	     buttonsPanel = new JPanel();
	     buttonsPanel.add(btnAdd);
	     buttonsPanel.add(btnEdit);
	     buttonsPanel.add(btnDelete);
	     buttonsPanel.setBackground(ConfigManager.background);
	         
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
         gc.gridwidth = 3;
         gc.fill = GridBagConstraints.BOTH;
         gc.anchor = GridBagConstraints.NORTH;
         getContentPane().add(lblTitle, gc);
         
         gc.gridx = 4;
         gc.gridwidth = 1;
         gc.fill = GridBagConstraints.NONE;
         gc.anchor = GridBagConstraints.NORTHEAST;
         getContentPane().add(lblLogo2, gc);
         
         gc.gridx = 1;
         gc.gridy = 1;
         gc.gridwidth = 3;
         gc.fill = GridBagConstraints.BOTH;
         gc.anchor = GridBagConstraints.NORTH;
         getContentPane().add(txtInstructions, gc);
         
         gc.gridx = 0;
         gc.gridy = 2;
         gc.gridwidth = 1;
         gc.gridheight = 7;
         gc.fill = GridBagConstraints.NONE;
         gc.anchor = GridBagConstraints.WEST;
         getContentPane().add(lblLeft, gc);
         
         gc.gridx = 1;
         gc.gridheight = 1;
         gc.fill = GridBagConstraints.NONE;
         gc.anchor = GridBagConstraints.WEST;
         getContentPane().add(lblLastName, gc);
         
         gc.gridx = 2;
         getContentPane().add(lblFirstName, gc);
         
         gc.gridx = 5;
         gc.gridheight = 7;
         gc.anchor = GridBagConstraints.EAST;
         getContentPane().add(lblRight, gc);
         
         gc.gridx = 1;
         gc.gridy = 3;
         gc.gridheight = 1;
         gc.anchor = GridBagConstraints.WEST;
         getContentPane().add(txtLastName, gc);
         
         gc.gridx = 2;
         getContentPane().add(txtFirstName, gc);
         
         JPanel searchClearPanel = new JPanel();
         searchClearPanel.setBackground(ConfigManager.background);
         searchClearPanel.add(btnSearch);
         searchClearPanel.add(btnClear);
         gc.gridx = 3;
         getContentPane().add(searchClearPanel, gc);
         
         gc.gridx = 1;
         gc.gridy = 4;
         getContentPane().add(lblAwardsForName, gc);
         
         gc.gridy = 5;
         gc.gridwidth = 2;
         gc.fill = GridBagConstraints.HORIZONTAL;
         getContentPane().add(scrollAwardList, gc);
         
         JPanel awardPanel = new JPanel();
         awardPanel.setBackground(ConfigManager.background);
         awardPanel.add(lblAward);
         awardPanel.add(awards);
         gc.gridy = 6;
         gc.gridx = 1;
         gc.gridwidth = 1;
         gc.fill = GridBagConstraints.NONE;
         getContentPane().add(awardPanel, gc);
         
         JPanel gradePanel = new JPanel();
         gradePanel.setBackground(ConfigManager.background);
         gradePanel.add(lblGrade);
         gradePanel.add(grade);
         gc.gridx = 2;
         getContentPane().add(gradePanel, gc);
         
         JPanel yearPanel = new JPanel();
         yearPanel.setBackground(ConfigManager.background);
         yearPanel.add(lblYear);
         yearPanel.add(txtYear);
         gc.gridx = 3;
         getContentPane().add(yearPanel, gc);
         
         JPanel descPanel = new JPanel();
         descPanel.setBackground(ConfigManager.background);
         descPanel.add(lblAwardDesc);
         descPanel.add(txtAwardDesc);
         gc.gridx = 1;
         gc.gridy = 7;
         gc.gridwidth = 3;
         getContentPane().add(descPanel, gc);
                
         gc.gridy = 8;
         gc.gridx = 1;
         gc.gridwidth = 3;
         gc.fill = GridBagConstraints.BOTH;
         getContentPane().add(buttonsPanel, gc);
         
	     getContentPane().setBackground(ConfigManager.background);
	        
	     //setLocationRelativeTo(null);
	     setSize(800, 400);
	     pack();
         Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
         this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
	     //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	     setVisible(true);
		 txtLastName.requestFocus();
		 btnEdit.setEnabled(false);
		 btnDelete.setEnabled(false);
	 }
	 
	 //invokes refreshList which pulls from the database all the awards for a specific student
	 public void searchStudent() {
		 refreshList(txtLastName.getText().trim().replace("'", "^"), txtFirstName.getText().trim().replace("'", "^"));
		 awardsForThisStudent.requestFocus();
	 }
	 
	 public void clearNames() {
		 txtLastName.setText("");
		 txtFirstName.setText("");
		 refreshList(txtLastName.getText().trim().replace("'", "^"), txtFirstName.getText().trim().replace("'", "^"));
		 txtLastName.requestFocus();
		 resetInputWindow();
	 }
	 
	 //pulls from the database all the awards for a specific student and fills out the list model
	 //invoked by many methods
	 public void refreshList(String lastName, String firstName){
		 awardsForThisStudent.removeListSelectionListener(this);
		 listModel.clear();
		 listModel.addElement("New Award");  
		 awardsForStudent = DatabaseManager.searchAwards(lastName, firstName);
		 for (int i=0; i<awardsForStudent.size();i++) {
			 listModel.addElement(awardsForStudent.get(i));
		 }	 
		 awardsForThisStudent.setModel(listModel);
		 awardsForThisStudent.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		 awardsForThisStudent.addListSelectionListener(this);
		 awardsForThisStudent.setSelectedIndex(0);
	 }
	 
	 public void resetInputWindow() {
	     txtYear.setText("");
	     grade.setSelectedItem("9");
	     txtAwardDesc.setText("");
	     awards.setSelectedItem("Honor Roll");
	     txtAwardDesc.setEditable(false);
	 }
	 
	 //valueChanged event in JList - update the fields with info from the student selectd
	 //clear the field if New Award is selected
	 public void valueChanged(ListSelectionEvent e) {
	     if (!e.getValueIsAdjusting()) {
	          JList list = (JList) e.getSource();
	          int selections[] = list.getSelectedIndices();
	          int index = selections[0];
			 //if new award was selected, reset the textfields, and disable btnEdit, btnDelete
			 if (index == 0) { //new award
				 awardsForThisStudent.requestFocus();
				 resetInputWindow();
				 btnEdit.setEnabled(false);
				 btnDelete.setEnabled(false);
				 btnAdd.setEnabled(true);
			 }
			 //update the textFields with appropriate info, and disable btnAdd
			 else {
				 awardsForThisStudent.requestFocus();
				 txtFirstName.setText(awardsForStudent.get(index-1).getFirstName().replace("^", "'"));
				 txtLastName.setText(awardsForStudent.get(index-1).getLastName().replace("^", "'")); 
				 txtAwardDesc.setText(awardsForStudent.get(index-1).getDescription().replace("^", "'"));   
				 awards.setSelectedItem(awardsForStudent.get(index-1).getAward());
				 grade.setSelectedItem(""+awardsForStudent.get(index-1).getGrade());
				 txtYear.setText(""+awardsForStudent.get(index-1).getYear());
				 btnAdd.setEnabled(false);
				 btnEdit.setEnabled(true);
				 btnDelete.setEnabled(true);
			 }
	        }
	 }
	 
	 public void addNewStudent() {
		 int year, gr;
		 String fn, ln, studentAward, descript;
		 if (txtLastName.getText().trim().length()>50) {//check if the first name has more than 50 characters
			 JOptionPane.showMessageDialog(null, "The last name can have maximum 50 characters!!!", "WARNING",
                     JOptionPane.INFORMATION_MESSAGE);
			 txtLastName.requestFocus();
			 return;
		 }
		 else if (txtLastName.getText().trim().length()==0) {//check if the first name has more than 50 characters
			 JOptionPane.showMessageDialog(null, "The last name cannot be empty!!!", "WARNING",
                     JOptionPane.INFORMATION_MESSAGE);
			 txtLastName.requestFocus();
			 return;
		 }
		 else if (txtFirstName.getText().trim().length()>50) {//check if the first name has more than 50 characters
			 JOptionPane.showMessageDialog(null, "The first name can have maximum 50 characters!!!", "WARNING",
                     JOptionPane.INFORMATION_MESSAGE);
			 txtFirstName.requestFocus();
			 return;
		 }
		 else if (txtFirstName.getText().trim().length()==0) {//check if the first name has more than 50 characters
			 JOptionPane.showMessageDialog(null, "The first name cannot be empty!!!", "WARNING",
                     JOptionPane.INFORMATION_MESSAGE);
			 txtFirstName.requestFocus();
			 return;
		 }
		 
		//check if the year entered has 4 digits
		 else if (txtYear.getText().trim().length()!=4) {
			 JOptionPane.showMessageDialog(null, "Invalid year: "+ txtYear.getText()+" !!!", "WARNING",
	             JOptionPane.INFORMATION_MESSAGE);
			 txtYear.requestFocus();
			 return;
		 }
		 else if (txtAwardDesc.getText().trim().length()>50) {//check if the description has more than 50 characters
			 JOptionPane.showMessageDialog(null, "The Description associated with Highest Mark has maximum 50 characters!!!", "WARNING",
                     JOptionPane.INFORMATION_MESSAGE);
			 txtAwardDesc.requestFocus();
			 return;
		 }
		 else {
			 try {
				 year = Integer.parseInt(txtYear.getText().trim());
			 }
			 catch(NumberFormatException nfe) {
				 JOptionPane.showMessageDialog(null, "Invalid year: "+ txtYear.getText()+" !!!", "WARNING",
			             JOptionPane.INFORMATION_MESSAGE);
				 txtYear.requestFocus();
				 return;
			 }
			 try {
				 gr = Integer.parseInt((String)grade.getSelectedItem());
			 }
			 catch(NumberFormatException nfe) {
				 JOptionPane.showMessageDialog(null, "Invalid grade: "+ (String)grade.getSelectedItem()+" !!!", "WARNING",
			             JOptionPane.INFORMATION_MESSAGE);
				 grade.requestFocus();
				 return;
			 }
			 ln = txtLastName.getText().trim().replace("'", "^");
			 fn = txtFirstName.getText().trim().replace("'", "^");
			 descript = txtAwardDesc.getText().trim().replace("'", "^");
			 studentAward = (String)awards.getSelectedItem();
			 refreshList(ln, fn);//pulls all the wards for a specific student
			 for (int i=0; i<awardsForStudent.size(); i++) {
				 //checks is the award to be added already exists for this student -> warning + return
				 if (awardsForStudent.get(i).getLastName().equals(ln) &&	 awardsForStudent.get(i).getFirstName().equals(fn) &&
						 awardsForStudent.get(i).getYear()==year && awardsForStudent.get(i).getGrade()==gr &&
						 awardsForStudent.get(i).getAward().equals(studentAward)) {
					 JOptionPane.showMessageDialog(null, "Award: "+ txtLastName.getText().trim()+", "+ txtLastName.getText().trim()+", year-"+year+", grade-"+gr+", "+ studentAward+" already exists!", "WARNING",
				             JOptionPane.INFORMATION_MESSAGE);
					 resetInputWindow();
					 awardsForThisStudent.requestFocus();
					 return;
				 }
			 }
			 //add the award in awards, refresh the list to show the newly added award, clean the input objects
			 DatabaseManager.addNewStudentAward(ln, fn, year, gr, studentAward, descript); 
			 refreshList(ln, fn);
			 resetInputWindow();
		 }
	 }
	 
	public void deleteStudentAward() {
		int year = Integer.parseInt(txtYear.getText().trim());
		int intGrade = Integer.parseInt(((String)grade.getSelectedItem()).trim());
		DatabaseManager.deleteStudentAward(txtLastName.getText().trim().replace("'", "^"), txtFirstName.getText().trim().replace("'", "^"), year, intGrade, (String)awards.getSelectedItem());
		refreshList(txtLastName.getText(), txtFirstName.getText());
		resetInputWindow();
		awardsForThisStudent.requestFocus();
	 }
		 
	 public void editStudent() {
		int selections[] = awardsForThisStudent.getSelectedIndices();
        int index = selections[0];
		Student student = awardsForStudent.get(index-1);
		String oldLn = student.getLastName().trim().replace("'", "^");
		String oldFn = student.getFirstName().trim().replace("'", "^");
		int oldYear = student.getYear();
		int oldGrade = student.getGrade();
		String oldAward = student.getAward();
		String oldDescription = student.getDescription().trim().replace("'", "^");
		String newLn = txtLastName.getText().trim().replace("'", "^");
		String newFn = txtFirstName.getText().trim().replace("'", "^");
		String newDescription = txtAwardDesc.getText().trim().replace("'", "^");
		int newYear = Integer.parseInt(txtYear.getText().trim());
		int newGrade = Integer.parseInt(((String)grade.getSelectedItem()).trim());
		String newAward = (String)awards.getSelectedItem();
		DatabaseManager.editStudentAward(oldLn, oldFn, oldYear, oldGrade, oldAward, oldDescription, newLn, newFn, newYear, newGrade, newAward, newDescription);
		refreshList(txtLastName.getText(), txtFirstName.getText());
		resetInputWindow();
		awardsForThisStudent.requestFocus();
	 }
}