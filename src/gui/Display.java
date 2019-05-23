package gui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import dataStructures.AwardInfo;
import dataStructures.FinishLoadingDataListener;
import dataStructures.Page;
import dataStructures.PagesMaker;
import dataStructures.Student;
import utilities.ConfigManager;
import utilities.DatabaseManager;

public class Display extends JFrame implements FinishLoadingDataListener{
	//a hashmap with an entry for each award, and an  object AwardInfo(name/desc/yearByYear). 
	//The key corresponds to the award field in the table award_type, basically the name of the award e.g. honor roll 
	private HashMap<String, AwardInfo> awardsInfo ;
	//Pages Maker is the object that pulls the yearByYearStudents and studentsNotYearByYeaassociatedr 
	//from the database, and it creates an ArrayList of Page objects. Each Page object will 
	//have all the required info (title, description, year, pageNo, ofPages).
	//If the year is 0 it would be a page that's not yea by year - the other awards not honor roll
	private PagesMaker pagesMaker;
	private ArrayList<Page> pages;
	int indexInPages = 0;

	//GUI elements
	private JLabel lblMoto, lblTitle, lblDesc1, lblDesc2, lblDesc3;
	//ImagePanel lblLogo1, lblLogo2;
	JLabel lblLogo1, lblLogo2;
	private JLabel lblTopDescription;
	private JPanel titlePanel, centerPanel, honorRollPanel, highestMarkPanel, otherAwardsPanel;
	final static String BYYEAR_PANEL = "BYYEAR_PANEL";
	final static String OTHERAWARDS_PANEL = "OTHERAWARDS_PANEL";
	private ArrayList<LabelAward> honorRoll_names = new ArrayList<>();
	private ArrayList<LabelAward> highestMark_names = new ArrayList<>();
	private ArrayList<LabelWithDescription> otherAwards_names = new ArrayList<>();
	CardLayout cl = new CardLayout();
	//private ArrayList<JLabel> names = new ArrayList<>();
	private Font ncaa, montserrat;
	private ImageIcon marcyLogo;
	private Timer myTimer;//a Timer used to generate action events
	private int transitionTime = ConfigManager.transitionTime;
	private int tick = 0;

