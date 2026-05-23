(function() {
  var interactive = document.querySelector('.gradient-bg');
  if (!interactive) return;

  var orb = document.querySelector('.interactive-orb');
  if (!orb) return;

  var curX = 0, curY = 0, tgX = 0, tgY = 0;

  document.addEventListener('mousemove', function(e) {
    tgX = e.clientX;
    tgY = e.clientY;
  });

  function animate() {
    curX += (tgX - curX) / 20;
    curY += (tgY - curY) / 20;
    orb.style.transform = 'translate(' + Math.round(curX) + 'px, ' + Math.round(curY) + 'px)';
    requestAnimationFrame(animate);
  }

  animate();
})();
