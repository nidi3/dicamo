window.addEventListener('error', e => debug(e));

let last = 0;
document.documentElement.addEventListener('click', async () => {
  const now = Date.now();
  if (now - last < 500) {
    const s = window.getSelection();
    s.modify('move', 'backward', 'word');
    s.modify('extend', 'forward', 'word');
    const word = window.getSelection().toString().replace(/.*?'|[,.)\]]/, '');
    const {_, entries} = await fetchJson('/query/' + word);
    if (entries.length === 1) {
      location.href = entries[0][0].link;
    } else {
      location.href = '/?q=' + word;
    }
  }
  last = now;
});

function debug(s) {
  const out = document.getElementById('out');
  out.appendChild(document.createTextNode(s + ' '));
}

async function fetchJson(url) {
  const res = await fetch(url);
  return await res.json();
}
