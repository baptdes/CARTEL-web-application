<script>
  import { createEventDispatcher } from "svelte";
  import { createFacture } from "$lib/services/factureService.js";
  import { fetchBooks } from "$lib/services/bookService.js";
  import { fetchGames } from "$lib/services/gameService.js";

  const dispatch = createEventDispatcher();

  // Props (using runes)
  const { showPopup = false } = $props();

  // State
  let factureName = $state("");
  let searchQuery = $state("");
  let searchResults = $state([]);
  let selectedItems = $state([]);
  let searchType = $state("books"); // 'books' or 'games'
  let isSubmitting = $state(false);
  let submitError = $state(null);
  let submitSuccess = $state(false);

  // Methods
  function closePopup() {
    dispatch("close");
  }

    $effect(() => {
    if (showPopup) {
      // Reset all form values when popup is opened
      factureName = "";
      searchQuery = "";
      searchResults = [];
      selectedItems = [];
      submitError = null;
      submitSuccess = false; // <-- This is the important part
    }
  });

  // Search for books or games
  async function handleSearch() {
    if (!searchQuery.trim()) {
      searchResults = [];
      return;
    }

    try {
      console.log(`Searching for ${searchType} with query "${searchQuery}"`);

      if (searchType === "books") {
        // Book search params - titleBook is the correct parameter name
        const params = {
          titleBook: searchQuery.trim(),
          pageNumber: 0,
          pageSize: 10,
          sortBy: "name",
          asc: true,
        };
        console.log("Book search params:", params);
        const result = await fetchBooks(params);
        console.log("Book search result:", result);
        searchResults = result.content || result || [];
      } else {
        // Game search params - titleGame is the correct parameter name
        const params = {
          titleGame: searchQuery.trim(),
          pageNumber: 0,
          pageSize: 10,
          sortBy: "name",
          asc: true,
        };
        console.log("Game search params:", params);
        const result = await fetchGames(params);
        console.log("Game search result:", result);
        searchResults = result.content || result || [];
      }
    } catch (err) {
      console.error("Search error:", err);
      searchResults = [];
    }
  }

  // Add item to selected items
  function addItem(item) {
    // Check if item already exists in selected items
    if (
      !selectedItems.some((selectedItem) => selectedItem.barcode === item.barcode)
    ) {
      selectedItems = [
        ...selectedItems,
        {
          ...item,
          id: item.barcode, // Use barcode as ID
          type: searchType === "books" ? "BOOK" : "GAME",
          quantity: 1,
        },
      ];
    }
    searchQuery = "";
    searchResults = [];
  }

  // Remove item from selected items
  function removeItem(itemId) {
    selectedItems = selectedItems.filter((item) => item.id !== itemId);
  }

  // Update item quantity
  function updateQuantity(itemId, delta) {
    selectedItems = selectedItems.map((item) => {
      if (item.id === itemId) {
        const newQuantity = Math.max(1, item.quantity + delta);
        return { ...item, quantity: newQuantity };
      }
      return item;
    });
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
        items: selectedItems.map((item) => ({
          itemId: item.barcode || item.id, // Try barcode first, then fallback to id
          itemType: item.type,
          quantity: item.quantity,
        })),
      };

      console.log("Submitting facture:", factureData);
      await createFacture(factureData);
      submitSuccess = true;

      // Notify parent of successful creation
      setTimeout(() => {
        dispatch("success");
      }, 1500);
    } catch (err) {
      submitError = `Erreur lors de la création de la facture: ${err.message || err}`;
    } finally {
      isSubmitting = false;
    }
  }

  // Handle search input keydown
  function handleSearchKeydown(event) {
    if (event.key === "Enter") {
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

{#if showPopup}
  <div
    class="popup-backdrop"
    role="dialog"
    tabindex=""
    onclick={closePopup}
    onkeydown={() => {}}
  >
    <div
      class="popup-content"
      role="document"
      onclick={(e) => e.stopPropagation()}
      onkeydown={() => {}}
    >
      <h3>Créer une nouvelle facture</h3>

      {#if submitSuccess}
        <div class="success-message">Facture créée avec succès!</div>
      {:else}
        <div class="facture-form">
          <!-- Facture Name -->
          <div class="form-section">
            <label class="full-width">
              Nom de la facture*:
              <input
                type="text"
                bind:value={factureName}
                required
                placeholder="Entrez un nom pour la facture"
              />
            </label>
          </div>

          <!-- Item Search -->
          <div class="form-section">
            <h4>Recherche d'articles</h4>

            <div class="search-type-toggle">
              <button
                class="toggle-btn"
                class:active={searchType === "books"}
                onclick={() => {
                  searchType = "books";
                  searchResults = [];
                }}
              >
                Livres
              </button>
              <button
                class="toggle-btn"
                class:active={searchType === "games"}
                onclick={() => {
                  searchType = "games";
                  searchResults = [];
                }}
              >
                Jeux
              </button>
            </div>

            <div class="search-container">
              <input
                type="text"
                bind:value={searchQuery}
                placeholder={searchType === "books"
                  ? "Rechercher un livre..."
                  : "Rechercher un jeu..."}
                onkeydown={handleSearchKeydown}
              />
              <button class="search-btn" onclick={handleSearch}>🔍</button>
            </div>

            <!-- Search Results -->
            {#if searchResults.length > 0}
              <div class="search-results">
                {#each searchResults as item}
                  <div
                    class="search-result-item"
                    role="button"
                    tabindex="0"
                    onclick={() => addItem(item)}
                    onkeydown={() => {}}
                  >
                    <div class="item-image">
                      <img
                        src={item.coverImage || "/placeholder_book.png"}
                        alt={item.name || "Article"}
                      />
                    </div>
                    <div class="item-details">
                      <div class="item-name">{item.name || "Sans titre"}</div>
                      <div class="item-info">
                        {#if searchType === "books" && item.authors && item.authors.length}
                          {item.authors
                            .map(
                              (a) => `${a.firstname || ""} ${a.surname || ""}`
                            )
                            .join(", ")}
                        {:else if searchType === "games" && item.creator}
                          {item.creator.firstname} {item.creator.surname}
                        {:else}
                          &nbsp;
                        {/if}
                      </div>
                    </div>
                  </div>
                {/each}
              </div>
            {:else if searchQuery && !isSubmitting}
              <div class="no-results">Aucun résultat trouvé</div>
            {/if}
          </div>

          <!-- Selected Items -->
          <div class="form-section">
            <h4>Articles sélectionnés</h4>

            {#if selectedItems.length === 0}
              <div class="no-selected-items">Aucun article sélectionné</div>
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
                      <div class="item-type">
                        {item.type === "BOOK" ? "Livre" : "Jeu"}
                      </div>
                    </div>
                    <div class="item-quantity">
                      <button
                        class="qty-btn minus"
                        onclick={() => updateQuantity(item.id, -1)}>-</button
                      >
                      <span>{item.quantity}</span>
                      <button
                        class="qty-btn plus"
                        onclick={() => updateQuantity(item.id, 1)}>+</button
                      >
                    </div>
                    <button
                      class="remove-btn"
                      onclick={() => removeItem(item.id)}>✕</button
                    >
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
              onclick={submitFacture}
            >
              {isSubmitting ? "Création en cours..." : "Créer la facture"}
            </button>
            <button
              type="button"
              class="cancel-btn"
              onclick={closePopup}
            >
              Annuler
            </button>
          </div>
        </div>
      {/if}
    </div>
  </div>
{/if}

<style lang="scss">
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
    background-color: var(--back);
    color: var(--primary);
    border: 2px solid var(--accent);
    border-radius: 0px;
    padding: 2rem;
    width: 90%;
    max-width: 800px;
    max-height: 90vh;
    overflow-y: auto;
    position: relative;

    h3 {
      margin-top: 0;
      text-align: center;
      margin-bottom: 1.5rem;
      color: var(--back);
      -webkit-text-stroke: 1px var(--accent);
      text-transform: uppercase;
      font-size: 2.5rem;
      font-family: Guisol;
      transform: scaleY(1.5);
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
      color: var(--primary);

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
          color: var(--primary);
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
      color: var(--primary);

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
          color: var(--primary);
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

  .no-selected-items,
  .no-results {
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