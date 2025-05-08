/**
 * Service for book-related API calls
 */

/**
 * Fetch books from API
 * @param {Object} params - Query parameters
 * @returns {Promise<Array>} List of books
 */
export async function fetchBooks(params = {}) {
  try {
    let url = '/api/public/books';
    
    // Simple search by title only - rest will be handled by API
    if (params.title) {
      url = `/api/public/books/search?title=${encodeURIComponent(params.title)}`;
    }
    
    const response = await fetch(url);
    
    if (!response.ok) {
      throw new Error(`Error: ${response.status} ${response.statusText}`);
    }
    
    const data = await response.json();

    return data;
  } catch (err) {
    console.error('Error fetching books:', err);
    throw err;
  }
}

/**
 * Fetch book formats from API
 * @returns {Promise<Array>} List of book formats
 */
export async function fetchFormats() {
  try {
    const response = await fetch('/api/public/books/genre');
    
    if (!response.ok) {
      throw new Error(`Error: ${response.status} ${response.statusText}`);
    }
    
    return await response.json();
  } catch (err) {
    console.error("Failed to fetch formats:", err);
    throw err;
  }
}

/**
 * Format author for display
 * @param {Object} book - Book object
 * @returns {string} Formatted author string
 */
export function formatAuthor(book) {
  if (!book.author || !Array.isArray(book.author) || book.author.length === 0) {
    return 'Auteur inconnu';
  }
  
  return book.author.map(author => 
    `${author.firstname || ''} ${author.surname || ''}`.trim()
  ).join(', ');
}

/**
 * Format publisher for display
 */
export function formatPublisher(book) {
  if (!book.publisher || !book.publisher.name) {
    return 'Éditeur inconnu';
  }
  return book.publisher.name;
}

/**
 * Format genre for display
 */
export function formatGenre(book) {
  if (!book.genre || !Array.isArray(book.genre) || book.genre.length === 0) {
    return 'Genre inconnu';
  }
  return book.genre.join(', ');
}

/**
 * Check if a book is available
 */
export function isBookAvailable(book) {
  return !(book.statut && book.statut.borrower);
}
