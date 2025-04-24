<script>
  let { 
    title = '',
    imageSrc = 'hagitest.jpeg',
    rating = null,
    description = "La description s'est perdue dans les méandres du temps.",
    altText = title,
    onClick = () => {}, // Default empty function
    iconType = '/icons/books.svg', // Default icon for articles
    frameColor = 'var(--dark-orange)' // Default frame color
  } = $props();

  function getStars(rating) {
    const fullStars = Math.floor(rating);
    const halfStar = rating % 1 >= 0.5;
    const emptyStars = 5 - fullStars - (halfStar ? 1 : 0);
    
    return {
      full: Array(fullStars).fill('★'),
      half: halfStar ? ['★'] : [],
      empty: Array(emptyStars).fill('☆')
    };
  }

  const stars = $derived(getStars(rating));
</script>

<button class="card-medieval" onclick={onClick} type="button" aria-label="Card">
  <div class="card-frame" style="--frame-color: {frameColor};">
    <div class="card-inner">
      <div class="card-image-container">
        <img src={imageSrc} alt={altText} class="card-image" />
      </div>
      
      <div class="card-content">
        <h3 class="card-title">{title}</h3>
        
        {#if rating !== null}
          <div class="rating">
            {#each stars.full as _}
              <span class="star full">★</span>
            {/each}
            {#each stars.half as _}
              <span class="star half">★</span>
            {/each}
            {#each stars.empty as _}
              <span class="star empty">☆</span>
            {/each}
          </div>
        {/if}
        
        <p class="card-description">{description}</p>
      </div>
      
      <!-- Decorative elements with icons -->
      <div class="corner top-left"></div>
      <div class="corner top-right"></div>
      <img src={iconType} alt="Item type" class="icon-corner icon-top-right" />
      <div class="corner bottom-left"></div>
      <img src={iconType} alt="Item type" class="icon-corner icon-bottom-left"/>
      <div class="corner bottom-right"></div>
    </div>
  </div>
</button>

<style>
  /* Reset button styles */
  .card-medieval {
    all: unset;
    display: block;
    width: 100%;
    perspective: 1000px;
    margin-bottom: 2rem;
    cursor: pointer;
    transition: transform 0.3s ease;
  }
  
  .card-medieval:hover {
    transform: translateY(-8px) scale(1.02);
  }
  
  /* Frame styling */
  .card-frame {
    width: 100%;
    height: 100%;
    border-radius: 8px;
    padding: 8px;
    background-color: var(--frame-color);
    box-shadow: 
      0 5px 15px rgba(0, 0, 0, 0.35),
      0 0 0 1px rgba(139, 69, 19, 0.5),
      inset 0 0 8px 2px rgba(255, 255, 255, 0.15);
    position: relative;
  }

  /* Inner card content */
  .card-inner {
    width: 100%;
    height: 100%;
    background: #f9e8c9; /* Parchment-like color */
    border-radius: 6px;
    display: flex;
    flex-direction: column;
    position: relative;
    box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.2);
  }

  /* Image container */
  .card-image-container {
    width: 100%;
    height: 200px;
    overflow: hidden;
    border-bottom: 4px solid var(--frame-color);
    position: relative;
  }

  /* Image styling */
  .card-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.5s ease;
  }

  .card-medieval:hover .card-image {
    transform: scale(1.1);
  }

  /* Text content */
  .card-content {
    padding: 1.2rem;
    flex: 1;
    position: relative;
    z-index: 2;
  }

  /* Title styling */
  .card-title {
    font-family: 'Pirata One', serif;
    color: #4a230c;
    font-size: 1.5rem;
    margin: 0 0 0.5rem;
    text-align: center;
    text-shadow: 1px 1px 1px rgba(255, 255, 255, 0.5);
  }

  /* Rating stars */
  .rating {
    display: flex;
    justify-content: center;
    margin-bottom: 0.8rem;
  }

  .star {
    font-size: 1.2rem;
    margin: 0 2px;
  }

  .star.full, .star.half {
    color: #d77d42;
  }

  .star.empty {
    color: #cca483;
  }

  /* Description text */
  .card-description {
    font-size: 0.95rem;
    color: #4d320e;
    line-height: 1.4;
    text-align: center;
  }

  /* Decorative corners */
  .corner {
    position: absolute;
    width: 35px;
    height: 35px;
    z-index: 3;
  }

  /* Corner icon styling */
  .icon-corner {
    width: 24px;
    height: 24px;
    position: absolute;
    z-index: 4;
    filter: invert(100%);
  }

  .icon-top-right {
    top: 0;
    right: 0;
  }

  .icon-bottom-left {
    bottom: 0;
    left: 0;
  }

  .top-left {
    top: 0;
    left: 0;
    background-color: var(--frame-color);
    border-bottom-right-radius: 100%;
  }

  .top-right {
    top: 0;
    right: 0;
    border-bottom-left-radius: 100%;
    background-color: var(--frame-color);
  }

  .bottom-left {
    bottom: 0;
    left: 0;
    border-top-right-radius: 100%;
    background-color: var(--frame-color);
  }

  .bottom-right {
    bottom: 0;
    right: 0;
    border-top-left-radius: 100%;
    background-color: var(--frame-color);
  }

  /* Responsive adjustments */
  @media (max-width: 768px) {
    .card-title {
      font-size: 1.3rem;
    }
    
    .card-description {
      font-size: 0.9rem;
    }
    
    .card-image-container {
      height: 180px;
    }
  }
</style>
