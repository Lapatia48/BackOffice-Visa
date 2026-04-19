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

CREATE TABLE Visa (
    id SERIAL PRIMARY KEY,
    reference VARCHAR(50) NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    id_categorie_visa INT REFERENCES Categorie_visa(id),
    id_passeport INT REFERENCES Passeport(id)
);

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
    statut INT REFERENCES Statut_demande(id),
    date_changement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    commentaire VARCHAR(255)
);
