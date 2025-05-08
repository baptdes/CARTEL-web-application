<script>
  import PointBar from '$lib/components/PointBar.svelte';
  
  // Books
  const exampleItems = [
    {
      id: 1,
      title: "Le Seigneur des Anneaux",
      author: "J.R.R. Tolkien",
      description: "Une épopée fantastique dans un monde imaginaire où différentes races s'allient pour détruire un anneau maléfique.",
      coverImage: "https://m.media-amazon.com/images/I/71jLBXtWJWL._AC_UF1000,1000_QL80_.jpg",
      publicationYear: 1954,
      category: "Fantasy",
      available: true,
      type: "book",
      publisher: "Allen & Unwin",
      pages: 1178
    },
    {
      id: 2,
      title: "Fondation",
      author: "Isaac Asimov",
      description: "Dans un futur lointain, un mathématicien développe une science permettant de prédire l'avenir de l'humanité et d'en altérer le cours.",
      coverImage: "https://m.media-amazon.com/images/I/81wW3qopnLL._AC_UF1000,1000_QL80_.jpg",
      publicationYear: 1951,
      category: "Science Fiction",
      available: true,
      type: "book",
      publisher: "Gnome Press",
      pages: 255
    }
  ];
  
  const categories = ["Toutes", "Fantasy", "Science Fiction", "Roman Historique", "Policier", "Biographie", "Manga", "Bande Dessinée"];
</script>

<svelte:head>
  <title>Livres | C.A.R.T.E.L</title>
</svelte:head>

