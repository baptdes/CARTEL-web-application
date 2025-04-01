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
        <img class="spice-icon fit" src="src/assets/img/icons/star_skew_twin_octo.svg" />
        <img class="spice-icon fit" src="src/assets/img/icons/echo_wave.svg" />
        <img class="spice-icon fit" src="src/assets/img/icons/earth_flat.svg" />
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
      <div class="stack-inner" style="align:center">
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
        <EchoText text="TEST" />
      </div>
      <div class="column"></div>
    </div>
  </div>
</section>

<style>

  .description {
    display: flex;
    justify-content: space-between;
    align-items: center;
    align-content: center;
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
  }
  .stack > * {
    grid-column: 1 / 1;
    grid-row: 1 / 1;
    width: 100%;
  }

  .stack-text {
    font-size: 1.5rem;
    font-family: Guisol;
    text-align: center;
    margin: 0;
    padding: 0;
    text-transform: uppercase;
    transform-style: preserve-3d;
    transition: transform 0.5s;
  }

  .back-cont {
    /* Prevent overflow text from expanding container */
    max-width: 80%;
    box-sizing: border-box;
    overflow: hidden;
    display: block;
    width: auto;
    white-space: nowrap;
    /* position: relative; */
    overflow: hidden;
    text-overflow: clip;
    display: flex;
    justify-content: center;
  }

  .back {
    text-align: center;
    color: var(--tertiary);
    position: static; /* Position the text absolutely */
    left: 50%; /* Position from center */
    width: max-content; /* Allow the text to take its natural width */
    line-height: 1.6;
  }

  .front {
    height: 1.6;
    paint-order: stroke;
    -webkit-text-stroke: 2rem;
    -webkit-text-stroke-color: var(--background);
  }

  .title-section {
    position: relative;
    height: 80vh;
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: hidden;
    perspective: 800px;
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
    display: flex;
    flex-direction: column;
    justify-content: center;
  }

  .fit {
  }

  .logo-container {
    margin-bottom: 1rem;
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: row;
    gap: 2rem;
  }

  .colorized-logo {
    fill: var(--tertiary);
    height: 10rem;
    width: auto;
    filter: invert() drop-shadow(0 0 1em var(--quaternary));
  }

  .spice-icons-container {
    height: 10rem; /* Set fixed height to match logo */
    display: flex;
    justify-content: space-between; /* Changed from center to space-between */
    align-items: center;
    flex-direction: column;
    gap: 1rem;
    overflow: visible; /* Prevent overflow */
  }

  .spice-icon {
    max-height: calc(
      (10rem - 2rem) / 3
    ); /* Calculate max height based on container */
    width: auto;
    filter: drop-shadow(0 0 1em var(--quaternary));
  }
</style>
