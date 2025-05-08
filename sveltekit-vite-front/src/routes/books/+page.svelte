<script>
  import PointBar from '$lib/components/PointBar.svelte';
  import BookCard from '$lib/components/BookCard.svelte';
  import { onMount } from 'svelte';
  import { fetchBooks, fetchFormats } from '$lib/services/bookService';
  
  // State variables
  let books = $state([]);
  let categories = $state(["Toutes"]);
  let isLoading = $state(true);
  let error = $state(null);
  let searchQuery = $state('');

  // Handle search
  async function handleSearch() {
    isLoading = true;
    error = null;
    try {
      books = await fetchBooks({ title: searchQuery });
    } catch (err) {
      error = err.message || 'Failed to search books';
    } finally {
      isLoading = false;
    }
  }
  
  // Reset search
  async function resetSearch() {
    searchQuery = '';
    isLoading = true;
    error = null;
    try {
      books = await fetchBooks();
    } catch (err) {
      error = err.message || 'Failed to reset search';
    } finally {
      isLoading = false;
    }
  }
  
  // Initialize component
  onMount(async () => {
    try {
      // Fetch formats
      const formats = await fetchFormats();
      categories = ["Toutes", ...formats];
      
      // Fetch books
      books = await fetchBooks();
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
    <div>
      <input 
        type="text" 
        bind:value={searchQuery}
        placeholder="Rechercher un livre par titre..."
      />
      <button on:click={handleSearch}>
        🔍
      </button>
    </div>
  </div>

  <div class="main">
    <!-- Left sidebar with filters - Will be implemented with API later -->
    <aside>
      <h2>Filtres</h2>
      
      <div class="section">
        <h3>Format</h3>
        <div>
          <select disabled title="Cette fonctionnalité sera bientôt disponible">
            {#each categories as category}
              <option value={category}>{category}</option>
            {/each}
          </select>
          <small class="future-note">Filtre par API à venir</small>
        </div>
      </div>
      
      <div class="section">
        <h3>Période de publication</h3>
        <div>
          <label>
            <input type="radio" name="period" value="all" disabled />
            <span>Toutes</span>
          </label>
          <label>
            <input type="radio" name="period" value="before1950" disabled />
            <span>Avant 1950</span>
          </label>
          <label>
            <input type="radio" name="period" value="1950-1999" disabled />
            <span>1950-1999</span>
          </label>
          <label>
            <input type="radio" name="period" value="after2000" disabled />
            <span>2000-présent</span>
          </label>
          <small class="future-note">Filtre par API à venir</small>
        </div>
      </div>
      
      <div class="section">
        <h3>Disponibilité</h3>
        <div>
          <label>
            <input type="radio" name="availability" value="all" disabled />
            <span>Tous</span>
          </label>
          <label>
            <input type="radio" name="availability" value="available" disabled />
            <span>Disponible</span>
          </label>
          <label>
            <input type="radio" name="availability" value="unavailable" disabled />
            <span>Non disponible</span>
          </label>
          <small class="future-note">Filtre par API à venir</small>
        </div>
      </div>
      
      <button class="reset" on:click={resetSearch}>
        Réinitialiser la recherche
      </button>
    </aside>
    
    <!-- Right side content area -->
    <div class="content">
      <!-- Basic info bar -->
      <div class="info-bar">
        <div>
          <span>Catalogue des livres</span>
        </div>
        <div>
          {books.length} résultats
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
    background-image: url('/textures/dark-brown-old-stone-wall.jpg');
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
      background-color: rgba(0, 0, 0, 0.7);
      z-index: 0;
    }
    
    .search {
      padding: 1rem;
      z-index: 100;
      position: relative;
      margin-bottom: 1.5rem;
      border-radius: 0 0 8px 8px;
      
      div {
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
        
        button {
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
      
      .section {
        margin-bottom: 1.5rem;
        padding-bottom: 1.5rem;
        border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        
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
              color: var(--white);
            }
          }
          
          select {
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
      background-color: var(--bg-card);
      border-radius: 8px;
      margin-bottom: 1.5rem;
      
      span {
        color: var(--white);
        font-size: 1.1rem;
      }
      
      div:last-child {
        color: var(--white);
        font-size: 0.9rem;
      }
    }
    
    .results {
      display: flex;
      flex-direction: column;
      gap: 1.5rem;
      padding-bottom: 1.5rem;
    }
  }
  
  // Loading and error states
  .loading, .error, .no-results {
    padding: 2rem;
    text-align: center;
    background-color: var(--bg-card);
    border-radius: 8px;
    margin: 1rem 0;
    
    .spinner {
      margin: 0 auto 1rem;
      width: 40px;
      height: 40px;
      border: 3px solid rgba(255, 255, 255, 0.2);
      border-radius: 50%;
      border-top-color: var(--orange);
      animation: spin 1s linear infinite;
    }
    
    p {
      color: var(--white);
      margin: 0;
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
  
  .error {
    border: 1px solid var(--red);
    
    p {
      color: var(--red);
    }
  }
  
  .no-results {
    padding: 3rem;
    
    p {
      font-size: 1.1rem;
      color: #aaa;
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
  
  .future-note {
    color: #999;
    font-size: 0.8rem;
    font-style: italic;
    margin-top: 0.25rem;
    display: block;
  }
</style>
