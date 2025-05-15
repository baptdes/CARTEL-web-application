/**
 * Service for game-related API calls
 */

/**
 * Fetch games from API with all available filters
 * @param {Object} params - Query parameters
 * @returns {Promise<Array>} List of games
 */
export async function fetchGames(params = {}) {
  try {
    const queryParams = new URLSearchParams();
    
    // Add all possible filter parameters from PublicGameController
    if (params.pageNumber !== undefined) queryParams.append('pageNumber', params.pageNumber);
    if (params.pageSize !== undefined) queryParams.append('pageSize', params.pageSize);
    if (params.asc !== undefined) queryParams.append('asc', params.asc);
    if (params.sortBy) queryParams.append('sortBy', params.sortBy);
    if (params.titleGame) queryParams.append('titleGame', params.titleGame);
    if (params.publisherName) queryParams.append('publisherName', params.publisherName);
    if (params.creatorFirstName) queryParams.append('creatorFirstName', params.creatorFirstName);
    if (params.creatorSurname) queryParams.append('creatorSurname', params.creatorSurname);
    if (params.minPlayers) queryParams.append('minPlayers', params.minPlayers);
    if (params.maxPlayers) queryParams.append('maxPlayers', params.maxPlayers);
    if (params.minPlaytime) queryParams.append('minPlaytime', params.minPlaytime);
    if (params.maxPlaytime) queryParams.append('maxPlaytime', params.maxPlaytime);
    if (params.category) queryParams.append('category', params.category);
    
    const url = `/api/public/games${queryParams.toString() ? '?' + queryParams.toString() : ''}`;
    
    const response = await fetch(url);
    
    if (!response.ok) {
      throw new Error(`Error: ${response.status} ${response.statusText}`);
    }
    
    return await response.json();
  } catch (err) {
    console.error('Error fetching games:', err);
    throw err;
  }
}

/**
 * Get a specific game by barcode
 * @param {string} barcode - The barcode of the game
 * @returns {Promise<Object>} Game details
 */
export async function getGameByBarcode(barcode) {
  try {
    const response = await fetch(`/api/public/games/${barcode}`);
    
    if (!response.ok) {
      throw new Error(`Error: ${response.status} ${response.statusText}`);
    }
    
    return await response.json();
  } catch (err) {
    console.error(`Error fetching game with barcode ${barcode}:`, err);
    throw err;
  }
}

/**
 * Fetch all game creators from the system
 * @returns {Promise<Array>} List of creators
 */
export async function getAllCreators() {
  try {
    const response = await fetch('/api/public/games/creators');
    
    if (!response.ok) {
      throw new Error(`Error: ${response.status} ${response.statusText}`);
    }
    
    return await response.json();
  } catch (err) {
    console.error("Failed to fetch creators:", err);
    throw err;
  }
}

/**
 * Fetch game categories from API
 * @returns {Promise<Array>} List of game categories
 */
export async function getAllGameCategories() {
  try {
    const response = await fetch('/api/public/games/categories');
    
    if (!response.ok) {
      throw new Error(`Error: ${response.status} ${response.statusText}`);
    }
    
    return await response.json();
  } catch (err) {
    console.error("Failed to fetch game categories:", err);
    throw err;
  }
}

/**
 * Format creator for display
 * @param {Object} game - Game object
 * @returns {string} Formatted creator string
 */
export function formatCreator(game) {
  if (!game.creators || !Array.isArray(game.creators) || game.creators.length === 0) {
    return 'Créateur inconnu';
  }
  
  return game.creators.map(creator => 
    `${creator.firstname || ''} ${creator.surname || ''}`.trim()
  ).join(', ');
}

/**
 * Format publisher for display
 */
export function formatPublisher(game) {
  if (!game.publisher || !game.publisher.name) {
    return 'Éditeur inconnu';
  }
  return game.publisher.name;
}

/**
 * Format categories for display
 */
export function formatCategories(game) {
  if (!game.categories || !Array.isArray(game.categories) || game.categories.length === 0) {
    return 'Catégorie inconnue';
  }
  
  return game.categories.join(', ');
}

/**
 * Format player count for display
 */
export function formatPlayerCount(game) {
  if (!game.minPlayers && !game.maxPlayers) {
    return 'Nombre de joueurs inconnu';
  }
  
  if (game.minPlayers === game.maxPlayers) {
    return `${game.minPlayers} joueur${game.minPlayers > 1 ? 's' : ''}`;
  }
  
  return `${game.minPlayers} - ${game.maxPlayers} joueurs`;
}

/**
 * Format playtime for display
 */
export function formatPlaytime(game) {
  if (!game.avgPlaytime) {
    return 'Durée inconnue';
  }
  
  return `${game.avgPlaytime} minute${game.avgPlaytime > 1 ? 's' : ''}`;
}

/**
 * If the game is available
 * @param {Object} game - Game object
 * @returns {boolean} True if the game is available, false otherwise
 */
export function isGameAvailable(game) {
  return game.copyCount > 0;
}
