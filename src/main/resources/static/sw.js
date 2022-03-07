self.addEventListener('install', (e) => {
  e.waitUntil(
      caches.open('dicamo').then((cache) => cache.addAll([
        '/install.js',
        '/script.js',
        '/search.html',
        '/search.js',
        '/styles.css'
      ]))
  );
});

self.addEventListener('fetch', (e) => {
  console.log(e.request.url);
  e.respondWith(
      caches.match(e.request).then((response) => response || fetch(e.request))
  );
});
