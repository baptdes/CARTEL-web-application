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
 * Create a loan by Cartel
 * @param {number} personId - ID of the person borrowing the item
 * @param {number} itemCopyId - ID of the item copy being borrowed
 * @returns {Promise<string>} - Confirmation message
 */
export async function createLoanByCartel(personId, itemCopyId) {
  try {
    const params = new URLSearchParams({
      personId,
      itemCopyId
    });
    
    const response = await fetch(`/api/public/loans/byCartel/addById?${params.toString()}`, {
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

/**
 * Filter loans to Cartel with advanced criteria
 * @param {Object} filters - Filter criteria
 * @param {number} filters.pageNumber - Page number (default: 0)
 * @param {number} filters.pageSize - Page size (default: 20)
 * @param {boolean} filters.asc - Sort direction (default: false)
 * @param {string} filters.sortBy - Field to sort by (default: loanDate)
 * @param {string} filters.itemName - Filter by item name
 * @param {string} filters.ownerFirstName - Filter by owner first name
 * @param {string} filters.ownerSurname - Filter by owner surname
 * @param {string} filters.startDateBefore - Filter loans starting before this date (YYYY-MM-DD)
 * @param {string} filters.startDateAfter - Filter loans starting after this date (YYYY-MM-DD)
 * @param {string} filters.endDateBefore - Filter loans ending before this date (YYYY-MM-DD)
 * @param {string} filters.endDateAfter - Filter loans ending after this date (YYYY-MM-DD)
 * @param {boolean} filters.active - Filter active or completed loans
 * @returns {Promise<Array>} - Filtered list of loans
 */
export async function filterLoansToCartel({
  pageNumber = 0,
  pageSize = 20,
  asc = false,
  sortBy = 'loanDate',
  itemName = null,
  ownerFirstName = null,
  ownerSurname = null,
  startDateBefore = null,
  startDateAfter = null,
  endDateBefore = null,
  endDateAfter = null,
  active = null
} = {}) {
  try {
    const params = new URLSearchParams();
    
    // Add all parameters to URLSearchParams
    params.append('pageNumber', pageNumber);
    params.append('pageSize', pageSize);
    params.append('asc', asc);
    params.append('sortBy', sortBy);
    
    // Only add optional parameters if they're provided
    if (itemName) params.append('itemName', itemName);
    if (ownerFirstName) params.append('ownerFirstName', ownerFirstName);
    if (ownerSurname) params.append('ownerSurname', ownerSurname);
    if (startDateBefore) params.append('startDateBefore', startDateBefore);
    if (startDateAfter) params.append('startDateAfter', startDateAfter);
    if (endDateBefore) params.append('endDateBefore', endDateBefore);
    if (endDateAfter) params.append('endDateAfter', endDateAfter);
    if (active !== null) params.append('active', active);
    
    const response = await fetch(`/api/public/loans/toCartel?${params.toString()}`, {
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error(`Error filtering loans to Cartel. Status: ${response.status}`);
    }
    
    return await response.json();
  } catch (error) {
    console.error('Error filtering loans to Cartel:', error);
    throw error;
  }
}

/**
 * Filter loans by Cartel with advanced criteria
 * @param {Object} filters - Filter criteria
 * @param {number} filters.pageNumber - Page number (default: 0)
 * @param {number} filters.pageSize - Page size (default: 20)
 * @param {boolean} filters.asc - Sort direction (default: false)
 * @param {string} filters.sortBy - Field to sort by (default: loanDate)
 * @param {string} filters.itemName - Filter by item name
 * @param {string} filters.borrowerFirstName - Filter by borrower first name
 * @param {string} filters.borrowerSurname - Filter by borrower surname
 * @param {string} filters.startDateBefore - Filter loans starting before this date (YYYY-MM-DD)
 * @param {string} filters.startDateAfter - Filter loans starting after this date (YYYY-MM-DD)
 * @param {string} filters.endDateBefore - Filter loans ending before this date (YYYY-MM-DD)
 * @param {string} filters.endDateAfter - Filter loans ending after this date (YYYY-MM-DD)
 * @param {boolean} filters.active - Filter active or completed loans
 * @returns {Promise<Array>} - Filtered list of loans
 */
export async function filterLoansByCartel({
  pageNumber = 0,
  pageSize = 20,
  asc = false,
  sortBy = 'loanDate',
  itemName = null,
  borrowerFirstName = null,
  borrowerSurname = null,
  startDateBefore = null,
  startDateAfter = null,
  endDateBefore = null,
  endDateAfter = null,
  active = null
} = {}) {
  try {
    const params = new URLSearchParams();
    
    // Add all parameters to URLSearchParams
    params.append('pageNumber', pageNumber);
    params.append('pageSize', pageSize);
    params.append('asc', asc);
    params.append('sortBy', sortBy);
    
    // Only add optional parameters if they're provided
    if (itemName) params.append('itemName', itemName);
    if (borrowerFirstName) params.append('borrowerFirstName', borrowerFirstName);
    if (borrowerSurname) params.append('borrowerSurname', borrowerSurname);
    if (startDateBefore) params.append('startDateBefore', startDateBefore);
    if (startDateAfter) params.append('startDateAfter', startDateAfter);
    if (endDateBefore) params.append('endDateBefore', endDateBefore);
    if (endDateAfter) params.append('endDateAfter', endDateAfter);
    if (active !== null) params.append('active', active);
    
    const response = await fetch(`/api/public/loans/byCartel?${params.toString()}`, {
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error(`Error filtering loans by Cartel. Status: ${response.status}`);
    }
    
    return await response.json();
  } catch (error) {
    console.error('Error filtering loans by Cartel:', error);
    throw error;
  }
}

/**
 * Mark a loan to Cartel as complete (item returned)
 * @param {number} loanId - ID of the loan to complete
 * @returns {Promise<string>} - Confirmation message
 */
export async function completeLoanToCartel(loanId) {
  try {
    const response = await fetch(`/api/public/loans/toCartel/${loanId}/complete`, {
      method: 'POST',
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error(`Error completing loan. Status: ${response.status}`);
    }
    
    return await response.text();
  } catch (error) {
    console.error('Error completing loan:', error);
    throw error;
  }
}

/**
 * Mark a loan by Cartel as complete (item returned)
 * @param {number} loanId - ID of the loan to complete
 * @returns {Promise<string>} - Confirmation message
 */
export async function completeLoanByCartel(loanId) {
  try {
    const response = await fetch(`/api/public/loans/byCartel/${loanId}/complete`, {
      method: 'POST',
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error(`Error completing loan. Status: ${response.status}`);
    }
    
    return await response.text();
  } catch (error) {
    console.error('Error completing loan:', error);
    throw error;
  }
}

/**
 * Delete a loan to Cartel
 * @param {number} loanId - ID of the loan to delete
 * @returns {Promise<void>}
 */
export async function deleteLoanToCartel(loanId) {
  try {
    const response = await fetch(`/api/public/loans/toCartel/${loanId}`, {
      method: 'DELETE',
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error(`Error deleting loan. Status: ${response.status}`);
    }
  } catch (error) {
    console.error('Error deleting loan:', error);
    throw error;
  }
}

/**
 * Delete a loan by Cartel
 * @param {number} loanId - ID of the loan to delete
 * @returns {Promise<void>}
 */
export async function deleteLoanByCartel(loanId) {
  try {
    const response = await fetch(`/api/public/loans/byCartel/${loanId}`, {
      method: 'DELETE',
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error(`Error deleting loan. Status: ${response.status}`);
    }
  } catch (error) {
    console.error('Error deleting loan:', error);
    throw error;
  }
}
