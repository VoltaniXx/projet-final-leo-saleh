package fr.efrei.collaborateurs;

import fr.efrei.collaborateurs.service.CollaborateurService;
import fr.efrei.collaborateurs.service.EquipeService;
import fr.efrei.collaborateurs.ui.ConsoleApp;

/** Point d'entrée : il assemble les morceaux, rien de plus. */
public class HelloEfrei {

    public static void main(String[] args) {
        CollaborateurService service = new CollaborateurService(JpaUtil.initEMFactory());
        EquipeService equipeService = new EquipeService(JpaUtil.initEMFactory());
        try {
            new ConsoleApp(service, equipeService).lancer();
        } finally {
            JpaUtil.fermerEMFactory();
        }
    }
}
