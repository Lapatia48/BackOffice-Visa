INSERT INTO Etat (libelle)
SELECT 'nouveau titre'
WHERE NOT EXISTS (
	SELECT 1 FROM Etat WHERE lower(libelle) = 'nouveau titre'
);

INSERT INTO Etat (libelle)
SELECT 'duplicata'
WHERE NOT EXISTS (
	SELECT 1 FROM Etat WHERE lower(libelle) = 'duplicata'
);

UPDATE Categorie_visa set libelle='travailleur' where id=1;
UPDATE Categorie_visa set libelle='investisseur' where id=2;
DELETE from Categorie_visa where id=3;