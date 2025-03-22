.PHONY: start stop install clean

# Chemins des projets
FRONTEND_DIR = svelte-vite-front
BACKEND_DIR = spring-boot-api
DATABASE_DIR = hsqldb

# Variables pour la base de données HSQLDB
DB_NAME = carteldb
DB_PORT = 9001

# Installer les dépendances
install: install-backend install-frontend

# Démarrer tous les services pour le développement
start: start-database start-backend start-frontend

# Arrêter tous les services
stop: stop-frontend stop-backend stop-database

# Frontend (Svelte+Vite)
install-frontend:
	@echo "\n |||||||||||||||||| Installing frontend dependencies... |||||||||||||||||| \n"
	cd $(FRONTEND_DIR) && npm install

start-frontend:
	@echo "Starting frontend dev server..."
	cd $(FRONTEND_DIR) && npm run dev &

stop-frontend:
	@echo "Stopping frontend..."
	-pkill -f "vite"

# Backend (Spring Boot)
install-backend:
	@echo "\n |||||||||||||||||| Installing backend dependencies... |||||||||||||||||| \n"
	cd $(BACKEND_DIR) && ./gradlew --refresh-dependencies

start-backend:
	@echo "Starting Spring Boot backend..."
	cd $(BACKEND_DIR) && ./gradlew bootRun &

stop-backend:
	@echo "Stopping backend..."
	-pkill -f "spring-boot-api"

# Base de données HSQLDB
start-database: 
	@echo "\n |||||||||||||||||| Starting HSQLDB... |||||||||||||||||| \n"
	@mkdir -p $(DATABASE_DIR)/data
	java -cp $(DATABASE_DIR)/hsqldb.jar org.hsqldb.server.Server --database.0 file:$(DATABASE_DIR)/data/$(DB_NAME) --dbname.0 $(DB_NAME) --port $(DB_PORT) &

dev-database:
	@echo "|||||||||||||||||| Starting HSQLDB with GUI for development ... ||||||||||||||||||"
	@mkdir -p $(DATABASE_DIR)/data
	java -cp $(DATABASE_DIR)/hsqldb.jar org.hsqldb.util.DatabaseManagerSwing

stop-database:
	@echo "\n |||||||||||||||||| Stopping HSQLDB... |||||||||||||||||| \n"
	-pkill -f "org.hsqldb"

# Nettoyage
clean: clean-backend clean-frontend clear-database

clean-frontend:
	@echo "\n |||||||||||||||||| Cleaning Frontend... |||||||||||||||||| \n"
	cd $(FRONTEND_DIR) && rm -rf node_modules dist

clean-backend:
	@echo "\n |||||||||||||||||| Cleaning Backend... |||||||||||||||||| \n"
	cd $(BACKEND_DIR) && ./gradlew clean

clear-database:
	@echo "\n |||||||||||||||||| Clearing Database HSQLDB... |||||||||||||||||| \n"
	rm -f $(DATABASE_DIR)/data/*
	@echo "Database files cleared successfully."

# Aide
help:
	@echo "Commandes disponibles:"
	@echo "  make install      - Installe toutes les dépendances"
	@echo "  make build        - Construit les projets frontend et backend"
	@echo "  make start        - Démarre tous les services (BDD, backend, frontend)"
	@echo "  make stop         - Arrête tous les services"
	@echo "  make clean        - Nettoie tous les projets"
	@echo "  make dev-database - Lance l'interface graphique HSQLDB pour le développement"