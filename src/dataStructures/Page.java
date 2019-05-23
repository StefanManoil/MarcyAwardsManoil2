package dataStructures;

import java.util.ArrayList;

public class Page {
 private AwardInfo awardInfo;
 private int year;
 private int pageNo;
 private int ofPages;
 private ArrayList<Student> studentsOnPage;
 public Page (AwardInfo awardInfo, int year, int pageNo) {
  this.awardInfo = awardInfo;
  this.year = year; //if year=0 the page is not year by year
  this.pageNo = pageNo;
  this.ofPages = 0;
  this.studentsOnPage = new ArrayList<Student>();
 }
 
 public int getPageNo() {
  return pageNo;
 }
 
 public void setPageNo(int newPN) {
  pageNo = newPN;
 }
 
 public int getOfPages() {
  return ofPages;
 }
 
 public void setOfPages(int newOP) {
  ofPages = newOP;
 }
 
 public int getYear() {
	  return year;
 }
	 
 public void setYear(int newYear) {
	  year = newYear;
 }
 
 public  ArrayList<Student> getStudentsOnPage(){
  return studentsOnPage;
 }
 
 public  void addStudent(Student newStudent){
	 studentsOnPage.add(newStudent);
 }
 
  public  AwardInfo getAwardInfo(){
     return awardInfo;
  }
  
  public String toString() {
	  return "page "+awardInfo.getAward()+", "+awardInfo.getDescription()+", "+year+", "+pageNo+", "+ofPages;
  }

}
