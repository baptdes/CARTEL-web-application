<script>
  import svelteLogo from './assets/svelte.svg';
  import viteLogo from '/vite.svg';
  import Counter from './lib/Counter.svelte';
  import { onMount } from 'svelte';
  import { invokeGet } from './api';

  let users = [];
  let errorMessage = '';

  onMount(async () => {
    try {
      users = await invokeGet('users') || [];
    } catch (error) {
      errorMessage = 'Error fetching users: ' + error.message;
      console.error(errorMessage);
    }
  });
</script>

<main>
  <div>
    <a href="https://vite.dev" target="_blank" rel="noreferrer">
      <img src={viteLogo} class="logo" alt="Vite Logo" />
    </a>
    <a href="https://svelte.dev" target="_blank" rel="noreferrer">
      <img src={svelteLogo} class="logo svelte" alt="Svelte Logo" />
    </a>
  </div>
  <h1>C.A.R.T.E.L</h1>
  <h2>Catalogue Annuellement Ressucité et Téléconsultable d'Elements Ludiques</h2>

  <div class="card">
    <Counter />
  </div>

  <h2>Users List</h2>
  {#if errorMessage}
    <p class="error">{errorMessage}</p>
  {:else}
    <ul>
      {#each users as user}
        <li>{user.name} - {user.email}</li>
      {/each}
    </ul>
  {/if}

  <p class="read-the-docs">
    Click on the Vite and Svelte logos to learn more
  </p>
</main>

<style>
  .logo {
    height: 6em;
    padding: 1.5em;
    will-change: filter;
    transition: filter 300ms;
  }
  .logo:hover {
    filter: drop-shadow(0 0 2em #646cffaa);
  }
  .logo.svelte:hover {
    filter: drop-shadow(0 0 2em #ff3e00aa);
  }
  .read-the-docs {
    color: #888;
  }
  ul {
    list-style-type: none;
    padding: 0;
  }
  li {
    margin: 0.5em 0;
  }
  .error {
    color: red;
    font-weight: bold;
  }
</style>
