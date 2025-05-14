<script>
  import { formatAuthor, formatPublisher, formatGenre, isBookAvailable } from '$lib/services/bookService';

  let { book } = $props();
  
  function handleClick() {
    // TODO : Create a page for book details
    console.log('Book clicked');
  }

  // Default frame color based on availability
  const frameColor = isBookAvailable(book) ? 'var(--dark-orange)' : '#6e2e2e';
</script>

<button 
  class="card-medieval" 
  onclick={handleClick}
  type="button"
>
  <div class="card-frame" style="--frame-color: {frameColor};">
    <div class="card-inner">
      <div class="card-content">
        <div class="card-layout">
          <div class="img">
            <img 
              src={book.coverImage || "/placeholder_book.png"}
              alt={`Couverture de ${book.name || 'Livre sans titre'}`}
              loading="lazy"
            />
          </div>
          
          <div class="info">
            <div class="header">
              <h3>{book.name || `Un troll 🧌 a mangé le titre de ce livre`}</h3>
              <div class:available={isBookAvailable(book)}>
                {isBookAvailable(book) ? 'Disponible' : 'Indisponible'}
              </div>
            </div>
            
            <div class="meta">
              <strong>{book.authors && Array.isArray(book.authors) && book.authors.length > 1 ? 'Auteurs' : 'Auteur'} :</strong> {formatAuthor(book) || 'Non spécifié'}
            </div>
            
            <div class="meta">
              <strong>Année :</strong> {book.publicationYear || 'Non spécifié'}
            </div>
            
            <div class="meta">
              <strong>Format :</strong> {book.format || 'Non spécifié'}
            </div>
            
            <div class="meta">
              <strong>Genre :</strong> {formatGenre(book)}
            </div>
            
            <div class="meta">
              <strong>Éditeur :</strong> {formatPublisher(book)}
            </div>
            
            <p>{book.description ? 
                (book.description.substring(0, 150) + (book.description.length > 150 ? '...' : '')) : 
                'Aucune description disponible.'}</p>
          </div>
        </div>
      </div>

      <!-- Decorative corners only (no icons) -->
      <div class="corner top-left"></div>
      <div class="corner top-right"></div>
      <div class="corner bottom-left"></div>
      <div class="corner bottom-right"></div>
    </div>
  </div>
</button>

<style lang="scss">
  .card-medieval {
    all: unset;
    display: block;
    width: 100%;
    perspective: 1000px;
    margin-bottom: 2rem;
    cursor: pointer;
    border: none;
    transition: transform 0.3s ease;
    
    &:hover {
      transform: translateY(-8px) scale(1.02);
    }

    &:focus {
      outline: 2px solid var(--orange);
    }
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
    display: flex;
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

  /* Card layout for the book structure */
  .card-layout {
    display: flex;
    padding: 1.5rem 1rem;
    
    .img {
      width: 180px;
      flex-shrink: 0;
      position: relative;
      margin-right: 1.5rem;
      
      img {
        width: 100%;
        object-fit: cover;
        border: 3px solid var(--frame-color);
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
        transition: transform 0.5s ease;
      }
    }
  }
  
  .card-medieval:hover .img img {
    transform: scale(1.05);
  }
  
  .info {
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
        color: #4a230c;
        font-size: 1.5rem;
        margin: 0;
        text-shadow: 1px 1px 1px rgba(255, 255, 255, 0.5);
      }
      
      div {
        padding: 0.3rem 0.6rem;
        border-radius: 4px;
        font-size: 0.8rem;
        font-weight: bold;
        background-color: #8B2E2E;
        color: white;
        border: 1px solid #6e2e2e;
        box-shadow: inset 0 0 4px rgba(255, 255, 255, 0.3);
        
        &.available {
          background-color: #5B8B2E;
          border: 1px solid #4a7a1d;
        }
      }
    }
    
    .meta {
      margin-bottom: 0.5rem;
      color: #4a230c;
      
      strong {
        color: #8B4513;
      }
    }
    
    p {
      margin: 0.8rem 0;
      line-height: 1.4;
      color: #4a230c;
      flex: 1;
      font-style: italic;
    }
  }

  .corner {
    position: absolute;
    width: 35px;
    height: 35px;
    z-index: 3;
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
  
  @media (max-width: 600px) {
    .card-layout {
      flex-direction: column;
      
      .img {
        width: 100%;
        height: auto;
        margin-right: 0;
        margin-bottom: 1.5rem;
      }
    }
  }
</style>
