create database if not exists test;

# table tt
drop table if exists tt;
create table if not exists tt
(
  id int primary key auto_increment
);
insert into tt value (),(),();

# table teacher
drop table if exists teacher;
create table if not exists teacher
(
  name     varchar(10),
  password varchar(10)
);
insert into teacher value ('xyz', 'xyz');

# table student
drop table if exists student;
create table if not exists student
(
  name     varchar(10),
  password varchar(10)
);
insert into student value ('stu1', 'stu1');

# table picture
drop table if exists picture;
create table if not exists picture
(
  id      int primary key auto_increment,
  content mediumblob
);

# table user
drop table if exists user;
create table if not exists user
(
  id       int primary key auto_increment,
  name     varchar(10),
  password varchar(10),
  balance  int
);
insert into user value (null, 'a', 'aa', 1000), (null, 'b', 'bb', 1000);

# table customer
drop table if exists customer;
create table if not exists customer
(
  id int primary key auto_increment,
  name  varchar(20),
  birth date
);

#存储过程
drop procedure if exists pow2;
DELIMITER $
create procedure pow2(in in_a int, out out_a int, inout b int)
begin
  select pow(in_a, 2) into out_a;
  select pow(b, 2) into b;
end $
DELIMITER ;
set @in_a = 2;
set @out_a = 1;
set @b = 3;
call pow2(@a, @out_a, @b);
select @a, @out_a, @b;

#函数
# 以下变量可能影响函数的正常建立
# show variables like 'log_bin_trust_function_creators';
#set global log_bin_trust_function_creators=1;
drop function if exists pow3;
DELIMITER $
create function pow3(in_a int) returns int
begin
  declare out_a int default 0;
  select pow(in_a, 3) into out_a;
  return out_a;
end $
DELIMITER ;
select pow3(3);

#存储过程-批量插入
truncate customer;
drop procedure if exists batchInsert;
DELIMITER $
create procedure batchInsert(IN base_name varchar(20), in count int)
begin
  declare startTime varchar(30);
  declare endTime varchar(30);
  declare curDate varchar(12);
  declare n int default 1;

  set autocommit=0;
  select date_format(now(), '%y-%m-%d') into curDate;
  select now() into startTime;

  repeat
    insert into customer
    values (null, concat(base_name,n), curDate);
    # values(null,base_name,count);
    set n = n+ 1;
    until n > count
  end repeat;
  commit ;
  select now() into endTime;
  select startTime, endTime;
end $
DELIMITER ;
select 10000000 into @count;
call batchInsert('e', @count);

#========================
DROP TABLE IF EXISTS index_test;
CREATE TABLE index_test(
                         id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
                         USER VARCHAR(16) DEFAULT NULL,
                         psd varchar(64) default null
)ENGINE=MyISAM DEFAULT CHARSET=utf8;



DELIMITER $
drop function if EXISTS ret_pwd $
create function ret_pwd() RETURNS int(5)
BEGIN
  declare r int default 0;
  SET r = floor(10+rand()*1000);
  return r;
END $
DELIMITER ;

DELIMITER $
USE `test` $
DROP PROCEDURE IF EXISTS `insert_data` $
CREATE DEFINER=`root`@`localhost` PROCEDURE `insert_data`(IN num INT)
BEGIN
  DECLARE n INT DEFAULT 1;/*定义一个变量，存储当前执行的次数*/
  WHILE n <= num
  DO
  INSERT INTO index_test(USER,psd) VALUES(concat('user',n),md5(n));
  set n=n+1;
  end while;
END $
DELIMITER ;