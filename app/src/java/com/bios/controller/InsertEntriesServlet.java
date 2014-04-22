/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bios.controller;

import au.com.bytecode.opencsv.CSVReader;
import com.bios.dao.SectionStudentDAO;
import com.bios.dao.bidDAO;
import com.bios.dao.courseDAO;
import com.bios.dao.sectionDAO;
import com.bios.dao.studentDAO;
import com.bios.model.Bid;
import com.bios.model.Course;
import com.bios.model.Section;
import com.bios.model.Student;
import com.bios.util.ConnectionManager;
import com.google.gson.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import com.bios.util.*;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;

public class InsertEntriesServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        LinkedHashMap<String, Object> messageReturn = new LinkedHashMap<String, Object>();
        List<Object> recordLoaded = null;
        ArrayList<Object> errors = new ArrayList<Object>();
        try {
            /* TODO output your page here. You may use following sample code. */

            Connection con = null;
            Statement stmt = null;
            ResultSet rs = null;
            CSVReader reader = null;


            try {
                //Connect to Database
                con = ConnectionManager.getConnection();

                //Wipe Database
                purgeDatabase(con);

                // BOOTSTRAP INTO STUDENT TABLE WITH VALIDATION
                int studentInsertCount = bootstrapStudent(0, con, reader, errors);
                // BOOTSTRAP INTO  COURSE TABLE WITH VALIDATION
                int courseInsertCount = bootstrapCourse(0, con, reader, errors);
                // BOOTSTRAP INTO SECTION TABLE WITH VALIDATION
                int sectionInsertCount = bootstrapSection(0, con, reader, errors);
                // BOOTSTRAP INTO  PREREQUISITE TABLE WITH VALIDATION
                int prerequisiteInsertCount = bootstrapPrerequisite(0, con, reader, errors);
                // BOOTSTRAP INTO COURSE_COMPLETED TABLE WITH VALIDATION
                int courseCompltInsertCount = bootstrapCourseCompleted(0, con, reader, errors);
                // BOOTSTRAP INTO BIDS TABLE WITH VALIDATION
                int bidInsertCount = bootstrapBid(0, con, reader, errors);
                
                //sort the error messages according to alphabetical order
                ArrayList<Object> errorsUnsorted = errors;
                errors = new ArrayList<Object>();
                //sort for bid.csv
                for (int i=0; i<errorsUnsorted.size(); i++){
                    LinkedHashMap<String, Object> errorObj = (LinkedHashMap<String, Object>) errorsUnsorted.get(i);
                    String filename = (String) errorObj.get("file");
                    if (filename.equals("bid.csv")){
                        errors.add(errorObj);
                    }
                }
                //sort for course.csv
                for (int i=0; i<errorsUnsorted.size(); i++){
                    LinkedHashMap<String, Object> errorObj = (LinkedHashMap<String, Object>) errorsUnsorted.get(i);
                    String filename = (String) errorObj.get("file");
                    if (filename.equals("course.csv")){
                        errors.add(errorObj);
                    }
                }
                //sort for course_completed.csv
                for (int i=0; i<errorsUnsorted.size(); i++){
                    LinkedHashMap<String, Object> errorObj = (LinkedHashMap<String, Object>) errorsUnsorted.get(i);
                    String filename = (String) errorObj.get("file");
                    if (filename.equals("course_completed.csv")){
                        errors.add(errorObj);
                    }
                }
                //sort for prerequisite.csv
                for (int i=0; i<errorsUnsorted.size(); i++){
                    LinkedHashMap<String, Object> errorObj = (LinkedHashMap<String, Object>) errorsUnsorted.get(i);
                    String filename = (String) errorObj.get("file");
                    if (filename.equals("prerequisite.csv")){
                        errors.add(errorObj);
                    }
                }
                //sort for section.csv
                for (int i=0; i<errorsUnsorted.size(); i++){
                    LinkedHashMap<String, Object> errorObj = (LinkedHashMap<String, Object>) errorsUnsorted.get(i);
                    String filename = (String) errorObj.get("file");
                    if (filename.equals("section.csv")){
                        errors.add(errorObj);
                    }
                }
                //sort for student.csv
                for (int i=0; i<errorsUnsorted.size(); i++){
                    LinkedHashMap<String, Object> errorObj = (LinkedHashMap<String, Object>) errorsUnsorted.get(i);
                    String filename = (String) errorObj.get("file");
                    if (filename.equals("student.csv")){
                        errors.add(errorObj);
                    }
                }
                
                recordLoaded = (List) new ArrayList<JsonObject>();

                JsonObject bidCount = new JsonObject();
                bidCount.addProperty("bid.csv", bidInsertCount);
                JsonObject courseCount = new JsonObject();
                courseCount.addProperty("course.csv", courseInsertCount);
                JsonObject courseCompltCount = new JsonObject();
                courseCompltCount.addProperty("course_completed.csv", courseCompltInsertCount);
                JsonObject prerequisiteCount = new JsonObject();
                prerequisiteCount.addProperty("prerequisite.csv", prerequisiteInsertCount);
                JsonObject sectionCount = new JsonObject();
                sectionCount.addProperty("section.csv", sectionInsertCount);
                JsonObject studentCount = new JsonObject();
                studentCount.addProperty("student.csv", studentInsertCount);

                recordLoaded.add(bidCount);
                recordLoaded.add(courseCount);
                recordLoaded.add(courseCompltCount);
                recordLoaded.add(prerequisiteCount);
                recordLoaded.add(sectionCount);
                recordLoaded.add(studentCount);


            } catch (SQLException e) {
                out.println("SQLException" + e);
                messageReturn.put("status", "error");
                messageReturn.put("message", e.getMessage());
            } catch (IOException e) {
                out.println("IOException" + e);
                messageReturn.put("status", "error");
                messageReturn.put("message", e.getMessage());
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                        rs = null;
                    }
                    if (stmt != null) {
                        stmt.close();
                        stmt = null;
                    }
                    if (con != null) {
                        con.close();
                        con = null;
                    }
                } catch (SQLException e) {
                    out.println("SQLException " + e);
                    messageReturn.put("status", "error");
                }
            }

            if (errors.isEmpty()) {
                messageReturn.put("status", "success");
                messageReturn.put("num-record-loaded", recordLoaded);
            } else {
                messageReturn.put("status", "error");
                messageReturn.put("num-record-loaded", recordLoaded);
                messageReturn.put("error", errors);
            }
            request.setAttribute("status", messageReturn);
            //dispatch to uploadDone.jsp
            getServletContext().getRequestDispatcher("/uploadDone.jsp").forward(request, response);

