CREATE DATABASE visa;

\c visa;

CREATE TABLE Situation_familiale (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL
);

CREATE TABLE Nationalite (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL
);

CREATE TABLE Statut_demande (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL
);

CREATE TABLE Type_demande (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL
);

CREATE TABLE Categorie_visa (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL
);

CREATE TABLE Demandeur (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    date_naissance DATE NOT NULL,
    lieu_naissance VARCHAR(100) NOT NULL,
    telephone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    adresse TEXT NOT NULL,
    id_situation_familiale INT REFERENCES Situation_familiale(id),
    id_nationalite INT REFERENCES Nationalite(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Passeport (
    id SERIAL PRIMARY KEY,
    id_demandeur INT REFERENCES Demandeur(id),
    numero_passeport VARCHAR(50) NOT NULL,
    date_delivrance DATE NOT NULL,
    date_expiration DATE NOT NULL,
    pays_delivrance VARCHAR(100) NOT NULL
);

CREATE TABLE Visa_transformable (
    id SERIAL PRIMARY KEY,
    id_demandeur INT NOT NULL REFERENCES Demandeur(id),
    reference VARCHAR(50) NOT NULL,
    date_arrivee_madagascar DATE NOT NULL,
    lieu_entree_madagascar VARCHAR(100) NOT NULL,
    date_donnation DATE NOT NULL,
    date_expiration DATE NOT NULL
);

CREATE INDEX ix_visa_transformable_demandeur ON Visa_transformable(id_demandeur);

CREATE TABLE Visa (
    id SERIAL PRIMARY KEY,
    reference VARCHAR(50) NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    id_categorie_visa INT REFERENCES Categorie_visa(id),
    id_passeport INT REFERENCES Passeport(id)
);

CREATE TABLE Carte_resident (
    id SERIAL PRIMARY KEY,
    numero VARCHAR(20) NOT NULL UNIQUE,
    date_donnation DATE NOT NULL,
    date_expiration DATE NOT NULL
);

CREATE TABLE Demandeur_visa_carte_resident (
    id SERIAL PRIMARY KEY,
    id_demandeur INT NOT NULL REFERENCES Demandeur(id),
    id_visa INT NOT NULL REFERENCES Visa(id),
    id_carte_resident INT NOT NULL REFERENCES Carte_resident(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ux_demandeur_visa_carte_resident_visa UNIQUE (id_visa),
    CONSTRAINT ux_demandeur_visa_carte_resident_carte UNIQUE (id_carte_resident),
    CONSTRAINT ux_demandeur_visa_carte_resident_all UNIQUE (id_demandeur, id_visa, id_carte_resident)
);

CREATE INDEX ix_demandeur_visa_carte_resident_demandeur ON Demandeur_visa_carte_resident(id_demandeur);
CREATE INDEX ix_demandeur_visa_carte_resident_visa ON Demandeur_visa_carte_resident(id_visa);

CREATE TABLE Demande (
    id SERIAL PRIMARY KEY,
    date_demande DATE NOT NULL,
    id_statut INT REFERENCES Statut_demande(id),
    id_demandeur INT REFERENCES Demandeur(id),
    id_visa INT REFERENCES Visa(id),
    id_type_demande INT REFERENCES Type_demande(id),
    observations TEXT,
    date_traitement DATE,
    motif_rejet TEXT
);

CREATE TABLE Histo_statut_demande (
    id SERIAL PRIMARY KEY,
    id_demande INT REFERENCES Demande(id),
    id_statut INT REFERENCES Statut_demande(id),
    date_changement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    commentaire VARCHAR(255)
);

CREATE TABLE Dossiers (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL UNIQUE,
    obligatoire BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Dossier_type_visa (
    id SERIAL PRIMARY KEY,
    id_dossier INT NOT NULL REFERENCES Dossiers(id),
    id_type_visa INT NULL REFERENCES Type_demande(id)
);

CREATE UNIQUE INDEX ux_dossier_type_nonnull
ON Dossier_type_visa(id_dossier, id_type_visa)
WHERE id_type_visa IS NOT NULL;

CREATE UNIQUE INDEX ux_dossier_type_common
ON Dossier_type_visa(id_dossier)
WHERE id_type_visa IS NULL;

CREATE TABLE Demande_dossier (
    id SERIAL PRIMARY KEY,
    id_demande INT NOT NULL REFERENCES Demande(id),
    id_dossier INT NOT NULL REFERENCES Dossiers(id),
    est_fourni BOOLEAN NOT NULL DEFAULT FALSE,
    date_mise_a_jour TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    commentaire VARCHAR(255)
);

CREATE UNIQUE INDEX ux_demande_dossier_unique
ON Demande_dossier(id_demande, id_dossier);

CREATE INDEX ix_demande_dossier_demande ON Demande_dossier(id_demande);
CREATE INDEX ix_demande_dossier_dossier ON Demande_dossier(id_dossier);

CREATE TABLE Demande_dossier_scan (
    id SERIAL PRIMARY KEY,
    id_demande_dossier INT NOT NULL REFERENCES Demande_dossier(id),
    chemin_fichier_absolu VARCHAR(1000) NOT NULL,
    nom_fichier VARCHAR(255),
    type_mime VARCHAR(100),
    date_scan TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX ux_demande_dossier_scan_unique
ON Demande_dossier_scan(id_demande_dossier);
