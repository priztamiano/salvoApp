$(function() {
  loadData();
  $("#login-btn").click(function(){
    login();
  });
  $("#logout-btn").click(function(){
    logout();
  });
});

function updateViewGames(data) {
  var userTxt = data.player;
  var htmlList = data.games.map(function (games) {
      return  '<li class="list-group-item">' + new Date(games.creationDate).toLocaleString() + ' ' + games.players.map(function(p) { return p.player.userName}).join(', ')  +'</li>';
  }).join('');
  $("#game-list").html(htmlList);
  if(userTxt!="GUEST"){
    $("#user-info").text('Hello ' + userTxt.name + '!');
    showLogin(false);
  }
}

function updateViewLBoard(data) {
  var htmlList = data.map(function (score) {
      return  '<tr><td>' + score.player + '</td>'
              + '<td>' + score.total + '</td>'
              + '<td>' + score.won + '</td>'
              + '<td>' + score.lost + '</td>'
              + '<td>' + score.tied + '</td></tr>';
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

function login(){
  $.post("/api/login", { userName: $("#username").val(), password: $("#password").val()})
    .done(function() {
      loadData(),
      showLogin(false);
    });
}

function logout(){
  $.post("/api/logout")
    .done(function() { 
      showLogin(true);
    });
}

function showLogin(show){
  if(show){
    $("#login-panel").show();
    $("#user-panel").hide();
  } else {
    $("#login-panel").hide();
    $("#user-panel").show();
  }
}