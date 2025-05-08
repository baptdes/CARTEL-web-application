<script>
  import PointBar from '$lib/components/PointBar.svelte';
  
  // Filtered data to show only board games
  const exampleItems = [
    {
      id: 3,
      title: "Catan",
      author: "Klaus Teuber",
      description: "Jeu de stratégie et de développement où les joueurs doivent coloniser une île en construisant des villes et des routes.",
      coverImage: "https://www.espritjeu.com/upload/image/catan-p-image-65490-grande.jpg",
      publicationYear: 1995,
      category: "Jeu de stratégie",
      available: false,
      type: "boardgame",
      publisher: "Kosmos",
      minPlayers: 3,
      maxPlayers: 4,
      playTime: "60-120 min",
      minAge: 10
    },
    {
      id: 4,
      title: "Pandemic",
      author: "Matt Leacock",
      description: "Jeu coopératif où les joueurs travaillent ensemble pour arrêter la propagation de maladies mortelles à travers le monde.",
      coverImage: "https://bienjouets.fr/3314-large_default/pandemic.jpg",
      publicationYear: 2008,
      category: "Jeu coopératif",
      available: true,
      type: "boardgame",
      publisher: "Z-Man Games",
      minPlayers: 2,
      maxPlayers: 4,
      playTime: "45 min",
      minAge: 8
    }
  ];
  
  // Game-specific categories
  const categories = ["Toutes", "Stratégie", "Coopératif", "Gestion", "Familial", "Ambiance", "Cartes", "Fantasy", "Aventure"];
</script>

<svelte:head>
  <title>Jeux de Société | C.A.R.T.E.L</title>
</svelte:head>

