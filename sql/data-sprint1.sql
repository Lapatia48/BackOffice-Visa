INSERT INTO Categorie_visa (libelle)
SELECT 'travailleur'
WHERE NOT EXISTS (
	SELECT 1 FROM Categorie_visa WHERE lower(libelle) = 'travailleur'
);

INSERT INTO Categorie_visa (libelle)
SELECT 'investisseur'
WHERE NOT EXISTS (
	SELECT 1 FROM Categorie_visa WHERE lower(libelle) = 'investisseur'
);

INSERT INTO Categorie_visa (libelle)
SELECT 'etudiant'
WHERE NOT EXISTS (
	SELECT 1 FROM Categorie_visa WHERE lower(libelle) = 'etudiant'
);

INSERT INTO Type_demande (libelle)
SELECT 'nouveau_titre'
WHERE NOT EXISTS (
	SELECT 1 FROM Type_demande WHERE lower(libelle) = 'nouveau_titre'
);

INSERT INTO Type_demande (libelle)
SELECT 'duplicata'
WHERE NOT EXISTS (
	SELECT 1 FROM Type_demande WHERE lower(libelle) = 'duplicata'
);

INSERT INTO Type_demande (libelle)
SELECT 'transfert'
WHERE NOT EXISTS (
	SELECT 1 FROM Type_demande WHERE lower(libelle) = 'transfert'
);

-- Compatibilite historique: ces valeurs restent necessaires pour le mapping de pieces par type technique existant.
INSERT INTO Type_demande (libelle)
SELECT 'investisseur'
WHERE NOT EXISTS (
	SELECT 1 FROM Type_demande WHERE lower(libelle) = 'investisseur'
);

INSERT INTO Type_demande (libelle)
SELECT 'travailleur'
WHERE NOT EXISTS (
	SELECT 1 FROM Type_demande WHERE lower(libelle) = 'travailleur'
);

INSERT INTO Statut_demande (libelle)
SELECT 'cree'
WHERE NOT EXISTS (
	SELECT 1 FROM Statut_demande WHERE lower(libelle) = 'cree'
);

INSERT INTO Statut_demande (libelle)
SELECT 'terminee'
WHERE NOT EXISTS (
	SELECT 1 FROM Statut_demande WHERE lower(libelle) = 'terminee'
);

INSERT INTO Statut_demande (libelle)
SELECT 'scanne'
WHERE NOT EXISTS (
	SELECT 1 FROM Statut_demande WHERE lower(libelle) = 'scanne'
);

INSERT INTO Statut_demande (libelle)
SELECT 'approuve'
WHERE NOT EXISTS (
	SELECT 1 FROM Statut_demande WHERE lower(libelle) = 'approuve'
);

INSERT INTO Statut_demande (libelle)
SELECT 'rejete'
WHERE NOT EXISTS (
	SELECT 1 FROM Statut_demande WHERE lower(libelle) = 'rejete'
);

INSERT INTO Situation_familiale (libelle)
SELECT 'celibataire'
WHERE NOT EXISTS (
	SELECT 1 FROM Situation_familiale WHERE lower(libelle) = 'celibataire'
);

INSERT INTO Situation_familiale (libelle)
SELECT 'marie'
WHERE NOT EXISTS (
	SELECT 1 FROM Situation_familiale WHERE lower(libelle) = 'marie'
);

INSERT INTO Situation_familiale (libelle)
SELECT 'divorce'
WHERE NOT EXISTS (
	SELECT 1 FROM Situation_familiale WHERE lower(libelle) = 'divorce'
);

INSERT INTO Nationalite (libelle)
SELECT 'malagasy'
WHERE NOT EXISTS (
	SELECT 1 FROM Nationalite WHERE lower(libelle) = 'malagasy'
);

INSERT INTO Nationalite (libelle)
SELECT 'francaise'
WHERE NOT EXISTS (
	SELECT 1 FROM Nationalite WHERE lower(libelle) = 'francaise'
);

INSERT INTO Nationalite (libelle)
SELECT 'indienne'
WHERE NOT EXISTS (
	SELECT 1 FROM Nationalite WHERE lower(libelle) = 'indienne'
);

INSERT INTO Dossiers (libelle, obligatoire)
SELECT '02 photos d''identite', TRUE
WHERE NOT EXISTS (SELECT 1 FROM Dossiers WHERE libelle = '02 photos d''identite');

INSERT INTO Dossiers (libelle, obligatoire)
SELECT 'Notice de renseignement', FALSE
WHERE NOT EXISTS (SELECT 1 FROM Dossiers WHERE libelle = 'Notice de renseignement');

INSERT INTO Dossiers (libelle, obligatoire)
SELECT 'Demande adressee a Mr le Ministere de l''Interieur et de la Decentralisation avec adresse e-mail et numero telephone portable', TRUE
WHERE NOT EXISTS (SELECT 1 FROM Dossiers WHERE libelle = 'Demande adressee a Mr le Ministere de l''Interieur et de la Decentralisation avec adresse e-mail et numero telephone portable');