	public Display(ImageIcon marcyLogo, Font ncaa, Font montserrat) {
		//invoke the PageMaker make pages. This will take a while - pulls from the database and loads
		//the ArrayList of Page objects, called Pages, in the PagesMaker class
		//register this class as a listener, so it would be notified the the loading process is over
		//once the FinishLoadingData notification is received, this class can call the getPages() method
		//of the PagesMaker class, to retrieve the ArrayList of Page objects and start displaying them
		this.marcyLogo = marcyLogo;
		this.ncaa = ncaa;
		this.montserrat = montserrat;
		this.setBackground(ConfigManager.background);
		this.getContentPane().setBackground(ConfigManager.background);
		//lblLogo1 = new ImagePanel(marcyLogo);
		//lblLogo2 = new ImagePanel(marcyLogo);
		lblLogo1 = new JLabel(marcyLogo);
		lblLogo2 = new JLabel(marcyLogo);
		setLayout(new GridBagLayout());

		lblTitle = new JLabel("MARCY AWARDS");
		Font fontTitle = ncaa;
		fontTitle = fontTitle.deriveFont(Font.BOLD, ConfigManager.titleSize);
		lblTitle.setFont(fontTitle);
		lblTitle.setOpaque(false);
		lblTitle.setForeground(ConfigManager.title);
		lblTitle.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		lblTitle.setHorizontalAlignment(JLabel.CENTER);

		Font fontDesc = ncaa;
		fontDesc = fontDesc.deriveFont(Font.BOLD, ConfigManager.descSize);
		
		this.setGlassPane(new ImagePanel(marcyLogo));
		this.getGlassPane().repaint();

		lblTopDescription = new JLabel("LOADING  DATA ............");
		//lblTopDescription.setBackground(ConfigManager.background);
		lblTopDescription.setOpaque(false);
		lblTopDescription.setHorizontalAlignment(JLabel.CENTER);
		lblTopDescription.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		lblTopDescription.setForeground(ConfigManager.description);
		lblTopDescription.setFont(fontDesc);

		Font fontMoto = ncaa;
		fontMoto = fontMoto.deriveFont(Font.BOLD, ConfigManager.mottoSize);
		lblMoto = new JLabel("LET  EVERY  SPIRIT  SOAR", JLabel.CENTER);
		lblMoto.setForeground(ConfigManager.motto);
		lblMoto.setFont(fontMoto); 

		
		titlePanel = new JPanel();
		titlePanel.setLayout(new GridBagLayout());
		GridBagConstraints tgc = new GridBagConstraints();
		tgc.gridx = 0;
		tgc.gridy = 0;
		tgc.gridwidth =1;
		tgc.gridheight = 1;
		tgc.weightx = 100.0;
		tgc.weighty = 100.0;
		tgc.insets = new Insets(0, 0, 0, 0);
		tgc.anchor = GridBagConstraints.NORTH;
		tgc.fill = GridBagConstraints.BOTH;
		titlePanel.setBackground(ConfigManager.background);
		titlePanel.add(lblTitle, tgc);
		tgc.gridy = 1;
		titlePanel.add(lblTopDescription, tgc);

		centerPanel = new JPanel();
		centerPanel.setOpaque(false);
		honorRollPanel = new JPanel();
		honorRollPanel.setOpaque(false);
		highestMarkPanel = new JPanel();
		highestMarkPanel.setOpaque(false);
		otherAwardsPanel = new JPanel();
		otherAwardsPanel.setOpaque(false);

		highestMarkPanel.setLayout(new GridLayout(ConfigManager.rows_HighestMark, ConfigManager.columns_HighestMark));
		createHighestMarkCells();
		for (int i=0; i<ConfigManager.rows_HighestMark*ConfigManager.columns_HighestMark;i++) {
			highestMarkPanel.add(highestMark_names.get(i));
		}
		centerPanel.add(highestMarkPanel);

		honorRollPanel.setLayout(new GridLayout(ConfigManager.rows_HonorRoll, ConfigManager.columns_HonorRoll));
		createHonorRollCells();
		for (int i=0; i<ConfigManager.rows_HonorRoll*ConfigManager.columns_HonorRoll;i++) {
			honorRollPanel.add(honorRoll_names.get(i));
		}

		otherAwardsPanel.setLayout(new GridLayout(ConfigManager.otherAwards_rows, ConfigManager.otherAwards_columns));
		createOtherAwardsCells();
		for (int i=0; i<ConfigManager.otherAwards_rows*ConfigManager.otherAwards_columns;i++) {
			otherAwardsPanel.add(otherAwards_names.get(i));
		}
		//cl.show(centerPanel, BYYEAR_PANEL);
		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth =1;
		gc.gridheight = 1;
		gc.weightx = 100.0;
		gc.weighty = 100.0;
		gc.insets = new Insets(20, 20, 0, 0);
		gc.anchor = GridBagConstraints.NORTHWEST;
		gc.fill = GridBagConstraints.NONE;
		getContentPane().add(lblLogo1, gc);

		gc.gridx = 1;
		gc.insets = new Insets(20, 0, 0, 0);
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.anchor = GridBagConstraints.NORTH;
		getContentPane().add(titlePanel, gc);

		gc.gridx = 2;
		gc.insets = new Insets(20, 0, 0, 20);
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.NORTHEAST;
		getContentPane().add(lblLogo2, gc);

		gc.gridx = 0;
		gc.gridy = 1;
		gc.gridwidth = 3;
		gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.NORTH;
		//gc.anchor = GridBagConstraints.NONE;
		gc.fill = GridBagConstraints.HORIZONTAL;
		//centerPanel.setBackground(Color.pink);
		getContentPane().add(centerPanel, gc);

		gc.gridy = 2;
		gc.gridx = 0;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.SOUTH;
		getContentPane().add(lblMoto, gc);

		setUndecorated(true);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setVisible(true);

		myTimer = new Timer (1000, new ActionListener(){
			public void actionPerformed (ActionEvent evt){
				tick++;
				if (tick > transitionTime) {//switch events
					tick = 0;
					//System.out.println(indexInPages);
					indexInPages++;
					if (indexInPages >= pages.size()){
						indexInPages = 0;
					}
					switchPage(pages.get(indexInPages));
				}
			}
		});
		//myTimer.start();
		awardsInfo = DatabaseManager.pullAwardsInfo();
		pagesMaker = new PagesMaker();
		pagesMaker.addFinishLoadingDataListener(this);
		pagesMaker.makePages();
	}

