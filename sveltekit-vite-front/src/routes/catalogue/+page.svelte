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
  
  // Navigate to book details
  function handleBookClick(book) {
    // For now, we'll just console.log the book details
    console.log('Book selected:', book);
    // In a future implementation, we could navigate to a detail page
    // window.location.href = `/catalogue/books/${book.id}`;
  }
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
          description={book.description || "Pas de description disponible"}
          imageSrc={book.coverImage || "/hagitest.jpeg"}
          rating={4}
          onClick={() => handleBookClick(book)}
          altText={`Couverture de ${book.title}`}
          iconType="/icons/books.svg"
          frameColor="var(--dark-orange)"
        />
      {/each}
    </div>
  {/if}
</div>

<style>
  .books-catalogue {
    max-width: 1200px;
    margin: 2rem auto;
    padding: 0 1rem;
  }
  
  h1 {
    font-family: "Pirata One", system-ui;
    font-size: 3rem;
    color: var(--red);
    text-align: center;
    margin-bottom: 2rem;
  }
  
  .books-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 2rem;
  }
  
  .loading, .error-message, .no-books {
    text-align: center;
    margin: 3rem 0;
    padding: 2rem;
    background-color: rgba(0, 0, 0, 0.2);
    border-radius: 8px;
  }
  
  .loading-spinner {
    display: inline-block;
    width: 2rem;
    height: 2rem;
    margin-bottom: 1rem;
    border: 3px solid rgba(255, 255, 255, 0.3);
    border-radius: 50%;
    border-top-color: var(--red);
    animation: spin 1s linear infinite;
  }
  
  @keyframes spin {
    to {
      transform: rotate(360deg);
    }
  }
  
  .error-message {
    color: #ff6b6b;
  }
  
  .error-message button {
    margin-top: 1rem;
    background-color: var(--red);
    color: white;
    border: none;
    border-radius: 4px;
    padding: 0.5rem 1rem;
    cursor: pointer;
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
