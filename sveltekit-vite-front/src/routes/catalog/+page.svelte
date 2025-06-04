<script>
  import PointBar from '$lib/components/PointBar.svelte';
  import ItemCard from '$lib/components/ItemCard.svelte';
  import { onMount } from 'svelte';
  import { page } from '$app/stores';
  
  // Import services for both item types
  import { fetchBooks, getAllGenres, getAllAuthors } from '$lib/services/bookService';
  import { fetchGames, getAllGameCategories, getAllCreators } from '$lib/services/gameService';
  
  const { data } = $props();

  // Mode toggle state - use initialMode from load function
  let mode = $state(data.initialMode || 'books'); // 'books' or 'games'
  
  // Initialize display text variables with default values first
  let pageTitle = $state('Livres');
  let searchPlaceholder = $state('Rechercher un livre par titre...');
  let catalogTitle = $state('Catalogue des livres');
  let loadingMessage = $state('Chargement des livres...');
  let emptyResultsMessage = $state('Aucun livre trouvé avec les critères actuels.');
  
  // Update all text variables when mode changes (moved to effect)
  $effect(() => {
    pageTitle = mode === 'books' ? 'Livres' : 'Jeux de société';
    searchPlaceholder = mode === 'books' ? 'Rechercher un livre par titre...' : 'Rechercher un jeu par titre...';
    catalogTitle = mode === 'books' ? 'Catalogue des livres' : 'Catalogue des jeux de société';
    loadingMessage = mode === 'books' ? 'Chargement des livres...' : 'Chargement des jeux...';
    emptyResultsMessage = mode === 'books' ? 'Aucun livre trouvé avec les critères actuels.' : 'Aucun jeu trouvé avec les critères actuels.';
  });
  
  // Shared state variables
  let items = $state([]);
  let isLoading = $state(true);
  let error = $state(null);
  let searchQuery = $state('');

  // Books specific state
  let genres = $state([]);
  let formats = $state([]);
  let authors = $state([]);
  let selectedGenre = $state("ALL");
  let selectedFormat = $state("ALL");
  let selectedAuthorFirstName = $state("");
  let selectedAuthorSurname = $state("");
  let selectedIllustratorFirstName = $state(""); // Separate illustrator first name
  let selectedIllustratorSurname = $state(""); // Separate illustrator surname

  // Games specific state
  let categories = $state([]);
  let creators = $state([]);
  let selectedCategory = $state("ALL");
  let selectedCreator = $state("ALL");
  let minPlayers = $state("");
  let maxPlayers = $state("");
  let minPlaytime = $state("");
  let maxPlaytime = $state("");
  
  // Shared filter state
  let selectedPublisher = $state("");
  
  // Pagination
  let pageNumber = $state(0);
  let pageSize = $state(20);
  
  // Sort options - Define before sortOption
  const sortOptions = [
    { value: "name_asc", label: "Titre (A-Z)", field: "name", asc: true },
    { value: "name_desc", label: "Titre (Z-A)", field: "name", asc: false },
    { value: "year_desc", label: "Plus récents", field: "publicationYear", asc: false },
    { value: "year_asc", label: "Plus anciens", field: "publicationYear", asc: true },
  ];
  
  // Add sorting state
  let sortOption = $state(sortOptions[0].value);
  
  // Switch between books and games mode
  function toggleMode() {
    // Reset all state first
    resetFilters();
    
    // Toggle mode
    mode = mode === 'books' ? 'games' : 'books';
    
    // Load data for the new mode
    loadInitialData();
  }
  
  // Handle search with filters - unified function that works for both modes
  async function handleSearch() {
    isLoading = true;
    error = null;
    try {
      // Find the selected sort option
      const selectedSort = sortOptions.find(option => option.value === sortOption);
      
      // Construct base query parameters
      const params = {
        pageNumber,
        pageSize,
        asc: selectedSort.asc,
        sortBy: selectedSort.field,
      };
      
      // Add mode-specific filters
      if (mode === 'books') {
        // Book specific parameters
        if (searchQuery) params.titleBook = searchQuery;
        if (selectedPublisher && selectedPublisher !== "") params.publisherName = selectedPublisher;
        if (selectedAuthorFirstName) params.authorFirstName = selectedAuthorFirstName;
        if (selectedAuthorSurname) params.authorSurname = selectedAuthorSurname;
        if (selectedFormat && selectedFormat !== "ALL") params.category = selectedFormat;
        if (selectedGenre && selectedGenre !== "ALL") params.genreName = selectedGenre;
        if (selectedIllustratorFirstName) params.illustratorFirstName = selectedIllustratorFirstName;
        if (selectedIllustratorSurname) params.illustratorSurname = selectedIllustratorSurname;

        // Fetch books
        items = await fetchBooks(params);
      } else {
        // Game specific parameters
        if (searchQuery) params.titleGame = searchQuery;
        if (selectedPublisher && selectedPublisher !== "") params.publisherName = selectedPublisher;
        if (selectedCreator && selectedCreator !== "ALL") {
          const [firstName, surname] = selectedCreator.split('|');
          params.creatorFirstName = firstName;
          params.creatorSurname = surname;
        }
        if (selectedCategory && selectedCategory !== "ALL") params.category = selectedCategory;
        if (minPlayers && !isNaN(parseInt(minPlayers))) params.minPlayers = parseInt(minPlayers);
        if (maxPlayers && !isNaN(parseInt(maxPlayers))) params.maxPlayers = parseInt(maxPlayers);
        if (minPlaytime && !isNaN(parseInt(minPlaytime))) params.minPlaytime = parseInt(minPlaytime);
        if (maxPlaytime && !isNaN(parseInt(maxPlaytime))) params.maxPlaytime = parseInt(maxPlaytime);
        
        // Fetch games
        items = await fetchGames(params);
      }
    } catch (err) {
      error = err.message || `Failed to search ${mode}`;
    } finally {
      isLoading = false;
    }
  }
  
  // Reset search filters but keep the current mode
  function resetFilters() {
    // Reset shared filters
    searchQuery = '';
    selectedPublisher = "";
    sortOption = sortOptions[0].value;
    pageNumber = 0;
    selectedIllustratorFirstName = ""; // Reset illustrator first name
    selectedIllustratorSurname = ""; // Reset illustrator surname
    
    // Reset mode-specific filters
    if (mode === 'books') {
      selectedGenre = "ALL";
      selectedFormat = "ALL";
    } else {
      selectedCategory = "ALL";
      selectedCreator = "ALL";
      minPlayers = "";
      maxPlayers = "";
      minPlaytime = "";
      maxPlaytime = "";
    }
  }
  
  // Reset search and reload data
  async function resetSearch() {
    resetFilters();
    await loadInitialData();
  }
  
  // Load initial data based on current mode
  async function loadInitialData() {
    isLoading = true;
    error = null;
    try {
      if (mode === 'books') {
        // Fetch book-specific data
        genres = await getAllGenres();
        genres = genres.map(genre => genre.name);
        formats = ["MANGA", "BD", "LIVRE"];
        authors = await getAllAuthors();
        
        // Get initial books
        items = await fetchBooks({
          pageNumber: 0,
          pageSize,
          asc: true,
          sortBy: "name"
        });
      } else {
        // Fetch game-specific data
        categories = await getAllGameCategories();
        creators = await getAllCreators();
        
        // Get initial games
        items = await fetchGames({
          pageNumber: 0,
          pageSize,
          asc: true,
          sortBy: "name"
        });
      }
    } catch (err) {
      error = err.message || 'Failed to load initial data';
    } finally {
      isLoading = false;
    }
  }
  
  // Initialize component
  onMount(() => {
    loadInitialData();
  });
