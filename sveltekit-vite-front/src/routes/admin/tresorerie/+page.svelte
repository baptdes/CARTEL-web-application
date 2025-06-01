<script>
  import { onMount } from 'svelte';
  import { goto } from '$app/navigation';
  import { adminPageState } from '../store.js';
  import DoubleText from "$lib/misc/DoubleText.svelte";
  import PointBar from "$lib/misc/PointBar.svelte";
  import { createFacture, getAllFactures, deleteFacture, getFactureById } from '$lib/services/factureService.js';
  import { fetchBooks } from '$lib/services/bookService.js';
  import { fetchGames } from '$lib/services/gameService.js';
  import { isAuthenticated, logout } from '$lib/auth';

  // State for factures data
  let factures = $state([]);
  let isLoading = $state(true);
  let error = $state(null);
  let isAuthError = $state(false);

  // Popup state
  let showAddFacturePopup = $state(false);
  let isSubmitting = $state(false);
  let submitError = $state(null);
  let submitSuccess = $state(false);

  // Facture detail popup state
  let showFactureDetailPopup = $state(false);
  let selectedFacture = $state(null);

  // Form fields
  let factureName = $state('');
  let searchQuery = $state('');
  let searchResults = $state([]);
  let selectedItems = $state([]);
  let searchType = $state('books'); // 'books' or 'games'
  
  // Initialize component
  onMount(async () => {
    await loadFactures();
  });

  // Load all factures
  async function loadFactures() {
    isLoading = true;
    error = null;
    isAuthError = false;
    try {
      factures = await getAllFactures();
      // Sort by date desc
      factures.sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt));
      console.log('Loaded factures:', factures);
    } catch (err) {
      // Check if it's an auth error (403 Forbidden)
      if (err.status === 403) {
        isAuthError = true;
        error = "Vous n'avez pas les droits nécessaires pour accéder à cette page. Veuillez vous reconnecter.";
      } else {
        error = `Erreur lors du chargement des factures: ${err.message || err}`;
      }
    } finally {
      isLoading = false;
    }
  }

  // Handle authentication error
  function handleAuthError() {
    // Log out the user and redirect to login page
    logout();
    goto('/login');
  }
  
  // Search for books or games
  async function handleSearch() {
    if (!searchQuery.trim()) {
      searchResults = [];
      return;
    }
    
    try {
      console.log(`Searching for ${searchType} with query "${searchQuery}"`);
      
      if (searchType === 'books') {
        // Book search params - titleBook is the correct parameter name
        const params = {
          titleBook: searchQuery.trim(),
          pageNumber: 0,
          pageSize: 10,
          sortBy: 'name',
          asc: true
        };
        console.log('Book search params:', params);
        const result = await fetchBooks(params);
        console.log('Book search result:', result);
        searchResults = result.content || result || [];
      } else {
        // Game search params - titleGame is the correct parameter name
        const params = {
          titleGame: searchQuery.trim(),
          pageNumber: 0,
          pageSize: 10,
          sortBy: 'name',
          asc: true
        };
        console.log('Game search params:', params);
        const result = await fetchGames(params);
        console.log('Game search result:', result);
        searchResults = result.content || result || [];
      }
    } catch (err) {
      console.error('Search error:', err);
      searchResults = [];
    }
  }

  // Add item to selected items
  function addItem(item) {
    // Check if item already exists in selected items
    if (!selectedItems.some(selectedItem => selectedItem.barcode === item.barcode)) {
      selectedItems = [...selectedItems, {
        ...item,
        id: item.barcode, // Use barcode as ID
        type: searchType === 'books' ? 'BOOK' : 'GAME',
        quantity: 1
      }];
    }
    searchQuery = '';
    searchResults = [];
  }

  // Remove item from selected items
  function removeItem(itemId) {
    selectedItems = selectedItems.filter(item => item.id !== itemId);
  }

  // Update item quantity
  function updateQuantity(itemId, delta) {
    selectedItems = selectedItems.map(item => {
      if (item.id === itemId) {
        const newQuantity = Math.max(1, item.quantity + delta);
        return { ...item, quantity: newQuantity };
      }
      return item;
    });
  }

  // Open add facture popup
  function openAddFacturePopup() {
    factureName = '';
    searchQuery = '';
    searchResults = [];
    selectedItems = [];
    submitError = null;
    submitSuccess = false;
    showAddFacturePopup = true;
  }

  // Close add facture popup
  function closeAddFacturePopup() {
    showAddFacturePopup = false;
  }

  // Open facture detail popup
  async function openFactureDetailPopup(facture) {
    // If we need to fetch detailed facture info
    try {
      selectedFacture = facture;
      showFactureDetailPopup = true;
    } catch (err) {
      error = `Erreur lors du chargement des détails de la facture: ${err.message || err}`;
    }
  }

  // Close facture detail popup
  function closeFactureDetailPopup() {
    showFactureDetailPopup = false;
    selectedFacture = null;
  }

  // Handle facture submission
  async function submitFacture() {
    if (!factureName.trim()) {
      submitError = "Veuillez donner un nom à la facture";
      return;
    }

    if (selectedItems.length === 0) {
      submitError = "Veuillez ajouter au moins un article à la facture";
      return;
    }

    isSubmitting = true;
    submitError = null;
    
    try {
      const factureData = {
        filename: factureName.trim(),
        items: selectedItems.map(item => ({
          itemId: item.barcode || item.id, // Try barcode first, then fallback to id
          itemType: item.type,
          quantity: item.quantity
        }))
      };

      console.log('Submitting facture:', factureData);
      await createFacture(factureData);
      submitSuccess = true;
      
      // Reload factures after a short delay
      setTimeout(async () => {
        await loadFactures();
        closeAddFacturePopup();
      }, 1500);
    } catch (err) {
      submitError = `Erreur lors de la création de la facture: ${err.message || err}`;
    } finally {
      isSubmitting = false;
    }
  }
  
  // Handle delete facture
  async function handleDeleteFacture(factureId) {
    if (confirm("Êtes-vous sûr de vouloir supprimer cette facture ?")) {
      try {
        await deleteFacture(factureId);
        await loadFactures();
      } catch (err) {
        error = `Erreur lors de la suppression de la facture: ${err.message || err}`;
      }
    }
  }

  // Format date for display
  function formatDate(dateString) {
    if (!dateString) return "N/A";
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('fr-FR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    }).format(date);
  }

  // Calculate total for a facture
  function calculateTotal(items) {
    if (!items || !items.length) return 0;
    return items.reduce((sum, item) => sum + (item.price || 0) * (item.quantity || 1), 0);
  }

  // Get item type display name
  function getItemTypeName(item) {
    if (!item) return "Inconnu";
    
    if (item.format) {
      // Book formats
      if (item.format === 'LIVRE') return 'Livre';
      if (item.format === 'BD') return 'Bande Dessinée';
      if (item.format === 'MANGA') return 'Manga';
      return item.format;
    }
    
    // Try to guess from item properties
    if (item.authors) return 'Livre';
    if (item.minPlayers) return 'Jeu';
    
    return "Article";
  }

  // Handle search input keydown
  function handleSearchKeydown(event) {
    if (event.key === 'Enter') {
      event.preventDefault();
      handleSearch();
    }
  }

  // Auto-search on query change (debounced)
  let searchTimeout;
  $effect(() => {
    clearTimeout(searchTimeout);
    if (searchQuery.trim()) {
      searchTimeout = setTimeout(handleSearch, 300);
    } else {
      searchResults = [];
    }
  });
</script>

<main>
  <DoubleText text="Trésorerie" size="4em" />
  <PointBar Color="var(--accent)" width="70%" />

  <div class="tresorerie-container">
    <!-- Header Actions -->
    <div class="header-actions">
      <button class="admin-button add-facture-btn" on:click={openAddFacturePopup}>
        <span>+</span> Nouvelle Facture
      </button>
      <button class="return-button" type="button" on:click={() => { $adminPageState = 0; goto('/admin'); }}>
        Retour
      </button>
    </div>

    <!-- Loading State -->
    {#if isLoading}
      <div class="loading-spinner">
        <div class="spinner"></div>
        <p>Chargement des factures...</p>
      </div>
    <!-- Error Message -->
    {:else if error}
      <div class="error-message">
        {error}
        {#if isAuthError}
          <div class="auth-error-actions">
            <button class="admin-button" on:click={handleAuthError}>Se reconnecter</button>
          </div>
        {:else}
          <button class="admin-button" on:click={loadFactures}>Réessayer</button>
        {/if}
      </div>
    {:else}
      <!-- Factures List -->
      <div class="factures-list">
        <h2>Factures</h2>
        
        {#if factures.length === 0}
          <div class="empty-state">
            <p>Aucune facture trouvée</p>
            <button class="admin-button" on:click={openAddFacturePopup}>Créer une facture</button>
          </div>
        {:else}
          <div class="factures-grid">
            <div class="grid-header">
              <div class="grid-cell">Nom</div>
              <div class="grid-cell">Date</div>
              <div class="grid-cell">Nombre d'articles</div>
              <div class="grid-cell">Actions</div>
            </div>
            
            {#each factures as facture}
              <div class="grid-row">
                <div class="grid-cell facture-name" on:click={() => openFactureDetailPopup(facture)} on:keydown={() => {}}>
                  {facture.filename || "Sans nom"}
                </div>
                <div class="grid-cell">{formatDate(facture.updatedAt)}</div>
                <div class="grid-cell">{facture.items ? facture.items.length : 0}</div>
                <div class="grid-cell actions-cell">
                  <button 
                    class="action-btn delete-btn" 
                    on:click={() => handleDeleteFacture(facture.id)}
                    title="Supprimer"
                  >
                    🗑️
                  </button>
                </div>
              </div>
            {/each}
          </div>
        {/if}
      </div>
    {/if}
  </div>
</main>

<!-- Add Facture Popup -->
{#if showAddFacturePopup}
  <div class="popup-backdrop" on:click={closeAddFacturePopup} on:keydown={() => {}}>
    <div class="popup-content" on:click|stopPropagation on:keydown={() => {}}>
      <h3>Créer une nouvelle facture</h3>
      
      {#if submitSuccess}
        <div class="success-message">
          Facture créée avec succès!
        </div>
      {:else}
        <div class="facture-form">
          <!-- Facture Name -->
          <div class="form-section">
            <label class="full-width">
              Nom de la facture*:
              <input type="text" bind:value={factureName} required placeholder="Entrez un nom pour la facture" />
            </label>
          </div>
          
          <!-- Item Search -->
          <div class="form-section">
            <h4>Recherche d'articles</h4>
            
            <div class="search-type-toggle">
              <button 
                class="toggle-btn" 
                class:active={searchType === 'books'}
                on:click={() => { searchType = 'books'; searchResults = []; }}
              >
                Livres
              </button>
              <button 
                class="toggle-btn" 
                class:active={searchType === 'games'}
                on:click={() => { searchType = 'games'; searchResults = []; }}
              >
                Jeux
              </button>
            </div>

            <div class="search-container">
              <input 
                type="text" 
                bind:value={searchQuery} 
                placeholder={searchType === 'books' ? "Rechercher un livre..." : "Rechercher un jeu..."}
                on:keydown={handleSearchKeydown}
              />
              <button class="search-btn" on:click={handleSearch}>🔍</button>
            </div>

            <!-- Search Results -->
            {#if searchResults.length > 0}
              <div class="search-results">
                {#each searchResults as item}
                  <div class="search-result-item" on:click={() => addItem(item)} on:keydown={() => {}}>
                    <div class="item-image">
                      <img 
                        src={item.coverImage || "/placeholder_book.png"} 
                        alt={item.name || "Article"} 
                      />
                    </div>
                    <div class="item-details">
                      <div class="item-name">{item.name || "Sans titre"}</div>
                      <div class="item-info">
                        {#if searchType === 'books' && item.authors && item.authors.length}
                          {item.authors.map(a => `${a.firstname || ''} ${a.surname || ''}`).join(', ')}
                        {:else if searchType === 'games' && item.creator}
                          {item.creator.firstname} {item.creator.surname}
                        {:else}
                          &nbsp;
                        {/if}
                      </div>
                    </div>
                  </div>
                {/each}
              </div>
            {:else if searchQuery && !isLoading}
              <div class="no-results">Aucun résultat trouvé</div>
            {/if}
          </div>
          
          <!-- Selected Items -->
          <div class="form-section">
            <h4>Articles sélectionnés</h4>
            
            {#if selectedItems.length === 0}
              <div class="no-selected-items">
                Aucun article sélectionné
              </div>
            {:else}
              <div class="selected-items-list">
                {#each selectedItems as item}
                  <div class="selected-item">
                    <div class="item-image">
                      <img 
                        src={item.coverImage || "/placeholder_book.png"} 
                        alt={item.name || "Article"} 
                      />
                    </div>
                    <div class="item-details">
                      <div class="item-name">{item.name || "Sans titre"}</div>
                      <div class="item-type">{item.type === 'BOOK' ? 'Livre' : 'Jeu'}</div>
                    </div>
                    <div class="item-quantity">
                      <button class="qty-btn minus" on:click={() => updateQuantity(item.id, -1)}>-</button>
                      <span>{item.quantity}</span>
                      <button class="qty-btn plus" on:click={() => updateQuantity(item.id, 1)}>+</button>
                    </div>
                    <button class="remove-btn" on:click={() => removeItem(item.id)}>✕</button>
                  </div>
                {/each}
              </div>
            {/if}
          </div>
          
          {#if submitError}
            <div class="error-message">{submitError}</div>
          {/if}
          
          <div class="form-actions">
            <button 
              type="button" 
              class="submit-btn" 
              disabled={isSubmitting} 
              on:click={submitFacture}
            >
              {isSubmitting ? 'Création en cours...' : 'Créer la facture'}
            </button>
            <button type="button" class="cancel-btn" on:click={closeAddFacturePopup}>
              Annuler
            </button>
          </div>
        </div>
      {/if}
    </div>
  </div>
{/if}

{#if showFactureDetailPopup && selectedFacture}
  <div class="popup-backdrop" on:click={closeFactureDetailPopup} on:keydown={() => {}}>
    <div class="popup-content facture-detail-popup" on:click|stopPropagation on:keydown={() => {}}>
      <h3>Détails de la facture : {selectedFacture.filename || "Sans nom"}</h3>
      
      <div class="facture-detail">
        <div class="facture-info">
          <div class="info-row">
            <span class="info-label">Date:</span> {formatDate(selectedFacture.updatedAt)}
          </div>
          <div class="info-row">
            <span class="info-label">Nombre d'articles:</span> {selectedFacture.items ? selectedFacture.items.length : 0}
          </div>
        </div>
        
        <h4>Articles</h4>
        
        {#if selectedFacture.items && selectedFacture.items.length > 0}
          <div class="items-detail-container">
            {#each selectedFacture.items as item}
              <div class="item-detail-card">
                <div class="item-card-header">
                  <div class="item-image">
                    <img src={item.coverImage || "/placeholder_book.png"} alt={item.name} />
                  </div>
                  <div class="item-header-info">
                    <h4>{item.name || "Sans nom"}</h4>
                    <div class="item-type-badge">{getItemTypeName(item)}</div>
                  </div>
                </div>
                
                <div class="item-details-grid">
                  <!-- Common information for all items -->
                  <div class="detail-item">
                    <span class="detail-label">Code-barres:</span>
                    <span class="detail-value">{item.barcode || "N/A"}</span>
                  </div>
                  <div class="detail-item">
                    <span class="detail-label">Année de publication:</span>
                    <span class="detail-value">{item.publicationYear || "N/A"}</span>
                  </div>
                  <div class="detail-item">
                    <span class="detail-label">Langue:</span>
                    <span class="detail-value">{item.language || "N/A"}</span>
                  </div>
                  
                  <!-- Book-specific information -->
                  {#if item.format && (item.format === 'LIVRE' || item.format === 'BD' || item.format === 'MANGA')}
                    <div class="detail-item">
                      <span class="detail-label">Format:</span>
                      <span class="detail-value">{item.format}</span>
                    </div>
                    {#if item.authors && item.authors.length}
                      <div class="detail-item">
                        <span class="detail-label">Auteur(s):</span>
                        <span class="detail-value">
                          {item.authors.map(a => `${a.firstname || ''} ${a.surname || ''}`).join(', ')}
                        </span>
                      </div>
                    {/if}
                    {#if item.publisher}
                      <div class="detail-item">
                        <span class="detail-label">Éditeur:</span>
                        <span class="detail-value">{item.publisher.name || "N/A"}</span>
                      </div>
                    {/if}
                    {#if item.series}
                      <div class="detail-item">
                        <span class="detail-label">Série:</span>
                        <span class="detail-value">{item.series.name || "N/A"}</span>
                      </div>
                    {/if}
                    {#if item.volumeNumber}
                      <div class="detail-item">
                        <span class="detail-label">Tome:</span>
                        <span class="detail-value">{item.volumeNumber}</span>
                      </div>
                    {/if}
                  
                  <!-- Game-specific information -->
                  {:else if item.minPlayers || item.maxPlayers}
                    {#if item.minPlayers && item.maxPlayers}
                      <div class="detail-item">
                        <span class="detail-label">Joueurs:</span>
                        <span class="detail-value">
                          {item.minPlayers === item.maxPlayers 
                            ? `${item.minPlayers} joueur${item.minPlayers > 1 ? 's' : ''}` 
                            : `${item.minPlayers} - ${item.maxPlayers} joueurs`}
                        </span>
                      </div>
                    {/if}
                    {#if item.avgPlaytime}
                      <div class="detail-item">
                        <span class="detail-label">Durée:</span>
                        <span class="detail-value">{item.avgPlaytime} min</span>
                      </div>
                    {/if}
                    {#if item.creator}
                      <div class="detail-item">
                        <span class="detail-label">Créateur:</span>
                        <span class="detail-value">
                          {item.creator.firstname} {item.creator.surname}
                        </span>
                      </div>
                    {/if}
                    {#if item.categories && item.categories.length}
                      <div class="detail-item">
                        <span class="detail-label">Catégories:</span>
                        <span class="detail-value">{item.categories.join(', ')}</span>
                      </div>
                    {/if}
                  {/if}
                </div>
                
                {#if item.description}
                  <div class="item-description">
                    <span class="description-label">Description:</span>
                    <p>{item.description}</p>
                  </div>
                {/if}
              </div>
            {/each}
          </div>
        {:else}
          <div class="no-items">Cette facture ne contient aucun article.</div>
        {/if}
      </div>
      
      <div class="popup-actions">
        <button type="button" class="close-btn" on:click={closeFactureDetailPopup}>
          Fermer
        </button>
      </div>
    </div>
  </div>
{/if}

<style lang="scss">
  @use "/src/lib/sass/base";
  
  main {
    flex: auto;
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  
  .tresorerie-container {
    width: 90%;
    max-width: 1200px;
    margin: 0 auto;
    padding: 1rem;
  }
  
  .header-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.5rem;
    
    .add-facture-btn {
      background: var(--accent);
      color: white;
      
      span {
        font-size: 1.2em;
        margin-right: 0.3em;
      }
    }
  }

  .auth-error-actions {
    margin-top: 1rem;
    display: flex;
    justify-content: center;
  }
  
  .loading-spinner {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    min-height: 200px;
    
    .spinner {
      width: 40px;
      height: 40px;
      border: 4px solid var(--secondary);
      border-top-color: var(--accent);
      border-radius: 50%;
      animation: spin 1s linear infinite;
      margin-bottom: 1rem;
    }
    
    @keyframes spin {
      to { transform: rotate(360deg); }
    }
  }
  
  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 3rem;
    text-align: center;
    background: rgba(0, 0, 0, 0.05);
    border-radius: 8px;
    
    p {
      color: var(--secondary);
      margin-bottom: 1.5rem;
      font-size: 1.2rem;
    }
  }
  
  .factures-list {
    h2 {
      color: var(--primary);
      margin-bottom: 1rem;
      font-size: 1.8rem;
    }
  }
  
  .factures-grid {
    background: rgba(0, 0, 0, 0.05);
    border-radius: 8px;
    overflow: hidden;
    
    .grid-header {
      display: grid;
      grid-template-columns: 2fr 1fr 1fr 0.5fr;
      background: var(--primary);
      color: black;
      font-weight: 600;
      
      .grid-cell {
        padding: 1rem;
        text-align: center;
      }
    }
    
    .grid-row {
      display: grid;
      grid-template-columns: 2fr 1fr 1fr 0.5fr;
      border-bottom: 1px solid white;
      color: var(--primary); // Added explicit text color for better contrast
      
      &:last-child {
        border-bottom: none;
      }
      
      &:nth-child(even) {
        background: rgba(0, 0, 0, 0.03);
      }
      
      .grid-cell {
        padding: 1rem;
        display: flex;
        align-items: center;
        justify-content: center;
      }
      
      .actions-cell {
        display: flex;
        gap: 0.5rem;
        justify-content: center;
      }
    }
  }
  
  .facture-name {
    cursor: pointer;
    color: var(--primary);
    text-decoration: underline;
    transition: color 0.2s;
    
    &:hover {
      color: var(--accent);
    }
  }
  
  .action-btn {
    background: none;
    border: none;
    cursor: pointer;
    font-size: 1.2rem;
    
    &.delete-btn:hover {
      color: var(--accent);
    }
  }
  
  .popup-backdrop {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.7);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
  }
  
  .popup-content {
    background-color: var(--tertiary);
    color: var(--primary);
    border: 2px solid var(--accent);
    border-radius: 8px;
    padding: 2rem;
    width: 90%;
    max-width: 800px;
    max-height: 90vh;
    overflow-y: auto;
    position: relative;
    
    h3 {
      color: var(--accent);
      margin-top: 0;
      text-align: center;
      margin-bottom: 1.5rem;
      font-size: 1.8rem;
    }
  }

  .facture-detail {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
    
    .facture-info {
      background-color: rgba(0, 0, 0, 0.05);
      border-radius: 4px;
      padding: 1rem;
      
      .info-row {
        margin-bottom: 0.5rem;
        
        &:last-child {
          margin-bottom: 0;
        }
        
        .info-label {
          font-weight: 600;
          margin-right: 0.5rem;
        }
      }
    }
    
    h4 {
      color: var(--primary);
      margin: 0.5rem 0;
      font-size: 1.2rem;
    }
  }
  
  .facture-form {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
    
    .form-section {
      border: 1px solid var(--secondary);
      border-radius: 4px;
      padding: 1rem;
      
      h4 {
        color: var(--primary);
        margin-top: 0;
        margin-bottom: 1rem;
        font-size: 1.2rem;
      }
    }
    
    label {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
      
      &.full-width {
        width: 100%;
      }
    }
    
    input {
      padding: 0.6rem;
      background-color: var(--tertiary);
      border: 1px solid var(--secondary);
      border-radius: 4px;
      color: var(--primary);
      
      &:focus {
        outline: none;
        border-color: var(--accent);
      }
    }
  }
  
  .items-grid {
    border: 1px solid var(--secondary);
    border-radius: 4px;
    overflow: hidden;
    
    .items-header {
      display: grid;
      grid-template-columns: 80px 2fr 1fr 1fr;
      background: var(--primary);
      color: black;
      font-weight: 600;
      
      .item-cell {
        padding: 0.8rem;
        text-align: center;
      }
    }
    
    .item-row {
      display: grid;
      grid-template-columns: 80px 2fr 1fr 1fr;
      border-bottom: 1px solid var(--secondary);
      
      &:last-child {
        border-bottom: none;
      }
      
      &:nth-child(even) {
        background: rgba(0, 0, 0, 0.03);
      }
      
      .item-cell {
        padding: 0.8rem;
        display: flex;
        align-items: center;
        justify-content: center;
        
        img {
          width: 50px;
          height: 70px;
          object-fit: cover;
          border-radius: 4px;
        }
      }
    }
  }

  .no-items {
    text-align: center;
    padding: 1.5rem;
    color: var(--secondary);
    background-color: rgba(0, 0, 0, 0.05);
    border-radius: 4px;
  }

  .popup-actions {
    display: flex;
    justify-content: center;
    margin-top: 2rem;
    
    .close-btn {
      padding: 0.8rem 2rem;
      border-radius: 4px;
      background: var(--accent);
      color: white;
      border: none;
      font-weight: 600;
      cursor: pointer;
      
      &:hover {
        filter: brightness(0.9);
      }
    }
  }
  
  .search-type-toggle {
    display: flex;
    margin-bottom: 1rem;
    
    .toggle-btn {
      flex: 1;
      padding: 0.5rem;
      border: 1px solid var(--secondary);
      background: var(--tertiary);
      color: var(--secondary);
      cursor: pointer;
      
      &:first-child {
        border-top-left-radius: 4px;
        border-bottom-left-radius: 4px;
      }
      
      &:last-child {
        border-top-right-radius: 4px;
        border-bottom-right-radius: 4px;
      }
      
      &.active {
        background: var(--accent);
        color: white;
        border-color: var(--accent);
      }
    }
  }
  
  .search-container {
    display: flex;
    gap: 0.5rem;
    margin-bottom: 1rem;
    
    input {
      flex: 1;
    }
    
    .search-btn {
      padding: 0.6rem;
      background: var(--primary);
      border: none;
      border-radius: 4px;
      color: white;
      cursor: pointer;
    }
  }
  
  .search-results {
    border: 1px solid var(--secondary);
    border-radius: 4px;
    max-height: 300px;
    overflow-y: auto;
    
    .search-result-item {
      display: flex;
      padding: 0.5rem;
      border-bottom: 1px solid var(--secondary);
      cursor: pointer;
      transition: background 0.2s;
      color: var(--primary); // Added explicit text color
      
      &:last-child {
        border-bottom: none;
      }
      
      &:hover {
        background: rgba(0, 0, 0, 0.05);
      }
      
      .item-image {
        width: 40px;
        height: 60px;
        margin-right: 1rem;
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          border-radius: 4px;
        }
      }
      
      .item-details {
        flex: 1;
        
        .item-name {
          font-weight: 600;
          margin-bottom: 0.3rem;
          color: var(--primary); // Explicit color
        }
        
        .item-info {
          font-size: 0.8rem;
          color: var(--secondary);
        }
      }
    }
  }
  
  .selected-items-list {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    
    .selected-item {
      display: flex;
      align-items: center;
      padding: 0.5rem;
      background: rgba(0, 0, 0, 0.03);
      border-radius: 4px;
      color: var(--primary); // Added explicit text color
      
      .item-image {
        width: 40px;
        height: 60px;
        margin-right: 1rem;
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          border-radius: 4px;
        }
      }
      
      .item-details {
        flex: 1;
        
        .item-name {
          font-weight: 600;
          color: var(--primary); // Explicit color
        }
        
        .item-type {
          font-size: 0.8rem;
          color: var(--secondary);
        }
      }
      
      .item-quantity {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        margin: 0 1rem;
        
        .qty-btn {
          width: 25px;
          height: 25px;
          display: flex;
          align-items: center;
          justify-content: center;
          background: var(--primary);
          color: white;
          border: none;
          border-radius: 50%;
          cursor: pointer;
          
          &:hover {
            background: var(--accent);
          }
        }
      }
      
      .remove-btn {
        background: none;
        border: none;
        color: var(--accent);
        font-size: 1.2rem;
        cursor: pointer;
        padding: 0.3rem;
      }
    }
  }
  
  .no-selected-items, .no-results {
    text-align: center;
    padding: 1rem;
    color: var(--secondary);
  }
  
  .form-actions {
    display: flex;
    justify-content: center;
    gap: 1rem;
    margin-top: 1rem;
    
    button {
      padding: 0.8rem 1.5rem;
      border-radius: 4px;
      cursor: pointer;
      font-weight: 600;
      
      &.submit-btn {
        background: var(--accent);
        color: white;
        border: none;
        
        &:disabled {
          opacity: 0.7;
          cursor: not-allowed;
        }
      }
      
      &.cancel-btn {
        background: none;
        border: 1px solid var(--secondary);
        color: var(--primary);
      }
    }
  }
  
  .success-message {
    background: rgba(40, 167, 69, 0.1);
    border: 1px solid #28a745;
    padding: 1rem;
    border-radius: 4px;
    margin: 1rem 0;
    text-align: center;
    color: #28a745;
    font-weight: 600;
  }
  
  .error-message {
    background: rgba(220, 53, 69, 0.1);
    border: 1px solid #dc3545;
    padding: 1rem;
    border-radius: 4px;
    margin: 1rem 0;
    text-align: center;
    color: #dc3545;
  }

  .facture-detail-popup {
  width: 95%;
  max-width: 900px;
}

.items-detail-container {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.item-detail-card {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid var(--secondary);
  border-radius: 8px;
  padding: 1.2rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  
  .item-card-header {
    display: flex;
    gap: 1rem;
    
    .item-image {
      width: 80px;
      min-width: 80px;
      height: 120px;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        border-radius: 4px;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
      }
    }
    
    .item-header-info {
      display: flex;
      flex-direction: column;
      justify-content: center;
      
      h4 {
        margin: 0;
        margin-bottom: 0.5rem;
        font-size: 1.2rem;
        color: var(--primary);
      }
      
      .item-type-badge {
        display: inline-block;
        background-color: var(--accent);
        color: white;
        padding: 0.3rem 0.8rem;
        font-size: 0.8rem;
        border-radius: 20px;
        font-weight: 600;
        align-self: flex-start;
      }
    }
  }
  
  .item-details-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 0.8rem;
    border-top: 1px solid rgba(0, 0, 0, 0.1);
    border-bottom: 1px solid rgba(0, 0, 0, 0.1);
    padding: 1rem 0;
    
    .detail-item {
      display: flex;
      flex-direction: column;
      
      .detail-label {
        font-size: 0.8rem;
        color: var(--secondary);
        margin-bottom: 0.2rem;
      }
      
      .detail-value {
        font-weight: 500;
      }
    }
  }
  
  .item-description {
    .description-label {
      font-size: 0.8rem;
      color: var(--secondary);
      margin-bottom: 0.4rem;
      display: block;
    }
    
    p {
      margin: 0;
      line-height: 1.5;
      font-size: 0.95rem;
      color: var(--primary);
    }
  }
}
</style>