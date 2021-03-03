package bgu.spl.net.impl.BGRSServer.DB;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Class to create "Course" object, which will contain all the data relevant for the specific course.
 * Implement it's own Sync procedure using the ReadWriteLock activated from the Database class.
 */
public class Course {
    //courseNum|courseName|KdamCoursesList|numOfMaxStudents
    private int courseNum;
    private String courseName;
    private ArrayList<Integer> KdamCoursesList;
    private int numOfMaxStudents;
    private int numOfAvailableSeats;
    private Set<String> listOfStudents;
    private ReadWriteLock courseRegistrationLock;


    public Course(int courseNum, String courseName, ArrayList<Integer> kdamCoursesList, int numOfMaxStudents) {
        this.courseNum = courseNum;
        this.courseName = courseName;
        this.KdamCoursesList = kdamCoursesList;
        this.numOfMaxStudents = numOfMaxStudents;
        this.numOfAvailableSeats = numOfMaxStudents;
        this.listOfStudents = ConcurrentHashMap.newKeySet();
        courseRegistrationLock = new ReentrantReadWriteLock();
    }


    public String getCourseName() {
        return courseName;
    }

    public ArrayList<Integer> getKdamCoursesList() {
        return KdamCoursesList;
    }

    public void setKdamCoursesList(ArrayList<Integer> kdamCoursesList) {
        KdamCoursesList = kdamCoursesList;
    }

    public int getNumOfMaxStudents() {
        return numOfMaxStudents;
    }

    public int getNumOfAvailableSeats() {
        return numOfAvailableSeats;
    }

    public ArrayList<String> getListOfStudents() {
        ArrayList<String> list = new ArrayList<>(listOfStudents);
        list.sort(Comparator.naturalOrder());
        return list;
    }

    /**
     * Add a student to the course if there is an empty seat and he wasn't registered to this course before.
     * @param studentName
     * @return
     */
    public boolean addStudent(String studentName) {
        //Make sure there is place in the course (return false if not)
        if((numOfAvailableSeats==0)|listOfStudents.contains(studentName)) return false;
        numOfAvailableSeats=numOfAvailableSeats-1;
        listOfStudents.add(studentName);
        return true;
    }

    /**
     * Remove a student from the course. make sure to increase the amount of empty seats.
     * Asuming the user is already registered.
     *
     * @param studentName
     * @return
     */
    public void removeStudent(String studentName) {
        listOfStudents.remove(studentName);
        numOfAvailableSeats=numOfAvailableSeats+1;
    }
    public ReadWriteLock getCourseRegistrationLock(){return courseRegistrationLock;}

}
