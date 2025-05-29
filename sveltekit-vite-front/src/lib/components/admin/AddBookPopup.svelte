<script>
  import { createEventDispatcher } from 'svelte';
  import { addAuthor, addBook, getAllGenres, addGenre, getAllAuthors, addIllustrator, getAllIllustrators } from '$lib/services/bookService';
  import { createItemCopy } from '$lib/services/copyService';
  import { createLoanToCartel, searchPersons, addPerson } from '$lib/services/loanService';
  import PeopleSelector from './PeopleSelector.svelte';
  import MemberSelector from './MemberSelector.svelte';

  let { show = $bindable(false) } = $props();

  const dispatch = createEventDispatcher();

  let addingBook = $state(false);
  let addingError = $state(null);
  let addingSuccess = $state(false);

  let availableGenres = $state([]);
  let availableAuthors = $state([]);
  let availableIllustrators = $state([]);
  
  // Manage exemplaires separately from newBook
  let exemplaires = $state([
    { id: Math.random().toString(36).slice(2), lender: null }
  ]);

  let newBook = $state({
    barcode: "",
    name: "",
    publicationYear: new Date().getFullYear(),
    language: "FR",
    description: "",
    format: "LIVRE",
    genres: [{id: null}], // Initial genre structure
    authors: [],
    illustrator: [],
    volumeNumber: null,
    // exemplaires property removed from here
  });

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

  // Enhanced status object to track both copies and loans
  let copiesStatus = $state({
    processing: false,
    created: 0,
    total: 0,
    errors: [],
    loans: {
      processing: false,
      created: 0,
      total: 0,
      errors: []
    }
  });

  // Load reference data and reset form when popup opens
  $effect(() => {
    if (show) {
      loadReferenceData();
      resetForm();
    }
  });

  async function loadReferenceData() {
    try {
      const [genres, authors, illustrators] = await Promise.all([
        getAllGenres(),
        getAllAuthors(),
        getAllIllustrators()
      ]);
      availableGenres = genres;
      availableAuthors = authors;
      availableIllustrators = illustrators;
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
      illustrator: [],
      volumeNumber: null
      // exemplaires removed from here
    };
    
    // Reset exemplaires separately
    exemplaires = [
      { id: Math.random().toString(36).slice(2), lender: null }
    ];
    
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

      $state.snapshot(newBook)
      const addedBook = await addBook(newBook); // Save the book response for barcode access
      
      // Count how many exemplaires have lenders (will be loans to Cartel)
      const loansToCreate = exemplaires.filter(ex => ex.lender && ex.lender.id).length;
      
      // Now create copies for each exemplaire
      const exemplairesCount = exemplaires.length;
      copiesStatus = {
        processing: true,
        created: 0,
        total: exemplairesCount,
        errors: [],
        loans: {
          processing: false,
          created: 0,
          total: loansToCreate,
          errors: []
        }
      };
      
      // Array to store created copies for loan association
      const createdCopies = [];
      
      // Create a copy for each exemplaire in the list
      for (let i = 0; i < exemplairesCount; i++) {
        try {
          const copy = await createItemCopy(addedBook.barcode);
          copiesStatus.created++;
          
          // Store the copy along with its exemplaire info (including lender)
          createdCopies.push({
            copy,
            exemplaire: exemplaires[i]
          });
        } catch (err) {
          copiesStatus.errors.push(`Erreur lors de la création de l'exemplaire ${i+1}: ${err.message}`);
          console.error(`Failed to create copy ${i+1}:`, err);
        }
      }
      
      copiesStatus.processing = false;
      
      // Now create loans for copies that have lenders
      if (loansToCreate > 0) {
        copiesStatus.loans.processing = true;
        
        for (const item of createdCopies) {
          if (item.exemplaire.lender && item.exemplaire.lender.id) {
            try {
              await createLoanToCartel(item.exemplaire.lender.id, item.copy.id);
              copiesStatus.loans.created++;
            } catch (err) {
              copiesStatus.loans.errors.push(
                `Erreur lors de la création du prêt pour l'exemplaire (membre: ${item.exemplaire.lender.firstname} ${item.exemplaire.lender.surname}): ${err.message}`
              );
              console.error(`Failed to create loan:`, err);
            }
          }
        }
        
        copiesStatus.loans.processing = false;
      }
      
      addingSuccess = true;

      // Only close the popup after a brief delay to show the success message
      const hasErrors = copiesStatus.errors.length > 0 || copiesStatus.loans.errors.length > 0;
      setTimeout(() => {
        dispatch('added');
        closePopup();
      }, hasErrors ? 2000 : 2000); // Longer delay if there are errors

    } catch (err) {
      addingError = err.message || "Erreur lors de l'ajout du livre";
    } finally {
      addingBook = false;
    }
  }

  // This function is passed to SelectionField as 'addObject'
  // It should expect an object with field1 and field2 (e.g., firstname, surname)
  async function handleAddNewAuthor(authorData) {
    // authorData is { field1: string, field2: string } from SelectionField
    if (!authorData.field1?.trim() || !authorData.field2?.trim()) {
        // This error should ideally be caught and displayed by SelectionField
        throw new Error("Le prénom et le nom de l'auteur sont requis.");
    }
    try {
      const newAuthorPayload = { firstname: authorData.field1.trim(), surname: authorData.field2.trim() };
      const addedAuthor = await addAuthor(newAuthorPayload); // Call your actual service
      availableAuthors = [...availableAuthors, addedAuthor]; // Update local list for search
      return addedAuthor; // Return the new author object for SelectionField
    } catch (err) {
      console.error("Erreur lors de l'ajout de l'auteur:", err);
      // Re-throw the error so SelectionField can display it
      throw new Error("Erreur serveur lors de l'ajout de l'auteur: " + (err.message || err));
    }
  }

  // Add new illustrator
  async function handleAddNewIllustrator(illustratorData) {
    if (!illustratorData.field1?.trim() || !illustratorData.field2?.trim()) {
      throw new Error("Le prénom et le nom de l'illustrateur sont requis.");
    }
    try {
      const newIllustratorPayload = { firstname: illustratorData.field1.trim(), surname: illustratorData.field2.trim() };
      const addedIllustrator = await addIllustrator(newIllustratorPayload);
      availableIllustrators = [...availableIllustrators, addedIllustrator];
      return addedIllustrator;
    } catch (err) {
      console.error("Erreur lors de l'ajout de l'illustrateur:", err);
      throw new Error("Erreur serveur lors de l'ajout de l'illustrateur: " + (err.message || err));
    }
  }

  /**
   * Function to search authors for SelectionField
   * @param {string} query
   * @returns {Promise<Array<{id: any, firstname: string, surname: string}>>}
   */
  async function searchAuthorsLocal(query){
    // searchObjects in SelectionField expects a promise
    if (!query) {
      return availableAuthors; // Return all available if query is empty
    }
    const lowerCaseQuery = query.toLowerCase();
    return availableAuthors.filter(author => {
      const fullName = `${author.firstname} ${author.surname}`.toLowerCase();
      return fullName.includes(lowerCaseQuery);
    });
  }

  /**
   * Function to search illustrators for SelectionField
   * @param {string} query
   * @returns {Promise<Array<{id: any, firstname: string, surname: string}>>}
   */
  async function searchIllustratorsLocal(query) {
    if (!query) {
      return availableIllustrators;
    }
    const lowerCaseQuery = query.toLowerCase();
    return availableIllustrators.filter(illustrator => {
      const fullName = `${illustrator.firstname} ${illustrator.surname}`.toLowerCase();
      return fullName.includes(lowerCaseQuery);
    });
  }

  // Function to search for members (persons)
  async function searchMembers(query) {
    try {
      const result = await searchPersons(query);
      return result.content || [];
    } catch (err) {
      console.error("Error searching for members:", err);
      return [];
    }
  }

  // Function to add a new member (person)
  async function handleAddNewMember(memberData) {
    if (!memberData.field1?.trim() || !memberData.field2?.trim()) {
      throw new Error("Le prénom et le nom du membre sont requis.");
    }
    
    try {
      const newMemberPayload = {
        firstname: memberData.field1.trim(),
        surname: memberData.field2.trim(),
        contact: memberData.contact || "",
        caution: memberData.caution || 0
      };
      
      const addedMember = await addPerson(newMemberPayload);
      return addedMember;
    } catch (err) {
      console.error("Erreur lors de l'ajout du membre:", err);
      throw new Error("Erreur serveur lors de l'ajout du membre: " + (err.message || err));
    }
  }

  // Helper: returns true if the genre is selected
  function isGenreSelected(genreId) {
    return newBook.genres.some(g => g.id === genreId);
  }

  // Adds or removes a genre from the selection
  function toggleGenre(genreId) {
    const genreExists = isGenreSelected(genreId);
    if (genreExists) {
      newBook.genres = newBook.genres.filter(g => g.id !== genreId);
    } else {
      // Add the new genre, removing the placeholder if it's the first real genre
      const currentGenres = newBook.genres.filter(g => g.id !== null && g.id !== undefined);
      newBook.genres = [...currentGenres, { id: genreId }];
    }
  }

  // Handler for adding a new genre via prompt
  async function addNewGenreToList() {
    const name = prompt("Nom du nouveau genre :");
    if (!name || !name.trim()) return;
    try {
      const newGenre = await addGenre(name.trim()); // Call your actual service
      availableGenres = [...availableGenres, newGenre];
      // Optionally auto-select the newly added genre
      // toggleGenre(newGenre.id); 
    } catch (err) {
      alert("Erreur lors de l'ajout du genre : " + (err.message || err));
    }
  }

  function addExemplaire() {
    exemplaires = [
      ...exemplaires,
      { id: Math.random().toString(36).slice(2), lender: null }
    ];
  }

  function removeExemplaire(idx) {
    exemplaires = exemplaires.filter((_, i) => i !== idx);
  }

  function closePopup() {
    show = false; // This will trigger the $bindable update
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
          {#if copiesStatus.processing}
            <div class="copies-status">
              Création des exemplaires en cours... ({copiesStatus.created}/{copiesStatus.total})
            </div>
          {:else}
            <div class="copies-status">
              {copiesStatus.created} exemplaire(s) créé(s) sur {copiesStatus.total}
              {#if copiesStatus.errors.length > 0}
                <ul class="copies-errors">
                  {#each copiesStatus.errors as error}
                    <li>{error}</li>
                  {/each}
                </ul>
              {/if}
              
              {#if copiesStatus.loans.total > 0}
                {#if copiesStatus.loans.processing}
                  <div class="loans-status">
                    Création des prêts en cours... ({copiesStatus.loans.created}/{copiesStatus.loans.total})
                  </div>
                {:else}
                  <div class="loans-status">
                    {copiesStatus.loans.created} prêt(s) au Cartel créé(s) sur {copiesStatus.loans.total}
                    {#if copiesStatus.loans.errors.length > 0}
                      <ul class="loans-errors">
                        {#each copiesStatus.loans.errors as error}
                          <li>{error}</li>
                        {/each}
                      </ul>
                    {/if}
                  </div>
                {/if}
              {/if}
            </div>
          {/if}
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
                <input type="number" bind:value={newBook.publicationYear} min="1000" max={new Date().getFullYear()} />
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
                    Auteurs
                    <PeopleSelector
                      bind:selectedPeople={newBook.authors}
                      placeholder="Sélectionner un/des auteurs"
                      addObject={handleAddNewAuthor}
                      searchObjects={searchAuthorsLocal}/>
                </label>
                <label>
                    Illustrateurs
                    <PeopleSelector
                      bind:selectedPeople={newBook.illustrator}
                      placeholder="Sélectionnez un/des illustrateurs"
                      addObject={handleAddNewIllustrator}
                      searchObjects={searchIllustratorsLocal}/>
                </label>
            </div>
            <div class="form-row">
                <label class="full-width">
                    Description:
                    <textarea bind:value={newBook.description} rows="4"></textarea>
                </label>
            </div>
          </div>
          <!-- Genres -->
          <div class="form-section">
            <h4>Genre(s)</h4>
            <div class="genre-chips">
              {#each availableGenres as availableGenre (availableGenre.id)}
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
                onclick={addNewGenreToList}
                aria-label="Ajouter un genre"
              >
                + Ajouter un genre
              </button>
            </div>
          </div>
          <!-- Exemplaires -->
          <div class="form-section">
            <h4>Exemplaire(s)</h4>
            {#each exemplaires as exemplaire, idx (exemplaire.id)}
              <div class="exemplaire-row">
                <span>Exemplaire {idx + 1}</span>
                <button type="button" class="remove-exemplaire-btn" onclick={() => removeExemplaire(idx)} disabled={exemplaires.length === 1}>✕</button>
                <div class="lender-section">
                  <label>
                    Prêté par un membre ?
                    <MemberSelector
                      bind:selectedMember={exemplaire.lender}
                      placeholder="Sélectionner ou ajouter un membre"
                      searchObjects={searchMembers}
                      addObject={handleAddNewMember}
                    />
                  </label>
                </div>
              </div>
            {/each}
            <button type="button" class="add-exemplaire-btn" onclick={addExemplaire}>+ Ajouter un exemplaire</button>
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
  .exemplaire-row {
    display: flex;
    align-items: center;
    gap: 1rem;
    margin-bottom: 1rem;
    background: #232323;
    border-radius: 6px;
    padding: 0.7rem 1rem;
    position: relative;
    .remove-exemplaire-btn {
      background: none;
      border: none;
      color: var(--accent);
      font-size: 1.2rem;
      cursor: pointer;
      margin-left: 0.5rem;
      &:disabled {
        opacity: 0.5;
        cursor: not-allowed;
      }
    }
    .lender-section {
      flex: 1;
      min-width: 220px;
    }
  }
  .add-exemplaire-btn {
    background: var(--accent);
    color: #fff;
    border: none;
    border-radius: 4px;
    padding: 0.4rem 1.2rem;
    font-size: 1rem;
    font-weight: bold;
    margin-top: 0.5rem;
    cursor: pointer;
    &:hover {
      filter: brightness(0.9);
    }
  }
  .copies-status, .loans-status {
    margin-top: 1rem;
    font-size: 0.95rem;
    font-weight: normal;
  }
  
  .copies-errors, .loans-errors {
    margin-top: 0.5rem;
    color: var(--accent);
    text-align: left;
    font-size: 0.9rem;
    background: rgba(255, 61, 0, 0.1);
    border-radius: 4px;
    padding: 0.5rem 1rem;
  }
  
  .loans-status {
    margin-top: 1.5rem;
    padding-top: 0.5rem;
    border-top: 1px dashed var(--secondary);
  }
</style>