<script>
  import { goto } from '$app/navigation';
  import Admin from './admin.svelte';
  import { adminPageState } from './store.js';
  import { isAuthenticated, logout } from '$lib/auth';
  

  

</script>


  <main>
    
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


<style>
  main {
    padding: 2rem;
    max-width: 1200px;
    margin: 0 auto;
  }
  
  .admin-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2rem;
    padding-bottom: 1rem;
    border-bottom: 2px solid var(--dark-red);
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
  }
  
  .logout-button:hover {
    background-color: var(--red);
  }
  
  .return-button {
    background-color: var(--orange);
    color: white;
    border: none;
    padding: 0.5rem 1rem;
    border-radius: 4px;
    cursor: pointer;
    font-weight: bold;
  }
  
  .return-button:hover {
    background-color: var(--dark-orange);
  }
</style>