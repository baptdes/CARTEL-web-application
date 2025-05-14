<script>
  import { goto } from "$app/navigation";
  import { adminPageState } from "../store.js";
  import { isAuthenticated, logout } from "$lib/auth";
  import DoubleText from "$lib/misc/DoubleText.svelte";
  import StackText from "$lib/misc/StackText.svelte";
  import PointBar from "$lib/misc/PointBar.svelte";
</script>

<main>
  <DoubleText text="Purge" size="4em" />
  <!-- <button class="return-button" type="button" onclick={() => { $adminPageState = 0; goto('/admin'); }}>Retour</button> -->

  <PointBar Color="var(--accent)" width="70%" />

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
</style>
