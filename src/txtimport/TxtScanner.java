package txtimport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JOptionPane;

import utilities.DatabaseManager;


public class TxtScanner {

	//reads the file passed as a parameter, line by line, and insert into the database all the Honor Roll
	public static void readHonorRoll(File file, String award, int grade, int year) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String text = "";
			while((text = br.readLine()) != null) {
				text = text.replaceAll(" ", ",").replace("'", "^");
				String[] split = text.split(",");
				String firstName = split[1];
				String lastName = split[0];
				System.out.println(firstName + " " + lastName + " " + year + " " + grade + " " + award);
				DatabaseManager.runStatement("INSERT INTO AWARDS(firstname, lastname, year, grade, award, description) VALUES('" + firstName + "', '" + lastName + "', '" + year + "', '" + grade + "', '" + award + "', NULL)", true);
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	//reads the file passed as a parameter, line by line, and insert into the database all the Highest Mark
	public static void readHighestMark(File file, String award, int year) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String text = "";
			while((text = br.readLine()) != null) {
				text = text.replace("'", "^");
				String[] split = text.split(",");
				String lastName = split[0].trim();
				String firstName = split[1].trim();
				int grade = Integer.parseInt(split[2].trim());
				int mark = Integer.parseInt(split[3].trim());
				String description="";
				if (mark!=0) {
					description = split[4].trim()+"-"+mark+"%";
				}
				else {
					description = split[4].trim();
				}
				System.out.println(firstName + " " + lastName + " " + year + " " + grade + " " + award+" "+description);
				DatabaseManager.runStatement("INSERT INTO AWARDS(firstname, lastname, year, grade, award, description) VALUES('" + firstName + "', '" + lastName + "', '" + year + "', '" + grade + "', '" + award + "', '"+description+"')",true);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//reads the file passed as a parameter, line by line, and insert into the database all the Other Award:Top Student, Mother Theresa ...
	public static void readOtherAwards(File file, int year) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String text = "";
			while((text = br.readLine()) != null) {
				text = text.replace("'", "^");
				System.out.println(text);
				String[] split = text.split(",");
				String lastName = split[0].trim();
				String firstName = split[1].trim();
				int grade = Integer.parseInt(split[2].trim());
				//int year = Integer.parseInt(split[3].trim());
				String award = split[4].trim();
				System.out.println(firstName + " " + lastName + " " + grade + " " + year + " " + award);
				DatabaseManager.runStatement("INSERT INTO AWARDS(firstname, lastname, year, grade, award, description) VALUES('" + firstName + "', '" + lastName + "', '" + year + "', '" + grade + "', '" + award + "', NULL)",true);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
