<script>
  import { page } from '$app/stores';
  import PointBar from './PointBar.svelte';
  import { onMount } from 'svelte'; // Added import for onMount

  const menuItems = [
    { href: '/', label: 'Home' },
    { href: '/about', label: 'About' },
    { href: '/catalogue', label: 'Catalogue' }
  ];

  let isMenuOpen = false;
  let isVisible = true;
  let lastScrollY = 0;

  function toggleMenu() {
    isMenuOpen = !isMenuOpen;
  }

  function handleScroll() {
    const currentScrollY = window.scrollY;
    isVisible = currentScrollY < lastScrollY || currentScrollY <= 0;
    lastScrollY = currentScrollY;
  }

  onMount(() => {
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  });
</script>

<nav class:hide={!isVisible}>
  <div class="navbar-container">
    <div class="logo">
      <a href="/">
        <img src="/logo/cartel_dragon.png" alt="Logo" />
        <img src="/logo/cartel_guisol.svg" class="inv" alt="Logo" />
      </a>
    </div>
    
    <div class="bar">
      <PointBar Color="white" endDotSrc="/icons/star_rf.svg" imageScale={3} />
    </div>
    
    <ul class="menu {isMenuOpen ? 'open' : ''}">
      {#each menuItems as item}
        <li class:active={$page.url.pathname === item.href}>
          <a href={item.href}>{item.label}</a>
        </li>
      {/each}
    </ul>
    
    <button class="menu-toggle" on:click={toggleMenu} aria-label="Toggle menu">
      ☰
    </button>
  </div>
</nav>

<style>
  nav {
    position: fixed;
    top: 0;
    width: 100%;
    z-index: 1000;
    background-color: #111111;
    color: var(--white);
    padding: 0.75rem 1rem;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3);
    transition: transform 0.3s ease, background-color 0.3s ease;
  }

  nav.hide {
    transform: translateY(-100%);
  }

  .navbar-container {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
  }

  .logo {
    font-size: 1.5rem;
    font-weight: bold;
    max-width: 200px;
    flex-shrink: 0;
  }

  .logo a {
    display: flex;
    align-items: center;
  }

  .logo img:first-child {
    height: 3rem;
  }

  .logo img:last-child {
    height: 2rem;
  }

  .logo img {
    width: auto;
    height: 2rem;
  }

  .inv {
    filter: invert(1) drop-shadow(0 0 0.0em #0000);
  }

  .bar {
    padding: 0 2rem;
    flex-grow: 1;
  }

  .menu {
    display: flex;
    list-style: none;
    margin: 0;
    padding: 0;
  }

  .menu li {
    position: relative;
    margin: 0 0.5rem;
  }

  .menu li a {
    display: block;
    color: white;
    text-decoration: none;
    padding: 0.75rem 1rem;
    border-radius: 5px;
    transition: all 0.3s ease;
    border: 2px solid transparent;
  }

  .menu li a:hover {
    background-color: var(--red);
    color: white;
    transform: scale(1.1);
  }

  .menu li.active a {
    background-color: var(--red);
    font-weight: bold;
    border: var(--dark-red) 2px solid;
  }

  .menu-toggle {
    display: none;
    background: none;
    border: none;
    color: white;
    font-size: 1.5rem;
    cursor: pointer;
  }

  @media (max-width: 768px) {
    .navbar-container {
      flex-wrap: nowrap;
      justify-content: space-between;
    }

    .logo {
      max-width: auto;
      flex-shrink: 1;
    }

    .logo img:first-child {
      height: 2.5rem;
    }

    .logo img:last-child {
      height: 1.5rem;
    }

    .menu {
      position: absolute;
      top: 100%;
      left: 0;
      right: 0;
      order: 3;
      width: 100%;
      flex-direction: column;
      align-items: center;
      max-height: 0;
      overflow: hidden;
      transition: max-height 0.3s ease;
      box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3);
    }
    
    .menu li {
      margin: 0.5rem 0;
      text-align: center;
    }

    .menu.open {
      max-height: 300px;
      padding-top: 0.5rem;
      padding-bottom: 0.5rem;
    }

    .menu-toggle {
      display: block;
      order: 2;
      padding: 0.5rem;
      margin-left: 0.5rem;
    }
  }
</style>