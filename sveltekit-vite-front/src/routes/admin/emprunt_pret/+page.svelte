<script>
  import { onMount } from "svelte";
  import { goto } from "$app/navigation";
  import { adminPageState } from "../store.js";
  import { isAuthenticated, logout } from "$lib/auth";
  import DoubleText from "$lib/misc/DoubleText.svelte";
  import PointBar from "$lib/misc/PointBar.svelte";
  import * as loanService from "$lib/services/loanService";
  import AddLoanPopup from "$lib/components/admin/AddLoanPopup.svelte";
  import LoanCard from "$lib/components/admin/LoanCard.svelte";

  // Mode toggle - true for emprunts (loanByCartel), false for prêts (loanToCartel)
  let isEmpruntMode = $state(true);

  // Search state
  let searchTerm = $state("");
  let isSearching = $state(false);

  // Sorting and filtering state
  let sortField = $state("loanDate");
  let sortAsc = $state(false);
  let filterActive = $state(null); // null = all, true = active only, false = completed only
  let filterDateStart = $state(null);
  let filterDateEnd = $state(null);

  // Data state
  let loans = $state([]);
  let loading = $state(true);
  let error = $state(null);

  // Dialog state for adding loans
  let showAddDialog = $state(false);

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
        filters.itemName = searchTerm;
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

  // Handle loan added event
  function handleLoanAdded() {
    loadLoans();
  }

  // Handle events from the LoanCard component
  function handleLoanComplete(event) {
    completeLoan(event.detail.id);
  }

  function handleLoanDelete(event) {
    deleteLoan(event.detail.id);
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
        onclick={() => {
          isEmpruntMode = true;
          loadLoans();
        }}
      >
        Emprunts
      </button>
      <button
        class="toggle-btn {!isEmpruntMode ? 'active' : ''}"
        onclick={() => {
          isEmpruntMode = false;
          loadLoans();
        }}
      >
        Prêts
      </button>
    </div>

    <button class="admin-button" onclick={() => (showAddDialog = true)}>
      Ajouter {isEmpruntMode ? "un emprunt" : "un prêt"}
    </button>
  </div>

  <div class="search-bar">
    <input
      type="text"
      bind:value={searchTerm}
      placeholder="Rechercher par nom de l'objet..."
      onkeyup={(event) => event.key === "Enter" && handleSearch()}
    />
    <button class="admin-button" onclick={handleSearch}>Rechercher</button>
  </div>

  <div class="filter-controls">
    <div class="filter-group">
      <label for="sortField">Trier par:</label>
      <select id="sortField" bind:value={sortField} onchange={loadLoans}>
        <option value="loanDate">Date d'emprunt</option>
        <option value="endDate">Date de retour</option>
        {#if isEmpruntMode}
          <option value="itemBorrower.firstname">Emprunteur</option>
        {:else}
          <option value="itemOwner.firstname">Prêteur</option>
        {/if}
        <option value="itemShared.objet.name">Nom de l'objet</option>
      </select>

      <button class="sort-dir" onclick={() => { sortAsc = !sortAsc; loadLoans(); }}>
        {sortAsc ? '↑' : '↓'}
      </button>
    </div>

    <div class="filter-group">
      <label for="filterActive">Statut:</label>
      <select id="filterActive" bind:value={filterActive} onchange={loadLoans}>
        <option value={null}>Tous</option>
        <option value={true}>Actifs</option>
        <option value={false}>Terminés</option>
      </select>
    </div>

    <div class="filter-group">
      <label for="filterDateStart">Période du:</label>
      <input id="filterDateStart" type="date" bind:value={filterDateStart} onchange={loadLoans} />
      <label for="filterDateEnd">au:</label>
      <input id="filterDateEnd" type="date" bind:value={filterDateEnd} onchange={loadLoans} />
    </div>

    <button class="admin-button reset-btn" onclick={resetFilters}>
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
      {#if isSearching}
        Aucun résultat trouvé
      {:else}
        Aucun prêt disponible
      {/if}
    </div>
  {:else}
    <div class="results">
      {#each loans as loan}
        <LoanCard 
          loan={loan} 
          isEmpruntMode={isEmpruntMode}
          on:complete={handleLoanComplete}
          on:delete={handleLoanDelete}
        />
      {/each}
    </div>
  {/if}

  <!-- Add Loan Dialog using the new component -->
  <AddLoanPopup 
    show={showAddDialog} 
    isEmpruntMode={isEmpruntMode}
    on:close={() => showAddDialog = false}
    on:added={handleLoanAdded}
  />
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
    border: 1px solid var(--primary);
    overflow: hidden;
    padding: 0.5em;
  }

  .toggle-btn {
    color : var(--primary);
    padding: 0.5em 1em;
    background: none;
    border: none;
    border-radius: 0;
    font-family: Guisol;
    font-size: 1.2em;
    cursor: pointer;
    transition: background-color 0.3s;

    &.active {
      border: 2px solid var(--primary);
      color: var(--accent);
    }

    &:hover {
      color: var(--accent);
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
    margin-bottom: 1.5em;
    justify-content: space-between;
    align-items: center;
  }

  .filter-group {
    display: flex;
    align-items: center;
    gap: 0.5em;
    border-radius: 0;

    label {
      font-weight: bold;
      color: var(--primary);
    }

    select,
    input[type="date"] {
      padding: 0.3em 0.5em;
      border: 1px solid var(--primary);
      font-family: inherit;
      background-color: var(--back);
      color: var(--secondary);
    }
  }

  .sort-dir {
    background: var(--back);
    color: white;
    width: 30px;
    height: 30px;
    display: flex;
    align-items: center;
    justify-content: center;
    border: none;
    cursor: pointer;
    font-size: 1.2em;
    padding: 0;
    border-radius: 0;
  }

  .reset-btn {
    border : 1px solid var(--accent);
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
</style>
