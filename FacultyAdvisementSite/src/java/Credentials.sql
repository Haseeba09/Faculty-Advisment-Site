drop table student;

create table STUDENT(
    STUID varchar(8),
    email varchar(255) not null unique,
    majorcode varchar(4),
    phone varchar(11),
    primary key (STUID)
);

create table USERTABLE (
    ID INT NOT NULL AUTO_INCREMENT,
    PASSWORD char(64), /* SHA-256 encryption */
    EMAIL varchar(8),
    primary key (id)
);

create table GROUPTABLE (
    ID INT NOT NULL AUTO_INCREMENT,
    GROUPNAME varchar(255),
    EMAIL varchar(255),
    primary key (id)
);