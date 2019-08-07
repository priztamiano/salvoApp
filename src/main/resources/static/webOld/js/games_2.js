$(function() {
    loadData()
});
function updateViewGames(data) {
  var htmlList = data.map(function (game) {
      return  '<li class="list-group-item">' + new Date(game.date).toLocaleString() + ' ' + game.players.map(function(p) { return p.player.email}).join(', ')  +'</li>';
  }).join('');
  document.getElementById("game-list").innerHTML = htmlList;
}
function updateViewLBoard(data) {
  var htmlList = data.map(function (score) {
      return  '<tr><td>' + score.email + '</td>'
              + '<td>' + score.total + '</td>'
              + '<td>' + score.won + '</td>'
              + '<td>' + score.lost + '</td>'
              + '<td>' + score.tie + '</td></tr>';
  }).join('');
  document.getElementById("leader-list").innerHTML = htmlList;
}
function loadData() {
  $.get("/api/games")
    .done(function(data) {
      updateViewGames(data);
    })
    .fail(function( jqXHR, textStatus ) {
      alert( "Failed: " + textStatus );
    });
  
  $.get("/api/leaderBoard")
    .done(function(data) {
      updateViewLBoard(data);
    })
    .fail(function( jqXHR, textStatus ) {
      alert( "Failed: " + textStatus );
    });
}