package fr.efrei.collaborateurs.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.efrei.collaborateurs.modele.Collaborateur;
import fr.efrei.collaborateurs.modele.Equipe;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

/**
 * Gestion des équipes, des affectations et des règles associées.
 */
public class EquipeService {

    private static final Logger logger = LoggerFactory.getLogger(EquipeService.class);
    private final EntityManagerFactory eMFactory;

    public EquipeService(EntityManagerFactory emf) {
        this.eMFactory = emf;
    }

    // 1. Créer une équipe
    public void creerEquipe(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'équipe ne peut pas être vide.");
        }

        EntityManager em = this.eMFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Long count = em.createQuery("SELECT COUNT(e) FROM Equipe e WHERE LOWER(e.nom) = LOWER(:nom)", Long.class)
                    .setParameter("nom", nom.trim())
                    .getSingleResult();
            if (count > 0) {
                throw new IllegalStateException("Une équipe avec ce nom existe déjà.");
            }

            Equipe equipe = new Equipe(nom.trim());
            em.persist(equipe);
            tx.commit();
            logger.info("Équipe '{}' créée avec succès.", nom.trim());
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // 2. Lister les équipes
    public List<Equipe> listerEquipes() {
        EntityManager em = this.eMFactory.createEntityManager();
        try {
            return em.createQuery("SELECT e FROM Equipe e", Equipe.class).getResultList();
        } finally {
            em.close();
        }
    }

    // 3. Affecter un collaborateur à une équipe
    public void affecterCollaborateurAEquipe(Long collaborateurId, Long equipeId) {
        EntityManager em = this.eMFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Collaborateur collab = em.find(Collaborateur.class, collaborateurId);
            Equipe equipe = em.find(Equipe.class, equipeId);

            if (collab == null || equipe == null) {
                throw new IllegalArgumentException("Collaborateur ou équipe introuvable.");
            }
            if (collab.getEquipe() != null) {
                throw new IllegalStateException("Ce collaborateur appartient déjà à une équipe.");
            }

            collab.setEquipe(equipe);
            tx.commit();
            logger.info("Collaborateur {} affecté à l'équipe {}", collaborateurId, equipeId);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // 4. Retirer un collaborateur d'une équipe
    public void retirerCollaborateurDEquipe(Long collaborateurId, Long equipeId) {
        EntityManager em = this.eMFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Collaborateur collab = em.find(Collaborateur.class, collaborateurId);

            if (collab == null || collab.getEquipe() == null || collab.getEquipe().getId() != equipeId) {
                throw new IllegalArgumentException("Le collaborateur n'appartient pas à cette équipe.");
            }

            collab.setEquipe(null);
            tx.commit();
            logger.info("Collaborateur {} retiré de l'équipe {}", collaborateurId, equipeId);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // 5. Afficher les collaborateurs sans équipe
    public List<Collaborateur> listerCollaborateursSansEquipe() {
        EntityManager em = this.eMFactory.createEntityManager();
        try {
            return em.createQuery("SELECT c FROM Collaborateur c WHERE c.equipe IS NULL", Collaborateur.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // 6. Renommer une équipe
    public void renommerEquipe(Long equipeId, String nouveauNom) {
        if (nouveauNom == null || nouveauNom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'équipe ne peut pas être vide.");
        }

        EntityManager em = this.eMFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Equipe equipe = em.find(Equipe.class, equipeId);
            if (equipe == null) {
                throw new IllegalArgumentException("Équipe introuvable.");
            }

            String nomNettoye = nouveauNom.trim();

            Long count = em.createQuery(
                            "SELECT COUNT(e) FROM Equipe e WHERE LOWER(e.nom) = LOWER(:nom) AND e.id != :id", Long.class)
                    .setParameter("nom", nomNettoye)
                    .setParameter("id", equipeId)
                    .getSingleResult();

            if (count > 0) {
                throw new IllegalStateException("Une autre équipe porte déjà ce nom.");
            }

            equipe.renommer(nomNettoye); // ou equipe.renommer(nomNettoye) selon votre méthode dans Equipe
            tx.commit();
            logger.info("Équipe {} renommée en '{}'", equipeId, nomNettoye);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // 7. Supprimer une équipe (seulement si elle est vide)
    public void supprimerEquipe(Long equipeId) {
        EntityManager em = this.eMFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Equipe equipe = em.find(Equipe.class, equipeId);
            if (equipe == null) {
                throw new IllegalArgumentException("Équipe introuvable.");
            }

            Long count = em.createQuery("SELECT COUNT(c) FROM Collaborateur c WHERE c.equipe.id = :equipeId", Long.class)
                    .setParameter("equipeId", equipeId)
                    .getSingleResult();

            if (count > 0) {
                throw new IllegalStateException("Impossible de supprimer une équipe qui contient encore des collaborateurs.");
            }
            em.remove(equipe);
            tx.commit();
            logger.info("Équipe {} supprimée.", equipeId);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

        // Afficher les collaborateurs d'une équipe
    public List<Collaborateur> listerCollaborateursParEquipe(Long equipeId) {
        EntityManager em = this.eMFactory.createEntityManager();
        try {
            // Vérifier d'abord si l'équipe existe
            Equipe equipe = em.find(Equipe.class, equipeId);
            if (equipe == null) {
                throw new IllegalArgumentException("Équipe introuvable.");
            }

            return em.createQuery("SELECT c FROM Collaborateur c WHERE c.equipe.id = :equipeId", Collaborateur.class)
                    .setParameter("equipeId", equipeId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}