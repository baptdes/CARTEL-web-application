<script>
  import { goto } from '$app/navigation';
  import { adminPageState } from '../store.js';
  import { isAuthenticated, logout } from '$lib/auth';
  import DoubleText from "$lib/misc/DoubleText.svelte";
  import PointBar from "$lib/misc/PointBar.svelte";
  import { fetchBooks, deleteBook, addBook, getAllAuthors, getAllGenres, getAllPublishers, getAllIllustrators } from '$lib/services/bookService';
  import ConfirmDialog from '$lib/components/admin/ConfirmDialog.svelte';

  // State for books data
  let books = $state([]);
  let isLoading = $state(true);
  let error = $state(null);
  let deletingError = $state(null);
  
  // Search and filter state
  let searchQuery = $state('');
  let pageNumber = $state(0);
  let pageSize = $state(10);
  
  // Modal state
  let showDeleteConfirm = $state(false);
  let bookToDelete = $state(null);
  
  // Sort options - Similar to public catalogue
  const sortOptions = [
    { value: "name_asc", label: "Titre (A-Z)", field: "name", asc: true },
    { value: "name_desc", label: "Titre (Z-A)", field: "name", asc: false },
    { value: "year_desc", label: "Plus récents", field: "publicationYear", asc: false },
    { value: "year_asc", label: "Plus anciens", field: "publicationYear", asc: true },
  ];
  
  let sortOption = $state(sortOptions[0].value);

  // Add book form and popup state
  let showAddBookPopup = $state(false);
  let addingBook = $state(false);
  let addingError = $state(null);
  let addingSuccess = $state(false);
  
  // Data for dropdown selections
  let availableAuthors = $state([]);
  let availableIllustrators = $state([]);
  let availablePublishers = $state([]);
  let availableGenres = $state([]);
  
  // Form fields for new book
  let newBook = $state({
    barcode: "",
    name: "",
    publicationYear: new Date().getFullYear(),
    language: "FR",
    description: "",
    authors: [{id: null}],
    publisher: {id: null},
    illustrator: [{id: null}],
    format: "LIVRE",
    genres: [{id: null}],
    volumeNumber: null
  });
  
  // Language options
  const languageOptions = [
    { value: "FR", label: "Français" },
    { value: "EN", label: "English" },
    { value: "ES", label: "Español" },
    { value: "DE", label: "Deutsch" }
  ];
  
  // Format options
  const formatOptions = [
    { value: "LIVRE", label: "Livre" },
    { value: "BD", label: "Bande Dessinée" },
    { value: "MANGA", label: "Manga" }
  ];

  // Load books with search, sort, and pagination
  async function loadBooks() {
    isLoading = true;
    error = null;
    try {
      // Find the selected sort option
      const selectedSort = sortOptions.find(option => option.value === sortOption);
      
      // Build query parameters
      const params = {
        pageNumber,
        pageSize,
        asc: selectedSort.asc,
        sortBy: selectedSort.field
      };
      
      // Add search parameter if present
      if (searchQuery.trim()) {
        params.title = searchQuery.trim();
      }
      
      books = await fetchBooks(params);
    } catch (err) {
      error = err.message || 'Failed to load books';
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
    searchQuery = '';
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

  // Handle book added event
  function handleBookAdded(event) {
    // Reload the books list to include the newly added book
    loadBooks();
  }

  // Handle modify button click
  function handleModify(book) {
    console.log('Modify book:', book);
    // This would navigate to an edit form in a real implementation
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
      books = books.filter(book => book.barcode !== isbn);
      
      // Close the dialog
      showDeleteConfirm = false;
      bookToDelete = null;
    } catch (err) {
      deletingError = err.message || 'Failed to delete book';
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
    // Reset form and any previous errors
    newBook = {
      barcode: "",
      name: "",
      publicationYear: new Date().getFullYear(),
      language: "FR",
      description: "",
      authors: [{id: null}],
      publisher: {id: null},
      illustrator: [{id: null}],
      format: "LIVRE",
      genres: [{id: null}],
      volumeNumber: null
    };
    addingError = null;
    addingSuccess = false;
    
    // Load reference data if needed
    loadReferenceData();
    
    // Show the popup
    showAddBookPopup = true;
  }
  
  // Load authors, publishers, etc.
  async function loadReferenceData() {
    try {
      [availableAuthors, availablePublishers, availableGenres, availableIllustrators] = await Promise.all([
        getAllAuthors(),
        getAllPublishers(),
        getAllGenres(),
        getAllIllustrators()
      ]);
    } catch (err) {
      addingError = "Erreur lors du chargement des données de référence: " + err.message;
    }
  }
  
  // Handle form submission
  async function submitBookForm() {
    addingBook = true;
    addingError = null;
    addingSuccess = false;
    
    try {
      // Validate required fields
      if (!newBook.barcode || !newBook.name || !newBook.publisher.id) {
        throw new Error("Veuillez remplir tous les champs obligatoires");
      }
      
      // Remove empty entries
      newBook.authors = newBook.authors.filter(a => a.id !== null);
      newBook.genres = newBook.genres.filter(g => g.id !== null);
      newBook.illustrator = newBook.illustrator.filter(i => i.id !== null);
      
      // If no author is selected, show error
      if (newBook.authors.length === 0) {
        throw new Error("Veuillez sélectionner au moins un auteur");
      }
      
      const result = await addBook(newBook);
      
      // Show success
      addingSuccess = true;
      
      // Reload the book list after a delay
      setTimeout(() => {
        showAddBookPopup = false;
        loadBooks(); // refresh the book list
      }, 1500);
      
    } catch (err) {
      addingError = err.message || "Erreur lors de l'ajout du livre";
    } finally {
      addingBook = false;
    }
  }
  
  // Add another author field
  function addAuthorField() {
    newBook.authors = [...newBook.authors, {id: null}];
  }
  
  // Add another genre field
  function addGenreField() {
    newBook.genres = [...newBook.genres, {id: null}];
  }
  
  // Add another illustrator field
  function addIllustratorField() {
    newBook.illustrator = [...newBook.illustrator, {id: null}];
  }
  
  // Remove a field from an array
  function removeField(array, index) {
    return array.filter((_, i) => i !== index);
  }

  // Initialize component
  $effect(() => {
    loadBooks();
  });
</script>

<main>
  <DoubleText text="Catalogue" size="4em" />
  <PointBar Color="var(--accent)" width="70%" />

  <div class="catalogue-container">
    <div class="header-actions">
      <div class="search-actions">
        <form class="search-form" onsubmit={handleSearch}>
          <input 
            type="text" 
            bind:value={searchQuery} 
            placeholder="Rechercher par titre..." 
            class="search-input"
          />
          <button type="submit" class="search-btn">🔍</button>
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
        <button class="return-button" type="button" onclick={() => { $adminPageState = 0; goto('/admin'); }}>
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
        <button class="admin-button" onclick={() => deletingError = null}>OK</button>
      </div>
    {/if}

    {#if !isLoading && !error}
      <div class="books-grid-container">
        <!-- Header Row -->
        <div class="grid-header">
          <div class="grid-cell">Couverture</div>
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
            <div class="grid-cell">{book.name || 'Sans titre'}</div>
            <div class="grid-cell">
              {#if book.authors && book.authors.length > 0}
                {book.authors.map(a => `${a.firstname || ''} ${a.surname || ''}`).join(', ')}
              {:else}
                Auteur inconnu
              {/if}
            </div>
            <div class="grid-cell">{book.format || '-'}</div>
            <div class="grid-cell">{book.publicationYear || '-'}</div>
            <div class="grid-cell">{book.barcode || '-'}</div>
            <div class="grid-cell actions-cell">
              <button 
                class="action-btn modify-btn" 
                onclick={() => handleModify(book)}
                title="Modifier"
              >
                ✏️
              </button>
              <button 
                class="action-btn delete-btn" 
                onclick={() => handleDelete(book)}
                title="Supprimer"
              >
                🗑️
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
    message={`Êtes-vous sûr de vouloir supprimer le livre "${bookToDelete?.name || ''}" ? Cette action est irréversible.`}
    confirmText="Supprimer"
    on:confirm={confirmDelete}
    on:cancel={cancelDelete}
  />

  <!-- Add book popup -->
  {#if showAddBookPopup}
    <!-- svelte-ignore a11y_click_events_have_key_events -->
    <!-- svelte-ignore a11y_no_static_element_interactions -->
    <div class="popup-backdrop" onclick={() => showAddBookPopup = false}>
      <div class="popup-content" onclick = {(e) => e.stopPropagation()}>
        <h3>Ajouter un nouveau livre</h3>
        
        {#if addingSuccess}
          <div class="success-message">
            Livre ajouté avec succès!
          </div>
        {:else}
          <form class="add-book-form" onsubmit = {(e) => {e.preventDefault();submitBookForm()}}>
            <!-- Basic Information -->
            <div class="form-section">
              <h4>Informations générales</h4>
              
              <div class="form-row">
                <label>
                  ISBN/Code-barres*:
                  <input type="text" bind:value={newBook.barcode} required />
                </label>
                
                <label>
                  Format:
                  <select bind:value={newBook.format}>
                    {#each formatOptions as option}
                      <option value={option.value}>{option.label}</option>
                    {/each}
                  </select>
                </label>
              </div>
              
              <div class="form-row">
                <label class="full-width">
                  Titre*:
                  <input type="text" bind:value={newBook.name} required />
                </label>
              </div>
              
              <div class="form-row">
                <label>
                  Année de publication:
                  <input type="number" bind:value={newBook.publicationYear} min="1000" max="2100" />
                </label>
                
                <label>
                  Langue:
                  <select bind:value={newBook.language}>
                    {#each languageOptions as option}
                      <option value={option.value}>{option.label}</option>
                    {/each}
                  </select>
                </label>
              </div>
              
              <div class="form-row">
                <label>
                  Numéro de volume:
                  <input type="number" bind:value={newBook.volumeNumber} min="1" />
                </label>
              </div>
            </div>
            
            <!-- Authors -->
            <div class="form-section">
              <h4>Auteur(s)*</h4>
              
              {#each newBook.authors as author, index}
                <div class="form-row array-row">
                  <select bind:value={author.id}>
                    <option value={null}>Sélectionner un auteur</option>
                    {#each availableAuthors as availableAuthor}
                      <option value={availableAuthor.id}>
                        {availableAuthor.firstname} {availableAuthor.surname}
                      </option>
                    {/each}
                  </select>
                  
                  {#if newBook.authors.length > 1}
                    <button type="button" class="remove-btn" onclick={() => newBook.authors = removeField(newBook.authors, index)}>
                      ✕
                    </button>
                  {/if}
                </div>
              {/each}
              
              <button type="button" class="add-btn" onclick={addAuthorField}>
                + Ajouter un auteur
              </button>
            </div>
            
            <!-- Publishers -->
            <div class="form-section">
              <h4>Éditeur*</h4>
              
              <div class="form-row">
                <select bind:value={newBook.publisher.id} required>
                  <option value={null}>Sélectionner un éditeur</option>
                  {#each availablePublishers as publisher}
                    <option value={publisher.id}>{publisher.name}</option>
                  {/each}
                </select>
              </div>
            </div>
            
            <!-- Illustrators -->
            <div class="form-section">
              <h4>Illustrateur(s)</h4>
              
              {#each newBook.illustrator as illustrator, index}
                <div class="form-row array-row">
                  <select bind:value={illustrator.id}>
                    <option value={null}>Sélectionner un illustrateur</option>
                    {#each availableIllustrators as availableIllustrator}
                      <option value={availableIllustrator.id}>
                        {availableIllustrator.firstname} {availableIllustrator.surname}
                      </option>
                    {/each}
                  </select>
                  
                  {#if newBook.illustrator.length > 1}
                    <button type="button" class="remove-btn" onclick={() => newBook.illustrator = removeField(newBook.illustrator, index)}>
                      ✕
                    </button>
                  {/if}
                </div>
              {/each}
              
              <button type="button" class="add-btn" onclick={addIllustratorField}>
                + Ajouter un illustrateur
              </button>
            </div>
            
            <!-- Genres -->
            <div class="form-section">
              <h4>Genre(s)</h4>
              
              {#each newBook.genres as genre, index}
                <div class="form-row array-row">
                  <select bind:value={genre.id}>
                    <option value={null}>Sélectionner un genre</option>
                    {#each availableGenres as availableGenre}
                      <option value={availableGenre.id}>{availableGenre.name}</option>
                    {/each}
                  </select>
                  
                  {#if newBook.genres.length > 1}
                    <button type="button" class="remove-btn" onclick={() => newBook.genres = removeField(newBook.genres, index)}>
                      ✕
                    </button>
                  {/if}
                </div>
              {/each}
              
              <button type="button" class="add-btn" onclick={addGenreField}>
                + Ajouter un genre
              </button>
            </div>
            
            <!-- Description -->
            <div class="form-section">
              <h4>Description</h4>
              
              <div class="form-row">
                <textarea bind:value={newBook.description} rows="3"></textarea>
              </div>
            </div>
            
            {#if addingError}
              <div class="error-message">{addingError}</div>
            {/if}
            
            <div class="form-actions">
              <button type="submit" class="submit-btn" disabled={addingBook}>
                {addingBook ? 'Ajout en cours...' : 'Ajouter le livre'}
              </button>
              <button type="button" class="cancel-btn" onclick={() => showAddBookPopup = false}>
                Annuler
              </button>
            </div>
          </form>
        {/if}
      </div>
    </div>
  {/if}
</main>

<style lang="scss">
  main {
    flex: auto;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .catalogue-container {
    width: 95%;
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
      max-width: 500px;
      
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

  .add-book-btn {
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

  // Grid Layout
  .books-grid-container {
    width: 100%;
    background-color: var(--back);
    border: 1px solid var(--secondary);
    overflow-x: auto;
  }

  .grid-header {
    display: grid;
    grid-template-columns: 80px 2fr 2fr 1fr 1fr 1fr 120px;
    background-color: var(--tertiary);
    color: var(--primary);
    font-weight: bold;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    border-bottom: 1px solid var(--secondary);
    
    .grid-cell {
      padding: 1rem 0.8rem;
    }
  }

  .grid-row {
    display: grid;
    grid-template-columns: 80px 2fr 2fr 1fr 1fr 1fr 120px;
    align-items: center;
    border-bottom: 1px solid var(--secondary);
    transition: background-color 0.2s;
    
    &:hover {
      background-color: rgba(255, 255, 255, 0.05);
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
    transition: all 0.3s ease;
    flex-shrink: 0;
    
    &:hover {
      border-color: var(--accent);
      color: var(--accent);
    }
    
    &.modify-btn:hover {
      background-color: rgba(255, 61, 0, 0.1);
    }
    
    &.delete-btn:hover {
      background-color: rgba(255, 61, 0, 0.1);
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

  .loading, .error, .no-books {
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

  // Popup styles
  .popup-backdrop {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.7);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
  }
  
  .popup-content {
    background-color: var(--back);
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
  
  .add-book-form {
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
    
    .form-row {
      display: flex;
      gap: 1rem;
      margin-bottom: 0.8rem;
      
      &.array-row {
        align-items: center;
      }
      
      &:last-child {
        margin-bottom: 0;
      }
      
      .full-width {
        width: 100%;
      }
    }
    
    label {
      display: flex;
      flex-direction: column;
      flex: 1;
      color: var(--primary);
      font-size: 0.9rem;
      gap: 0.3rem;
    }
    
    input, select, textarea {
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
    
    .add-btn, .remove-btn {
      background: transparent;
      border: 1px solid var(--secondary);
      color: var(--primary);
      padding: 0.4rem 0.8rem;
      border-radius: 4px;
      cursor: pointer;
      transition: all 0.2s;
      
      &:hover {
        background-color: var(--secondary);
      }
    }
    
    .remove-btn {
      width: 32px;
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 0;
      
      &:hover {
        color: var(--accent);
        border-color: var(--accent);
      }
    }
  }
  
  .form-actions {
    display: flex;
    gap: 1rem;
    justify-content: center;
    margin-top: 1rem;
  }
  
  .submit-btn {
    background-color: var(--accent);
    color: white;
    border: none;
    padding: 0.8rem 1.5rem;
    border-radius: 4px;
    cursor: pointer;
    font-weight: bold;
    
    &:hover:not(:disabled) {
      filter: brightness(0.85);
    }
    
    &:disabled {
      opacity: 0.7;
      cursor: not-allowed;
    }
  }
  
  .cancel-btn {
    background-color: transparent;
    color: var(--primary);
    border: 1px solid var(--secondary);
    padding: 0.8rem 1.5rem;
    border-radius: 4px;
    cursor: pointer;
    
    &:hover {
      background-color: var(--secondary);
    }
  }
  
  .error-message {
    color: var(--accent);
    text-align: center;
    padding: 0.5rem;
    border: 1px solid var(--accent);
    border-radius: 4px;
    background-color: rgba(255, 61, 0, 0.1);
  }
  
  .success-message {
    color: #4CAF50;
    text-align: center;
    padding: 2rem;
    border: 1px solid #4CAF50;
    border-radius: 4px;
    background-color: rgba(76, 175, 80, 0.1);
    font-size: 1.2rem;
    font-weight: bold;
  }

  // Responsive adjustments
  @media screen and (max-width: 1200px) {
    .grid-header, .grid-row {
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
    
    .grid-header, .grid-row {
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

