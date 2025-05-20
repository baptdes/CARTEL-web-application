.PHONY: start stop install clean

# Chemins des projets
FRONTEND_DIR = sveltekit-vite-front
BACKEND_DIR = spring-boot-api

# Installer les dépendances
install: install-backend install-frontend

# Arrêter tous les services
stop: stop-frontend stop-backend

# Frontend (Svelte+Vite)
install-frontend:
	@echo "\n |||||||||||||||||| Installing frontend dependencies... |||||||||||||||||| \n"
	cd $(FRONTEND_DIR) && npm install
	cd $(FRONTEND_DIR) && npm install jspdf jspdf-autotable

start-frontend:
	@echo "Starting frontend dev server..."
	cd $(FRONTEND_DIR) && npm run dev

stop-frontend:
	@echo "Stopping frontend..."
	-pkill -f "vite"

# Backend (Spring Boot)
install-backend:
	@echo "\n |||||||||||||||||| Installing backend dependencies... |||||||||||||||||| \n"
	cd $(BACKEND_DIR) && ./gradlew --refresh-dependencies

start-backend:
	@echo "|||||||||||||||||| Starting Backend... |||||||||||||||||| \n"
	@echo "To access H2 Console (SQL Console) once Spring Boot is running:"
	@echo "1. Open your browser and go to http://localhost:8080/h2-console"
	@echo "2. JDBC URL: jdbc:hsqldb:mem:carteldb"
	@echo "3. Username: sa (default)"
	@echo "4. Password: (leave empty by default)"
	@echo "\n |||||||||||||||||| Running Backend... ||||||||||||||||||"
	cd $(BACKEND_DIR) && ./gradlew bootRun --args='--spring.profiles.active=dev'

start-build-continous:
	@echo "|||||||||||||||||| Starting Build Continuous... |||||||||||||||||| \n"
	cd $(BACKEND_DIR) && ./gradlew build --continuous --parallel --build-cache --configuration-cache

# Nettoyage
clean: clean-backend clean-frontend clear-database

clean-frontend:
	@echo "\n |||||||||||||||||| Cleaning Frontend... |||||||||||||||||| \n"
	cd $(FRONTEND_DIR) && rm -rf node_modules dist

clean-backend:
	@echo "\n |||||||||||||||||| Cleaning Backend... |||||||||||||||||| \n"
	cd $(BACKEND_DIR) && ./gradlew clean

# Aide
help:
	@echo "Commandes disponibles:"
	@echo "  make install         - Installe toutes les dépendances"
	@echo "  make install-backend - Installe les dépendances du backend"
	@echo "  make install-frontend - Installe les dépendances du frontend"
	@echo "  make start-backend   - Démarre le backend"
	@echo "  make start-frontend  - Démarre le frontend"
	@echo "  make start-build-continous   - Démarre le build continu backend"
	@echo "  make stop            - Arrête tous les services"
	@echo "  make stop-backend    - Arrête le backend"
	@echo "  make stop-frontend   - Arrête le frontend"
	@echo "  make clean           - Nettoie tous les projets"
	@echo "  make clean-backend   - Nettoie le backend"
	@echo "  make clean-frontend  - Nettoie le frontend"