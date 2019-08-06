let playersArray;
let gamesData;

$(function() {
    loadData()
});

function updateViewGames(data) {

  var htmlList = data.map(function (games) {
      return  '<li class="list-group-item">' + new Date(games.created).toLocaleString() + ' '
      + games.gamePlayers.map(function(p) { return p.player.email}).join(', ') + ' ' + games.status + '</li>';
  }).join('');
  document.getElementById("game-list").innerHTML = htmlList;
}

function getPlayers(gamesData) {

  playersArray = [];
  let playersIds = [];

  for (let i = 0; i < gamesData.length; i++) {

      for (let j = 0; j < gamesData[i].gamePlayers.length; j++) {

          if (!playersIds.includes(gamesData[i].gamePlayers[j].player.id)) {
              playersIds.push(gamesData[i].gamePlayers[j].player.id);
              let playerScoreData = {
                  "id": gamesData[i].gamePlayers[j].player.id,
                  "email": gamesData[i].gamePlayers[j].player.email,
                  "scores": [],
                  "total": 0.0
              };
              playersArray.push(playerScoreData);
          }
      }
  }
  return playersArray;
}

function addScoresToPlayersArray(playersArray) {

  for (let i = 0; i < gamesData.length; i++) {

      for (let j = 0; j < gamesData[i].scores.length; j++) {

          let scorePlayerId = gamesData[i].scores[j].playerID;

          for (let k = 0; k < playersArray.length; k++) {

              if (playersArray[k].id == scorePlayerId) {
                  playersArray[k].scores.push(gamesData[i].scores[j].score);
                  playersArray[k].total += gamesData[i].scores[j].score;
              }
          }
      }
  }
}

function showScoreBoard(playersArray) {

  playersArray.sort(function (a, b) {
      return b.total - a.total;
  });

  let table = "#leader-list";
  $(table).empty();

  for (let m = 0; m < playersArray.length; m++) {
      let countWon = 0;
      let countLost = 0;
      let countTied = 0;

      if (playersArray[m].scores.length > 0) {

          for (let n = 0; n < playersArray[m].scores.length; n++) {
              if (playersArray[m].scores[n] == 0.0) {
                  countLost++;
              } else if (playersArray[m].scores[n] == 0.5) {
                  countTied++;
              } else if (playersArray[m].scores[n] == 1.0) {
                  countWon++;
              }
          }

          let row = $('<tr></tr>').appendTo(table);
          $('<td>' + playersArray[m].email + '</td>').appendTo(row);
          $("<td class='textCenter'>" + playersArray[m].total.toFixed(1) + '</td>').appendTo(row);
          $("<td class='textCenter'>" + countWon + '</td>').appendTo(row);
          $("<td class='textCenter'>" + countLost + '</td>').appendTo(row);
          $("<td class='textCenter'>" + countTied + '</td>').appendTo(row);
      }
  }
}

function loadData() {
  $.get("/api/games")
    .done(function(data) {
      updateViewGames(data);
      gamesData = data;
      addScoresToPlayersArray(getPlayers(gamesData));
      showScoreBoard(playersArray);
    })
    .fail(function( jqXHR, textStatus ) {
      alert( "Failed: " + textStatus );
    });
}