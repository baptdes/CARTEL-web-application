import { getAuthHeader } from './auth';

const request = async (url, options = {}) => {
  const headers = {
    'Content-Type': 'application/json',
    ...getAuthHeader(),
    ...options.headers,
  };

  const response = await fetch(url, { ...options, headers });
  if (!response.ok) throw new Error(`Request failed: ${response.statusText}`);
  return response.status !== 204 ? await response.json() : true; // Handle no-content responses
};