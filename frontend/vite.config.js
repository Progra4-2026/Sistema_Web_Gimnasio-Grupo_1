import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// Cabeceras HTTP de seguridad para las páginas del frontend (las de la API las pone
// Spring Security en SecurityConfig.java):
//  - X-Frame-Options: DENY            → el sitio no se puede incrustar en un iframe (clickjacking)
//  - X-Content-Type-Options: nosniff  → el navegador respeta el tipo de cada archivo
//  - Referrer-Policy                  → no envía la URL completa a otros sitios
// Strict-Transport-Security no se envía aquí porque el servidor de desarrollo usa HTTP;
// solo aplica cuando el sitio se publica con HTTPS.
const cabecerasSeguridad = {
  'X-Frame-Options': 'DENY',
  'X-Content-Type-Options': 'nosniff',
  'Referrer-Policy': 'strict-origin-when-cross-origin',
}

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    headers: cabecerasSeguridad,
  },
  preview: {
    headers: cabecerasSeguridad,
  },
})
