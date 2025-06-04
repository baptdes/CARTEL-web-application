<script>
  import { createEventDispatcher } from 'svelte';
  import * as loanService from '$lib/services/loanService';
  import * as itemService from '$lib/services/itemService';
  import * as copyService from '$lib/services/copyService';
  import MemberSelector from './MemberSelector.svelte';
  import ItemSelector from './ItemSelector.svelte';

  let { show = $bindable(false), isEmpruntMode = $bindable(true) } = $props();
  
  const dispatch = createEventDispatcher();
  
  // State for loan creation
  let newLoan = $state({
    person: null,
    itemCopyId: ""
  });
  
  let error = $state(null);
  let selectedItem = $state(null);
  let searchingCopies = $state(false);
  let availableCopies = $state([]);
  
  // Reset form when popup opens
  $effect(() => {
    if (show) {
      resetForm();
    }
  });

  $effect(() => {
    if (selectedItem && isEmpruntMode) {
      loadItemCopies(selectedItem.barcode);
      console.log("Selected item for emprunt mode:", selectedItem.barcode);
    }
  });
  
  function resetForm() {
    newLoan = {
      person: null,
      itemCopyId: ""
    };
    selectedItem = null;
    error = null;
    availableCopies = [];
    searchingCopies = false;
  }

  // Load available copies for an item (for emprunt mode)
  async function loadItemCopies(itemId) {
    if (!itemId) return;

    searchingCopies = true;
    try {
      const response = await copyService.searchItemCopies(itemId);
      const copies = Array.isArray(response) ? response : response.content || [];
      console.log("Loaded item copies:", copies);

      // Fix: Use copy.borrowable instead of copy.isBorrowable
      availableCopies = copies.filter(copy => copy.borrowable);

      console.log("Available copies filtered:", availableCopies);
    } catch (err) {
      console.error("Error loading item copies:", err);
      error = "Erreur lors du chargement des copies disponibles";
    } finally {
      searchingCopies = false;
    }
  }

  // Function to search for members (persons)
  async function searchMembers(query) {
    try {
      const result = await loanService.searchPersons(query);
      return result.content || [];
    } catch (err) {
      console.error("Error searching for members:", err);
      return [];
    }
  }

  // Function to search for items
  async function searchItems(query) {
    try {
      const params = {
        name: query,
        pageSize: 10,
        pageNumber: 0
      };
      return await itemService.searchItems(params);
    } catch (err) {
      console.error("Error searching for items:", err);
      return { content: [] };
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
      
      const addedMember = await loanService.addPerson(newMemberPayload);
      return addedMember;
    } catch (err) {
      console.error("Erreur lors de l'ajout du membre:", err);
      throw new Error("Erreur serveur lors de l'ajout du membre: " + (err.message || err));
    }
  }

  // Add new loan
  async function addLoan() {
    if (!newLoan.person) {
      error = "Veuillez sélectionner une personne";
      return;
    }

    try {
      if (isEmpruntMode) {
        // For loans BY Cartel, we need an item copy ID
        if (!newLoan.itemCopyId) {
          error = "Veuillez sélectionner une copie d'objet";
          return;
        }
        
        await loanService.createLoanByCartel(newLoan.person.id, newLoan.itemCopyId);
      } else {
        // For loans TO Cartel, we need an item ID (not an item copy)
        if (!selectedItem) {
          error = "Veuillez sélectionner un objet";
          return;
        }
        
        // Use the special endpoint that creates a copy automatically
        await loanService.createLoanToCartel(newLoan.person.id, selectedItem.barcode);
      }

      dispatch('added');
      closePopup();
    } catch (err) {
      console.error("Error adding loan:", err);
      error = "Erreur lors de l'ajout du prêt: " + (err.message || err);
    }
  }

  function closePopup() {
    show = false; // This will trigger the $bindable update
    dispatch('close');
    resetForm();
  }

  function selectCopy(copyId) {
    newLoan.itemCopyId = copyId;
  }
</script>