	/**
	 * Loads the JLabel cells with correct font and size. Setting label.setText(String) will update the text in the label
	 */
	/*private void createCells() {
		// private Color topColor, rightColor, bottomColor, leftColor, lineColor;
		//AdvancedBevelBorder border = new AdvancedBevelBorder(new Color(120, 172, 220), new Color(55, 93, 128),
		//        new Color(73, 124, 169), new Color(150, 191, 229), new Color(36, 83, 126), 10);
		AdvancedBevelBorder border = new AdvancedBevelBorder(Color.GRAY, Color.GRAY,
				Color.GRAY, Color.GRAY, Color.BLACK, 5);
		for(int i = 0; i < ConfigManager.amount; i++) {
			JLabel label = new JLabel(i + "", SwingConstants.CENTER);
			//JLabel label = new JLabel(i + "", smallIcon, SwingConstants.CENTER);
			Font fontnames = montserrat.deriveFont(Font.PLAIN, (int)(ConfigManager.namesSize));
			label.setFont(fontnames);
			label.setOpaque(true);
			label.setBackground(ConfigManager.background);
			names.add(label);
			label.setBorder(border);
			label.setForeground(ConfigManager.motto);
			this.add(label);
		}
	}*/

	private void createHighestMarkCells() {
		// private Color topColor, rightColor, bottomColor, leftColor, lineColor;
		AdvancedBevelBorder border = new AdvancedBevelBorder(Color.GRAY, Color.GRAY,
				Color.GRAY, Color.GRAY, Color.BLACK, 4);
		Font fontnames = montserrat.deriveFont(Font.BOLD, (int)(ConfigManager.namesSize));
		for(int i = 0; i < ConfigManager.rows_HighestMark * ConfigManager.columns_HighestMark; i++) {
			LabelAward cell = new LabelAward(montserrat, ConfigManager.motto);
			//JLabel label = new JLabel(i + "", smallIcon, SwingConstants.CENTER);
			highestMark_names.add(cell);
			cell.setBorder(border);
			highestMarkPanel.add(cell);
		}
	}

	private void createHonorRollCells() {
		// private Color topColor, rightColor, bottomColor, leftColor, lineColor;
		AdvancedBevelBorder border = new AdvancedBevelBorder(Color.GRAY, Color.GRAY,
				Color.GRAY, Color.GRAY, Color.BLACK, 4);
		Font fontnames = montserrat.deriveFont(Font.PLAIN, (int)(ConfigManager.namesSize));
		for(int i = 0; i < ConfigManager.rows_HonorRoll * ConfigManager.columns_HonorRoll; i++) {
			LabelAward cell = new LabelAward(montserrat, ConfigManager.motto);
			//JLabel label = new JLabel(i + "", smallIcon, SwingConstants.CENTER);
			honorRoll_names.add(cell);
			cell.setBorder(border);
			honorRollPanel.add(cell);
		}
	}

	private void createOtherAwardsCells() {
		// private Color topColor, rightColor, bottomColor, leftColor, lineColor;
		AdvancedBevelBorder border = new AdvancedBevelBorder(Color.GRAY, Color.GRAY,
				Color.GRAY, Color.GRAY, Color.BLACK, 4);
		Font fontnames = montserrat.deriveFont(Font.PLAIN, (int)(ConfigManager.namesSize));
		for(int i = 0; i < ConfigManager.otherAwards_rows*ConfigManager.otherAwards_columns; i++) {
			LabelWithDescription cell = new LabelWithDescription(montserrat, ConfigManager.motto);
			//JLabel label = new JLabel(i + "", smallIcon, SwingConstants.CENTER);
			otherAwards_names.add(cell);
			cell.setBorder(border);
			otherAwardsPanel.add(cell);
		}
	}

	public void finishLoadingData() {
		pages = pagesMaker.getPages();
		//System.out.println(pages.get(20));
		switchPage(pages.get(0));
		myTimer.start();
	}

