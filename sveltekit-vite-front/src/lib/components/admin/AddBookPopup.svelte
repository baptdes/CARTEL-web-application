<script>
  import { createEventDispatcher } from 'svelte';
  import { addAuthor, addBook, getAllGenres, addGenre, getAllAuthors } from '$lib/services/bookService';

  export let show = false;

  const dispatch = createEventDispatcher();

  let addingBook = false;
  let addingError = null;
  let addingSuccess = false;

  let availableGenres = [];
  let availableAuthors = [];

  let newBook = {
    barcode: "",
    name: "",
    publicationYear: new Date().getFullYear(),
    language: "FR",
    description: "",
    format: "LIVRE",
    genres: [{id: null}],
    authors: [],
    volumeNumber: null
  };

  const languageOptions = [
    { value: "FR", label: "Français" },
    { value: "EN", label: "Anglais" },
    { value: "JA", label: "Japonais" }
  ];

  const formatOptions = [
    { value: "LIVRE", label: "Livre" },
    { value: "BD", label: "Bande Dessinée" },
    { value: "MANGA", label: "Manga" }
  ];

  let authorInput = '';
  let authorSuggestions = [];
  let showAddAuthorPrompt = false;
  let authorToAdd = { firstname: '', surname: '' };
  let authorAddFirstname = '';
  let authorAddSurname = '';
  let addAuthorError = '';

  // Load reference data when popup opens
  $: if (show) {
    loadReferenceData();
    resetForm();
  }

  async function loadReferenceData() {
    try {
      [availableGenres, availableAuthors] = await Promise.all([
        getAllGenres(),
        getAllAuthors()
      ]);
    } catch (err) {
      addingError = "Erreur lors du chargement des données de référence: " + err.message;
    }
  }

  function resetForm() {
    newBook = {
      barcode: "",
      name: "",
      publicationYear: new Date().getFullYear(),
      language: "FR",
      description: "",
      format: "LIVRE",
      genres: [{id: null}],
      authors: [],
      volumeNumber: null
    };
    authorInput = '';
    authorSuggestions = [];
    showAddAuthorPrompt = false;
    authorToAdd = { firstname: '', surname: '' };
    authorAddFirstname = '';
    authorAddSurname = '';
    addingError = null;
    addingSuccess = false;
  }

  async function submitBookForm() {
    addingBook = true;
    addingError = null;
    addingSuccess = false;

    try {
      if (!newBook.barcode || !newBook.name) {
        throw new Error("Veuillez remplir tous les champs obligatoires");
      }
      newBook.genres = newBook.genres.filter(g => g.id !== null);

      await addBook(newBook);

      addingSuccess = true;

      setTimeout(() => {
        dispatch('added');
      }, 1500);

    } catch (err) {
      addingError = err.message || "Erreur lors de l'ajout du livre";
    } finally {
      addingBook = false;
    }
  }

  // Gestion de la saisie d'auteur
  $: if (authorInput && availableAuthors.length > 0) {
    const input = authorInput.trim().toLowerCase();
    authorSuggestions = availableAuthors.filter(a =>
      (`${a.firstname} ${a.surname}`.toLowerCase().includes(input))
      && !newBook.authors.some(sel => sel.id === a.id)
    );
    showAddAuthorPrompt = authorSuggestions.length === 0 && input.length > 0;
    if (showAddAuthorPrompt) {
      // Découper prénom/nom
      const [firstname, ...surnameArr] = authorInput.trim().split(' ');
      authorToAdd = { firstname, surname: surnameArr.join(' ') };
    }
  } else {
    authorSuggestions = [];
    showAddAuthorPrompt = false;
  }

  function selectAuthor(author) {
    newBook.authors = [...newBook.authors, author];
    authorInput = '';
    authorSuggestions = [];
    showAddAuthorPrompt = false;
  }

  function removeAuthor(authorId) {
    newBook.authors = newBook.authors.filter(a => a.id !== authorId);
  }

  // Ajout d'un nouvel auteur (fonction fictive, à créer côté service si besoin)
  async function addNewAuthor() {
    if (!authorAddFirstname.trim() || !authorAddSurname.trim()) {
      addAuthorError = "Veuillez saisir un prénom et un nom pour l'auteur.";
      return;
    }
    try {
      addAuthorError = '';
      const res = await addAuthor({ firstname: authorAddFirstname.trim(), surname: authorAddSurname.trim() });
      availableAuthors = [...availableAuthors, res];
      selectAuthor(res);
      authorAddFirstname = '';
      authorAddSurname = '';
    } catch (err) {
      addAuthorError = "Erreur lors de l'ajout de l'auteur : " + (err.message || err);
    }
  }

  // Helper: retourne true si le genre est sélectionné
  function isGenreSelected(genreId) {
    return newBook.genres.some(g => g.id === genreId);
  }

  // Ajoute ou retire un genre de la sélection
  function toggleGenre(genreId) {
    if (isGenreSelected(genreId)) {
      newBook.genres = newBook.genres.filter(g => g.id !== genreId);
    } else {
      newBook.genres = [...newBook.genres, { id: genreId }];
    }
  }

  // Handler pour l'ajout d'un nouveau genre
  async function addNewGenre() {
    const name = prompt("Nom du nouveau genre :");
    if (!name) return;
    try {
      const genre = await addGenre(name);
      console.log("Nouveau genre ajouté:", genre);
      availableGenres = [...availableGenres, genre];
    } catch (err) {
      alert("Erreur lors de l'ajout du genre : " + (err.message || err));
    }
  }

  function closePopup() {
    dispatch('close');
  }
