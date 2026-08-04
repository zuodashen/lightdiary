import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import tailwindcss from '@tailwindcss/vite';
import { fileURLToPath, URL } from 'node:url';
const API_PREFIXES = [
    '/admin',
    '/article',
    '/category',
    '/tag',
    '/page',
    '/bookmark',
    '/bookmarkCategory',
    '/navItem',
    '/socialLink',
    '/siteSetting',
    '/comment',
    '/role',
    '/menu',
    '/resource',
    '/resourceCategory',
];
const proxy = Object.fromEntries(API_PREFIXES.map((prefix) => [
    prefix,
    { target: 'http://localhost:8080', changeOrigin: true },
]));
export default defineConfig({
    plugins: [vue(), tailwindcss()],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url)),
        },
    },
    server: {
        port: 5174,
        proxy,
    },
});
