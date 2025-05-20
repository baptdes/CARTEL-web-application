<script lang="ts">
  export let text: string = "TEST";
  export let size: string = "2rem";
</script>

<div class="doubleText outer">
  <span class="text1 below" style="font-size: {size};">
    {text}
  </span>
  <span class="text2 top" style="font-size: {size}">
    {text}
  </span>
</div>

<style lang="scss">
  .doubleText {
    color: var(--primary);
    transform: scale(1,1);
  }

  .text1 {
    text-align: center;
    align-self: center;

    text-transform: uppercase;
    -webkit-text-stroke: 1px var(--primary);
    color: var(--back);
    white-space: nowrap;
    font-family: "Guisol", monospace;
    transform: scale(1,2);
    transition: all .5s ease-in-out;
  }

  .text2 {
    text-align: center;
    align-self: center;

    text-transform: uppercase;
    color: var(--accent);
    white-space: nowrap;
    font-family: "Apocalypse Grunge", cursive;
    text-shadow: 0 0 10px var(--back), 0 0 5px var(--back),0 0 4px var(--back);
  }

  .outer {
    display: grid;
    grid-template: 1fr / 1fr;
    place-items: center;
  }
  .outer > * {
    grid-column: 1 / 1;
    grid-row: 1 / 1;
  }
  .outer .below {
    z-index: 1;
  }
  .outer .top {
    z-index: 2;
  }
</style>
