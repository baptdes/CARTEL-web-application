<script lang="ts">
  export let src: string = "/src/assets/img/thres/divine.jpg";
  export let text: string = "test";
  export let isAnimated: boolean = false;

  export let size: number = 3;
  function handleError(e) {
    console.error("Image failed to load:", src, e);
  }

  function handleLoad() {
    console.log("Image loaded successfully:", src);
  }
</script>

<div
  class="outer {isAnimated ? 'isAnimated' : ''}"
  style="min-width: {text.length * size + (text.length - 1)}em;
     height: {size * 1.3}em;"
>
  <img
    {src}
    class="below"
    alt={src.split("/").pop().split(".")[0].toUpperCase()}
    on:error={handleError}
    on:load={handleLoad}
  />
  <span
    class="top"
    style="
        font-size: {size}em;
        transform: scaleY(1.5) translateY({1.3*size}px);"
  >
    <p>{text}</p>
  </span>
</div>

<style>
  div {
    width: 100%;
    position: relative;
    overflow: hidden;
  }

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  span {
    color: var(--back);
    font-family: Guisol;
    font-weight: bold;
    text-align: center;
    text-transform: uppercase;

    -webkit-text-stroke: 1px var(--primary);

    width: 100%;
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

  .isAnimated {
    span {
        p {
            transition: transform 0.1s ease-in-out;
            transform: scaleX(1);

            &:hover {
                transform: scaleX(2);
            }
        }
    }
  }
</style>
