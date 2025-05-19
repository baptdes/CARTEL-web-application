<script>
  import { onMount } from 'svelte';
  import { createSuggestion, fetchAllSuggestions } from '$lib/services/suggestionService.js';

  let showBookForm = $state(false);
  let showOtherForm = $state(false);

  let selectedType = $state('Livre');
  let suggestionName = $state('');
  let suggestionReason = $state('');
  let otherSuggestion = $state('');
  let isSubmitting = false;
  let submitMessage = '';

  let suggestions = $state([]);
  let error = '';

  const types = ['BD', 'Livre', 'Manga', 'JDS', 'JDR'];

  onMount(async () => {
    await loadSuggestions();
  });

  async function loadSuggestions() {
    try {
      suggestions = await fetchAllSuggestions();
    } catch (e) {
      error = "Erreur lors du chargement des suggestions.";
    }
  }

  function handleBookClick() {
    showBookForm = true;
    showOtherForm = false;
    submitMessage = '';
  }

  function handleOtherClick() {
    showOtherForm = true;
    showBookForm = false;
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
      };
      await createSuggestion(suggestion);
      submitMessage = 'Suggestion envoyée avec succès !';
      suggestionName = '';
      suggestionReason = '';
      showBookForm = false;
      await loadSuggestions();
      setTimeout(() => {submitMessage = '';}, 3000);
    } catch (e) {
      submitMessage = "Erreur lors de l'envoi de la suggestion.";
    } finally {
      isSubmitting = false;
    }
  }

  async function submitOtherSuggestion() {
    // You can implement this if needed
    showOtherForm = false;
  }

  function formatDate(dateStr) {
    if (!dateStr) return '';
    return dateStr.slice(0, 10);
  }
</script>

<main>
  <h2>Suggestions</h2>
  <div class="suggestion-buttons">
    <button class="suggestion-btn" on:click={handleBookClick}>
      Suggestions de Livre/JDS/Autre
    </button>
  </div>

  {#if submitMessage}
    <div class="submit-message">{submitMessage}</div>
  {/if}

  {#if error}
    <div class="error">{error}</div>
  {/if}

  <!-- Suggestions Table -->
  <div class="suggestions-table-container">
    {#if suggestions.length > 0}
      <table class="suggestions-table">
        <thead>
          <tr>
            <th>Nom</th>
            <th>Type</th>
            <th>Description</th>
            <th>Date</th>
          </tr>
        </thead>
        <tbody>
          {#each suggestions as s}
            <tr>
              <td>{s.name}</td>
              <td>{s.type}</td>
              <td>{s.description}</td>
              <td>{formatDate(s.createdAt)}</td>
            </tr>
          {/each}
        </tbody>
      </table>
    {:else}
      <div style="text-align:center; margin:2rem;">Aucune suggestion pour l’instant.</div>
    {/if}
  </div>

  <!-- Modal Form Overlay -->
  {#if showBookForm}
    <div class="modal-overlay" on:click={() => showBookForm = false}>
      <form class="suggestion-form modal-form" on:submit|preventDefault={submitBookSuggestion} on:click|stopPropagation>
        <button type="button" class="close-btn" on:click={() => showBookForm = false}>&times;</button>
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
      </form>
    </div>
  {/if}

  {#if showOtherForm}
    <div class="modal-overlay" on:click={() => showOtherForm = false}>
      <form class="suggestion-form modal-form" on:submit|preventDefault={submitOtherSuggestion} on:click|stopPropagation>
        <button type="button" class="close-btn" on:click={() => showOtherForm = false}>&times;</button>
        <label>
          Quel est votre Réclamation/suggestion :
          <input type="text" bind:value={otherSuggestion} required />
        </label>
        <button type="submit" class="submit-btn">Envoyer</button>
      </form>
    </div>
  {/if}
</main>

<style>
  h2 {
    font-family: "Pirata One", cursive;
    font-size: 3rem;
    color: var(--orange);
    margin-top: 2rem;
    margin-bottom: 1.5rem;
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

  .suggestions-table-container {
    display: flex;
    justify-content: center;
    margin-bottom: 2rem;
  }

  .suggestions-table {
    border-collapse: collapse;
    width: 90%;
    max-width: 900px;
    background: #fff8f0;
    box-shadow: 0 2px 8px rgba(0,0,0,0.07);
    color: #000;
    margin: 0 auto;
  }
  .suggestions-table th, .suggestions-table td {
    border: 1px solid #ddd;
    padding: 0.7rem 1rem;
    text-align: left;
  }
  .suggestions-table th {
    background: var(--orange);
    color: #fff;
    font-weight: bold;
  }

  .submit-message {
    text-align: center;
    color: var(--orange);
    font-weight: bold;
    margin-bottom: 1rem;
  }

  .error {
    color: red;
    text-align: center;
    margin-bottom: 1rem;
  }

  /* Modal Overlay */
  .modal-overlay {
    position: fixed;
    top: 0; left: 0; right: 0; bottom: 0;
    background: rgba(0,0,0,0.4);
    z-index: 1000;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .modal-form {
    background: #fff8f0;
    border-radius: 8px;
    padding: 2rem 2.5rem 2rem 2.5rem;
    box-shadow: 0 4px 24px rgba(0,0,0,0.18);
    position: relative;
    min-width: 320px;
    max-width: 95vw;
  }
  .close-btn {
    position: absolute;
    top: 0;
    right: 0;
    background: none;
    border: none;
    font-size: 2rem;
    color: var(--orange);
    cursor: pointer;
    line-height: 1;
  }

  .suggestion-form {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
    max-width: 400px;
    margin: 0 auto 2rem auto;
    background: #fff8f0;
    border-radius: 8px;
    padding: 2rem;
    box-shadow: 0 4px 12px rgba(0,0,0,0.08);
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
  }

  .submit-btn:hover {
    background-color: var(--dark-orange);
  }
</style>