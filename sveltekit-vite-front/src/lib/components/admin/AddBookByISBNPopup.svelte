<script>
  import { createEventDispatcher } from 'svelte';
  import { getBookFromBNF, addBook } from '$lib/services/bookService';
  import { createItemCopy } from '$lib/services/copyService';

  let { show = $bindable(false) } = $props();

  const dispatch = createEventDispatcher();

  // States
  let step = $state('format'); // 'format', 'isbn', 'confirm', 'success'
  let isLoading = $state(false);
  let error = $state(null);
  let successMessage = $state('');
  
  // Selected format
  let selectedFormat = $state('LIVRE');
  
  // ISBN handling
  let isbnInput = $state('');
  let currentBook = $state(null);
  
  // Multiple books handling
  let addedBooks = $state([]);

  // Format options (same as in AddBookPopup)
  const formatOptions = [
    { value: "LIVRE", label: "Livre" },
    { value: "BD", label: "Bande Dessinée" },
    { value: "MANGA", label: "Manga" }
  ];
  
  // Number of copies to create
  let copiesCount = $state(1);
  
  function resetForm() {
    step = 'format';
    isLoading = false;
    error = null;
    successMessage = '';
    selectedFormat = 'LIVRE';
    isbnInput = '';
    currentBook = null;
    copiesCount = 1;
  }
  
  function closePopup() {
    show = false;
    dispatch('close');
    
    // Reset the form after a short delay
    setTimeout(resetForm, 300);
  }
  
  function continueToISBNStep() {
    step = 'isbn';
    error = null;
  }
  
  async function fetchBookByISBN() {
    if (!isbnInput.trim()) {
      error = "Veuillez saisir un ISBN";
      return;
    }
    
    try {
    isLoading = true;
    error = null;

    const response = await getBookFromBNF(isbnInput.trim());

    console.log("Fetched book from BNF:", response);

    if (response && response.barcode) {
      currentBook = { ...response, format: selectedFormat };

      // Move to confirmation step
      step = 'confirm';
      } else {
        error = "Livre non trouvé avec cet ISBN";
      }
    } catch (err) {
      error = `Erreur lors de la récupération des détails du livre: ${err.message}`;
      console.error("Failed to fetch book from BNF:", err);
    } finally {
      isLoading = false;
    }
  }
  
  async function confirmAndAddBook() {
    if (!currentBook) {
      error = "Aucun livre à ajouter";
      return;
    }
    
    try {
      isLoading = true;
      error = null;
      
      // Add the book
      const addedBook = await addBook(currentBook);
      
      // Create copies as specified
      for (let i = 0; i < copiesCount; i++) {
        console.log(`Creating copy ${i + 1} for book:`, addedBook.barcode);
        await createItemCopy(addedBook.barcode);
      }
      
      // Add to the list of added books
      addedBooks = [...addedBooks, {
        title: addedBook.name,
        isbn: addedBook.barcode,
        copies: copiesCount
      }];
      
      // Show success message
      successMessage = `Livre "${addedBook.name}" ajouté avec ${copiesCount} exemplaire(s)`;
      
      // Reset for next book
      isbnInput = '';
      currentBook = null;
      copiesCount = 1;
      
      // Return to ISBN input step to add more books
      step = 'isbn';
    } catch (err) {
      error = `Erreur lors de l'ajout du livre: ${err.message}`;
      console.error("Failed to add book:", err);
    } finally {
      isLoading = false;
    }
  }
  
  function handleKeyPress(event) {
    // Automatically submit when scanning with a barcode reader
    // Most barcode scanners add a newline or tab after the scan
    if (event.key === 'Enter' && isbnInput.trim() && !isLoading) {
      fetchBookByISBN();
    }
  }
  
  // Focus the ISBN input when we reach that step
  $effect(() => {
    if (step === 'isbn') {
      setTimeout(() => {
        const input = document.getElementById('isbn-input');
        if (input) input.focus();
      }, 100);
    }
  });
  
  function goBackToISBN() {
    step = 'isbn';
    currentBook = null;
    error = null;
  }
  
  function goBackToFormat() {
    step = 'format';
  }
  
  function finishAndClose() {
    dispatch('added', { addedBooks });
    closePopup();
  }
</script>

