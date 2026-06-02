INSERT INTO vets VALUES (default, 0, 'James', 'Carter');
INSERT INTO vets VALUES (default, 0, 'Helen', 'Leary');
INSERT INTO vets VALUES (default, 0, 'Linda', 'Douglas');
INSERT INTO vets VALUES (default, 0, 'Rafael', 'Ortega');
INSERT INTO vets VALUES (default, 0, 'Henry', 'Stevens');
INSERT INTO vets VALUES (default, 0, 'Sharon', 'Jenkins');

INSERT INTO specialties VALUES (default, 0, 'radiology');
INSERT INTO specialties VALUES (default, 0, 'surgery');
INSERT INTO specialties VALUES (default, 0, 'dentistry');

INSERT INTO vet_specialties VALUES (2, 1);
INSERT INTO vet_specialties VALUES (3, 2);
INSERT INTO vet_specialties VALUES (3, 3);
INSERT INTO vet_specialties VALUES (4, 2);
INSERT INTO vet_specialties VALUES (5, 1);

INSERT INTO types VALUES (default, 0, 'cat');
INSERT INTO types VALUES (default, 0, 'dog');
INSERT INTO types VALUES (default, 0, 'lizard');
INSERT INTO types VALUES (default, 0, 'snake');
INSERT INTO types VALUES (default, 0, 'bird');
INSERT INTO types VALUES (default, 0, 'hamster');

INSERT INTO owners VALUES (default, 0, 'George', 'Franklin', '110 W. Liberty St.', 'Madison', '6085551023');
INSERT INTO owners VALUES (default, 0, 'Betty', 'Davis', '638 Cardinal Ave.', 'Sun Prairie', '6085551749');
INSERT INTO owners VALUES (default, 0, 'Eduardo', 'Rodriquez', '2693 Commerce St.', 'McFarland', '6085558763');
INSERT INTO owners VALUES (default, 0, 'Harold', 'Davis', '563 Friendly St.', 'Windsor', '6085553198');
INSERT INTO owners VALUES (default, 0, 'Peter', 'McTavish', '2387 S. Fair Way', 'Madison', '6085552765');
INSERT INTO owners VALUES (default, 0, 'Jean', 'Coleman', '105 N. Lake St.', 'Monona', '6085552654');
INSERT INTO owners VALUES (default, 0, 'Jeff', 'Black', '1450 Oak Blvd.', 'Monona', '6085555387');
INSERT INTO owners VALUES (default, 0, 'Maria', 'Escobito', '345 Maple St.', 'Madison', '6085557683');
INSERT INTO owners VALUES (default, 0, 'David', 'Schroeder', '2749 Blackhawk Trail', 'Madison', '6085559435');
INSERT INTO owners VALUES (default, 0, 'Carlos', 'Estaban', '2335 Independence La.', 'Waunakee', '6085555487');

INSERT INTO pets VALUES (default, 0, 'Leo', '2010-09-07', 1, 1);
INSERT INTO pets VALUES (default, 0, 'Basil', '2012-08-06', 6, 2);
INSERT INTO pets VALUES (default, 0, 'Rosy', '2011-04-17', 2, 3);
INSERT INTO pets VALUES (default, 0, 'Jewel', '2010-03-07', 2, 3);
INSERT INTO pets VALUES (default, 0, 'Iggy', '2010-11-30', 3, 4);
INSERT INTO pets VALUES (default, 0, 'George', '2010-01-20', 4, 5);
INSERT INTO pets VALUES (default, 0, 'Samantha', '2012-09-04', 1, 6);
INSERT INTO pets VALUES (default, 0, 'Max', '2012-09-04', 1, 6);
INSERT INTO pets VALUES (default, 0, 'Lucky', '2011-08-06', 5, 7);
INSERT INTO pets VALUES (default, 0, 'Mulligan', '2007-02-24', 2, 8);
INSERT INTO pets VALUES (default, 0, 'Freddy', '2010-03-09', 5, 9);
INSERT INTO pets VALUES (default, 0, 'Lucky', '2010-06-24', 2, 10);
INSERT INTO pets VALUES (default, 0, 'Sly', '2012-06-08', 1, 10);

INSERT INTO visits VALUES (default, 0, 7, '2013-01-01', 'rabies shot');
INSERT INTO visits VALUES (default, 0, 8, '2013-01-02', 'rabies shot');
INSERT INTO visits VALUES (default, 0, 8, '2013-01-03', 'neutered');
INSERT INTO visits VALUES (default, 0, 7, '2013-01-04', 'spayed');
