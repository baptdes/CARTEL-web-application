<script>
  import { createEventDispatcher } from 'svelte';

  let {
    selectedMember = $bindable(null),
    placeholder = "Rechercher un membre...",
    addObject = null,
    searchObjects = null
  } = $props();

  const dispatch = createEventDispatcher();

  // Recherche
  let searchInput = $state('');
  let suggestions = $state([]);
  let showSuggestions = $state(false);
  let selectedSuggestionIndex = $state(-1);

  // Ajout membre
  let firstname = $state('');
  let surname = $state('');
  let contact = $state('');
  let caution = $state('');
  let addItemError = $state('');
  let showAddForm = $state(false);

  // Affichage membre
  const displayMember = (item) => {
      return `${item.firstname} ${item.surname} ${item.contact ? `(${item.contact})` : ''}`;
  };

  // Recherche suggestions
  $effect(() => {
    if (!searchInput.trim() || !searchObjects) {
      resetSuggestions();
      return;
    }

    const input = searchInput.trim().toLowerCase();
    Promise.resolve(searchObjects(input)).then(results => {
      suggestions = results;
      showSuggestions = results.length > 0;
      selectedSuggestionIndex = results.length > 0 ? 0 : -1;
    });
  });

  function resetSuggestions() {
    suggestions = [];
    showSuggestions = false;
    selectedSuggestionIndex = -1;
  }

  function selectItem(item) {
    selectedMember = item;
    searchInput = '';
    resetSuggestions();
  }

  function handleKeydown(e) {
    if (!showSuggestions || suggestions.length === 0) return;

    switch(e.key) {
      case 'ArrowDown':
        e.preventDefault();
        selectedSuggestionIndex = (selectedSuggestionIndex + 1) % suggestions.length;
        scrollToSelected();
        break;
      case 'ArrowUp':
        e.preventDefault();
        selectedSuggestionIndex = (selectedSuggestionIndex - 1 + suggestions.length) % suggestions.length;
        scrollToSelected();
        break;
      case 'Enter':
        if (selectedSuggestionIndex >= 0) {
          e.preventDefault();
          selectItem(suggestions[selectedSuggestionIndex]);
        }
        break;
      case 'Escape':
        e.preventDefault();
        resetSuggestions();
        break;
    }
  }

  function scrollToSelected() {
    setTimeout(() => {
      const el = document.getElementById(`suggestion-${selectedSuggestionIndex}`);
      if (el) el.scrollIntoView({ block: 'nearest' });
    }, 0);
  }

  function handleShowAddForm() {
    showAddForm = true;
    addItemError = '';
    firstname = '';
    surname = '';
    contact = '';
    caution = '';
  }

  function handleCancelAdd() {
    showAddForm = false;
    addItemError = '';
    firstname = '';
    surname = '';
    contact = '';
    caution = '';
  }

  async function handleAddNewItem() {
    // Validation simple
    if (!firstname.trim() || !surname.trim()) {
      addItemError = "Le prénom et nom sont obligatoires.";
      return;
    }
    if (!/^[A-Z][a-zA-Z]*$/.test(firstname.trim()) || !/^[A-Z][a-zA-Z]*$/.test(surname.trim())) {
      addItemError = "Le prénom et le nom doivent commencer par une majuscule et ne contenir que des lettres.";
      return;
    }
    if (caution && (isNaN(Number(caution)) || Number(caution) < 0)) {
      addItemError = "La caution doit être un nombre positif.";
      return;
    }

    if (!addObject) {
      // Si pas de fonction d'ajout, création d'un objet local seulement
      const newMember = {
        id: Math.random().toString(36).slice(2),
        firstname: firstname.trim(),
        surname: surname.trim(),
        contact: contact.trim(),
        caution: caution ? Number(caution) : 0
      };
      selectItem(newMember);
      showAddForm = false;
      return;
    }

    try {
      addItemError = '';
      const newMember = await addObject({
        field1: firstname.trim(),
        field2: surname.trim(),
        contact: contact.trim(),
        caution: caution ? Number(caution) : 0
      });
      
      // Add to selection and reset form
      selectItem(newMember);
      firstname = '';
      surname = '';
      contact = '';
      caution = '';
      showAddForm = false;
    } catch (err) {
      addItemError = "Erreur lors de l'ajout : " + (err.message || err);
    }
  }
</script>

