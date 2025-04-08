<script>
  import "../app.css";
  import Navbar from '$lib/components/Navbar.svelte';
</script>

<div class="app">
  <Navbar />
  <main>
    <slot />
  </main>
</div>

<style>
  .app {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
  }

  main {
    flex: 1;
  }
</style>
