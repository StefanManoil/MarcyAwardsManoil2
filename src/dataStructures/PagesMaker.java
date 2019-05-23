package dataStructures;
import java.util.ArrayList;
import java.util.HashMap;
import utilities.ConfigManager;
import utilities.DatabaseManager;

/**
 * @author Manoil
 * This class is used to make an arrayList of Page objects, basically the pages that will be displayed
 * The constructor initializes a HashMap of AwardInfo objects, basically the awards info(type, description, yearByYear)
 * pages - The ArrayList of Page objects will be loaded first with yearByYear Page objects, and 
 * then with pages not year by year. A Page object that has year = 0 it a not year by year Page
 * pages - the arrayList of Pages objects, can be liniary parsed in the Display class
 * to display pages, one after another.
 */
public class PagesMaker {
	//an arraylist of Student objects that need to be displayed year by year(like honor roll)
	private ArrayList<Student> studentsYearByYear;
	//an arraylist of Student objects that are not displayed year by year(Mother Theresa ward, John McAllister award...))
	private ArrayList<Student> studentsNotYearByYear;
	private HashMap<String, AwardInfo> awardsInfo ; 
	private ArrayList<Page> pages;
	private ArrayList<FinishLoadingDataListener> listeners;

	public PagesMaker () {
		pages = new ArrayList<Page>();
		listeners = new ArrayList<FinishLoadingDataListener>();
	}

	/**
	 * This method pulls from the database and loads two main data structures 
	 * ArrayLists studentsYearByYear and  studentsNotYearByYear
	 * Then it invokes the two methods that create Page objects and ArrayLists
	 * of Page objects: makeYBYPages and makeNotYBYPage
	 * @param an ArrayList of Student objects order by: ....
	 * @return none. It just populates the big data structure pages with appropriate pages 
	 */
	public void makePages() {
		//init must be called before running any statement, otherwise connections and database will not work.
		//it checks if the awards database and the awards and award_type tables exit, and it creates them if not 
		DatabaseManager.init();
		awardsInfo = DatabaseManager.pullAwardsInfo();
		studentsYearByYear = DatabaseManager.load_YearByYearStudents();
		//System.out.println("size"+studentsYearByYear.size());
		studentsNotYearByYear = DatabaseManager.load_NotYearByYearStudents();
		//printStudents(studentsYearByYear);
		//printStudents(studentsNotYearByYear);
		//printAwardInfo(awardsInfo);
		makeYBYPages(studentsYearByYear);
		makeNotYBYPages(studentsNotYearByYear);


		//once information was pulled from the database, parsed, and loaded into the main 
		//datastructure, the ArrayList pages, the listeners need to be notified
		for(int i=0; i<listeners.size(); i++) {
			listeners.get(i).finishLoadingData();
		}
	}

	public void addFinishLoadingDataListener(FinishLoadingDataListener listener) {
		listeners.add(listener);
	}

	/**
	 * This method organizes an ArrayList of Student objects in pages. The ArrayList of students is ordered by year, award,
	 * grade, last name, first name It creates a new page when the next student to be added to a page has a different year, 
	 * different award, or there is no more room on the current page. 
	 * @param an ArrayList of Student objects order by: ....
	 * @return none. It just populates the big data structure pages with appropriate pages 
	 */
	public void makeYBYPages(ArrayList<Student> students){
		if (students.size()>0) {
			// make a new Page(AwardInfo object corresponding to the first student- retrieve from the HashMap an AwardInfo object 
			//corresponding to the award for the first student in students list
			int pageNo = 1;
			Page currentPage = new Page(awardsInfo.get(students.get(0).getAward()), students.get(0).getYear(),pageNo);
			currentPage.addStudent(students.get(0));
			students.get(0).setIndexInPages(0);
			pages.add(currentPage);

			//parse the students list and add each student on pages as appropriate. I
			for (int i = 1; i< students.size(); i++) {
				currentPage = pages.get(pages.size()-1);//get the last page added to pages

				//different type page - if the next student to be added has a different year or a different award than the last page
				//the field ofPage in the previous same type pages is updated to pageNo (1 of 3, 2 of 3, 3 of 3)
				//a new page will be created, and the next student will be added to this page
				if (students.get(i).getYear()!=currentPage.getYear() || !(students.get(i).getAward().equals(currentPage.getAwardInfo().getAward()))){
					//update the ofPages field in the same type pages previously added
					for (int j = pageNo; j>0; j-- ) {
						pages.get(pages.size()-j).setOfPages(pageNo);
					}
					pageNo = 1;
					currentPage = new Page (awardsInfo.get(students.get(i).getAward()),students.get(i).getYear(), pageNo);
					currentPage.addStudent(students.get(i));
					pages.add(currentPage);
					//the indexInPages field of the Student class can be useful if adding a search student feature
					students.get(i).setIndexInPages(pages.size()-1);//update the indexInPages field for the student
				}
				//same type page but no more room: if no more room on this page, but the next student to be added has the same year and the same award, 
				//a new page will be created 
				//else if (currentPage.getStudentsOnPage().size()+1 > (ConfigManager.rows*ConfigManager.columns) && students.get(i).getYear()==currentPage.getYear() && students.get(i).getAward().equals(currentPage.getAwardInfo().getAward())){
				else if (currentPage.getAwardInfo().getAward().equals("Honor Roll") && currentPage.getStudentsOnPage().size()+1 > (ConfigManager.rows_HonorRoll*ConfigManager.columns_HonorRoll)){
					pageNo = currentPage.getPageNo() + 1;
					currentPage = new Page (awardsInfo.get(students.get(i).getAward()),students.get(i).getYear(), pageNo);
					currentPage.addStudent(students.get(i));
					pages.add(currentPage);
					//the indexInPages field of the Student class can be useful if adding a search student feature
					students.get(i).setIndexInPages(pages.size()-1);//update the indexInPages field for the student
				}
				//if enough room on this page, and the next student to be added has the same year and the same award as currentPage,
				else if (currentPage.getAwardInfo().getAward().equals("Honor Roll") && currentPage.getStudentsOnPage().size()+1 <= (ConfigManager.rows_HonorRoll*ConfigManager.columns_HonorRoll)){
					currentPage.addStudent(students.get(i));
					students.get(i).setIndexInPages(pages.size()-1);//update the indexInPages field for the student
				}
				else if (currentPage.getAwardInfo().getAward().equals("Highest Mark Per Subject") && currentPage.getStudentsOnPage().size()+1 > (ConfigManager.rows_HighestMark*ConfigManager.columns_HighestMark)){
					pageNo = currentPage.getPageNo() + 1;
					currentPage = new Page (awardsInfo.get(students.get(i).getAward()),students.get(i).getYear(), pageNo);
					currentPage.addStudent(students.get(i));
					pages.add(currentPage);
					//the indexInPages field of the Student class can be useful if adding a search student feature
					students.get(i).setIndexInPages(pages.size()-1);//update the indexInPages field for the student
				}
				//if enough room on this page, and the next student to be added has the same year and the same award as currentPage,
				else if (currentPage.getAwardInfo().getAward().equals("Highest Mark Per Subject") && currentPage.getStudentsOnPage().size()+1 <= (ConfigManager.rows_HighestMark*ConfigManager.columns_HighestMark)){
					currentPage.addStudent(students.get(i));
					students.get(i).setIndexInPages(pages.size()-1);//update the indexInPages field for the student
				}
			}
			//fill out the ofPages for the last group year by year
			for (int j = pageNo; j>0; j-- ) {
				pages.get(pages.size()-j).setOfPages(pageNo);
			}
		}
	}

