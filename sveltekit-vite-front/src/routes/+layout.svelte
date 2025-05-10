<script>
  import "../app.css";
  import Navbar from '$lib/components/Navbar.svelte';
  import Footer from '$lib/components/Footer.svelte';
</script>

<div class="app">
  <Navbar />
  <main>
    <slot />
  </main>
  <Footer />
</div>

<style>
  .app {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
  }

  main {
    flex: 1;
    padding-top: 4rem;
  }
</style>
