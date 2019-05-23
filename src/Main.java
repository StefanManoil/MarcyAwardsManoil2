import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import gui.Display;
import gui.ImagePanel;
import gui.ImportFromFile;
import gui.UpdateAwardTypes;
import gui.UpdateStudents;
import utilities.ConfigManager;
import utilities.DatabaseManager;

/**
 * @author Manoil (May 2018)
 * This class loads the property file that should be located in a folder named award on the desktop: awards\ini.txt
 * It also loads the fonts, located in a source folder: resources\monserat, resources\ncaa
 * It also pulls from the database and loads data into three main data structures: awardsInfo, studentsYearByYear,studentsNotYearByYear
 * - awardsInfo - a HashMap containing the awards type 
 * - studentsYearByYear - an ArrayList with all the students/awards that needs to be displayed year by year
 * - studentsNotYearByYear - an ArrayList with all the students/awards that don't need to be displayed year by year
 *
 */
public class Main extends JFrame {
 	PrintStream errorsPS = null;
    JButton btnDisplay, btnStudents, btnAwards, btnImport;
    JLabel lblTitle, lblInput, lblDisplay, lblEdit, lblLogo1, lblLogo2, lblImport;
    Font fontTitle, fontBody, ncaa, montserrat;
    JFileChooser jfc;
    ImageIcon marcyLogo;

    public static void main(String[] args) {
       Console c = System.console();
        if(c == null && !GraphicsEnvironment.isHeadless()) {
            // creates visible console cmd prompt
            String filename = Main.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6);
            System.out.println(filename);
            try {
                Runtime.getRuntime().exec(new String[]{"cmd","/c","start","cmd","/k","java -jar \"" + filename + "\""});
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            SwingUtilities.invokeLater(() -> {
            	new Main();
            });
        } 
   
     /* SwingUtilities.invokeLater(() -> {
        	new Main();
        }); */
   }
    
