package utilities;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * @author Manoil
 * Manages configurable settings form a properties file located on Desktop/awards/ini.properties
 * Default settings are the address, username, port, password, rows, columns, and various font sizes. Adjust as necessary 
 *
 */
public class ConfigManager {

    public static String address = "";
    public static String username = "";
    public static String port = "";
    public static String password = "";
    public static int titleSize = 0;
    public static int mottoSize = 0;
    public static int descSize = 0;
    public static int namesSize = 0;
    public static int rows_HonorRoll = 0;
    public static int columns_HonorRoll = 0;
    public static int rows_HighestMark = 0;
    public static int columns_HighestMark = 0;
    public static int otherAwards_rows = 0;
    public static int otherAwards_columns = 0;
    public static int transitionTime = 0;
    public static Color gr9;
    public static Color gr10;
    public static Color gr11;
    public static Color gr12;
    public static Color background;
    public static Color description;
    public static Color motto;
    public static Color title;

    // Prevent instatiation
    private ConfigManager() {
        throw new AssertionError();
    }

    public static void loadProperties() {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            File file = new File(System.getProperty("user.home"), "/Desktop/awards/ini.properties");
            file.mkdirs();
            FileReader fileReader = new FileReader(file);

            //load a properties file from class path, inside static method
            prop.load(fileReader);
            try {
                rows_HonorRoll = Integer.valueOf(prop.getProperty("rows_HonorRoll"));
                columns_HonorRoll = Integer.valueOf(prop.getProperty("columns_HonorRoll"));
                rows_HighestMark = Integer.valueOf(prop.getProperty("rows_HighestMark"));
                columns_HighestMark = Integer.valueOf(prop.getProperty("columns_HighestMark"));
                otherAwards_rows = Integer.valueOf(prop.getProperty("otherAwards_rows"));
                otherAwards_columns = Integer.valueOf(prop.getProperty("otherAwards_columns"));
                titleSize = Integer.valueOf(prop.getProperty("titleSize"));
                mottoSize = Integer.valueOf(prop.getProperty("mottoSize"));
                descSize = Integer.valueOf(prop.getProperty("descSize"));
                namesSize = Integer.valueOf(prop.getProperty("namesSize"));
                transitionTime = Integer.valueOf(prop.getProperty("transitionTime"));
                String[] values = prop.getProperty("grade9").replaceAll(",", "").split(" ");
                gr9 = new Color(Integer.valueOf(values[0]), Integer.valueOf(values[1]), Integer.valueOf(values[2]));
                values = prop.getProperty("grade10").replaceAll(",", "").split(" ");
                gr10 = new Color(Integer.valueOf(values[0]), Integer.valueOf(values[1]), Integer.valueOf(values[2]));
                values = prop.getProperty("grade11").replaceAll(",", "").split(" ");
                gr11 = new Color(Integer.valueOf(values[0]), Integer.valueOf(values[1]), Integer.valueOf(values[2]));
                values = prop.getProperty("grade12").replaceAll(",", "").split(" ");
                gr12 = new Color(Integer.valueOf(values[0]), Integer.valueOf(values[1]), Integer.valueOf(values[2]));
                values = prop.getProperty("background").replaceAll(",", "").split(" ");
                background = new Color(Integer.valueOf(values[0]), Integer.valueOf(values[1]), Integer.valueOf(values[2]));
                values = prop.getProperty("description").replaceAll(",", "").split(" ");
                description = new Color(Integer.valueOf(values[0]), Integer.valueOf(values[1]), Integer.valueOf(values[2]));
                values = prop.getProperty("motto").replaceAll(",", "").split(" ");
                motto = new Color(Integer.valueOf(values[0]), Integer.valueOf(values[1]), Integer.valueOf(values[2]));
                values = prop.getProperty("title").replaceAll(",", "").split(" ");
                title = new Color(Integer.valueOf(values[0]), Integer.valueOf(values[1]), Integer.valueOf(values[2]));
            } catch(NumberFormatException e) {
                System.out.println("Error in number format in resolution definition in ini.properties");
                e.printStackTrace();
            }
            address = prop.getProperty("address");
            username = prop.getProperty("user");
            port = prop.getProperty("port");
            password = prop.getProperty("password");
            password = prop.getProperty("password");
            //amount = 2;
        } catch(IOException ex) {
            ex.printStackTrace();
        } finally {
            if(input != null) {
                try {
                    input.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void initiateProperties() {
        File file = new File(System.getProperty("user.home"), "/Desktop/awards/ini.properties");
        if(file.exists()) return;
        File folder = new File(System.getProperty("user.home"), "/Desktop/awards");//manoil
        folder.mkdir();//manoil: the awards folder needs to be created before the file ini.properties is created
        Properties prop = new Properties();
        OutputStream output = null;
        try {
            output = new FileOutputStream(file);
            // set the properties value
            prop.setProperty("password", "Marcellinus");
            prop.setProperty("address", "localhost");
            prop.setProperty("user", "root");
            prop.setProperty("port", "3306");
            prop.setProperty("transitionTime", "10");
            prop.setProperty("titleSize", "60");
            prop.setProperty("title", "255, 60, 0");
            prop.setProperty("descSize", "30");
            prop.setProperty("description", "255, 255, 255");
            prop.setProperty("mottoSize", "40");
            prop.setProperty("motto", "255, 60, 0");
            prop.setProperty("namesSize", "20");
            prop.setProperty("background", "0, 0, 0");
            prop.setProperty("grade9", "255, 255, 255");
            prop.setProperty("grade10", "255, 60, 0");
            prop.setProperty("grade11", "255, 255, 255");
            prop.setProperty("grade12", "255, 60, 0");
            prop.setProperty("rows_HonorRoll", "11");
            prop.setProperty("columns_HonorRoll", "4");
            prop.setProperty("rows_HighestMark", "9");
            prop.setProperty("columns_HighestMark", "4");
            prop.setProperty("otherAwards_rows", "4");
            prop.setProperty("otherAwards_columns", "4");
                       
            prop.store(output, null);
        } catch(IOException io) {
            io.printStackTrace();
        } finally {
            if(output != null) {
                try {
                    output.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
