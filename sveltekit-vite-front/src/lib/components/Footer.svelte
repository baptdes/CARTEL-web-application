<script>
  const currentYear = new Date().getFullYear();
</script>

<footer class="medieval-footer">
  <div class="footer-content">
    <div class="footer-logo">
      <img src="/logo/cartel.png" alt="CARTEL Logo" />
    </div>
    
    <div class="footer-links">
      <h3>Rejoignez-nous</h3>
      <div class="social-links">
        <a href="https://discord.gg/zcz2mfH4JH" target="_blank" rel="noopener noreferrer" class="social-link">
          <img src="/logo/discord.svg" alt="Discord Logo" class="icon" />
          <span>Discord</span>
        </a>
        <a href="https://www.instagram.com/lecarteln7/" target="_blank" rel="noopener noreferrer" class="social-link">
            <img src="/logo/instagram.svg" alt="Instagram Logo" class="icon" />
          <span>Instagram</span>
        </a>
        <a href="https://jejeu.org/jeu.php?id=29" target="_blank" rel="noopener noreferrer" class="social-link">
            <img src="/icons/adult-18.svg" alt="Adulte +18 Logo" class="icon" />
          <span>OnlyFan</span>
        </a>
      </div>
    </div>
  </div>
  
  <div class="footer-info">
    <p>&copy; {currentYear} CARTEL - Tous droits réservés</p>
    <a href={'/admin'} class="admin-link">Espace administrateur</a>
  </div>
</footer>

<script context="module">
  import PointBar from './PointBar.svelte';
</script>

<style lang="scss">
  $spacing-sm: 0.5rem;
  $spacing-md: 1rem;
  $spacing-lg: 1.5rem;
  $spacing-xl: 2rem;
  $max-width: 1200px;
  $border-radius: 5px;
  $transition-speed: 0.3s;
  
  .medieval-footer {
    background-color: #111111;
    color: var(--white);
    position: relative;
    z-index: 10;
    padding-bottom: $spacing-lg;
    padding-top: $spacing-xl;
  }
  
  .footer-content {
    max-width: $max-width;
    margin: 0 auto;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: $spacing-xl;
    padding: 0 $spacing-xl;
  }
  
  .footer-logo {
    display: flex;
    align-items: center;
    gap: $spacing-md;
    
    img:first-child {
      height: 6rem;
    }
  }
  
  .footer-links {
    h3 {
      font-family: "Pirata One", cursive;
      font-size: 1.8rem;
      margin-bottom: $spacing-md;
      color: var(--red);
    }
  }
  
  .social-links {
    display: flex;
    gap: $spacing-lg;
    flex-wrap: wrap;
    
    .social-link {
      display: flex;
      align-items: center;
      gap: $spacing-sm;
      color: white;
      text-decoration: none;
      padding: 0.6rem 1.2rem;
      border: 2px solid transparent;
      border-radius: $border-radius;
      transition: all $transition-speed ease;
      background-color: var(--bg-card);
      
      &:hover {
        background-color: var(--red);
        transform: translateY(-3px) scale(1.05);
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
      }
      
      .icon {
        height: 20px;
        width: 20px;
        fill: currentColor;
        filter: invert(100%);
      }
    }
  }
  
  .footer-info {
    text-align: center;
    margin-top: $spacing-xl;
    padding-top: $spacing-md;
    border-top: 1px solid #333;
    max-width: $max-width;
    margin-left: auto;
    margin-right: auto;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: $spacing-sm;
    
    .admin-link {
      color: #666;
      text-decoration: none;
      font-size: 0.8rem;
      transition: color $transition-speed;
      padding: 0.25rem $spacing-sm;
      
      &:hover {
        color: var(--orange);
        text-decoration: underline;
      }
    }
  }
  
  @media (max-width: 768px) {
    .footer-content {
      gap: $spacing-lg;
    }
    
    .footer-links h3 {
      font-size: 1.5rem;
    }
    
    .social-link {
      padding: $spacing-sm $spacing-md;
    }
  }
</style>
