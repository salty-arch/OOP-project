(function() {
  var container = document.querySelector('.nav-pill-container');
  if (!container) return;

  var cursor = container.querySelector('.nav-pill-cursor');
  var items = container.querySelectorAll('.nav-pill-item');

  function updateCursor(item) {
    if (!item) {
      cursor.style.opacity = '0';
      return;
    }
    var rect = item.getBoundingClientRect();
    var cRect = container.getBoundingClientRect();
    cursor.style.left = (rect.left - cRect.left) + 'px';
    cursor.style.width = rect.width + 'px';
    cursor.style.opacity = '1';
  }

  items.forEach(function(item) {
    item.addEventListener('mouseenter', function() {
      updateCursor(item);
    });
  });

  container.addEventListener('mouseleave', function() {
    var active = container.querySelector('.nav-pill-item.active');
    if (active) {
      updateCursor(active);
    } else {
      cursor.style.opacity = '0';
    }
  });

  var active = container.querySelector('.nav-pill-item.active');
  if (active) {
    setTimeout(function() { updateCursor(active); }, 10);
  }
})();
