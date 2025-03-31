<script>
  import PointBar from "./misc/PointBar.svelte";
</script>

<nav class="grain">
  <div class="logo">
    <a href="/" aria-label="Home">
      <img src="src/assets/img/cartel_guisol.svg" />
    </a>
  </div>
  <PointBar Color="var(--primary)" />
  <ul class="nav-links">
    <li>
      <a href="/catalogue">Catalogue</a>
    </li>
    <div class="vr"></div>
    <li>
      <a href="/courses">Courses</a>
    </li>
    <div class="vr"></div>
    <li>
      <a href="/admin">Admin</a>
    </li>
  </ul>
</nav>

<style>

  nav {
    background-color: var(--background);
    overflow: hidden;
    display: flex;
    justify-content: space-between;
    /* Add spacing between items */
    gap: 2rem;
    align-items: center;
    color: white;
    padding: 1rem 2rem;
    border-bottom: var(--primary) 1px solid;
    position: relative;
  }

  .logo {
    max-width: 200px; /* Adjust this value based on your desired logo size */
  }

  .logo img {
    width: auto;
    height: 3rem;
    filter: invert(0) drop-shadow(0 0 0.1em #ffffff);
    display: block;
    will-change: filter;
    transition: filter 300ms;
  }

  .logo:hover img {
    filter: invert(1) drop-shadow(0 0 0.3em #000000);
  }

  .logo a {
    font-size: 1.5rem;
    font-weight: bold;
    color: white;
    text-decoration: none;
  }

  .nav-links {
    display: flex;
    list-style: none;
    gap: 1.5rem;
    margin: 0;
    padding: 0;
  }

  .nav-links li a {
    color: white;
    text-decoration: none;
    transition: color 0.3s;
  }

  .nav-links li a:hover {
    color: #ddd;
  }

  .active a {
    color: #00bcd4;
    font-weight: bold;
  }
</style>
