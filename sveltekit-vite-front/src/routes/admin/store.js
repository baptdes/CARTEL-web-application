import { writable } from "svelte/store";

// Create the writable store for navigation state
export const adminPageState = writable(0);
