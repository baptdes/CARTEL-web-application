<script>
  import "../app.css";
  import Navbar from '$lib/components/Navbar.svelte';
</script>

<div class="app">
  <Navbar />
  <main>
    <slot />
  </main>
  <footer>
    <p>© {new Date().getFullYear()} C.A.R.T.E.L - Catalogue Annuellement Ressucité et Téléconsultable d'Elements Ludiques</p>
  </footer>
</div>

<style>
  .app {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
  }

  main {
    flex: 1;
    padding: 1rem;
  }

  footer {
    text-align: center;
    padding: 1rem;
    background-color: #f5f5f5;
    font-size: 0.8rem;
    color: #666;
    margin-top: 2rem;
  }
</style>
