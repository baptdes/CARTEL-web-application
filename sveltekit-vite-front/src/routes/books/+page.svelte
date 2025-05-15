<script>
  import PointBar from '$lib/components/PointBar.svelte';
  import BookCard from '$lib/components/BookCard.svelte';
  import { onMount } from 'svelte';
  import { fetchBooks, fetchGenres, getAllAuthors } from '$lib/services/bookService';
  
  // State variables
  let books = $state([]);
  let genres = $state([]);
  let formats = $state([]);
  let authors = $state([]);
  let isLoading = $state(true);
  let error = $state(null);
  let searchQuery = $state('');

  // Filter state variables
  let selectedGenre = $state("ALL");
  let selectedFormat = $state("ALL");
  let selectedAuthor = $state("ALL");
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
  
  // Add sorting state with string key instead of object
  let sortOption = $state(sortOptions[0].value);
  
  // Handle search with filters
  async function handleSearch() {
    isLoading = true;
    error = null;
    try {
      // Find the selected sort option
      const selectedSort = sortOptions.find(option => option.value === sortOption);
      
      // Construct query parameters based on filters
      const params = {
        pageNumber,
        pageSize,
        asc: selectedSort.asc,
        sortBy: selectedSort.field,
      };
      
      // Add filters if they're set
      if (searchQuery) params.title = searchQuery;
      if (selectedPublisher && selectedPublisher !== "") params.publisherName = selectedPublisher;
      if (selectedAuthor && selectedAuthor !== "ALL") {
        const [firstName, surname] = selectedAuthor.split('|');
        params.authorFirstName = firstName;
        params.authorSurname = surname;
      }
      if (selectedFormat && selectedFormat !== "ALL") params.category = selectedFormat;
      if (selectedGenre && selectedGenre !== "ALL") params.genreName = selectedGenre;
      
      books = await fetchBooks(params);
    } catch (err) {
      error = err.message || 'Failed to search books';
    } finally {
      isLoading = false;
    }
  }
  
  // Reset search
  async function resetSearch() {
    searchQuery = '';
    selectedGenre = "ALL";
    selectedFormat = "ALL";
    selectedAuthor = "ALL";
    selectedPublisher = "";
    sortOption = sortOptions[0].value; // Reset to first option
    pageNumber = 0;
    
    isLoading = true;
    error = null;
    try {
      books = await fetchBooks({
        pageNumber: 0,
        pageSize,
        asc: true,
        sortBy: "name"
      });
    } catch (err) {
      error = err.message || 'Failed to reset search';
    } finally {
      isLoading = false;
    }
  }
  
  // Initialize component
  onMount(async () => {
    try {
      // Fetch genres and formats
      genres = await fetchGenres();
      
      // Extract available book formats from the enum
      formats = ["MANGA", "BD", "LIVRE"];
      
      // Fetch authors
      authors = await getAllAuthors();
      
      // Fetch books with default parameters
      books = await fetchBooks({
        pageNumber: 0,
        pageSize,
        asc: true,
        sortBy: "name"
      });
    } catch (err) {
      error = err.message || 'Failed to load initial data';
    } finally {
      isLoading = false;
    }
  });
</script>

<svelte:head>
  <title>Livres | C.A.R.T.E.L</title>
</svelte:head>

<div class="container">
  <div class="search">
    <div class="search-container">
      <input 
        type="text" 
        bind:value={searchQuery}
        placeholder="Rechercher un livre par titre..."
        on:keydown={(e) => e.key === 'Enter' && handleSearch()}
      />
      <button on:click={handleSearch} aria-label="Rechercher">
        🔍
      </button>
    </div>
  </div>

  <div class="main">
    <!-- Left sidebar with filters -->
    <aside>
      <h2>Filtres</h2>
      
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
          <select bind:value={selectedAuthor} title="Sélectionner un auteur">
            <option value="ALL">Tous les auteurs</option>
            {#each authors as author}
              <option value={`${author.firstname || ''}|${author.surname || ''}`}>
                {(author.firstname || '') + ' ' + (author.surname || '')}
              </option>
            {/each}
          </select>
        </div>
      </div>
      
      <div class="section">
        <h3>Éditeur</h3>
        <div>
          <input 
            type="text" 
            bind:value={selectedPublisher} 
            placeholder="Nom de l'éditeur"
            class="publisher-input"
          />
        </div>
      </div>
      
      <button class="apply-filters" on:click={handleSearch}>
        Appliquer les filtres
      </button>
      
      <button class="reset" on:click={resetSearch}>
        Réinitialiser la recherche
      </button>
    </aside>
    
    <!-- Right side content area -->
    <div class="content">
      <div class="info-bar">
        <div>
          <span>Catalogue des livres</span>
        </div>
        <div class="sort-container">
          <label for="sort-select">Trier par:</label>
          <select id="sort-select" bind:value={sortOption} class="sort-select" on:change={handleSearch}>
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
          <p>Chargement des livres...</p>
        </div>
      {/if}
      
      <!-- Error state -->
      {#if error}
        <div class="error">
          <p>Erreur lors du chargement des données: {error}</p>
          <button on:click={resetSearch}>Réessayer</button>
        </div>
      {/if}
      
      <!-- Results display - cards -->
      <div class="results">
        {#if !isLoading && books.length === 0}
          <div class="no-results">
            <p>Aucun livre trouvé avec les critères actuels.</p>
          </div>
        {/if}
        
        {#each books as book (book.barcode)}
          <BookCard {book} />
        {/each}
      </div>
      
      <!-- Pagination controls -->
      {#if books.length > 0}
        <div class="pagination">
          <button 
            disabled={pageNumber === 0} 
            on:click={() => { pageNumber -= 1; handleSearch(); }}
            class="pagination-button"
          >
            &laquo; Précédent
          </button>
          
          <span class="page-info">Page {pageNumber + 1}</span>
          
          <button 
            disabled={books.length < pageSize}
            on:click={() => { pageNumber += 1; handleSearch(); }}
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
          
          label {
            display: flex;
            align-items: center;
            cursor: pointer;
            
            input {
              margin-right: 0.5rem;
            }
            
            span {
              color: #e0d6c2;
            }
          }
          
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
        
        div {
          color: #e0d6c2;
          font-size: 0.9rem;
          margin-left: 0.5rem;
          white-space: nowrap;
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
    .info-bar {
      flex-direction: column;
      gap: 0.75rem;
      align-items: flex-start;
      
      .sort-container {
        flex-wrap: wrap;
        width: 100%;
        
        div {
          margin-top: 0.5rem;
          width: 100%;
        }
      }
    }
  }
  
  .publisher-input {
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
</style>
