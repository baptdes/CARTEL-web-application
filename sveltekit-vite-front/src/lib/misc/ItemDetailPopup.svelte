<script>
  import { createEventDispatcher } from 'svelte';
  const dispatch = createEventDispatcher();

  // Single $props() declaration with destructuring
  let { item, type = 'book', show = false } = $props();

  function closePopup() {
    show = false;
    dispatch('close');
  }

  // Close popup when clicking outside the content
  function handleBackdropClick(event) {
    if (event.target === event.currentTarget) {
      closePopup();
    }
  }

  // Handle escape key to close
  function handleKeydown(event) {
    if (event.key === 'Escape' && show) {
      closePopup();
    }
  }

  $effect(() => {
    // Add/remove keydown listener when show changes
    if (show) {
      window.addEventListener('keydown', handleKeydown);
    } else {
      window.removeEventListener('keydown', handleKeydown);
    }

    // Cleanup on component destroy
    return () => {
      window.removeEventListener('keydown', handleKeydown);
    };
  });
  
  // Function to get item cover image or appropriate placeholder
  function getCoverImage() {
    if (item?.coverImage) {
      return item.coverImage;
    } else {
      return type === 'book' ? "/placeholder_book.png" : "/placeholder_game.jpg";
    }
  }
  
  // Function to handle image error
  function handleImageError(event) {
    event.target.onerror = null; // Prevent infinite error loops
    event.target.src = type === 'book' ? "/placeholder_book.png" : "/placeholder_game.jpg";
  }
</script>

