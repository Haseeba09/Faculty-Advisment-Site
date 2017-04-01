DROP TABLE Course;
Drop Table Prerequisite;
Drop Table Completed;

CREATE TABLE Course 
    (
        Course_Name varchar(32), 
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
        values ('Programming I', 'CMSC', '1613', '3');

    INSERT INTO  Course (Course_Name, Subject, Course_Number, Credits)
        values ('Programming II', 'CMSC', '1621', '3');
    INSERT INTO  Prerequisite (course_subject, Course_Number, prereq_subject, prereq_number)
        values ('CMSC', '1621', 'CMSC', '1613');
    INSERT INTO Completed (STUID, subject, course_number) 
        values ('11111112','CMSC', '1613');