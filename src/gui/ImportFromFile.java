package gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
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

import dataStructures.AwardInfo;
import dataStructures.Student;
import txtimport.TxtScanner;
import utilities.ConfigManager;
import utilities.DatabaseManager;

/**
 * @author Manoil (April 2018)
 * GUI interface that allows selecting the type of award to be imported and the year
 * Reads from text files and loads the MySQL database
 * Uses TxtScanner to scan line by line the text files
 * Invoked from Main when selecting "Import from File"
 */
public class ImportFromFile extends JFrame implements ListSelectionListener{
	ImageIcon marcyLogo;
	Font ncaa;
	Font montserrat;
	private ArrayList<Student> awardsForTypeYear;
	private HashMap<String, AwardInfo> awardsInfo ;

	//gui elements
	JLabel lblLogo1, lblLogo2, lblTitle, lblLeft, lblRight, lblAwardType, lblYear, lblConnect, lblDrop;
	JButton btnImport, btnSearch, btnDelete, btnConnect, btnDrop;
	JTextArea txtInstructions;
	JTextField txtYear;
	JPanel buttonsPanel;
	private JComboBox awards;
	private JList<String> awardsForYear;
	private DefaultComboBoxModel<String> awardsModel;//used for awards JComboBox
	JList<String> awardsList;
	DefaultListModel listModel;//used to make the JList
	JScrollPane scrollAwardList;
	String awardToImport="";
	public ImportFromFile(ImageIcon marcyLogo, Font ncaa, Font montserrat) {
		this.marcyLogo = marcyLogo;
		this.ncaa = ncaa;
		this.montserrat = montserrat;
		//init must be called before running any statement, otherwise connections and database will not work.
		//it checks if the awards database and the awards and award_type tables exit, and it creates them if not 
		DatabaseManager.init();
		listModel = new DefaultListModel();

		lblLogo1 = new JLabel(marcyLogo);
		lblLogo2 = new JLabel(marcyLogo);
		lblTitle = new JLabel("DATABASE MAINTAIN / IMPORT FROM TXT FILES");
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
		txtInstructions = new JTextArea(" - The text files need to be in the appropriate folder in desktop\\awards.\n"
				+ " - Press the SEARCH button to search for specific awards\n"
				+ " - To delete an all the awards for a specific year and press DELETE.\n"
				+ " - To import an all the awards for a specific year and press IMPORT.\n"
				+ " - To complete delete the database and start up fresh, press DELETE DATABASE.\n"
				+ " - !!! It is adviced to search first and check what is already loaded before importing.\n");
		txtInstructions.setEditable(false);
		txtInstructions.setBackground(ConfigManager.background);
		txtInstructions.setForeground(ConfigManager.description);
		txtInstructions.setFont(fontInstructions);

		Font fontLabels = montserrat;
		fontLabels = fontLabels.deriveFont(Font.BOLD, (int)(ConfigManager.titleSize/4));


		lblConnect = new JLabel("Check MySql Connection: ");
		lblConnect.setOpaque(true);
		lblConnect.setBackground(ConfigManager.background);
		lblConnect.setForeground(ConfigManager.motto);
		lblConnect.setFont(fontLabels);

		lblDrop = new JLabel("Drop Database: ");
		lblDrop.setOpaque(true);
		lblDrop.setBackground(ConfigManager.background);
		lblDrop.setForeground(ConfigManager.motto);
		lblDrop.setFont(fontLabels);

		btnConnect = new JButton ("CONNECT");
		btnConnect.setFont(fontLabels);
		btnConnect.addActionListener(a -> connect());

		btnDrop = new JButton ("DELETE DATABASE");
		btnDrop.setFont(fontLabels);
		btnDrop.addActionListener(a -> drop());

		lblAwardType = new JLabel(" Award Type: ");
		lblAwardType.setOpaque(true);
		lblAwardType.setBackground(ConfigManager.background);
		lblAwardType.setForeground(ConfigManager.motto);
		lblAwardType.setFont(fontLabels);

		awardsModel = new DefaultComboBoxModel<String>();
		awardsModel.addElement("Honor Roll");
		awardsModel.addElement("Highest Mark Per Subject");
		awardsModel.addElement("Other Awards");

		awards = new JComboBox<String>(awardsModel);
		awards.setFont(fontInstructions);
		
		awards.addItemListener(e-> {
			String studentAward = (String)awards.getSelectedItem();
		    awardToImport = (String)awards.getSelectedItem();
		});
		awards.setSelectedIndex(1);//to refresh the list
		awards.setSelectedIndex(0);

		lblYear = new JLabel(" Year: ");
		lblYear.setOpaque(true);
		lblYear.setBackground(ConfigManager.background);
		lblYear.setForeground(ConfigManager.motto);
		lblYear.setFont(fontLabels);

		txtYear = new JTextField(4);
		txtYear.setFont(fontInstructions);

		btnSearch = new JButton ("SEARCH");
		btnSearch.setFont(fontLabels);
		btnSearch.addActionListener(a -> searchAwardYear());
		btnImport = new JButton ("IMPORT");
		btnImport.setFont(fontLabels);
		btnImport.addActionListener(a -> importAwardYear());
		btnDelete = new JButton ("DELETE");
		btnDelete.setFont(fontLabels);
		btnDelete.addActionListener(a -> deleteAwardYear());
		buttonsPanel = new JPanel();
		buttonsPanel.add(btnSearch);
		buttonsPanel.add(btnImport);
		buttonsPanel.add(btnDelete);
		buttonsPanel.setBackground(ConfigManager.background);

		listModel.addElement(""); 	
		awardsForYear = new JList<String>(listModel);
		awardsForYear.setVisibleRowCount(150);
		scrollAwardList = new JScrollPane(awardsForYear, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		awardsForYear.setFont(fontInstructions);
		awardsForYear.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		awardsForYear.setSelectedIndex(0);
		awardsForYear.addListSelectionListener(this);

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
		gc.gridwidth = 4;
		gc.fill = GridBagConstraints.BOTH;
		gc.anchor = GridBagConstraints.NORTH;
		getContentPane().add(lblTitle, gc);

		gc.gridx = 5;
		gc.gridwidth = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.NORTHEAST;
		getContentPane().add(lblLogo2, gc);

		gc.gridx = 1;
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
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.WEST;
		getContentPane().add(lblConnect, gc);

		gc.gridx = 2;
		getContentPane().add(btnConnect, gc);

		gc.gridx = 3;
		getContentPane().add(lblDrop, gc);

		gc.gridx = 4;
		getContentPane().add(btnDrop, gc);

		gc.gridx = 1;
		gc.gridy = 3;
		getContentPane().add(lblAwardType, gc);

		gc.gridx = 2;
		getContentPane().add(awards, gc);

		gc.gridx = 3;
		getContentPane().add(lblYear, gc);

		gc.gridx = 4;
		getContentPane().add(txtYear, gc);

		gc.gridx = 1;
		gc.gridy = 4;
		gc.gridwidth = 4;
		gc.anchor = GridBagConstraints.NORTH;
		gc.fill = GridBagConstraints.HORIZONTAL;
		getContentPane().add(buttonsPanel, gc);

		gc.gridx = 1;
		gc.gridy = 5;
		gc.anchor = GridBagConstraints.WEST;
		gc.fill = GridBagConstraints.HORIZONTAL;
		getContentPane().add(scrollAwardList, gc);

		connect();
		getContentPane().setBackground(ConfigManager.background);

		//setLocationRelativeTo(null);
		//setSize(800, 400);
		pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		//this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		this.setLocation(dim.width/2-this.getSize().width/2, 30);
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		txtYear.requestFocus();
	}

	//tests the connection to mysql
	public void connect() {
		if(!DatabaseManager.connect()) {
			System.out.println("Unable to connect to database.");
			JOptionPane.showMessageDialog(null, "Error connecting to the data base", "Error", JOptionPane.ERROR_MESSAGE);
			System.err.println("Error connecting to the data base");
			return;
		}
		else {
			System.out.println("Connected To Database.");
			JOptionPane.showMessageDialog(null, "Connected To The Database", "Information", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
	}

	//drop the awards database
	public void drop() {
		int option = JOptionPane.showConfirmDialog(null,
				"Are you sure you want to recreate the database","Input", 
				JOptionPane.YES_NO_OPTION);	
		if (option==JOptionPane.YES_OPTION) {
			DatabaseManager.dropDatabase();
			JOptionPane.showMessageDialog(null, "Database Recreted", "Information", JOptionPane.INFORMATION_MESSAGE);
			System.out.println("database recreated");
		}
	}

	//searches all the awards for a specific type (awardToImport) and a specific year 
	//invokes refreshList which pulls from the database
	public void searchAwardYear() {
		int year;
		try {
			year = Integer.parseInt(txtYear.getText().trim());
		}
		catch(NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null, "Invalid year: "+ txtYear.getText()+" !!!", "WARNING",
					JOptionPane.INFORMATION_MESSAGE);
			txtYear.requestFocus();
			return;
		}
		refreshList(awardToImport, year);
		awardsForYear.requestFocus(); 
	}

	//pulls from the database all the awards for a specific type (awardToImport) and year
	//refreshes the listModel of the awardsForYear JList
	public void refreshList(String awardToImport, int year){
		awardsForYear.removeListSelectionListener(this);
		listModel.clear();
		
        if (awardToImport.equals("Honor Roll")||(awardToImport.equals("Highest Mark Per Subject"))){
        	awardsForTypeYear = DatabaseManager.searchAwardsTypeYear(awardToImport, year);
        }
        else {
        	awardsForTypeYear = DatabaseManager.searchOtherAwardsTypeYear( year);
        }
        for (int i=0; i<awardsForTypeYear.size();i++) {
			listModel.addElement(awardsForTypeYear.get(i));
		}	 
		awardsForYear.setModel(listModel);
		awardsForYear.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		awardsForYear.addListSelectionListener(this);
		awardsForYear.setSelectedIndex(0);
	}

	//imports from files all the data and loads it in mysql
	//the files need to be in the appropriate folder, named and formated properly
	public void importAwardYear() {
		int year;
		try {
			year = Integer.parseInt(txtYear.getText().trim());
		}
		catch(NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null, "Invalid year: "+ txtYear.getText()+" !!!", "WARNING",
					JOptionPane.INFORMATION_MESSAGE);
			txtYear.requestFocus();
			return;
		}
		int option = JOptionPane.showConfirmDialog(null,
				"Do you want to import from file the "+awardToImport+" awards for "+year+"?","Input", 
				JOptionPane.YES_NO_OPTION);	
		if (option==JOptionPane.YES_OPTION) {
			if (awardToImport.equals("Honor Roll")) {
				//input honor roll
				for(File f : new File(System.getProperty("user.home"), "/Desktop/awards/awardsTextFiles/HonorRoll").listFiles()) {
					String name = f.getName();
					//import only from the files for a specific year
					if (name.contains(""+year)) {
						name = name.replace(".txt", "");
						//split by - to isolate the award, the year, and the grade
						String[] split = name.split("-");
						System.out.println();
						System.out.println(f.getName());
						System.out.println();
						//param - file, award, year, grade
						TxtScanner.readHonorRoll(f, split[0].replaceAll("'", "|"), Integer.valueOf(split[2]), Integer.valueOf(split[1]));

					}
				}
				 //a hashmap that contains the award as key and an AwardInfo object for each key
				//load awardsInfo to check if it contains an entry "Honor Roll"
				//if not "Honor Roll" needs to be adeed in award_type - DatabaseManager.addNewAwardInAward_Type
				 awardsInfo = DatabaseManager.pullAwardsInfo();
				 if (!awardsInfo.containsKey("Honor Roll")) {
					    String description = "Students who have achieved an overall average of 80 percent or greater in all of their courses";
					 	DatabaseManager.addNewAwardInAward_Type("Honor Roll", description, 1);//1 means it needs to be displayed year by year
			
				 }
			}

			
			if (awardToImport.equals("Highest Mark Per Subject")) {
				//input highest mark
				for(File f : new File(System.getProperty("user.home"), "/Desktop/awards/awardsTextFiles/HighestMarkPerSubject").listFiles()) {
					String name = f.getName();
					//import only from the files for a specific year
					if (name.contains(""+year)) {
						name = name.replace(".txt", "");
						String[] split = name.split("-");
						System.out.println();
						System.out.println(f.getName());
						System.out.println(split[1]);
						//param - file, award, year
						TxtScanner.readHighestMark(f, split[0].replaceAll("'", ""), Integer.valueOf(split[1]));
					}
				}
				//a hashmap that contains the award as key and an AwardInfo object for each key
				//load awardsInfo to check if it contains an entry "Highest Mark Per Subject"
				//if not "Highest Mark Per Subject" needs to be adeed in award_type - DatabaseManager.addNewAwardInAward_Type
				awardsInfo = DatabaseManager.pullAwardsInfo();
				 if (!awardsInfo.containsKey("Highest Mark Per Subject")) {
					    String description = "Students who have achieved the highest mark in a specific subject";
					 	DatabaseManager.addNewAwardInAward_Type("Highest Mark Per Subject", description, 1);
			
				 }
			}

			if (awardToImport.equals("Other Awards")) {
				//input other awards
				for(File f : new File(System.getProperty("user.home"), "/Desktop/awards/awardsTextFiles/OtherAwards").listFiles()) {
					String name = f.getName();
					if (name.contains(""+year)) {
						name = name.replace(".txt", "");
						String[] split = name.split("-");
						System.out.println();
						System.out.println(f.getName());
						System.out.println(split[1]);
						TxtScanner.readOtherAwards(f, Integer.valueOf(split[1]));
					}
				}
				//a hashmap that contains the award as key and an AwardInfo object for each key
				//load awardsInfo to check if it contains an entry "Top Student"
				//if not "Top Student" needs to be adeed in award_type - DatabaseManager.addNewAwardInAward_Type
				awardsInfo = DatabaseManager.pullAwardsInfo();
				 if (!awardsInfo.containsKey("Top Student")) {
					    String description = "This award is presented to the top three students with the highest academic average in each Grade.";
					 	DatabaseManager.addNewAwardInAward_Type("Top Student", description, 0);
			
				 }
				 if (!awardsInfo.containsKey("Spirit Award")) {
					    String description = "This award is chosen by staff members and is awarded to the student who exhibits a high degree  of Catholic values. The recipient demonstrates through attitudes and behaviours, the attributes of a positive Catholic role model in the St. Marcellinus Community.";
					 	DatabaseManager.addNewAwardInAward_Type("Spirit Award", description, 0);
			
				 }
				 if (!awardsInfo.containsKey("Mother Theresa Award")) {
					    String description = "This award is presented to the student who has demonstrated exemplary service of student leadership for the greater by providing assistance or services to those in need.";
					 	DatabaseManager.addNewAwardInAward_Type("Mother Theresa Award", description, 0);
			
				 }
				 if (!awardsInfo.containsKey("Archbishop Oscar Romero Award")) {
					    String description = "This award is presented to the graduating student who challenges the status quo for the improvement of the human condition and social justice in his/her community.";
					 	DatabaseManager.addNewAwardInAward_Type("Archbishop Oscar Romero Award", description, 0);
			
				 }
				 if (!awardsInfo.containsKey("Thomas J. Reilly Scholarship")) {
					    String description = "This award is presented to a graduating student who has demonstrated excellence in the study of Modern Languages.";
					 	DatabaseManager.addNewAwardInAward_Type("Thomas J. Reilly Scholarship", description, 0);
			
				 }
				 if (!awardsInfo.containsKey("Joe Hugel Award")) {
					    String description = "This award is presented by the Dufferin-Peel Catholic District School Board to the top Grade 10 student in recognition of academic excellence.";
					 	DatabaseManager.addNewAwardInAward_Type("Joe Hugel Award", description, 0);
			
				 }
				 if (!awardsInfo.containsKey("Eco Club Award")) {
					    String description = "This award is presented to the student who demonstrated exemplary environmental stewardship through ongoing involvement in the eco club activities.";
					    //String description = "This award is presented to the student who \n demonstrated exemplary environmental stewardship \n  through ongoing involvement \n  in the eco club activities.";
					 	DatabaseManager.addNewAwardInAward_Type("Eco Club Award", description, 0);
				 }
				 if (!awardsInfo.containsKey("Governor General Award")) {
					    String description = "This award is granted to the graduating student with the highest overall academic average in all Grade11 and Grade 12 courses.";
					 	DatabaseManager.addNewAwardInAward_Type("Governor General Award", description, 0);
				 }
				 if (!awardsInfo.containsKey("Governor General^s Award")) {
					    String description = "The Governor General Award recognizes outstanding scholastic achievement and is awarded to the graduating student with the highest overall average in grade 11 and 12 courses.";
					 	DatabaseManager.addNewAwardInAward_Type("Governor General^s Award", description, 0);
				 }
				 if (!awardsInfo.containsKey("Edward King Memorial Award")) {
					    String description = "This award is given to a student in the graduating class who demonstrates academic excellence and leadership in the school programs.";
					 	DatabaseManager.addNewAwardInAward_Type("Eduard King Memorial Award", description, 0);
				 }
				 if (!awardsInfo.containsKey("Knights of Columbus Award")) {
					    String description = "This award is  granted to a graduating student who excels in academics, participates in St. Francis Xavier Church, and embodies the four principles of the Knights of Columbus: Charity, unity, Fraternity, and Patriotism.";
					 	DatabaseManager.addNewAwardInAward_Type("Knights of Columbus Award", description, 0);
				 }
				 if (!awardsInfo.containsKey("Lieut. Governor^s Community")) {
					    String description = "This award recognizes an exemplary volunteer among the graduating class. It pays tribute to the graduating student for exemplary community contribution or outstanding achievement in volunteer activity.";
					 	DatabaseManager.addNewAwardInAward_Type("Lieut. Governor^s Community", description, 0);
				 }
				 if (!awardsInfo.containsKey("Jim Dutfield Memorial Award")) {
					    String description = "This award is presented to a graduating multi-sport student-athlete who has achieved an overall average of 80% or higher while demonstrating positive spirit, leadership and commitment.";
					 	DatabaseManager.addNewAwardInAward_Type("Jim Dutfield Memorial Award", description, 0);
				 }
				 if (!awardsInfo.containsKey("Jacor Marketing Award")) {
					    String description = "This award is given to the student who best exemplifies student equality by taking measures to prevent bullying and promote inclusion within the student body.";
					 	DatabaseManager.addNewAwardInAward_Type("Jacor Marketing Award", description, 0);
				 }
				 if (!awardsInfo.containsKey("Shawn D. MacIntosh Award")) {
					    String description = "This award is given to an exemplary student who pursues a career in construction trades.";
					 	DatabaseManager.addNewAwardInAward_Type("Shawn D. MacIntosh Award", description, 0);
				 }
				 if (!awardsInfo.containsKey("Chaplaincy Award")) {
					    String description = "This award is given to the graduating student who selflessly goes beyound expectations to help the Chaplain with food and clothing drives, as well as taking on leadership roles with different events.";
					 	DatabaseManager.addNewAwardInAward_Type("Chaplaincy Award", description, 0);
				 }
				 if (!awardsInfo.containsKey("Vice Principal^s Award")) {
					    String description = "The Vice Principal^s Award recognizes outstanding scholastic achievements. It is awarded to the graduating student with the second highest average in Grade 12 courses.";
					 	DatabaseManager.addNewAwardInAward_Type("Vice Principal^s Award", description, 0);
				 }
				 if (!awardsInfo.containsKey("Principal^s Award")) {
					    String description = "The Principal^s Award recognizes outstanding scholastic achievements. It is awarded to the graduating student with the highest average in Grade 12 courses.";
					 	DatabaseManager.addNewAwardInAward_Type("Principal^s Award", description, 0);
				 }
			}
			//refreshed the list, once the import is finished
			refreshList(awardToImport, year);
		}//end if yes 
	}
	
	//deletes all the awards for a specific type (awardToImport) and a specific year 
	//if awardToImport is "Other Awards" delete all "Top Student", "Mother Thresa", "Joe Hugel" etc.
    //invokes DatabaseManager.deleteAwardYear
	public void deleteAwardYear() {
		int year;
		try {
			year = Integer.parseInt(txtYear.getText().trim());
		}
		catch(NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null, "Invalid year: "+ txtYear.getText()+" !!!", "WARNING",
					JOptionPane.INFORMATION_MESSAGE);
			txtYear.requestFocus();
			return;
		}
		int option = JOptionPane.showConfirmDialog(null,
				"Do you want to delete the "+awardToImport+" awards for "+year+"?","Warning", 
				JOptionPane.YES_NO_OPTION);	
		if ((option==JOptionPane.YES_OPTION) && (awardToImport.equals("Honor Roll") || awardToImport.equals("Highest Mark Per Subject"))){
			DatabaseManager.deleteAwardYear(awardToImport, year);
			refreshList(awardToImport, year);
			awardsForYear.requestFocus();
		}
		if ((option==JOptionPane.YES_OPTION) && (awardToImport.equals("Other Awards"))){
			DatabaseManager.deleteAwardYear("Top Student", year);
			DatabaseManager.deleteAwardYear("Spirit Award", year);
			DatabaseManager.deleteAwardYear("Mother Theresa Award", year);
			DatabaseManager.deleteAwardYear("Archbishop Oscar Romero Award", year);
			DatabaseManager.deleteAwardYear("Thomas J. Reilly Scholarship", year);
			DatabaseManager.deleteAwardYear("Joe Hugel Award", year);
			DatabaseManager.deleteAwardYear("Eco Club Award", year);
			DatabaseManager.deleteAwardYear("Governor General Award", year);
			DatabaseManager.deleteAwardYear("Governor General^s Award", year);
			DatabaseManager.deleteAwardYear("Edward King Memorial Award", year);
			DatabaseManager.deleteAwardYear("Knights of Columbus Award", year);
			DatabaseManager.deleteAwardYear("Lieut. Governor^s Community", year);
			DatabaseManager.deleteAwardYear("Jim Dutfield Memorial Award", year);
			DatabaseManager.deleteAwardYear("Jacor Marketing Award", year);
			DatabaseManager.deleteAwardYear("Shawn D. MacIntosh Award", year);
			DatabaseManager.deleteAwardYear("Chaplaincy Award", year);
			DatabaseManager.deleteAwardYear("Vice Principal^s Award", year);
			DatabaseManager.deleteAwardYear("Principal^s Award", year);
			
			refreshList(awardToImport, year);
			awardsForYear.requestFocus();
		}
		
	}

	//can be used to select/edit a certain student in the 
	public void valueChanged(ListSelectionEvent e) {
		/*  if (!e.getValueIsAdjusting()) {
	          JList list = (JList) e.getSource();
	          int selections[] = list.getSelectedIndices();
	          int index = selections[0];
			 //if new award was selected, reset the textfields, and disable btnEdit, btnDelete

				 awardsForThisStudent.requestFocus();
				 txtFirstName.setText(awardsForStudent.get(index-1).getFirstName());
				 txtLastName.setText(awardsForStudent.get(index-1).getLastName()); 
				 txtAwardDesc.setText(awardsForStudent.get(index-1).getDescription());   
				 awards.setSelectedItem(awardsForStudent.get(index-1).getAward());
				 grade.setSelectedItem(""+awardsForStudent.get(index-1).getGrade());
				 txtYear.setText(""+awardsForStudent.get(index-1).getYear());
				 btnAdd.setEnabled(false);
				 btnEdit.setEnabled(true);
				 btnDelete.setEnabled(true);
			 }
	        }*/
	}
}
