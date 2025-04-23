<script>
    import CardArticle from '$lib/components/CardArticle.svelte';
    
    // This will eventually be replaced with data from your API
    let catalogItems = $state([
      { id: 1, title: 'GetHagimoned 1', imageSrc: 'hagitest.jpeg', rating: 4.5, description: 'Description du jeu 1' },
      { id: 2, title: 'GetHagimoned 2', imageSrc: 'hagitest.jpeg', rating: 3, description: 'Description du jeu 2' },
      { id: 3, title: 'GetHagimoned 3', imageSrc: 'hagitest.jpeg', rating: 5, description: 'Description du jeu 3' },
      { id: 4, title: 'GetHagimoned 4', imageSrc: 'hagitest.jpeg', rating: 4, description: 'Description du jeu 4' },
      { id: 5, title: 'GetHagimoned 5', imageSrc: 'hagitest.jpeg', rating: 3.5, description: 'Description du jeu 5' },
      { id: 6, title: 'GetHagimoned 6', imageSrc: 'hagitest.jpeg', rating: 4, description: 'Description du jeu 6' },
      // You can add more items here or load them from an API
    ]);
  
    function handleItemClick(item) {
      alert(`Vous avez sélectionné ${item.title}`);
      // You could navigate to a detail page or show a modal here
    }
</script>

<div class="catalogue-container">
  <h1>Catalogue des Jeux</h1>
  
  <div class="search-filter">
    <!-- Search/Filter controls can be added here -->
  </div>
  
  <div class="catalogue-grid">
    {#each catalogItems as item (item.id)}
      <CardArticle
        title={item.title}
        imageSrc={item.imageSrc}
        rating={item.rating}
        description={item.description}
        onclick={() => handleItemClick(item)}
      />
    {/each}
  </div>
</div>

<style>
  .catalogue-container {
    max-width: 1200px;
    margin: 2rem auto;
    padding: 0 1rem;
  }
  
  h1 {
    font-family: "Pirata One", system-ui;
    font-size: 3rem;
    color: var(--red);
    text-align: center;
    margin-bottom: 2rem;
  }
  
  .search-filter {
    margin-bottom: 2rem;
  }
  
  .catalogue-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 2rem;
  }
  
  @media (max-width: 768px) {
    h1 {
      font-size: 2.5rem;
    }
    
    .catalogue-grid {
      grid-template-columns: 1fr;
    }
  }
</style>