<div class="member-selector">
  <div class="input-row">
    {#if !showAddForm && !selectedMember}
      <div class="autocomplete-container">
        <input
          type="text"
          placeholder={placeholder}
          bind:value={searchInput}
          onkeydown={handleKeydown}
          onblur={() => setTimeout(() => { showSuggestions = false; }, 120)}
          aria-autocomplete="list"
          aria-controls="suggestions-list"
          aria-activedescendant={showSuggestions && selectedSuggestionIndex >= 0 ? 
            `suggestion-${selectedSuggestionIndex}` : undefined}
        />
        <button
          type="button"
          class="add-btn"
          title="Ajouter un nouveau membre"
          onclick={handleShowAddForm}
        >+</button>
        {#if showSuggestions}
          <ul class="suggestions-list" id="suggestions-list" role="listbox">
            {#each suggestions as item, i}
              <li
                id={`suggestion-${i}`}
                class:selected={i === selectedSuggestionIndex}
                role="option"
                aria-selected={i === selectedSuggestionIndex}
              >
                <button
                  type="button"
                  tabindex="-1"
                  class:selected={i === selectedSuggestionIndex}
                  onmousedown = {(e) => {e.preventDefault(); selectItem(item)}}
                >
                  {displayMember(item)}
                </button>
              </li>
            {/each}
          </ul>
        {/if}
      </div>
    {:else if showAddForm}
      <form class="add-form" onsubmit = {(e) => {e.preventDefault(); handleAddNewItem()}}>
        <input type="text" placeholder="Prénom" bind:value={firstname} class="field-input" />
        <input type="text" placeholder="Nom" bind:value={surname} class="field-input" />
        <input type="text" placeholder="Contact" bind:value={contact} class="field-input" />
        <input type="number" placeholder="Caution" bind:value={caution} min="0" class="field-input" />
        <div class="form-actions">
          <button type="submit" class="validate-btn">Valider</button>
          <button type="button" class="cancel-btn" onclick={handleCancelAdd}>Annuler</button>
        </div>
      </form>
    {/if}
  </div>
  
  {#if addItemError}
    <div class="error-message">{addItemError}</div>
  {/if}

  {#if selectedMember}
    <div class="selected-member">
      {displayMember(selectedMember)}
      <button type="button" class="remove-button" onclick={() => selectedMember = null} aria-label="Retirer">
        <img src="/icons/cancel.svg" alt="Supprimer" class="remove-icon" />
      </button>
    </div>
  {/if}
</div>

<style lang="scss">
.member-selector {
  width: 100%;
  max-width: 420px;
  .input-row {
    display: flex;
    align-items: center;
    gap: 0.7rem;
    margin-bottom: 0.5rem;
    .autocomplete-container {
      flex: 1 1 0%;
      display: flex;
      align-items: center;
      position: relative;
      width: 100%;
      input[type="text"] {
        flex: 1 1 0%;
        width: 100%;
        min-width: 0;
        padding: 0.45rem 0.8rem;
        border-radius: 4px 0 0 4px;
        border: 1px solid var(--secondary);
        background: var(--tertiary);
        color: var(--primary);
        font-size: 1rem;
        &:focus {
          outline: none;
          box-shadow: none;
          border-color: var(--secondary);
        }
      }
      .add-btn {
        border-radius: 0 4px 4px 0;
        border: 1px solid var(--secondary);
        border-left: none;
        background: var(--accent);
        color: #fff;
        font-size: 1.3rem;
        font-weight: bold;
        padding: calc(0.42rem - 2px) 0.8rem;
        cursor: pointer;
        transition: filter 0.15s;
        &:hover {
          filter: brightness(0.9);
        }
      }
      .suggestions-list {
        list-style: none;
        margin: 0;
        padding: 0;
        background: var(--tertiary);
        border: 1px solid var(--secondary);
        border-radius: 4px;
        max-height: 120px;
        overflow-y: auto;
        position: absolute;
        top: 100%;
        left: 0;
        width: 100%;
        z-index: 10;
        box-shadow: 0 4px 16px rgba(0,0,0,0.07);

        li {
          margin: 0;
          &.selected, button.selected {
            background: var(--secondary);
            color: var(--accent);
          }
          
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
              color: var(--accent);
            }
          }
        }
      }
    }
    .add-form {
      display: flex;
      flex-direction: column;
      gap: 0.7rem;
      width: 100%;
      .field-input {
        padding: 0.4rem 0.7rem;
        border-radius: 4px;
        border: 1px solid var(--secondary);
        background: var(--tertiary);
        color: var(--primary);
        font-size: 0.97rem;
        width: 100%;
      }
      .form-actions {
        display: flex;
        justify-content: flex-end;
        gap: 0.7rem;
        margin-top: 0.2rem;
        .validate-btn {
          background: var(--accent);
          color: white;
          border: none;
          border-radius: 3px;
          padding: 0.4rem 1.1rem;
          font-size: 1rem;
          cursor: pointer;
          font-weight: bold;
          &:hover {
            filter: brightness(0.9);
          }
        }
        .cancel-btn {
          background: none;
          border: 1px solid var(--secondary);
          color: var(--accent);
          border-radius: 3px;
          font-size: 1rem;
          padding: 0.4rem 1.1rem;
          cursor: pointer;
          &:hover {
            background: var(--secondary);
            color: #b71c1c;
          }
        }
      }
    }
  }
  .error-message {
    color: var(--accent);
    font-size: 0.97rem;
    margin-bottom: 0.3rem;
  }
  .selected-member {
    margin-top: 0.5rem;
    color: var(--white);
    border-radius: 90px;
    padding: 0 0;
    display: flex;
    align-items: center;
    font-size: 0.97rem;
    gap: 1rem;
    .remove-button {
      background: none;
      border: none;
      cursor: pointer;
      display: flex;
      align-items: center;
      padding-left: 0.6rem;
      padding-right: 0;
      &:hover {
        .remove-icon {
          filter: invert(18%) sepia(99%) saturate(2000%) hue-rotate(357deg) brightness(92%) contrast(119%);
        }
      }
      .remove-icon {
        width: 1.5em;
        height: 1.5em;
        display: block;
        pointer-events: none;
        filter: invert(18%) sepia(99%) saturate(7486%) hue-rotate(357deg) brightness(92%) contrast(119%);
      }
    }
  }
}
</style>
