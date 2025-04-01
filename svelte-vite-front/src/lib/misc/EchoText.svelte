<script>
  // Props for the component
  export let text = "Default text";
  export let textSize = 3;
  export let textColor = "var(--primary)";
  export let maxScale = 1.5;
</script>

<div class="stack" style="--max-scale: {maxScale};--text-color : {textColor};--text-size : {textSize}rem;">
    <!-- For i in range of 0 to echoes, -->
    {#each Array.from({ length: 3 }) as _, i}
        <p class="stack-inner">
            {text}
        </p>
    {/each}

</div>

<style lang="scss">

    @use "sass:math" as math;
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
    $maxScale: var(--max-scale);
    $textColor: var(--text-color);
    
    .stack{

        @for $i from 1 through 3 {
        :nth-child(#{$i}) {
            transform: scaleY(calc($maxScale - ($maxScale/3) * ($i - 2)));
            // background-color: var(--background-color);
            text-align: center;
            paint-order: stroke;
            -webkit-text-stroke: 0.4rem;
            -webkit-text-stroke-color: var(--background);
            }
        }

    }


    .stack-inner {
        color: $textColor;
        font-size: var(--text-size);
    }
</style>
