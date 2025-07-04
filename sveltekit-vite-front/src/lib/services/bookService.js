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

    // Required/default params
    if (params.pageNumber !== undefined) queryParams.append('pageNumber', params.pageNumber);
    if (params.pageSize !== undefined) queryParams.append('pageSize', params.pageSize);
    if (params.asc !== undefined) queryParams.append('asc', params.asc);
    if (params.sortBy) queryParams.append('sortBy', params.sortBy);

    // Optional filters
    if (params.titleBook) queryParams.append('titleBook', params.titleBook);
    if (params.publisherName) queryParams.append('publisherName', params.publisherName);
    if (params.authorFirstName) queryParams.append('authorFirstName', params.authorFirstName);
    if (params.authorSurname) queryParams.append('authorSurname', params.authorSurname);
    if (params.authorFullName) queryParams.append('authorFullName', params.authorFullName);
    if (params.illustratorFirstName) queryParams.append('illustratorFirstName', params.illustratorFirstName);
    if (params.illustratorSurname) queryParams.append('illustratorSurname', params.illustratorSurname);
    if (params.illustratorFullName) queryParams.append('illustratorFullName', params.illustratorFullName);
    if (params.category) queryParams.append('category', params.category);
    if (params.serieName) queryParams.append('serieName', params.serieName);
    if (params.genreName) queryParams.append('genreName', params.genreName);

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
export async function getAllGenres() {
  try {
    const response = await fetch('/api/public/books/genres');
    
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
 * Add a new genre
 * @param {String} genre - The genre to add
 * @returns {Promise<Object>} The added genre object
 */
export async function addGenre(genre) {
  try {
    const response = await fetch("/api/public/books/genres?name=" + genre, {
      method: 'POST',
      headers: {
      'Content-Type': 'application/json'
      },
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error(`Error adding genre. Status: ${response.status}`);
    }
    
    return await response.json();
  } catch (error) {
    console.error('Error adding genre:', error);
    throw error;
  }
}

/**
 * Fetch book illustrators from API
 * @returns {Promise<Array>} List of book illustrators
 */
export async function getAllIllustrators() {
  try {
    const response = await fetch('/api/public/books/illustrators');
    
    if (!response.ok) {
      throw new Error(`Error: ${response.status} ${response.statusText}`);
    }
    
    return await response.json();
  } catch (err) {
    console.error("Failed to fetch illustrators:", err);
    throw err;
  }
}

/**
 * Get all publishers
 * @returns {Promise<Array>} List of publishers
 */
export async function getAllPublishers() {
  try {
    const response = await fetch('/api/public/books/publishers');
    
    if (!response.ok) {
      throw new Error(`Error: ${response.status} ${response.statusText}`);
    }
    
    return await response.json();
  } catch (err) {
    console.error("Failed to fetch publishers:", err);
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
 * Format illustrator for display
 * @param {Object} book - Book object
 * @returns {string} Formatted illustrator string
 */
export function formatIllustrator(book) {
  if (!book.illustrator || !Array.isArray(book.illustrator) || book.illustrator.length === 0) {
    return 'Illustrateur inconnu';
  }
  
  return book.illustrator.map(illustrator => 
    `${illustrator.firstname || ''} ${illustrator.surname || ''}`.trim()
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
  // Check for genres property first (new format)
  if (book.genres && Array.isArray(book.genres) && book.genres.length > 0) {
    // If genres are objects with name property
    if (typeof book.genres[0] === 'object') {
      return book.genres.map(genre => genre.name).join(', ');
    }
    // If genres are already strings
    return book.genres.join(', ');
  }
  
  return 'Genre inconnu';
}

/**
 * If the book is avaivable
 * @param {Object} book - Book object
 * @returns {boolean} True if the book is available, false otherwise
 */
export function isBookAvailable(book) {
  return true; // Placeholder for actual availability logic
}

/**
 * Delete a book by ISBN
 * @param {string} isbn - The ISBN of the book to delete
 * @returns {Promise<boolean} True if the book was deleted, false otherwise
 */
export async function deleteBook(isbn) {
  try {
    const response = await fetch(`/api/public/books/${isbn}`, {
      method: 'DELETE',
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error(`Error deleting book. Status: ${response.status}`);
    }
    
    return true;
  } catch (error) {
    console.error('Error deleting book:', error);
    throw error;
  }
}

/**
 * Add a new book
 * @param {Object} book - The book object to add
 * @returns {Promise<Object>} The added book object
 */
export async function addBook(book) {
  try {
    const response = await fetch('/api/public/books', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include',
      body: JSON.stringify(book)
    });
    
    if (!response.ok) {
      throw new Error(`Error adding book. Status: ${response.status}`);
    }
    
    return await response.json();
  } catch (error) {
    console.error('Error adding book:', error);
    throw error;
  }
}

/**
 * Add a new author
 * @param {Object} author - { firstname, surname }
 * @returns {Promise<Object>} The added author object
 */
export async function addAuthor(author) {
  try {
    const { surname, firstname } = author;
    const params = new URLSearchParams({ surname, firstname });
    const response = await fetch(`/api/public/books/authors?${params.toString()}`, {
      method: 'POST',
      headers: {
      'Content-Type': 'application/json'
      },
      credentials: 'include'
    });
    if (!response.ok) {
      throw new Error(`Error adding author. Status: ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error('Error adding author:', error);
    throw error;
  }
}

/** Add a new Illustrators
 * @param {Object} illustrator - { firstname, surname }
 * @returns {Promise<Object>} The added illustrator object
 */
export async function addIllustrator(illustrator) {
  try {
    const { surname, firstname } = illustrator;
    const params = new URLSearchParams({ surname, firstname });
    const response = await fetch(`/api/public/books/illustrators?${params.toString()}`, {
      method: 'POST',
      headers: {
      'Content-Type': 'application/json'
      },
      credentials: 'include'
    });
    if (!response.ok) {
      throw new Error(`Error adding illustrator. Status: ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error('Error adding illustrator:', error);
    throw error;
  }
}

/**
 * Get book details from BNF API by ISBN
 * @param {string} isbn - The ISBN of the book
 * @returns {Promise<Object>} The book details from BNF
 */
export async function getBookFromBNF(isbn) {
  try {
    const response = await fetch(`/api/public/books/bnf/${isbn}`, {
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error(`Error fetching book from BNF. Status: ${response.status}`);
    }
    
    return await response.json();
  } catch (error) {
    console.error('Error fetching book from BNF:', error);
    throw error;
  }
}