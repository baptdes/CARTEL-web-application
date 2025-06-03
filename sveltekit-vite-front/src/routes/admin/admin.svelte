<script>
  import { goto } from "$app/navigation";
  import { adminPageState } from "./store.js";
  import DoubleText from "$lib/misc/DoubleText.svelte";
  import PointBar from "$lib/misc/PointBar.svelte";
  import Starline from "$lib/misc/Starline.svelte";
</script>

<main>
  <!-- <PointBar Color="var(--accent)" /> -->

  <div class="boxes">
    <button
      class="innerBouton line"
      type="button"
      onclick={() => goto("/admin/catalogue")}
    >
      <div class="border">
        <img
          src="/src/assets/img/thres/distort_grid.jpg"
          alt="arrow"
          class="thres hoverBlur"
        />
      </div>
      <DoubleText text="CATALOGUE" size="4em" hoverAnim=true />
    </button>

    <button
      class="innerBouton line"
      type="button"
      onclick={() => goto("/admin/purge_course")}
    >
      <DoubleText text="PURGE" size="4em" hoverAnim=true />
      <div class="border">
        <img src="/src/assets/img/thres/fire.jpg" alt="arrow" class="thres" />
      </div>
    </button>

    <button
      class="innerBouton line"
      type="button"
      onclick={() => goto("/admin/tresorerie")}
    >
      <div class="border">
        <img
          src="/src/assets/img/thres/stock_chart.jpg"
          alt="arrow"
          class="thres hoverBlur"
        />
      </div>
      <DoubleText text="trésorerie" size="4em" hoverAnim=true />
    </button>

    <button
      class="innerBouton line"
      type="button"
      onclick={() => goto("/admin/emprunt_pret")}
    >
      <DoubleText text="Emprunt/Prêt" size="4em" hoverAnim=true />
      <div class="border">
        <img
          src="/src/assets/img/thres/hands_e.jpg"
          alt="arrow"
          class="thres"
        />
      </div>
    </button>
  </div>

  <PointBar Color="var(--accent)" />

  <Starline
    content={[
      { type: "icon*", content: "/src/assets/img/icons/star_dual_skew.svg" },
      { type: "text", content: "let the" },
      { type: "line", content: "30%" },
      { type: "text*", content: "people" },
      { type: "line", content: "25%" },
      { type: "icon", content: "/src/assets/img/icons/world_grid_outline.svg" },
    ]}
  />

  <Starline
    content={[
      { type: "icon", content: "/src/assets/img/icons/three_arcs.svg" },
      { type: "line", content: "20%" },
      { type: "icon*", content: "/src/assets/img/icons/ring_bell.svg"},
      { type: "text", content: "take control" },
      { type: "line", content: "15%" },
      { type: "text", content: "back" },
      { type: "icon", content: "/src/assets/img/icons/warp_skew.svg" },
    ]}
  />

  <Starline
    content={[
      { type: "icon", content: "/src/assets/img/icons/star_sparkle.svg" },
      { type: "line*", content: "15%" },
      { type: "text", content: "and reach" },
      { type: "line", content: "25%" },
      { type: "text*", content: "the divine" },
      { type: "line", content: "15%" },
      { type: "icon", content: "/src/assets/img/icons/double_cone_skew.svg" },
    ]}
  />

  <img
    src="/src/assets/img/thres/divine.jpg"
    alt="Warp Skew"
    class=""
    style="width: 100%; height: auto; margin-top: 2em; border: 1px solid var(--primary);">

</main>

<style lang="scss">
  @use "/src/lib/sass/base";
  @use "/src/lib/sass/mixins" as mixins;

  .border {
    border: 1px solid var(--primary);
    overflow: hidden;
    position: relative;
    img {
      transition: all 0.5s ease-in-out;
      height: 7em; /* Match the text size */
      width: 100%; /* Maintain aspect ratio */
      object-fit: cover; /* Ensure the entire image is visible */
      vertical-align: middle; /* Align with text baseline */
    }
  }

  .thres {
    @include mixins.thres(0%, 0px);
    &:hover {
      @include mixins.thres(50%, 4px);
    }
  }

  .line {
    display: flex;
    flex-direction: row;
    flex-grow: 1;
    gap: 1em;
  }

  /* Added: ensure the image container has the same dimensions */
  .line .border {
    max-height: 100%; /* Limit to parent height */
    width: 100%;
    flex-grow: 1;
    min-width: 100px;
  }

  .innerBouton {
    border-radius: 0em;
    border: none;
    background-color: transparent;
    flex-grow: 0;

    &:hover {
      img {
        @include mixins.thres(100%, 4px);
      }
    }
  }

  .boxes {
    max-width: 100vw;
    width: 100%;
    display: flex;
    flex-direction: column;
    flex-grow: 1;
    box-sizing: border-box; /* Added: include padding in width calculation */
  }

  main {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 100%; /* Added: ensure main takes full width */
    height: 100%;
    box-sizing: border-box;
  }
</style>
