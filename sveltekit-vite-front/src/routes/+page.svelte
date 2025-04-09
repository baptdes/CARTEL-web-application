<script>
  import CardArticle from '$lib/components/CardArticle.svelte';
  import PurgeCountdown from '$lib/components/PurgeCountdown.svelte';
  
  // Sample recommendation data
  const recommendations = [
    {
      title: "Gloomhaven",
      imageSrc: "/hagitest.jpeg", 
      rating: 5,
      description: "Une aventure épique de donjon-crawler, avec des mécaniques innovantes et une campagne immersive!"
    },
    {
      title: "Les Colons de Catane",
      imageSrc: "/hagitest.jpeg",
      rating: 4,
      description: "Un classique intemporel de stratégie et de négociation qui captive depuis des années."
    },
    {
      title: "Pandemic Legacy",
      imageSrc: "/hagitest.jpeg",
      rating: 5,
      description: "Une expérience coopérative évolutive où vos décisions changent le jeu à jamais!"
    }
  ];
  
  const purgeDate = new Date(2025, 5, 30, 18, 0, 0);
  
  function handleCardClick(title) {
    console.log(`Clicked on ${title}`);
    // You can add navigation or modal opening logic here
  }
</script>

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
  
  <!-- Monthly Recommendations Section -->
  <section class="monthly-recommendations">
    <div class="frame-container">
      <h2>Recommandations du Mois</h2>
      <div class="recommendations-grid">
        {#each recommendations as item}
          <CardArticle 
            title={item.title}
            imageSrc={item.imageSrc}
            rating={item.rating}
            description={item.description}
            onClick={() => handleCardClick(item.title)}
          />
        {/each}
      </div>
    </div>
  </section>

  <!-- Purge Countdown Section -->
  <section class="purge-countdown">
    <div class="countdown-container">
      <h2>La Prochaine Purge Approche</h2>
      <p class="subtitle">Attention, la grande purge des suggestions d'achats approche ! Les dragons trieront vos suggestions avec plus de flammes que de tact... 🐉🔥</p>
      <PurgeCountdown targetDate={purgeDate} />
    </div>
  </section>
</main>

<style>
  /* Hero section */
  .hero {
    background-image: url('/dragon_library.png');
    background-size: cover;
    background-position: center;
    height: 102vh; /* Full page height */
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
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
  
  /* Monthly Recommendations Section */
  .monthly-recommendations {
    padding: 3rem 2rem;
    background-image: url('ancient_paper_bg.webp');
    background-repeat: no-repeat;
    background-size: 101% 102%;
    background-position: 0.0;
    margin-top: -2rem;
    margin-bottom: -2rem;
  }

  .frame-container {
    margin: 0 auto;
    padding: 2.5rem;
    position: relative;
    max-width: 80vw;
  }
  
  .monthly-recommendations h2 {
    text-align: center;
    font-family: "Pirata One", system-ui;
    font-size: 3rem;
    color: #8b4513;
    margin-bottom: 2rem;
  }
  
  .recommendations-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 2rem;
  }

  /* Purge Countdown Section */
  .purge-countdown {
    background-image: url('/red_wallpaper.webp');
    background-repeat: no-repeat;
    background-size: cover;
    background-position: center;
    padding: 6rem 2rem;
    color: #fff;
    text-align: center;
    position: relative;
    z-index: -2;
  }
  
  .countdown-container {
    position: relative;
    z-index: 2;
    max-width: 900px;
    margin: 0 auto;
    padding: 2rem;
    background: rgba(0, 0, 0, 0.7);
    border: 3px solid var(--dark-red);
    box-shadow: 0 0 15px rgba(0, 0, 0, 0.7);
  }
  
  .purge-countdown h2 {
    font-family: "Pirata One", cursive;
    font-size: 3.5rem;
    margin-bottom: 1rem;
    color: var(--red);
    text-shadow: 2px 2px 5px #000;
  }
  
  .subtitle {
    font-size: 1.2rem;
    margin-bottom: 2rem;
    font-style: italic;
  }
  
  @media (max-width: 768px) {
    .recommendations-grid {
      grid-template-columns: 1fr;
    }
    
    .frame-container {
      padding: 1.5rem;
    }
    
    .monthly-recommendations h2 {
      font-size: 2.5rem;
    }
    
    .purge-countdown h2 {
      font-size: 2.5rem;
    }
  }
</style>