CREATE TABLE Episode (
id int PRIMARY KEY,
name varchar(50) NOT NULL,
season_id int NOT NULL,
FOREIGN KEY (season_id) references Season(id)
)

CREATE TABLE Season (
id int PRIMARY KEY,
name varchar(50) NOT NULL,
season_id int NOT NULL,
FOREIGN KEY (series_id) references Series(id)
)

CREATE TABLE Series (
id int PRIMARY KEY,
name varchar(50) NOT NULL,
awards varchar(100) NOT NULL,
awards_count int NOT Null
)

Insert into Series (id,name,awards)
values (1,'Game of Thrones','25 Nominations for gold award and 5 for Oscar',10)

Select * 
From series s1
join season s2 on s2.season_id = s1.id
where s2.name not like "Game" 

Select name
count(awards)
From series
group by name
