INSERT INTO vets VALUES (1, 0, 'James', 'Carter');
INSERT INTO vets VALUES (2, 0, 'Helen', 'Leary');
INSERT INTO vets VALUES (3, 0, 'Linda', 'Douglas');
INSERT INTO vets VALUES (4, 0, 'Rafael', 'Ortega');
INSERT INTO vets VALUES (5, 0, 'Henry', 'Stevens');
INSERT INTO vets VALUES (6, 0, 'Sharon', 'Jenkins');

INSERT INTO specialties VALUES (1, 0, 'radiology');
INSERT INTO specialties VALUES (2, 0, 'surgery');
INSERT INTO specialties VALUES (3, 0, 'dentistry');

INSERT INTO vet_specialties VALUES (2, 1);
INSERT INTO vet_specialties VALUES (3, 2);
INSERT INTO vet_specialties VALUES (3, 3);
INSERT INTO vet_specialties VALUES (4, 2);
INSERT INTO vet_specialties VALUES (5, 1);

INSERT INTO types VALUES (1, 0, 'cat');
INSERT INTO types VALUES (2, 0, 'dog');
INSERT INTO types VALUES (3, 0, 'lizard');
INSERT INTO types VALUES (4, 0, 'snake');
INSERT INTO types VALUES (5, 0, 'bird');
INSERT INTO types VALUES (6, 0, 'hamster');

INSERT INTO owners VALUES (1, 0, 'George', 'Franklin', '110 W. Liberty St.', 'Madison', '6085551023');
INSERT INTO owners VALUES (2, 0, 'Betty', 'Davis', '638 Cardinal Ave.', 'Sun Prairie', '6085551749');
INSERT INTO owners VALUES (3, 0, 'Eduardo', 'Rodriquez', '2693 Commerce St.', 'McFarland', '6085558763');
INSERT INTO owners VALUES (4, 0, 'Harold', 'Davis', '563 Friendly St.', 'Windsor', '6085553198');
INSERT INTO owners VALUES (5, 0, 'Peter', 'McTavish', '2387 S. Fair Way', 'Madison', '6085552765');
INSERT INTO owners VALUES (6, 0, 'Jean', 'Coleman', '105 N. Lake St.', 'Monona', '6085552654');
INSERT INTO owners VALUES (7, 0, 'Jeff', 'Black', '1450 Oak Blvd.', 'Monona', '6085555387');
INSERT INTO owners VALUES (8, 0, 'Maria', 'Escobito', '345 Maple St.', 'Madison', '6085557683');
INSERT INTO owners VALUES (9, 0, 'David', 'Schroeder', '2749 Blackhawk Trail', 'Madison', '6085559435');
INSERT INTO owners VALUES (10, 0, 'Carlos', 'Estaban', '2335 Independence La.', 'Waunakee', '6085555487');

INSERT INTO pets VALUES (1, 0, 'Leo', '2010-09-07', 1, 1);
INSERT INTO pets VALUES (2, 0, 'Basil', '2012-08-06', 6, 2);
INSERT INTO pets VALUES (3, 0, 'Rosy', '2011-04-17', 2, 3);
INSERT INTO pets VALUES (4, 0, 'Jewel', '2010-03-07', 2, 3);
INSERT INTO pets VALUES (5, 0, 'Iggy', '2010-11-30', 3, 4);
INSERT INTO pets VALUES (6, 0, 'George', '2010-01-20', 4, 5);
INSERT INTO pets VALUES (7, 0, 'Samantha', '2012-09-04', 1, 6);
INSERT INTO pets VALUES (8, 0, 'Max', '2012-09-04', 1, 6);
INSERT INTO pets VALUES (9, 0, 'Lucky', '2011-08-06', 5, 7);
INSERT INTO pets VALUES (10, 0, 'Mulligan', '2007-02-24', 2, 8);
INSERT INTO pets VALUES (11, 0, 'Freddy', '2010-03-09', 5, 9);
INSERT INTO pets VALUES (12, 0, 'Lucky', '2010-06-24', 2, 10);
INSERT INTO pets VALUES (13, 0, 'Sly', '2012-06-08', 1, 10);

INSERT INTO visits VALUES (1, 0, 7, '2013-01-01', 'rabies shot');
INSERT INTO visits VALUES (2, 0, 8, '2013-01-02', 'rabies shot');
INSERT INTO visits VALUES (3, 0, 8, '2013-01-03', 'neutered');
INSERT INTO visits VALUES (4, 0, 7, '2013-01-04', 'spayed');
