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
          <span>Adulte +18</span>
        </a>
      </div>
    </div>
  </div>
  
  <div class="footer-info">
    <p>&copy; {currentYear} CARTEL - Tous droits réservés</p>
  </div>
</footer>

<script context="module">
  import PointBar from './PointBar.svelte';
</script>

<style>
  .medieval-footer {
    background-color: #111111;
    color: var(--white);
    position: relative;
    z-index: 10;
    padding-bottom: 1.5rem;
    padding-top: 2rem;
  }
  
  .footer-content {
    max-width: 1200px;
    margin: 0 auto;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 2rem;
    padding: 0 2rem;
  }
  
  .footer-logo {
    display: flex;
    align-items: center;
    gap: 1rem;
  }
  
  .footer-logo img:first-child {
    height: 6rem;
  }
  
  .footer-links h3 {
    font-family: "Pirata One", cursive;
    font-size: 1.8rem;
    margin-bottom: 1rem;
    color: var(--red);
  }
  
  .social-links {
    display: flex;
    gap: 1.5rem;
    flex-wrap: wrap;
  }
  
  .social-link {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    color: white;
    text-decoration: none;
    padding: 0.6rem 1.2rem;
    border: 2px solid transparent;
    border-radius: 5px;
    transition: all 0.3s ease;
    background-color: var(--bg-card);
  }
  
  .social-link:hover {
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
  
  .footer-info {
    text-align: center;
    margin-top: 2rem;
    padding-top: 1rem;
    border-top: 1px solid #333;
    max-width: 1200px;
    margin-left: auto;
    margin-right: auto;
  }
  
  @media (max-width: 768px) {

    .footer-content {
      gap: 1.5rem;
    }
    
    .footer-links h3 {
      font-size: 1.5rem;
    }
    
    .social-link {
      padding: 0.5rem 1rem;
    }
  }
</style>
