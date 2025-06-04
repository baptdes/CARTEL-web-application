<script lang="ts">
  import { goto } from "$app/navigation";
  import { adminPageState } from "../store.js";
  import { isAuthenticated, logout } from "$lib/auth";
  import DoubleText from "$lib/misc/DoubleText.svelte";
  import PointBar from "$lib/misc/PointBar.svelte";
  import { fetchBooks, deleteBook, addBook } from "$lib/services/bookService";
  import ConfirmDialog from "$lib/components/admin/ConfirmDialog.svelte";
  import AddBookPopup from "$lib/components/admin/AddBookPopup.svelte";
  import AddBookByISBNPopup from "$lib/components/admin/AddBookByISBNPopup.svelte";
  // State for books data
  let books = $state([]);
  let isLoading = $state(true);
  let error = $state(null);
  let deletingError = $state(null);

  // Search and filter state
  let searchQuery = $state("");
  let pageNumber = $state(0);
  let pageSize = $state(10);

  // Modal state
  let showDeleteConfirm = $state(false);
  let bookToDelete = $state(null);

  // Sort options - Similar to public catalogue
  const sortOptions = [
    { value: "name_asc", label: "Titre (A-Z)", field: "name", asc: true },
    { value: "name_desc", label: "Titre (Z-A)", field: "name", asc: false },
    {
      value: "year_desc",
      label: "Plus récents",
      field: "publicationYear",
      asc: false,
    },
    {
      value: "year_asc",
      label: "Plus anciens",
      field: "publicationYear",
      asc: true,
    },
  ];

  let sortOption = $state(sortOptions[0].value);

  // Add book form and popup state
  let showAddBookPopup = $state(false);
  let showAddBookByISBNPopup = $state(false);

  // Load books with search, sort, and pagination
  async function loadBooks() {
    isLoading = true;
    error = null;
    try {
      // Find the selected sort option
      const selectedSort = sortOptions.find(
        (option) => option.value === sortOption
      );

      // Build query parameters
      const params = {
        pageNumber,
        pageSize,
        asc: selectedSort.asc,
        sortBy: selectedSort.field,
      };

      // Add search parameter if present
      if (searchQuery.trim()) {
        params.titleBook = searchQuery.trim();
      }

      books = await fetchBooks(params);
    } catch (err) {
      error = err.message || "Failed to load books";
    } finally {
      isLoading = false;
    }
  }

  // Navigate to the next page
  function nextPage() {
    pageNumber += 1;
    loadBooks();
  }

  // Navigate to the previous page
  function prevPage() {
    if (pageNumber > 0) {
      pageNumber -= 1;
      loadBooks();
    }
  }

  // Reset search and filters
  function resetSearch() {
    searchQuery = "";
    pageNumber = 0;
    sortOption = sortOptions[0].value;
    loadBooks();
  }

  // Handle search form submission
  function handleSearch(event) {
    event.preventDefault();
    pageNumber = 0; // Reset to first page on new search
    loadBooks();
  }

  // Handle delete button click - open confirmation dialog
  function handleDelete(book) {
    bookToDelete = book;
    showDeleteConfirm = true;
  }

  // Handle delete confirmation
  async function confirmDelete() {
    if (!bookToDelete) return;

    try {
      deletingError = null;
      const isbn = bookToDelete.barcode;

      await deleteBook(isbn);

      // Remove the book from the local list to update the UI immediately
      books = books.filter((book) => book.barcode !== isbn);

      // Close the dialog
      showDeleteConfirm = false;
      bookToDelete = null;
    } catch (err) {
      deletingError = err.message || "Failed to delete book";
    }
  }

  // Handle delete cancellation
  function cancelDelete() {
    showDeleteConfirm = false;
    bookToDelete = null;
    deletingError = null;
  }

  // Handle opening the add book popup
  function handleAddBook() {
    showAddBookPopup = true;
  }

  // Handle opening the add book by ISBN popup
  function handleAddBookByISBN() {
    showAddBookByISBNPopup = true;
  }

  // Handle book added event
  function handleBookAdded(event) {
    showAddBookPopup = false;
    loadBooks();
  }

  // Handle book added by ISBN event
  function handleBookAddedByISBN(event) {
    const { addedBooks } = event.detail;
    console.log(`Added ${addedBooks.length} books by ISBN`);
    showAddBookByISBNPopup = false;
    loadBooks();
  }

  // Initialize component
  $effect(() => {
    loadBooks();
  });
