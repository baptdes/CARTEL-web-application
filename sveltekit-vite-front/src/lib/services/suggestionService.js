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