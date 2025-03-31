<script>
  import svelteLogo from './assets/svelte.svg';
  import viteLogo from '/vite.svg';
  import Counter from './lib/Counter.svelte';
  import Navbar from './lib/Navbar.svelte';
  import Title from './lib/home/Title.svelte';
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

<header>
  <Navbar />
</header>

<main>
  
  <Title />

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