</script>

<div class="catalogue-container">
  <div class="header-actions">
    <div class="search-actions" style="border-radius: 0px;">
      <form class="search-form" onsubmit={handleSearch}>
        <input
          type="text"
          bind:value={searchQuery}
          placeholder="Rechercher par titre..."
          class="search-input"
          style="border-radius: 0px;"
        />
        <button type="submit" class="search-btn">
          <img
            src="/src/assets/img/icons/magglass.svg"
            alt="Modifier"
            width="20"
            height="20"
          />
        </button>
      </form>

      <div class="sort-container">
        <label for="sort-select">Trier par:</label>
        <select
          id="sort-select"
          bind:value={sortOption}
          onchange={loadBooks}
          class="sort-select"
        >
          {#each sortOptions as option}
            <option value={option.value}>{option.label}</option>
          {/each}
        </select>
      </div>
    </div>

    <div class="action-buttons">
      <button class="admin-button add-book-btn" onclick={handleAddBook}>
        <span>+</span> Ajouter un livre
      </button>
      <button class="admin-button add-isbn-btn" onclick={handleAddBookByISBN}>
        <!-- <span>📊</span> --> Ajouter par ISBN
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
  </div>

  {#if isLoading}
    <div class="loading">
      <div class="spinner"></div>
      <p>Chargement des livres...</p>
    </div>
  {/if}

  {#if error}
    <div class="error">
      <p>Erreur lors du chargement: {error}</p>
      <button class="admin-button" onclick={loadBooks}>Réessayer</button>
    </div>
  {/if}

  {#if deletingError}
    <div class="error">
      <p>Erreur lors de la suppression: {deletingError}</p>
      <button class="admin-button" onclick={() => (deletingError = null)}
        >OK</button
      >
    </div>
  {/if}

  {#if !isLoading && !error}
    <div class="books-grid-container">
      <!-- Header Row -->
      <div class="grid-header">
        <div class="grid-cell">•</div>
        <div class="grid-cell">Titre</div>
        <div class="grid-cell">Auteur(s)</div>
        <div class="grid-cell">Format</div>
        <div class="grid-cell">Année</div>
        <div class="grid-cell">ISBN</div>
        <div class="grid-cell">Actions</div>
      </div>

      <!-- Book Rows -->
      {#each books as book}
        <div class="grid-row">
          <div class="grid-cell cover-cell">
            <img
              src={book.coverImage || "/placeholder_book.png"}
              alt="Couverture"
              class="book-cover"
            />
          </div>
          <div class="grid-cell">{book.name || "Sans titre"}</div>
          <div class="grid-cell">
            {#if book.authors && book.authors.length > 0}
              {book.authors
                .map((a) => `${a.firstname || ""} ${a.surname || ""}`)
                .join(", ")}
            {:else}
              Auteur inconnu
            {/if}
          </div>
          <div class="grid-cell">{book.format || "-"}</div>
          <div class="grid-cell">{book.publicationYear || "-"}</div>
          <div class="grid-cell">{book.barcode || "-"}</div>
          <div class="grid-cell actions-cell">
            <button
              class="action-btn modify-btn"
              onclick={() => handleModify(book)}
              title="Modifier"
            >
              <img
                src="/src/assets/img/icons/pen.svg"
                style=" filter: invert(1);"
                alt="Modifier"
                width="20"
                height="20"
              />
            </button>
            <button
              class="action-btn delete-btn"
              onclick={() => handleDelete(book)}
              title="Supprimer"
            >
              <img
                src="/src/assets/img/icons/bin.svg"
                style=" filter: invert(1);"
                alt="Modifier"
                width="20"
                height="20"
              />
            </button>
          </div>
        </div>
      {/each}
    </div>

    {#if books.length === 0 && !isLoading}
      <div class="no-books">
        <p>Aucun livre trouvé.</p>
      </div>
    {/if}

    <!-- Pagination Controls -->
    {#if books.length > 0}
      <div class="pagination">
        <button
          class="pagination-btn prev"
          disabled={pageNumber === 0}
          onclick={prevPage}
        >
          &laquo; Précédent
        </button>

        <span class="page-indicator">Page {pageNumber + 1}</span>

        <button
          class="pagination-btn next"
          disabled={books.length < pageSize}
          onclick={nextPage}
        >
          Suivant &raquo;
        </button>
      </div>
    {/if}
  {/if}
</div>

<!-- Confirmation Dialog for Delete -->
<ConfirmDialog
  show={showDeleteConfirm}
  title="Confirmer la suppression"
  message={`Êtes-vous sûr de vouloir supprimer le livre "${bookToDelete?.name || ""}" ? Cette action est irréversible.`}
  confirmText="Supprimer"
  on:confirm={confirmDelete}
  on:cancel={cancelDelete}
/>

<!-- Add book popup (now as a component) -->
<AddBookPopup
  show={showAddBookPopup}
  on:close={() => (showAddBookPopup = false)}
  on:added={handleBookAdded}
/>

<!-- Add book by ISBN popup -->
<AddBookByISBNPopup
  show={showAddBookByISBNPopup}
  on:close={() => (showAddBookByISBNPopup = false)}
  on:added={handleBookAddedByISBN}
/>

<style lang="scss">
  @use "/src/lib/sass/base" as base;

  main {
    flex: auto;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .catalogue-container {
    width: 100%;
    margin-top: 2rem;
  }

  .header-actions {
    display: flex;
    flex-direction: column;
    gap: 1rem;
    margin-bottom: 2rem;

    .action-buttons {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .search-actions {
      display: flex;
      flex-wrap: wrap;
      gap: 1rem;
      align-items: center;
      justify-content: space-between;
      padding: 0.8rem;
      background-color: rgba(var(--tertiary-rgb), 0.5);
      border: 1px solid var(--secondary);
      border-radius: 4px;
    }

    .search-form {
      display: flex;
      flex: 1;
      position: relative;
      max-width: 600px;

      .search-input {
        width: 100%;
        padding: 0.7rem 1rem;
        padding-right: 3rem;
        background-color: var(--back);
        color: var(--primary);
        border: 1px solid var(--secondary);
        border-radius: 4px;
        font-size: 1rem;
        transition: border-color 0.3s;

        &:focus {
          outline: none;
          border-color: var(--accent);
        }

        &::placeholder {
          color: rgba(255, 255, 255, 0.5);
        }
      }

      .search-btn {
        position: absolute;
        right: 0.5rem;
        top: 50%;
        transform: translateY(-50%);
        background: none;
        border: none;
        color: var(--primary);
        font-size: 1.2rem;
        cursor: pointer;
        transition: color 0.3s;

        &:hover {
          color: var(--accent);
        }

        img {
          filter: invert(1);
          &:hover {
            @extend .accent-colorize;
          }
        }
      }
    }

    .sort-container {
      display: flex;
      align-items: center;
      gap: 0.5rem;

      label {
        color: var(--primary);
        font-size: 0.9rem;
      }

      .sort-select {
        padding: 0.5rem;
        background-color: var(--back);
        color: var(--primary);
        border: 1px solid var(--secondary);
        border-radius: 4px;

        &:focus {
          outline: none;
          border-color: var(--accent);
        }
      }
    }
  }

  .add-book-btn,
  .add-isbn-btn {
    background-color: var(--accent);
    color: white;
    border: none;
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.6rem 1.2rem;

    span {
      font-size: 1.4rem;
      font-weight: bold;
    }

    &:hover {
      background-color: var(--accent);
      filter: brightness(0.85);
    }
  }

  .add-isbn-btn {
    background-color: #4caf50; // Different color to distinguish from regular add
    margin-left: 0.5rem;
  }

  // Grid Layout
  .books-grid-container {
    width: 100%;
    background-color: var(--back);
    overflow-x: auto;
  }

  .grid-header {
    display: grid;
    grid-template-columns: 80px 2fr 2fr 1fr 1fr 1fr 120px;
    border: none;
    gap: 5px;
    color: var(--back);
    font-family: Guisol;
    font-size: 1.5em;
    text-transform: uppercase;
    letter-spacing: 0.5px;

    .grid-cell {
      padding: 1rem 0.8rem;
      border-radius: 15px;
      background-color: var(--accent);
      text-overflow: "•";
    }
  }

  .grid-row {
    display: grid;
    grid-template-columns: 80px 2fr 2fr 1fr 1fr 1fr 120px;
    align-items: center;
    border-bottom: 1px solid var(--secondary);
    transition: background-color 0.2s;

    &:hover {
      outline: 5px dashed var(--accent);
    }

    &:last-child {
      border-bottom: none;
    }
  }

  .grid-cell {
    padding: 0.8rem;
    align-self: center;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .return-button {
    background-color: var(--secondary);
    color: var(--primary);
    border: none;
    padding: 0.6rem 1.2rem;
    cursor: pointer;
    transition: all 0.3s;
    border-radius: 0;
    &:hover {
      background-color: var(--accent);
      filter: brightness(0.85);
    }
  }

  .cover-cell {
    display: flex;
    justify-content: center;

    .book-cover {
      width: 50px;
      height: 70px;
      object-fit: cover;
      border: 1px solid var(--secondary);
    }
  }

  .actions-cell {
    display: flex;
    gap: 0.5rem;
    justify-content: center;
  }

  .action-btn {
    background: transparent;
    border: 1px solid var(--secondary);
    border-radius: 0px;
    color: var(--secondary);
    width: 36px;
    height: 36px;
    min-height: 36px;
    min-width: 36px;
    padding: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: border-color 0.3s ease, filter 0.1s ease;
    flex-shrink: 0;

    &:hover {
      border-color: var(--accent);
      @extend .accent-colorize;
    }
  }

  // Pagination Controls
  .pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 1rem;
    margin-top: 1.5rem;

    .pagination-btn {
      padding: 0.6rem 1.2rem;
      background-color: var(--back);
      color: var(--primary);
      border: 1px solid var(--secondary);
      border-radius: 4px;
      cursor: pointer;
      transition: all 0.3s;

      &:hover:not(:disabled) {
        background-color: var(--accent);
        border-color: var(--accent);
      }

      &:disabled {
        opacity: 0.5;
        cursor: not-allowed;
      }
    }

    .page-indicator {
      color: var(--primary);
    }
  }

  .loading,
  .error,
  .no-books {
    width: 100%;
    padding: 2rem;
    text-align: center;
    border: 1px solid var(--secondary);
    margin-top: 1rem;
  }

  .loading {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 1rem;

    .spinner {
      width: 40px;
      height: 40px;
      border: 3px solid rgba(255, 255, 255, 0.1);
      border-radius: 50%;
      border-top-color: var(--accent);
      animation: spin 1s ease-in-out infinite;
    }
  }

  .error {
    border-color: var(--accent);
    color: var(--accent);

    button {
      margin-top: 1rem;
    }
  }

  @keyframes spin {
    to {
      transform: rotate(360deg);
    }
  }

  // Responsive adjustments
  @media screen and (max-width: 1200px) {
    .grid-header,
    .grid-row {
      grid-template-columns: 80px 2fr 2fr 1fr 1fr 120px;
    }

    .grid-header .grid-cell:nth-child(6),
    .grid-row .grid-cell:nth-child(6) {
      display: none; // Hide ISBN on smaller screens
    }
  }

  @media screen and (max-width: 768px) {
    .header-actions {
      .search-actions {
        flex-direction: column;
        align-items: stretch;
      }

      .sort-container {
        flex-wrap: wrap;
      }
    }

    .grid-header,
    .grid-row {
      grid-template-columns: 80px 2fr 2fr 120px;
    }

    .grid-header .grid-cell:nth-child(4),
    .grid-row .grid-cell:nth-child(4),
    .grid-header .grid-cell:nth-child(5),
    .grid-row .grid-cell:nth-child(5) {
      display: none; // Hide Format and Year on mobile
    }
  }
</style>
