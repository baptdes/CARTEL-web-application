<script>
    import { goto } from '$app/navigation';
    import { isAuthenticated, logout } from '$lib/auth';
  
    let authenticated = $state(false);

    $effect(() => {
    authenticated = $isAuthenticated;
    if (!authenticated) {
        goto('/login');
    }
    });
  
  function handleLogout() {
    logout();
    goto('/login');
  }
  </script>
  
  <div class="admin-layout">
    <div class="admin-header">
      <h1>Panel d'administration</h1>
      <button class="logout-button" onclick={handleLogout}>Déconnexion</button>
    </div>
    <slot /> 
  </div>
  
  <style>
    .admin-layout {
      padding: 2rem;
      max-width: 1200px;
      margin: 0 auto;
    }
  
    .admin-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 2rem;
      padding-bottom: 1rem;
      border-bottom: 2px solid var(--dark-red);
    }
  
    h1 {
      font-family: "Pirata One", cursive;
      color: var(--red);
      margin: 0;
    }
  
    .logout-button {
      background-color: var(--dark-red);
      color: white;
      border: none;
      padding: 0.5rem 1rem;
      border-radius: 4px;
      cursor: pointer;
      font-weight: bold;
      transition: background-color 0.3s;
    }
  
    .logout-button:hover {
      background-color: var(--red);
    }
  </style>