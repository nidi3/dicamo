const input = document.getElementById('input');
const options = document.getElementById('options');

window.addEventListener('error', e => debug(e));

window.addEventListener('pageshow', () => {
  if (location.search) {
    const query = Object.fromEntries(location.search.substring(1).split('&').map(q => q.split('=')));
    if (query['q']) {
      input.value = decodeURIComponent(query['q']);
      load('go' in query);
    }
  }
});

input.addEventListener('input', () => load());
input.addEventListener('keypress', e => {
  if (e.key === 'Enter') {
    options.firstChild?.firstChild?.click();
  }
});

async function load(go) {
  if (input.value.length <= 1) {
    clear();
  } else {
    try {
      await query('/query', async entries => {
        if (go && entries.length === 1) {
          document.location.replace(entries[0][0].link);
        } else {
          clear();
          addEntries(entries);
          await query('/extended', entries => addEntries(entries, true));
        }
      });
    } catch (e) {
      debug(e);
    }
  }
}

async function query(path, consumer) {
  const {query, entries} = await fetchJson(path + '/' + input.value);
  if (query === input.value) {
    consumer(entries);
  }
}

async function fetchJson(url) {
  const res = await fetch(url);
  return await res.json();
}

function addEntries(entries, top) {
  entries.forEach(entry => {
    const op = document.createElement('div');
    options.insertBefore(op, top ? options.firstChild : null);
    entry.forEach(({link, word}) => {
      const en = document.createElement('span');
      en.appendChild(document.createTextNode(word));
      en.onclick = () => location.href = link;
      op.appendChild(en);
    });
  });
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
