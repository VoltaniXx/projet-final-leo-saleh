package fr.efrei.collaborateurs.ui;

import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.efrei.collaborateurs.exception.CollaborateurDejaExistantException;
import fr.efrei.collaborateurs.exception.CollaborateurIntrouvableException;
import fr.efrei.collaborateurs.modele.Adresse;
import fr.efrei.collaborateurs.modele.Collaborateur;
import fr.efrei.collaborateurs.modele.Equipe;
import fr.efrei.collaborateurs.modele.Programmeur;
import fr.efrei.collaborateurs.modele.Testeur;
import fr.efrei.collaborateurs.service.CollaborateurService;
import fr.efrei.collaborateurs.service.EquipeService;
import jakarta.persistence.PersistenceException;

/**
 * Seule classe qui dialogue avec l'utilisateur : ses println sont des affichages
 * utilisateur. Les informations techniques passent par le logger.
 */
public class ConsoleApp {

    private static final Logger logger = LoggerFactory.getLogger(ConsoleApp.class);

    private final CollaborateurService service;
    private final EquipeService equipeService;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleApp(CollaborateurService service, EquipeService equipeService) {
        this.service = service;
        this.equipeService = equipeService;
    }

    public void lancer() {
        boolean continuer = true;
        while (continuer) {
            afficherMenu();
            String choix = scanner.nextLine().trim();
            try {
                switch (choix) {
                    case "1" -> ajouterProgrammeur();
                    case "2" -> ajouterTesteur();
                    case "3" -> listerSalaireSuperieur();
                    case "4" -> rechercherParNom();
                    case "5" -> augmenter();
                    case "6" -> listerParLangage();
                    case "7" -> creerEquipe();
                    case "8" -> listerEquipe();
                    case "9" -> ajouterCollaborateurAEquipe();
                    case "10" -> retirerCollaborateurDuneEquipe();
                    case "11" -> listerCollaborateursDuneEquipe();
                    case "12" -> renommerEquipe();
                    case "13" -> supprimerEquipe();
                    case "14" -> afficherCollaborateursSansEquipe();
                    case "0" -> continuer = false;
                    default -> System.out.println("Choix inconnu.");
                }
            } catch (CollaborateurDejaExistantException e) {
                System.out.println("Ce identifiant est déjà pris (" + e.identifiant() + "). Choisissez-en un autre.");
            } catch (CollaborateurIntrouvableException e) {
                System.out.println("Aucun collaborateur n'a le identifiant " + e.identifiant() + ".");
            } catch (SaisieInvalideException | IllegalArgumentException e) {
                System.out.println("Saisie refusée : " + e.getMessage());
            } catch (PersistenceException e) {
                // Problème technique : détail dans les logs, message sobre pour l'utilisateur
                logger.error("Erreur d'accès à la base de données", e);
                System.out.println("Un problème technique est survenu, l'opération n'a pas été enregistrée.");
            }
        }
        System.out.println("Au revoir !");
    }

    private void afficherMenu() {
        System.out.println("===== GESTION DES COLLABORATEURS =====");
        System.out.println("1. Ajouter un programmeur");
        System.out.println("2. Ajouter un testeur");
        System.out.println("3. Salaire supérieur à ...");
        System.out.println("4. Rechercher par nom");
        System.out.println("5. Augmenter un salaire");
        System.out.println("6. Programmeurs par langage");
        System.out.println();
        System.out.println("===== GESTION DES EQUIPES =====");
        System.out.println("7. Créer une équipe");
        System.out.println("8. Lister les équipes existantes");
        System.out.println("9. Ajouter un collaborateur à une équipe");
        System.out.println("10. Retirer un collaborateur d'une équipe");
        System.out.println("11. Lister les collaborateurs d'une équipe");
        System.out.println("12. Renommer une équipe");
        System.out.println("13. Supprimer une équipe");
        System.out.println("14. Afficher les collaborateurs sans équipe");
        System.out.println("0. Quitter");
        System.out.print("Votre choix : ");

    }

    private void creerEquipe() {
        String nom = lireTexte("Nom de l'équipe");
        equipeService.creerEquipe(nom);
        System.out.println("Équipe enregistrée.");
    }


