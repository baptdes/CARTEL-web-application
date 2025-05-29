<script>
  import { onMount } from "svelte";
  import { goto } from "$app/navigation";
  import { adminPageState } from "../store.js";
  import { isAuthenticated, logout } from "$lib/auth";
  import DoubleText from "$lib/misc/DoubleText.svelte";
  import PointBar from "$lib/misc/PointBar.svelte";
  import * as loanService from "$lib/services/loanService";

  // Mode toggle - true for emprunts (loanByCartel), false for prêts (loanToCartel)
  let isEmpruntMode = true;

  // Search state
  let searchTerm = "";
  let isSearching = false;

  // Sorting and filtering state
  let sortField = "loanDate";
  let sortAsc = false;
  let filterActive = null; // null = all, true = active only, false = completed only
  let filterDateStart = null;
  let filterDateEnd = null;

  // Data state
  let loans = [];
  let loading = true;
  let error = null;

  // Dialog state for adding loans
  let showAddDialog = false;
  let newLoan = {
    personId: "",
    itemCopyId: "",
    personName: "",
  };

  // Person search for adding loans
  let personSearchResults = [];
  let personSearchTerm = "";
  let showPersonSearch = false;

  // Load loans based on current state
  async function loadLoans() {
    loading = true;
    error = null;

    try {
      const filters = {
        pageNumber: 0,
        pageSize: 50,
        asc: sortAsc,
        sortBy: sortField,
        active: filterActive,
        startDateAfter: filterDateStart,
        endDateBefore: filterDateEnd,
      };

      if (searchTerm) {
        if (isEmpruntMode) {
          filters.borrowerFirstName = searchTerm;
          filters.borrowerSurname = searchTerm;
        } else {
          filters.ownerFirstName = searchTerm;
          filters.ownerSurname = searchTerm;
        }
      }

      if (isEmpruntMode) {
        loans = await loanService.filterLoansByCartel(filters);
      } else {
        loans = await loanService.filterLoansToCartel(filters);
      }
    } catch (e) {
      console.error("Error loading loans:", e);
      error = "Erreur lors du chargement des données";
    } finally {
      loading = false;
    }
  }

  // Complete a loan
  async function completeLoan(id) {
    try {
      if (isEmpruntMode) {
        await loanService.completeLoanByCartel(id);
      } else {
        await loanService.completeLoanToCartel(id);
      }
      await loadLoans(); // Reload after completion
    } catch (e) {
      console.error("Error completing loan:", e);
      error = "Erreur lors de la complétion du prêt";
    }
  }

  // Delete a loan
  async function deleteLoan(id) {
    if (!confirm("Êtes-vous sûr de vouloir supprimer ce prêt ?")) {
      return;
    }

    try {
      if (isEmpruntMode) {
        await loanService.deleteLoanByCartel(id);
      } else {
        await loanService.deleteLoanToCartel(id);
      }
      await loadLoans(); // Reload after deletion
    } catch (e) {
      console.error("Error deleting loan:", e);
      error = "Erreur lors de la suppression du prêt";
    }
  }

  // Search for persons
  async function searchPersons() {
    if (!personSearchTerm) return;

    try {
      const results = await loanService.searchPersons(personSearchTerm);
      personSearchResults = results.content || [];
    } catch (e) {
      console.error("Error searching for persons:", e);
      error = "Erreur lors de la recherche de personnes";
    }
  }

  // Select a person for new loan
  function selectPerson(person) {
    newLoan.personId = person.id;
    newLoan.personName = `${person.firstname} ${person.surname}`;
    showPersonSearch = false;
  }

  // Add new loan
  async function addLoan() {
    if (!newLoan.personId || !newLoan.itemCopyId) {
      error = "Veuillez remplir tous les champs";
      return;
    }

    try {
      if (isEmpruntMode) {
        await loanService.createLoanByCartel(newLoan.personId, newLoan.itemCopyId);
      } else {
        await loanService.createLoanToCartel(newLoan.personId, newLoan.itemCopyId);
      }

      showAddDialog = false;
      newLoan = { personId: "", itemCopyId: "", personName: "" };
      await loadLoans(); // Reload after adding
    } catch (e) {
      console.error("Error adding loan:", e);
      error = "Erreur lors de l'ajout du prêt";
    }
  }

  // Format date for display
  function formatDate(dateStr) {
    if (!dateStr) return "N/A";
    const date = new Date(dateStr);
    return new Intl.DateTimeFormat("fr-FR").format(date);
  }

  // Handle search input
  function handleSearch() {
    isSearching = true;
    loadLoans();
  }

  // Reset filters
  function resetFilters() {
    searchTerm = "";
    sortField = "loanDate";
    sortAsc = false;
    filterActive = null;
    filterDateStart = null;
    filterDateEnd = null;
    isSearching = false;
    loadLoans();
  }

  // Toggle mode and reload data
  function toggleMode() {
    isEmpruntMode = !isEmpruntMode;
    loadLoans();
  }

  onMount(() => {
    loadLoans();
  });
