import { sveltekit } from '@sveltejs/kit/vite';
import { defineConfig } from 'vite';

export default defineConfig({
	plugins: [sveltekit()],
	server: {
		proxy: {
		  '/api': {
			target: 'http://localhost:8080',
			changeOrigin: true,
			// Fix: Don't rewrite the path, keep the /api prefix
			// rewrite: path => path.replace(/^\/api/, '')
		  }
		}
	},
	optimizeDeps: {
        exclude: ['chunk-BCP3RLPO'] // Add the problematic dependency here
    }
});
