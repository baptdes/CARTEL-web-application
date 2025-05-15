<script>
  import { createSuggestion } from '$lib/services/suggestionService.js';

  let showBookForm = $state(false);
  let showOtherForm = $state(false);

  let selectedType = $state('Livre');
  let suggestionName = $state('');
  let suggestionReason = $state('');
  let otherSuggestion = $state('');
  let isSubmitting = false;
  let submitMessage = '';

  const types = ['BD', 'Livre', 'Manga', 'JDS', 'JDR'];

  function handleBookClick() {
    showBookForm = !showBookForm;
    showOtherForm = false;
    submitMessage = '';
  }

  function handleOtherClick() {
    showOtherForm = !showOtherForm;
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
        description: suggestionReason
      };
      await createSuggestion(suggestion);
      submitMessage = 'Suggestion envoyée avec succès !';
      suggestionName = '';
      suggestionReason = '';
      showBookForm = false;
    } catch (e) {
      submitMessage = "Erreur lors de l'envoi de la suggestion.";
    } finally {
      isSubmitting = false;
    }
  }

  function submitOtherSuggestion() {
    // TODO: handle other suggestion submission
    alert(`Réclamation/Suggestion envoyée : ${otherSuggestion}`);
    otherSuggestion = '';
    showOtherForm = false;
  }
</script>

<main>
  <h2>Suggestions</h2>
  <div class="suggestion-buttons">
    <button class="suggestion-btn" on:click={handleBookClick}>
      Suggestions de Livre/JDS/Autre
    </button>
    <button class="suggestion-btn" on:click={handleOtherClick}>
      Suggestions et Réclamations autre
    </button>
  </div>

  {#if submitMessage}
    <div class="submit-message">{submitMessage}</div>
  {/if}

  {#if showBookForm}
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
    </form>
  {/if}

  {#if showOtherForm}
    <form class="suggestion-form" on:submit|preventDefault={submitOtherSuggestion}>
      <label>
        Quel est votre Réclamation/suggestion :
        <input type="text" bind:value={otherSuggestion} required />
      </label>
      <button type="submit" class="submit-btn">Envoyer</button>
    </form>
  {/if}
</main>

<style>
  h2 {
    font-family: "Pirata One", cursive;
    color: var(--orange);
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