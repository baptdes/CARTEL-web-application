<script>
  import { onMount } from 'svelte';
  
  let fadeIn = false;
  
  onMount(() => {
    setTimeout(() => {
      fadeIn = true;
    }, 100);
  });
</script>

<div class="about-container" class:fade-in={fadeIn}>
  <h1>About C.A.R.T.E.L</h1>
  
  <div class="description">
    <h2>Catalogue Annuellement Ressucité et Téléconsultable d'Elements Ludiques</h2>
    <p>
      C.A.R.T.E.L is the student library and game library of N7 Engineering School. 
      Our mission is to provide students with access to a wide variety of books, 
      board games, comics, and more.
    </p>
  </div>
  
  <div class="navigation">
    <a href="/" class="back-btn">Back to Home</a>
  </div>
</div>

<style>
  .about-container {
    max-width: 900px;
    margin: 0 auto;
    padding: 2rem;
    opacity: 0;
    transition: opacity 0.6s ease-in-out;
  }
  
  .fade-in {
    opacity: 1;
  }
  
  h1 {
    font-size: 2.5rem;
    margin-bottom: 1rem;
    color: #ff3e00;
  }
  
  h2 {
    font-size: 1.5rem;
    margin-bottom: 1.5rem;
    color: #ffd900;
  }
  
  .description {
    margin-bottom: 2rem;
  }
  
  p {
    line-height: 1.6;
    margin-bottom: 1.5rem;
  }

  .navigation {
    margin-top: 2rem;
  }
  
  .back-btn {
    display: inline-block;
    padding: 0.75rem 1.5rem;
    background-color: #ff3e00;
    color: white;
    border-radius: 4px;
    text-decoration: none;
    transition: background-color 0.3s;
  }
  
  .back-btn:hover {
    background-color: #e63700;
  }
</style>
