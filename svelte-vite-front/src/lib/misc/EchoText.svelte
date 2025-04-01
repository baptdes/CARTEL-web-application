<script>
  // Props for the component
  export let text = "Default text";
  export let echoes = 3;
  export let maxScale = 2;
  export let backgroundColor = "black";
</script>

<div class="stack">
  <p class="stack-inner" style="transform: scaleY({maxScale})">
    {text}
  </p>
  <p class="stack-inner">
    {text}
  </p>
</div>

<style>
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

    /* For every class such as stack-<number> */
    
  

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
</style>
