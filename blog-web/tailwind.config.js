/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  darkMode: ['class', '.dark'],
  theme: {
    extend: {
      colors: {
        primary: '#7509B6',
        secondary: '#0153E5',
        surface: {
          DEFAULT: '#111114',
          elevated: '#1a1a1f',
        },
        muted: '#8b8b9a',
      },
      fontFamily: {
        sans: ['Inter', 'Noto Sans SC', 'system-ui', 'sans-serif'],
      },
    },
  },
  plugins: [],
}
