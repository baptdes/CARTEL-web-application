<script>
  import { page } from '$app/stores';
  import PointBar from './PointBar.svelte';

  const menuItems = $state([
    { href: '/', label: 'Accueil' },
    { href: '/about', label: 'Test' },
    { href: '/catalogue', label: 'Catalogue' }
  ]);

  let isMenuOpen = $state(false);
  let isVisible = $state(true);
  let lastScrollY = $state(0);

  function toggleMenu() {
    isMenuOpen = !isMenuOpen;
  }

  function handleScroll() {
    const currentScrollY = window.scrollY;
    isVisible = currentScrollY < lastScrollY || currentScrollY <= 0;
    lastScrollY = currentScrollY;
  }

  $effect(() => {
    if (typeof window !== 'undefined') {
      window.addEventListener('scroll', handleScroll);
      return () => window.removeEventListener('scroll', handleScroll);
    }
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
    
    <button class="menu-toggle" onclick={toggleMenu} aria-label="Toggle menu">
      ☰
    </button>
  </div>
</nav>

<style lang="scss">
  $nav-height: 3rem;
  $shadow-default: 0 4px 10px rgba(0, 0, 0, 0.3);
  $transition-speed: 0.3s;
  $logo-height: 3rem;
  $logo-height-small: 2.5rem;
  $border-radius: 5px;
  $spacing-sm: 0.5rem;
  $spacing-md: 0.75rem;
  $spacing-lg: 1rem;
  $spacing-xl: 2rem;
  
  nav {
    position: fixed;
    top: 0;
    width: 100%;
    z-index: 1000;
    background-color: #111111;
    color: var(--white);
    padding: $spacing-md $spacing-md;
    box-shadow: $shadow-default;
    transition: transform $transition-speed ease, background-color $transition-speed ease;
    
    &.hide {
      transform: translateY(-100%);
    }
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
    
    a {
      display: flex;
      align-items: center;
    }
    
    img {
      width: auto;
      height: 2rem;
      
      &:first-child {
        height: $logo-height;
      }
      
      &:last-child {
        height: 2rem;
      }
    }
  }

  .inv {
    filter: invert(1) drop-shadow(0 0 0.0em #0000);
  }

  .bar {
    padding: 0 $spacing-xl;
    flex-grow: 1;
  }

  .menu {
    display: flex;
    list-style: none;
    margin: 0;
    padding: 0;
    background-color: #111111;
    
    li {
      position: relative;
      margin: 0 $spacing-sm;
      
      a {
        display: block;
        color: white;
        text-decoration: none;
        padding: $spacing-md $spacing-md;
        border-radius: $border-radius;
        transition: all $transition-speed ease;
        border: 2px solid transparent;
        background-color: var();
        
        &:hover {
          background-color: var(--red);
          color: white;
          transform: scale(1.1);
        }
      }
      
      &.active a {
        background-color: var(--red);
        font-weight: bold;
        border: var(--dark-red) 2px solid;
      }
    }
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
      
      img {
        &:first-child {
          height: $logo-height-small;
        }
        
        &:last-child {
          height: 1.5rem;
        }
      }
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
      transition: max-height $transition-speed ease;
      box-shadow: $shadow-default;
      
      &.open {
        max-height: 300px;
        padding-top: $spacing-sm;
        padding-bottom: $spacing-sm;
      }
      
      li {
        margin: $spacing-sm 0;
        text-align: center;
      }
    }

    .menu-toggle {
      display: block;
      order: 2;
      padding: $spacing-sm;
      margin-left: $spacing-sm;
    }
  }
</style>