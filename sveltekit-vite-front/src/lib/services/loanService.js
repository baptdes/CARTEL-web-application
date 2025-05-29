/**
 * Service for loan-related API calls
 */

/**
 * Search for persons by full name (first name or surname)
 * @param {string} fullname - The name to search for
 * @param {number} pageNumber - The page number (default: 0)
 * @param {number} pageSize - The number of results per page (default: 20)
 * @returns {Promise<Object>} - Page of persons matching the search
 */
export async function searchPersons(fullname, pageNumber = 0, pageSize = 20) {
  try {
    const params = new URLSearchParams({
      fullname,
      pageNumber,
      pageSize
    });
    
    const response = await fetch(`/api/public/loans/persons/search?${params.toString()}`, {
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error(`Error searching persons. Status: ${response.status}`);
    }
    
    return await response.json();
  } catch (error) {
    console.error('Error searching persons:', error);
    throw error;
  }
}

/**
 * Add a new person
 * @param {Object} personData - Person data { firstname, surname, contact, caution }
 * @returns {Promise<Object>} - The created person
 */
export async function addPerson(personData) {
  try {
    const response = await fetch('/api/public/loans/persons/add', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include',
      body: JSON.stringify(personData)
    });
    
    if (!response.ok) {
      throw new Error(`Error adding person. Status: ${response.status}`);
    }
    
    return await response.json();
  } catch (error) {
    console.error('Error adding person:', error);
    throw error;
  }
}

/**
 * Create a loan to Cartel
 * @param {number} personId - ID of the person lending the item
 * @param {number} itemCopyId - ID of the item copy being lent
 * @returns {Promise<string>} - Confirmation message
 */
export async function createLoanToCartel(personId, itemCopyId) {
  try {
    const params = new URLSearchParams({
      personId,
      itemCopyId
    });
    
    const response = await fetch(`/api/public/loans/toCartel/addById?${params.toString()}`, {
      method: 'POST',
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error(`Error creating loan. Status: ${response.status}`);
    }
    
    return await response.text();
  } catch (error) {
    console.error('Error creating loan:', error);
    throw error;
  }
}

/**
 * Check if an item can be borrowed from Cartel
 * @param {string} itemId - The item barcode
 * @returns {Promise<boolean>} - True if the item can be borrowed
 */
export async function checkItemSharable(itemId) {
  try {
    const response = await fetch(`/api/public/loans/check/sharable/${itemId}`, {
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error(`Error checking if item is sharable. Status: ${response.status}`);
    }
    
    return await response.json();
  } catch (error) {
    console.error('Error checking if item is sharable:', error);
    throw error;
  }
}

/**
 * Get all loans to Cartel
 * @returns {Promise<Array>} - List of all loans to Cartel
 */
export async function getAllLoansToCartel() {
  try {
    const response = await fetch('/api/public/loans/toCartel/all/', {
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error(`Error retrieving loans to Cartel. Status: ${response.status}`);
    }
    
    return await response.json();
  } catch (error) {
    console.error('Error retrieving loans to Cartel:', error);
    throw error;
  }
}

/**
 * Get all loans from Cartel
 * @returns {Promise<Array>} - List of all loans from Cartel
 */
export async function getAllLoansFromCartel() {
  try {
    const response = await fetch('/api/public/loans/byCartel/all/', {
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error(`Error retrieving loans from Cartel. Status: ${response.status}`);
    }
    
    return await response.json();
  } catch (error) {
    console.error('Error retrieving loans from Cartel:', error);
    throw error;
  }
}
