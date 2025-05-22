<script>
  import { createEventDispatcher } from 'svelte';

  let { 
    label = "Objet(s)", 
    selectedObject = $bindable([]), 
    addObject = null, 
    searchObjects = null,
    field1Label = "Prénom",
    field2Label = "Nom",
    itemDisplayFn = null
  } = $props();

  const dispatch = createEventDispatcher();

  // Search state
  let searchInput = $state('');
  let suggestions = $state([]);
  let showSuggestions = $state(false);
  let selectedSuggestionIndex = $state(-1);
  let showNoResultsPrompt = $state(false);

  // Add new item state
  let field1Value = $state('');
  let field2Value = $state('');
  let addItemError = $state('');

  // Computed display function for items
  const displayItem = (item) => {
    if (itemDisplayFn) return itemDisplayFn(item);
    
    if (item.firstname && item.surname) {
      return `${item.firstname} ${item.surname}`;
    } else if (item.name) {
      return item.name;
    } else {
      return JSON.stringify(item);
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
    if (!selectedObject.some(o => o.id === item.id)) {
      selectedObject = [...selectedObject, item];
    }
    searchInput = '';
    resetSuggestions();
  }

  function removeItem(itemId) {
    selectedObject = selectedObject.filter(o => o.id !== itemId);
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
    if (!field1Value.trim() || !field2Value.trim()) {
      addItemError = "Veuillez remplir les deux champs.";
      return;
    }
    if (!addObject) {
      addItemError = "Fonction d'ajout non disponible.";
      return;
    }

    try {
      addItemError = '';
      const newItem = await addObject({ 
        field1: field1Value.trim(), 
        field2: field2Value.trim() 
      });
      
      // Add to selection and reset form
      selectItem(newItem);
      field1Value = '';
      field2Value = '';
    } catch (err) {
      addItemError = "Erreur lors de l'ajout : " + (err.message || err);
    }
  }
</script>

<div class="selection-field">
  <h4>{label}</h4>
  
  <div class="input-row">
    <!-- Search section -->
    <div class="search-container">
      <input
        type="text"
        placeholder="Rechercher"
        bind:value={searchInput}
        onkeydown={handleKeydown}
        onblur={() => setTimeout(() => { showSuggestions = false; }, 120)}
        aria-autocomplete="list"
        aria-controls="suggestions-list"
        aria-activedescendant={showSuggestions && selectedSuggestionIndex >= 0 ? 
          `suggestion-${selectedSuggestionIndex}` : undefined}
      />
      
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

    <!-- Add new item section -->
    <div class="add-item-container">
      <input
        type="text"
        placeholder={field1Label}
        bind:value={field1Value}
        class="field-input"
      />
      <input
        type="text"
        placeholder={field2Label}
        bind:value={field2Value}
        class="field-input"
      />
      <button
        type="button"
        class="add-button"
        title="Ajouter"
        onclick={handleAddNewItem}
      >+</button>
    </div>
  </div>

  <!-- Error messages -->
  {#if addItemError}
    <div class="error-message">{addItemError}</div>
  {/if}
  
  {#if showNoResultsPrompt}
    <div class="no-results-message">
      Aucun résultat pour "{searchInput}".
    </div>
  {/if}

  <!-- Selected items display -->
  {#if selectedObject.length > 0}
    <div class="selected-items">
      {#each selectedObject as item (item.id)}
        <span class="item-chip">
          {displayItem(item)}
          <button 
            type="button" 
            class="remove-button" 
            onclick={() => removeItem(item.id)}
            aria-label="Supprimer"
          >&times;</button>
        </span>
      {/each}
    </div>
  {/if}
</div>

<style lang="scss">
  .selection-field {
    .input-row {
      display: flex;
      gap: 1.2rem;
      align-items: flex-start;
      margin-bottom: 0.5rem;
      
      .search-container {
        flex: 2;
        position: relative;
      }
      
      .add-item-container {
        flex: 1.2;
        display: flex;
        gap: 0.3rem;
        align-items: flex-start;
        
        .field-input {
          padding: 0.4rem 0.7rem;
          border-radius: 4px;
          border: 1px solid var(--secondary);
          background: var(--tertiary);
          color: var(--primary);
          font-size: 0.97rem;
          width: 6.5rem;
        }
        
        .add-button {
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
    
    .suggestions-list {
      list-style: none;
      margin: 0.3rem 0 0 0;
      padding: 0;
      background: var(--tertiary);
      border: 1px solid var(--secondary);
      border-radius: 4px;
      max-height: 120px;
      overflow-y: auto;
      position: absolute;
      width: 100%;
      z-index: 10;
      
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
        border-radius: 12px;
        padding: 0.2rem 0.8rem;
        display: flex;
        align-items: center;
        font-size: 0.97rem;
        
        .remove-button {
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
  }
</style>