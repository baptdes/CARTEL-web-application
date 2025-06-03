<script>
  import { goto } from "$app/navigation";
  import { adminPageState } from "../store.js";
  import { isAuthenticated, logout } from "$lib/auth";
  import DoubleText from "$lib/misc/DoubleText.svelte";
  import PointBar from "$lib/misc/PointBar.svelte";
  import ImageText from "$lib/misc/ImageText.svelte";
  import { page } from "$app/stores";

  $: isJdsActive = $page.url.pathname.includes("/jds");

  // Function to switch between catalogues
  function switchCatalogue() {
    if (isJdsActive) {
      goto("/admin/catalogue");
    } else {
      goto("/admin/catalogue/jds");
    }
  }
</script>

<main>
  <DoubleText text="Catalogue" size="4em" />
  <PointBar Color="var(--accent)" width="70%" />

  <div class="catalogue-toggle">
    <button
      class:active={!isJdsActive}
      on:click={() => !isJdsActive || switchCatalogue()}
    >
      <ImageText src="/src/assets/img/thres/book.jpg" text="Livres" size="3em" isAnimated=true />
    </button>
    <button
      class:active={isJdsActive}
      on:click={() => isJdsActive || switchCatalogue()}
    >
    <ImageText src="/src/assets/img/thres/jeux.jpg" size="3em" isAnimated=true />
    </button>
  </div>

  <div class="catalogue-container">
    <slot></slot>
  </div>
</main>

<style lang="scss">
  main {
    flex: auto;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .catalogue-container {
    width: 95%;
    margin-top: 2rem;
  }
</style>
