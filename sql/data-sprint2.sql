INSERT INTO Etat (libelle)
SELECT 'cree'
WHERE NOT EXISTS (
	SELECT 1 FROM Etat WHERE lower(libelle) = 'cree'
);

INSERT INTO Etat (libelle)
SELECT 'scanne'
WHERE NOT EXISTS (
	SELECT 1 FROM Etat WHERE lower(libelle) = 'scanne'
);

INSERT INTO Etat (libelle)
SELECT 'approuve'
WHERE NOT EXISTS (
	SELECT 1 FROM Etat WHERE lower(libelle) = 'approuve'
);

INSERT INTO Etat (libelle)
SELECT 'rejete'
WHERE NOT EXISTS (
	SELECT 1 FROM Etat WHERE lower(libelle) = 'rejete'
);

-- Etats metier utilises pour la carte resident.
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


INSERT INTO Dossiers (libelle, obligatoire)
SELECT 'Declaration de perte de la carte de resident', TRUE
WHERE NOT EXISTS (
	SELECT 1 FROM Dossiers WHERE libelle = 'Declaration de perte de la carte de resident'
);

INSERT INTO Dossier_type_visa (id_dossier, id_type_visa)
SELECT d.id, td.id
FROM Dossiers d
JOIN Type_demande td ON lower(td.libelle) = 'duplicata'
WHERE d.libelle = 'Declaration de perte de la carte de resident'
AND NOT EXISTS (
	SELECT 1 FROM Dossier_type_visa dtv
	WHERE dtv.id_dossier = d.id
	  AND dtv.id_type_visa = td.id
);