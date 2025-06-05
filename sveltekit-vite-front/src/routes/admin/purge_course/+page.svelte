<script>
  import { onMount } from "svelte";
  import { goto } from "$app/navigation";
  import { adminPageState } from "../store.js";
  import DoubleText from "$lib/misc/DoubleText.svelte";
  import PointBar from "$lib/misc/PointBar.svelte";
  import {
    getAllFactures,
    deleteFacture,
    getFactureById,
    searchFacturesByItem,
  } from "$lib/services/factureService.js";
  import { isAuthenticated, logout } from "$lib/auth";
  
  // Import our new components
  import AddFacturePopup from "$lib/components/admin/AddFacturePopup.svelte";
  import FactureDetailPopup from "$lib/components/admin/FactureDetailPopup.svelte";

  // State for factures data
  let factures = $state([]);
  let isLoading = $state(true);
  let error = $state(null);
  let isAuthError = $state(false);

  // Search state
  let searchQuery = $state("");
  let isSearching = $state(false);
  let searchResults = $state([]);
  let searchPerformed = $state(false);

  // Popup state
  let showAddFacturePopup = $state(false);
  let showFactureDetailPopup = $state(false);
  let selectedFacture = $state(null);

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
      console.log("Loaded factures:", factures);
      
      // Reset search state
      searchResults = [];
      searchPerformed = false;
    } catch (err) {
      // Check if it's an auth error (403 Forbidden)
      if (err.status === 403) {
        isAuthError = true;
        error =
          "Vous n'avez pas les droits nécessaires pour accéder à cette page. Veuillez vous reconnecter.";
      } else {
        error = `Erreur lors du chargement des factures: ${err.message || err}`;
      }
    } finally {
      isLoading = false;
    }
  }
  
  // Search for factures containing the given item
  async function searchFacturesByItemName() {
    if (!searchQuery.trim()) {
      // If search query is empty, just show all factures
      searchResults = [];
      searchPerformed = false;
      return;
    }
    
    isSearching = true;
    error = null;
    try {
      const result = await searchFacturesByItem(searchQuery.trim());
      searchResults = result;
      searchPerformed = true;
    } catch (err) {
      error = `Erreur lors de la recherche: ${err.message || err}`;
    } finally {
      isSearching = false;
    }
  }
  
  // Handle search form submission
  function handleSearchSubmit(event) {
    event.preventDefault();
    searchFacturesByItemName();
  }
  
  // Reset search
  function resetSearch() {
    searchQuery = "";
    searchResults = [];
    searchPerformed = false;
  }

  // Handle authentication error
  function handleAuthError() {
    // Log out the user and redirect to login page
    logout();
    goto("/login");
  }

  // Open add facture popup
  function openAddFacturePopup() {
    showAddFacturePopup = true;
  }

  // Close add facture popup
  function closeAddFacturePopup() {
    showAddFacturePopup = false;
  }

  // Handle successful facture creation
  async function handleFactureCreated() {
    await loadFactures();
    closeAddFacturePopup();
  }

  // Open facture detail popup
  function openFactureDetailPopup(facture) {
    selectedFacture = facture;
    showFactureDetailPopup = true;
  }

  // Close facture detail popup
  function closeFactureDetailPopup() {
    showFactureDetailPopup = false;
    selectedFacture = null;
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
    return new Intl.DateTimeFormat("fr-FR", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    }).format(date);
  }
  
  // Highlight search term in text
  function highlightMatch(text, query) {
    if (!query || !text) return text;
    
    const regex = new RegExp(`(${query})`, 'gi');
    return text.replace(regex, '<mark>$1</mark>');
  }
</script>

