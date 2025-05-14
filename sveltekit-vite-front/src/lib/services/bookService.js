/**
 * Service for book-related API calls
 */

/**
 * Fetch books from API with all available filters
 * @param {Object} params - Query parameters
 * @returns {Promise<Array>} List of books
 */
export async function fetchBooks(params = {}) {
  try {
    const queryParams = new URLSearchParams();
    
    // Add all possible filter parameters from PublicBookController
    if (params.pageNumber !== undefined) queryParams.append('pageNumber', params.pageNumber);
    if (params.pageSize !== undefined) queryParams.append('pageSize', params.pageSize);
    if (params.asc !== undefined) queryParams.append('asc', params.asc);
    if (params.sortBy) queryParams.append('sortBy', params.sortBy);
    if (params.title) queryParams.append('titleBook', params.title);
    if (params.publisherName) queryParams.append('publisherName', params.publisherName);
    if (params.authorFirstName) queryParams.append('authorFirstName', params.authorFirstName);
    if (params.authorSurname) queryParams.append('authorSurname', params.authorSurname);
    if (params.illustratorFirstName) queryParams.append('illustratorFirstName', params.illustratorFirstName);
    if (params.illustratorSurname) queryParams.append('illustratorSurname', params.illustratorSurname);
    if (params.category) queryParams.append('category', params.category);
    if (params.serieName) queryParams.append('serieName', params.serieName);
    
    const url = `/api/public/books${queryParams.toString() ? '?' + queryParams.toString() : ''}`;
    
    const response = await fetch(url);
    
    if (!response.ok) {
      throw new Error(`Error: ${response.status} ${response.statusText}`);
    }
    
    return await response.json();
  } catch (err) {
    console.error('Error fetching books:', err);
    throw err;
  }
}

/**
 * Get a specific book by ISBN
 * @param {string} isbn - The ISBN of the book
 * @returns {Promise<Object>} Book details
 */
export async function getBookByISBN(isbn) {
  try {
    const response = await fetch(`/api/public/books/${isbn}`);
    
    if (!response.ok) {
      throw new Error(`Error: ${response.status} ${response.statusText}`);
    }
    
    return await response.json();
  } catch (err) {
    console.error(`Error fetching book with ISBN ${isbn}:`, err);
    throw err;
  }
}

/**
 * Fetch all authors from the system
 * @returns {Promise<Array>} List of authors
 */
export async function getAllAuthors() {
  try {
    const response = await fetch('/api/public/books/authors');
    
    if (!response.ok) {
      throw new Error(`Error: ${response.status} ${response.statusText}`);
    }
    
    return await response.json();
  } catch (err) {
    console.error("Failed to fetch authors:", err);
    throw err;
  }
}

/**
 * Fetch book genres from API
 * @returns {Promise<Array>} List of book genres
 */
export async function fetchGenres() {
  try {
    const response = await fetch('/api/public/books/genre');
    
    if (!response.ok) {
      throw new Error(`Error: ${response.status} ${response.statusText}`);
    }
    
    return await response.json();
  } catch (err) {
    console.error("Failed to fetch genres:", err);
    throw err;
  }
}

/**
 * Format author for display
 * @param {Object} book - Book object
 * @returns {string} Formatted author string
 */
export function formatAuthor(book) {
  if (!book.authors || !Array.isArray(book.authors) || book.authors.length === 0) {
    return 'Auteur inconnu';
  }
  
  return book.authors.map(author => 
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
 * If the book is avaivable
 * @param {Object} book - Book object
 * @returns {boolean} True if the book is available, false otherwise
 */
export function isBookAvailable(book) {
  return true; // Placeholder for actual availability logic
}