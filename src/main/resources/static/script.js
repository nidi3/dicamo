window.addEventListener('error', e => debug(e));

let last = 0;
document.documentElement.addEventListener('click', async () => {
  const now = Date.now();
  if (now - last < 500) {
    const sel = selection();
    sel.selectWord();
    const word = sel.getWord();
    const {_, entries} = await fetchJson('/query/' + word);
    location.href = entries.length === 1 ? entries[0][0].link : '/?q=' + word;
  }
  last = now;

  function isWordChar(c) {
    return c.toLowerCase() !== c.toUpperCase() || c === '·';
  }

  function selection() {
    const s = window.getSelection();
    s.collapseToStart();
    let node = s.anchorNode;
    let offset = s.anchorOffset;
    if (offset === node.textContent.length) {
      node = s.anchorNode.nextSibling.firstChild;
      offset = 0;
    }
    const text = node.textContent;
    const pos = text.indexOf('\'', offset);
    if (pos >= 0 && pos < offset + 3) {
      offset = pos + 1;
    }
    const start = wordStart();
    const end = wordEnd();

    return {
      getWord() {
        return text.substring(start, end);
      },
      selectWord() {
        window.getSelection().setBaseAndExtent(node, start, node, end);
      }
    };

    function wordStart() {
      let start = offset;
      while (start >= 0) {
        if (!isWordChar(text[start])) {
          break;
        }
        start--;
      }
      return start + 1;
    }

    function wordEnd() {
      let end = offset;
      while (end < text.length) {
        if (!isWordChar(text[end])) {
          break;
        }
        end++;
      }
      return end;
    }
  }

});

async function say(elem) {
  const text = elem.parentNode.parentNode.textContent.replaceAll(/\s+/g, ' ');
  const lines = splitLines(text);
  const audios = await Promise.all(lines.map(loadAudio));
  play(0);

  function play(i) {
    if (i < audios.length) {
      audios[i].addEventListener('ended', () => play(i + 1));
      audios[i].play();
    }
  }

  function splitLines(text) {
    const lines = [];
    let line = '';
    const words = text.split(' ');
    for (const word of words) {
      if ((line + word).length > 200) {
        lines.push(line);
        line = '';
      }
      line += word + ' ';
    }
    lines.push(line);
    return lines;
  }

  function loadAudio(line) {
    return new Audio('http://translate.google.com/translate_tts?q=' + encodeURIComponent(line) + '&tl=ca&client=tw-ob');
  }
}

function debug(s) {
  const out = document.getElementById('out');
  out.appendChild(document.createTextNode(s + ' '));
}

async function fetchJson(url) {
  const res = await fetch(url);
  return await res.json();
}
