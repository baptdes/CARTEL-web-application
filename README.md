# 📚🎲 CARTEL - Application de Gestion de Bibliothèque & Ludothèque Étudiante

Bienvenue sur le projet **CARTEL**, l'application web officielle pour gérer le catalogue de la bibliothèque/ludothèque de la bibliothèque & ludothèque étudiante CARTEL. Pour les plus intimes, cette application se nomme :

C - Catalogue

A - Annuellement

R - Ressucité et

T - Téléconsultable d'

E - Elements

L - Ludiques

Cette application permet la gestion de livres et de jeux de société, la consultation du catalogue, ainsi que la suggestion de nouveaux achats par les utilisateurs.

---

## ✨ Fonctionnalités principales

### Catalogue (Livres & Jeux de Société)
- Ajouter, modifier et supprimer un livre ou un jeu de société.
- Rechercher un item par titre, auteur, éditeur ou code-barres (ISBN).
- Ajout rapide d'items par scan du code-barres.

### Factures
- Associer une facture (document PDF ou image) à chaque item du catalogue.

### Système de Suggestions ("Course")
- Les utilisateurs peuvent suggérer de nouveaux livres ou jeux à acheter.
- Les administrateurs peuvent consulter, accepter ou refuser les suggestions.
- Une section "Suggestions du mois" est affichée sur la page d'accueil.

### Administration
- Accès admin sécurisé par mot de passe unique.
- Interface dédiée pour ajouter/modifier/supprimer des items et gérer les suggestions.

### Site Vitrine
- Page d'accueil
- Page de présentation

---

## 🛠️ Technologies utilisées

- **Frontend** : Svelte + Vite
- **Backend** : Spring Boot + Maven
- **Base de données** : HSQLDB

---

## 🚀 Mise en route rapide

### Prérequis
- Node.js + npm
- Java 21 et Maven

### Installation

```bash
# Cloner le projet
git clone https://github.com/baptdes/CARTEL-web-application
cd CARTEL-web-application

# Installer les dépendances
make install

# Lancer le frontend
make start-frontend

# Lancer le backend
make start-backend
```
