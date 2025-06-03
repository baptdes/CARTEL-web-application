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

<div class="image-text-container"
    style="max-width: {text.length*size}em; max-height: {size};">
  <img {src} 
    alt="{src.split('/').pop().split('.')[0].toUpperCase()}"
    on:error={handleError} 
    on:load={handleLoad} />
  <span
    class:isAnimated={isAnimated}
    style="font-size: {size};">
    {text}
  </span>
</div>

<style>
  div {
    position: relative;
    width: 100%;
    height: 100%;
    overflow: hidden;
    display: block;
  }

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
  }

  span {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);

    color: var(--back);
    font-weight: bold;
    text-align: center;
    text-transform: uppercase;

    -webkit-text-stroke: 1px var(--primary);

    z-index: 2;
    width: 90%;
    padding: 0.5rem;
  }

    .isAnimated {
        letter-spacing: 0px;
        transition: letter-spacing 0.3s ease-in-out;

        &:hover {
            letter-spacing: 15px;
        }
    }
</style>
