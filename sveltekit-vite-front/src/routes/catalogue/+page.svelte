<script>
    let images = [
      { id: 1, src: 'hagitest.jpeg', alt: 'GetHagimoned 1' },
      { id: 2, src: 'hagitest.jpeg', alt: 'GetHagimoned 2' },
      { id: 3, src: 'hagitest.jpeg', alt: 'GetHagimoned 3' },
      { id: 4, src: 'hagitest.jpeg', alt: 'GetHagimoned 4' },
      { id: 5, src: 'hagitest.jpeg', alt: 'GetHagimoned 5' },
      { id: 6, src: 'hagitest.jpeg', alt: 'GetHagimoned 6' },
      { id: 7, src: 'hagitest.jpeg', alt: 'GetHagimoned 7' },
      { id: 8, src: 'hagitest.jpeg', alt: 'GetHagimoned 8' },
      { id: 9, src: 'hagitest.jpeg', alt: 'GetHagimoned 9' },
      { id: 10, src: 'hagitest.jpeg', alt: 'GetHagimoned 10' },
      { id: 11, src: 'hagitest.jpeg', alt: 'GetHagimoned 11' },
      { id: 12, src: 'hagitest.jpeg', alt: 'GetHagimoned 12' },
      { id: 13, src: 'hagitest.jpeg', alt: 'GetHagimoned 13' },
      { id: 14, src: 'hagitest.jpeg', alt: 'GetHagimoned 14' },
      { id: 15, src: 'hagitest.jpeg', alt: 'GetHagimoned 15' },
      { id: 16, src: 'hagitest.jpeg', alt: 'GetHagimoned 16' },
      { id: 17, src: 'hagitest.jpeg', alt: 'GetHagimoned 17' },
      { id: 18, src: 'hagitest.jpeg', alt: 'GetHagimoned 18' },
      { id: 19, src: 'hagitest.jpeg', alt: 'GetHagimoned 19' },
      { id: 20, src: 'hagitest.jpeg', alt: 'GetHagimoned 20' },
      { id: 21, src: 'hagitest.jpeg', alt: 'GetHagimoned 21' },
      { id: 22, src: 'hagitest.jpeg', alt: 'GetHagimoned 22' },
      { id: 23, src: 'hagitest.jpeg', alt: 'GetHagimoned 23' },
      { id: 24, src: 'hagitest.jpeg', alt: 'GetHagimoned 24' },
      { id: 25, src: 'hagitest.jpeg', alt: 'GetHagimoned 25' },
      { id: 26, src: 'hagitest.jpeg', alt: 'GetHagimoned 26' },
      { id: 27, src: 'hagitest.jpeg', alt: 'GetHagimoned 27' },
      { id: 28, src: 'hagitest.jpeg', alt: 'GetHagimoned 28' },
      { id: 29, src: 'hagitest.jpeg', alt: 'GetHagimoned 29' },
      { id: 30, src: 'hagitest.jpeg', alt: 'GetHagimoned 30' },
      { id: 31, src: 'hagitest.jpeg', alt: 'GetHagimoned 31' },
      { id: 32, src: 'hagitest.jpeg', alt: 'GetHagimoned 32' },
      { id: 33, src: 'hagitest.jpeg', alt: 'GetHagimoned 33' },
      { id: 34, src: 'hagitest.jpeg', alt: 'GetHagimoned 34' },
      { id: 35, src: 'hagitest.jpeg', alt: 'GetHagimoned 35' },
      { id: 36, src: 'hagitest.jpeg', alt: 'GetHagimoned 36' },
      { id: 37, src: 'hagitest.jpeg', alt: 'GetHagimoned 37' },
      { id: 38, src: 'hagitest.jpeg', alt: 'GetHagimoned 38' },
      { id: 39, src: 'hagitest.jpeg', alt: 'GetHagimoned 39' },
      { id: 40, src: 'hagitest.jpeg', alt: 'GetHagimoned 40' },
      { id: 41, src: 'hagitest.jpeg', alt: 'GetHagimoned 41' },
      { id: 42, src: 'hagitest.jpeg', alt: 'GetHagimoned 42' },
      { id: 43, src: 'hagitest.jpeg', alt: 'GetHagimoned 43' },
      { id: 44, src: 'hagitest.jpeg', alt: 'GetHagimoned 44' },
      { id: 45, src: 'hagitest.jpeg', alt: 'GetHagimoned 45' },
      { id: 46, src: 'hagitest.jpeg', alt: 'GetHagimoned 46' },
      { id: 47, src: 'hagitest.jpeg', alt: 'GetHagimoned 47' }
    ];
  
    function handleClick(image) {
      alert(`${image.alt}`);
    }
  </script>
  
  <div class="image-grid">
    {#each images as image}
      <img
        src={image.src}
        alt={image.alt}
        on:click={() => handleClick(image)}
      />
    {/each}
  </div>
  
  <style>
    .image-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
      gap: 1rem;
      padding: 1rem;
    }
  
    .image-grid img {
      width: 100%;
      height: auto;
      cursor: pointer;
      border-radius: 8px;
      transition: transform 0.2s;
    }
  
    .image-grid img:hover {
      transform: scale(1.05);
    }
  </style>