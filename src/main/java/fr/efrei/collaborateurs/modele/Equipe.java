package fr.efrei.collaborateurs.modele;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "equipe")
public class Equipe
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nom;

    protected Equipe()
    {
        // exigé par JPA
    }

    public Equipe(String nom)
    {
        
        setNom(nom);
    }

    private void setNom(String nom)
    {
        if (nom == null || nom.trim().isEmpty())
        {
            throw new IllegalArgumentException("Nom d'équipe invalide !");
        }
        this.nom = nom;
    }

    public long getId()
    {
        return id;
    }

    public String getNom()
    {
        return nom;
    }
    
    public void renommer(String nouveauNom){
        if (nouveauNom == null || nouveauNom.trim().isEmpty())
        {
            throw new IllegalArgumentException("Nom d'équipe invalide !");
        }
        this.nom = nouveauNom.trim();
    }
}
