
DROP TABLE PREREQUISITE;
DROP TABLE COREQUISITE;
DROP TABLE SUGGESTED;
DROP TABLE DESIRED;
DROP TABLE COMPLETED;
drop table currentcourse;

drop table desired;
drop table course;

drop table APPOINTMENT;
drop table student;
drop table USERTABLE;
drop table GROUPTABLE;
drop table AVAILABLEMAJOR;
drop table TOKENRESET;
drop table TOKENVERIFICATION;


create table STUDENT (
    STUID varchar(8),
    email varchar(255) not null unique,
    firstname varchar(60),
    lastname varchar(60),
    majorcode varchar(4),
    phone varchar(11),
    advised varchar(5),

    primary key (STUID)
);

create table USERTABLE (
    ID INT NOT NULL AUTO_INCREMENT,
    USERNAME varchar(255) not null unique,
    PASSWORD char(64), /* SHA-256 encryption */
    VERIFIED varchar(5),
    image blob,
    primary key (id)
);

create table GROUPTABLE (
    ID INT NOT NULL AUTO_INCREMENT,
    GROUPNAME varchar(255),
    USERNAME varchar(255),
    primary key (id)
);

create table AVAILABLEMAJOR (
    MAJORCODE varchar(4),
    primary key (MAJORCODE)
);

create table APPOINTMENT (
    ID INT NOT NULL AUTO_INCREMENT,
    SID varchar(8),
    stime time,
    sdate date,
    comment varchar(255),
    foreign key(SID) references student(stuid),
    primary key (ID)
);

create table TOKENRESET (
    TOKEN varchar(64),
    EMAIL varchar(255),
    EXPIRATION timestamp,
    primary key (TOKEN)
);

create table TOKENVERIFICATION (
    TOKEN varchar(64),
    EMAIL varchar(255),
    EXPIRATION timestamp,
    primary key (TOKEN)
);

/* username is root@uco.edu
    password is ppp
*/



insert into USERTABLE (username, password, verified)
    values ('root@uco.edu', 'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7', 'true');
insert into GROUPTABLE (groupname, USERNAME) values ('admingroup', 'root@uco.edu');
insert into GROUPTABLE (groupname, USERNAME) values ('customergroup', 'root@uco.edu');
insert into STUDENT(STUID, email, firstname, lastname, majorcode, phone, advised) values ('10000000', 'root@uco.edu', 'John', 'Doe', '6100', '4055551234', 'false');

insert into USERTABLE (username, password, verified)
    values ('student1@uco.edu', 'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7', 'true');
insert into GROUPTABLE (groupname, USERNAME) values ('customergroup', 'student1@uco.edu');
insert into STUDENT(STUID, email, firstname, lastname, majorcode, phone, advised) values ('10000001', 'student1@uco.edu', 'Greig', 'Caelinus', '6101', '4055550001', 'false');

insert into USERTABLE (username, password, verified)
    values ('student2@uco.edu', 'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7', 'true');
insert into GROUPTABLE (groupname, USERNAME) values ('customergroup', 'student2@uco.edu');
insert into STUDENT(STUID, email, firstname, lastname, majorcode, phone, advised) values ('10000002', 'student2@uco.edu', 'Emil', 'Prabhakar', '6102', '4055550002', 'false');

insert into USERTABLE (username, password, verified)
    values ('student3@uco.edu', 'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7', 'true');
insert into GROUPTABLE (groupname, USERNAME) values ('customergroup', 'student3@uco.edu');
insert into STUDENT(STUID, email, firstname, lastname, majorcode, phone, advised) values ('10000003', 'student3@uco.edu', 'Chidiebere', 'Juliusz', '6110', '4055550003', 'false');

insert into USERTABLE (username, password, verified)
    values ('abilby@uco.edu', 'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7', 'true');
insert into GROUPTABLE (groupname, USERNAME) values ('customergroup', 'abilby@uco.edu');
insert into STUDENT(STUID, email, firstname, lastname, majorcode, phone, advised) values ('10000004', 'abilby@uco.edu', 'Adam', 'Bilby', '6660', '4055550004', 'false');

insert into AVAILABLEMAJOR (majorcode) values ('6100');
insert into AVAILABLEMAJOR (majorcode) values ('6101');
insert into AVAILABLEMAJOR (majorcode) values ('6102');
insert into AVAILABLEMAJOR (majorcode) values ('6110');
insert into AVAILABLEMAJOR (majorcode) values ('6660');

insert into APPOINTMENT (SID, sdate, stime) values ('10000000', '2017-04-02', '12:00:00');
insert into APPOINTMENT (sdate, stime) values ('2017-04-03', '13:00:00');
insert into APPOINTMENT (sdate, stime) values ('2017-04-04', '14:00:00');
insert into APPOINTMENT (sdate, stime) values ('2017-04-05', '15:00:00');

/* 
Used to test email
*/
insert into USERTABLE (username, password, verified)
    values ('uco.student1@gmail.com', 'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7', 'false');
insert into GROUPTABLE (groupname, USERNAME) values ('customergroup', 'uco.student1@gmail.com');
insert into STUDENT(STUID, email, firstname, lastname, majorcode, phone, advised) values ('99999999', 'uco.student1@gmail.com', 'Rodolfo', 'Frank', '6100', '4050000000', 'false');

insert into APPOINTMENT (SID, sdate, stime) values ('99999999', '2017-04-10', '12:00:00');