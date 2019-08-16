$(function() {
    loadData()
});

function updateView(data) {
    var htmlList = data.games.map(function (games) {
            return  '<li>' + new Date(games.creationDate).toLocaleString() + ' ' + games.gamePlayer.map(function(p) { return p.player.userName}).join(',')  +'</li>';
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