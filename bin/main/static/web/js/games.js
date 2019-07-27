$(function() {
  loadData()
});

function updateView(data) {
  let htmlList = data.map(function (games) {
      return  '<li>' + new Date(games.creationDate).toLocaleString() + ' ' + games.players.map(function(p) { 
        console.log(p.player.userName);
        return p.player.userName}).join(',')  +'</li>';
  }).join('');
document.getElementById("game-list").innerHTML = htmlList;
}

// load and display JSON sent by server for /players

function loadData() {
  $.get("/api/games")
      .done(function(data) {
        updateView(data);
      })
      .fail(function( jqXHR, textStatus ) {
        alert( "Failed: " + textStatus );
      });
}