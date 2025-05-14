# Educo - Plateforme de Gestion Scolaire Complète



## Table des Matières
- [Introduction](#introduction)
- [Fonctionnalités](#fonctionnalités)
- [Technologies Utilisées](#technologies-utilisées)
- [Installation](#installation)
  - [Prérequis](#prérequis)
  - [Clonage du Projet](#clonage-du-projet)
  - [Configuration de la Base de Données](#configuration-de-la-base-de-données)
  - [Configuration des API](#configuration-des-api)
  - [Lancement de l'Application](#lancement-de-lapplication)
- [Structure du Projet](#structure-du-projet)
- [Captures d'Écran](#captures-décran)
- [Contributions](#contributions)
- [Licence](#licence)
- [Contact](#contact)

## Introduction

Educo est une plateforme de gestion scolaire complète développée en JavaFX avec une architecture MVC. Cette application permet la gestion centralisée de tous les aspects d'un établissement éducatif, depuis l'administration des utilisateurs jusqu'à la gestion académique et financière.

Avec son interface moderne et intuitive, Educo intègre des technologies avancées comme :
- Paiements en ligne via Stripe
- Notification en temps réel par SMS et email
- Chatbot intelligent avec Gemini/Llama
- Génération de rapports PDF et Excel
-Securitse l'application
## Fonctionnalités

### Gestion Administrative
- **Utilisateurs** : Gestion multi-rôles (Admin, Enseignants,  Parents)
- **Classes** : Organisation hiérarchique des classes/niveaux
- **Emplois du temps** : Planification visuelle des cours

### Gestion Académique
- **Cours & Matières** : Catalogue complet des disciplines
- **Examens & Quiz** : Création avec différents types de questions
- **Notes** : Bulletin numérique avec statistiques
- **Réclamations** : Système de tickets avec suivi

### Services Intégrés
- **Paiements** : Portail sécurisé avec Stripe
- **Communication** : Envoi groupé de SMS/emails
- **Calendrier** : Gestion des événements scolaires
- **Chatbot** : Assistance intelligente aux utilisateurs

## Technologies Utilisées

### Backend
- **Java 20** : Langage principal
- **JavaFX 17** : Interface graphique moderne
- **Maven** : Gestion des dépendances

### Base de Données
- **MySQL 8.0** : Stockage relationnel
- **XAMPP** : Environnement de développement

### API Externes
- **Stripe** : Paiements en ligne
- **Twilio** : Service de SMS
- **JavaMail** : Envoi d'emails
- **Google Gemini/Llama** : Intelligence Artificielle

### Bibliothèques Utilitaires
- **iTextPDF** : Génération de PDF
- **Apache POI** : Manipulation Excel
- **ZXing** : Génération de QR Codes
- **Lombok** : Réduction de code boilerplate

## Installation

### Prérequis

Avant de commencer, assurez-vous d'avoir installé :
1. Java JDK 20
2. MySQL Server (via XAMPP recommandé)
3. Maven 3.8+
4. Un client Git

### Clonage du Projet

```bash
git clone https://github.com/votre-utilisateur/pi_javafxmllll.git
cd pi_javafxmllll



