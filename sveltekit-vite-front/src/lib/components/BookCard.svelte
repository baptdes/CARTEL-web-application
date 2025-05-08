<script>
  import { formatAuthor, formatPublisher, formatGenre, isBookAvailable } from '$lib/services/bookService';

  // Props
  export let book;
  
  // Event handlers
  function handleClick() {
    // Could be expanded to navigate to a detail page, etc.
    console.log('Book clicked:', book);
  }
</script>

<button 
  class="card" 
  on:click={handleClick}
  on:keydown={(e) => e.key === 'Enter' && handleClick()}
  type="button"
>
  <div class="img">
    <img 
      src={book.coverImage || "/placeholder_book.png"}
      alt={`Couverture de ${book.name || 'Livre sans titre'}`}
      loading="lazy"
    />
    <div class="badge">
      <img src="/icons/books.svg" alt="Livre" />
    </div>
  </div>
  
  <div class="info">
    <div class="header">
      <h3>{book.name || `Un troll 🧌 a mangé le titre de ce livre`}</h3>
      <div class:available={isBookAvailable(book)}>
        {isBookAvailable(book) ? 'Disponible' : 'Indisponible'}
      </div>
    </div>
    
    <div class="meta">
      <strong>Auteur :</strong> {formatAuthor(book) || 'Non spécifié'}
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
</button>

<style lang="scss">
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
  
  @media (max-width: 600px) {
    .card {
      flex-direction: column;
      
      .img {
        width: 100%;
        height: 200px;
      }
    }
  }
</style>
