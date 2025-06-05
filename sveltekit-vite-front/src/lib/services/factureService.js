/**
 * Service for facture-related API calls
 */

/**
 * Create a new facture
 * @param {Object} factureData - The facture data
 * @returns {Promise<Object>} The created facture
 */
export async function createFacture(factureData) {
  try {
    // Before sending, rename 'items' to 'copies' to match backend
    const formattedData = {
      ...factureData,
      copies: factureData.items  // Rename the property
    };
    delete formattedData.items;  // Remove the old property
    
    const response = await fetch('/api/public/factures', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include',
      body: JSON.stringify(formattedData)
    });
    
    if (!response.ok) {
      const errorText = await response.text().catch(() => null);
      const error = new Error(`Error creating facture. Status: ${response.status}${errorText ? ` - ${errorText}` : ''}`);
      error.status = response.status;
      throw error;
    }
    
    // Parse response and adapt the received object back to frontend's expectation
    const data = await response.json();
    if (data.copies) {
      data.items = data.copies; // Add back an items property for frontend compatibility
    }
    return data;
  } catch (error) {
    console.error('Error creating facture:', error);
    throw error;
  }
}

/**
 * Get all factures
 * @returns {Promise<Array>} List of factures
 */
export async function getAllFactures() {
  try {
    const response = await fetch('/api/public/factures', {
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error(`Error fetching factures. Status: ${response.status}`);
    }
    
    // Adapt the returned data to maintain frontend compatibility
    const factures = await response.json();
    return factures.map(facture => {
      if (facture.copies) {
        // Map the copies to items (with their parent item data) for frontend
        facture.items = facture.copies.map(copy => {
          return {
            ...copy.objet, // Spread the parent item properties
            copyId: copy.id, // Keep track of the copy ID
            available: copy.available,
            borrowable: copy.borrowable
          };
        });
      }
      return facture;
    });
  } catch (error) {
    console.error('Error fetching factures:', error);
    throw error;
  }
}

/**
 * Get a facture by ID
 * @param {string} id - The facture ID
 * @returns {Promise<Object>} The facture object
 */
export async function getFactureById(id) {
  try {
    const response = await fetch(`/api/public/factures/${id}`, {
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error(`Error fetching facture. Status: ${response.status}`);
    }
    
    // Adapt the returned data to maintain frontend compatibility
    const facture = await response.json();
    if (facture.copies) {
      // Map the copies to items for frontend
      facture.items = facture.copies.map(copy => {
        return {
          ...copy.objet, // Spread the parent item properties
          copyId: copy.id, // Keep track of the copy ID
          available: copy.available,
          borrowable: copy.borrowable
        };
      });
    }
    return facture;
  } catch (error) {
    console.error('Error fetching facture:', error);
    throw error;
  }
}

/**
 * Delete a facture
 * @param {string} id - The facture ID
 * @returns {Promise<void>}
 */
export async function deleteFacture(id) {
  try {
    const response = await fetch(`/api/public/factures/${id}`, {
      method: 'DELETE',
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error(`Error deleting facture. Status: ${response.status}`);
    }
    
    return true;
  } catch (error) {
    console.error('Error deleting facture:', error);
    throw error;
  }


}

export async function searchFacturesByItem(query) {
  try {
    const response = await fetch(`/api/public/factures/search-by-item?query=${encodeURIComponent(query)}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include',
    });

    if (!response.ok) {
      if (response.status === 403) {
        throw { status: 403, message: 'Forbidden' };
      }
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to search factures');
    }

    return await response.json();
  } catch (error) {
    console.error('Error searching factures by item:', error);
    throw error;
  }
}