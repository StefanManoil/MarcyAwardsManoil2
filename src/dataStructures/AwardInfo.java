package dataStructures;


public class AwardInfo {
	private String award, description;
	private boolean yearByYear;
	public AwardInfo(String award, String description, boolean yearByYear) {
		this.award = award;
		this.description = description;
		this.yearByYear = yearByYear;
	}
	
	public boolean isYearByYear() {
		return yearByYear;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAward() {
		return award;
	}

	public void setAward(String newAward) {
		this.award = newAward;
	}
	
	public String toString() {
		return award+", yearByYear "+yearByYear+ " desc: "+description;
	}

}
