<script>
  import { goto } from '$app/navigation';
  import Admin from './admin.svelte';
  import { adminPageState } from './store.js';
  import { isAuthenticated, logout } from '$lib/auth';
  
  let authenticated = $state(false);
  
  $effect(() => {
    authenticated = $isAuthenticated;
    if (!authenticated) {
      goto('/login');
    }
  });
  
  function handleLogout() {
    logout();
    goto('/login');
  }
</script>

{#if authenticated}
  <main>
    <div class="admin-header">
      <h1>Panel d'administration</h1>
      <button class="logout-button" onclick={handleLogout}>Déconnexion</button>
    </div>
    
    {#if $adminPageState === 0}
      <Admin/>
    {:else if $adminPageState === 1}
      <h2>Catalogue</h2>
      <button class="return-button" type="button" onclick={()=>$adminPageState=0}>Retour</button>
    {:else if $adminPageState === 2}
      <h2>Course</h2>
      <button class="return-button" type="button" onclick={()=>$adminPageState=0}>Retour</button>
    {:else if $adminPageState === 3}
      <h2>Tresorerie</h2>
      <button class="return-button" type="button" onclick={()=>$adminPageState=0}>Retour</button>
    {:else if $adminPageState === 4}
      <h2>Empruntpret</h2>
      <button class="return-button" type="button" onclick={()=>$adminPageState=0}>Retour</button>
    {:else}
      <h2>Erreur</h2>
      <button class="return-button" type="button" onclick={()=>$adminPageState=0}>Retour</button>
    {/if}
  </main>
{/if}

<style lang="scss">
  $section-spacing: 2rem;
  $border-width: 2px;
  
  main {
    padding: $section-spacing;
    max-width: 1200px;
    margin: 0 auto;
  }
  
  .admin-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: $section-spacing;
    padding-bottom: 1rem;
    border-bottom: $border-width solid var(--dark-red);
  }
  
  h1 {
    font-family: "Pirata One", cursive;
    color: var(--red);
    margin: 0;
  }
  
  h2 {
    font-family: "Pirata One", cursive;
    color: var(--orange);
    margin-bottom: 1.5rem;
  }
  
  .logout-button {
    background-color: var(--dark-red);
    color: white;
    border: none;
    padding: 0.5rem 1rem;
    border-radius: 4px;
    cursor: pointer;
    font-weight: bold;
    transition: background-color 0.3s;
    
    &:hover {
      background-color: var(--red);
    }
  }
  
  .return-button {
    background-color: var(--orange);
    color: white;
    border: none;
    padding: 0.5rem 1rem;
    border-radius: 4px;
    cursor: pointer;
    font-weight: bold;
    
    &:hover {
      background-color: var(--dark-orange);
    }
  }
</style>