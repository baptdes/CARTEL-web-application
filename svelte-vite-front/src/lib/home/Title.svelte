<script>
  import { text } from "@sveltejs/kit";
  import EchoText from "../misc/EchoText.svelte";
  import PointBar from "../misc/PointBar.svelte";

  const letters = ["C", "A", "R", "T", "E", "L"];
  const repeats = 250; // Initial value

</script>

<section class="title-section">
  <div class="grid-background"></div>

  <div class="content">
    <div class="logo-container">
      <img
        class="colorized-logo"
        src="src/assets/img/cartel_guisol.svg"
        alt="Logo"
      />
      <div class="flex-vertical spice-icons-container">
        <img class="spice-icon" src="src/assets/img/icons/star_skew_twin_octo.svg" alt="ICON" />
        <img class="spice-icon" src="src/assets/img/icons/echo_wave.svg" alt="ICON" />
        <img class="spice-icon" src="src/assets/img/icons/earth_flat.svg" alt="ICON" />
      </div>
    </div>
    <div class="stack">
      <div class="stack-inner back-cont">
        <p class="stack-text back">
          {#each letters as letter}
            {letter.repeat(repeats)}<br />
          {/each}
        </p>
      </div>
      <div class="stack-inner">
        <p class="stack-text front">
          Catalogue <br />
          Annuellement <br />
          Ressucité et <br />
          Téléconsultable d' <br />
          Elements <br />
          Ludiques
        </p>
      </div>
    </div>
    <PointBar
      width="100%"
      startDotSrc="src/assets/img/icons/star_rf.svg"
      endDotSrc="src/assets/img/icons/star_rf.svg"
      imageScale={3}
    />
    <div class="description">
      <div class="column">
        <EchoText text="TEST" textColor="var(--tertiary)" />
      </div>
      <div class="column">
        <EchoText text="TEST" />
      </div>
    </div>
  </div>
</section>

<style lang="scss">
  @use '../sass/variables' as v;
  @use '../sass/mixins' as m;

  .description {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 2rem;
    margin-top: 2rem;
  }

  .column {
    flex-grow: 1;
  }

  .stack {
    display: grid;
    grid-template: 1fr / 1fr;
    place-items: center;
    position: relative;
    width: 100%;
    margin: 2rem 0;
    
    > * {
      grid-column: 1 / 1;
      grid-row: 1 / 1;
      width: 100%;
    }
  }

  .stack-text {
    font-size: 1.5rem;
    font-family: Guisol;
    text-align: center;
    margin: 0;
    padding: 0;
    text-transform: uppercase;
    transform-style: preserve-3d;
    
    &.back {
      text-align: center;
      color: var(--tertiary);
      line-height: 1.6;
    }
    
    &.front {
      line-height: 1.6;
      paint-order: stroke;
      -webkit-text-stroke: 2rem;
      -webkit-text-stroke-color: var(--background);
    }
  }

  .back-cont {
    max-width: 80%;
    box-sizing: border-box;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: clip;
    display: flex;
    justify-content: center;
  }

  .title-section {
    position: relative;
    height: 80vh;
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: visible;
    perspective: 800px;
    margin: 5rem;
  }

  .grid-background {
    position: absolute;
    top: -100%;
    left: -100%;
    width: 300%;
    height: 300%;
    background-image: linear-gradient(
        to right,
        rgba(42, 67, 101, 0.15) 1px,
        transparent 1px
      ),
      linear-gradient(to bottom, rgba(42, 67, 101, 0.15) 1px, transparent 1px);
    background-size: 50px 50px;
    transform: rotateX(60deg);
    animation: grid-move 0.8s linear infinite;
  }

  @keyframes grid-move {
    0% {
      transform: rotateX(60deg) translateY(0);
    }
    100% {
      transform: rotateX(60deg) translateY(50px);
    }
  }

  .content {
    text-align: center;
    z-index: 10;
    padding: 2rem;
    border-radius: 8px;
    @include m.flex-column;
    justify-content: center;
  }

  .logo-container {
    margin-bottom: 1rem;
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 2rem;
  }

  .colorized-logo {
    fill: var(--tertiary);
    height: 10rem;
    width: auto;
    filter: invert(1) drop-shadow(0 0 1em var(--quaternary));
  }

  .spice-icons-container {
    height: 10rem;
    @include m.flex-column;
    justify-content: space-between;
    gap: 1rem;
    overflow: visible;
  }

  .spice-icon {
    max-height: calc((10rem - 2rem) / 3);
    width: auto;
    filter: drop-shadow(0 0 1em var(--quaternary));
  }
</style>
