<script>
  import CardArticle from '$lib/components/CardArticle.svelte';
  import { booksApi } from '$lib/api';
  
  // State
  let books = $state([]);
  let loading = $state(true);
  let error = $state(null);
  
  // Fetch books on component initialization using $effect
  $effect(async () => {
    try {
      books = await booksApi.getAll();
    } catch (err) {
      error = err.message;
      console.error('Error fetching books:', err);
    } finally {
      loading = false;
    }
  });
</script>

<div class="books-catalogue">
  <h1>Bibliothèque du CARTEL</h1>
  
  {#if loading}
    <div class="loading">
      <span class="loading-spinner"></span>
      <p>Chargement des livres...</p>
    </div>
  {:else if error}
    <div class="error-message">
      <p>Une erreur est survenue : {error}</p>
      <button onclick={() => window.location.reload()}>Réessayer</button>
    </div>
  {:else if books.length === 0}
    <div class="no-books">
      <p>Aucun livre trouvé.</p>
    </div>
  {:else}
    <div class="books-grid">
      {#each books as book}
        <CardArticle
          title={book.title}
          iconType="/icons/books.svg"
        />
      {/each}
    </div>
  {/if}
</div>

<style lang="scss">
  $max-width: 1200px;
  $spacing-standard: 2rem;
  $card-min-width: 300px;
  $border-radius: 8px;
  $animation-speed: 1s;
  
  .books-catalogue {
    max-width: $max-width;
    margin: $spacing-standard auto;
    padding: 0 1rem;
  }
  
  h1 {
    font-family: "Pirata One", system-ui;
    font-size: 3rem;
    color: var(--red);
    text-align: center;
    margin-bottom: $spacing-standard;
  }
  
  .books-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax($card-min-width, 1fr));
    gap: $spacing-standard;
  }
  
  .loading, .error-message, .no-books {
    text-align: center;
    margin: 3rem 0;
    padding: $spacing-standard;
    background-color: rgba(0, 0, 0, 0.2);
    border-radius: $border-radius;
  }
  
  .loading-spinner {
    display: inline-block;
    width: $spacing-standard;
    height: $spacing-standard;
    margin-bottom: 1rem;
    border: 3px solid rgba(255, 255, 255, 0.3);
    border-radius: 50%;
    border-top-color: var(--red);
    animation: spin $animation-speed linear infinite;
  }
  
  @keyframes spin {
    to {
      transform: rotate(360deg);
    }
  }
  
  .error-message {
    color: #ff6b6b;
    
    button {
      margin-top: 1rem;
      background-color: var(--red);
      color: white;
      border: none;
      border-radius: 4px;
      padding: 0.5rem 1rem;
      cursor: pointer;
    }
  }
  
  @media (max-width: 768px) {
    h1 {
      font-size: 2.5rem;
    }
    
    .books-grid {
      grid-template-columns: 1fr;
    }
  }
</style>
