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
    (
        course varchar(32),
        prereq varchar(32),
        FOREIGN KEY course_fk(course)
        REFERENCES Course(subject, course_number),
        FOREIGN KEY prereq_fk(prereq)
        REFERENCES Course(subject, course_number)
    );
 
     
 CREATE TABLE Completed
    (
        STUID varchar(8),
        course varchar(32), 
        FOREIGN KEY student_fk(STUID)
        REFERENCES Student(STUID),
        FOREIGN KEY course_fk(course)
        REFERENCES Course(subject, course_number)
    );