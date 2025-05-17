/**
 * Service for interacting with the public suggestion API.
 * See: /spring-boot-api/src/main/java/cartel/spring_boot_api/controller/PublicSuggestionController.java
 */

const API_URL = '/api/public/suggestions';

/**
 * Create a new suggestion.
 * @param {Object} suggestion - The suggestion object to send.
 * @param {string} suggestion.name - The suggestion name.
 * @param {string} suggestion.type - The suggestion type (LIVRE, MANGA, BD, JDS, JDR, AUTRE).
 * @returns {Promise<Object>} The created suggestion.
 */
export async function createSuggestion(suggestion) {
  const response = await fetch(API_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(suggestion)
  });

  if (!response.ok) {
    throw new Error('Erreur lors de la création de la suggestion');
  }

  return await response.json();
}


/**
 * Fetch all suggestions from the API.
 * @returns {Promise<Array>} List of suggestions
 */
export async function fetchAllSuggestions() {
  const response = await fetch('/api/public/suggestions');
  if (!response.ok) {
    throw new Error('Erreur lors de la récupération des suggestions');
  }
  return await response.json();
}

/**
 * delete a suggestion by id
 * @param {number} id - The id of the suggestion to delete.
 * @returns {Promise<void>}
 */
export async function deleteSuggestion(id) {
  const response = await fetch(`${API_URL}/${id}`, {
    method: 'DELETE'
  });

  if (!response.ok) {
    throw new Error('Erreur lors de la suppression de la suggestion');
  }
}