</script>

<svelte:head>
  <title>{pageTitle} | C.A.R.T.E.L</title>
</svelte:head>

<div class="container">
  <div class="mode-toggle">
    <div class="toggle-container">
      <button 
        class="toggle-button" 
        class:active={mode === 'books'} 
        onclick={() => mode === 'games' && toggleMode()}
      >
        📚 Livres
      </button>
      <button 
        class="toggle-button" 
        class:active={mode === 'games'} 
        onclick={() => mode === 'books' && toggleMode()}
      >
        🎲 Jeux
      </button>
    </div>
  </div>
  
  <div class="search">
    <div class="search-container">
      <input 
        type="text" 
        bind:value={searchQuery}
        placeholder={searchPlaceholder}
        onkeydown={(e) => e.key === 'Enter' && handleSearch()}
      />
      <button onclick={handleSearch} aria-label="Rechercher">
        🔍
      </button>
    </div>
  </div>

  <div class="main">
    <!-- Left sidebar with filters -->
    <aside>
      <h2>Filtres</h2>
      
      {#if mode === 'books'}
        <!-- Book-specific filters -->
        <div class="section">
          <h3>Genre</h3>
          <div>
            <select bind:value={selectedGenre} title="Sélectionner un genre">
              <option value="ALL">Tous les genres</option>
              {#each genres as genre}
                <option value={genre}>{genre}</option>
              {/each}
            </select>
          </div>
        </div>

        <div class="section">
          <h3>Format</h3>
          <div>
              <select bind:value={selectedFormat} title="Sélectionner un format">
              <option value="ALL">Tous les formats</option>
              {#each formats as format}
                <option value={format}>{format === "LIVRE" ? "Livre" : format === "BD" ? "Bande Dessinée" : format}</option>
              {/each}
              </select>
          </div>
        </div>
        
        <div class="section">
          <h3>Auteur</h3>
          <div>
            <input 
              type="text" 
              bind:value={selectedAuthorFirstName} 
              placeholder="Prénom de l'auteur"
              class="author-input"
            />
            <input 
              type="text" 
              bind:value={selectedAuthorSurname} 
              placeholder="Nom de famille de l'auteur"
              class="author-input"
            />
          </div>
        </div>

        <div class="section">
          <h3>Illustrateur</h3>
          <div>
            <input 
              type="text" 
              bind:value={selectedIllustratorFirstName} 
              placeholder="Prénom de l'illustrateur"
              class="illustrator-input"
            />
            <input 
              type="text" 
              bind:value={selectedIllustratorSurname} 
              placeholder="Nom de famille de l'illustrateur"
              class="illustrator-input"
            />
          </div>
        </div>
      {:else}
        <div class="section">
          <h3>Nombre de joueurs</h3>
          <div class="range-inputs">
            <input 
              type="number"
              bind:value={minPlayers}
              placeholder="Min"
              min="1"
            />
            <span class="range-separator">à</span>
            <input 
              type="number"
              bind:value={maxPlayers}
              placeholder="Max"
              min="1"
            />
          </div>
        </div>

        <div class="section">
          <h3>Durée (minutes)</h3>
          <div class="range-inputs">
            <input 
              type="number"
              bind:value={minPlaytime}
              placeholder="Min"
              min="1"
            />
            <span class="range-separator">à</span>
            <input 
              type="number"
              bind:value={maxPlaytime}
              placeholder="Max"
              min="1"
            />
          </div>
        </div>
      {/if}
      
      <button class="apply-filters" onclick={handleSearch}>
        Appliquer les filtres
      </button>
      
      <button class="reset" onclick={resetSearch}>
        Réinitialiser la recherche
      </button>
    </aside>
    
    <!-- Right side content area -->
    <div class="content">
      <div class="info-bar">
        <div>
          <span>{catalogTitle}</span>
        </div>
        <div class="sort-container">
          <label for="sort-select">Trier par:</label>
          <select id="sort-select" bind:value={sortOption} class="sort-select" onchange={handleSearch}>
            {#each sortOptions as option}
              <option value={option.value}>{option.label}</option>
            {/each}
          </select>
        </div>
      </div>
      
      <!-- Loading state -->
      {#if isLoading}
        <div class="loading">
          <div class="spinner"></div>
          <p>{loadingMessage}</p>
        </div>
      {/if}
      
      <!-- Error state -->
      {#if error}
        <div class="error">
          <p>Erreur lors du chargement des données: {error}</p>
          <button onclick={resetSearch}>Réessayer</button>
        </div>
      {/if}
      
      <!-- Results display - cards -->
      <div class="results">
        {#if !isLoading && items.length === 0}
          <div class="no-results">
            <p>{emptyResultsMessage}</p>
          </div>
        {/if}
        
        {#each items as item (item.barcode)}
          <ItemCard {item} type={mode === 'books' ? 'book' : 'game'} />
        {/each}
      </div>
      
      <!-- Pagination controls -->
      {#if items.length > 0}
        <div class="pagination">
          <button 
            disabled={pageNumber === 0} 
            onclick={() => { pageNumber -= 1; handleSearch(); }}
            class="pagination-button"
          >
            &laquo; Précédent
          </button>
          
          <span class="page-info">Page {pageNumber + 1}</span>
          
          <button 
            disabled={items.length < pageSize}
            onclick={() => { pageNumber += 1; handleSearch(); }}
            class="pagination-button"
          >
            Suivant &raquo;
          </button>
        </div>
      {/if}
    </div>
  </div>
</div>

<style lang="scss">
  .container {
    display: flex;
    flex-direction: column;
    min-height: calc(100vh - 4rem);
    position: relative;
    padding-bottom: 2rem;
    padding-top: 2rem;
    background-image: url('/textures/dark-brown-old-stone-wall.png');
    background-size: cover;
    background-attachment: fixed;
    background-position: center;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background-color: rgba(0, 0, 0, 0.2);
      z-index: 0;
    }
  }

  .mode-toggle {
    z-index: 100;
    position: relative;
    display: flex;
    justify-content: center;
    margin-bottom: 1.5rem;

    .toggle-container {
      display: flex;
      background-color: #2a2a2a;
      border: 2px solid var(--dark-orange);
      border-radius: 50px;
      padding: 0.3rem;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.25);
      
      .toggle-button {
        padding: 0.8rem 2rem;
        border: none;
        background-color: transparent;
        color: #e0d6c2;
        border-radius: 50px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s ease;
        font-size: 1rem;
        
        &:focus {
          outline: none;
        }
        
        &.active {
          background-color: var(--dark-orange);
          color: white;
          box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
        }

        &:not(.active):hover {
          background-color: rgba(255, 255, 255, 0.1);
        }
      }
    }
  }
  
  .search {
    padding: 1rem;
    z-index: 100;
    position: relative;
    margin-bottom: 1.5rem;
    
    .search-container {
      max-width: 800px;
      margin: 0 auto;
      position: relative;
      display: flex;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      border-radius: 50px;
      background-color: #f0e6d2;
      
      input {
        flex: 1;
        padding: 0.8rem 1.5rem;
        background-color: transparent;
        border: 2px solid #d4b483;
        border-radius: 50px;
        color: #59280d;
        font-size: 1rem;
        transition: all 0.3s ease;
        
        &::placeholder {
          color: rgba(89, 40, 13, 0.5);
        }
        
        &:focus {
          outline: none;
          border-color: var(--orange);
          box-shadow: 0 0 0 2px rgba(199, 112, 49, 0.2);
        }
      }
      
      button {
        position: absolute;
        right: 6px;
        top: 50%;
        transform: translateY(-50%);
        width: 40px;
        height: 40px;
        border-radius: 50%;
        background-color: var(--orange);
        color: white;
        border: none;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        transition: background-color 0.2s;
        font-size: 1.1rem;
        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
        
        &:hover {
          background-color: var(--dark-orange);
        }
      }
    }
  }
  
  .main {
    display: flex;
    flex: 1;
    padding: 0 1rem;
    max-width: 1400px;
    margin: 0 auto;
    width: 100%;
    position: relative;
    z-index: 1;
    
    aside {
      width: 250px;
      flex-shrink: 0;
      padding: 1.5rem;
      background-color: #2a2a2a; /* Dark background */
      border: 8px solid var(--dark-orange); /* Keep orange border for consistency */
      border-radius: 8px;
      margin-right: 1.5rem;
      height: fit-content;
      box-shadow: 0 5px 15px rgba(0, 0, 0, 0.35);
      
      h2 {
        font-family: "Pirata One", cursive;
        color: var(--white);
        margin-top: 0;
        margin-bottom: 1.5rem;
        font-size: 1.8rem;
        text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
      }
      
      h3 {
        color: var(--white);
        margin: 0 0 0.8rem 0;
        font-size: 1.2rem;
      }
      
      .section {
        margin-bottom: 1.5rem;
        padding-bottom: 1.5rem;
        border-bottom: 1px solid rgba(224, 214, 194, 0.2);
        
        &:last-child {
          border-bottom: none;
        }
        
        div {
          display: flex;
          flex-direction: column;
          gap: 0.5rem;
          
          select {
            width: 100%;
            padding: 0.5rem;
            background-color: #343434;
            color: #e0d6c2;
            border: 1px solid var(--dark-orange);
            border-radius: 4px;
            
            &:focus {
              outline: none;
              border-color: var(--orange);
            }
          }
        }
      }
      
      .apply-filters {
        width: 100%;
        padding: 0.8rem;
        background-color: var(--orange);
        color: white;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        font-weight: bold;
        transition: background-color 0.2s;
        margin-bottom: 1rem;
        
        &:hover {
          background-color: var(--dark-orange);
        }
      }
      
      .reset {
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
    }
  }
  
  .content {
    flex: 1;
    display: flex;
    flex-direction: column;
    
    .info-bar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 1rem;
      background-color: #2a2a2a; /* Dark background */
      border: 2px solid var(--dark-orange);
      border-radius: 8px;
      margin-bottom: 1.5rem;
      color: var(--white);
      
      span {
        font-size: 1.1rem;
        font-weight: bold;
      }
      
      .sort-container {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        
        label {
          color: #e0d6c2;
          font-size: 0.9rem;
        }
        
        .sort-select {
          padding: 0.3rem;
          background-color: #343434;
          color: var(--white);
          border: 1px solid var(--dark-orange);
          border-radius: 4px;
          font-size: 0.9rem;
          
          &:focus {
            outline: none;
            border-color: var(--orange);
          }
        }
      }
    }
    
    .results {
      display: flex;
      flex-direction: column;
      padding-bottom: 1.5rem;
    }
  }
  
  // Loading and error states
  .loading, .error, .no-results {
    padding: 2rem;
    text-align: center;
    background-color: #2a2a2a; /* Dark background */
    border: 2px solid var(--dark-orange);
    border-radius: 8px;
    margin: 1rem 0;
    
    p {
      color: #e0d6c2;
      margin: 0;
    }
  }
  
  .loading .spinner {
    margin: 0 auto 1rem;
    width: 40px;
    height: 40px;
    border: 3px solid rgba(224, 214, 194, 0.2);
    border-radius: 50%;
    border-top-color: var(--orange);
    animation: spin 1s linear infinite;
  }
  
  .error {
    border: 2px solid var(--red);
    
    p {
      color: var(--red);
    }
    
    button {
      margin-top: 1rem;
      padding: 0.5rem 1rem;
      background-color: var(--orange);
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      
      &:hover {
        background-color: var(--dark-orange);
      }
    }
  }
  
  .no-results {
    padding: 3rem;
    
    p {
      font-size: 1.1rem;
      color: #e0d6c2;
      font-style: italic;
    }
  }
  
  @keyframes spin {
    to {
      transform: rotate(360deg);
    }
  }
  
  @media (max-width: 900px) {
    .main {
      flex-direction: column;
      
      aside {
        width: 100%;
        margin-right: 0;
        margin-bottom: 1.5rem;
      }
    }
  }
  
  @media (max-width: 768px) {
    .mode-toggle .toggle-container .toggle-button {
      padding: 0.8rem 1rem;
    }
    
    .info-bar {
      flex-direction: column;
      gap: 0.75rem;
      align-items: flex-start;
      
      .sort-container {
        flex-wrap: wrap;
        width: 100%;
      }
    }
  }

  input {
     width: 100%;
    padding: 0.5rem;
    background-color: #343434;
    color: #e0d6c2;
    border: 1px solid var(--dark-orange);
    border-radius: 4px;
    
    &:focus {
      outline: none;
      border-color: var(--orange);
    }
  }

  .range-inputs {
    display: flex;
    align-items: center;
    gap: 0.5rem;

    .range-separator {
      color: #e0d6c2;
      font-size: 0.9rem;
    }
  }
  
  .pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 1.5rem;
    gap: 1rem;
    
    .pagination-button {
      padding: 0.5rem 1rem;
      background-color: #343434;
      color: #e0d6c2;
      border: 1px solid var(--dark-orange);
      border-radius: 4px;
      cursor: pointer;
      transition: background-color 0.2s;
      
      &:hover:not(:disabled) {
        background-color: var(--dark-orange);
        color: white;
      }
      
      &:disabled {
        opacity: 0.5;
        cursor: not-allowed;
      }
    }
    
    .page-info {
      color: #e0d6c2;
      font-size: 0.9rem;
    }
  }

  .illustrator-input {
    width: 100%;
    padding: 0.5rem;
    background-color: #343434;
    color: #e0d6c2;
    border: 1px solid var(--dark-orange);
    border-radius: 4px;

    &:focus {
      outline: none;
      border-color: var(--orange);
    }
  }
</style>
