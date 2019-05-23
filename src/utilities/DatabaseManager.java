package utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import dataStructures.AwardInfo;
import dataStructures.Student;

/**
 * @author Manoil (May 2018)
 * This class handles all database connections.
 * connect() needs to be called first to check the driver and the mysql connection 
 * init() must be called before running any statement, otherwise connections and database will not work.
 * Uses MySQL.
 *
 */
public class DatabaseManager {

	//the mysql connection url: jdbc:mysql://localhost:3306/?user=...&password=...
	static String url = "";
	//a hashmap with an entry for each award, and an associated object AwardInfo(name/desc/yearByYear). 
	//The key corresponds to the award field in the table award_type, basically the name of the award e.g. honor roll 
		 
	/**
	 * constructor - meant to prevent instantiation 
	 * @param none
	 */	 
	private DatabaseManager() {
		throw new AssertionError();
	}
	
	/**
	 * @param none
	 * @return true if the database connected properly
	 * It load the driver - the one that Maven downloads when building the jar, under users\....m2 folder
	 * attempts to make a connection to the database. If successful initializes the url String variable, used by the other methods
	 */
	public static boolean connect() {
		//Database details
		boolean b = false;
		try {
			// Driver Installed with MySQL ('Connector/J')- under c:ProgramFile\MySQL
			// the jar will be included in the project by Maven. Make sure that the right version is under dependencies
			// in the pom.xml file, update Maven, build with Maven (first time only - clean package)
			// if Maven downloaded the right jar, it should be under c:/users/.../.m2/repository/mysql/mysql-connector-java/
			// there should be a jar file there corresponding to the version of the connector installed with mysql server
			Class.forName("com.mysql.jdbc.Driver");
			@SuppressWarnings("unused")
			Connection c = DriverManager.getConnection("jdbc:mysql://" + ConfigManager.address + ":" + ConfigManager.port + "/?user=" + ConfigManager.username + "&password=" + ConfigManager.password);
			url = "jdbc:mysql://" + ConfigManager.address + ":" + ConfigManager.port + "/?user=" + ConfigManager.username + "&password=" + ConfigManager.password;
			b = true;
			System.out.println("connected");
		} catch (SQLException | ClassNotFoundException e) {
			System.out.println("Error connecting to database");
			e.printStackTrace();
			System.err.println(e.getStackTrace());//Manoil
		}
		return b;
	}

	/**
	 * init must be called before running any statement, otherwise connections and database will not work.
	 * it checks if the awards database and the awards and award_type tables exit, and it creates them if not 
	 */
	public static void init() {
		runStatement("CREATE DATABASE IF NOT EXISTS AWARDS", false);
		url = "jdbc:mysql://" + ConfigManager.address + ":" + ConfigManager.port + "/awards?user=" + ConfigManager.username + "&password=" + ConfigManager.password;
        //awards table: ID - key, autoincrement, firstname - varchar(50), lastname - varchar(50), year - int, award - varchar(30)
		runStatement("CREATE TABLE IF NOT EXISTS AWARDS(ID int NOT NULL AUTO_INCREMENT PRIMARY KEY, "
				+ "firstname VARCHAR(50) NOT NULL, lastname VARCHAR(50), year INT NOT NULL, grade INT, award varchar(30) NOT NULL, description varchar(200))", false);
        //award_type table: award - varchar(30), key, description - varchar (1200), year BOOL (true if it needs to be displayed by year
		runStatement("CREATE TABLE IF NOT EXISTS AWARD_TYPE(award VARCHAR(30) NOT NULL PRIMARY KEY, description VARCHAR(1200), year BOOL NOT NULL)", false);
	}