{#if show}
<!-- svelte-ignore a11y_no_static_element_interactions -->
  <div class="popup-backdrop" onclick={closePopup} onkeydown={() => {}}>
    <div class="popup-content" onclick = {(e) => e.stopPropagation()} onkeydown={(e) => handleKeyPress(e)}>
      <h3>Ajouter des livres par ISBN</h3>
      
      {#if error}
        <div class="error-message">{error}</div>
      {/if}
      
      {#if successMessage}
        <div class="success-message">{successMessage}</div>
      {/if}
      
      {#if step === 'format'}
        <div class="step-container">
          <h4>Étape 1: Sélectionner le format</h4>
          <p class="instruction">Sélectionnez le format des livres que vous allez ajouter:</p>
          
          <div class="format-options">
            {#each formatOptions as option}
              <label class="format-option">
                <input 
                  type="radio" 
                  name="format" 
                  value={option.value} 
                  bind:group={selectedFormat} 
                />
                <span>{option.label}</span>
              </label>
            {/each}
          </div>
          
          <div class="form-actions">
            <button type="button" class="submit-btn" onclick={continueToISBNStep}>
              Continuer
            </button>
            <button type="button" class="cancel-btn" onclick={closePopup}>
              Annuler
            </button>
          </div>
        </div>
      {/if}
      
      {#if step === 'isbn'}
        <div class="step-container">
          <h4>Étape 2: Scanner ou saisir l'ISBN</h4>
          
          {#if addedBooks.length > 0}
            <div class="added-books-summary">
              <h5>Livres déjà ajoutés: {addedBooks.length}</h5>
              <ul>
                {#each addedBooks as book}
                  <li>
                    <strong>{book.title}</strong> (ISBN: {book.isbn}) - {book.copies} exemplaire(s)
                  </li>
                {/each}
              </ul>
            </div>
          {/if}
          
          <p class="instruction">
            Scannez le code-barres avec un scanner ou saisissez l'ISBN manuellement:
          </p>
          
          <div class="isbn-form">
            <input 
              type="text" 
              id="isbn-input"
              placeholder="ISBN (ex: 9782820341136)" 
              bind:value={isbnInput}
              disabled={isLoading}
              class="isbn-input"
            />
            <button 
              type="button" 
              class="search-btn" 
              onclick={fetchBookByISBN}
              disabled={isLoading || !isbnInput.trim()}
            >
              {#if isLoading}
                <span class="spinner"></span>
              {:else}
                Rechercher
              {/if}
            </button>
          </div>
          
          <div class="form-actions">
            <button type="button" class="back-btn" onclick={goBackToFormat}>
              Retour
            </button>
            <button type="button" class="finish-btn" onclick={finishAndClose} disabled={addedBooks.length === 0}>
              Terminer ({addedBooks.length})
            </button>
          </div>
        </div>
      {/if}
      
      {#if step === 'confirm' && currentBook}
        <div class="step-container">
          <h4>Étape 3: Confirmer les détails du livre</h4>
          
          <div class="book-details">
            <div class="book-detail">
              <span class="label">Titre:</span>
              <span class="value">{currentBook.name}</span>
            </div>
            <div class="book-detail">
              <span class="label">Auteur(s):</span>
              <span class="value">
                {#if currentBook.authors && currentBook.authors.length > 0}
                  {currentBook.authors.map(a => `${a.firstname || ''} ${a.surname || ''}`).join(', ')}
                {:else}
                  Auteur inconnu
                {/if}
              </span>
            </div>
            <div class="book-detail">
              <span class="label">ISBN:</span>
              <span class="value">{currentBook.barcode}</span>
            </div>
            <div class="book-detail">
              <span class="label">Format:</span>
              <span class="value">
                {formatOptions.find(f => f.value === currentBook.format)?.label || currentBook.format}
              </span>
            </div>
            <div class="book-detail">
              <span class="label">Année:</span>
              <span class="value">{currentBook.publicationYear || 'Non spécifiée'}</span>
            </div>
            {#if currentBook.description}
              <div class="book-detail description">
                <span class="label">Description:</span>
                <span class="value">{currentBook.description}</span>
              </div>
            {/if}
            
            <div class="copies-input">
              <label for="copies-count">Nombre d'exemplaires à créer:</label>
              <input 
                type="number" 
                id="copies-count" 
                bind:value={copiesCount} 
                min="1" 
                max="50"
              />
            </div>
          </div>
          
          <div class="form-actions">
            <button type="button" class="back-btn" onclick={goBackToISBN}>
              Retour
            </button>
            <button 
              type="button" 
              class="submit-btn" 
              onclick={confirmAndAddBook}
              disabled={isLoading}
            >
              {#if isLoading}
                <span class="spinner"></span> Ajout en cours...
              {:else}
                Confirmer et Ajouter
              {/if}
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
    align-items: center;
    justify-content: center;
    z-index: 1000;
  }
  
  .popup-content {
    background-color: var(--back);
    border: 2px solid var(--accent);
    border-radius: 0px;
    padding: 2rem;
    width: 90%;
    max-width: 800px;
    max-height: 90vh;
    overflow-y: auto;
    
    h3 {
      color: var(--back);
      -webkit-text-stroke: 1px var(--accent);
      text-transform: uppercase;
      font-size: 2.5rem;
      font-family: Guisol;
      transform: scaleY(1.5);
      margin-top: 0;
      text-align: center;
      margin-bottom: 1.5rem;
    }
    
    h4 {
      color: var(--primary);
      margin-top: 0;
      margin-bottom: 1rem;
      font-size: 1.2rem;
    }
    
    h5 {
      color: var(--accent);
      margin-top: 0.5rem;
      margin-bottom: 0.5rem;
      font-size: 1rem;
    }
  }
  
  .step-container {
    margin-top: 1rem;
  }
  
  .instruction {
    color: var(--primary);
    margin-bottom: 1.5rem;
    font-size: 1rem;
  }
  
  .format-options {
    display: flex;
    flex-direction: column;
    gap: 1rem;
    margin-bottom: 2rem;
    
    .format-option {
      display: flex;
      align-items: center;
      gap: 1rem;
      cursor: pointer;
      padding: 0.8rem;
      border: 1px solid var(--secondary);
      border-radius: 4px;
      transition: all 0.2s;
      
      &:hover {
        background-color: rgba(255, 255, 255, 0.05);
      }
      
      input {
        margin: 0;
        cursor: pointer;
      }
      
      span {
        color: var(--primary);
        font-size: 1rem;
      }
    }
  }
  
  .isbn-form {
    display: flex;
    gap: 1rem;
    margin-bottom: 2rem;
    
    .isbn-input {
      flex: 1;
      padding: 0.8rem;
      background-color: var(--tertiary);
      border: 1px solid var(--secondary);
      border-radius: 4px;
      color: var(--primary);
      font-size: 1rem;
      
      &:focus {
        outline: none;
        border-color: var(--accent);
      }
    }
    
    .search-btn {
      background-color: var(--accent);
      color: white;
      border: none;
      padding: 0.8rem 1.5rem;
      border-radius: 4px;
      cursor: pointer;
      font-weight: bold;
      
      &:hover:not(:disabled) {
        filter: brightness(0.9);
      }
      
      &:disabled {
        opacity: 0.5;
        cursor: not-allowed;
      }
    }
  }
  
  .added-books-summary {
    margin-bottom: 1.5rem;
    padding: 1rem;
    border: 1px solid var(--secondary);
    border-radius: 4px;
    background-color: rgba(255, 255, 255, 0.05);
    
    ul {
      margin: 0.5rem 0 0 0;
      padding-left: 1.5rem;
      
      li {
        margin-bottom: 0.3rem;
      }
    }
  }
  
  .book-details {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    padding: 1rem;
    border: 1px solid var(--secondary);
    border-radius: 4px;
    margin-bottom: 2rem;
    
    .book-detail {
      display: flex;
      gap: 0.5rem;
      
      .label {
        min-width: 100px;
        font-weight: bold;
        color: var(--accent);
      }
      
      .value {
        color: var(--primary);
      }
      
      &.description {
        flex-direction: column;
        
        .value {
          padding: 0.5rem;
          background-color: rgba(0, 0, 0, 0.2);
          border-radius: 4px;
          font-style: italic;
          margin-top: 0.3rem;
          white-space: pre-line;
        }
      }
    }
  }
  
  .copies-input {
    margin-top: 1rem;
    padding-top: 1rem;
    border-top: 1px dashed var(--secondary);
    display: flex;
    align-items: center;
    gap: 1rem;
    
    label {
      color: var(--primary);
      min-width: 220px;
    }
    
    input {
      width: 80px;
      padding: 0.5rem;
      background-color: var(--tertiary);
      border: 1px solid var(--secondary);
      border-radius: 4px;
      color: var(--primary);
      text-align: center;
    }
  }
  
  .form-actions {
    display: flex;
    justify-content: space-between;
    margin-top: 1rem;
    gap: 1rem;
  }
  
  .submit-btn, .finish-btn {
    background-color: var(--accent);
    color: white;
    border: none;
    padding: 0.8rem 1.5rem;
    border-radius: 4px;
    cursor: pointer;
    font-weight: bold;
    
    &:hover:not(:disabled) {
      filter: brightness(0.9);
    }
    
    &:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }
  }
  
  .finish-btn {
    background-color: #4CAF50; // Green color for finish button
  }
  
  .back-btn, .cancel-btn {
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
    padding: 0.8rem;
    border: 1px solid var(--accent);
    border-radius: 4px;
    background-color: rgba(255, 61, 0, 0.1);
    margin-bottom: 1rem;
  }
  
  .success-message {
    color: #4CAF50;
    text-align: center;
    padding: 0.8rem;
    border: 1px solid #4CAF50;
    border-radius: 4px;
    background-color: rgba(76, 175, 80, 0.1);
    margin-bottom: 1rem;
  }
  
  .spinner {
    display: inline-block;
    width: 16px;
    height: 16px;
    border: 2px solid rgba(255, 255, 255, 0.3);
    border-radius: 50%;
    border-top-color: #fff;
    animation: spin 1s ease infinite;
    margin-right: 0.5rem;
  }

  @keyframes spin {
    to {
      transform: rotate(360deg);
    }
  }
</style>
