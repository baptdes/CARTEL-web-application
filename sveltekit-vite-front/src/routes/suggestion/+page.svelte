<script>
  import { onMount } from 'svelte';
  import { createSuggestion, fetchAllSuggestions } from '$lib/services/suggestionService.js';

  let suggestions = [];
  let error = '';
  let isLoading = false;

  // Popup state
  let showPopup = false;
  let isSubmitting = false;
  let submitMessage = '';

  // Form fields
  let selectedType = 'Livre';
  let suggestionName = '';
  let suggestionReason = '';

  const types = ['BD', 'Livre', 'Manga', 'JDS', 'JDR'];

  // Load suggestions on mount
  onMount(async () => {
    await loadSuggestions();
  });

  async function loadSuggestions() {
    isLoading = true;
    error = '';
    try {
      suggestions = await fetchAllSuggestions();
      // Sort by date desc
      suggestions.sort((a, b) => (b.createdAt || '').localeCompare(a.createdAt || ''));
    } catch (e) {
      error = "Erreur lors du chargement des suggestions.";
    } finally {
      isLoading = false;
    }
  }

  function openPopup() {
    showPopup = true;
    submitMessage = '';
    // Reset form
    selectedType = 'Livre';
    suggestionName = '';
    suggestionReason = '';
  }

  function closePopup() {
    showPopup = false;
    submitMessage = '';
  }

  async function submitBookSuggestion() {
    isSubmitting = true;
    submitMessage = '';
    try {
      // Map UI type to backend enum
      const typeMap = {
        'Livre': 'LIVRE',
        'BD': 'BD',
        'Manga': 'MANGA',
        'JDS': 'JDS',
        'JDR': 'JDR'
      };
      const suggestion = {
        name: suggestionName,
        type: typeMap[selectedType] || 'AUTRE',
        description: suggestionReason,
        isSuggestion: true
      };
      await createSuggestion(suggestion);
      submitMessage = 'Suggestion envoyée avec succès !';
      await loadSuggestions();
      setTimeout(() => {
        closePopup();
      }, 1200);
    } catch (e) {
      submitMessage = "Erreur lors de l'envoi de la suggestion.";
    } finally {
      isSubmitting = false;
    }
  }
</script>

