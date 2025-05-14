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
  

  
  h2 {
    font-family: "Pirata One", cursive;
    color: var(--orange);
    margin-bottom: 1.5rem;
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