## Sprint 2 : Duplicata — traitement des cas de perte et sans données antérieures.*
Une personne peut faire une demande de duplicata de carte de résident ou un transfert de visa en cas de perte. Les deux cas partagent le même formulaire, avec un choix au départ :
- Passeport perdu → Transfert de visa vers le nouveau passeport. L'information supplémentaire requise est uniquement le nouveau numéro de passeport.
- Carte de résident perdue → Demande de duplicata de carte de résident.

Dans les deux cas, si la personne est déjà dans le système, ses données sont pré-remplies.

Si la personne n'a aucune donnée antérieure dans le système, elle saisit toutes les informations from scratch, comme pour une nouvelle demande. Le statut sera directement Approuvée (et non "Document créé" comme au Sprint 1), car il s'agit d'une régularisation administrative.


etat civil -- ok
infos simples -- ok
infos sur passeport (dernier) --ok 
infos sur le dernier visa(dernier) -- ok
infos carte de resident : 
    Numero carte resident
    Reference visa
    Categorie
    Type demande
    Statut dossier
    Date donnation
    Date expiration
    Duree de validite
    Numero passeport
Bouton creer un duplicata