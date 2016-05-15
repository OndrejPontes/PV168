INSERT INTO Agent (name, rating) VALUES ('Alex Rider', 1), ('James Adams', 2), 
('Ali Imran', 5), ('Matt Helm', 6), ('Hal Ambler', 2), ('Jane Blonde', 7), 
('Basil Argyros', 8), ('Modesty Blaise', 2), ('Drongo', 3), ('James Bond', 10), 
('Nancy Drew', 1), ('The Hardy Boys', 6), ('Sherlock Holmes', 7), ('Peter Pettigrew', 5);

INSERT INTO Mission (name, target, from_date, to_date) VALUES ('Save the queen', 'Queen', '2015-07-06', '2016-09-14');

INSERT INTO Manager (agent, mission) VALUES (2,1), (1,1), (3,1);
