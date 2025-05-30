/**
 * Service for item copy-related API calls
 */

/**
 * Create a new copy for an item
 * @param {string} itemId - The ID (barcode) of the item for which to create a copy
 * @returns {Promise<Object>} The created copy object
 */
export async function createItemCopy(itemId) {
  try {
    const params = new URLSearchParams({
      itemId
    });
    
    const response = await fetch(`/api/public/copies/create?${params.toString()}`, {
      method: 'POST',
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
 * Get copy by ID
 * @param {number} copyId - The ID of the copy to retrieve
 * @returns {Promise<Object>} The copy object
 */
export async function getItemCopy(copyId) {
  try {
    const response = await fetch(`/api/public/copies/${copyId}`, {
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error(`Error retrieving item copy. Status: ${response.status}`);
    }
    
    return await response.json();
  } catch (error) {
    console.error('Error retrieving item copy:', error);
    throw error;
  }
}

/**
 * Delete a copy by ID
 * @param {number} copyId - The ID of the copy to delete
 * @returns {Promise<string>} Confirmation message
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

    return await response.text();
  } catch (error) {
    console.error('Error deleting item copy:', error);
    throw error;
  }
}

/**
 * Search for item copies by item ID (barcode)
 * @param {string} itemId - The item ID/barcode
 * @returns {Promise<Object>} - List of item copies matching the itemId
 */
export async function searchItemCopies(itemId) {
  try {
    const response = await fetch(`/api/public/items/${itemId}/copies`, {
      credentials: 'include'
    });

    if (!response.ok) {
      throw new Error(`Error searching item copies. Status: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error('Error searching item copies:', error);
    throw error;
  }
}

/**
 * Check if a copy is available (borrowable)
 * @param {number} copyId - The ID of the copy to check
 * @returns {Promise<boolean>} True if the copy is available, false otherwise
 */
export async function isItemCopyBorrowable(copyId) {
  try {
    const response = await fetch(`/api/public/copies/${copyId}/isAvailable`, {
      credentials: 'include'
    });

    if (!response.ok) {
      throw new Error(`Error checking if item copy is borrowable. Status: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error('Error checking if item copy is borrowable:', error);
    throw error;
  }
}