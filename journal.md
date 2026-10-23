# Journal — BEN ATIA Saleh & KALMAN Léo

## Partie A — Fiabiliser l’application

### 1. Commencez par casser votre application

#### À réfléchir : Une erreur de saisie et une règle métier violée sont-elles le même problème ? Où devrait-on les traiter ?

Une erreur de saisie est une erreur que l’utilisateur va entrer dans un mauvais format (ex. : âge = `"leo"`). Tandis qu’une erreur de règle métier est une erreur liée à une contrainte non respectée (ex. : un salaire négatif).

Les erreurs de saisie doivent être traitées lors de la saisie de données de l'utilisateur, tandis que les violations de règles métier doivent être traitées directement dans la logique de l’application.

```text
7. Trier par nom puis prénom
8. Afficher la fiche d'un collaborateur
9. Augmenter le salaire d'un collaborateur
0. Quitter
Votre choix : 9
Identifiant du collaborateur : C001
Pourcentage d'augmentation : -1
Salaire : 42000,00 € -> 42420,00 €
```

---

## Partie B — Alice et Alex refusent de disparaître

### Que voyez-vous dans la console et phpMyAdmin ?
Dans la console, on peut voir les requêtes SQL générées automatiquement par Hibernate, notamment celles qui servent à créer les tables. Dans phpMyAdmin, on retrouve les tables créées dans la base de données MySQL.

### Que se passe-t-il si vous arrêtez MySQL dans XAMPP puis relancez ?
Si on arrête MySQL puis qu'on relance le programme, une erreur apparaît car il n'arrive plus à se connecter à la base de données. Une exception est alors levée, par exemple une `PersistenceException`, accompagnée d'un message d'erreur dans la console.

### Quel message est destiné à l'utilisateur, lequel au développeur ?
La *stack trace* affichée dans la console est destinée au développeur, car elle contient des informations techniques qui permettent de comprendre l'origine de l'erreur. Pour l'utilisateur, il vaut mieux afficher un message plus simple, par exemple : *« Impossible de se connecter au serveur, veuillez réessayer plus tard »*. Pour cela, le développeur peut utiliser un bloc `try-catch` afin de gérer l'erreur et d'éviter d'afficher tous les détails techniques à l'utilisateur.

---

## Partie C — Prendre du recul

### Comparez l'entité générée à votre entité manuelle et repérez au moins trois décisions de l'outil.
Nous avons utilisé IntelliJ Ultimate pour générer automatiquement une entité Java à partir de la table `collaborateur_demo`.

Nous avons remarqué plusieurs choix effectués par IntelliJ :
* **La clé primaire :** IntelliJ a utilisé un `String` avec l'annotation `@Id`, sans `@GeneratedValue`. L'identifiant doit donc être fourni par l'application et n'est pas généré automatiquement.
* **Les types des attributs :** le salaire est représenté par un `Double`, tandis que les noms et le métier sont représentés par des `String`.
* **L'héritage :** IntelliJ a créé un simple attribut `metier` au lieu de générer les classes `Programmeur` et `Testeur` présentes dans notre modèle métier.
* **Les annotations :** IntelliJ a ajouté des annotations `@Column` pour préciser les noms des colonnes et certaines contraintes comme `nullable` et `length`.
* **Le nommage :** les noms des colonnes de la base sont adaptés aux conventions Java, par exemple `id_collaborateur` devient `idCollaborateur` et `langage_prefere` devient `langagePrefere`.

### Est-ce que je comprends ces décisions et est-ce que je souhaite les conserver ?
La génération automatique permet de gagner du temps, car IntelliJ crée directement une classe Java correspondant à la structure de la base de données.

Cependant, certaines décisions ne correspondent pas forcément à notre modèle métier, dont l'absence d'héritage entre les collaborateurs.

Donc il est mieux de vérifier et d'adapter le code généré plutôt que de l'utiliser directement. Nous avons conservé `CollaborateurDemo` dans un package séparé, sans l'ajouter à `persistence.xml`, afin de ne pas perturber le code de notre application.

---

## 11. Avant de quitter Alice et Alex

Ce TP nous a permis de comprendre plusieurs aspects du développement d'une application Java.

Nous avons utilisé Maven pour gérer les dépendances, les exceptions pour traiter les erreurs et les logs Hibernate pour observer les opérations SQL. La compilation Maven s'est terminée avec le message `BUILD SUCCESS`.

Avec JPA et Hibernate, nous avons pu enregistrer Alice dans MySQL, la retrouver grâce à `find()` et modifier son salaire avec une transaction. Nous avons également exécuté une recherche JPQL et observé la relation entre un collaborateur et son adresse.

Nous avons constaté qu'un identifiant fixe comme `C001` provoque une erreur de clé primaire si nous essayons d'insérer plusieurs fois le même collaborateur.

Nous avons aussi compris la différence entre un annuaire Java en mémoire et des données persistées dans MySQL. Le menu initial utilise les données de démonstration, tandis que le service JPA accède à la base de données.

Enfin, la génération automatique d'une entité avec IntelliJ nous a montré que le code généré doit être vérifié, notamment pour les types Java, les annotations et la représentation des règles métier.