<script>
  import { createEventDispatcher } from 'svelte';
  import * as itemService from '$lib/services/itemService';

  let {
    selectedItem = $bindable(null),
    placeholder = "Rechercher un objet...",
    addObject = null,
    searchObjects = null
  } = $props();

  const dispatch = createEventDispatcher();

  // Recherche
  let searchInput = $state('');
  let suggestions = $state([]);
  let showSuggestions = $state(false);
  let selectedSuggestionIndex = $state(-1);

  // Ajout objet
  let name = $state('');
  let barcode = $state('');
  let publicationYear = $state('');
  let language = $state('FR');
  let addItemError = $state('');
  let showAddForm = $state(false);

  const languages = ['FR', 'EN', 'JA'];

  // Affichage item
  const displayItem = (item) => {
    return `${item.name} (${item.publicationYear})`;
  };

  // Placeholder image when cover not available
  const placeholderImage = "/images/placeholder-cover.png";

  // Recherche suggestions
  $effect(() => {
    if (!searchInput.trim() || !searchObjects) {
      resetSuggestions();
      return;
    }

    const input = searchInput.trim().toLowerCase();
    Promise.resolve(searchObjects(input)).then(results => {
      suggestions = results?.content || [];
      showSuggestions = suggestions.length > 0;
      selectedSuggestionIndex = suggestions.length > 0 ? 0 : -1;
    });
  });

  function resetSuggestions() {
    suggestions = [];
    showSuggestions = false;
    selectedSuggestionIndex = -1;
  }

  function selectItem(item) {
    selectedItem = item;
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
    name = '';
    barcode = '';
    publicationYear = new Date().getFullYear().toString();
    language = 'FR';
  }

  function handleCancelAdd() {
    showAddForm = false;
    addItemError = '';
  }

  async function handleAddNewItem() {
    // Validation simple
    if (!name.trim()) {
      addItemError = "Le nom est obligatoire.";
      return;
    }
    if (!barcode.trim()) {
      addItemError = "Le code-barres est obligatoire.";
      return;
    }
    if (!publicationYear || isNaN(Number(publicationYear)) || Number(publicationYear) < 0) {
      addItemError = "L'année de publication doit être un nombre positif.";
      return;
    }

    if (!addObject) {
      // Si pas de fonction d'ajout, création d'un objet local seulement
      const newItem = {
        barcode: barcode.trim(),
        name: name.trim(),
        publicationYear: Number(publicationYear),
        language: language
      };
      selectItem(newItem);
      showAddForm = false;
      return;
    }

    try {
      addItemError = '';
      const newItem = await addObject({
        barcode: barcode.trim(),
        name: name.trim(),
        publicationYear: Number(publicationYear),
        language: language
      });
      
      // Add to selection and reset form
      selectItem(newItem);
      showAddForm = false;
    } catch (err) {
      addItemError = "Erreur lors de l'ajout : " + (err.message || err);
    }
  }
</script>

<div class="item-selector">
  <div class="input-row">
    {#if !showAddForm && !selectedItem}
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
          title="Ajouter un nouvel objet"
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
                  <div class="item-suggestion">
                    <div class="item-image">
                      <img 
                        src={item.coverImage || placeholderImage} 
                        alt={item.name} 
                        onerror={(e) => e.target.src = placeholderImage} 
                      />
                    </div>
                    <div class="item-details">
                      <div class="item-name">{item.name}</div>
                      <div class="item-year">{item.publicationYear}</div>
                    </div>
                  </div>
                </button>
              </li>
            {/each}
          </ul>
        {/if}
      </div>
    {:else if showAddForm}
      <form class="add-form" onsubmit = {(e) => {e.preventDefault(); handleAddNewItem()}}>
        <input type="text" placeholder="Code-barres" bind:value={barcode} class="field-input" />
        <input type="text" placeholder="Nom" bind:value={name} class="field-input" />
        <input type="number" placeholder="Année de publication" bind:value={publicationYear} min="0" class="field-input" />
        <select bind:value={language} class="field-input">
          {#each languages as lang}
            <option value={lang}>{lang}</option>
          {/each}
        </select>
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

  {#if selectedItem}
    <div class="selected-item">
      <div class="item-display">
        <div class="item-image">
          <img 
            src={selectedItem.coverImage || placeholderImage} 
            alt={selectedItem.name} 
            onerror={(e) => e.target.src = placeholderImage} 
          />
        </div>
        <div class="item-details">
          <div class="item-name">{selectedItem.name}</div>
          <div class="item-year">{selectedItem.publicationYear}</div>
        </div>
      </div>
      <button type="button" class="remove-button" onclick={() => selectedItem = null} aria-label="Retirer">
        <img src="/icons/cancel.svg" alt="Supprimer" class="remove-icon" />
      </button>
    </div>
  {/if}
</div>

<style lang="scss">
.item-selector {
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
        max-height: 240px;
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
            .item-name {
              color: var(--accent);
            }
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
              .item-name {
                color: var(--accent);
              }
            }
            
            .item-suggestion {
              display: flex;
              align-items: center;
              gap: 0.8rem;
              
              .item-image {
                width: 40px;
                height: 60px;
                min-width: 40px;
                img {
                  width: 100%;
                  height: 100%;
                  object-fit: cover;
                  border-radius: 2px;
                }
              }
              
              .item-details {
                .item-name {
                  font-weight: 500;
                  margin-bottom: 0.2rem;
                }
                .item-year {
                  font-size: 0.9em;
                  opacity: 0.8;
                }
              }
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
  .selected-item {
    margin-top: 0.5rem;
    padding: 0.5rem;
    border-radius: 4px;
    border: 1px solid var(--secondary);
    background: var(--tertiary);
    display: flex;
    align-items: center;
    justify-content: space-between;
    
    .item-display {
      display: flex;
      align-items: center;
      gap: 0.8rem;
      
      .item-image {
        width: 40px;
        height: 60px;
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          border-radius: 2px;
        }
      }
      
      .item-details {
        .item-name {
          font-weight: 500;
          margin-bottom: 0.2rem;
        }
        .item-year {
          font-size: 0.9em;
          opacity: 0.8;
        }
      }
    }
    
    .remove-button {
      background: none;
      border: none;
      cursor: pointer;
      display: flex;
      align-items: center;
      padding: 0.6rem;
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
