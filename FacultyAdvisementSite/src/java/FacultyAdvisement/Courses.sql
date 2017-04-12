drop table completed;
drop table suggested;
drop table corequisite;
Drop Table Prerequisite;
drop table course;

CREATE TABLE Course 
    (
        Course_Name varchar(255), 
        Subject varchar(5), 
        Course_Number varchar(4), 
        Credits int(1),
        PRIMARY KEY (subject, course_number)
    );
CREATE TABLE Prerequisite
    (   course_subject varchar(5),
        course_number varchar(4),
        prereq_subject varchar(5), 
        prereq_number varchar(4),
        FOREIGN KEY course_fk(course_subject, course_number)
        REFERENCES Course(subject, course_number),
        FOREIGN KEY prereq_fk(prereq_subject, prereq_number)
        REFERENCES Course(subject, course_number)
        
    );
 
CREATE TABLE Corequisite
    (   course_subject varchar(5),
        course_number varchar(4),
        coreq_subject varchar(5), 
        coreq_number varchar(4),
        FOREIGN KEY course_fk(course_subject, course_number)
        REFERENCES Course(subject, course_number),
        FOREIGN KEY coreq_fk(coreq_subject, coreq_number)
        REFERENCES Course(subject, course_number)
        
    );
 
CREATE TABLE Suggested
    (   course_subject varchar(5),
        course_number varchar(4),
        suggested_subject varchar(5), 
        suggested_number varchar(4),
        FOREIGN KEY course_fk(course_subject, course_number)
        REFERENCES Course(subject, course_number),
        FOREIGN KEY suggested_fk(suggested_subject, suggested_number)
        REFERENCES Course(subject, course_number)
        
    );
    
 CREATE TABLE Completed
    (
        STUID varchar(8),
        subject varchar(5),
        course_number varchar(4),
        FOREIGN KEY student_fk(STUID)
        REFERENCES Student(STUID),
        FOREIGN KEY course_fk(subject, course_number)
        REFERENCES Course(subject, course_number)
    );

   
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Professional Computer Applications and Problem Solving', 'CMSC', '1053', '3');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Intro to Computing Systems', 'CMSC', '1103', '3');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Beginning Programming', 'CMSC', '1513', '3');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Beginning Programming Lab', 'CMSC', '1521', '1');
/*need to add concurrent enrollemnt reccomended for bp and bp lab*/
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Visual Programming', 'CMSC', '2413', '3');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('CMSC', '2413', 'CMSC', '1513');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('College Algebra', 'MATH', '1513', '3');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('College Algebra & Trigonometry', 'MATH', '1555', '5');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Programming I', 'CMSC', '1613', '3');
/*both needed? for prog 1 prereq*/
INSERT INTO  Corequisite (course_subject, Course_Number, coreq_subject, coreq_number)
        values ('CMSC', '1613', 'MATH', '1513');
INSERT INTO  Corequisite (course_subject, Course_Number, coreq_subject, coreq_number)
        values ('CMSC', '1613', 'MATH', '1555');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Programming I Lab', 'CMSC', '1621', '1');
/*need to add concurrent reccomended*/
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Computer Organization I', 'CMSC', '2833', '3');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('CMSC', '2833', 'CMSC', '1613');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Calculus I', 'MATH', '2313', '3');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('MATH', '2313', 'MATH', '1513');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Discrete Structures', 'CMSC', '2123', '3');
INSERT INTO  Corequisite (course_subject, Course_Number, coreq_subject, coreq_number)
        values ('CMSC', '2123', 'MATH', '2313');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Programming II', 'CMSC', '2613', '3');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Programming II Lab', 'CMSC', '2621', '1');
/*need to add concurrent reccomended*/
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Calculus II', 'MATH', '2323', '3');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('MATH', '2323', 'MATH', '2313');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Calculus III', 'MATH', '2333', '3');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('MATH', '2333', 'MATH', '2323');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Liner Algebra', 'MATH', '3143', '3');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('MATH', '3143', 'MATH', '2333');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Statistical Methods', 'STAT', '2103', '3');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('STAT', '2103', 'MATH', '1513');

/*need to figure out stats*/

INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Plane Trigonometry', 'MATH', '1593', '3');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('MATH', '2313', 'MATH', '1593');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('MATH', '3143', 'MATH', '2333');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Computer Organization II', 'CMSC', '3833', '3');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('CMSC', '3833', 'CMSC', '2833');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Enterprise Programming', 'CMSC', '3413', '3');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('CMSC', '3413', 'CMSC', '2613');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Object Oriented Software', 'SE', '3103', '3');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('SE', '3103', 'CMSC', '2613');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Software Engineering I', 'SE', '4283', '3');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('SE', '4283', 'CMSC', '2613');
INSERT INTO  Corequisite (course_subject, Course_Number, coreq_subject, coreq_number)
        values ('SE', '4283', 'MATH', '2313');
INSERT INTO  Corequisite (course_subject, Course_Number, coreq_subject, coreq_number)
        values ('SE', '4283', 'STAT', '2103');
INSERT INTO  Suggested (course_subject, Course_Number, suggested_subject, suggested_number)
        values ('SE', '4283', 'SE', '3103');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Applications Database MGMT', 'CMSC', '4003', '3');
INSERT INTO  Corequisite (course_subject, Course_Number, coreq_subject, coreq_number)
        values ('CMSC', '4003', 'MATH', '2313');
INSERT INTO  Corequisite (course_subject, Course_Number, coreq_subject, coreq_number)
        values ('CMSC', '4003', 'STAT', '2103');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('CMSC', '4003', 'CMSC', '2613');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Mobile Application Development', 'CMSC', '4303', '3');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('CMSC', '4303', 'SE', '3103');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Web Server Programming', 'CMSC', '4373', '3');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('CMSC', '4373', 'SE', '3103');
INSERT INTO  Suggested (course_subject, Course_Number, suggested_subject, suggested_number)
        values ('CMSC', '4373', 'CMSC', '4003');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Data Structures and Algorithms', 'CMSC', '3613', '3');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('CMSC', '3613', 'CMSC', '2123');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('CMSC', '3613', 'CMSC', '2613');
INSERT INTO  Corequisite (course_subject, Course_Number, coreq_subject, coreq_number)
        values ('CMSC', '3613', 'MATH', '2313');
INSERT INTO  Corequisite (course_subject, Course_Number, coreq_subject, coreq_number)
        values ('CMSC', '3613', 'STAT', '2103');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Seminar', 'CMSC', '4910', '0');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('CMSC', '4910', 'CMSC', '3613');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Artificial Intelligence', 'CMSC', '4133', '3');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('CMSC', '4133', 'CMSC', '3613');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Operating Systems', 'CMSC', '4153', '3');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('CMSC', '4153', 'CMSC', '3613');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Computer & Network Security', 'CMSC', '4323', '3');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('CMSC', '4323', 'CMSC', '3613');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Networks', 'CMSC', '4063', '3');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('CMSC', '4063', 'CMSC', '3613');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Robotics', 'CMSC', '4193', '3');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('CMSC', '4193', 'CMSC', '3613');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Ethics', 'CMSC', '4401', '1');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('CMSC', '4401', 'CMSC', '3613');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Programming Languages', 'CMSC', '4023', '3');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('CMSC', '4023', 'CMSC', '3613');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Translator Design', 'CMSC', '4173', '3');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('CMSC', '4173', 'CMSC', '3613');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Theory of Computing', 'CMSC', '4273', '3');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('CMSC', '4273', 'CMSC', '3613');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Software Engineering II', 'SE', '4423', '3');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('SE', '4423', 'SE', '4283');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Software Architecture & Design', 'SE', '4433', '3');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('SE', '4433', 'SE', '4283');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Senior Project', 'SE', '4513', '3');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('SE', '4513', 'SE', '4423');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('SE', '4513', 'SE', '4433');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('SE', '4513', 'CMSC', '4003');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Software Design & Development', 'CMSC', '4513', '3');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('CMSC', '4513', 'CMSC', '4003');
INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('CMSC', '4513', 'SE', '4283');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Independent Study', 'CMSC', '4930', '0');
INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Internship', 'CMSC', '4950', '0');
    




    INSERT INTO Completed (STUID, subject, course_number) 
        values ('10000001','CMSC', '1613');
