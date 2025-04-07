<svelte:head>
  <title>C.A.R.T.E.L</title>
  <meta name="description" content="Catalogue Annuellement Ressucité et Téléconsultable d'Elements Ludiques" />
</svelte:head>

<main>
  <!-- Hero Section -->
  <section class="hero">
    <div class="hero-content">
        <h1>Bienvenue dans<br>la <span style="color: #d77d42;">C</span>averne <span style="color: #d77d42;">A</span>ux <span style="color: #d77d42;">R</span>ichesses <span style="color: #d77d42;">T</span>éléconsultables <span style="color: #d77d42;">E</span>t <span style="color: #d77d42;">L</span>égendaires</h1>
        <p>Explorez notre collection et trouvez votre prochaine aventure !</p>
    </div>
  </section>
  
  <section class="content">
    <h2>About Us</h2>
    <p>C.A.R.T.E.L is dedicated to providing a comprehensive catalog of playful elements, ensuring that you have access to the best resources available.</p>
    <ul>
      <li>Explore our extensive collection of games and activities.</li>
      <li>Stay updated with our annual releases.</li>
      <li>Join our community of enthusiasts and share your experiences.</li>
    </ul>
  </section>
</main>

<style>
  .hero {
    background-image: url('/dragon_library.png');
    background-size: cover;
    background-position: center;
    height: 100vh; /* Full page height */
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    padding: 1em;
  }
  
  .hero-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    text-align: center;
    width: 100%;
    max-width: 80vw;
  }
  
  .hero-content h1 {
    font-family: "Pirata One", system-ui;
    font-size: 6rem;
    margin-bottom: 1rem;
    text-shadow: 4px 4px 4px rgba(0, 0, 0, 0.8);
    color: #fff;
    line-height: 1.2;
  }
  
  .hero-content p {
    font-size: 2.5rem;
    font-weight: bold;
    padding: 1rem;
    max-width: 80%;
    margin-top: 1rem;
  }
  
  @media (max-width: 768px) {
    .hero-content h1 {
      font-size: 3.5rem;
    }
    
    .hero-content p {
      max-width: 90%;
      font-size: 2rem;
    }
  }
  
  .content {
    padding: 2rem;
  }
  
  ul {
    list-style-type: none;
    padding: 0;
  }
  
  li {
    margin: 0.5em 0;
  }
</style>