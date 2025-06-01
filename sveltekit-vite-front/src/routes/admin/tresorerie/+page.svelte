<script>
  import { onMount } from 'svelte';
  import { goto } from '$app/navigation';
  import { adminPageState } from '../store.js';
  import DoubleText from "$lib/misc/DoubleText.svelte";
  import PointBar from "$lib/misc/PointBar.svelte";
  import { createFacture, getAllFactures, deleteFacture } from '$lib/services/factureService.js';
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
      factures.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
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
      if (searchType === 'books') {
        const params = {
          title: searchQuery.trim(),
          pageNumber: 0,
          pageSize: 10,
          sortBy: 'name',
          asc: true
        };
        const result = await fetchBooks(params);
        searchResults = result.content || [];
      } else {
        const params = {
          titleGame: searchQuery.trim(),
          pageNumber: 0,
          pageSize: 10,
          sortBy: 'name',
          asc: true
        };
        const result = await fetchGames(params);
        searchResults = result.content || [];
      }
    } catch (err) {
      console.error('Search error:', err);
      searchResults = [];
    }
  }

  // Add item to selected items
  function addItem(item) {
    // Check if item already exists in selected items
    if (!selectedItems.some(selectedItem => selectedItem.id === item.id)) {
      selectedItems = [...selectedItems, {
        ...item,
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
        name: factureName.trim(),
        items: selectedItems.map(item => ({
          itemId: item.id,
          itemType: item.type,
          quantity: item.quantity
        }))
      };

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
<!-- Replace your existing error message display with this -->
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
              <div class="grid-cell">Total</div>
              <div class="grid-cell">Actions</div>
            </div>
            
            {#each factures as facture}
              <div class="grid-row">
                <div class="grid-cell">{facture.name}</div>
                <div class="grid-cell">{formatDate(facture.createdAt)}</div>
                <div class="grid-cell">{facture.items ? facture.items.length : 0}</div>
                <div class="grid-cell">{calculateTotal(facture.items).toFixed(2)} €</div>
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
      grid-template-columns: 2fr 1fr 1fr 1fr 0.5fr;
      background: var(--primary);
      color: white;
      font-weight: 600;
      
      .grid-cell {
        padding: 1rem;
        text-align: center;
      }
    }
    
    .grid-row {
      display: grid;
      grid-template-columns: 2fr 1fr 1fr 1fr 0.5fr;
      border-bottom: 1px solid rgba(0, 0, 0, 0.1);
      
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
</style>