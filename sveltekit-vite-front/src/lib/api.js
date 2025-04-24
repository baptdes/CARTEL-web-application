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

// Books API
export const booksApi = {
  getAll: () => request('/api/public/books'),
  getById: (id) => request(`/api/public/books/${id}`),
  search: (params) => {
    const queryString = new URLSearchParams(params).toString();
    return request(`/api/public/books/search?${queryString}`);
  },
  create: (bookData) => request('/api/admin/books', { method: 'POST', body: JSON.stringify(bookData) }),
  update: (id, bookData) => request(`/api/admin/books/${id}`, { method: 'PUT', body: JSON.stringify(bookData) }),
  delete: (id) => request(`/api/admin/books/${id}`, { method: 'DELETE' }),
};