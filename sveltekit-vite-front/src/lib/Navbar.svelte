<script>
  import PointBar from "./misc/PointBar.svelte";
</script>

<nav class="grain">
  <div class="logo hover-invert">
    <a href="/" aria-label="Home">
      <img src="src/assets/img/cartel_guisol.svg" />
    </a>
  </div>
  <PointBar Color="var(--primary)" endDotSrc="src/assets/img/icons/star_rf.svg" imageScale={3} />
  <ul class="nav-links">
    <li>
      <a class="hover-invert" href="/catalogue">Catalogue</a>
    </li>
    <div class="vr"></div>
    <li>
      <a class="hover-invert" href="/courses">Courses</a>
    </li>
    <div class="vr"></div>
    <li>
      <a class="hover-invert" href="/admin">Admin</a>
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
    filter: invert(1) drop-shadow(0 0 0.0em #ffffff);
  }

  .hover-invert {
    will-change: filter;
    transition: filter 300ms;
    display: block;
    color: var(--primary);
    filter: invert(0) drop-shadow(0 0 0em #ffffff);
  }

  .hover-invert:hover {
    will-change: filter;
    transition: filter 300ms;
    filter: invert(1) drop-shadow(0 0 0.3em #ffffff);
  }

  .logo a {
    font-size: 1.5rem;
    font-weight: bold;
    color: var(--primary);
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
