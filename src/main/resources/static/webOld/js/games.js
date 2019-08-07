var stats = {
    "positioning": []
}

//Función JQuery para traer los datos
function getDta() {
 stats.positioning = [];
$.get("/api/games").done(function(data){
    app.games = data.games;
    app.player = data.player;
    createStatsObject();
    change();
})
}
$(getDta());

//Creación del objeto JSON dinámico
function createStatsObject() {
  for (i in app.games) {
    for (j in app.games[i].players) {
      var examine = stats.positioning.find(function(player){ return player.id == app.games[i].players[j].player.id });
        if (examine == undefined) {
          var statsObject = new Object();
          statsObject.userName = app.games[i].players[j].player.userName;
          statsObject.id = app.games[i].players[j].player.id;
          statsObject.total = 0;
          statsObject.won = 0;
          statsObject.lost = 0;
          statsObject.tied = 0;
          stats.positioning.push(statsObject);
   }
          evaluate(app.games[i].score, app.games[i].players[j].player.id);
  }
 }
}


//Función que realiza el cálculo de la tabla de puntos
function evaluate (score, playerId) {
    stats.positioning.map(function(positioning) {
        if (playerId == positioning.id) {
            positioning.score += score;
            if (score == 1.0) {
               positioning.won += 1
            }
            else if (score == 0.0) {
               positioning.lost += 1
            }
            else if (score == 0.5) {
               positioning.tied += 1
            }
  }
 })
}

//Obtención de datos con VUE
var app = new Vue ({
	el: '#app',
	data: {
        games: [],
        stats: stats,
        player: []
}});

//Log in
function logIn () {
    $.post("/api/login", { userName: $('#user').val(), password: $('#password').val() })
         .done(function(data) {
              $("#msg").html("");
              getDta()
              console.log('logged in!');
         })
         .fail(function() {
              $("#msg").html("Wrong user or password").addClass("spanColor");
         })
}

$("#btn").click(logIn);

//Log out
$("#btn-logout").click(function(){
  $.post("/api/logout")
        .done(function(data) {
            getDta();
            console.log('logged out!');
        })
})

//Ocultar Log in o Log out
function change() {
    if (app.player == 'GUEST') {
        $('#logForm').removeClass('hide');
        $('#btn-logout').addClass('hide');

    } else {
        $('#btn-logout').removeClass('hide');
        $('#logForm').addClass('hide');
    }
}


/*function change (a,b,c,d) {

	$(a).toggle();
	$(b).toggle();
	$(c).toggle();
    $(d).toggle();

}*/


//Sign up
$("#btn-sign-up").click(function() {
    $.post("/api/players", { userName: $('#user').val(), password: $('#password').val() })
            .done(function(data) {
                  logIn()
                  $("#msg").html("");
                  console.log('Signed up!');
            })
            .fail(function() {
                 $("#msg").html("Sign up fail").addClass("spanColor");
                 //alert('Sign up fail');
            })

})


