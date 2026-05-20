-- changer le status de la demande en cree apres creation mais pas direct termine
-- ajouter sexe dans le formulaire de creation de demande
-- ajouter un bouton pour prendre la photo de la personne + recevoir la signature de la personne

CREATE TABLE IF NOT EXISTS Demandeur_photo_signature (
	id SERIAL PRIMARY KEY,
	id_demandeur INT NOT NULL UNIQUE REFERENCES Demandeur(id),
	photo TEXT,
	signature TEXT,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);