{#if show}
  <div class="dialog-overlay">
    <div class="dialog">
      <h2>Ajouter {isEmpruntMode ? "un emprunt" : "un prêt"}</h2>

      <div class="dialog-field">
        <label>
          {isEmpruntMode ? "Emprunteur" : "Prêteur"}:
        </label>
        <MemberSelector
          bind:selectedMember={newLoan.person}
          placeholder="Sélectionner ou ajouter une personne"
          searchObjects={searchMembers}
          addObject={handleAddNewMember}
        />
      </div>

      <div class="dialog-field">
        <label>Objet:</label>
        <ItemSelector
          bind:selectedItem={selectedItem}
          placeholder="Rechercher un objet"
          searchObjects={searchItems}
        />
      </div>

      {#if isEmpruntMode && selectedItem}
        <!-- For "emprunt par Cartel", we need to select a specific item copy -->
        <div class="dialog-field">
          <label>Sélectionner une copie:</label>
          
          {#if searchingCopies}
            <div class="loading">Chargement des copies...</div>
          {:else if availableCopies.length === 0}
            <div class="no-copies">Aucune copie disponible pour cet objet</div>
          {:else}
            <div class="copies-list">
              {#each availableCopies as copy}
                <!-- svelte-ignore a11y_no_static_element_interactions -->
                <!-- svelte-ignore a11y_click_events_have_key_events -->
                <div 
                  class="copy-item" 
                  class:selected={newLoan.itemCopyId === copy.id.toString()}
                  onclick={() => selectCopy(copy.id.toString())}
                >
                  Copie #{copy.id}
                </div>
              {/each}
            </div>
          {/if}
        </div>
      {:else if !isEmpruntMode && selectedItem}
        <!-- For "prêt au Cartel", inform that a copy will be created -->
        <div class="dialog-field info-box">
          <p>Une nouvelle copie de <strong>{selectedItem.name}</strong> sera automatiquement créée.</p>
        </div>
      {/if}

      {#if error}
        <div class="error-message">{error}</div>
      {/if}

      <div class="dialog-actions">
        <button class="admin-button" onclick={addLoan}>Ajouter</button>
        <button class="admin-button cancel" onclick={closePopup}>Annuler</button>
      </div>
    </div>
  </div>
{/if}

<style lang="scss">
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
    border-radius: 0px;
    width: 90%;
    max-width: 500px;
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);

    h2 {
      margin-top: 0;
      text-align: center;
      margin-bottom: 1rem;

      color: var(--back);
      -webkit-text-stroke: 1px var(--accent);
      text-transform: uppercase;
      font-size: 2.5rem;
      font-family: Guisol;
      transform: scaleY(1.5);
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
      background-color: var(--tertiary);
      color: var(--primary);
      
      &.disabled {
        background-color: var(--secondary);
        opacity: 0.6;
        cursor: not-allowed;
      }
    }
    
    .hint {
      display: block;
      color: var(--secondary);
      margin-top: 0.3em;
      font-style: italic;
    }
  }

  .copies-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
    gap: 0.5em;
    margin-bottom: 1em;
    
    .copy-item {
      padding: 0.5em;
      border: 1px solid var(--secondary);
      text-align: center;
      cursor: pointer;
      
      &:hover {
        background-color: var(--tertiary);
      }
      
      &.selected {
        background-color: var(--primary);
        color: var(--back);
      }
    }
  }

  .loading, .no-copies {
    padding: 1em;
    text-align: center;
    color: var(--secondary);
    border: 1px dashed var(--secondary);
    margin-bottom: 1em;
  }

  .info-box {
    padding: 0.8em;
    border: 1px dashed var(--primary);
    color: var(--primary);
    text-align: center;
    
    p {
      margin: 0;
    }
  }

  .error-message {
    color: var(--accent);
    text-align: center;
    padding: 0.5rem;
    border: 1px solid var(--accent);
    border-radius: 4px;
    background-color: rgba(255, 61, 0, 0.1);
    margin-bottom: 1rem;
  }

  .dialog-actions {
    display: flex;
    justify-content: flex-end;
    gap: 1em;

    button {
      padding: 0.5em 1em;
      background-color: var(--primary);
      color: var(--back);
    }

    .cancel {
      color: var(--secondary);
      background-color: var(--back);
    }
  }
</style>