	/**
	 * This method organizes an ArrayList of Student objects in pages. The ArrayList of students is ordered by award, year,
	 * grade, last name, first name It creates a new page when there is no more room on the current page.
	 * These pages do not show the year and the award in the title. They contain the individual student awards (Mother Theresa...)AA 
	 * @param no parameters
	 * @return returns an ArrayList of Page object, in order that they need to be displayed 
	 */
	public void makeNotYBYPages(ArrayList<Student> students){
		if (students.size()>0) {
			// make a new Page(AwardInfo object corresponding to the first student- retrieve from the HashMap an AwardInfo object 
			//corresponding to the award for the first student in students list
			int pageNo = 1;//pages ArrayList is already filled out with pages yearByYear
			int firstOtherAwardsIndex = pages.size();
			// make a new Page with a special AwardInfo object - "Other Marcy Awards"
			AwardInfo otherAwards = new AwardInfo ("Other Marcy Awards", "Academic excellence, leadership in the school programs and community involvement", false);
			Page currentPage = new Page(otherAwards, 0 , pageNo);//creates a page with year 0 - not yearByYear, and an "Other Marcy Awards" AwardInfo object
			currentPage.addStudent(students.get(0));
			pages.add(currentPage);
			//the indexInPages field of the Student class can be useful if adding a search student feature
			students.get(0).setIndexInPages(pages.size()-1);


			//parse the students list and add each student on pages as appropriate. I
			for (int i = 1; i< students.size(); i++) {
				currentPage = pages.get(pages.size()-1);//get the last page added to pages

				//if no more room on this page a new page will be created 
				if (currentPage.getStudentsOnPage().size()+1 > (ConfigManager.otherAwards_rows*ConfigManager.otherAwards_columns)){
					pageNo = currentPage.getPageNo() + 1;
					currentPage = new Page (otherAwards, 0, pageNo);
					currentPage.addStudent(students.get(i));
					pages.add(currentPage);
					//the indexInPages field of the Student class can be useful if adding a search student feature
					students.get(i).setIndexInPages(pages.size()-1);//update the indexInPages field for the student
				}

				//if enough room on this page
				else if (currentPage.getStudentsOnPage().size()+1 <= (ConfigManager.otherAwards_rows*ConfigManager.otherAwards_columns)){
					currentPage.addStudent(students.get(i));
					//the indexInPages field of the Student class can be useful if adding a search student feature
					students.get(i).setIndexInPages(pages.size()-1);//update the indexInPages field for the student
				}
			}
			for (int j = firstOtherAwardsIndex; j<pages.size(); j++ ) {
				pages.get(j).setOfPages(pageNo);
			}
		}
	}

	public HashMap<String, AwardInfo> getAwardsInfo(){
		return awardsInfo;
	}

	public ArrayList<Page> getPages(){
		return pages;
	}

	public void printStudents(ArrayList<Student> students) {
		for (int i=0; i<students.size();i++) {
			System.out.println(students.get(i));
		}
	}

	public void printAwardInfo( HashMap<String, AwardInfo> awardsInfo) {
		System.out.println("awardsInfo size: "+awardsInfo.size());
		System.out.println();
		for (int i=0; i<awardsInfo.size();i++) {
		    System.out.println(awardsInfo.get(i));
		}
	}

}
