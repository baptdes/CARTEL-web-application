<script>
  import { createEventDispatcher } from 'svelte';

  let {
    selectedPeople = $bindable([]),
    placeholder = "Rechercher une personne...",
    addObject = null, 
    searchObjects = null
  } = $props();

  const dispatch = createEventDispatcher();

  // Search state
  let searchInput = $state('');
  let suggestions = $state([]);
  let showSuggestions = $state(false);
  let selectedSuggestionIndex = $state(-1);
  let showNoResultsPrompt = $state(false);

  // Add new item state
  let firstname = $state('');
  let surname = $state('');
  let addItemError = $state('');
  let showAddForm = $state(false);

  // Computed display function for items
  const displayItem = (item) => {
    if (item.firstname && item.surname) {
      return `${item.firstname} ${item.surname}`;
    } else {
      return "Personne bizarre qui veut pas se montrer";
    }
  };

  // Handle search and suggestions
  $effect(() => {
    if (!searchInput.trim() || !searchObjects) {
      resetSuggestions();
      return;
    }

    const input = searchInput.trim().toLowerCase();
    Promise.resolve(searchObjects(input)).then(results => {
      suggestions = results;
      showSuggestions = results.length > 0;
      showNoResultsPrompt = results.length === 0 && input.length > 0;
      selectedSuggestionIndex = results.length > 0 ? 0 : -1;
    });
  });

  function resetSuggestions() {
    suggestions = [];
    showSuggestions = false;
    showNoResultsPrompt = false;
    selectedSuggestionIndex = -1;
  }

  function selectItem(item) {
    if (!selectedPeople.some(o => o.id === item.id)) {
      selectedPeople = [...selectedPeople, item];
    }
    searchInput = '';
    resetSuggestions();
  }

  function removeItem(itemId) {
    selectedPeople = selectedPeople.filter(o => o.id !== itemId);
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
        showSuggestions = false;
        break;
    }
  }

  function scrollToSelected() {
    setTimeout(() => {
      const el = document.getElementById(`suggestion-${selectedSuggestionIndex}`);
      if (el) el.scrollIntoView({ block: 'nearest' });
    }, 0);
  }

  async function handleAddNewItem() {
    // Validation
    if (!firstname.trim() || !surname.trim()) {
      addItemError = "Veuillez remplir les deux champs.";
      return;
    }
    if (!/^[A-Z][a-zA-Z]*$/.test(firstname.trim()) || !/^[A-Z][a-zA-Z]*$/.test(surname.trim())) {
      addItemError = "Le prénom et le nom doivent commencer par une majuscule et ne contenir que des lettres.";
      return;
    }
    if (!addObject) {
      addItemError = "Fonction d'ajout non disponible.";
      return;
    }

    try {
      addItemError = '';
      const newItem = await addObject({ 
        field1: firstname.trim(), 
        field2: surname.trim() 
      });
      
      // Add to selection and reset form
      selectItem(newItem);
      firstname = '';
      surname = '';
      showAddForm = false;
    } catch (err) {
      addItemError = "Erreur lors de l'ajout : " + (err.message || err);
    }
  }

  function handleShowAddForm() {
    showAddForm = true;
    addItemError = '';
    firstname = '';
    surname = '';
  }

  function handleCancelAdd() {
    showAddForm = false;
    addItemError = '';
    firstname = '';
    surname = '';
  }
</script>

<div class="people-selector">
  <div class="input-row">
    {#if !showAddForm}
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
          title="Ajouter une nouvelle personne"
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
                  {displayItem(item)}
                </button>
              </li>
            {/each}
          </ul>
        {/if}
      </div>
    {:else}
      <div class="add-form">
        <input
          type="text"
          placeholder="Prénom"
          bind:value={firstname}
          class="field-input"
        />
        <input
          type="text"
          placeholder="Nom"
          bind:value={surname}
          class="field-input"
        />
        <button
          type="button"
          class="validate-btn"
          title="Valider"
          onclick={handleAddNewItem}
        >Valider</button>
        <button
          type="button"
          class="cancel-btn"
          title="Annuler"
          onclick={handleCancelAdd}
        >✕</button>
      </div>
    {/if}
  </div>

  {#if showAddForm}
    <div class="maj-warning">
      Attention : Le prénom et le nom doivent commencer par une majuscule et ne contenir que des lettres.
    </div>
  {/if}

  {#if addItemError}
    <div class="error-message">{addItemError}</div>
  {/if}
  
  {#if showNoResultsPrompt && !showAddForm}
    <div class="no-results-message">
      Aucun résultat pour "{searchInput}".
    </div>
  {/if}

  {#if selectedPeople.length > 0}
    <div class="selected-items">
      {#each selectedPeople as item (item.id)}
        <span class="item-chip">
          {displayItem(item)}
          <button 
            type="button" 
            class="remove-button" 
            onclick={() => removeItem(item.id)}
            aria-label="Supprimer"
          >
            <img src="/icons/cancel.svg" alt="Supprimer" class="remove-icon" />
          </button>
        </span>
      {/each}
    </div>
  {/if}
</div>

<style lang="scss">
  .people-selector {
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
              }
            }
          }
        }
      }
      .add-form {
        display: flex;
        align-items: center;
        gap: 0.4rem;
        .field-input {
          padding: 0.4rem 0.7rem;
          border-radius: 4px;
          border: 1px solid var(--secondary);
          background: var(--tertiary);
          color: var(--primary);
          font-size: 0.97rem;
          width: 7.2rem;
        }
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
          border: none;
          color: var(--accent);
          font-size: 1.2rem;
          margin-left: 0.2rem;
          cursor: pointer;
          &:hover {
            color: #b71c1c;
          }
        }
      }
    }
    .maj-warning {
      color: var(--white);
      font-size: 0.97rem;
      margin-bottom: 0.2rem;
    }
    .error-message {
      color: var(--accent);
      font-size: 0.97rem;
      margin-bottom: 0.3rem;
    }
    
    .no-results-message {
      margin-top: 0.4rem;
      color: var(--primary);
      font-size: 0.95rem;
    }
    
    .selected-items {
      margin-top: 0.5rem;
      display: flex;
      flex-wrap: wrap;
      gap: 0.5rem;
      .item-chip {
        background: var(--secondary);
        color: var(--primary);
        border-radius: 90px;
        padding: 0 1rem;
        display: flex;
        align-items: center;
        font-size: 0.97rem;
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
              filter: brightness(0.7);
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
  }
</style>