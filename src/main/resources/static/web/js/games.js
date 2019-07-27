$(function() {
    loadData()
});

function updateView(data) {
    var htmlList = data.map(function (game) {
         return  '<li>' + new Date(game.creationDate).toLocaleString() + ' ' + game.players.map(function(p) { return p.player}).join(',') +'</li>';
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