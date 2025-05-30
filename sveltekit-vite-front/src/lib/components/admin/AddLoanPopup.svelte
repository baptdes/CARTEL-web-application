<script>
  import { createEventDispatcher } from 'svelte';
  import * as loanService from '$lib/services/loanService';
  import MemberSelector from './MemberSelector.svelte';

  let { show = $bindable(false), isEmpruntMode = $bindable(true) } = $props();
  
  const dispatch = createEventDispatcher();
  
  // State for loan creation
  let newLoan = $state({
    person: null,
    itemCopyId: ""
  });
  
  let error = $state(null);
  
  // Reset form when popup opens
  $effect(() => {
    if (show) {
      resetForm();
    }
  });
  
  function resetForm() {
    newLoan = {
      person: null,
      itemCopyId: ""
    };
    error = null;
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
    if (!newLoan.person || !newLoan.itemCopyId) {
      error = "Veuillez remplir tous les champs";
      return;
    }

    try {
      if (isEmpruntMode) {
        await loanService.createLoanByCartel(newLoan.person.id, newLoan.itemCopyId);
      } else {
        await loanService.createLoanToCartel(newLoan.person.id, newLoan.itemCopyId);
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
</script>

{#if show}
  <div class="dialog-overlay">
    <div class="dialog">
      <h2>Ajouter {isEmpruntMode ? "un emprunt" : "un prêt"}</h2>

      <div class="dialog-field">
        <label>Personne:</label>
        <MemberSelector
          bind:selectedMember={newLoan.person}
          placeholder="Sélectionner ou ajouter une personne"
          searchObjects={searchMembers}
          addObject={handleAddNewMember}
        />
      </div>

      <div class="dialog-field">
        <label>ID de l'objet:</label>
        <input type="text" bind:value={newLoan.itemCopyId} placeholder="ID de la copie" />
      </div>

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
      background-color: var(--tertiary);
      color: var(--primary);
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
    }

    .cancel {
      background-color: var(--secondary);
    }
  }
</style>
