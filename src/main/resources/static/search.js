const input = document.getElementById('input');
const options = document.getElementById('options');

window.addEventListener('error', e => debug(e));

window.addEventListener('pageshow', () => {
  input.focus();
  if (location.search) {
    const query = Object.fromEntries(location.search.substring(1).split('&').map(q => q.split('=')));
    if (query['q']) {
      input.value = decodeURIComponent(query['q']);
      load();
    }
  }
});

input.addEventListener('input', load);

function load() {
  if (input.value.length <= 2) {
    clear();
  } else {
    fetch('/query/' + input.value)
        .then(res => res.json())
        .then(entries => {
          clear();
          entries.forEach(({id, word}) => {
            const op = document.createElement('div');
            op.appendChild(document.createTextNode(word));
            op.onclick = () => location.href = '/entry/' + id;
            options.appendChild(op);
          });
        })
        .catch(e => debug(e));
  }
}

function clear() {
  while (options.firstChild) {
    options.removeChild(options.firstChild);
  }
}

function debug(s) {
  const out = document.getElementById('out');
  out.appendChild(document.createTextNode(s + ' '));
}
