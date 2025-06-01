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
    const response = await fetch('/api/factures', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include',
      body: JSON.stringify(factureData)
    });
    
    if (!response.ok) {
      throw new Error(`Error creating facture. Status: ${response.status}`);
    }
    
    return await response.json();
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
    const response = await fetch('/api/factures', {
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error(`Error fetching factures. Status: ${response.status}`);
    }
    
    return await response.json();
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
    const response = await fetch(`/api/factures/${id}`, {
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error(`Error fetching facture. Status: ${response.status}`);
    }
    
    return await response.json();
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
    const response = await fetch(`/api/factures/${id}`, {
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