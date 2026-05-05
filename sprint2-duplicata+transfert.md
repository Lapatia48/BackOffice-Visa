## Sprint 2 : Duplicata et Transfert Visa (avec et sans donnees anterieures)

### Regles metier

1. Demande de type duplicata
- Cas avec donnees anterieures:
    Recherche sur la carte resident concernee du demandeur.
- Cas sans donnees anterieures:
    Nouvelle saisie complete (meme formulaire qu'un nouveau titre).
    Le systeme cree ensuite deux demandes:
    - une demande nouveau titre avec statut validee/acceptee
    - une demande duplicata avec statut cree, basee sur les memes donnees saisies

2. Demande de type transfert visa
- Cas avec donnees anterieures:
    Recherche sur le visa concerne du demandeur, puis transfert vers le passeport cible.
- Cas sans donnees anterieures:
    Nouvelle saisie complete (meme formulaire qu'un nouveau titre) avec passeport obligatoire
    et informations de visa transformable.
    Le systeme cree ensuite deux demandes:
    - une demande nouveau titre avec statut validee/acceptee
    - une demande transfert visa avec statut cree, basee sur les memes donnees saisies

### Ecrans / workflow

- Les pages restent separees par type (duplicata et transfert visa) mais suivent les memes etapes.
- Le cas sans donnees anterieures redirige vers la saisie complete.
- La derniere etape affiche:
    - informations de la demande (type, statut, date_demande)
    - pieces justificatives du type de demande concerne (duplicata ou transfert)
    - informations creees sur carte resident et visa (references generees automatiquement)
    - informations du nouveau titre cree (cas sans donnees anterieures)
    - informations du visa transformable saisi (cas sans donnees anterieures)

sprint 3 ok