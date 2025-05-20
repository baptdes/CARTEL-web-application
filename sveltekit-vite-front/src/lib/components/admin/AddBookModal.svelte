<script>
  import { createEventDispatcher } from 'svelte';
  import { fetchGenres, getAllAuthors } from '$lib/services/bookService';
  
  const dispatch = createEventDispatcher();
  
  // Props
  let { show = false } = $props();
  
  // Form data
  let bookData = $state({
    barcode: '',
    name: '',
    authors: [],
    publisher: { name: '' },
    format: 'LIVRE',
    genres: [],
    publicationYear: new Date().getFullYear(),
    language: 'FR',
    description: '',
    coverImage: '',
    volumeNumber: null
  });
  
  // Form state
  let selectedAuthors = $state([]);
  let newAuthorFirstname = $state('');
  let newAuthorSurname = $state('');
  let selectedGenres = $state([]);
  let isLoading = $state(false);
  let error = $state(null);
  let success = $state(false);
  
  // Form options
  let authorOptions = $state([]);
  let genreOptions = $state([]);
  const formatOptions = ['LIVRE', 'BD', 'MANGA'];
  const languageOptions = ['FR', 'EN', 'JA'];
  
  // Load form options when the modal is shown
  $effect(() => {
    if (show) {
      loadFormOptions();
    }
  });
  
  async function loadFormOptions() {
    isLoading = true;
    error = null;
    
    try {
      const [genres, authors] = await Promise.all([
        fetchGenres(),
        getAllAuthors()
      ]);
      
      genreOptions = genres;
      authorOptions = authors;
    } catch (err) {
      error = err.message || 'Failed to load options';
    } finally {
      isLoading = false;
    }
  }
  
  function addAuthor() {
    if (newAuthorFirstname.trim() || newAuthorSurname.trim()) {
      selectedAuthors = [
        ...selectedAuthors, 
        { firstname: newAuthorFirstname, surname: newAuthorSurname }
      ];
      newAuthorFirstname = '';
      newAuthorSurname = '';
    }
  }
  
  function removeAuthor(index) {
    selectedAuthors = selectedAuthors.filter((_, i) => i !== index);
  }
  
  function selectExistingAuthor(event) {
    const value = event.target.value;
    if (value === '') return;
    
    const [firstname, surname] = value.split('|');
    const authorExists = selectedAuthors.some(
      author => author.firstname === firstname && author.surname === surname
    );
    
    if (!authorExists) {
      selectedAuthors = [...selectedAuthors, { firstname, surname }];
    }
    
    // Reset the select element
    event.target.value = '';
  }
  
  function toggleGenre(genre) {
    if (selectedGenres.includes(genre)) {
      selectedGenres = selectedGenres.filter(g => g !== genre);
    } else {
      selectedGenres = [...selectedGenres, genre];
    }
  }
  
  function closeModal() {
    dispatch('close');
    resetForm();
  }
  
  function resetForm() {
    bookData = {
      barcode: '',
      name: '',
      authors: [],
      publisher: { name: '' },
      format: 'LIVRE',
      genres: [],
      publicationYear: new Date().getFullYear(),
      language: 'FR',
      description: '',
      coverImage: '',
      volumeNumber: null
    };
    selectedAuthors = [];
    selectedGenres = [];
    newAuthorFirstname = '';
    newAuthorSurname = '';
    error = null;
    success = false;
  }
  
  async function handleSubmit(e) {
    if (e) e.preventDefault();
    isLoading = true;
    error = null;
    success = false;
    
    try {
      // Validate required fields
      if (!bookData.barcode || !bookData.name || !bookData.publisher.name || selectedAuthors.length === 0) {
        throw new Error('Veuillez remplir tous les champs obligatoires');
      }
      
      // Prepare data for submission
      const bookPayload = {
        ...bookData,
        authors: selectedAuthors,
        genres: selectedGenres.map(name => ({ name }))
      };
      
      // This is a UI-only implementation - just log the data
      console.log('Submitting book:', bookPayload);
      
      // In a real implementation, you would submit to the API:
      // await addBook(bookPayload);
      
      success = true;
      setTimeout(() => {
        dispatch('bookAdded', bookPayload);
        closeModal();
      }, 1500);
    } catch (err) {
      error = err.message || 'Error adding book';
    } finally {
      isLoading = false;
    }
  }

  function handleKeydown(e) {
    if (e.key === 'Escape') {
      closeModal();
    }
  }
