import { writable } from 'svelte/store';

// Create a writable store for auth token and loading state
export const token = writable(null);
export const isAuthenticated = writable(false);
export const authLoading = writable(false);

// Initialize auth state from localStorage if available
if (typeof window !== 'undefined') {
    const storedToken = localStorage.getItem('authToken');
    if (storedToken) {
        token.set(storedToken);
        isAuthenticated.set(true);
    }
}

// Function to handle login
export async function login(password) {
    authLoading.set(true);
    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ password }),
        });
        
        if (!response.ok) {
            const message = await response.text();
            throw new Error(message || 'Authentication failed');
        }
        
        const data = await response.json();
        
        // Store token in localStorage and update stores
        localStorage.setItem('authToken', data.token);
        token.set(data.token);
        isAuthenticated.set(true);
        
        return true;
    } catch (error) {
        console.error('Login error:', error);
        return { error: error.message };
    } finally {
        authLoading.set(false);
    }
}

// Function to handle logout
export function logout() {
    // Clear token from localStorage and reset stores
    localStorage.removeItem('authToken');
    token.set(null);
    isAuthenticated.set(false);
}

// Function to get auth header for API requests
export function getAuthHeader() {
    let currentToken = null;
    token.subscribe(value => { currentToken = value; })();
    
    return currentToken ? { Authorization: `Bearer ${currentToken}` } : {};
}
