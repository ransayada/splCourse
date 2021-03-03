package bgu.spl.net.impl.BGRSServer.DB;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;

public class Database {
    private static class SingletonHolder {
        private static Database instance = new Database();
    }

    //TODO:SYNC
    private String pathCourses = null;
    private ConcurrentHashMap<Integer, Course> coursesDB;
    private ConcurrentHashMap<String, User> usersDB;
    private ArrayList<Integer> courseOrder;


    //to prevent user from creating new Database
    private Database() {
        // TODO: implement - make sure threadSafe singelton?
        coursesDB = new ConcurrentHashMap<Integer, Course>();
        usersDB = new ConcurrentHashMap<String, User>();
        courseOrder = new ArrayList<Integer>();
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static Database getInstance() { // singleton instance checker
        return SingletonHolder.instance;
    }

    /**
     * makes Course Object from string line.
     *
     * @param line
     * @return course object
     */
    private Course strToCourse(String line) {
        //course number
        int pointer1 = line.indexOf('|');
        int courseNum = Integer.parseInt(line.substring(0, pointer1)); //int value of string
        line = line.substring(pointer1 + 1);
        //course name
        int pointer2 = line.indexOf('|');
        String courseName = line.substring(0, pointer2);
        line = line.substring(pointer2 + 1);
        //kdam courses list
        int pointer3 = line.indexOf('|');
        String kdamCoursesString = line.substring(1, pointer3 - 1);
        ArrayList<String> kdamCoursesList = new ArrayList(Arrays.asList(kdamCoursesString.split(",")));
        ArrayList<Integer> kdamCoursesListInt = new ArrayList();
        if (!kdamCoursesString.equals("")) {
            for (String s : kdamCoursesList) {
                kdamCoursesListInt.add(Integer.parseInt(s));
            }
        }
        line = line.substring(pointer3 + 1);
        //number of students
        int numOfMaxStudents = Integer.parseInt(line);
        return new Course(courseNum, courseName, kdamCoursesListInt, numOfMaxStudents); //course class to be implemented
    }

    /**
     * loades the courses from the file path {@code coursesFilePath }specified
     * into the Database, returns true if successful.
     *
     * @param coursesFilePath
     * @return true if succeed anf false if not
     */
    public boolean initialize(String coursesFilePath) {
        if (pathCourses != null) throw new RuntimeException("The Database is already initialized");
        pathCourses=coursesFilePath;
        try (BufferedReader reader = new BufferedReader(new FileReader(pathCourses))) //create new buffer reader
        {
            String line = reader.readLine(); // reads the first line of the txt file
            while (line != null) { //loop stops when there are no remaining lines
                String lineClone = line;
                Course course = strToCourse(lineClone); // makes a course object out of the line
                int pointer = line.indexOf('|');
                Integer courseNum = Integer.parseInt(line.substring(0, pointer)); // put the new course in the hash map
                courseOrder.add(courseNum);
                coursesDB.putIfAbsent(courseNum, course);
                // read next line
                line = reader.readLine();
            }
        } catch (IOException e) { //TODO: ask what should be here
            return false;
        }
        for (Course course : coursesDB.values()) { //loop on every value on courses DB
            ArrayList<Integer> temp = course.getKdamCoursesList();
            temp.sort(Comparator.comparingInt(o -> courseOrder.indexOf(o))); // reordering the kdam list to the fixed order
            course.setKdamCoursesList(temp);
        }
        return true;
    }

    //DB functions for protocol's use:

    /**
     * Make sure @userName is a registered user.
     *
     * @param userName
     * @return
     */
    private boolean isUser(String userName) {
        return usersDB.containsKey(userName);
    }

    /**
     * Make sure if @course is a valid course.
     *
     * @param course
     * @return
     */
    private boolean isCourse(int course) {
        return coursesDB.containsKey(course);
    }

    /**
     * Make sure if the user is an admin or not. If doesn't exist - throw an exception.
     *
     * @param userName
     * @return
     * @throws NoSuchElementException
     */
    public boolean isAdmin(String userName) throws NoSuchElementException {
        if (!isUser(userName)) throw new NoSuchElementException("no such userName");
        return usersDB.get(userName).isAdmin();
    }

    /**
     * Try to register a new user if possible.
     * If already exist - return false;
     * else, create a new User entity and add to the userDb.
     * <p>
     * #method is blocking on usersDB as isUser and put are separated methods.
     *
     * @param userName
     * @param password
     * @param isAdmin
     * @return
     */
    public boolean registerNewUser(String userName, String password, boolean isAdmin) {
        synchronized (usersDB) {
            if (isUser(userName)) return false;
            User userToRegister = new User(userName, password, isAdmin);
            usersDB.put(userName, userToRegister);
            return true;
        }
    }


    /**
     * Try to register @userName to @courseNumber.
     * Validate:
     * -The user and the course are valid entities
     * -The user isn't already attending this course
     * -The user has all relevant Kdams
     * -The course still has place
     * <p>
     * #Note that in order to remain threadSafe, both the user and courses registration WriteLocks will be locked for the actions
     *
     * @param userName
     * @param courseNumber
     * @return true if registered successfully.
     */
    public boolean registerToCourse(String userName, int courseNumber) {
        boolean result = true;
        try {
            if (isRegisteredForCourse(userName, courseNumber)) return false;
        } catch (NoSuchElementException e) {
            return false;
        }
        User user1 = usersDB.get(userName);
        Course course1 = coursesDB.get(courseNumber);
        ArrayList<Integer> kdam = course1.getKdamCoursesList();

        for (int i : kdam) {
            if (!user1.isAttending(i)) return false;
        }

        //WriteLock the user's and course's registration Lock
        ReadWriteLock courseRegistrationLock = course1.getCourseRegistrationLock();
        ReadWriteLock userRegistrationLock = user1.getCourseRegistrationLock();
        courseRegistrationLock.writeLock().lock();
        userRegistrationLock.writeLock().lock();

        //Make sure the user attends all courses in the kdam list for courseNumber

        //try to addStudent to course, if doesn't have place - return false
        if (!course1.addStudent(userName)) result = false;
        //add the course to the coursesList for the user.
        if(result) user1.registerToCourse(courseNumber);

        //unLock the user's and course's registration Lock
        courseRegistrationLock.writeLock().unlock();
        userRegistrationLock.writeLock().unlock();
        return result;
    }

    /**
     * Try to unregister the user from the course. Make sure are valid entities, and the user was attending this course.
     * #Note that in order to remain threadSafe, both the user and courses registration WriteLocks will be locked for the actions
     *
     * @param userName
     * @param courseNumber
     * @return true if successfully done
     */
    public boolean unRegisterFromCourse(String userName, int courseNumber) {
        try {
            if (!isRegisteredForCourse(userName, courseNumber)) return false;
        } catch (NoSuchElementException e) {
            return false;
        }
        User user1 = usersDB.get(userName);
        Course course1 = coursesDB.get(courseNumber);

        //WriteLock the user's and course's registration Lock
        ReadWriteLock courseRegistrationLock = course1.getCourseRegistrationLock();
        ReadWriteLock userRegistrationLock = user1.getCourseRegistrationLock();
        courseRegistrationLock.writeLock().lock();
        userRegistrationLock.writeLock().lock();

        //unRegister
        course1.removeStudent(userName);
        user1.unregisterFromCourse(courseNumber);

        //unLock the user's and course's registration Lock
        courseRegistrationLock.writeLock().unlock();
        userRegistrationLock.writeLock().unlock();
        return true;
    }

    /**
     * Log in the user if possible and return the result:
     * if @userName doesn't exists, password isn't correct or is already logged in - return false;
     * else - true
     *
     * @param userName
     * @param password
     * @return
     */
    public boolean logInUser(String userName, String password) {
        if (!isUser(userName)) return false;
        User user1 = usersDB.get(userName);
        if ((user1.getPass().equals(password))) {
            return user1.setLoggedIn(true);
        }
        return false;
    }

    /**
     * Login the user if possible and return the result:
     * if @userName doesn't exists, or is not logged in - return false;
     * else - true
     *
     * @param userName
     * @return
     */
    public boolean logOutUser(String userName) {
        if (!isUser(userName)) return false;
        User user1 = usersDB.get(userName);
        return user1.setLoggedIn(false);

    }


    /**
     * Return the kdam list for the course as a string. Throw exception if doesn't exist
     *
     * @param courseNumber
     * @return
     * @throws NoSuchElementException
     */
    public String getKdamForCourse(int courseNumber) throws NoSuchElementException {
        if (!isCourse(courseNumber)) throw new NoSuchElementException("No such Course");
        return coursesDB.get(courseNumber).getKdamCoursesList().toString().replace(" ", ""); //TODO:make sure good toString;
    }

    /**
     * Return the courses the user has registered to, ordered based on the original courses.txt order.
     * Throw exception if the user doesn't exist
     * <p>
     * #Note that in order to remain threadSafe, the user registration ReadLocks will be locked for the actions
     *
     * @param userName
     * @return
     * @throws NoSuchElementException
     */
    public String getMyCourses(String userName) throws NoSuchElementException {
        if (!isUser(userName)) throw new NoSuchElementException("No such user");
        User user1 = usersDB.get(userName);

        //ReadLock the user's registration Lock
        ReadWriteLock courseRegistrationLock = user1.getCourseRegistrationLock();
        courseRegistrationLock.readLock().lock();
        Vector<Integer> temp = user1.getListOfCoursesAttendTo();

        //unLock the user's and course's registration Lock
        courseRegistrationLock.readLock().unlock();

        temp.sort(Comparator.comparingInt(o -> courseOrder.indexOf(o)));
        return temp.toString().replace(" ", ""); //TODO:make sure good toString
    }

    /**
     * Make sure the user is registered to this course. Throw exception in case the user/course doesn't exists.
     * <p>
     * #Note that in order to remain threadSafe, the user registration ReadLocks will be locked for the actions
     *
     * @param userName
     * @param courseNumber
     * @return
     * @throws NoSuchElementException
     */
    public boolean isRegisteredForCourse(String userName, int courseNumber) throws NoSuchElementException {
        if (!isUser(userName) | (!isCourse(courseNumber))) throw new NoSuchElementException("No such Course/User");
        User user1 = usersDB.get(userName);

        //ReadLock the user's registration Lock
        ReadWriteLock courseRegistrationLock = user1.getCourseRegistrationLock();
        courseRegistrationLock.readLock().lock();
        boolean result = user1.isAttending(courseNumber);

        //unLock the user's and course's registration Lock
        courseRegistrationLock.readLock().unlock();

        return result;
    }

    /**
     * Return the CourseStats - number, name, available seats and registered students.
     * Throw an exception if there is no such course.
     * <p>
     * #Note that in order to remain threadSafe, the course's registration ReadLocks will be locked for the actions
     *
     * @param courseNumber
     * @return
     * @throws NoSuchElementException
     */
    public String getCourseStat(int courseNumber) throws NoSuchElementException {
        if (!isCourse(courseNumber)) throw new NoSuchElementException("No such Course");
        Course course1 = coursesDB.get(courseNumber);
        StringBuilder st = new StringBuilder();
        //Add course's name
        st.append("Course: (" + courseNumber + ") " + course1.getCourseName());
        st.append("\n");
        //Add Seats Available
        ReadWriteLock courseRegistrationLock = course1.getCourseRegistrationLock();
        courseRegistrationLock.readLock().lock();
        st.append("Seats Available: " + course1.getNumOfAvailableSeats() + "/" + course1.getNumOfMaxStudents());
        st.append("\n");
        //Add registered students
        st.append("Students Registered: " + course1.getListOfStudents().toString().replace(" ", ""));
        courseRegistrationLock.readLock().unlock();
        return st.toString();
    }//TODO:implement

    /**
     * Return the StudentStats - Name and courses he's registered too based on the order in courses.txt
     * Throw an exception if the userName isn't registered.
     * <p>
     * #Note that in order to remain threadSafe, the user registration ReadLocks will be locked for the actions
     *
     * @param userName
     * @return
     * @throws NoSuchElementException
     */
    public String getStudentStat(String userName) throws NoSuchElementException {
        if (!isUser(userName)||isAdmin(userName)) throw new NoSuchElementException("No such student");
        User user1 = usersDB.get(userName);
        StringBuilder st = new StringBuilder();
        //Add student's name
        st.append("Student: " + userName);
        st.append("\n");
        //Add list of the courses based on the order in courses.txt
        ReadWriteLock courseRegistrationLock = user1.getCourseRegistrationLock();
        courseRegistrationLock.readLock().lock();
        Vector<Integer> sorted = user1.getListOfCoursesAttendTo();
        courseRegistrationLock.readLock().unlock();
        sorted.sort(Comparator.comparingInt(o -> courseOrder.indexOf(o)));
        st.append("Courses: " + sorted.toString().replace(" ", ""));
        return st.toString();
    }
}
