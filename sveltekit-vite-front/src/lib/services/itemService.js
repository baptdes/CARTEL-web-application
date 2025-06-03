/**
 * Service for item-related API calls
 */

/**
 * Fetch items from API with name search and pagination
 * @param {Object} params - Query parameters
 * @returns {Promise<Object>} Paginated list of items
 */
export async function searchItems(params = {}) {
  try {
    const queryParams = new URLSearchParams();

    // Required/default params
    if (params.name !== undefined) queryParams.append('name', params.name);
    if (params.pageNumber !== undefined) queryParams.append('pageNumber', params.pageNumber);
    if (params.pageSize !== undefined) queryParams.append('pageSize', params.pageSize);
    if (params.asc !== undefined) queryParams.append('asc', params.asc);
    if (params.sortBy) queryParams.append('sortBy', params.sortBy);

    const url = `/api/public/items/search${queryParams.toString() ? '?' + queryParams.toString() : ''}`;

    const response = await fetch(url);

    if (!response.ok) {
      throw new Error(`Error: ${response.status} ${response.statusText}`);
    }

    return await response.json();
  } catch (err) {
    console.error('Error searching items:', err);
    throw err;
  }
}

/**
 * Fetch all items with pagination
 * @param {Object} params - Query parameters for pagination
 * @returns {Promise<Object>} Paginated list of all items
 */
export async function fetchAllItems(params = {}) {
  try {
    const queryParams = new URLSearchParams();

    // Pagination params
    if (params.pageNumber !== undefined) queryParams.append('pageNumber', params.pageNumber);
    if (params.pageSize !== undefined) queryParams.append('pageSize', params.pageSize);
    if (params.asc !== undefined) queryParams.append('asc', params.asc);
    if (params.sortBy) queryParams.append('sortBy', params.sortBy);

    const url = `/api/public/items${queryParams.toString() ? '?' + queryParams.toString() : ''}`;

    const response = await fetch(url);

    if (!response.ok) {
      throw new Error(`Error: ${response.status} ${response.statusText}`);
    }

    return await response.json();
  } catch (err) {
    console.error('Error fetching all items:', err);
    throw err;
  }
}

/**
 * Fetch an item by its ID (barcode)
 * @param {string} itemId - The item's barcode or ID
 * @returns {Promise<Object>} Item details
 */
export async function getItemById(itemId) {
  try {
    const response = await fetch(`/api/public/items/${itemId}`);

    if (!response.ok) {
      throw new Error(`Error: ${response.status} ${response.statusText}`);
    }

    return await response.json();
  } catch (err) {
    console.error(`Error fetching item with ID ${itemId}:`, err);
    throw err;
  }
}

/**
 * Create a new item copy
 * @param {string} itemId - The ID of the item for which to create a copy
 * @returns {Promise<Object>} The created item copy
 */
export async function createItemCopy(itemId) {
  try {
    const response = await fetch(`/api/public/copies/create?itemId=${itemId}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error(`Error creating item copy. Status: ${response.status}`);
    }
    
    return await response.json();
  } catch (error) {
    console.error('Error creating item copy:', error);
    throw error;
  }
}

/**
 * Get an item copy by its ID
 * @param {number} copyId - The ID of the item copy
 * @returns {Promise<Object>} The item copy details
 */
export async function getItemCopyById(copyId) {
  try {
    const response = await fetch(`/api/public/${copyId}/copies`);
    
    if (!response.ok) {
      throw new Error(`Error: ${response.status} ${response.statusText}`);
    }
    
    return await response.json();
  } catch (err) {
    console.error(`Error fetching item copy with ID ${copyId}:`, err);
    throw err;
  }
}

/**
 * Delete an item copy
 * @param {number} copyId - The ID of the item copy to delete
 * @returns {Promise<boolean>} True if deletion was successful
 */
export async function deleteItemCopy(copyId) {
  try {
    const response = await fetch(`/api/public/copies/${copyId}`, {
      method: 'DELETE',
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error(`Error deleting item copy. Status: ${response.status}`);
    }
    
    return true;
  } catch (error) {
    console.error('Error deleting item copy:', error);
    throw error;
  }
}

/**
 * Get all item copies
 * @returns {Promise<Array>} List of all item copies
 */
export async function getAllItemCopies() {
  try {
    const response = await fetch('/api/public/copies/all');
    
    if (!response.ok) {
      throw new Error(`Error: ${response.status} ${response.statusText}`);
    }
    
    return await response.json();
  } catch (err) {
    console.error("Failed to fetch item copies:", err);
    throw err;
  }
}

/**
 * Format item availability status
 * @param {Object} item - The item object 
 * @returns {boolean} True if the item is available
 */
export function isItemAvailable(item) {
  // Cette fonction pourrait être développée davantage selon la logique spécifique
  // pour déterminer si un item est disponible
  return item && item.available !== false;
}

/**
 * Format item details for display
 * @param {Object} item - The item object
 * @returns {Object} Formatted item details
 */
export function formatItemDetails(item) {
  if (!item) return null;
  
  return {
    id: item.barcode || 'ID inconnu',
    name: item.name || 'Nom inconnu',
    description: item.description || 'Pas de description disponible',
    type: getItemTypeName(item),
    // Ajoutez d'autres propriétés formatées selon vos besoins
  };
}
