<script>
  import { goto } from '$app/navigation';
  import { login, isAuthenticated, authLoading } from '$lib/auth';

  let password = $state('');
  let errorMessage = $state('');
  let showPassword = $state(false);

  // Replace onMount with $effect
  $effect(() => {
    if ($isAuthenticated) {
      goto('/admin');
    }
  });

  async function handleSubmit() {
    errorMessage = '';
    
    if (!password) {
      errorMessage = 'Veuillez entrer le mot de passe';
      return;
    }
    
    const result = await login(password);
    
    if (result === true) {
      goto('/admin');
    } else if (result.error) {
      errorMessage = result.error;
    }
  }

  function togglePasswordVisibility() {
    showPassword = !showPassword;
  }
</script>

<div class="login-container">
  <div class="login-card">
    <div class="login-header">
      <img src="/logo/cartel_dragon.png" alt="CARTEL Logo" class="logo" />
      <h1>Administration CARTEL</h1>
    </div>
    <form onsubmit={handleSubmit} class="login-form">
      <div class="form-group">
        <label for="password">Mot de passe</label>
        <div class="password-input-container">
          <input
            type={showPassword ? 'text' : 'password'}
            id="password"
            bind:value={password}
            placeholder="Entrez le mot de passe"
            autocomplete="current-password"
          />
          <button 
            type="button" 
            class="toggle-password" 
            onclick={togglePasswordVisibility}
            aria-label={showPassword ? 'Cacher le mot de passe' : 'Afficher le mot de passe'}
          >
            {showPassword ? '👁️' : '👁️‍🗨️'}
          </button>
        </div>
      </div>
      
      {#if errorMessage}
        <div class="error-message">
          {errorMessage}
        </div>
      {/if}
      
      <button 
        type="submit" 
        class="login-button" 
        disabled={$authLoading}
      >
        {#if $authLoading}
          <span class="loading-spinner"></span> Connexion...
        {:else}
          Se connecter
        {/if}
      </button>
    </form>
    
    <div class="login-footer">
      <a href="/">Retour à l'accueil</a>
    </div>
  </div>
</div>

<style>
  .login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: calc(100vh - 4rem);
    padding: 2rem;
    background-image: url('/red_wallpaper.webp');
    background-size: cover;
    background-position: center;
  }
  
  .login-card {
    width: 100%;
    max-width: 450px;
    background-color: rgba(26, 26, 26, 0.95);
    border-radius: 8px;
    padding: 2rem;
    box-shadow: 0 10px 25px rgba(0, 0, 0, 0.5);
    border: 2px solid var(--dark-red);
    position: relative;
  }
  
  .login-header {
    text-align: center;
    margin-bottom: 2rem;
  }
  
  .logo {
    width: 80px;
    margin-bottom: 1rem;
  }
  
  h1 {
    font-family: "Pirata One", cursive;
    color: var(--red);
    font-size: 2.2rem;
    margin: 0;
  }
  
  .login-form {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }
  
  .form-group {
    display: flex;
    flex-direction: column;
  }
  
  label {
    margin-bottom: 0.5rem;
    color: var(--white);
    font-weight: bold;
  }
  
  .password-input-container {
    position: relative;
    display: flex;
    align-items: center;
  }
  
  input {
    width: 100%;
    padding: 0.8rem 1rem;
    border: 2px solid #444;
    background-color: #222;
    color: white;
    border-radius: 4px;
    font-size: 1rem;
    transition: border-color 0.3s ease;
  }
  
  input:focus {
    outline: none;
    border-color: var(--orange);
  }
  
  .toggle-password {
    position: absolute;
    right: 10px;
    background: none;
    border: none;
    color: #888;
    cursor: pointer;
    font-size: 1.2rem;
    padding: 0;
  }
  
  .toggle-password:hover {
    color: var(--white);
  }
  
  .error-message {
    background-color: rgba(220, 53, 69, 0.2);
    color: #ff6b6b;
    padding: 0.75rem;
    border-radius: 4px;
    font-size: 0.9rem;
    text-align: center;
    border: 1px solid rgba(220, 53, 69, 0.5);
  }
  
  .login-button {
    background-color: var(--red);
    color: white;
    padding: 0.8rem;
    border: none;
    border-radius: 4px;
    font-size: 1rem;
    font-weight: bold;
    cursor: pointer;
    transition: background-color 0.3s ease, transform 0.2s ease;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  .login-button:hover {
    background-color: var(--dark-red);
    transform: translateY(-2px);
  }
  
  .login-button:disabled {
    background-color: #555;
    cursor: not-allowed;
    transform: none;
  }
  
  .loading-spinner {
    display: inline-block;
    width: 1rem;
    height: 1rem;
    margin-right: 0.5rem;
    border: 2px solid rgba(255, 255, 255, 0.3);
    border-radius: 50%;
    border-top-color: white;
    animation: spin 1s linear infinite;
  }
  
  @keyframes spin {
    to {
      transform: rotate(360deg);
    }
  }
  
  .login-footer {
    margin-top: 1.5rem;
    text-align: center;
  }
  
  .login-footer a {
    color: var(--orange);
    text-decoration: none;
    font-size: 0.9rem;
  }
  
  .login-footer a:hover {
    text-decoration: underline;
  }
  
  @media (max-width: 480px) {
    .login-card {
      padding: 1.5rem;
    }
    
    h1 {
      font-size: 1.8rem;
    }
  }
</style>
