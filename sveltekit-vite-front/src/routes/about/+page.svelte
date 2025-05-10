<script>
  let fadeIn = $state(false);
  
  $effect(() => {
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
  
  <div class="features">
    <div class="feature">
      <h3>Books</h3>
      <p>Science fiction, fantasy, and more</p>
    </div>
    <div class="feature">
      <h3>Board Games</h3>
      <p>Strategy, party games, and classics</p>
    </div>
    <div class="feature">
      <h3>Comics</h3>
      <p>Manga, graphic novels, and comics</p>
    </div>
  </div>
  
  <div class="navigation">
    <a href="/" class="back-btn">Back to Home</a>
  </div>
</div>

<style lang="scss">
  @use 'sass:color';
  @use 'sass:math';

  $primary-color: #ff3e00;
  $secondary-color: #ffd900;
  $background-dark: #242424;
  $text-color: #ffffff;
  $spacing-base: 1rem;
  $transition-speed: 0.6s;
  
  @mixin card-style {
    background-color: rgba(color.adjust($background-dark, $lightness: -5%), 0.7);
    border-left: 4px solid $primary-color;
    border-radius: calc($spacing-base / 2);
    padding: $spacing-base * 1.5;
    margin-bottom: $spacing-base * 1.5;
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 10px 20px rgba(0, 0, 0, 0.3);
    }
  }

  .about-container {
    max-width: 900px;
    margin: 0 auto;
    padding: $spacing-base * 2;
    opacity: 0;
    transition: opacity $transition-speed ease-in-out;
    color: $text-color;
    
    &.fade-in {
      opacity: 1;
    }
  }
  
  h1 {
    font-size: 2.5rem;
    margin-bottom: $spacing-base;
    color: $primary-color;
    text-align: center;

    &::after {
      content: "";
      display: block;
      width: 50px;
      height: 4px;
      background-color: $primary-color;
      margin: $spacing-base auto;
    }
  }
  
  h2 {
    font-size: 1.5rem;
    margin-bottom: $spacing-base * 1.5;
    color: $secondary-color;
  }
  
  .description {
    margin-bottom: $spacing-base * 2;
    
    p {
      line-height: 1.6;
      margin-bottom: $spacing-base * 1.5;
    }
  }

  .features {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: $spacing-base * 2;
    margin: $spacing-base * 2 0;
    
    .feature {
      @include card-style;
      
      h3 {
        color: color.adjust($primary-color, $lightness: 10%);
        margin-bottom: $spacing-base;
        font-size: 1.3rem;
      }
    }
  }

  .navigation {
    margin-top: $spacing-base * 2;
    text-align: center;
  }
  
  .back-btn {
    display: inline-block;
    padding: $spacing-base * 0.75 $spacing-base * 1.5;
    background-color: $primary-color;
    color: white;
    border-radius: 4px;
    text-decoration: none;
    transition: background-color 0.3s;
    
    &:hover {
      background-color: color.adjust($primary-color, $lightness: -10%);
    }
  }

  @media (max-width: 768px) {
    .features {
      grid-template-columns: 1fr;
    }
    
    h1 {
      font-size: 2rem;
    }
  }
</style>