{#if show && item}
  <div class="popup-backdrop" on:click={handleBackdropClick}>
    <div class="popup-content {type}">
      
      <div class="item-header">
        <h2>{item.name}</h2>
        {#if item.barcode}
          <div class="barcode">Code: {item.barcode}</div>
        {/if}
      </div>
      
      <div class="item-details">
        {#if type === 'book'}
          <div class="item-image">
            <img 
              src={getCoverImage()} 
              alt={`Couverture de ${item.name}`}
              class="book-cover-img"
              on:error={handleImageError} 
            />
          </div>
          
          <div class="item-info">
            <!-- Book specific information -->
            {#if item.authors && item.authors.length}
              <div class="info-group">
                <h3>Auteur{item.authors.length > 1 ? 's' : ''}</h3>
                <p>{item.authors.map(a => `${a.firstname || ''} ${a.surname || ''}`).join(', ')}</p>
              </div>
            {/if}
            
            {#if item.illustrator && item.illustrator.length}
              <div class="info-group">
                <h3>Illustrateur{item.illustrator.length > 1 ? 's' : ''}</h3>
                <p>{item.illustrator.map(i => `${i.firstname || ''} ${i.surname || ''}`).join(', ')}</p>
              </div>
            {/if}

            {#if item.publicationYear}
              <div class="info-group">
                <h3>Année de publication</h3>
                <p>{item.publicationYear}</p>
              </div>
            {/if}

            {#if item.format}
              <div class="info-group">
                <h3>Format</h3>
                <p>
                  {item.format === 'LIVRE' ? 'Livre' : 
                   item.format === 'BD' ? 'Bande Dessinée' : 
                   item.format === 'MANGA' ? 'Manga' : item.format}
                </p>
              </div>
            {/if}

            {#if item.language}
              <div class="info-group">
                <h3>Langue</h3>
                <p>
                  {item.language === 'FR' ? 'Français' :
                   item.language === 'EN' ? 'Anglais' :
                   item.language === 'JA' ? 'Japonais' : item.language}
                </p>
              </div>
            {/if}
            
            {#if item.genres && item.genres.length}
              <div class="info-group">
                <h3>Genre{item.genres.length > 1 ? 's' : ''}</h3>
                <div class="genre-tags">
                  {#each item.genres as genre}
                    {#if genre.name}<span class="tag">{genre.name}</span>{/if}
                  {/each}
                </div>
              </div>
            {/if}

            {#if item.volumeNumber}
              <div class="info-group">
                <h3>Volume</h3>
                <p>{item.volumeNumber}</p>
              </div>
            {/if}
          </div>
        {:else}
          <!-- Game specific information -->
          <div class="item-image">
            <img 
              src={getCoverImage()}
              alt={`Image de ${item.name}`}
              class="game-cover-img"
              on:error={handleImageError} 
            />
          </div>
          
          <div class="item-info">
            {#if item.createdBy && item.createdBy.length}
              <div class="info-group">
                <h3>Créateur{item.createdBy.length > 1 ? 's' : ''}</h3>
                <p>{item.createdBy.map(c => `${c.firstname || ''} ${c.surname || ''}`).join(', ')}</p>
              </div>
            {/if}

            {#if item.publisher}
              <div class="info-group">
                <h3>Éditeur</h3>
                <p>{item.publisher}</p>
              </div>
            {/if}
            
            {#if item.publicationYear}
              <div class="info-group">
                <h3>Année de publication</h3>
                <p>{item.publicationYear}</p>
              </div>
            {/if}
            
            {#if item.minPlayers || item.maxPlayers}
              <div class="info-group">
                <h3>Nombre de joueurs</h3>
                <p>
                  {#if item.minPlayers && item.maxPlayers}
                    {item.minPlayers} - {item.maxPlayers} joueurs
                  {:else if item.minPlayers}
                    Min {item.minPlayers} joueurs
                  {:else if item.maxPlayers}
                    Max {item.maxPlayers} joueurs
                  {/if}
                </p>
              </div>
            {/if}
            
            {#if item.minPlaytime || item.maxPlaytime}
              <div class="info-group">
                <h3>Durée</h3>
                <p>
                  {#if item.minPlaytime && item.maxPlaytime}
                    {item.minPlaytime} - {item.maxPlaytime} minutes
                  {:else if item.minPlaytime}
                    Min {item.minPlaytime} minutes
                  {:else if item.maxPlaytime}
                    Max {item.maxPlaytime} minutes
                  {/if}
                </p>
              </div>
            {/if}
            
            {#if item.minAge}
              <div class="info-group">
                <h3>Âge minimum</h3>
                <p>{item.minAge} ans</p>
              </div>
            {/if}
            
            {#if item.category}
              <div class="info-group">
                <h3>Catégorie</h3>
                <div class="genre-tags">
                  <span class="tag">{item.category}</span>
                </div>
              </div>
            {/if}
          </div>
        {/if}
      </div>
      
      {#if item.description}
        <div class="item-description">
          <h3>Description</h3>
          <p>{item.description}</p>
        </div>
      {/if}
      
      <!-- Availability information could be added here -->
      <div class="availability-section">
        <h3>Disponibilité</h3>
        <p>
          {#if item.copies && item.copies.length > 0}
            {item.copies.filter(c => c.status === 'AVAILABLE').length} exemplaire(s) disponible(s) sur {item.copies.length}
          {:else}
            Information de disponibilité non disponible
          {/if}
        </p>
      </div>
      
      <div class="popup-actions">
        <button class="close-action" on:click={closePopup}>Fermer</button>
      </div>
    </div>
  </div>
{/if}

<style>
  .popup-backdrop {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.7);
    z-index: 1000;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20px;
    animation: fadeIn 0.2s ease-out;
  }
  
  .popup-content {
    background-color: #2a2a2a;
    border: 8px solid var(--dark-orange);
    border-radius: 10px;
    width: 100%;
    max-width: 700px;
    max-height: 90vh;
    overflow-y: auto;
    padding: 2rem;
    position: relative;
    box-shadow: 0 5px 20px rgba(0, 0, 0, 0.5);
    animation: slideUp 0.3s ease-out;
  }
  
  .close-button {
    position: absolute;
    top: 1rem;
    right: 1rem;
    background: var(--dark-orange);
    color: white;
    font-size: 1.5rem;
    width: 35px;
    height: 35px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: background-color 0.2s;
    border: none;
    border-radius: 50%;
  }
  
  .close-button:hover {
    background-color: var(--orange);
  }

  .item-header {
    margin-bottom: 1.5rem;
    padding-bottom: 1rem;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  }
  
  .item-header h2 {
    color: var(--white);
    font-size: 1.8rem;
    margin: 0;
    font-family: "Pirata One", cursive;
  }
  
  .barcode {
    color: #e0d6c2;
    font-size: 0.9rem;
    opacity: 0.7;
    margin-top: 0.25rem;
  }
  
  .item-details {
    display: flex;
    gap: 2rem;
    margin-bottom: 2rem;
  }
  
  .item-image {
    flex: 0 0 180px;
    position: relative;
  }
  
  .book-cover-img, .game-cover-img {
    width: 100%;
    object-fit: cover;
    border: 3px solid var(--dark-orange);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
    border-radius: 6px;
    background-color: #444;
  }
  
  .book-cover-img {
    height: 260px;
  }
  
  .game-cover-img {
    height: 180px;
  }
  
  .item-info {
    flex: 1;
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
    gap: 1.5rem;
  }
  
  .info-group h3 {
    color: var(--orange);
    font-size: 1rem;
    margin: 0 0 0.5rem 0;
  }
  
  .info-group p {
    color: #e0d6c2;
    margin: 0;
    font-size: 0.95rem;
  }
  
  .genre-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;
  }
  
  .tag {
    background-color: #444;
    color: #e0d6c2;
    padding: 0.25rem 0.75rem;
    border-radius: 20px;
    font-size: 0.85rem;
    display: inline-block;
  }
  
  .item-description {
    margin-bottom: 2rem;
    padding-top: 1rem;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
  }
  
  .item-description h3 {
    color: var(--white);
    font-size: 1.2rem;
    margin: 0 0 1rem 0;
  }
  
  .item-description p {
    color: #e0d6c2;
    line-height: 1.6;
    margin: 0;
    white-space: pre-line;
  }
  
  .availability-section {
    padding-top: 1rem;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
  }
  
  .availability-section h3 {
    color: var(--white);
    font-size: 1.2rem;
    margin: 0 0 1rem 0;
  }
  
  .availability-section p {
    color: #e0d6c2;
    margin: 0;
  }
  
  .popup-actions {
    margin-top: 2rem;
    display: flex;
    justify-content: flex-end;
  }
  
  .close-action {
    background-color: #444;
    color: #e0d6c2;
    border: none;
    padding: 0.75rem 1.5rem;
    border-radius: 4px;
    font-size: 0.95rem;
    cursor: pointer;
    transition: background-color 0.2s;
  }
  
  .close-action:hover {
    background-color: #555;
  }
  
  @keyframes fadeIn {
    from { opacity: 0; }
    to { opacity: 1; }
  }
  
  @keyframes slideUp {
    from { transform: translateY(30px); opacity: 0; }
    to { transform: translateY(0); opacity: 1; }
  }
  
  @media (max-width: 768px) {
    .item-details {
      flex-direction: column;
    }
    
    .item-info {
      grid-template-columns: 1fr;
    }
    
    .item-image {
      display: flex;
      justify-content: center;
      margin-bottom: 1.5rem;
    }
    
    .book-cover-img, .game-cover-img {
      max-width: 180px;
    }
  }
</style>