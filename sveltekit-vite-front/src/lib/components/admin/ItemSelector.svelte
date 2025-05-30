<script>
  import { createEventDispatcher } from 'svelte';
  import * as itemService from '$lib/services/itemService';

  let {
    selectedItem = $bindable(null),
    placeholder = "Rechercher un objet...",
    searchObjects = null
  } = $props();

  const dispatch = createEventDispatcher();

  // Recherche
  let searchInput = $state('');
  let suggestions = $state([]);
  let showSuggestions = $state(false);
  let selectedSuggestionIndex = $state(-1);

  // Placeholder image when cover not available
  const placeholderImage = "/placeholder_book.png";

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
</script>

<div class="item-selector">
  <div class="input-row">
    {#if !selectedItem}
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
    {/if}
  </div>

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
        border-radius: 4px;
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
