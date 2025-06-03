<script lang="ts">
  export let text: string = "TEST";
  export let size: number = "4";
  export let hoverAnim: boolean = false;
</script>

<div class="doubleText outer" style="height: {size * 2}rem;">
  <span class="text below" style="font-size: {size}rem;">
    {text}
  </span>
  <span class="text middle" style="font-size: {size}rem">
    {text}
  </span>
  <span class="text top" style="font-size: {size}rem">
    {text}
  </span>
</div>

<style lang="scss">
  .doubleText {
    color: var(--primary);
    transform: scale(1, 1);
    .top {
      transform: scale(1, 1);
    }
    .middle {
      transform: scale(1, 1.5);
    }
    .below {
      transform: scale(1, 2);
    }
  }

  .text {
    text-align: center;
    align-self: center;
    text-shadow: 0 0 10px var(--back);

    text-transform: uppercase;
    -webkit-text-stroke: 1px var(--primary);
    color: var(--back);
    white-space: nowrap;
    font-family: "Guisol", monospace;
    transition: all 0.5s ease-in-out;
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
  .outer .middle {
    z-index: 2;
  }
  .outer .top {
    z-index: 3;
  }
</style>
