<script>
  import { goto } from "$app/navigation";
  import { adminPageState } from "../store.js";
  import { isAuthenticated, logout } from "$lib/auth";
  import DoubleText from "$lib/misc/DoubleText.svelte";
  import PointBar from "$lib/misc/PointBar.svelte";
  import ImageText from "$lib/misc/ImageText.svelte";
  import { page } from "$app/stores";
  import Starline from "$lib/misc/Starline.svelte";

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
  <div style="margin-bottom: 1rem;"></div>

  <div class="catalogue-toggle">
    <button
      class:active={!isJdsActive}
      on:click={() => !isJdsActive || switchCatalogue()}
    >
      <ImageText
        src="/src/assets/img/thres/book.jpg"
        text="Livres"
        size="4"
        isAnimated="true"
      />
    </button>
    <button
      class:active={isJdsActive}
      on:click={() => isJdsActive || switchCatalogue()}
    >
      <ImageText
        src="/src/assets/img/thres/cards.jpg"
        text="Jeux"
        size="4"
        isAnimated="true"
      />
    </button>
  </div>

  <div class="catalogue-container">
    <slot></slot>
  </div>

  <div style="margin-bottom: 4rem;"></div>

  <PointBar Color="var(--accent)" width="90%" />

  <div style="margin-bottom: 3rem;"></div>

  <Starline
    content={[
      { type: "icon", content: "/src/assets/img/icons/arc_ring.svg" },
      { type: "line", content: "5%" },
      { type: "text*", content: "bread" },
      { type: "line", content: "30%" },
      { type: "text", content: "and" },
      { type: "line", content: "25%" },
      { type: "icon*", content: "/src/assets/img/icons/star_hexa.svg" },
    ]}
  />

  <Starline
    content={[
      { type: "icon", content: "/src/assets/img/icons/earth_flat.svg" },
      { type: "line", content: "30%" },
      { type: "text*", content: "circuses" },
      { type: "line", content: "45%" },
      { type: "text", content: "kill" },
      { type: "line*", content: "1%" },
      { type: "icon", content: "/src/assets/img/icons/warp_tall.svg" },
    ]}
  />

  <Starline
    content={[
      { type: "icon", content: "/src/assets/img/icons/star_area.svg" },
      { type: "line*", content: "1%" },
      { type: "text", content: "the" },
      { type: "line", content: "45%" },
      { type: "text*", content: "revolution" },
      { type: "line", content: "15%" },
      { type: "icon", content: "/src/assets/img/icons/circle_grid_flat.svg" },
    ]}
  />

  <img
    src="/src/assets/img/thres/{isJdsActive ? 'chess' : 'war'}.jpg"
    alt="Warp Skew"
    class=""
    style="width: 100%; height: auto; margin-top: 2em; border: 1px solid var(--primary);"
  />
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
    flex: auto;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .catalogue-toggle {
    display: flex;
    flex-direction: row;
    gap: 1rem;
    align-items: center;
    width: 100%;

    button {
      flex: 1;
      display: l flex;
      width: 100%;
      height: 100%;
      padding: 0;
      border: none;
      border-radius: 0px;
      overflow: hidden;
      outline: 1px solid var(--primary);

      &.active {
        /* Active button styling */
        outline: 1px solid var(--accent);
      }
    }
  }
</style>
