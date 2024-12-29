# Application de Santé Mentale - Backend

## Description
Application backend pour un système de gestion de la santé mentale construite avec Spring Boot et Docker.

## Technologies Utilisées
- Java 17
- Spring Boot
- Docker
- Maven
- SonarQube pour la qualité du code
- Eclipse Temurin JDK

## Prérequis
- Java 17 ou supérieur
- Docker
- Maven
- Docker Compose

## Structure du Projet
```
mentalhealth/
├── src/
├── target/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Étapes d'Installation

1. Cloner le dépôt
```bash
git clone [url-de-votre-dépôt]
cd mentalhealth
```

2. Construire l'application
```bash
mvn clean package -DskipTests
```

3. Construire l'image Docker
```bash
docker build -t monapp-spring:1.0 .
```

4. Lancer l'application
```bash
docker-compose up
```

## Configuration Docker

### Dockerfile
```dockerfile
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## Processus de Construction Docker
![photoDocker](https://github.com/user-attachments/assets/75971b21-863b-4b85-a165-2cff5aaf24a8)
*Construction de l'image JDK Alpine et téléchargement des composants nécessaires*

## Exécution de Docker Compose
![photo2](https://github.com/user-attachments/assets/4c47458e-0217-43cf-aa86-063c1ab30040)
*Docker Compose téléchargeant et exécutant le conteneur de base de données*

## Analyse de la Qualité du Code (SonarQube)

### Métriques Détaillées
![photo4](https://github.com/user-attachments/assets/b5ace586-6061-4cc6-aaa6-9be9bbd8ccf0)
*Répartition détaillée des paramètres de qualité du code*

Métriques actuelles :
- Lignes de Code : 1.8k
- Maintenabilité : A (62 points)
- Fiabilité : C (17 points)
- Sécurité : E (1 problème)
- Couverture de Code : 0%
- Duplications : 0%

## Surveillance des Performances

### Utilisation de la Mémoire
![photo5](https://github.com/user-attachments/assets/dfc68b70-3681-4e3c-b122-9f27ea0aa2a7)
*Surveillance de l'utilisation de la mémoire du conteneur (706.1MB / 15.16GB)*

### Utilisation du CPU
![photo6](https://github.com/user-attachments/assets/e16efcd8-0eb3-4751-9682-4b656acb06d5)
*Surveillance de l'utilisation du CPU du conteneur (0.80% / 1200%)*

## Exigences Système
- Mémoire : Minimum 1GB recommandé (Utilisation actuelle ~706MB)
- CPU : Multi-cœurs supporté (12 CPUs disponibles)

## Base de Données
- Base de données embarquée pour l'évaluation
- Taille : ~65MB

## Notes de Développement
- La base de données embarquée ne doit être utilisée qu'à des fins d'évaluation
- Assurez-vous que les mesures de sécurité appropriées sont mises en place avant le déploiement en production
- Augmentation de la couverture des tests recommandée
- Résoudre les problèmes de fiabilité identifiés par SonarQube
