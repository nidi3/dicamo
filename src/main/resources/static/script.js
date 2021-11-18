window.addEventListener('error', e => debug(e));

let last = 0;
document.documentElement.addEventListener('click', () => {
  const now = Date.now();
  if (now - last < 500) {
    const s = window.getSelection();
    s.modify('move', 'backward', 'word');
    s.modify('extend', 'forward', 'word');
    const word = window.getSelection().toString().replace(/.*?'/, '');
    fetch('/query/' + word)
        .then(res => res.json())
        .then(entries => {
          if (entries.length === 1) {
            location.href = '/entry/' + entries[0].id;
          } else {
            location.href = '/?q=' + word;
          }
        });
  }
  last = now;
});

function debug(s) {
  const out = document.getElementById('out');
  out.appendChild(document.createTextNode(s + ' '));
}