</script>

<!-- Modal backdrop -->
{#if show}
  <div 
    class="modal-backdrop" 
    onclick={closeModal} 
    onkeydown={handleKeydown} 
    role="dialog" 
    aria-modal="true" 
    aria-labelledby="modal-title"
    tabindex="-1"
  >
    <!-- Modal content - stop click propagation to prevent closing when clicking inside -->
    <div 
      class="modal-content" 
      onclick={(e) => { e.stopPropagation(); }} 
      role="document"
    >
      <div class="modal-header">
        <h2 id="modal-title">Ajouter un nouveau livre</h2>
        <button class="close-btn" onclick={closeModal} aria-label="Fermer">&times;</button>
      </div>
      
      {#if isLoading && !success}
        <div class="modal-loading">
          <div class="spinner"></div>
          <p>Chargement...</p>
        </div>
      {:else if error}
        <div class="modal-error">
          <p>{error}</p>
          <button class="admin-button" onclick={() => error = null}>OK</button>
        </div>
      {:else if success}
        <div class="modal-success">
          <p>Livre ajouté avec succès!</p>
          <div class="spinner success-spinner"></div>
        </div>
      {:else}
        <div class="modal-body">
          <form onsubmit={handleSubmit}>
            <div class="form-grid">
              <!-- Basic Information Section -->
              <div class="form-section">
                <h3>Informations de base</h3>
                
                <div class="form-group">
                  <label for="isbn">ISBN / Code-barres *</label>
                  <input 
                    type="text" 
                    id="isbn" 
                    bind:value={bookData.barcode} 
                    required
                    placeholder="9782017042471"
                  />
                </div>
                
                <div class="form-group">
                  <label for="title">Titre *</label>
                  <input 
                    type="text" 
                    id="title" 
                    bind:value={bookData.name} 
                    required
                    placeholder="Titre du livre"
                  />
                </div>
                
                <div class="form-group">
                  <label for="publisher">Éditeur *</label>
                  <input 
                    type="text" 
                    id="publisher" 
                    bind:value={bookData.publisher.name} 
                    required
                    placeholder="Nom de l'éditeur"
                  />
                </div>
                
                <div class="form-row">
                  <div class="form-group">
                    <label for="year">Année de publication</label>
                    <input 
                      type="number" 
                      id="year" 
                      bind:value={bookData.publicationYear} 
                      min="1000" 
                      max="2100"
                    />
                  </div>
                  
                  <div class="form-group">
                    <label for="format">Format *</label>
                    <select id="format" bind:value={bookData.format}>
                      {#each formatOptions as format}
                        <option value={format}>{format === "LIVRE" ? "Livre" : format === "BD" ? "Bande Dessinée" : format}</option>
                      {/each}
                    </select>
                  </div>
                </div>
                
                <div class="form-row">
                  <div class="form-group">
                    <label for="volume">Numéro de volume</label>
                    <input 
                      type="number" 
                      id="volume" 
                      bind:value={bookData.volumeNumber} 
                      min="1"
                      placeholder="Optionnel"
                    />
                  </div>
                  
                  <div class="form-group">
                    <label for="language">Langue</label>
                    <select id="language" bind:value={bookData.language}>
                      {#each languageOptions as lang}
                        <option value={lang}>
                          {lang === "FR" ? "Français" : lang === "EN" ? "Anglais" : "Japonais"}
                        </option>
                      {/each}
                    </select>
                  </div>
                </div>
              </div>
              
              <!-- Authors Section -->
              <div class="form-section">
                <h3>Auteurs *</h3>
                
                <div class="form-group">
                  <label for="existing-author">Sélectionner un auteur existant</label>
                  <select id="existing-author" onchange={selectExistingAuthor}>
                    <option value="">-- Sélectionner un auteur --</option>
                    {#each authorOptions as author}
                      <option value={`${author.firstname || ''}|${author.surname || ''}`}>
                        {(author.firstname || '') + ' ' + (author.surname || '')}
                      </option>
                    {/each}
                  </select>
                </div>
                
                <p class="or-divider">ou ajouter un nouvel auteur</p>
                
                <div class="form-row">
                  <div class="form-group">
                    <label for="author-firstname">Prénom</label>
                    <input type="text" id="author-firstname" bind:value={newAuthorFirstname} />
                  </div>
                  
                  <div class="form-group">
                    <label for="author-surname">Nom</label>
                    <input type="text" id="author-surname" bind:value={newAuthorSurname} />
                  </div>
                </div>
                
                <button type="button" class="add-btn" onclick={addAuthor}>
                  Ajouter l'auteur
                </button>
                
                {#if selectedAuthors.length > 0}
                  <div class="selected-items">
                    <p>Auteurs sélectionnés:</p>
                    <ul>
                      {#each selectedAuthors as author, i}
                        <li>
                          {(author.firstname || '') + ' ' + (author.surname || '')}
                          <button type="button" class="remove-btn" onclick={() => removeAuthor(i)}>
                            &times;
                          </button>
                        </li>
                      {/each}
                    </ul>
                  </div>
                {/if}
              </div>
              
              <!-- Additional Information Section -->
              <div class="form-section full-width">
                <h3>Informations complémentaires</h3>
                
                <div class="form-group">
                  <label for="cover">URL de la couverture</label>
                  <input 
                    type="url" 
                    id="cover" 
                    bind:value={bookData.coverImage} 
                    placeholder="https://example.com/cover.jpg"
                  />
                </div>
                
                <div class="form-group">
                  <label for="description">Description</label>
                  <textarea 
                    id="description" 
                    bind:value={bookData.description} 
                    rows="4"
                    placeholder="Description du livre"
                  ></textarea>
                </div>
                
                <div class="form-group">
                  <label>Genres</label>
                  <div class="checkbox-grid">
                    {#each genreOptions as genre}
                      <div class="checkbox-label">
                        <input 
                          type="checkbox" 
                          id={`genre-${genre}`}
                          checked={selectedGenres.includes(genre)} 
                          onclick={() => toggleGenre(genre)} 
                        />
                        <label for={`genre-${genre}`}>{genre}</label>
                      </div>
                    {/each}
                  </div>
                </div>
              </div>
            </div>
            
            <div class="form-actions">
              <button type="button" class="cancel-btn" onclick={closeModal}>
                Annuler
              </button>
              <button type="submit" class="submit-btn" disabled={isLoading}>
                {isLoading ? 'Ajout en cours...' : 'Ajouter le livre'}
              </button>
            </div>
          </form>
        </div>
      {/if}
    </div>
  </div>
{/if}

<style lang="scss">
  .modal-backdrop {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.7);
    z-index: 1000;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 2rem;
    overflow-y: auto;
  }
  
  .modal-content {
    background-color: var(--back);
    border: 1px solid var(--secondary);
    max-width: 900px;
    width: 100%;
    max-height: 90vh;
    overflow-y: auto;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
    display: flex;
    flex-direction: column;
  }
  
  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem 1.5rem;
    background-color: var(--tertiary);
    border-bottom: 1px solid var(--secondary);
    
    h2 {
      color: var(--primary);
      margin: 0;
      font-size: 1.5rem;
    }
    
    .close-btn {
      background: none;
      border: none;
      color: var(--primary);
      font-size: 1.5rem;
      cursor: pointer;
      
      &:hover {
        color: var(--accent);
      }
    }
  }
  
  .modal-body {
    padding: 1.5rem;
    flex: 1;
    
    .form-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 1.5rem;
      
      @media (max-width: 768px) {
        grid-template-columns: 1fr;
      }
    }
    
    .full-width {
      grid-column: 1 / -1;
    }
  }
  
  .form-section {
    h3 {
      color: var(--accent);
      margin-top: 0;
      margin-bottom: 1.2rem;
      border-bottom: 1px solid var(--secondary);
      padding-bottom: 0.5rem;
    }
  }
  
  .form-group {
    margin-bottom: 1rem;
    
    label {
      display: block;
      margin-bottom: 0.4rem;
      color: var(--primary);
      font-weight: bold;
    }
    
    input, select, textarea {
      width: 100%;
      padding: 0.6rem;
      background-color: rgba(255, 255, 255, 0.05);
      color: var(--primary);
      border: 1px solid var(--secondary);
      border-radius: 3px;
      
      &:focus {
        outline: none;
        border-color: var(--accent);
      }
    }
  }
  
  .form-row {
    display: flex;
    gap: 1rem;
    
    .form-group {
      flex: 1;
    }
    
    @media (max-width: 576px) {
      flex-direction: column;
      gap: 0.5rem;
    }
  }
  
  .or-divider {
    text-align: center;
    position: relative;
    margin: 1rem 0;
    color: var(--secondary);
    
    &::before, &::after {
      content: '';
      position: absolute;
      top: 50%;
      width: 30%;
      height: 1px;
      background-color: var(--secondary);
    }
    
    &::before {
      left: 0;
    }
    
    &::after {
      right: 0;
    }
  }
  
  .add-btn {
    background-color: transparent;
    color: var(--accent);
    border: 1px solid var(--accent);
    padding: 0.5rem 1rem;
    cursor: pointer;
    width: 100%;
    margin-bottom: 1rem;
    
    &:hover {
      background-color: rgba(255, 61, 0, 0.1);
    }
  }
  
  .selected-items {
    margin-top: 1rem;
    background-color: rgba(255, 255, 255, 0.05);
    padding: 0.8rem;
    border-radius: 3px;
    
    p {
      margin-top: 0;
      margin-bottom: 0.5rem;
      color: var(--primary);
      font-weight: bold;
    }
    
    ul {
      list-style: none;
      padding: 0;
      margin: 0;
      
      li {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0.3rem 0;
        color: var(--primary);
        
        .remove-btn {
          background: none;
          border: none;
          color: var(--secondary);
          cursor: pointer;
          font-size: 1rem;
          
          &:hover {
            color: var(--accent);
          }
        }
      }
    }
  }
  
  .checkbox-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 0.5rem;
    
    .checkbox-label {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      
      input[type="checkbox"] {
        width: auto;
      }
      
      label {
        font-weight: normal;
        margin-bottom: 0;
      }
    }
  }
  
  .form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 1rem;
    margin-top: 1.5rem;
    
    button {
      padding: 0.7rem 1.5rem;
      cursor: pointer;
      border-radius: 3px;
      font-weight: bold;
    }
    
    .cancel-btn {
      background-color: transparent;
      color: var(--primary);
      border: 1px solid var(--secondary);
      
      &:hover {
        background-color: rgba(255, 255, 255, 0.05);
      }
    }
    
    .submit-btn {
      background-color: var(--accent);
      color: white;
      border: none;
      
      &:hover:not(:disabled) {
        filter: brightness(0.9);
      }
      
      &:disabled {
        background-color: #444;
        cursor: not-allowed;
      }
    }
  }
  
  .modal-loading, .modal-error, .modal-success {
    padding: 3rem;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    text-align: center;
  }
  
  .spinner {
    width: 40px;
    height: 40px;
    border: 3px solid rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    border-top-color: var(--accent);
    animation: spin 1s linear infinite;
    margin-bottom: 1rem;
  }
  
  .success-spinner {
    border-top-color: #28a745;
  }
  
  .modal-error p {
    color: var(--accent);
    margin-bottom: 1rem;
  }
  
  .modal-success p {
    color: #28a745;
    margin-bottom: 1rem;
  }
  
  @keyframes spin {
    to { transform: rotate(360deg); }
  }
  
  /* Scrollbar styling */
  .modal-content::-webkit-scrollbar {
    width: 8px;
  }
  
  .modal-content::-webkit-scrollbar-track {
    background: #222;
  }
  
  .modal-content::-webkit-scrollbar-thumb {
    background-color: var(--tertiary);
    border-radius: 4px;
  }
  
  .modal-content::-webkit-scrollbar-thumb:hover {
    background-color: var(--secondary);
  }
</style>
