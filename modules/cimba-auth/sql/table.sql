
create database cimba default CHARACTER SET utf8;

create table users(
  id int auto_increment,
  username varchar(32) not null,
  password varchar(128) not null,
  email varchar(32) not null,
  lastpasswordresetdate DATETIME not null,
  PRIMARY KEY (id)
);


create table user_roles(
  id int auto_increment,
  userid int not null,   /*对应于users表的id*/
  role varchar(32) not null,
  PRIMARY KEY (id)
);

create table user_param(
  id int auto_increment,
  userid int not null,   /*对应于users表的id*/
  param_name varchar(32) not null,
  param_desc varchar(128),
  param_value varchar(32) null,
  PRIMARY KEY (id)
);
insert into user_param(userid,param_name,param_desc,param_value) values(1,"bwflag","blacklist or whitelist","0");

create table blacklist(
  id int auto_increment,
  userid int not null,   /*对应于users表的id*/
  domain varchar(64) not null,
  PRIMARY KEY (id)
);

create table whitelist(
  id int auto_increment,
  userid int not null,   /*对应于users表的id*/
  domain varchar(64) not null,
  PRIMARY KEY (id)
);
insert into whitelist(userid,domain) values(1,"bing.com");
insert into whitelist(userid,domain) values(1,"sina.com");
insert into whitelist(userid,domain) values(1,"sina.com.cn");
insert into whitelist(userid,domain) values(1,"qq.com");

/**
获得结果后用一下语句查看异常记录

select * from userinfo where custname="" and accstatus<>1;


select umid,accstatus from userinfo where needclear=-1;
select umid,accstatus from userinfo where needclear=0;

select umid,accstatus,amount,needclear,custname,custcertname,custcerid,jbrname,jbridnum,custphone from userinfo limit 10;


select umid,accstatus from userinfo where custname="";
 */
