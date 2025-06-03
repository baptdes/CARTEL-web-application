<script>
  export let content = [
    { type: "text", content: "Starline" },
    { type: "line", content: "50%" },
    { type: "icon", content: "/src/assets/img/icons/world_grid_outline.svg" },
  ];

  export let size = "64"; // in pixels
</script>

<div class="starline" style="height: {size}px;">
  {#each content as item}
    {#if item.type === "text"}
      <span class="starline-text" style="font-size: {size / 2}px;">
        {item.content}
      </span>
    {:else if item.type === "text*"}
      <span
        class="starline-text"
        style="font-size: {size / 2}px; color: var(--accent);"
      >
        {item.content}
      </span>
    {:else if item.type === "line"}
      <div class="starline-line" style="width:{item.content};"></div>
    {:else if item.type === "line*"}
      <div class="starline-line" style="width:{item.content}; background-color: var(--accent);"></div>
    {:else if item.type === "icon"}
      <img
        src={item.content}
        alt="icon"
        class="starline-icon"
        style="height: {size}px; width: {size}px;"
      />
    {:else if item.type === "icon*"}
      <img
        src={item.content}
        alt="icon"
        class="starline-icon colorised"
        style="height: {size}px; width: {size}px;"
      />
    {/if}
  {/each}
</div>

<style lang="scss">
  @use "/src/lib/sass/base" as base;

  .starline {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: center;
    flex-grow: 1;
    width: 100%;
    gap: 12px;
    color: var(--primary);

    .colorised {
      @extend .accent-colorize;
    }

    .starline-icon {
      object-fit: contain;
    }

    .starline-text {
      font-family: Guisol;
      text-transform: uppercase;
      transform: scale(1, 2);
      white-space: nowrap;
    }

    .starline-line {
      height: 2px;
      background-color: var(--primary);
      flex-grow: 1;
    }
  }
</style>
