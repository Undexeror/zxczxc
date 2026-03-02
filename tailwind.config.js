/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/main/resources/templates/**/*.html"
  ],
  theme: {
    extend: {
      colors: {
        cyan: {
          500: '#06b6d4',
          600: '#0891b2',
        }
      }
    }
  },
  plugins: [],
}