<main>
  <h2>Suggestions</h2>
  <div class="suggestion-buttons">
    <button class="suggestion-btn" on:click={openPopup}>
      Ajouter une suggestion
    </button>
  </div>

  {#if error}
    <div class="error">{error}</div>
  {/if}

  {#if isLoading}
    <div class="loading">Chargement...</div>
  {:else}
    <div class="suggestion-table-container">
      <table class="suggestion-table">
        <thead>
          <tr>
            <th>Type</th>
            <th>Nom</th>
            <th>Description</th>
            <th>Date d'ajout</th>
          </tr>
        </thead>
        <tbody>
          {#each suggestions as s}
            <tr>
              <td>{s.type}</td>
              <td>{s.name}</td>
              <td>{s.description}</td>
              <td>{s.createdAt ? s.createdAt.slice(0, 10) : ''}</td>
            </tr>
          {/each}
        </tbody>
      </table>
      {#if suggestions.length === 0}
        <div class="no-results">Aucune suggestion pour le moment.</div>
      {/if}
    </div>
  {/if}

  {#if showPopup}
    <div class="popup-backdrop" on:click={closePopup}>
      <div class="popup-content" on:click|stopPropagation>
        <h3>Ajouter une suggestion</h3>
        <form class="suggestion-form" on:submit|preventDefault={submitBookSuggestion}>
          <label>
            Type :
            <select bind:value={selectedType} required>
              {#each types as type}
                <option value={type}>{type}</option>
              {/each}
            </select>
          </label>
          <label>
            Nom de la suggestion :
            <input type="text" bind:value={suggestionName} required />
          </label>
          <label>
            Pourquoi cette suggestion ?
            <input type="text" bind:value={suggestionReason} required />
          </label>
          <button type="submit" class="submit-btn" disabled={isSubmitting}>Envoyer</button>
          <button type="button" class="cancel-btn" on:click={closePopup}>Annuler</button>
        </form>
        {#if submitMessage}
          <div class="submit-message">{submitMessage}</div>
        {/if}
      </div>
    </div>
  {/if}
</main>

<style>
  h2 {
    font-family: "Pirata One", cursive;
    color: var(--orange);
    margin-bottom: 1.5rem;
    margin-top : 2rem;
    font-size: 4rem;
    text-align: center;
  }

  .suggestion-buttons {
    display: flex;
    gap: 2rem;
    justify-content: center;
    margin-bottom: 2rem;
    flex-wrap: wrap;
  }

  .suggestion-btn {
    background-color: var(--orange);
    color: white;
    border: none;
    padding: 1rem 2rem;
    border-radius: 4px;
    cursor: pointer;
    font-weight: bold;
    font-size: 1.1rem;
    transition: background-color 0.3s;
  }

  .suggestion-btn:hover {
    background-color: var(--dark-orange);
  }

  .suggestion-table-container {
    max-width: 900px;
    margin: 0 auto;
    overflow-x: auto;
  }

  .suggestion-table {
    width: 100%;
    border-collapse: collapse;
    background: #fff8f0;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.08);
    margin-bottom: 2rem;
  }

  .suggestion-table th, .suggestion-table td {
    padding: 1rem;
    border: 1px solid var(--orange);
    text-align: left;
    font-size: 1rem;
    color: #000;
  }

  .suggestion-table th {
    background: var(--orange);
    color: #fff;
    font-weight: bold;
  }

  .suggestion-table tr:nth-child(even) {
    background: #fff3e0;
  }

  .no-results {
    text-align: center;
    color: #888;
    margin: 2rem 0;
  }

  .error {
    color: var(--red);
    text-align: center;
    margin-bottom: 1rem;
  }

  .loading {
    text-align: center;
    color: var(--orange);
    margin: 2rem 0;
  }

  /* Popup styles */
  .popup-backdrop {
    position: fixed;
    top: 0; left: 0; right: 0; bottom: 0;
    background: rgba(0,0,0,0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
  }
  .popup-content {
    background: #fff8f0;
    border-radius: 8px;
    padding: 2rem 2.5rem;
    min-width: 320px;
    max-width: 95vw;
    box-shadow: 0 4px 24px rgba(0,0,0,0.15);
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: stretch;
  }
  .popup-content h3 {
    margin-top: 0;
    color: var(--orange);
    font-size: 2rem;
    text-align: center;
    margin-bottom: 1.5rem;
  }
  .suggestion-form {
    display: flex;
    flex-direction: column;
    gap: 1.2rem;
  }
  label {
    display: flex;
    flex-direction: column;
    font-weight: bold;
    color: var(--orange);
    font-size: 1rem;
  }
  select, input[type="text"] {
    margin-top: 0.5rem;
    padding: 0.7rem;
    border-radius: 4px;
    border: 1px solid var(--orange);
    font-size: 1rem;
  }
  .submit-btn {
    background-color: var(--orange);
    color: white;
    border: none;
    border-radius: 4px;
    padding: 0.8rem;
    font-size: 1rem;
    font-weight: bold;
    cursor: pointer;
    transition: background-color 0.3s;
    margin-bottom: 0.5rem;
  }
  .submit-btn:hover {
    background-color: var(--dark-orange);
  }
  .cancel-btn {
    background: #eee;
    color: var(--orange);
    border: 1px solid var(--orange);
    border-radius: 4px;
    padding: 0.7rem;
    font-size: 1rem;
    font-weight: bold;
    cursor: pointer;
    transition: background 0.2s;
  }
  .cancel-btn:hover {
    background: #ffe0b2;
  }
  .submit-message {
    text-align: center;
    color: var(--orange);
    margin-top: 1rem;
    font-weight: bold;
  }
</style>