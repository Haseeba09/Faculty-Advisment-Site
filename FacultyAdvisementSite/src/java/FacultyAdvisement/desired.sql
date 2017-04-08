/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  abilb
 * Created: Apr 7, 2017
 */

DROP TABLE DESIRED;

CREATE TABLE Desired
    (   course_subject varchar(5),
        course_number varchar(4),
        ID INT,
        FOREIGN KEY course_fk(course_subject, course_number)
        REFERENCES Course(subject, course_number),
        FOREIGN KEY ID_fk(ID)
        REFERENCES APPOINTMENT(ID),
        PRIMARY KEY (course_subject, course_Number, id)
        
 );