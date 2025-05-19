<script>
  import { goto } from "$app/navigation";
  import { adminPageState } from "../store.js";
  import { isAuthenticated, logout } from "$lib/auth";
  import DoubleText from "$lib/misc/DoubleText.svelte";
  import StackText from "$lib/misc/StackText.svelte";
  import PointBar from "$lib/misc/PointBar.svelte";
  import { onMount } from "svelte";
  import { fetchAllSuggestions, deleteSuggestion } from "$lib/services/suggestionService.js";

  let suggestions = [];
  let error = "";

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

  async function handleDelete(id) {
    if (confirm("Êtes-vous sûr de vouloir supprimer cette suggestion ?")) {
      try {
        await deleteSuggestion(id);
        await loadSuggestions();
      } catch (e) {
        error = "Erreur lors de la suppression de la suggestion.";
      }
    }
  }
</script>

<main>
  <DoubleText text="Purge" size="4em" />
  <!-- <button class="return-button" type="button" onclick={() => { $adminPageState = 0; goto('/admin'); }}>Retour</button> -->

  <PointBar Color="var(--accent)" width="70%" />

  {#if error}
    <div class="error">{error}</div>
  {/if}

  {#if suggestions.length > 0}
    <table>
      <thead>
        <tr>
          <th>Nom</th>
          <th>Type</th>
          <th>Description</th>
          <th>Date</th>
          <th>Action</th>
        </tr>
      </thead>
      <tbody>
        {#each suggestions as s}
          <tr>
            <td>{s.name}</td>
            <td>{s.type}</td>
            <td>{s.description}</td>
            <td>{s.createdAt?.slice(0, 10)}</td>
            <td>
              <button on:click={() => handleDelete(s.id)}>
                Supprimer
              </button>
            </td>
          </tr>
        {/each}
      </tbody>
    </table>
  {:else}
    <p>Aucune suggestion trouvée.</p>
  {/if}

  <StackText/>
</main>

<style lang="scss">
  @use "/src/lib/sass/base" as base;

  main {
    flex: auto;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  table {
    margin-top: 2rem;
    border-collapse: collapse;
    width: 90%;
    max-width: 900px;
    background: #fff;
    box-shadow: 0 2px 8px rgba(0,0,0,0.07);
    color: #000;
  }
  th, td {
    border: 1px solid #ddd;
    padding: 0.7rem 1rem;
    text-align: left;
    color: #000;
  }
  th {
    background: var(--accent);
    color: #fff;
  }
  tr:nth-child(even) {
    background: #f9f9f9;
  }
  .error {
    color: red;
    margin: 1rem 0;
  }
  button {
    background: #d32f2f;
    color: #fff;
    border: none;
    padding: 0.4em 1em;
    border-radius: 4px;
    cursor: pointer;
    transition: background 0.2s;
  }
  button:hover {
    background: #b71c1c;
  }
</style>