INSERT INTO Dossiers (libelle, obligatoire)
SELECT 'Photocopie certifiee du visa en cours de validite', TRUE
WHERE NOT EXISTS (SELECT 1 FROM Dossiers WHERE libelle = 'Photocopie certifiee du visa en cours de validite');

INSERT INTO Dossiers (libelle, obligatoire)
SELECT 'Photocopie certifiee de la premiere page du passeport', TRUE
WHERE NOT EXISTS (SELECT 1 FROM Dossiers WHERE libelle = 'Photocopie certifiee de la premiere page du passeport');

INSERT INTO Dossiers (libelle, obligatoire)
SELECT 'Photocopie certifiee de la carte resident en cours de validite', TRUE
WHERE NOT EXISTS (SELECT 1 FROM Dossiers WHERE libelle = 'Photocopie certifiee de la carte resident en cours de validite');

INSERT INTO Dossiers (libelle, obligatoire)
SELECT 'Certificat de residence a Madagascar', TRUE
WHERE NOT EXISTS (SELECT 1 FROM Dossiers WHERE libelle = 'Certificat de residence a Madagascar');

INSERT INTO Dossiers (libelle, obligatoire)
SELECT 'Extrait de casier judiciaire moins de 3 mois', FALSE
WHERE NOT EXISTS (SELECT 1 FROM Dossiers WHERE libelle = 'Extrait de casier judiciaire moins de 3 mois');

INSERT INTO Dossiers (libelle, obligatoire)
SELECT 'Statut de la Societe', TRUE
WHERE NOT EXISTS (SELECT 1 FROM Dossiers WHERE libelle = 'Statut de la Societe');

INSERT INTO Dossiers (libelle, obligatoire)
SELECT 'Extrait d''inscription au registre de commerce', TRUE
WHERE NOT EXISTS (SELECT 1 FROM Dossiers WHERE libelle = 'Extrait d''inscription au registre de commerce');

INSERT INTO Dossiers (libelle, obligatoire)
SELECT 'Carte fiscale', TRUE
WHERE NOT EXISTS (SELECT 1 FROM Dossiers WHERE libelle = 'Carte fiscale');

INSERT INTO Dossiers (libelle, obligatoire)
SELECT 'Autorisation emploi delivree a Madagascar par le Ministere de la Fonction publique', TRUE
WHERE NOT EXISTS (SELECT 1 FROM Dossiers WHERE libelle = 'Autorisation emploi delivree a Madagascar par le Ministere de la Fonction publique');

INSERT INTO Dossiers (libelle, obligatoire)
SELECT 'Attestation d''emploi delivre par l''employeur (Original)', TRUE
WHERE NOT EXISTS (SELECT 1 FROM Dossiers WHERE libelle = 'Attestation d''emploi delivre par l''employeur (Original)');

INSERT INTO Dossier_type_visa (id_dossier, id_type_visa)
SELECT d.id, NULL
FROM Dossiers d
WHERE d.libelle IN (
	'02 photos d''identite',
	'Notice de renseignement',
	'Demande adressee a Mr le Ministere de l''Interieur et de la Decentralisation avec adresse e-mail et numero telephone portable',
	'Photocopie certifiee du visa en cours de validite',
	'Photocopie certifiee de la premiere page du passeport',
	'Photocopie certifiee de la carte resident en cours de validite',
	'Certificat de residence a Madagascar',
	'Extrait de casier judiciaire moins de 3 mois'
)
AND NOT EXISTS (
	SELECT 1 FROM Dossier_type_visa dtv
	WHERE dtv.id_dossier = d.id
	  AND dtv.id_type_visa IS NULL
);

INSERT INTO Dossier_type_visa (id_dossier, id_type_visa)
SELECT d.id, td.id
FROM Dossiers d
JOIN Type_demande td ON lower(td.libelle) = 'investisseur'
WHERE d.libelle IN (
	'Statut de la Societe',
	'Extrait d''inscription au registre de commerce',
	'Carte fiscale'
)
AND NOT EXISTS (
	SELECT 1 FROM Dossier_type_visa dtv
	WHERE dtv.id_dossier = d.id
	  AND dtv.id_type_visa = td.id
);

INSERT INTO Dossier_type_visa (id_dossier, id_type_visa)
SELECT d.id, td.id
FROM Dossiers d
JOIN Type_demande td ON lower(td.libelle) = 'travailleur'
WHERE d.libelle IN (
	'Autorisation emploi delivree a Madagascar par le Ministere de la Fonction publique',
	'Attestation d''emploi delivre par l''employeur (Original)'
)
AND NOT EXISTS (
	SELECT 1 FROM Dossier_type_visa dtv
	WHERE dtv.id_dossier = d.id
	  AND dtv.id_type_visa = td.id
);