</script>

{#if show}
<!-- svelte-ignore a11y_no_static_element_interactions -->
  <div class="popup-backdrop" onclick={closePopup} onkeydown={() => {}}>
    <div class="popup-content" onclick = {(e) => e.stopPropagation()} onkeydown={() => {}}>
      <h3>Ajouter un nouveau livre</h3>
      {#if addingSuccess}
        <div class="success-message">
          Livre ajouté avec succès!
        </div>
      {:else}
        <form class="add-book-form" onsubmit = {(e) => {e.preventDefault(); submitBookForm()}}>
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
                <label class="full-width">
                    Description:
                    <textarea bind:value={newBook.description} rows="4"></textarea>
                </label>
            </div>
          </div>
          <!-- Auteurs -->
          <div class="form-section">
            <h4>Auteur(s)</h4>
            <div class="author-row">
              <div class="author-search">
                <input
                  type="text"
                  placeholder="Rechercher un auteur (Prénom Nom)"
                  bind:value={authorInput}
                  autocomplete="off"
                />
                {#if authorSuggestions.length > 0}
                  <ul class="author-suggestions">
                    {#each authorSuggestions as author}
                      <li>
                        <button type="button" onclick={() => selectAuthor(author)}>
                          {author.firstname} {author.surname}
                        </button>
                      </li>
                    {/each}
                  </ul>
                {/if}
              </div>
              <div class="add-author-inline">
                <input
                  type="text"
                  placeholder="Prénom"
                  bind:value={authorAddFirstname}
                  class="author-inline-input"
                />
                <input
                  type="text"
                  placeholder="Nom"
                  bind:value={authorAddSurname}
                  class="author-inline-input"
                />
                <button
                  type="button"
                  class="add-author-btn"
                  title="Ajouter l'auteur"
                  onclick={addNewAuthor}
                >+</button>
              </div>
            </div>
            {#if addAuthorError}
              <div class="add-author-error">{addAuthorError}</div>
            {/if}
            {#if showAddAuthorPrompt && authorInput.trim().length > 0}
              <div class="add-author-prompt">
                Aucun auteur trouvé pour "{authorInput}".
              </div>
            {/if}
            {#if newBook.authors.length > 0}
              <div class="selected-authors">
                {#each newBook.authors as author (author.id)}
                  <span class="author-chip">
                    {author.firstname} {author.surname}
                    <button type="button" class="remove-author" onclick={() => removeAuthor(author.id)}>&times;</button>
                  </span>
                {/each}
              </div>
            {/if}
          </div>
          <!-- Genres -->
          <div class="form-section">
            <h4>Genre(s)</h4>
            <div class="genre-chips">
              {#each availableGenres as availableGenre}
                <button
                  type="button"
                  class="genre-chip"
                  class:selected={isGenreSelected(availableGenre.id)}
                  style="background-color: {availableGenre.color || '#eee'}"
                  onclick={() => toggleGenre(availableGenre.id)}
                >
                  {availableGenre.name}
                </button>
              {/each}
              <button
                type="button"
                class="genre-chip add-genre-chip"
                onclick={addNewGenre}
                aria-label="Ajouter un genre"
              >
                + Ajouter un genre
              </button>
            </div>
          </div>
          {#if addingError}
            <div class="error-message">{addingError}</div>
          {/if}
          <div class="form-actions">
            <button type="submit" class="submit-btn" disabled={addingBook}>
              {addingBook ? 'Ajout en cours...' : 'Ajouter le livre'}
            </button>
            <button type="button" class="cancel-btn" onclick={closePopup}>
              Annuler
            </button>
          </div>
        </form>
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
  .genre-chips {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;
    margin-bottom: 0.5rem;
  }
  .genre-chip {
    border: 2px solid transparent;
    border-radius: 16px;
    padding: 0.4rem 1.1rem;
    color: #222;
    background-color: #eee;
    cursor: pointer;
    font-size: 0.95rem;
    font-weight: 500;
    transition: filter 0.15s, box-shadow 0.15s;
    box-shadow: 0 1px 2px rgba(0,0,0,0.07);
    outline: none;
    &:hover, &:focus {
      filter: brightness(0.92);
      box-shadow: 0 2px 6px rgba(0,0,0,0.10);
    }
    &.selected {
      border: 2px solid var(--accent);
      color: var(--accent);
      font-weight: bold;
      filter: brightness(1.07);
    }
  }
  .add-genre-chip {
    border: 2px dashed var(--white);
    background: transparent;
    color: var(--white);
    font-weight: 600;
    &:hover, &:focus {
      background: rgba(255, 61, 0, 0.07);
      filter: none;
      box-shadow: none;
    }
  }
  .author-suggestions {
    list-style: none;
    margin: 0.3rem 0 0 0;
    padding: 0;
    background: var(--tertiary);
    border: 1px solid var(--secondary);
    border-radius: 4px;
    max-height: 120px;
    overflow-y: auto;
    li {
      margin: 0;
      button {
        background: none;
        border: none;
        width: 100%;
        text-align: left;
        padding: 0.5rem 1rem;
        cursor: pointer;
        color: var(--primary);
        &:hover {
          background: var(--secondary);
        }
      }
    }
  }
  .add-author-prompt {
    margin-top: 0.4rem;
    color: var(--primary);
    font-size: 0.95rem;
    button {
      margin-left: 0.7rem;
      background: var(--accent);
      color: white;
      border: none;
      border-radius: 3px;
      padding: 0.2rem 0.7rem;
      cursor: pointer;
      font-size: 0.95rem;
      &:hover {
        filter: brightness(0.9);
      }
    }
  }
  .selected-authors {
    margin-top: 0.5rem;
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;
    .author-chip {
      background: var(--secondary);
      color: var(--primary);
      border-radius: 12px;
      padding: 0.2rem 0.8rem;
      display: flex;
      align-items: center;
      font-size: 0.97rem;
      .remove-author {
        background: none;
        border: none;
        color: var(--accent);
        font-size: 1.1rem;
        margin-left: 0.5rem;
        cursor: pointer;
        &:hover {
          color: #b71c1c;
        }
      }
    }
  }
  .author-row {
    display: flex;
    gap: 1.2rem;
    align-items: flex-start;
    margin-bottom: 0.5rem;
    .author-search {
      flex: 2;
      position: relative;
    }
    .add-author-inline {
      flex: 1.2;
      display: flex;
      gap: 0.3rem;
      align-items: flex-start;
      .author-inline-input {
        padding: 0.4rem 0.7rem;
        border-radius: 4px;
        border: 1px solid var(--secondary);
        background: var(--tertiary);
        color: var(--primary);
        font-size: 0.97rem;
        width: 6.5rem;
      }
      .add-author-btn {
        background: var(--accent);
        color: white;
        border: none;
        border-radius: 3px;
        padding: 0.4rem 0.9rem;
        font-size: 1.2rem;
        cursor: pointer;
        font-weight: bold;
        &:hover {
          filter: brightness(0.9);
        }
      }
    }
  }
  .add-author-error {
    color: var(--accent);
    font-size: 0.97rem;
    margin-bottom: 0.3rem;
  }
</style>