<div class="catalogue-container">
  <div class="search-container search-top">
    <div class="search-bar">
      <input 
        type="text" 
        placeholder="Rechercher un jeu par titre, éditeur ou mécaniques..."
      />
      <button class="search-button">
        🔍
      </button>
    </div>
  </div>

  <div class="catalogue-main">
    <!-- Left sidebar with game-specific filters -->
    <aside class="filter-panel">
      <h2>Filtres</h2>
      
      <div class="filter-section">
        <h3>Catégorie</h3>
        <div class="filter-options">
          <select class="filter-select">
            {#each categories as category}
              <option>{category}</option>
            {/each}
          </select>
        </div>
      </div>
      
      <div class="filter-section">
        <h3>Nombre de joueurs</h3>
        <div class="filter-options">
          <label class="filter-option">
            <input type="radio" name="players" checked />
            <span>Tous</span>
          </label>
          <label class="filter-option">
            <input type="radio" name="players" />
            <span>2 joueurs</span>
          </label>
          <label class="filter-option">
            <input type="radio" name="players" />
            <span>3-4 joueurs</span>
          </label>
          <label class="filter-option">
            <input type="radio" name="players" />
            <span>5+ joueurs</span>
          </label>
        </div>
      </div>
      
      <div class="filter-section">
        <h3>Durée de partie</h3>
        <div class="filter-options">
          <label class="filter-option">
            <input type="radio" name="duration" checked />
            <span>Toutes</span>
          </label>
          <label class="filter-option">
            <input type="radio" name="duration" />
            <span>30 min</span>
          </label>
          <label class="filter-option">
            <input type="radio" name="duration" />
            <span>30-60 min</span>
          </label>
          <label class="filter-option">
            <input type="radio" name="duration" />
            <span>60+ min</span>
          </label>
        </div>
      </div>
      
      <div class="filter-section">
        <h3>Disponibilité</h3>
        <div class="filter-options">
          <label class="filter-option">
            <input type="radio" name="availability" checked />
            <span>Tous</span>
          </label>
          <label class="filter-option">
            <input type="radio" name="availability" />
            <span>Disponible</span>
          </label>
          <label class="filter-option">
            <input type="radio" name="availability" />
            <span>Non disponible</span>
          </label>
        </div>
      </div>
      
      <button class="reset-filters">
        Réinitialiser les filtres
      </button>
    </aside>
    
    <!-- Right side content area -->
    <div class="catalogue-content">
      <!-- Sorting options bar (game-specific) -->
      <div class="sort-bar">
        <div class="sort-options">
          <span>Trier par:</span>
          <select>
            <option>Titre</option>
            <option>Éditeur</option>
            <option>Année</option>
            <option>Durée de jeu</option>
          </select>
          <button class="sort-direction" aria-label="Toggle sort direction">
            ↑
          </button>
        </div>
        <div class="results-count">
          {exampleItems.length} résultats
        </div>
      </div>
      
      <!-- Results display - Board game-specific cards -->
      <div class="catalogue-results">
        
        {#each exampleItems as item}
          <div class="catalogue-card">
            <div class="card-image">
              <img 
                src={item.coverImage || "/placeholder_game.png"}
                alt={`Image de ${item.title}`}
                loading="lazy"
              />
              <div class="item-type-badge">
                <img src="/icons/dice.svg" alt="Jeu" />
              </div>
            </div>
            
            <div class="card-content">
              <div class="card-header">
                <h3 class="card-title">{item.title}</h3>
                <div class="availability-badge" class:available={item.available}>
                  {item.available ? 'Disponible' : 'Indisponible'}
                </div>
              </div>
              
              <div class="card-author">
                <strong>Créateur :</strong> {item.author}
              </div>
              
              <div class="card-publisher">
                <strong>Éditeur :</strong> {item.publisher}
              </div>
              
              <div class="card-year">
                <strong>Année :</strong> {item.publicationYear}
              </div>
              
              <div class="card-category">
                <strong>Type de jeu :</strong> {item.category}
              </div>
              
              <div class="card-players">
                <strong>Joueurs :</strong> {item.minPlayers}-{item.maxPlayers}
              </div>
              
              <div class="card-time">
                <strong>Durée :</strong> {item.playTime}
              </div>
              
              <div class="card-age">
                <strong>Âge :</strong> {item.minAge}+
              </div>
              
              <p class="card-description">{item.description.substring(0, 150)}{item.description.length > 150 ? '...' : ''}</p>
            </div>
          </div>
        {/each}
      </div>
    </div>
  </div>
</div>

<style lang="scss">
  .catalogue-container {
    display: flex;
    flex-direction: column;
    min-height: calc(100vh - 4rem);
    position: relative;
    padding-bottom: 2rem;
  }
  
  .catalogue-main {
    display: flex;
    flex: 1;
    padding: 0 1rem;
    max-width: 1400px;
    margin: 0 auto;
    width: 100%;
  }
  
  // Filter panel (left sidebar)
  .filter-panel {
    width: 250px;
    flex-shrink: 0;
    padding: 1.5rem;
    background-color: var(--bg-card);
    border-radius: 8px;
    margin-right: 1.5rem;
    height: fit-content;
    
    h2 {
      font-family: "Pirata One", cursive;
      color: var(--orange);
      margin-top: 0;
      margin-bottom: 1.5rem;
      font-size: 1.8rem;
    }
    
    h3 {
      color: var(--white);
      margin: 0 0 0.8rem 0;
      font-size: 1.2rem;
    }
  }
  
  .filter-section {
    margin-bottom: 1.5rem;
    padding-bottom: 1.5rem;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    
    &:last-child {
      border-bottom: none;
    }
  }
  
  .filter-options {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .filter-option {
    display: flex;
    align-items: center;
    cursor: pointer;
    
    input {
      margin-right: 0.5rem;
    }
    
    span {
      color: var(--white);
    }
  }
  
  .filter-select {
    width: 100%;
    padding: 0.5rem;
    background-color: #333;
    color: white;
    border: 1px solid #555;
    border-radius: 4px;
    
    &:focus {
      outline: none;
      border-color: var(--orange);
    }
  }
  
  .reset-filters {
    width: 100%;
    padding: 0.6rem;
    background-color: transparent;
    color: var(--orange);
    border: 1px solid var(--orange);
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.2s;
    
    &:hover {
      background-color: rgba(199, 112, 49, 0.2);
    }
  }
  
  // Content area (right side)
  .catalogue-content {
    flex: 1;
    display: flex;
    flex-direction: column;
  }
  
  // Sort bar
  .sort-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem;
    background-color: var(--bg-card);
    border-radius: 8px;
    margin-bottom: 1.5rem;
    
    .sort-options {
      display: flex;
      align-items: center;
      gap: 1rem;
      
      span {
        color: var(--white);
      }
      
      select {
        padding: 0.5rem;
        background-color: #333;
        color: white;
        border: 1px solid #555;
        border-radius: 4px;
      }
      
      .sort-direction {
        background-color: var(--orange);
        color: white;
        border: none;
        border-radius: 4px;
        width: 30px;
        height: 30px;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        transition: background-color 0.2s;
        
        &:hover {
          background-color: var(--dark-orange);
        }
      }
    }
    
    .results-count {
      color: var(--white);
      font-size: 0.9rem;
    }
  }
  
  // Catalog results
  .catalogue-results {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
    padding-bottom: 1.5rem;
  }
  
  // Catalog cards
  .catalogue-card {
    display: flex;
    background-color: #2a2a2a;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
    transition: transform 0.2s, box-shadow 0.2s;
    cursor: pointer;
    width: 100%;
    text-align: left;
    font-family: inherit;
    padding: 0;
    border: none;
    margin: 0;
    
    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 16px rgba(0, 0, 0, 0.3);
    }
    
    &:focus {
      outline: 2px solid var(--orange);
      transform: translateY(-4px);
    }
  }
  
  .card-image {
    width: 180px;
    flex-shrink: 0;
    position: relative;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
    
    .item-type-badge {
      position: absolute;
      top: 10px;
      left: 10px;
      background-color: rgba(0, 0, 0, 0.7);
      border-radius: 50%;
      width: 30px;
      height: 30px;
      display: flex;
      align-items: center;
      justify-content: center;
      
      img {
        width: 18px;
        height: 18px;
        filter: invert(1);
      }
    }
  }
  
  .card-content {
    padding: 1.5rem;
    flex: 1;
    display: flex;
    flex-direction: column;
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 0.8rem;
  }
  
  .card-title {
    font-family: "Pirata One", cursive;
    color: var(--red);
    font-size: 1.5rem;
    margin: 0;
  }
  
  .availability-badge {
    padding: 0.3rem 0.6rem;
    border-radius: 4px;
    font-size: 0.8rem;
    font-weight: bold;
    background-color: #f44336;
    color: white;
    
    &.available {
      background-color: #4caf50;
    }
  }
  
  .card-author,
  .card-year,
  .card-category {
    margin-bottom: 0.5rem;
    color: #ddd;
    
    strong {
      color: var(--orange);
    }
  }
  
  .card-publisher,
  .card-players,
  .card-time,
  .card-age {
    margin-bottom: 0.5rem;
    color: #ddd;
    
    strong {
      color: var(--orange);
    }
  }
  
  .card-description {
    margin: 0.8rem 0;
    line-height: 1.4;
    color: #bbb;
    flex: 1;
  }
  
  // Search bar styling
  .search-container {
    background-color: rgba(26, 26, 26, 0.95);
    padding: 1rem;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
    z-index: 100;
    
    // Special styling for top search bar
    &.search-top {
      position: relative;
      margin-bottom: 1.5rem;
      border-radius: 0 0 8px 8px;
    }
  }
  
  .search-bar {
    max-width: 800px;
    margin: 0 auto;
    display: flex;
    gap: 0.5rem;
    
    input {
      flex: 1;
      padding: 0.8rem 1rem;
      border: 2px solid var(--dark-red);
      background-color: #222;
      color: white;
      border-radius: 4px;
      font-size: 1rem;
      
      &:focus {
        outline: none;
        border-color: var(--red);
      }
    }
    
    .search-button {
      padding: 0 1.2rem;
      background-color: var(--red);
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      transition: background-color 0.2s;
      font-size: 1.2rem;
      
      &:hover {
        background-color: var(--dark-red);
      }
    }
  }
  
  // Responsive styles
  @media (max-width: 900px) {
    .catalogue-main {
      flex-direction: column;
    }
    
    .filter-panel {
      width: 100%;
      margin-right: 0;
      margin-bottom: 1.5rem;
    }
    
    .card-image {
      width: 120px;
    }
  }
  
  @media (max-width: 600px) {
    .catalogue-card {
      flex-direction: column;
    }
    
    .card-image {
      width: 100%;
      height: 200px;
    }
    
    .sort-bar {
      flex-direction: column;
      gap: 1rem;
      
      .sort-options {
        width: 100%;
        justify-content: space-between;
      }
    }
  }
</style>