	public void switchPage(Page crtPage) {
		//a year by year page
		if (crtPage.getYear()!=0) {
			String description = crtPage.getAwardInfo().getDescription();
			lblTopDescription.setText(description);
			lblTitle.setText(crtPage.getAwardInfo().getAward() +",   "+(crtPage.getYear()-1)+"-"+crtPage.getYear()+",   "+crtPage.getPageNo()+" / "+crtPage.getOfPages());
			if (crtPage.getAwardInfo().getAward().equals("Highest Mark Per Subject")) {
				for(int i = 0; i < (ConfigManager.rows_HighestMark * ConfigManager.columns_HighestMark); i++) {
					highestMark_names.get(i).setVisible(false);
				}

				for (int i=0; i<crtPage.getStudentsOnPage().size();i++) {
					Student student = crtPage.getStudentsOnPage().get(i);
					//formatLabel(names.get(i), student.getLastName(), student.getFirstName(), student.getGrade(), student.getYear(), student.getAward());
					formatCell(highestMark_names.get(i), student);
					highestMark_names.get(i).setVisible(true);
				}
				centerPanel.remove(honorRollPanel);
				centerPanel.remove(otherAwardsPanel);
				centerPanel.add(highestMarkPanel);
				//cl.show(centerPanel, BYYEAR_PANEL);
			}
			else if (crtPage.getAwardInfo().getAward().equals("Honor Roll")) {
				for(int i = 0; i < (ConfigManager.rows_HonorRoll * ConfigManager.columns_HonorRoll); i++) {
					honorRoll_names.get(i).setVisible(false);
				}

				for (int i=0; i<crtPage.getStudentsOnPage().size();i++) {
					Student student = crtPage.getStudentsOnPage().get(i);
					//formatLabel(names.get(i), student.getLastName(), student.getFirstName(), student.getGrade(), student.getYear(), student.getAward());
					formatCell(honorRoll_names.get(i), student);
					honorRoll_names.get(i).setVisible(true);
				}
				centerPanel.remove(highestMarkPanel);
				centerPanel.remove(otherAwardsPanel);
				centerPanel.add(honorRollPanel);
				//cl.show(centerPanel, BYYEAR_PANEL);
			}
		}
		else {//an other awards page, not year by year
			String description = crtPage.getAwardInfo().getDescription();
			lblTopDescription.setText(description);
			lblTitle.setText("Marcy Awards  -  "+crtPage.getPageNo()+" / "+crtPage.getOfPages());
			for(int i = 0; i < (ConfigManager.otherAwards_rows*ConfigManager.otherAwards_columns); i++) {
				otherAwards_names.get(i).setVisible(false);
			}

			for (int i=0; i<crtPage.getStudentsOnPage().size();i++) {
				Student student = crtPage.getStudentsOnPage().get(i);
				//formatLabel(names.get(i), student.getLastName(), student.getFirstName(), student.getGrade(), student.getYear(), student.getAward());
				formatCellOtherAwards(otherAwards_names.get(i), student);
				otherAwards_names.get(i).setVisible(true);
			}
			centerPanel.remove(highestMarkPanel);
			centerPanel.remove(honorRollPanel);
			centerPanel.add(otherAwardsPanel);
			//cl.show(centerPanel, OTHERAWARDS_PANEL);
		}
	}


	//formating the cell label using HTML
	/*public void formatLabel(JLabel theLabel, String ln, String fn, int gradeLabel, int yearLabel, String awardLabel) {
		String imagePath = "file:\\"+System.getProperty("user.home")+ "\\Desktop\\awards\\Images\\HonorRoll.png";
		//System.out.println(imagePath);
		//Image image = smallIcon.getImage().getScaledInstance(25,25,Image.SCALE_DEFAULT);
		Image image = smallIcon.getImage();
		theLabel.setText("<html><center><center>"+ ln +", "+ fn + "</center>"
				          +"<center><img src =\""+imagePath+"\"></center>"
		                  +"<center> Gr. "+ gradeLabel +", "+ yearLabel + " - "+ awardLabel+"</center><html>");
		//theLabel.setText(ln +", "+ fn + "\n"+ gradeLabel +", "+ yearLabel + " - "+ awardLabel);
		 //		          +"<center><img src =\""+imagePath+"\" width=\"25\" heigh=\"25\"></center>"         
	}*/

