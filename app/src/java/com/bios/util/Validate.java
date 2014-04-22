/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bios.util;

import com.bios.model.*;
import com.bios.dao.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author andreng
 */
public class Validate {

    /**
     * Uses a StudentID and a Course to check if the student qualifies for the
     * course.
     *
     * @param studentID the ID of the student to check
     * @param uncheckedCourse the intended course he/she is taking.
     * @return true when prerequisites are met, false otherwise.
     * @throws SQLException
     */
    public static boolean prerequisiteCheck(String studentName, String uncheckedCourse) throws SQLException {
        // To leverage on other checks
        // checks if student exist or not
        // checks if Course exists or not.
        if (studentName == null || studentName.equals("")) {
            return false;
        }

        if (uncheckedCourse == null || uncheckedCourse.equals("")) {
            return false;
        }
        // get list of prerequisites
        ArrayList<Prerequisite> prerequisiteList = new ArrayList<Prerequisite>();
        Connection con = ConnectionManager.getConnection();
        PreparedStatement psmt = con.prepareStatement("SELECT * FROM PREREQUISITE WHERE `course` = ?");
        psmt.setString(1, uncheckedCourse);
        ResultSet rs1 = psmt.executeQuery();

        while (rs1.next()) {
            prerequisiteList.add(new Prerequisite(rs1.getString("course"),
                    rs1.getString("prerequisite")));
        }

        // get list of completed course by student
        //ArrayList<CourseComplete> courseCompletedList = new ArrayList<CourseComplete>();
        ArrayList<String> completedCodes = new ArrayList<String>();
        PreparedStatement psmt2 = con.prepareStatement("SELECT * FROM COURSE_COMPLETED WHERE `userID` = ?");
        psmt2.setString(1, studentName);
        ResultSet rs2 = psmt2.executeQuery();

        while (rs2.next()) {
            completedCodes.add(rs2.getString("code"));
        }

        for (Prerequisite eachPrerequisite : prerequisiteList) {
            if (!completedCodes.contains(eachPrerequisite.getPrerequisite())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Uses a StudentName and a CourseID to check if the student completed the
     * course.
     *
     * @param studentID the ID of the student to check
     * @param courseID the intended course he/she wants to take.
     * @return true when the student has already taken the course.
     * @throws SQLException
     */
    public static boolean checkIfCompleted(String studentID, String courseID) throws SQLException {

        if (studentID == null || studentID.equals("")) {
            return false;
        }

        if (courseID == null || courseID.equals("")) {
            return false;
        }

        Connection con = ConnectionManager.getConnection();
        PreparedStatement psmt = con.prepareStatement("SELECT * FROM COURSE_COMPLETED WHERE userID = ?  AND code = ?");
        psmt.setString(1, studentID);
        psmt.setString(2, courseID);

        ResultSet rs = psmt.executeQuery();

        if (rs.next()) {
            return true;
        }

        return false;
    }

    /**
     * Uses a StudentID and proposed Expenditure to check if there is enough
     * balance.
     *
     * @param studentID the ID of the student to check
     * @param expenditure proposed Expenditure.
     * @return true when proposed expenditure is within budget.
     * @throws SQLException
     */
    public static boolean checkIfEnoughMoney(String studentID, double expenditure) throws SQLException {

        Student thisStudent = studentDAO.retrieveStudentByUserid(studentID);
        if (thisStudent == null) {
            return false;
        }

        if (thisStudent.getEDollars() >= expenditure) {
            return true;
        }
        return false;
    }

    /**
     * Give a course code, verify that the code is valid
     *
     * @param courseID the course code to check
     * @return true if the course code is valid
     * @throws SQLException
     */
    public static boolean checkIfValidCourse(String courseID) throws SQLException {
        if (courseID == null || courseID.equals("")) {
            return false;
        }

        List<Course> allCourses = courseDAO.retrieveAll();
        for (Course eachCourse : allCourses) {
            String eachCourseID = eachCourse.getCode();
            if (eachCourseID.equals(courseID)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Uses a student ID to check it it exist.
     *
     * @param studentID the ID of the student
     * @return true when the student exists.
     * @throws SQLException
     */
    public static boolean checkIfStudentExist(String userID) throws SQLException {

        if (userID == null || userID.equals("")) {
            return false;
        }

        List<Student> allStudents = studentDAO.retrieveAll();
        for (Student eachStudent : allStudents) {
            String eachStudentID = eachStudent.getUserID();
            if (eachStudentID.equals(userID)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Uses a courseID and a studentID to check if bid exist.
     *
     * @param studentID the ID of the student
     * @param courseID the course code to check
     * @return true when the bid exists.
     * @throws SQLException
     */
    public static boolean checkIfBidExist(String userID, String courseID) throws SQLException {
        if (userID == null || userID.equals("")) {
            return false;
        }
        if (courseID == null || courseID.equals("")) {
            return false;
        }

        List<Bid> bids = bidDAO.retrieveBidByStudentCourse(userID, courseID);
        if (bids == null || bids.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Uses a courseID, Section to check if given section exists for the given
     * courseID.
     *
     * @param courseID the course code to verify
     * @param section the section number to verify
     * @return true when the section exists within the given course.
     * @throws SQLException
     */
    public static boolean checkIfCourseSectionExist(String courseID, String section) throws SQLException {
        if (courseID == null || courseID.equals("")) {
            return false;
        }
        if (section == null || section.equals("")) {
            return false;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT * FROM SECTION WHERE course= ? AND section=? ;");
            stmt.setString(1, courseID);
            stmt.setString(2, section);
            rs = stmt.executeQuery();

            while (!rs.next()) {
                return false;
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }

        return true;
    }

    /**
     * Compares two Section objects and checks if it overlaps or not.
     *
     * @param s1 first section
     * @param s2 second section
     * @return true when overlap occurs else false.
     * @throws SQLException
     */
    public static boolean checkOverlapClass(Section s1, Section s2) {

        if (s1 == null || s2 == null) {
            return false;
        }

        boolean result = true;

        if (s1.getDay() == s2.getDay()) {

            String time1_start = s1.getStart().replace(":", "");
            String time1_end = s1.getEnd().replace(":", "");

            String time2_start = s2.getStart().replace(":", "");
            String time2_end = s2.getEnd().replace(":", "");

            int startTime_1 = Integer.parseInt(time1_start);
            int endTime_1 = Integer.parseInt(time1_end);

            int startTime_2 = Integer.parseInt(time2_start);
            int endTime_2 = Integer.parseInt(time2_end);

            if (startTime_2 >= endTime_1 || startTime_1 >= endTime_2) {
                result = false;
            }
        } else {
            result = false;
        }

        return result;

    }

    /**
     * Compares two Course objects and checks if exam timings overlaps or not.
     *
     * @param c1 first course
     * @param c2 second course
     * @return true when overlap occurs else false.
     * @throws SQLException
     */
    public static boolean checkOverlapExam(Course c1, Course c2) {

        if (c1 == null || c2 == null) {
            return false;
        }

        boolean result = true;

        if (c1.getExam_Date().equals(c2.getExam_Date())) {

            String time1_start = c1.getExam_Start().replace(":", "");
            String time1_end = c1.getExam_End().replace(":", "");

            String time2_start = c2.getExam_Start().replace(":", "");
            String time2_end = c2.getExam_End().replace(":", "");

            int startTime_1 = Integer.parseInt(time1_start);
            int endTime_1 = Integer.parseInt(time1_end);

            int startTime_2 = Integer.parseInt(time2_start);
            int endTime_2 = Integer.parseInt(time2_end);

            if (startTime_2 >= endTime_1 || startTime_1 >= endTime_2) {
                result = false;
            }
        } else {
            result = false;
        }

        return result;

    }

    /**
     * Given a studentID this method checks if student has reached the limit of
     * 5 sections.
     *
     * @param studentID the ID of the student to check
     * @return true when limit of 5 has reached else false.
     * @throws SQLException
     */
    public static boolean checkIfSectionLimitReached(String studentID) throws SQLException {

        if (studentID == null || studentID.equals("")) {
            return false;
        }

        List<Bid> allBids = bidDAO.retrieveBidByStudent(studentID);
        List<Bid> successfulBidsRound1 = SectionStudentDAO.retrieveBidsByStudent(studentID);
        List<Bid> successfulBidsRound2 = SectionStudentRound2DAO.retrieveBidsByStudent(studentID);
        allBids.addAll(successfulBidsRound1);
        allBids.addAll(successfulBidsRound2);

        if (allBids.size() >= 5) {
            return true;
        }

        return false;
    }

    /**
     * Given a studentID, courseID and SectionID this method checks if student
     * has been successfully enrolled or not.
     *
     * @param studentID the ID of the student to verify
     * @param courseID the course code of the bid
     * @param setion the section code of the bid
     * @return true when the student's bid is successful.
     * @throws SQLException
     */
    public static boolean checkIfSuccessfulBid(String studentID, String courseID, String section) throws SQLException {
        if (studentID == null || courseID == null || section == null) {
            return false;
        }

        if (studentID.equals("") || courseID.equals("") || section.equals("")) {
            return false;
        }

        List<Bid> allBids = SectionStudentDAO.retrieveAll();
        List<Bid> allBidsRound2 = SectionStudentRound2DAO.retrieveAll();
        allBids.addAll(allBidsRound2);

        for (Bid eachBid : allBids) {
            if (eachBid.getCode().equalsIgnoreCase(courseID)
                    && eachBid.getSection().equalsIgnoreCase(section)
                    && eachBid.getUserID().equalsIgnoreCase(studentID)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Given a date, verify that the date is valid
     *
     * @param date date given
     * @return true if the date is valid
     */
    public static boolean checkIfDateValid(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        sdf.setLenient(false);
        //test if the given date can be parsed into a SimpleDateFormat
        if (date.length() != 8) {
            return false;
        }
        try {
            sdf.parse(date);
        } catch (Exception e) {
            e.getMessage();
            return false;
        }

        return true;
    }

    /**
     * Given a time, verify it matches the desired pattern
     *
     * @param time input of time from the user
     * @return true if time follows the format HH:MM
     */
    public static boolean checkIfTimeValid(String time) {
        if (time == null || time.equals("")) {
            return false;
        }
        String formatPattern = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
        try {
            Pattern pattern = Pattern.compile(formatPattern);
            Matcher matcher = pattern.matcher(time);
            return matcher.matches();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Given a time, verify it matches the desired pattern
     *
     * @param time input of time from the user
     * @return true if time follows the format HH:MM
     */
    public static boolean checkIfStartTimeValid(String time) {
        if (time == null || time.equals("")) {
            return false;
        }
        String formatPattern = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
        try {
            Pattern pattern = Pattern.compile(formatPattern);
            Matcher matcher = pattern.matcher(time);
            matcher.matches();
        } catch (Exception e) {
            return false;
        }
        if (time.equals("08:30") || time.equals("8:30")
                || time.equals("12:00") || time.equals("15:30")
                || time.equals("19:00")) {
            return true;
        }
        return false;
    }

    /**
     * Given a time, verify it matches the desired pattern
     *
     * @param time input of time from the user
     * @return true if time follows the format HH:MM
     */
    public static boolean checkIfEndTimeValid(String time) {
        if (time == null || time.equals("")) {
            return false;
        }
        String formatPattern = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
        try {
            Pattern pattern = Pattern.compile(formatPattern);
            Matcher matcher = pattern.matcher(time);
            matcher.matches();
        } catch (Exception e) {
            return false;
        }
        if (time.equals("11:45") || time.equals("15:15")
                || time.equals("18:45") /*|| time.equals("22:15")*/) {
            return true;
        }
        return false;
    }

    /**
     * Given a section, verify if the course and the section is valid
     *
     * @param courseID section given by user
     * @param section section given by user
     * @return true if section and course is valid
     * @throws SQLException
     */
    public static boolean checkIfSectionValid(String courseID, String section) throws SQLException {
        //if course is invalid, return false

        if (courseID == null || courseID.equals("") || section == null || section.equals("")) {
            return false;
        }

        if (!checkIfValidCourse(courseID)) {
            return false;
        }

        //if first char not S or s, return false
        String code = section;

        if (code.charAt(0) != 'S') {
            return false;
        }

        if (code.charAt(1) == '0') {
            return false;
        }
        //check if the rest of the code is an integer
        String codeNumber = code.substring(1);
        try {
            int number = Integer.parseInt(codeNumber);

            if (number < 1 || number > 99) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * Given a section of a particular course, check if the section is valid
     * after bootstrapping is done
     *
     * @param courseID the course code
     * @param section the section code
     * @return true if the section is valid after bootstrap
     * @throws SQLException
     */
    public static boolean checkIfSectionValidAfterBootstrap(String courseID, String section) throws SQLException {
        //if course is invalid, return false

        if (courseID == null || courseID.equals("") || section == null || section.equals("")) {
            return false;
        }

        if (!checkIfValidCourse(courseID)) {
            return false;
        }

        //if first char not S or s, return false
        String code = section;

        if (code.charAt(0) != 'S') {
            return false;
        }

        //check if the rest of the code is an integer
        String codeNumber = code.substring(1);
        try {
            int number = Integer.parseInt(codeNumber);

            if (number < 1 || number > 99) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        Section s = sectionDAO.retrieveCourseSection(courseID, section);
        if (s == null) {
            return false;
        }
        return true;
    }

    /**
     * Verify a given day is valid
     *
     * @param day input from user
     * @return true if the day is between 1 and 7, inclusive
     */
    public static boolean checkIfDayValid(int day) {
        if (day < 1 || day > 7) {
            return false;
        }
        return true;
    }

    /**
     * Checks if a given class size is valid
     *
     * @param size the size of the class
     * @return true if the size is a positive integer
     */
    public static boolean checkIfSizeValid(int size) {
        if (size < 0) {
            return false;
        }

        return true;
    }

    /**
     * Given an amount, verify amount is valid
     *
     * @param amount amount given by user
     * @return true if amount is to 2 decimal places and only contains numbers
     */
    public static boolean checkIfAmountValid(double amount) {
        String value = amount + "";
        //check for '.' and if there is only up to 2 decimal places
        String decimalPlaces = null;
        try {
            decimalPlaces = value.substring(value.indexOf('.'));
            if (decimalPlaces.length() > 3) {
                return false;
            }

            //if successful, get numbers only
            decimalPlaces = decimalPlaces.substring(1);

        } //error occurs if amount.indexOf() returns -1
        catch (IndexOutOfBoundsException e) {
            return false;
        }

        //return false if amount is less than 10
        if (Double.compare(amount, 10.00) < 0) {
            return false;
        }

        return true;
    }

    /**
     * Given an amount from SQL, use the amount as a String to verify if the
     * amount is valid
     *
     * @param amount the amount to check, as a String
     * @return true if the amount is valid
     */
    public static boolean checkIfAmountValid(String amount) {
        if (amount.indexOf('-') != -1) {
            return false;
        }
        if (amount.indexOf('.') != -1) {
            int decimals = amount.length() - amount.indexOf('.') - 1;
            if (decimals > 2) {
                return false;
            }
        }
        double amountDouble = 0;
        try {
            amountDouble = Double.parseDouble(amount);
        } catch (NumberFormatException e) {
            return false;
        }
        if (amountDouble < 10) {
            return false;
        }
        return true;
    }

    /**
     * Given a field, check if the field is blank
     *
     * @param check the field from the .csv file
     * @return true if field is blank
     */
    public static boolean checkIfFieldBlank(String check) {
        if (check == null || check.equals("")) {
            return true;
        }
        return false;
    }

    /**
     * Given a course code, check if any sections exists under it
     *
     * @param courseID the course code to verify
     * @return true there are sections under this courseID
     */
    public static boolean checkIfCourseHasSection(String courseID) throws SQLException {
        Section thisSection = sectionDAO.retrieveCourse(courseID);

        if (thisSection == null) {
            return false;
        }
        return true;
    }

    /**
     * Given the course and section ID of a bid, verify that the student's bid
     * does not clash with his existing timetable
     *
     * @param studentID the student ID to verify
     * @param courseID the course code of the new bid
     * @param section the section code of the new bid
     * @return true of the given course and section clashes with the present
     * timetable
     * @throws SQLException
     */
    public static boolean checkClashWithAllTimetable(String studentID, String courseID, String section) throws SQLException {

        if (studentID == null || courseID == null || section == null) {
            return false;
        } else if (studentID.equals("") || courseID.equals("") || section.equals("")) {
            return false;
        }

        try {

            Section thisSection = sectionDAO.retrieveCourseSection(courseID, section);
            List<Bid> pendingBids = bidDAO.retrieveBidByStudent(studentID);

            for (Bid bid : pendingBids) {

                Section eachSection = sectionDAO.retrieveCourseSection(bid.getCode(), bid.getSection());
                if (Validate.checkOverlapClass(thisSection, eachSection)) {
                    return true;
                }
            }

            List<Bid> confirmedBids = SectionStudentDAO.retrieveBidsByStudent(studentID);
            List<Bid> successfulBidsRound2 = SectionStudentRound2DAO.retrieveBidsByStudent(studentID);
            confirmedBids.addAll(successfulBidsRound2);

            for (Bid bid : confirmedBids) {
                Section eachSection = sectionDAO.retrieveCourseSection(bid.getCode(), bid.getSection());
                if (Validate.checkOverlapClass(thisSection, eachSection)) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Given a studentID, a course and section pair, check if any timetable
     * clash with student's existing exams.
     *
     * @param studentID the ID of the student to verify
     * @param courseID the course code of the course to verify
     * @return true when there is a clash
     */
    public static boolean checkClashWithExams(String studentID, String courseID) throws SQLException {

        if (studentID == null || courseID == null) {
            return false;
        } else if (studentID.equals("") || courseID.equals("")) {
            return false;
        }

        try {

            Course thisCourse = courseDAO.retreiveCourseByCode(courseID);
            List<Bid> pendingBids = bidDAO.retrieveBidByStudent(studentID);

            for (Bid bid : pendingBids) {
                Course eachCourse = courseDAO.retreiveCourseByCode(bid.getCode());
                if (Validate.checkOverlapExam(thisCourse, eachCourse)) {
                    return true;
                }
            }

            List<Bid> confirmedBids = SectionStudentDAO.retrieveBidsByStudent(studentID);

            for (Bid bid : confirmedBids) {
                Course eachCourse = courseDAO.retreiveCourseByCode(bid.getCode());
                if (Validate.checkOverlapExam(thisCourse, eachCourse)) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Given a studentID and a course, check if course belongs to the school.
     *
     * @param studentID the student to use for the check
     * @param courseID the code of the course to be verified
     * @return true when the course belongs to the school
     */
    public static boolean checkIfCourseFromStudentSchool(String studentID, String courseID) throws SQLException {
        String courseSch = courseDAO.retreiveCourseByCode(courseID.trim()).getSchool();
        if (courseSch.equalsIgnoreCase(studentDAO.retrieveStudentByUserid(studentID).getSchool())) {
            return true;
        }
        return false;
    }

    /**
     * Given a section, verify if the section still has vacancies
     *
     * @param courseID section given by user
     * @param section section given by user
     * @return true if section and course is valid & still has vacancies
     * @throws SQLException
     */
    public static boolean checkIfSectionHasVacancies(String courseID, String section) throws SQLException {
        if (!checkIfSectionValid(courseID, section)) {
            return false;
        }
        Section s = sectionDAO.retrieveCourseSection(courseID, section);
        List<Bid> enrolledList = SectionStudentDAO.retrieveBidsBySection(courseID, section);
        int vacancies = s.getSize() - enrolledList.size();
        if (vacancies == 0) {
            return false;
        }
        return true;
    }

    /**
     * This is a universal missing field and blank field checker for json input
     * @param obj JsonObject that you are testing.
     * @param properties this is the attribute name that you are interested
     * @returns a JsonArray
     */

        public static JsonArray checkMissing(JsonObject obj, String... properties){
        JsonArray errors = new JsonArray();
        List<String> errorMsg = new ArrayList<String>();
        List<String> found = new ArrayList<String>();
        for (String p : properties) {
            if(obj.get(p) == null){
                String msg = p + " is missing";
                errorMsg.add(msg);
            }else{
                found.add(p);
            }
        }
        
        for (String p : found) {
            if(obj.get(p).getAsString().equals("")){
                String msg = p + " is blank";
                errorMsg.add(msg);
            }
        }
        
        Collections.sort(errorMsg);
        
        for (String e : errorMsg) {
            errors.add(new JsonPrimitive(e));
        }
        
        return errors;
    }
    
    
        /**
     * Main method used to test the various methods implemented in this class
     *
     * @param args
     */

    
    public static void main(String[] args) {
        try {
            System.out.println(prerequisiteCheck(null, "IS100"));
            System.out.println(checkIfCompleted("ben.ng.2009", "IS102"));
            System.out.println(checkIfEnoughMoney("ben.ng.2009", 100000));
            System.out.println(checkIfValidCourse("MGMT010"));
            System.out.println(checkIfStudentExist("amy.ng.2009"));
            System.out.println(checkIfCourseSectionExist("IS101", "S3"));
            System.out.println(checkIfSectionLimitReached("ben.ng.2009"));
            System.out.println(checkIfSuccessfulBid("amy.ng.2009", "IS100", "S1"));
            System.out.println(checkIfAmountValid(100.00));
            System.out.println(checkIfAmountValid(9.994));
            System.out.println(checkIfAmountValid(-10.000));
            System.out.println(checkIfAmountValid(09.99));
            System.out.println(checkIfAmountValid(10));
            System.out.println(checkIfBidExist("grace.ng.2009", "IS100"));
            System.out.println(checkIfDateValid("2011303"));
            System.out.println(checkIfSectionValid("IS100", "S1"));
            System.out.println(checkIfTimeValid("19:00"));
            //System.out.println(checkClashWithAllTimetable("amy.ng.2009", "IS10"));
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}