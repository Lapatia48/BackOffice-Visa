# Sprint 1 - Nouveau titre

## Ce que fait ce projet
Ce projet est un back-office pour gerer des demandes de visa.
Pour ce sprint, la partie principale est le parcours **Nouveau titre**.

En pratique, l application permet de :
- creer une nouvelle demande avec les infos du demandeur et du passeport ;
- choisir les pieces a fournir (communes + selon le type de demande) ;
- voir les dossiers en cours ;
- ajouter plus tard les pieces manquantes ;
- voir les dossiers termines.

## Ce qui est deja en place (Sprint 1)
- Ecrans JSP fonctionnels : nouveau titre, dossiers en cours, dossiers termines, ajout de dossier.
- Navigation simple entre les pages.
- Filtrage cote ecran sur les listes (recherche rapide).
- Controle de base : les pieces obligatoires doivent etre cochees.
- Structure SQL + donnees de depart pour lancer le module.

## Comment continuer (reprise)
1. Verifier d abord que la base est bien creee avec `sql/script.sql` puis `sql/data-sprint1.sql`.
2. Tester le flux complet : creation -> en cours -> ajout de pieces -> terminee.
3. Ajouter des validations plus claires (messages utilisateur et controles de dates/champs).
4. Commencer les modules suivants (`duplicata`, `transfert-visa`) sur le meme modele.
5. Ajouter des tests automatiques pour securiser les prochaines evolutions.

## Resume en une phrase
Le sprint 1 pose une base solide pour gerer le cycle de vie d une demande de **nouveau titre** dans un back-office visa.


# Test sprint 1 termine