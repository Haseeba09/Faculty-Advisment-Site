drop table student;
drop table USERTABLE;
drop table GROUPTABLE;

create table STUDENT(
    STUID varchar(8),
    email varchar(255) not null unique,
    majorcode varchar(4),
    phone varchar(11),
    primary key (STUID)
);

create table USERTABLE (
    ID INT NOT NULL AUTO_INCREMENT,
    USERNAME varchar(255),
    PASSWORD char(64), /* SHA-256 encryption */
    primary key (id)
);

create table GROUPTABLE (
    ID INT NOT NULL AUTO_INCREMENT,
    GROUPNAME varchar(255),
    USERNAME varchar(255),
    primary key (id)
);

/* username is root@uco.edu
    password is ppp
*/

insert into USERTABLE (username, password)
    values ('root@uco.edu',
        'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7');
insert into GROUPTABLE (groupname, USERNAME) values ('admingroup', 'root@uco.edu');
insert into GROUPTABLE (groupname, USERNAME) values ('customergroup', 'root@uco.edu');
insert into STUDENT(STUID, email, majorcode, phone) values ('00000000', 'root@uco.edu', '6100', '4055551234');