//        } catch (Exception e){
//            out.println(e);
        } finally {
            out.close();
        }
    }

    // Here are all the refracted codes.
    private String getAbsolutePath(String relativePath) {
        String absolutePath = null;
        if (System.getenv("OPENSHIFT_DATA_DIR") == null) {
            absolutePath = getServletContext().getRealPath(relativePath);
        } else {
            String path = System.getenv("OPENSHIFT_DATA_DIR");
            absolutePath = path + relativePath;
        }
        return absolutePath;

    }

    private void purgeDatabase(Connection con) throws SQLException {
        String wipeSectionStudent = "delete from SECTION_STUDENT";
        String wipeSectionStudentRound2 = "delete from SECTION_STUDENT_ROUND2";
        String wipeBid = "delete from BID;";
        String wipeSection = "delete from SECTION;";
        String wipePrerequisite = "delete from PREREQUISITE;";
        String wipeCourseCompleted = "delete from COURSE_COMPLETED;";
        String wipeCourse = "delete from COURSE;";
        String wipeStudent = "delete from STUDENT;";
        PreparedStatement wipeBidStatement = con.prepareStatement(wipeBid);
        PreparedStatement wipeSectionStatement = con.prepareStatement(wipeSection);
        PreparedStatement wipePrerequisiteStatement = con.prepareStatement(wipePrerequisite);
        PreparedStatement wipeCourseCompletedStatement = con.prepareStatement(wipeCourseCompleted);
        PreparedStatement wipeCourseStatement = con.prepareStatement(wipeCourse);
        PreparedStatement wipeStudentStatement = con.prepareStatement(wipeStudent);
        PreparedStatement wipeSectionStudentStatement = con.prepareStatement(wipeSectionStudent);
        PreparedStatement wipeSectionStudentRound2Statement = con.prepareStatement(wipeSectionStudentRound2);
        wipeSectionStudentStatement.execute();
        wipeSectionStudentRound2Statement.execute();
        wipeBidStatement.execute();
        wipeSectionStatement.execute();
        wipePrerequisiteStatement.execute();
        wipeCourseCompletedStatement.execute();
        wipeCourseStatement.execute();
        wipeStudentStatement.execute();
    }

    private int bootstrapStudent(int studentInsertCount, Connection con,
            CSVReader reader, ArrayList<Object> errors) throws FileNotFoundException,
            IOException, SQLException {

        String relativeWebPath = "data/student.csv";
        int totalStudentRowCount = 1;
        String absoluteDiskPath = getAbsolutePath(relativeWebPath);
        reader = new CSVReader(new FileReader(absoluteDiskPath), ',', '"', '\'', 1);

        String update_student = "INSERT INTO STUDENT (userid,password,name,school,eDollar)"
                + " VALUES (?,?,?,?,?);";

        PreparedStatement preparedStatement = con.prepareStatement(update_student);

        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            // nextLine[] is an array of values from the line
            ArrayList<String> studentLineErrors = new ArrayList<String>();
            String userID = nextLine[0].trim();
            String password = nextLine[1].trim();
            String name = nextLine[2].trim();
            String school = nextLine[3].trim();
            double eDollar = 0;

            if (Validate.checkIfFieldBlank(userID)) {
                studentLineErrors.add("userid field is blank");
            }

            if (Validate.checkIfFieldBlank(password)) {
                studentLineErrors.add("password field is blank");
            }

            if (Validate.checkIfFieldBlank(name)) {
                studentLineErrors.add("name field is blank");
            }

            if (Validate.checkIfFieldBlank(school)) {
                studentLineErrors.add("school field is blank");
            }

            if (!nextLine[4].isEmpty()) {
                try {
                    eDollar = Double.parseDouble(nextLine[4].trim());
                } catch (Exception e) {
                    studentLineErrors.add("invalid e-dollar");
                }
            } else {
                studentLineErrors.add("edollar field is blank");
            }

            if (Validate.checkIfStudentExist(userID)) {
                studentLineErrors.add("duplicate userid");
            }

            if (eDollar < 0) {
                studentLineErrors.add("invalid e-dollar");
            }

            if (studentLineErrors.isEmpty()) {
                preparedStatement.setString(1, userID);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, name);
                preparedStatement.setString(4, school);
                preparedStatement.setDouble(5, eDollar);

                preparedStatement.execute();
                studentInsertCount++;
                totalStudentRowCount++;
            } else {
                LinkedHashMap<String, Object> errorObj = new LinkedHashMap<String, Object>();
                errorObj.put("file", "student.csv");
                errorObj.put("line", ++totalStudentRowCount);
                errorObj.put("message", studentLineErrors);
                errors.add(errorObj);
            }
        }
        return studentInsertCount;
    }

    private int bootstrapCourse(int courseInsertCount, Connection con,
            CSVReader reader, ArrayList<Object> errors) throws FileNotFoundException,
            IOException, SQLException {
        String[] nextLine;
        String relativeWebPath = "data/course.csv";
        int totalCourseRow = 1;
        String absoluteDiskPath = getAbsolutePath(relativeWebPath);
        reader = new CSVReader(new FileReader(absoluteDiskPath), ',', '"', 1);

        String update_course = "INSERT INTO COURSE (course,school,title,description,examdate,exam_start,exam_end)"
                + " VALUES (?,?,?,?,?,?,?);";

        PreparedStatement preparedStatement_2 = con.prepareStatement(update_course);

        while ((nextLine = reader.readNext()) != null) {
            ArrayList<String> courseErrors = new ArrayList<String>();
            // nextLine[] is an array of values from the line
            String course_course = nextLine[0].trim();
            String course_school = nextLine[1].trim();
            String course_title = nextLine[2].trim();
            String course_description = nextLine[3].trim();
            String course_examdate = nextLine[4].trim();
            String course_examstart = nextLine[5].trim();
            String course_examend = nextLine[6].trim();

            if (Validate.checkIfFieldBlank(course_course)) {
                courseErrors.add("course field is blank");
            }

            if (Validate.checkIfFieldBlank(course_school)) {
                courseErrors.add("school field is blank");
            }

            if (Validate.checkIfFieldBlank(course_title)) {
                courseErrors.add("title field is blank");
            }
            if (Validate.checkIfFieldBlank(course_description)) {
                courseErrors.add("description field is blank");
            }
            if (Validate.checkIfFieldBlank(course_examdate)) {
                courseErrors.add("exam date field is blank");
            }
            if (Validate.checkIfFieldBlank(course_examstart)) {
                courseErrors.add("exam start field is blank");
            }
            if (Validate.checkIfFieldBlank(course_examend)) {
                courseErrors.add("exam end field is blank");
            }
            if (!Validate.checkIfDateValid(course_examdate)) {
                courseErrors.add("invalid exam date");
            }
            if (!Validate.checkIfTimeValid(course_examstart)) {
                courseErrors.add("invalid exam start");
            }
            if (!Validate.checkIfTimeValid(course_examend)) {
                courseErrors.add("invalid exam end");
            }

            if (courseErrors.isEmpty()) {
                preparedStatement_2.setString(1, course_course);
                preparedStatement_2.setString(2, course_school);
                preparedStatement_2.setString(3, course_title);
                preparedStatement_2.setString(4, course_description);
                preparedStatement_2.setString(5, course_examdate);
                preparedStatement_2.setString(6, course_examstart);
                preparedStatement_2.setString(7, course_examend);

                preparedStatement_2.execute();
                courseInsertCount++;
                totalCourseRow++;
            } else {
                LinkedHashMap<String, Object> errorObj = new LinkedHashMap<String, Object>();
                errorObj.put("file", "course.csv");
                errorObj.put("line", ++totalCourseRow);
                errorObj.put("message", courseErrors);
                errors.add(errorObj);
            }
        }
        return courseInsertCount;
    }

    private int bootstrapSection(int sectionInsertCount, Connection con,
            CSVReader reader, ArrayList<Object> errors) throws FileNotFoundException,
            IOException, SQLException {

        String relativeWebPath = "data/section.csv";
        String[] nextLine;
        int totalSectionRow = 1;
        String absoluteDiskPath = getAbsolutePath(relativeWebPath);
        reader = new CSVReader(new FileReader(absoluteDiskPath), ',', '"', '\'', 1);

        String update_section = "INSERT INTO SECTION (course, section, day, start, end, instructor, venue, size)"
                + " VALUES (?,?,?,?,?,?,?,?);";

        PreparedStatement preparedStatement_3 = con.prepareStatement(update_section);


        while ((nextLine = reader.readNext()) != null) {
            ArrayList<String> sectionErrors = new ArrayList<String>();
            // nextLine[] is an array of values from the line
            String section_course = nextLine[0].trim();
            String section_section = nextLine[1].trim();
            String section_day = nextLine[2].trim();
            String section_start = nextLine[3].trim();
            String section_end = nextLine[4].trim();
            String section_instructor = nextLine[5].trim();
            String section_venue = nextLine[6].trim();
            String section_size = nextLine[7].trim();


            if (Validate.checkIfFieldBlank(section_course)) {
                sectionErrors.add("course field is blank");
            }

            if (Validate.checkIfFieldBlank(section_section)) {
                sectionErrors.add("section field is blank");
            }

            if (Validate.checkIfFieldBlank(section_day)) {
                sectionErrors.add("day field is blank");
            }

            if (Validate.checkIfFieldBlank(section_start)) {
                sectionErrors.add("start field is blank");
            }

            if (Validate.checkIfFieldBlank(section_end)) {
                sectionErrors.add("end field is blank");
            }

            if (Validate.checkIfFieldBlank(section_instructor)) {
                sectionErrors.add("instructor field is blank");
            }

            if (Validate.checkIfFieldBlank(section_venue)) {
                sectionErrors.add("venue field is blank");
            }

            if (Validate.checkIfFieldBlank(section_size)) {
                sectionErrors.add("size field is blank");
            }

            if (!Validate.checkIfValidCourse(section_course)) {
                sectionErrors.add("invalid course");
            } else {
                if (!Validate.checkIfSectionValid(section_course, section_section)) {
                    sectionErrors.add("invalid section");
                }
            }

            if (!section_day.isEmpty()) {
                try {
                    if (!Validate.checkIfDayValid(Integer.parseInt(section_day))) {
                        sectionErrors.add("invalid day");
                    }
                } catch (Exception e) {
                    sectionErrors.add("invalid day");
                }
            }
            if (!Validate.checkIfStartTimeValid(section_start)) {
                sectionErrors.add("invalid start");
            }
            if (!Validate.checkIfEndTimeValid(section_end)) {
                sectionErrors.add("invalid end");
            }
            
            if (Validate.checkIfStartTimeValid(section_start)
                    && Validate.checkIfEndTimeValid(section_end)) {
                try {
                    int startTime = Integer.parseInt(section_start.replace(":", ""));
                    int endTime = Integer.parseInt(section_end.replace(":", ""));
                    if (endTime < startTime) {
                        sectionErrors.add("invalid end");
                    }
                } catch (Exception e) {
                    sectionErrors.add("invalid start/end");
                }
            }
            
            try {
                if (Integer.parseInt(section_size) <= 0) {
                    sectionErrors.add("invalid size");
                }
            } catch (Exception e) {
                sectionErrors.add("invalid size");
            }

            if (sectionErrors.isEmpty()) {
                preparedStatement_3.setString(1, section_course);
                preparedStatement_3.setString(2, section_section);
                preparedStatement_3.setString(3, section_day);
                preparedStatement_3.setString(4, section_start);
                preparedStatement_3.setString(5, section_end);
                preparedStatement_3.setString(6, section_instructor);
                preparedStatement_3.setString(7, section_venue);
                preparedStatement_3.setString(8, section_size);
                preparedStatement_3.execute();
                sectionInsertCount++;
                totalSectionRow++;
            } else {
                LinkedHashMap<String, Object> errorObj = new LinkedHashMap<String, Object>();
                errorObj.put("file", "section.csv");
                errorObj.put("line", ++totalSectionRow);
                errorObj.put("message", sectionErrors);
                errors.add(errorObj);
            }
        }
        return sectionInsertCount;
    }

    private int bootstrapPrerequisite(int prerequisiteInsertCount, Connection con,
            CSVReader reader, ArrayList<Object> errors) throws FileNotFoundException,
            IOException, SQLException {

        String relativeWebPath = "data/prerequisite.csv";
        int totalPrerequisiteRow = 1;
        String[] nextLine;
        String absoluteDiskPath = getAbsolutePath(relativeWebPath);
        reader = new CSVReader(new FileReader(absoluteDiskPath), ',', '"', '\'', 1);

        String update_prerequisite = "INSERT INTO PREREQUISITE (course, prerequisite)"
                + " VALUES (?,?);";

        PreparedStatement preparedStatement_4 = con.prepareStatement(update_prerequisite);

        while ((nextLine = reader.readNext()) != null) {
            ArrayList<String> prerequisiteErrors = new ArrayList<String>();
            String prerequisite_course = nextLine[0].trim();
            String prerequisite_prerequisite = nextLine[1].trim();

            if (Validate.checkIfFieldBlank(prerequisite_course)) {
                prerequisiteErrors.add("course field is blank");
            }

            if (Validate.checkIfFieldBlank(prerequisite_prerequisite)) {
                prerequisiteErrors.add("prerequisite field is blank");
            }

            if (!Validate.checkIfValidCourse(prerequisite_course)) {
                prerequisiteErrors.add("invalid course");
            }
            if (!Validate.checkIfValidCourse(prerequisite_prerequisite)) {
                prerequisiteErrors.add("invalid prerequisite");
            }

            if (prerequisiteErrors.isEmpty()) {
                preparedStatement_4.setString(1, prerequisite_course);
                preparedStatement_4.setString(2, prerequisite_prerequisite);
                preparedStatement_4.execute();
                prerequisiteInsertCount++;
                totalPrerequisiteRow++;
            } else {
                LinkedHashMap<String, Object> errorObj = new LinkedHashMap<String, Object>();
                errorObj.put("file", "prerequisite.csv");
                errorObj.put("line", ++totalPrerequisiteRow);
                errorObj.put("message", prerequisiteErrors);
                errors.add(errorObj);
            }
        }

        return prerequisiteInsertCount;
    }

    private int bootstrapCourseCompleted(int courseCompltInsertCount, Connection con,
            CSVReader reader, ArrayList<Object> errors) throws FileNotFoundException,
            IOException, SQLException {

        String relativeWebPath = "data/course_completed.csv";
        String[] nextLine;
        int totalCCRow = 1;
        String absoluteDiskPath = getAbsolutePath(relativeWebPath);
        reader = new CSVReader(new FileReader(absoluteDiskPath), ',', '"', '\'', 1);

        String update_course_completed = "INSERT INTO COURSE_COMPLETED (userid, code)"
                + " VALUES (?,?);";

        PreparedStatement preparedStatement_5 = con.prepareStatement(update_course_completed);


        while ((nextLine = reader.readNext()) != null) {
            ArrayList<String> CCErrors = new ArrayList<String>();
            String courseComplt_userID = nextLine[0].trim();
            String courseComplt_course = nextLine[1].trim();

            if (Validate.checkIfFieldBlank(courseComplt_userID)) {
                CCErrors.add("userid field is blank");
            }

            if (Validate.checkIfFieldBlank(courseComplt_course)) {
                CCErrors.add("course field is blank");
            }

            if (!Validate.checkIfStudentExist(courseComplt_userID)) {
                CCErrors.add("invalid userid");
            }

            if (!Validate.checkIfValidCourse(courseComplt_course)) {
                CCErrors.add("invalid code");
            }
            if (!Validate.prerequisiteCheck(courseComplt_userID, courseComplt_course)) {
                CCErrors.add("invalid course completed");
            }

            if (CCErrors.isEmpty()) {

                preparedStatement_5.setString(1, courseComplt_userID);
                preparedStatement_5.setString(2, courseComplt_course);

                preparedStatement_5.execute();
                courseCompltInsertCount++;
                totalCCRow++;
            } else {
                LinkedHashMap<String, Object> errorObj = new LinkedHashMap<String, Object>();
                errorObj.put("file", "course_completed.csv");
                errorObj.put("line", ++totalCCRow);
                errorObj.put("message", CCErrors);
                errors.add(errorObj);
            }
        }

        return courseCompltInsertCount;
    }

    private int bootstrapBid(int bidInsertCount, Connection con, CSVReader reader,
            ArrayList<Object> errors) throws FileNotFoundException,
            IOException, SQLException {

        String relativeWebPath = "data/bid.csv";
        String absoluteDiskPath = getAbsolutePath(relativeWebPath);
        reader = new CSVReader(new FileReader(absoluteDiskPath), ',', '"', '\'', 1);
        String[] nextLine;
        int totalRowCount = 1;

        while ((nextLine = reader.readNext()) != null) {
            // nextLine[] is an array of values from the line
            ArrayList<String> bidLineErrors = new ArrayList<String>();
            String bid_userID = nextLine[0].trim();
            String bid_amount = nextLine[1].trim();
            String bid_code = nextLine[2].trim();
            String bid_section = nextLine[3].trim();
            double bid_amount_double = 0;
            // String userID, double amount, String code, String section

            boolean checkIfFieldBlank = false;
            if ((Validate.checkIfFieldBlank(bid_userID))) {
                bidLineErrors.add("userid is blank");
                checkIfFieldBlank = true;
            }

            if ((Validate.checkIfFieldBlank(bid_amount))) {
                bidLineErrors.add("amount is blank");
                checkIfFieldBlank = true;
            }

            if ((Validate.checkIfFieldBlank(bid_code))) {
                bidLineErrors.add("code is blank");
                checkIfFieldBlank = true;
            }

            if ((Validate.checkIfFieldBlank(bid_section))) {
                bidLineErrors.add("section is blank");
                checkIfFieldBlank = true;
            }
            if (!checkIfFieldBlank) {
                if (!(Validate.checkIfStudentExist(bid_userID))) {
                    bidLineErrors.add("invalid userid");
                }

                try {
                    bid_amount_double = Double.parseDouble(bid_amount);
                    if (!(Validate.checkIfAmountValid(bid_amount_double))) {
                        bidLineErrors.add("invalid amount");
                    }
                } catch (Exception e) {
                    bidLineErrors.add("invalid amount");
                }

                if (Validate.checkIfValidCourse(bid_code)) {

                    if (!(Validate.checkIfCourseSectionExist(bid_code, bid_section))) {
                        bidLineErrors.add("invalid section");
                    }
                } else {
                    bidLineErrors.add("invalid code");
                }
            }
// You edited this line;

            if (!Validate.checkIfBidExist(bid_userID, bid_code)) {
                Section thisSection = sectionDAO.retrieveCourseSection(bid_code, bid_section);
                Course thisCourse = courseDAO.retreiveCourseByCode(bid_code);
                List<Bid> userSuccessfulBids = SectionStudentDAO.retrieveBidsByStudent(bid_userID);
                List<Bid> userUnsucessfulBids = bidDAO.retrieveBidByStudent(bid_userID);
                List<Bid> pendingBids = bidDAO.retrieveBidByStudent(bid_userID);

                if (Validate.checkIfCompleted(bid_userID, bid_code)) {
                    bidLineErrors.add("already completed");
                }


                // Check for Class Timetable Clash
                for (Bid eachBid : userSuccessfulBids) {
                    String bid_sectionID = eachBid.getSection();
                    String bid_courseID = eachBid.getCode();
                    Section eachSection = sectionDAO.retrieveCourseSection(bid_courseID, bid_sectionID);
                    if ((Validate.checkOverlapClass(thisSection, eachSection))) {
                        bidLineErrors.add("class timetable clash");
                    }
                }

                for (Bid eachBid : userUnsucessfulBids) {
                    String bid_sectionID = eachBid.getSection();
                    String bid_courseID = eachBid.getCode();
                    Section eachSection = sectionDAO.retrieveCourseSection(bid_courseID, bid_sectionID);
                    if (eachSection != null) {
                        if ((Validate.checkOverlapClass(thisSection, eachSection))) {
                            bidLineErrors.add("class timetable clash");
                        }
                    }
                }
                // Check for Exam Timetable Clash                 
                for (Bid eachBid : userSuccessfulBids) {
                    String bid_courseID = eachBid.getCode();
                    Course eachCourse = courseDAO.retreiveCourseByCode(bid_courseID);
                    if ((Validate.checkOverlapExam(thisCourse, eachCourse))) {
                        bidLineErrors.add("exam timetable clash");
                    }
                }
                for (Bid eachBid : userUnsucessfulBids) {
                    String bid_courseID = eachBid.getCode();
                    Course eachCourse = courseDAO.retreiveCourseByCode(bid_courseID);

                    if (eachCourse != null) {
                        if ((Validate.checkOverlapExam(thisCourse, eachCourse))) {
                            bidLineErrors.add("exam timetable clash");
                        }
                    }
                }
            }
            if (bidLineErrors.isEmpty()) {

                if (!(Validate.prerequisiteCheck(bid_userID, bid_code))) {
                    bidLineErrors.add("incomplete prerequisites");
                }
                if (!Validate.checkIfBidExist(bid_userID, bid_code)) {
                    if (!Validate.checkIfEnoughMoney(bid_userID, bid_amount_double)) {
                        bidLineErrors.add("not enough e-dollar");
                    }
                } else {
                    Bid oldBid = bidDAO.retrieveSingleBid(bid_userID, bid_code);
                    Student student = studentDAO.retrieveStudentByUserid(bid_userID);
                    double oldAmt = oldBid.getAmount();
                    double balanceToCompare = student.getEDollars() + oldAmt;
                    if (balanceToCompare < bid_amount_double) {
                        bidLineErrors.add("not enough e-dollar");
                    }
                }

                if (!Validate.checkIfCourseFromStudentSchool(bid_userID, bid_code)) {
                    bidLineErrors.add("not own school course");
                }

                if (Validate.checkIfSectionLimitReached(bid_userID)) {
                    bidLineErrors.add("section limit reached");
                }

            }

            if (bidLineErrors.isEmpty()) {
                // If sudent bidded for sections in the same course,  
                // remove previous bid and insert new one

                if (Validate.checkIfBidExist(bid_userID, bid_code)) {
                    Bid pastBid = bidDAO.retrieveSingleBid(bid_userID, bid_code);
                    double pastBidAmount = 0;
                    // Takes in the past bid and refunds the amount before updating the amount;
                    if (pastBid != null) {
                        pastBidAmount = pastBid.getAmount();
                        studentDAO.refundMoney(bid_userID, pastBidAmount);
                    }

                    bidDAO.updateSingleBid(bid_userID, bid_amount_double, bid_code, bid_section);
                    studentDAO.deductAmt(bid_amount_double, bid_userID);
                    bidInsertCount++;
                    totalRowCount++;
                } else {
                    bidDAO.insertStudentBid(bid_userID, bid_amount_double, bid_code, bid_section);
                    studentDAO.deductAmt(bid_amount_double, bid_userID);
                    bidInsertCount++;
                    totalRowCount++;
                }
            } else {
                LinkedHashMap<String, Object> errorObj = new LinkedHashMap<String, Object>();
                errorObj.put("file", "bid.csv");
                errorObj.put("line", ++totalRowCount);
                errorObj.put("message", bidLineErrors);
                errors.add(errorObj);
            }
        }

        return bidInsertCount;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}