	public void formatCell(LabelAward theLabel, Student student) {
		if (student.getGrade() == 9) {
			theLabel.setForegroundCol(ConfigManager.gr9);
		}
		else if (student.getGrade() == 10) {
			theLabel.setForegroundCol(ConfigManager.gr10);
		}
		else if (student.getGrade() == 11) {
			theLabel.setForegroundCol(ConfigManager.gr11);
		}
		else if (student.getGrade() == 12) {
			theLabel.setForegroundCol(ConfigManager.gr12);
		}

		String name = new String (student.getLastName().replace("^", "'")+", "+student.getFirstName().replace("^", "'"));
		if (name.length()>41) {
			name = name.substring(0, 40);
		}
		else {//fill out with spaces left/right
			int noSpacesFront = (40 - (student.getLastName().trim().replace("^", "'")+", "+student.getFirstName().trim().replace("^", "'")).length())/2;
			int noSpacesEnd = 40 - name.length()-noSpacesFront;
			StringBuffer newString = new StringBuffer();
			for (int i =0; i<noSpacesFront; i++) {
				newString.append(" ");
			}
			newString.append(name);
			for (int i =0; i<noSpacesEnd; i++) {
				newString.append(" ");
			}
			name = newString.toString();
		}
		theLabel.lblName.setText(name);
		if (student.getAward().equals("Honor Roll") && (student.getGrade()==9)) {//an extra space in front of 9, so that the cells have ct. width
			theLabel.lblAward.setText("Gr.  "+ student.getGrade()+", "+ (student.getYear()-1)+"-"+student.getYear()+", "+student.getAward());
		}
		if (student.getAward().equals("Honor Roll") && !(student.getGrade()==9)) {
			theLabel.lblAward.setText("Gr. "+ student.getGrade()+", "+ (student.getYear()-1)+"-"+student.getYear()+", "+student.getAward());
		}
		if (student.getAward().equals("Highest Mark Per Subject")) {
			//theLabel.lblAward.setText(""+(student.getYear()-1)+"-"+student.getYear()+", "+student.getAward());
			theLabel.lblAward.setText(""+(student.getYear()-1)+"-"+student.getYear());
		}
		if(student.getDescription() !=null ) {
			theLabel.addDescription();
			//theLabel.txtAreaDecription.setLineWrap(false);
			theLabel.lblDescription.setText(student.getDescription().trim().replace("^", "'"));
		}
		else {
			theLabel.removeDescription();
		}
	}

	public void formatCellOtherAwards(LabelWithDescription theLabel, Student student) {
		theLabel.setForegroundCol(ConfigManager.gr9);
		/*if (student.getGrade() == 9) {
			theLabel.setForegroundCol(ConfigManager.gr9);
		}
		else if (student.getGrade() == 10) {
			theLabel.setForegroundCol(ConfigManager.gr10);
		}
		else if (student.getGrade() == 11) {
			theLabel.setForegroundCol(ConfigManager.gr11);
		}
		else if (student.getGrade() == 12) {
			theLabel.setForegroundCol(ConfigManager.gr12);
		}*/
		theLabel.lblName.setText(student.getLastName().trim().replace("^", "'")+", "+student.getFirstName().trim().replace("^", "'"));
		String gradeString = "";
		if (student.getGrade() != 0) {
			gradeString = "Gr."+student.getGrade()+", ";
		}
		//theLabel.lblAward.setText(gradeString+(student.getYear()-1)+"-"+student.getYear()+", "+student.getAward().replace("^", "'"));
		theLabel.lblAward.setText(gradeString+(student.getYear()-1)+"-"+student.getYear()+", "+  String.format("%-30s", student.getAward().replace("^", "'")));
		System.out.println(student); // uncomment to catch diff between award in awards and award in award_type
		System.out.println(student.getAward());
		//System.out.println( awardsInfo);
		System.out.println( awardsInfo.get(student.getAward()).getDescription());
		String description = (awardsInfo.get(student.getAward()).getDescription()).trim().replace("^", "'");
		//theLabel.txtAreaDecription.setLineWrap(true);
		theLabel.txtAreaDecription.setText(description);
	}

}

