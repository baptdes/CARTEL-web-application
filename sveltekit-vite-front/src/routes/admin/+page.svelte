<script>
  import Admin from './admin.svelte';
  import { adminPageState } from './store.js';
</script>

<main>
  {#if $adminPageState === 0}
    <Admin/>

  {:else if $adminPageState === 1}
    <h1>Catalogue</h1>
    <button class="Thebouton" type="button" on:click={()=>$adminPageState=0}>Retour </button>
  {:else if $adminPageState === 2}
    <h1>Course</h1>
    <button class="Thebouton" type="button" on:click={()=>$adminPageState=0}>Retour </button>
  {:else if $adminPageState === 3}
    <h1>Tresorerie</h1>
    <button class="Thebouton" type="button" on:click={()=>$adminPageState=0}>Retour </button>
  {:else if $adminPageState === 4}
    <h1>Empruntpret</h1>
    <button class="Thebouton" type="button" on:click={()=>$adminPageState=0}>Retour </button>
  {:else}
    <h1>Erreur</h1>
    <button class="Thebouton" type="button" on:click={()=>$adminPageState=0}>Retour </button>
  {/if}
</main>

<style>
</style>