<div class="container">
  <div class="search">
    <div>
      <input 
        type="text" 
        placeholder="Rechercher un livre par titre, auteur ou genre..."
      />
      <button>
        🔍
      </button>
    </div>
  </div>

  <div class="main">
    <!-- Left sidebar with filters -->
    <aside>
      <h2>Filtres</h2>
      
      <div class="section">
        <h3>Genre</h3>
        <div>
          <select>
            {#each categories as category}
              <option>{category}</option>
            {/each}
          </select>
        </div>
      </div>
      
      <div class="section">
        <h3>Période de publication</h3>
        <div>
          <label>
            <input type="radio" name="period" checked />
            <span>Toutes</span>
          </label>
          <label>
            <input type="radio" name="period" />
            <span>Avant 1950</span>
          </label>
          <label>
            <input type="radio" name="period" />
            <span>1950-1999</span>
          </label>
          <label>
            <input type="radio" name="period" />
            <span>2000-présent</span>
          </label>
        </div>
      </div>
      
      <div class="section">
        <h3>Disponibilité</h3>
        <div>
          <label>
            <input type="radio" name="availability" checked />
            <span>Tous</span>
          </label>
          <label>
            <input type="radio" name="availability" />
            <span>Disponible</span>
          </label>
          <label>
            <input type="radio" name="availability" />
            <span>Non disponible</span>
          </label>
        </div>
      </div>
      
      <button class="reset">
        Réinitialiser les filtres
      </button>
    </aside>
    
    <!-- Right side content area -->
    <div class="content">
      <!-- Sorting options bar -->
      <div class="sort">
        <div>
          <span>Trier par:</span>
          <select>
            <option>Titre</option>
            <option>Auteur</option>
            <option>Année de publication</option>
            <option>Nombre de pages</option>
          </select>
          <button aria-label="Toggle sort direction">
            ↑
          </button>
        </div>
        <div>
          {exampleItems.length} résultats
        </div>
      </div>
      
      <!-- Results display - cards -->
      <div class="results">
        
        {#each exampleItems as item}
          <div class="card">
            <div class="img">
              <img 
                src={item.coverImage}
                alt={`Couverture de ${item.title}`}
                loading="lazy"
              />
              <div class="badge">
                <img src="/icons/books.svg" alt="Livre" />
              </div>
            </div>
            
            <div class="info">
              <div class="header">
                <h3>{item.title}</h3>
                <div class:available={item.available}>
                  {item.available ? 'Disponible' : 'Indisponible'}
                </div>
              </div>
              
              <div class="meta">
                <strong>Auteur :</strong> {item.author}
              </div>
              
              <div class="meta">
                <strong>Année :</strong> {item.publicationYear}
              </div>
              
              <div class="meta">
                <strong>Genre :</strong> {item.category}
              </div>
              
              <div class="meta">
                <strong>Éditeur :</strong> {item.publisher}
              </div>
              
              <p>{item.description.substring(0, 150)}{item.description.length > 150 ? '...' : ''}</p>
            </div>
          </div>
        {/each}
      </div>
    </div>
  </div>
</div>

<style lang="scss">
  .container {
    display: flex;
    flex-direction: column;
    min-height: calc(100vh - 4rem);
    position: relative;
    padding-bottom: 2rem;
    background-image: url('/textures/dark-brown-old-stone-wall.jpg');
    background-size: cover;
    background-attachment: fixed;
    background-position: center;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background-color: rgba(0, 0, 0, 0.7);
      z-index: 0;
    }
    
    .search {
      padding: 1rem;
      z-index: 100;
      position: relative;
      margin-bottom: 1.5rem;
      border-radius: 0 0 8px 8px;
      
      div {
        max-width: 800px;
        margin: 0 auto;
        display: flex;
        gap: 0.5rem;
        
        input {
          flex: 1;
          padding: 0.8rem 1rem;
          border: 2px solid var(--dark-red);
          background-color: #222;
          color: white;
          border-radius: 4px;
          font-size: 1rem;
          
          &:focus {
            outline: none;
            border-color: var(--red);
          }
        }
        
        button {
          padding: 0 1.2rem;
          background-color: var(--red);
          color: white;
          border: none;
          border-radius: 4px;
          cursor: pointer;
          transition: background-color 0.2s;
          font-size: 1.2rem;
          
          &:hover {
            background-color: var(--dark-red);
          }
        }
      }
    }
  }
  
  .main {
    display: flex;
    flex: 1;
    padding: 0 1rem;
    max-width: 1400px;
    margin: 0 auto;
    width: 100%;
    position: relative;
    z-index: 1;
    
    aside {
      width: 250px;
      flex-shrink: 0;
      padding: 1.5rem;
      background-color: var(--bg-card);
      border-radius: 8px;
      margin-right: 1.5rem;
      height: fit-content;
      
      h2 {
        font-family: "Pirata One", cursive;
        color: var(--orange);
        margin-top: 0;
        margin-bottom: 1.5rem;
        font-size: 1.8rem;
      }
      
      h3 {
        color: var(--white);
        margin: 0 0 0.8rem 0;
        font-size: 1.2rem;
      }
      
      .section {
        margin-bottom: 1.5rem;
        padding-bottom: 1.5rem;
        border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        
        &:last-child {
          border-bottom: none;
        }
        
        div {
          display: flex;
          flex-direction: column;
          gap: 0.5rem;
          
          label {
            display: flex;
            align-items: center;
            cursor: pointer;
            
            input {
              margin-right: 0.5rem;
            }
            
            span {
              color: var(--white);
            }
          }
          
          select {
            width: 100%;
            padding: 0.5rem;
            background-color: #333;
            color: white;
            border: 1px solid #555;
            border-radius: 4px;
            
            &:focus {
              outline: none;
              border-color: var(--orange);
            }
          }
        }
      }
      
      .reset {
        width: 100%;
        padding: 0.6rem;
        background-color: transparent;
        color: var(--orange);
        border: 1px solid var(--orange);
        border-radius: 4px;
        cursor: pointer;
        transition: background-color 0.2s;
        
        &:hover {
          background-color: rgba(199, 112, 49, 0.2);
        }
      }
    }
  }
  
  .content {
    flex: 1;
    display: flex;
    flex-direction: column;
    
    .sort {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 1rem;
      background-color: var(--bg-card);
      border-radius: 8px;
      margin-bottom: 1.5rem;
      
      div:first-child {
        display: flex;
        align-items: center;
        gap: 1rem;
        
        span {
          color: var(--white);
        }
        
        select {
          padding: 0.5rem;
          background-color: #333;
          color: white;
          border: 1px solid #555;
          border-radius: 4px;
        }
        
        button {
          background-color: var(--orange);
          color: white;
          border: none;
          border-radius: 4px;
          width: 30px;
          height: 30px;
          display: flex;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          transition: background-color 0.2s;
          
          &:hover {
            background-color: var(--dark-orange);
          }
        }
      }
      
      div:last-child {
        color: var(--white);
        font-size: 0.9rem;
      }
    }
    
    .results {
      display: flex;
      flex-direction: column;
      gap: 1.5rem;
      padding-bottom: 1.5rem;
      
      .card {
        display: flex;
        background-color: #2a2a2a;
        border-radius: 8px;
        overflow: hidden;
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
        transition: transform 0.2s, box-shadow 0.2s;
        cursor: pointer;
        width: 100%;
        text-align: left;
        font-family: inherit;
        padding: 0;
        border: none;
        margin: 0;
        
        &:hover {
          transform: translateY(-4px);
          box-shadow: 0 8px 16px rgba(0, 0, 0, 0.3);
        }
        
        &:focus {
          outline: 2px solid var(--orange);
          transform: translateY(-4px);
        }
        
        .img {
          width: 180px;
          flex-shrink: 0;
          position: relative;
          
          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
          
          .badge {
            position: absolute;
            top: 10px;
            left: 10px;
            background-color: rgba(0, 0, 0, 0.7);
            border-radius: 50%;
            width: 30px;
            height: 30px;
            display: flex;
            align-items: center;
            justify-content: center;
            
            img {
              width: 18px;
              height: 18px;
              filter: invert(1);
            }
          }
        }
        
        .info {
          padding: 1.5rem;
          flex: 1;
          display: flex;
          flex-direction: column;
          
          .header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            margin-bottom: 0.8rem;
            
            h3 {
              font-family: "Pirata One", cursive;
              color: var(--red);
              font-size: 1.5rem;
              margin: 0;
            }
            
            div {
              padding: 0.3rem 0.6rem;
              border-radius: 4px;
              font-size: 0.8rem;
              font-weight: bold;
              background-color: #f44336;
              color: white;
              
              &.available {
                background-color: #4caf50;
              }
            }
          }
          
          .meta {
            margin-bottom: 0.5rem;
            color: #ddd;
            
            strong {
              color: var(--orange);
            }
          }
          
          p {
            margin: 0.8rem 0;
            line-height: 1.4;
            color: #bbb;
            flex: 1;
          }
        }
      }
    }
  }
  
  // Responsive styles
  @media (max-width: 900px) {
    .main {
      flex-direction: column;
      
      aside {
        width: 100%;
        margin-right: 0;
        margin-bottom: 1.5rem;
      }
      
      .content .results .card .img {
        width: 120px;
      }
    }
  }
  
  @media (max-width: 600px) {
    .content {
      .results .card {
        flex-direction: column;
        
        .img {
          width: 100%;
          height: 200px;
        }
      }
      
      .sort {
        flex-direction: column;
        gap: 1rem;
        
        div:first-child {
          width: 100%;
          justify-content: space-between;
        }
      }
    }
  }
</style>
