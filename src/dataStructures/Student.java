package dataStructures;

public class Student {

	private String firstName, lastName;
	private int grade, year, indexInPages;
	private String award;
	private String description;
	public Student(String firstName, String lastName, String award, int grade, int year, String description, int indexInPages) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.year = year;
		this.grade = grade;
		this.award = award;
		this.description=description;
		indexInPages = 0;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String newFN) {
		firstName = newFN;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String newLastName) {
		lastName = newLastName;
	}
	
	public String getAward() {
		return award;
	}
	
	public void setAward(String newAward) {
		award = newAward;
	}
	
	public int getYear() {
		return year;
	}
	
	public void setYear (int newYear) {
		year = newYear;
	}
	
	public int getGrade() {
		return grade;
	}
	
	public void setGrade (int newGrade) {
		grade = newGrade;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription (String newDesc) {
		description = newDesc;
	}
	
	public int getIndexInPages() {
		return indexInPages;
	}
	
	public void setIndexInPages (int newIndexInPages) {
		indexInPages = newIndexInPages;
	}
	//printable representation for Student-for debugging
	public String toString() {
	  if (description == null){
		  description = "";
	  }
	  return lastName+" "+ firstName + ": year-" + year + ", grade-" + grade +", award-"+ award+" "+description;
	}

	//method used to store the Student objects in order: yearByYear, year, award, grade, lastName, firstName
	/*public int compareTo (Student other) {
		if (this.awardInfo.isYearByYear() && !other.awardInfo.isYearByYear()) {
			return 1;
		}
		if (!this.awardInfo.isYearByYear() && other.awardInfo.isYearByYear()) {
			return -1;
		}
		if (this.awardInfo.isYearByYear() && other.awardInfo.isYearByYear()) {//both yearByYear, go ahead and compare year
			if (this.year<other.year) {
				return 1;
			}
			else if(this.year>other.year) {
				return -1;
			}
			else if(this.year==other.year) {//same yearByYear and year, go ahead and compare award
				if (this.awardInfo.getName().compareTo(other.awardInfo.getName())>0){
					return 1;
				}
				else if (this.awardInfo.getName().compareTo(other.awardInfo.getName())<0){
					return -1;
				}
				else if (this.awardInfo.getName().compareTo(other.awardInfo.getName())==0){//same award for both student, go ahead and compare grade
					if (this.grade < other.grade){
						return 1;
					}
					else if (this.grade < other.grade){
						return 1;
					}
					else if (this.grade == other.grade){//same yearByYear, year, award, grade, go ahead and compare lastname
						if (this.lastName.compareTo(other.getLastName())>0) {
							return 1;
						}
						else if (this.getLastName().compareTo(other.getLastName())<0) {
							return -1;
						}
						if (this.getLastName().compareTo(other.getLastName())==0) {//same lastname go ahead compare firstName
							if (this.getFirstName().compareTo(other.getFirstName())>0) {
								return 1;
							}
							else if (this.getFirstName().compareTo(other.getFirstName())<0) {
								return -1;
							}
							if (this.getFirstName().compareTo(other.getFirstName())==0) {//same lastname go ahead compare firstName
								return 0;
							}
						}
					}
				}
			}
		}
		return 0;
		/*if(type.isYearByYear()) {
			rs = s.executeQuery("SELECT * FROM AWARDS WHERE award = '" + type.getName() + "' ORDER BY award, year, grade");
		} else {
			rs = s.executeQuery("SELECT * FROM AWARDS WHERE award = '" + type.getName() + "' ORDER BY award, grade, year");
		}*/
	//}
	
}