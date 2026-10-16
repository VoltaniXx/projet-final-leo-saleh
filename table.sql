
-- ============================================
-- EFREIForge - Projet final Java
-- MariaDB / MySQL - XAMPP
-- ============================================

SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS efreiforge
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE efreiforge;

-- ============================================
-- 1. TABLE EQUIPE
-- ============================================

CREATE TABLE IF NOT EXISTS equipe (
    id INT NOT NULL AUTO_INCREMENT,
    nom VARCHAR(255) NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT uq_equipe_nom UNIQUE (nom),
    CONSTRAINT chk_equipe_nom_non_vide
        CHECK (CHAR_LENGTH(TRIM(nom)) > 0)

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;


-- ============================================
-- 2. TABLE ADRESSE
-- ============================================

CREATE TABLE IF NOT EXISTS adresse (
    id INT NOT NULL AUTO_INCREMENT,
    rue VARCHAR(100) NOT NULL,
    code_postal VARCHAR(10) NOT NULL,
    ville VARCHAR(60) NOT NULL,
    pays VARCHAR(60) NOT NULL,

    PRIMARY KEY (id)

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;


-- ============================================
-- 3. TABLE COLLABORATEUR
-- Heritage JPA : SINGLE_TABLE
-- ============================================

CREATE TABLE IF NOT EXISTS collaborateur (
    identifiant VARCHAR(10) NOT NULL,
    metier VARCHAR(40) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    nom VARCHAR(50) NOT NULL,
    salaire DOUBLE NOT NULL,
    langage_prefere VARCHAR(40) NULL,

    adresse_id INT NOT NULL,
    equipe_id INT NULL,

    PRIMARY KEY (identifiant),

    INDEX idx_collaborateur_adresse (adresse_id),
    INDEX idx_collaborateur_equipe (equipe_id),

    CONSTRAINT fk_collaborateur_adresse
        FOREIGN KEY (adresse_id)
        REFERENCES adresse(id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT,

    CONSTRAINT fk_collaborateur_equipe
        FOREIGN KEY (equipe_id)
        REFERENCES equipe(id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;


-- ============================================
-- 4. INSERTION DES EQUIPES
-- ============================================

INSERT IGNORE INTO equipe (nom)
VALUES
    ('Backend'),
    ('Qualite');


-- ============================================
-- 5. INSERTION DES ADRESSES
-- ============================================

INSERT IGNORE INTO adresse
    (id, rue, code_postal, ville, pays)
VALUES
    (1, '12 rue des Lilas',
        '75011', 'Paris', 'France'),

    (2, '8 avenue Victor Hugo',
        '69006', 'Lyon', 'France'),

    (3, '25 rue de la Republique',
        '59000', 'Lille', 'France'),

    (4, '4 boulevard Saint-Michel',
        '75005', 'Paris', 'France'),

    (5, '19 rue Nationale',
        '13001', 'Marseille', 'France'),

    (6, '7 rue des Ecoles',
        '44000', 'Nantes', 'France');


-- ============================================
-- 6. INSERTION DES COLLABORATEURS
-- ============================================

INSERT IGNORE INTO collaborateur
    (identifiant, metier, prenom, nom,
     salaire, langage_prefere,
     adresse_id, equipe_id)
VALUES

    ('C001', 'Programmeur', 'Alice',
     'Martin', 42000, 'Java', 1,
     (SELECT id FROM equipe WHERE nom = 'Backend')),

    ('C002', 'Programmeur', 'Alex',
     'Dupont', 44500, 'Python', 2,
     (SELECT id FROM equipe WHERE nom = 'Backend')),

    ('C003', 'Testeur', 'Chloé',
     'Bernard', 39000, NULL, 3, NULL),

    ('C004', 'Programmeur', 'Nora',
     'Diallo', 47000, 'Java', 4,
     (SELECT id FROM equipe WHERE nom = 'Backend')),

    ('C005', 'Testeur', 'Hugo',
     'Petit', 41000, NULL, 5,
     (SELECT id FROM equipe WHERE nom = 'Qualite')),

    ('C006', 'Programmeur', 'Lina',
     'Moreau', 45500, 'Kotlin', 6, NULL);


-- ============================================
-- 7. VERIFICATION DES EQUIPES
-- ============================================

SELECT * FROM equipe;


-- ============================================
-- 8. VERIFICATION DES COLLABORATEURS
-- ============================================

SELECT
    c.identifiant,
    c.prenom,
    c.nom,
    c.metier,
    c.langage_prefere,
    a.rue,
    a.code_postal,
    a.ville,
    a.pays,
    e.nom AS equipe

FROM collaborateur c

JOIN adresse a
    ON c.adresse_id = a.id

LEFT JOIN equipe e
    ON c.equipe_id = e.id

ORDER BY c.identifiant;


-- ============================================
-- 9. COLLABORATEURS SANS EQUIPE
-- ============================================

SELECT
    identifiant,
    prenom,
    nom,
    metier

FROM collaborateur

WHERE equipe_id IS NULL

ORDER BY identifiant;
