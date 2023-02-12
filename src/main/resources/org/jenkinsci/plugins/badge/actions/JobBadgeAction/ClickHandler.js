Behaviour.register({
  "INPUT.select-all" : function(e) {
    e.onclick = function () {
      e.focus();
      e.select();
    }
  }
});
