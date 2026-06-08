INSERT IGNORE INTO vets VALUES (1, 0, 'James', 'Carter');
INSERT IGNORE INTO vets VALUES (2, 0, 'Helen', 'Leary');
INSERT IGNORE INTO vets VALUES (3, 0, 'Linda', 'Douglas');
INSERT IGNORE INTO vets VALUES (4, 0, 'Rafael', 'Ortega');
INSERT IGNORE INTO vets VALUES (5, 0, 'Henry', 'Stevens');
INSERT IGNORE INTO vets VALUES (6, 0, 'Sharon', 'Jenkins');

INSERT IGNORE INTO specialties VALUES (1, 0, 'radiology');
INSERT IGNORE INTO specialties VALUES (2, 0, 'surgery');
INSERT IGNORE INTO specialties VALUES (3, 0, 'dentistry');

INSERT IGNORE INTO vet_specialties VALUES (2, 1);
INSERT IGNORE INTO vet_specialties VALUES (3, 2);
INSERT IGNORE INTO vet_specialties VALUES (3, 3);
INSERT IGNORE INTO vet_specialties VALUES (4, 2);
INSERT IGNORE INTO vet_specialties VALUES (5, 1);

INSERT IGNORE INTO types VALUES (1, 0, 'cat');
INSERT IGNORE INTO types VALUES (2, 0, 'dog');
INSERT IGNORE INTO types VALUES (3, 0, 'lizard');
INSERT IGNORE INTO types VALUES (4, 0, 'snake');
INSERT IGNORE INTO types VALUES (5, 0, 'bird');
INSERT IGNORE INTO types VALUES (6, 0, 'hamster');

INSERT IGNORE INTO owners VALUES (1, 0, 'George', 'Franklin', '110 W. Liberty St.', 'Madison', '6085551023');
INSERT IGNORE INTO owners VALUES (2, 0, 'Betty', 'Davis', '638 Cardinal Ave.', 'Sun Prairie', '6085551749');
INSERT IGNORE INTO owners VALUES (3, 0, 'Eduardo', 'Rodriquez', '2693 Commerce St.', 'McFarland', '6085558763');
INSERT IGNORE INTO owners VALUES (4, 0, 'Harold', 'Davis', '563 Friendly St.', 'Windsor', '6085553198');
INSERT IGNORE INTO owners VALUES (5, 0, 'Peter', 'McTavish', '2387 S. Fair Way', 'Madison', '6085552765');
INSERT IGNORE INTO owners VALUES (6, 0, 'Jean', 'Coleman', '105 N. Lake St.', 'Monona', '6085552654');
INSERT IGNORE INTO owners VALUES (7, 0, 'Jeff', 'Black', '1450 Oak Blvd.', 'Monona', '6085555387');
INSERT IGNORE INTO owners VALUES (8, 0, 'Maria', 'Escobito', '345 Maple St.', 'Madison', '6085557683');
INSERT IGNORE INTO owners VALUES (9, 0, 'David', 'Schroeder', '2749 Blackhawk Trail', 'Madison', '6085559435');
INSERT IGNORE INTO owners VALUES (10, 0, 'Carlos', 'Estaban', '2335 Independence La.', 'Waunakee', '6085555487');

INSERT IGNORE INTO pets VALUES (1, 0, 'Leo', '2000-09-07', 1, 1);
INSERT IGNORE INTO pets VALUES (2, 0, 'Basil', '2002-08-06', 6, 2);
INSERT IGNORE INTO pets VALUES (3, 0, 'Rosy', '2001-04-17', 2, 3);
INSERT IGNORE INTO pets VALUES (4, 0, 'Jewel', '2000-03-07', 2, 3);
INSERT IGNORE INTO pets VALUES (5, 0, 'Iggy', '2000-11-30', 3, 4);
INSERT IGNORE INTO pets VALUES (6, 0, 'George', '2000-01-20', 4, 5);
INSERT IGNORE INTO pets VALUES (7, 0, 'Samantha', '1995-09-04', 1, 6);
INSERT IGNORE INTO pets VALUES (8, 0, 'Max', '1995-09-04', 1, 6);
INSERT IGNORE INTO pets VALUES (9, 0, 'Lucky', '1999-08-06', 5, 7);
INSERT IGNORE INTO pets VALUES (10, 0, 'Mulligan', '1997-02-24', 2, 8);
INSERT IGNORE INTO pets VALUES (11, 0, 'Freddy', '2000-03-09', 5, 9);
INSERT IGNORE INTO pets VALUES (12, 0, 'Lucky', '2000-06-24', 2, 10);
INSERT IGNORE INTO pets VALUES (13, 0, 'Sly', '2002-06-08', 1, 10);

INSERT IGNORE INTO visits VALUES (1, 0, 7, '2010-03-04', 'rabies shot');
INSERT IGNORE INTO visits VALUES (2, 0, 8, '2011-03-04', 'rabies shot');
INSERT IGNORE INTO visits VALUES (3, 0, 8, '2009-06-04', 'neutered');
INSERT IGNORE INTO visits VALUES (4, 0, 7, '2008-09-04', 'spayed');
