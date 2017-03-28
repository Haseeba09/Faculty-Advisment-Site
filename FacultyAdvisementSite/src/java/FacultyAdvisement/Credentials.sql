drop table appointment;
drop table student;
drop table USERTABLE;
drop table GROUPTABLE;
drop table AVAILABLEMAJOR;


create table STUDENT(
    STUID varchar(8),
    email varchar(255) not null unique,
    majorcode varchar(4),
    phone varchar(11),
    primary key (STUID)
);

create table USERTABLE (
    ID INT NOT NULL AUTO_INCREMENT,
    USERNAME varchar(255) not null unique,
    PASSWORD char(64), /* SHA-256 encryption */
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
    stime datetime,
    foreign key(SID) references student(stuid),
    primary key (ID)
       
);

/* username is root@uco.edu
    password is ppp
*/



insert into USERTABLE (username, password)
    values ('root@uco.edu', 'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7');
insert into GROUPTABLE (groupname, USERNAME) values ('admingroup', 'root@uco.edu');
insert into GROUPTABLE (groupname, USERNAME) values ('customergroup', 'root@uco.edu');
insert into STUDENT(STUID, email, majorcode, phone) values ('00000000', 'root@uco.edu', '6100', '4055551234');

insert into USERTABLE (username, password)
    values ('student1@uco.edu', 'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7');
insert into GROUPTABLE (groupname, USERNAME) values ('customergroup', 'student1@uco.edu');
insert into STUDENT(STUID, email, majorcode, phone) values ('00000001', 'student1@uco.edu', '6101', '4055550001');

insert into USERTABLE (username, password)
    values ('student2@uco.edu', 'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7');
insert into GROUPTABLE (groupname, USERNAME) values ('customergroup', 'student2@uco.edu');
insert into STUDENT(STUID, email, majorcode, phone) values ('00000002', 'student2@uco.edu', '6102', '4055550002');

insert into USERTABLE (username, password)
    values ('student3@uco.edu', 'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7');
insert into GROUPTABLE (groupname, USERNAME) values ('customergroup', 'student3@uco.edu');
insert into STUDENT(STUID, email, majorcode, phone) values ('00000003', 'student3@uco.edu', '6110', '4055550003');

insert into USERTABLE (username, password)
    values ('student4@uco.edu', 'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7');
insert into GROUPTABLE (groupname, USERNAME) values ('customergroup', 'student4@uco.edu');
insert into STUDENT(STUID, email, majorcode, phone) values ('00000004', 'student4@uco.edu', '6660', '4055550004');

insert into AVAILABLEMAJOR (majorcode) values ('6100');
insert into AVAILABLEMAJOR (majorcode) values ('6101');
insert into AVAILABLEMAJOR (majorcode) values ('6102');
insert into AVAILABLEMAJOR (majorcode) values ('6110');
insert into AVAILABLEMAJOR (majorcode) values ('6660');

insert into APPOINTMENT (SID, stime) values ('00000001', '2017-03-28 16:30:00');