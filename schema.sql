create table students(
	sid integer primary key,
	sname varchar2(30));
create table courses(
	cid integer primary key,
	cname varchar2(40),
	credits integer);
create table enrolled(
	sid integer,
	cid integer,
	primary key(sid,cid),
	foreign key(sid) references students,
	foreign key(cid) references courses);

insert into students values(1, 'Raman');

insert into courses values(11, 'Java', 4);
insert into courses values(22, 'AmericanHistory', 2);
insert into courses values(33, 'WebDev', 3);

insert into enrolled values(1,11);
insert into enrolled values(1,33);
