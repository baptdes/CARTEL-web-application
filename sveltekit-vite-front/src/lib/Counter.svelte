<script>
  let count = $state(0);
  const increment = () => {
    count += 1;
  };
</script>

<!-- svelte-ignore event_directive_deprecated -->
<button on:click={increment}>
  count is {count}
</button>
