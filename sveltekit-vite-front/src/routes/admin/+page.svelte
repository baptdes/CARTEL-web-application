<script>
  import Admin from './admin.svelte';
  import {nb2} from './page.js';
</script>


<main>
  {#if $nb2 === 0}
    <Admin/>

  {:else if $nb2 === 1}
    <h1>Catalogue</h1>
    <button class="Thebouton" type="button" on:click={()=>$nb2=0}>Retour </button>
  {:else if $nb2 === 2}
    <h1>Course</h1>
    <button class="Thebouton" type="button" on:click={()=>$nb2=0}>Retour </button>
  {:else if $nb2 === 3}
    <h1>Tresorerie</h1>
    <button class="Thebouton" type="button" on:click={()=>$nb2=0}>Retour </button>
  {:else if $nb2 === 4}
    <h1>Empruntpret</h1>
    <button class="Thebouton" type="button" on:click={()=>$nb2=0}>Retour </button>
  {:else}
    <h1>Erreur</h1>
    <button class="Thebouton" type="button" on:click={()=>$nb2=0}>Retour </button>
  {/if}
</main>

<style>
</style>