    /**
     * constructor Main
     * invokes  
     * invokes  
     */
    public Main() {
    	 ConfigManager.initiateProperties();
    	 //create a FileOutput for errors, errors.txt under desktop\awards to be able to track errors
    	 FileOutputStream fos = null;
    	 File file = new File(System.getProperty("user.home"), "/Desktop/awards/"+"errors.txt");
    	 try {
    	  fos = new FileOutputStream(file);
    	 }
    	 catch (IOException ioe) {
    	  System.err.println("redirection not possible:"+ ioe);
    	 }
    	 errorsPS = new PrintStream(fos);
    	 //System.setErr(errorsPS); // trying to redirect the errors stream to a file
    	 System.err.println("errors go to file");
         ConfigManager.loadProperties();
         System.setProperty("awt.useSystemAAFontSettings","on");
         System.setProperty("swing.aatext", "true");
         ncaa = loadFont("ncaa");
         montserrat = loadFont("montserrat");
         fontTitle = ncaa;
         fontBody = ncaa;
         fontTitle = fontTitle.deriveFont(Font.BOLD, ConfigManager.titleSize);
         fontBody = fontBody.deriveFont(Font.BOLD, (int)(ConfigManager.titleSize*0.6));
         //try to connect to the database to check the mysql connection
         if(!DatabaseManager.connect()) {
             System.out.println("Unable to connect to database.");
             JOptionPane.showMessageDialog(null, "Error connecting to the data base", "Error", JOptionPane.ERROR_MESSAGE);
             System.err.println("Error connecting to the data base");
             return;
         }
         try {
     	    marcyLogo = new ImageIcon(ImageIO.read(Main.class.getResourceAsStream("/resources/logo.png")));
     	 }
         catch (IOException ioe) {
     		System.out.println("Couldn't load the logo");
     		System.err.println("Couldn't load the logo");
     		ioe.printStackTrace();
     	}    
         lblLogo1 = new JLabel(marcyLogo);
         lblLogo2 = new JLabel(marcyLogo);
         setLayout(new GridBagLayout());
         lblTitle = new JLabel("MARCY AWARDS DISPLAY");
         lblTitle.setFont(fontTitle);
         lblDisplay = new JLabel ("Display the awards               - ");
         lblDisplay.setFont(fontBody);
         lblInput = new JLabel ("Input new awards/students - ");
         lblInput.setFont(fontBody);
         lblEdit = new JLabel ("Add/Edit/Delete Award Types - ");
         lblEdit.setFont(fontBody);
         lblImport = new JLabel ("Import Awards from Text Files");
         lblImport.setFont(fontBody);
         btnDisplay = new JButton ("Display");
         btnDisplay.setFont(fontBody);
         btnStudents = new JButton ("Enter/Update Students");
         btnStudents.setFont(fontBody);
         btnAwards = new JButton ("Enter/Update Awards");
         btnAwards.setFont(fontBody);
         btnImport = new JButton ("Import From Files");
         btnImport.setFont(fontBody);
         
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
         //imageLabel1.setBackground(Color.GREEN);
         getContentPane().add(lblLogo1, gc);
         gc.gridx = 1;
         gc.gridwidth = 2;
         getContentPane().add(lblTitle, gc);
         
         gc.gridx = 3;//or 3
         gc.gridwidth = 1;
         gc.anchor = GridBagConstraints.NORTHWEST;
         getContentPane().add(lblLogo2, gc);
         
         gc.gridy = 1;
         gc.gridx = 1;
         getContentPane().add(lblDisplay, gc);
         
         gc.gridx = 2;
         btnDisplay.addActionListener(a -> initDisplay());
         getContentPane().add(btnDisplay, gc);

         gc.gridy = 2;
         gc.gridx = 1;
         getContentPane().add(lblInput, gc);
         
         gc.gridx = 2;
         btnStudents.addActionListener(a -> updateStudents());
         getContentPane().add(btnStudents, gc);
         
         gc.gridy = 3;
         gc.gridx = 1;
         getContentPane().add(lblEdit, gc);
         
         gc.gridx = 2;
         btnAwards.addActionListener(a -> updateAwardTypes());
         getContentPane().add(btnAwards, gc);
         
         gc.gridy = 4;
         gc.gridx = 1;
         getContentPane().add(lblImport, gc);
         
         gc.gridx = 2;
         btnImport.addActionListener(a -> importFromFiles());
         getContentPane().add(btnImport, gc);
         
         getContentPane().setBackground(Color.RED);
         setSize(200, 100);
         pack();
         Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
         this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
         //setLocationRelativeTo(null);
         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         setVisible(true);
    }
    
    /**
     * Loads True Type Fonts of the desired name
     * @param fontName filename in folder resources/resources/
     */
    private Font loadFont(String fontName) {
        InputStream fontStream = new BufferedInputStream(Main.class.getResourceAsStream("/resources/" + fontName + ".ttf"));
        Font font = null;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
        } catch(FontFormatException | IOException e) {
            System.err.println("Error loading font");
        }
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(font);
        return font;
    }

    /**
     * Initializes displaying awards using specified constraints from the ini.properies file. Initializes
     * container panels and jframe for double buffering.
     */
     public void initDisplay() {
    	 Display display = new Display(marcyLogo, ncaa, montserrat);
     }	
     
     /**
      * Initializes displaying awards using specified constraints from the ini.properies file. Initializes
      * container panels and jframe for double buffering.
      */
      public void updateAwardTypes() {
     	 UpdateAwardTypes updateAwardTypes = new UpdateAwardTypes(marcyLogo, ncaa, montserrat);
      }	

      /**
       * Initializes displaying awards using specified constraints from the ini.properies file. Initializes
       * container panels and jframe for double buffering.
       */
       public void updateStudents() {
      	 UpdateStudents updateStudents = new UpdateStudents(marcyLogo, ncaa, montserrat);
       }	
       
       public void importFromFiles() {
        	 ImportFromFile importFromFile = new ImportFromFile(marcyLogo, ncaa, montserrat);
         }	
}