    public void listerEquipe(){
        afficherEquipe(equipeService.listerEquipes());
    }

    
    public void ajouterCollaborateurAEquipe(){
        long identifiant = Long.parseLong(lireTexte("Identifiant du collaborateur"));
        long equipeId = Long.parseLong(lireTexte("ID de l'équipe"));
        equipeService.affecterCollaborateurAEquipe(identifiant, equipeId);
        System.out.println("Collaborateur ajouté à l'équipe.");
    }

    public void retirerCollaborateurDuneEquipe(){
        long identifiant = Long.parseLong(lireTexte("Identifiant du collaborateur"));
        long equipeId = Long.parseLong(lireTexte("ID de l'équipe"));
        equipeService.retirerCollaborateurDEquipe(identifiant, equipeId);
        System.out.println("Collaborateur retiré de l'équipe.");
    }

    public void listerCollaborateursDuneEquipe(){
        long equipeId = Long.parseLong(lireTexte("ID de l'équipe"));
        List<Collaborateur> collaborateurs = equipeService.listerCollaborateursParEquipe(equipeId);
        afficher(collaborateurs);
    }

    public void renommerEquipe(){
        long equipeId = Long.parseLong(lireTexte("ID de l'équipe"));
        String nouveauNom = lireTexte("Nouveau nom de l'équipe");
        equipeService.renommerEquipe(equipeId, nouveauNom);
        System.out.println("Équipe renommée.");
    }

    public void supprimerEquipe(){
        long equipeId = Long.parseLong(lireTexte("ID de l'équipe"));
        equipeService.supprimerEquipe(equipeId);
        System.out.println("Équipe supprimée.");
    }

    public void afficherCollaborateursSansEquipe(){
        List<Collaborateur> collaborateurs = equipeService.listerCollaborateursSansEquipe();
        afficher(collaborateurs);
    }





    private void ajouterProgrammeur() {
        String identifiant = lireTexte("Identifiant");
        String prenom = lireTexte("Prénom");
        String nom = lireTexte("Nom");
        double salaire = lireDecimal("Salaire");
        String langage = lireTexte("Langage préféré");
        service.ajouter(new Programmeur(identifiant, prenom, nom, salaire, lireAdresse(), langage));
        System.out.println("Programmeur enregistré.");
    }


    private void ajouterTesteur() {
        String identifiant = lireTexte("Identifiant");
        String prenom = lireTexte("Prénom");
        String nom = lireTexte("Nom");
        double salaire = lireDecimal("Salaire");
        service.ajouter(new Testeur(identifiant, prenom, nom, salaire, lireAdresse()));
        System.out.println("Testeur enregistré.");
    }

    private void listerSalaireSuperieur() {
        afficher(service.salaireSuperieurA(lireDecimal("Seuil")));
    }

    private void rechercherParNom() {
        afficher(service.rechercherParNom(lireTexte("Fragment du nom")));
    }

    private void augmenter() {
        String identifiant = lireTexte("Identifiant");
        double pourcentage = lireDecimal("Pourcentage d'augmentation (10 = +10 %)");
        double nouveau = service.augmenterSalaire(identifiant, pourcentage);
        System.out.printf("Nouveau salaire : %.2f EUR%n", nouveau);
    }

    private void listerParLangage() {
        afficher(service.programmeursParLangage(lireTexte("Langage")));
    }

    private Adresse lireAdresse() {
        return new Adresse(lireTexte("Rue"), lireTexte("Code postal"), lireTexte("Ville"), lireTexte("Pays"));
    }

    private void afficher(List<? extends Collaborateur> liste) {
        if (liste.isEmpty()) {
            System.out.println("Aucun résultat.");
        }
        liste.forEach(System.out::println);
    }

    private void afficherEquipe(List<? extends Equipe> liste) {
        if (liste.isEmpty()) {
            System.out.println("Aucun résultat.");
        }
        liste.forEach(System.out::println);
    }

    // ---- Lecture : une erreur de saisie n'est pas une règle métier ----

    private String lireTexte(String libelle) {
        System.out.print(libelle + " : ");
        String valeur = scanner.nextLine().trim();
        if (valeur.isEmpty()) {
            throw new SaisieInvalideException(libelle + " ne peut pas être vide");
        }
        return valeur;
    }

    private double lireDecimal(String libelle) {
        String texte = lireTexte(libelle);
        try {
            return Double.parseDouble(texte.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new SaisieInvalideException("\"" + texte + "\" n'est pas un nombre");
        }
    }

    private static class SaisieInvalideException extends RuntimeException {
        SaisieInvalideException(String message) {
            super(message);
        }
    }
}