	/**
	 * @param str runs sql statement specified by this string
	 * @return returns integer value of the record added to database
	 * It creates a connection to the database and it sends the mysql command provided as the paramater str.
	 */
	public static int runStatement(String str, boolean useAwards) {
		Connection c = null;
		Statement s = null;
		PreparedStatement p;
		int generatedID = 0;
		try {
			c = DriverManager.getConnection(url);
			//manoil - calling runStatement from init with "use awards" - exception: Database does not exist
			//calling runStatement from TXTScanner requires use awards
			//I added a boolean useAwards to make this method more flexible
			if (useAwards) {
				p = c.prepareStatement("USE AWARDS");
				p.execute();
			}
			s = c.prepareStatement(str);
			s.executeUpdate(str, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = s.getGeneratedKeys();
			while(rs.next()) {
				generatedID = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				try {
					c.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(s != null) {
				try {
					s.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return generatedID;
	}

	/**
	 * pullAwardsInfo
	 * @param none
	 * @return a hashmap with all the award types loaded so far in the award_type table
	 * 
	 */
	public static HashMap<String, AwardInfo> pullAwardsInfo() {
		HashMap<String, AwardInfo> awardsInfo = new HashMap<String, AwardInfo>();
		Connection c = null;
		Statement s = null;
		PreparedStatement p;
		ResultSet rs = null;
		try {
			c = DriverManager.getConnection(url);
			s = c.createStatement();
			p = c.prepareStatement("USE AWARDS");
			p.execute();
			rs = s.executeQuery("select * from award_type ORDER BY award");
			try {
				while(rs.next()) {
					if(awardsInfo.containsKey(rs.getString("award"))) continue;
					awardsInfo.put(rs.getString("award"), new AwardInfo(rs.getString("award"), rs.getString("description"), rs.getBoolean("year")));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				try {
					c.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(s != null) {
				try {
					s.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return awardsInfo;
	}
	
	/**
	 * pulls from the awards table all the students that need to be displayed year by year
	 * order by year, award type, grade, lastName, firstName
	 * @param none
	 * @return an ArrayList with all the students/awards that need to be displayed by year
	 * 
	 */
	public static ArrayList<Student> load_YearByYearStudents() {
		ArrayList<Student> students = new ArrayList<Student>();
		Connection c = null;
		Statement s = null;
		PreparedStatement p;
		ResultSet rs = null;
		try {
			c = DriverManager.getConnection(url);
			s = c.createStatement();
			p = c.prepareStatement("USE AWARDS");
			p.execute();
			rs = s.executeQuery("SELECT * FROM AWARDS, AWARD_TYPE WHERE awards.award = award_type.award and award_type.year = true ORDER BY awards.year, awards.award, awards.grade, awards.lastname, awards.firstname");
			while(rs.next()) {
					Student student = new Student(rs.getString("firstname"), rs.getString("lastname"),  
							                       rs.getString("award"), rs.getInt("grade"), rs.getInt("year"), rs.getString("description"), 0);
					students.add(student);
					//if(student.getDescription()!=null)
					//System.out.println(student.getDescription().length());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				try {
					c.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(s != null) {
				try {
					s.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return students;
	}

	/**
	 * pulls from the awards table all the students/awards that don't needs to be displayed by year
	 * order by award type, year, grade, lastName, firstName
	 * @param none
	 * @return an ArrayList with all the students/awards that need to be displayed by year
	 * 
	 */	
	public static ArrayList<Student> load_NotYearByYearStudents() {
		ArrayList<Student> students = new ArrayList<Student>();
		Connection c = null;
		Statement s = null;
		PreparedStatement p;
		ResultSet rs = null;
		try {
			c = DriverManager.getConnection(url);
			s = c.createStatement();
			p = c.prepareStatement("USE AWARDS");
			p.execute();
			rs = s.executeQuery("SELECT * FROM AWARDS, AWARD_TYPE WHERE awards.award = award_type.award and award_type.year = false ORDER BY awards.year, awards.award, awards.grade, awards.lastname, awards.firstname");
			while(rs.next()) {
					Student student = new Student(rs.getString("firstname"), rs.getString("lastname"),  
							                       rs.getString("award"), rs.getInt("grade"), rs.getInt("year"),rs.getString("description") , 0);
					students.add(student);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				try {
					c.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(s != null) {
				try {
					s.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return students;
	}

	/**
	 * Loads awards from database
	 */
	/*public static void loadAwards() {
		Connection c = null;
		Statement s = null;
		PreparedStatement p;
		ResultSet rs = null;
		System.out.println("loading");
		try {
			c = DriverManager.getConnection(url);
			s = c.createStatement();
			p = c.prepareStatement("USE AWARDS");
			p.execute();
			// Can remove this to just select by award, then order by using a arraylist buffer then into add award
			for(AwardType type : AwardManager.getAwards().values()) {
				System.out.println("has types");
				System.out.println(type.getName());
				if(type.isYearByYear()) {
					rs = s.executeQuery("SELECT * FROM AWARDS WHERE award = '" + type.getName() + "' ORDER BY award, year, grade, lastname, firstname");
				} else {
					rs = s.executeQuery("SELECT * FROM AWARDS WHERE award = '" + type.getName() + "' ORDER BY award, grade, year, lastname, firstname");
				}
				while(rs.next()) {
					System.out.println("has next");
					Student studentAward = new Student(rs.getString("firstname"), rs.getString("lastname"),  
							rs.getString("award"), rs.getInt("grade"), rs.getInt("year"));
					System.out.println(studentAward.toString());
					AwardManager.addAward(studentAward);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				try {
					c.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(s != null) {
				try {
					s.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}*/

	/**
	 * searches all the awards with specified name
	 */
	public static ArrayList<Student> searchAwards(String lastName, String firstName) {
		ArrayList<Student> students = new ArrayList<Student>();
		Connection c = null;
		Statement s = null;
		PreparedStatement p;
		ResultSet rs = null;
		try {
			c = DriverManager.getConnection(url);
			s = c.createStatement();
			p = c.prepareStatement("USE AWARDS");
			p.execute();
			if(lastName.equals("") && !firstName.equals("")) {
				rs = s.executeQuery("SELECT * FROM AWARDS WHERE firstname LIKE '" + firstName + "%'");
			} else if(firstName.equals("") && !lastName.equals("")) {
				rs = s.executeQuery("SELECT * FROM AWARDS WHERE lastname LIKE '" + lastName + "%'");
			} else if(!firstName.equals("") && !lastName.equals("")) {   
			    rs = s.executeQuery("SELECT * FROM AWARDS WHERE firstname LIKE '" + firstName + "%' AND lastname LIKE '" + lastName + "%'");
			}
			try {
				int count = 0;
				if(rs == null) return students;
				while(rs.next()) {
					Student student = new Student(rs.getString("firstname"), rs.getString("lastname"),  
							rs.getString("award"), rs.getInt("grade"), rs.getInt("year"), rs.getString("description"), rs.getInt("ID"));
					students.add(student);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				try {
					c.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(s != null) {
				try {
					s.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return students;
	}
	
	
	/**
	 * searches all the awards with specified award type and year
	 */
	public static ArrayList<Student> searchAwardsTypeYear(String type, int year) {
		ArrayList<Student> students = new ArrayList<Student>();
		Connection c = null;
		Statement s = null;
		PreparedStatement p;
		ResultSet rs = null;
		try {
			c = DriverManager.getConnection(url);
			p = c.prepareStatement("USE AWARDS");
			p.execute();
			s = c.createStatement();
			rs = s.executeQuery("SELECT * FROM AWARDS WHERE award LIKE '" + type + "%' AND year='" + year+"'");
			try {
				int count = 0;
				if(rs == null) return students;
				while(rs.next()) {
					Student student = new Student(rs.getString("firstname"), rs.getString("lastname"),  
							rs.getString("award"), rs.getInt("grade"), rs.getInt("year"), rs.getString("description"), rs.getInt("ID"));
					students.add(student);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				try {
					c.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(s != null) {
				try {
					s.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return students;
	}
	
	/**
	 * searches all the "other awards"
	 */
	public static ArrayList<Student> searchOtherAwardsTypeYear(int year) {
		ArrayList<Student> students = new ArrayList<Student>();
		Connection c = null;
		Statement s = null;
		PreparedStatement p;
		ResultSet rs = null;
		try {
			c = DriverManager.getConnection(url);
			p = c.prepareStatement("USE AWARDS");
			p.execute();
			s = c.createStatement();
			rs = s.executeQuery("SELECT * FROM AWARDS WHERE award <> 'Honor Roll' AND award <> 'Highest Mark Per Subject' AND year= '" + year+"';");
			try {
				int count = 0;
				if(rs == null) return students;
				while(rs.next()) {
					Student student = new Student(rs.getString("firstname"), rs.getString("lastname"),  
							rs.getString("award"), rs.getInt("grade"), rs.getInt("year"), rs.getString("description"), rs.getInt("ID"));
					students.add(student);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				try {
					c.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(s != null) {
				try {
					s.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return students;
	}
	
	/**
	 * This method is used to check if a specific award has been assigned to students.
	 * It is used by deleteAward() to check if a specific award can be deleted from award_type
	 * pulls from the awards table all the students that are awarded a specific award
	 * order by year, award type, grade, lastName, firstName
	 * @param String - the award
	 * @return an ArrayList with all the students/awards that have that award
	 * 
	 */
	public static ArrayList<Student> loadStudentsForACertainAward(String theAward) {
		ArrayList<Student> students = new ArrayList<Student>();
		Connection c = null;
		Statement s = null;
		PreparedStatement p;
		ResultSet rs = null;
		try {
			c = DriverManager.getConnection(url);
			s = c.createStatement();
			p = c.prepareStatement("USE AWARDS");
			p.execute();
			rs = s.executeQuery("SELECT * FROM AWARDS WHERE award = '"+ theAward+"' ORDER BY year, award, grade, lastname, firstname");
			while(rs.next()) {
					Student student = new Student(rs.getString("firstname"), rs.getString("lastname"),  
							                       rs.getString("award"), rs.getInt("grade"), rs.getInt("year"), rs.getString("description"),  0);
					students.add(student);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				try {
					c.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(s != null) {
				try {
					s.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return students;
	}
	
	/**
	 * inserts a new record into award_type, invokes runStatement to accomplish this
	 * the code that invokes this method, addNewAward() from UpdateAwardTypes checks if
	 * an award with the same name already exists, and it gives the user feedback
	 * @param String newAward, String newDescription, int newYBY
	 * @return an ArrayList with all the students/awards that have that award
	 * 
	 */
	public static int addNewAwardInAward_Type(String newAward, String newDescription, int newYBY) {
		return DatabaseManager.runStatement("INSERT INTO AWARD_TYPE(award, description, year) VALUES('" +  newAward + "', '" + newDescription + "', '" + newYBY +  "')", true);
	}
	
	/**
	 * inserts a new record into award_type, invokes runStatement to accomplish this
	 * the code that invokes this method, addNewAward() from UpdateAwardTypes checks if
	 * an award with the same name already exists, and it gives the user feedback
	 * @param String newAward, String newDescription, int newYBY
	 * @return an ArrayList with all the students/awards that have that award
	 * 
	 */
	public static int deleteAwardFromAward_Type(String awardToDelete) {
		return DatabaseManager.runStatement("DELETE FROM AWARD_TYPE WHERE award = '"+ awardToDelete+"'", true);
	}
	
	/**
	 * replace an existing award in award_type
	 * the code that invokes this method, editAward() from UpdateAwardTypes checks if
	 * the award's name has changed. If not, replace just in award_type, if yes also replace in awards
	 * @param String awardToReplace, String newAward, String newDescription, int newYBY
	 * @return int the key
	 * 
	 */
	public static int editAwardInAward_Type(String awardToReplace, String newAward, String newDescription, int newYBY) {
		return DatabaseManager.runStatement("UPDATE AWARD_TYPE SET award = '"+newAward+"', description = '"+newDescription+"', year = '"+newYBY+"' WHERE award = '"+awardToReplace+"'", true);
	}
	
	/**
	 * replace an existing award name in awards
	 * the code that invokes this method, editAward() from UpdateAwardTypes checks if
	 * the award's name has changed. If not, replace just in award_type, if yes also replace in awards
	 * @param Strng awardToReplace, String newAward
	 * @return int the key
	 * 
	 */
	public static int editAwardInAwards(String awardToReplace, String newAward) {
		return DatabaseManager.runStatement("UPDATE AWARDS SET award = '"+newAward+"'WHERE award = '"+awardToReplace, true);
	}
	
	/**
	 * appends a new student/record in awards
	 * the code that invokes this method, addNewStudent() from UpdateStudents
	 * @param String fn, String ln, int year, int grade, String award
	 * @return int the key
	 * 
	 */
	public static int addNewStudentAward(String ln, String fn, int year, int grade, String award, String descript) {
		//System.out.println("VALUES('" +fn+"', '"+ln+"', '"+year+"', '"+grade+"', '"+award+"')");
		return DatabaseManager.runStatement("INSERT INTO AWARDS (firstname, lastname, year, grade, award, description) VALUES('" +fn+"', '"+ln+"', '"+year+"', '"+grade+"', '"+award+"', '" +descript+"')", true);
	}
	
	/**
	 * delete a student/record from awards
	 * the code that invokes this method, deleteStudentAward() from UpdateStudents
	 * @param String fn, String ln, int year, int grade, String award
	 * @return int the key
	 * 
	 */
	public static int deleteStudentAward(String ln, String fn, int year, int grade, String award) {
		//System.out.println("DELETE FROM AWARDS WHERE lastname = '"+ ln+"' AND firstname = '"+ fn+"' AND year = '"+year+"' AND grade = '"+ grade+"' AND award = '"+award+"';");
		return DatabaseManager.runStatement("DELETE FROM AWARDS WHERE lastname = '"+ ln+"' AND firstname = '"+ fn+"' AND year = '"+year+"' AND grade = '"+ grade+"' AND award = '"+award+"';", true);
	}
	
	/**
	 * delete a student/record from awards
	 * the code that invokes this method, deleteStudentAward() from UpdateStudents
	 * @param String fn, String ln, int year, int grade, String award
	 * @return int the key
	 * 
	 */
	public static int editStudentAward(String oldLn, String oldFn, int oldYear, int oldGrade, String oldAward, String oldDescription, String newLn, String newFn, int newYear, int newGrade, String newAward, String newDescription) {
		//System.out.println("UPDATE AWARDS SET firstname='"+newFn+"', lastname='"+ newLn+"', year='"+newYear+"', grade='"+newGrade+"', award='" +newAward+"' WHERE firstname='"+oldFn+"' AND lastname='"+ oldLn+"' AND year='"+oldYear+"' AND grade='"+newGrade+"' AND award='" +newAward+"';");
		return DatabaseManager.runStatement("UPDATE AWARDS SET firstname='"+newFn+"', lastname='"+ newLn+"', year='"+newYear+"', grade='"+newGrade+"', award='" +newAward+"', description='" +newDescription+"' WHERE firstname='"+oldFn+"' AND lastname='"+ oldLn+"' AND year='"+oldYear+"' AND grade='"+oldGrade+"' AND award='" +oldAward+"' AND description='" +oldDescription+"';", true);
	}
	/**
	 * delete the database structure
	 * the code that invokes this method, drop() from ImportFromFile
	 * @param none
	 * @return none
	 * 
	 */
	public static void dropDatabase() {
		DatabaseManager.runStatement("DROP DATABASE awards;", true);
		init();
	}
	
	/**
	 * delete a student/record from awards
	 * the code that invokes this method, deleteAwardear() from ImportFromFile
	 * @param String String award, int year
	 * @return int the key
	 * 
	 */
	public static int deleteAwardYear(String award, int year) {
		//System.out.println("DELETE FROM AWARDS WHERE year = '"+year+"' AND award = '"+award+"';");
		return DatabaseManager.runStatement("DELETE FROM AWARDS WHERE year = '"+year+"' AND award = '"+award+"';", true);
	}
}