<main>
  <DoubleText text="Trésorerie" size="4em" />
  <PointBar Color="var(--accent)" width="70%" />

  <div class="tresorerie-container">
    <!-- Header Actions -->
    <div class="header-actions">
      <button
        class="admin-button add-facture-btn"
        onclick={openAddFacturePopup}
      >
        <span>+</span> Nouvelle Facture
      </button>
      <button
        class="return-button"
        type="button"
        onclick={() => {
          $adminPageState = 0;
          goto("/admin");
        }}
      >
        Retour
      </button>
    </div>
    
    <!-- Search Bar -->
    <div class="search-bar">
      <form on:submit={handleSearchSubmit}>
        <div class="search-input-container">
          <input
            type="text"
            bind:value={searchQuery}
            placeholder="Rechercher un article dans les factures..."
            class="search-input"
          />
          <button type="submit" class="search-btn">
            🔍
          </button>
          {#if searchPerformed}
            <button type="button" class="reset-btn" on:click={resetSearch}>
              ✕
            </button>
          {/if}
        </div>
      </form>
    </div>

    <!-- Loading State -->
    {#if isLoading || isSearching}
      <div class="loading-spinner">
        <div class="spinner"></div>
        <p>{isSearching ? "Recherche en cours..." : "Chargement des factures..."}</p>
      </div>
      <!-- Error Message -->
    {:else if error}
      <div class="error-message">
        {error}
        {#if isAuthError}
          <div class="auth-error-actions">
            <button class="admin-button" onclick={handleAuthError}
              >Se reconnecter</button
            >
          </div>
        {:else}
          <button class="admin-button" onclick={loadFactures}>Réessayer</button>
        {/if}
      </div>
    {:else}
      <!-- Factures List -->
      <div class="factures-list">
        {#if searchPerformed}
          <h2>Résultats de la recherche</h2>
          {#if searchResults.length === 0}
            <div class="empty-state">
              <p>Aucune facture trouvée contenant "{searchQuery}"</p>
              <button class="admin-button" onclick={resetSearch}>Réinitialiser la recherche</button>
            </div>
          {:else}
            <p class="search-results-count">{searchResults.length} facture(s) trouvée(s)</p>
            <div class="factures-grid">
              <div class="grid-header">
                <div class="grid-cell">Nom</div>
                <div class="grid-cell">Date</div>
                <div class="grid-cell">Articles trouvés</div>
                <div class="grid-cell">Actions</div>
              </div>

              {#each searchResults as facture}
                <div class="grid-row">
                  <div
                    class="grid-cell facture-name"
                    role="button"
                    tabindex="0"
                    onclick={() => openFactureDetailPopup(facture)}
                    onkeydown={() => {}}
                  >
                    {facture.filename || "Sans nom"}
                  </div>
                  <div class="grid-cell">
                    {formatDate(facture.updatedAt)}
                  </div>
                  <div class="grid-cell">
                    <!-- Display matching items -->
                    {#if facture.items}
                      {facture.items.filter(item => 
                        item.name.toLowerCase().includes(searchQuery.toLowerCase())
                      ).length}/{facture.items.length}
                    {:else}
                      0
                    {/if}
                  </div>
                  <div class="grid-cell actions-cell">
                    <button
                      class="action-btn delete-btn"
                      onclick={() => handleDeleteFacture(facture.id)}
                      title="Supprimer"
                    >
                      🗑️
                    </button>
                  </div>
                </div>
              {/each}
            </div>
          {/if}
        {:else}
          <h2>Factures</h2>

          {#if factures.length === 0}
            <div class="empty-state">
              <p>Aucune facture trouvée</p>
              <button class="admin-button" onclick={openAddFacturePopup}
                >Créer une facture</button
              >
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
                  <div
                    class="grid-cell facture-name"
                    role="button"
                    tabindex="0"
                    onclick={() => openFactureDetailPopup(facture)}
                    onkeydown={() => {}}
                  >
                    {facture.filename || "Sans nom"}
                  </div>
                  <div class="grid-cell">
                    {formatDate(facture.updatedAt)}
                  </div>
                  <div class="grid-cell">
                    {facture.items ? facture.items.length : 0}
                  </div>
                  <div class="grid-cell actions-cell">
                    <button
                      class="action-btn delete-btn"
                      onclick={() => handleDeleteFacture(facture.id)}
                      title="Supprimer"
                    >
                      🗑️
                    </button>
                  </div>
                </div>
              {/each}
            </div>
          {/if}
        {/if}
      </div>
    {/if}
  </div>
</main>

<!-- Use our components for popups -->
<AddFacturePopup 
  showPopup={showAddFacturePopup}
  on:close={closeAddFacturePopup}
  on:success={handleFactureCreated}
/>

<FactureDetailPopup 
  showPopup={showFactureDetailPopup}
  facture={selectedFacture}
  on:close={closeFactureDetailPopup}
/>

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
  
  .search-bar {
    margin-bottom: 1.5rem;
    
    .search-input-container {
      position: relative;
      display: flex;
      
      .search-input {
        flex: 1;
        padding: 0.7rem 1rem;
        padding-right: 3rem;
        background-color: var(--back);
        color: var(--primary);
        border: 1px solid var(--secondary);
        border-radius: 4px;
        font-size: 1rem;
        width: 100%;
        
        &:focus {
          outline: none;
          border-color: var(--accent);
        }
      }
      
      .search-btn, .reset-btn {
        position: absolute;
        background: none;
        border: none;
        padding: 0.5rem;
        cursor: pointer;
        color: var(--secondary);
        
        &:hover {
          color: var(--accent);
        }
      }
      
      .search-btn {
        right: 0;
        top: 50%;
        transform: translateY(-50%);
        font-size: 1.2rem;
      }
      
      .reset-btn {
        right: 2rem;
        top: 50%;
        transform: translateY(-50%);
        font-size: 1.2rem;
      }
    }
  }
  
  .search-results-count {
    margin-bottom: 1rem;
    color: var(--secondary);
    font-style: italic;
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
      to {
        transform: rotate(360deg);
      }
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
      color: var(--primary);

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

  .error-message {
    background: rgba(220, 53, 69, 0.1);
    border: 1px solid #dc3545;
    padding: 1rem;
    border-radius: 4px;
    margin: 1rem 0;
    text-align: center;
    color: #dc3545;
  }
  
  mark {
    background-color: rgba(var(--accent-rgb), 0.3);
    padding: 0 2px;
  }
</style>