package fr.efrei.collaborateurs.modele;

import jakarta.persistence.*;

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
}
