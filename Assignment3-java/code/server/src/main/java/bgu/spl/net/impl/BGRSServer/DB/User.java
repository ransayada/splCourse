package bgu.spl.net.impl.BGRSServer.DB;

import java.util.Vector;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Class to create "Course" object, which will contain all the data relevant for the specific course.
 * Implement it's own Sync procedure using the ReadWriteLock activated from the Database class.
 */
public class User {
    private String userName;
    private String pass;
    private boolean isAdmin;
    private boolean isLoggedIn;
    private Object isloggedInLock = new Object();
    private Vector<Integer> listOfCoursesAttendTo;
    private ReadWriteLock courseRegistrationLock;

    public User(String userName, String pass, boolean isAdmin) {
        this.userName = userName;
        this.pass = pass;
        this.isAdmin = isAdmin;
        this.isLoggedIn = false; //user must logged in to flag true in logged in
        listOfCoursesAttendTo = new Vector<Integer>(); // initializing new courses per students collection
        courseRegistrationLock = new ReentrantReadWriteLock();
    }

    public String getUserName() {
        return userName;
    }

    public String getPass() {
        return pass;
    }

    public boolean isAdmin() {
        return isAdmin;
    }


    /**
     * Make sure the user is attending @course, search for the entity in the @listOfCoursesAttendTo
     *
     * @param course
     * @return
     */
    public boolean isAttending(int course) {
        return listOfCoursesAttendTo.contains(course);
    }

    /**
     * Assuming the user isn't registered for the course, register him.
     *
     * @param course
     */
    public void registerToCourse(int course) {
        listOfCoursesAttendTo.add(course);
    }

    /**
     * Assuming the user is registered for the course, unregister him.
     *
     * @param course
     */
    public void unregisterFromCourse(int course) {
        listOfCoursesAttendTo.remove(listOfCoursesAttendTo.indexOf(course));
    }

    /**
     * Change isLoggedIn for the user, making sure the change is valid.
     * @param loggedIn
     * @return true if successful or false if the change isn't valid
     */
    public boolean setLoggedIn(boolean loggedIn) {
        synchronized (isloggedInLock) {
            if((isLoggedIn&loggedIn)|(!isLoggedIn&!loggedIn)) return false;
            isLoggedIn = loggedIn;
            return true;
        }
    }

    public Vector<Integer> getListOfCoursesAttendTo() {
        return listOfCoursesAttendTo;
    }
    public ReadWriteLock getCourseRegistrationLock(){return courseRegistrationLock;}

}
