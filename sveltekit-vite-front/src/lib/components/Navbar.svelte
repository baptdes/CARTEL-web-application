<script>
  import { page } from '$app/stores';

  // Define menu items
  const menuItems = [
    { href: '/', label: 'Home' },
    { href: '/about', label: 'About' }
    { href: '/catalogue', label: 'Catalogue' }
  ];
</script>

<nav>
  <div class="logo">
    <a href="/">C.A.R.T.E.L</a>
  </div>
  <ul class="menu">
    {#each menuItems as item}
      <li class:active={$page.url.pathname === item.href}>
        <a href={item.href}>{item.label}</a>
      </li>
    {/each}
  </ul>
</nav>

<style>
  nav {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background-color: #333;
    color: white;
    padding: 0 1rem;
    box-shadow: 0 2px 5px rgba(0,0,0,0.1);
  }

  .logo {
    font-size: 1.5rem;
    font-weight: bold;
    padding: 1rem 0;
  }

  .logo a {
    color: #ff3e00;
    text-decoration: none;
  }

  .menu {
    display: flex;
    list-style: none;
    margin: 0;
    padding: 0;
  }

  .menu li {
    position: relative;
  }

  .menu li a {
    display: block;
    color: white;
    text-decoration: none;
    padding: 1rem;
    transition: background-color 0.3s;
  }

  .menu li a:hover {
    background-color: #444;
  }

  .menu li.active a {
    background-color: #555;
    font-weight: bold;
  }

  .menu li.active::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 3px;
    background-color: #ff3e00;
  }

  @media (max-width: 768px) {
    nav {
      flex-direction: column;
    }

    .menu {
      width: 100%;
    }

    .menu li {
      text-align: center;
      flex: 1;
    }
  }
</style>