</script>

<main>
  <DoubleText text="Emprunts/Prêts" size="4em" />
  <PointBar Color="var(--accent)" width="70%" />

  <div class="control-bar">
    <div class="toggle-container">
      <button
        class="toggle-btn {isEmpruntMode ? 'active' : ''}"
        on:click={() => {
          isEmpruntMode = true;
          loadLoans();
        }}
      >
        Emprunts
      </button>
      <button
        class="toggle-btn {!isEmpruntMode ? 'active' : ''}"
        on:click={() => {
          isEmpruntMode = false;
          loadLoans();
        }}
      >
        Prêts
      </button>
    </div>

    <button class="admin-button add-btn" on:click={() => (showAddDialog = true)}>
      <i class="fas fa-plus"></i> Ajouter {isEmpruntMode ? "un emprunt" : "un prêt"}
    </button>
  </div>

  <div class="search-bar">
    <input
      type="text"
      bind:value={searchTerm}
      placeholder="Rechercher par nom/prénom..."
      on:keyup={(event) => event.key === "Enter" && handleSearch()}
    />
    <button class="admin-button" on:click={handleSearch}>Rechercher</button>
  </div>

  <div class="filter-controls">
    <div class="filter-group">
      <label for="sortField">Trier par:</label>
      <select id="sortField" bind:value={sortField} on:change={loadLoans}>
        <option value="loanDate">Date d'emprunt</option>
        <option value="endDate">Date de retour</option>
        {#if isEmpruntMode}
        <option value="itemBorrower.firstname">Emprunteur</option>
        {:else}
        <option value="itemOwner.firstname">Prêteur</option>
        {/if}
        <option value="itemShared.objet.name">Nom de l'objet</option>
      </select>

      <button class="sort-dir" on:click={() => { sortAsc = !sortAsc; loadLoans(); }}>
        {sortAsc ? '↑' : '↓'}
      </button>
    </div>

    <div class="filter-group">
      <label for="filterActive">Statut:</label>
      <select id="filterActive" bind:value={filterActive} on:change={loadLoans}>
        <option value={null}>Tous</option>
        <option value={true}>Actifs</option>
        <option value={false}>Terminés</option>
      </select>
    </div>

    <div class="filter-group">
      <label>Période du:</label>
      <input type="date" bind:value={filterDateStart} on:change={loadLoans} />
      <label>au:</label>
      <input type="date" bind:value={filterDateEnd} on:change={loadLoans} />
    </div>

    <button class="admin-button reset-btn" on:click={resetFilters}>
      Réinitialiser
    </button>
  </div>

  {#if error}
  <div class="error">{error}</div>
  {/if}

  {#if loading}
  <div class="loading">Chargement en cours...</div>
  {:else if loans.length === 0}
  <div class="no-results">
    {isSearching ? "Aucun résultat trouvé" : "Aucun prêt disponible"}
  </div>
  {:else}
  <div class="results">
    {#each loans as loan}
    <div class="item">
      <div class="itemInfo">
        <img
          src={loan.itemShared?.objet?.coverImage || "/placeholder_game.png"}
          class="itemImage"
          alt="Image de {loan.itemShared?.objet?.name}"
        />
        <div class="itemDetails">
          <p class="type">
            {isEmpruntMode ? "Emprunt" : "Prêt"} —
            {loan.endDate ? "Terminé" : "En cours"}
          </p>
          <p class="title">{loan.itemShared?.objet?.name || "Sans titre"}</p>
          <p class="details">
            {#if isEmpruntMode}
            Emprunté par <strong>{loan.itemBorrower?.firstname} {loan.itemBorrower?.surname}</strong>
            {:else}
            Prêté par <strong>{loan.itemOwner?.firstname} {loan.itemOwner?.surname}</strong>
            {/if}
            <br />
            Date d'emprunt: <strong>{formatDate(loan.loanDate)}</strong>
            {#if loan.endDate}
            <br />
            Date de retour: <strong>{formatDate(loan.endDate)}</strong>
            {/if}
          </p>
        </div>
      </div>

      <div class="loanInfo">
        {#if !loan.endDate}
        <button class="complete-btn" on:click={() => completeLoan(loan.id)}>
          Terminer
        </button>
        {/if}
      </div>

      <button class="cross" on:click={() => deleteLoan(loan.id)}>
        <img src="/src/assets/img/icons/star_rf.svg" alt="cross" />
      </button>
    </div>
    {/each}
  </div>
  {/if}

  <!-- Add Loan Dialog -->
  {#if showAddDialog}
  <div class="dialog-overlay">
    <div class="dialog">
      <h2>Ajouter {isEmpruntMode ? "un emprunt" : "un prêt"}</h2>

      <div class="dialog-field">
        <label>Personne:</label>
        {#if newLoan.personName}
        <div class="selected-person">
          {newLoan.personName}
          <button on:click={() => { newLoan.personId = ""; newLoan.personName = ""; }}>✕</button>
        </div>
        {:else}
        <div class="person-search">
          <input
            type="text"
            bind:value={personSearchTerm}
            placeholder="Rechercher une personne..."
            on:focus={() => (showPersonSearch = true)}
            on:keyup={(event) => event.key === "Enter" && searchPersons()}
          />
          <button on:click={searchPersons}>🔍</button>

          {#if showPersonSearch && personSearchResults.length > 0}
          <div class="search-results">
            {#each personSearchResults as person}
            <div class="person-result" on:click={() => selectPerson(person)}>
              {person.firstname} {person.surname}
            </div>
            {/each}
          </div>
          {/if}
        </div>
        {/if}
      </div>

      <div class="dialog-field">
        <label>ID de l'objet:</label>
        <input type="text" bind:value={newLoan.itemCopyId} placeholder="ID de la copie" />
      </div>

      <div class="dialog-actions">
        <button class="admin-button" on:click={addLoan}>Ajouter</button>
        <button class="admin-button cancel" on:click={() => (showAddDialog = false)}>Annuler</button>
      </div>
    </div>
  </div>
  {/if}
</main>

<style lang="scss">
  @use "../../../lib/sass/base";

  main {
    flex: auto;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 0 2em;
  }

  .control-bar {
    display: flex;
    width: 90%;
    justify-content: space-between;
    margin: 1em 0;
    align-items: center;
  }

  .toggle-container {
    display: flex;
    border: 2px solid var(--primary);
    border-radius: 4px;
    overflow: hidden;
  }

  .toggle-btn {
    padding: 0.5em 1em;
    background: none;
    border: none;
    font-family: Guisol;
    font-size: 1.2em;
    cursor: pointer;
    transition: background-color 0.3s;

    &.active {
      background-color: var(--primary);
      color: var(--back);
    }

    &:not(.active):hover {
      background-color: rgba(var(--primary-rgb), 0.2);
    }
  }

  .add-btn {
    padding: 0.5em 1em;
    font-size: 1em;
  }

  input {
    @extend .admin-button;
    width: 100%;
    min-height: 50px;
    padding: 0.2em;
    font-family: Guisol;
    font-size: 1.5em;
    text-transform: uppercase;
  }

  .search-bar {
    width: 90%;
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 1rem;
    margin-bottom: 1em;
  }

  .filter-controls {
    width: 90%;
    display: flex;
    flex-wrap: wrap;
    gap: 1em;
    margin-bottom: 1.5em;
    justify-content: space-between;
    padding: 0.5em;
    background-color: rgba(var(--secondary-rgb), 0.1);
    border-radius: 4px;
    align-items: center;
  }

  .filter-group {
    display: flex;
    align-items: center;
    gap: 0.5em;

    label {
      font-weight: bold;
      color: var(--secondary);
    }

    select,
    input[type="date"] {
      padding: 0.3em 0.5em;
      border: 1px solid var(--secondary);
      border-radius: 4px;
      background-color: var(--back);
      font-family: inherit;
      color: var(--secondary);
    }
  }

  .sort-dir {
    background: var(--primary);
    color: white;
    width: 30px;
    height: 30px;
    display: flex;
    align-items: center;
    justify-content: center;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 1.2em;
    padding: 0;
  }

  .reset-btn {
    padding: 0.3em 0.8em;
    font-size: 0.9em;
    background-color: var(--secondary);
  }

  .error {
    color: red;
    margin: 1em 0;
    padding: 1em;
    border: 1px solid red;
    border-radius: 4px;
    width: 90%;
    text-align: center;
  }

  .loading,
  .no-results {
    margin: 2em 0;
    font-size: 1.2em;
    color: var(--secondary);
    text-align: center;
  }

  .results {
    width: 90%;
    display: flex;
    flex-direction: column;
    gap: 1em;
  }

  .item {
    display: flex;
    flex-grow: 1;
    gap: 0.5em;
    min-height: 160px;
    width: 100%;
    flex-direction: row;
    border: 1px solid var(--primary);
    padding: 0.5em;
  }

  .itemInfo {
    display: flex;
    flex-direction: row;
    gap: 1em;
    flex-grow: 3;

    .itemImage {
      aspect-ratio: 1;
      object-fit: cover;
      max-height: 150px;
    }

    .itemDetails {
      display: flex;
      flex-direction: column;
      gap: 0.1em;
      font-size: 1.2em;
      align-content: space-between;
      margin-top: 0.5em;
      margin-bottom: 0.5em;
      margin-right: 0.5em;

      .type {
        color: var(--accent);
        font-size: 0.8em;
        text-transform: uppercase;
      }

      .title {
        font-weight: bold;
        font-size: 1.5em;
        color: var(--primary);
      }

      .details {
        min-width: 0px;
        font-size: 0.9em;
        color: var(--secondary);
        height: 100%;
        word-wrap: break-word;

        strong {
          color: var(--primary);
        }
      }
    }
  }

  .loanInfo {
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1px solid var(--accent);
    padding: 0.5em;
    flex-grow: 1;
    color: var(--accent);

    .complete-btn {
      background-color: var(--accent);
      color: white;
      border: none;
      padding: 0.5em 1em;
      cursor: pointer;
      border-radius: 4px;
      text-transform: uppercase;
      font-family: inherit;
      font-weight: bold;

      &:hover {
        opacity: 0.9;
      }
    }
  }

  .cross {
    width: 40px;
    height: 40px;
    flex-shrink: 0;
    position: relative;
    background-color: var(--back);
    padding: 0%;
    border: 1px solid var(--accent);
    border-radius: 0%;

    img {
      width: 100%;
      height: 100%;
      object-fit: contain;
      rotate: 45deg;
      transition: rotate 1s ease-out;
    }

    &:hover {
      img {
        rotate: 315deg;
      }
    }
  }

  .dialog-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.7);
    z-index: 1000;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .dialog {
    background-color: var(--back);
    padding: 2em;
    border-radius: 4px;
    width: 90%;
    max-width: 500px;
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);

    h2 {
      margin-top: 0;
      color: var(--primary);
      text-align: center;
      margin-bottom: 1.5em;
    }
  }

  .dialog-field {
    margin-bottom: 1.5em;

    label {
      display: block;
      margin-bottom: 0.5em;
      font-weight: bold;
      color: var(--secondary);
    }

    input {
      width: 100%;
      padding: 0.5em;
      font-size: 1em;
      border: 1px solid var(--secondary);
      border-radius: 4px;
    }
  }

  .dialog-actions {
    display: flex;
    justify-content: flex-end;
    gap: 1em;

    button {
      padding: 0.5em 1em;
    }

    .cancel {
      background-color: var(--secondary);
    }
  }

  .person-search {
    position: relative;
    display: flex;

    input {
      flex-grow: 1;
    }

    button {
      width: 40px;
      padding: 0;
    }
  }

  .search-results {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    background-color: var(--back);
    border: 1px solid var(--secondary);
    z-index: 10;
    max-height: 200px;
    overflow-y: auto;

    .person-result {
      padding: 0.5em;
      cursor: pointer;

      &:hover {
        background-color: rgba(var(--primary-rgb), 0.1);
      }
    }
  }

  .selected-person {
    padding: 0.5em;
    border: 1px solid var(--primary);
    border-radius: 4px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    button {
      background-color: transparent;
      border: none;
      color: var(--secondary);
      cursor: pointer;
      font-size: 1.2em;

      &:hover {
        color: var(--primary);
      }
    }
  }
</style>
