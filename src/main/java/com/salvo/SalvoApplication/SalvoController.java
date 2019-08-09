package com.salvo.SalvoApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Id;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api") // Gestión de rutas
public class SalvoController {

    @Autowired // Gestión de repositorio desde Spring
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private SalvoRepository salvoRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private PlayerRepository playerRepository;

    ///////////// ENDPOINTS

    // Endpoint /api/games 
    @RequestMapping("/games")
    public Map<String,Object> getLoggedPlayer(Authentication authentication){
        Map<String,Object> dto = new LinkedHashMap<>();
        authentication =  SecurityContextHolder.getContext().getAuthentication();
        Player authenticatedPlayer = getAuthentication(authentication);
        if(authenticatedPlayer == null)
            dto.put("player", "GUEST");
        else
            dto.put("player",loggedPlayerDTO(authenticatedPlayer));
        dto.put("games", getAll()); // Invoca al método que nos devuelve todos los juegos
        return dto;
    }

    // Spring Authentication
    private Player getAuthentication(Authentication authentication){
        if(authentication == null || authentication instanceof AnonymousAuthenticationToken){
            return null;
        } else {
            return (playerRepository.findByUserName(authentication.getName()));
        }
    }

    public Map<String,Object> loggedPlayerDTO(Player player){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", player.getId());
        dto.put("name",player.getUserName());
        return dto;
    }

    public List<Object> getAll() {
        return gameRepository.findAll()
                .stream() // Método de String que gestiona el array y nos brinda métodos como .map y .collect
                .map(game -> gameDTO(game))
                .collect(Collectors.toList());
    }

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {
        if (authentication == null) {
            return new ResponseEntity<>(MakeMap("error", "Unauthorized"), HttpStatus.UNAUTHORIZED);
        } else {
            Game game = gameRepository.save(new Game());
            GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(game, getAuthentication(authentication)));
            return new ResponseEntity<>(MakeMap("gpId", gamePlayer.getId()), HttpStatus.CREATED);
        }
    }

    @RequestMapping(path = "game/{id}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(Authentication authentication, @PathVariable Long id) {
        if (authentication.getName().isEmpty()) {
            return new ResponseEntity<>(MakeMap("error", "No Player logged in"), HttpStatus.UNAUTHORIZED);
        }
        Game gameToJoin = gameRepository.getOne(id);
        if (gameToJoin == null) {
            return new ResponseEntity<>(MakeMap("error", "No such game"), HttpStatus.FORBIDDEN);
        }
        if (gameToJoin.getGamePlayers().size() > 2) {
            return new ResponseEntity<>(MakeMap("error", "Game is full"), HttpStatus.FORBIDDEN);
        }
        GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(gameToJoin, getAuthentication(authentication)));
        gameToJoin.addGamePlayer(gamePlayer);
        return new ResponseEntity<>(MakeMap("gpId", gamePlayer.getId()), HttpStatus.CREATED);
    }

    @RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> setShips(Authentication authentication, @PathVariable Long gamePlayerId,
                                                           @RequestBody List<Ship> ships) {
        GamePlayer gamePlayer = gamePlayerRepository.findOne(gamePlayerId);
        if (getAuthentication(authentication) == null) {
            return new ResponseEntity<>(MakeMap("error", "No Player logged in"), HttpStatus.UNAUTHORIZED);
        } if (gamePlayer.getShips().size() > 0 || gamePlayer.getShips().size() > 5) {
            return new ResponseEntity<>(MakeMap("error", "Ships already placed"), HttpStatus.FORBIDDEN);
        }
        // Otherwise, the ships should be added to the game player and saved.
        ships.stream()
              .forEach(ship -> {gamePlayer.addShip(ship); ship.setGamePlayer(gamePlayer); shipRepository.save(ship);
              });
        return new ResponseEntity<>(MakeMap("created", "Ships added and saved"), HttpStatus.CREATED);
    }

    @RequestMapping(path ="/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> createUser(@RequestParam String name, @RequestParam String pwd){
        if (name.isEmpty()) {
            return new ResponseEntity<>(MakeMap("error","No name given"), HttpStatus.FORBIDDEN);
        }
        Player newPlayer = playerRepository.findByUserName(name);
        if (newPlayer != null) {
            return new ResponseEntity<>(MakeMap("error", "Username already exists"), HttpStatus.FORBIDDEN);
        }
        Player player = playerRepository.save(new Player(name, pwd));
        return new ResponseEntity<>(MakeMap("Player added", player.getUserName()), HttpStatus.CREATED);
    }

    private Map<String, Object> MakeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    // Endpoint /api/game_view/nn
    @RequestMapping(path = "/game_view/{gamePlayerId}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getGameView(Authentication authentication, @PathVariable Long gamePlayerId) {
        Map<String, Object> dto = new LinkedHashMap<>();
        Player loggedPlayer = getAuthentication(authentication);

        if (loggedPlayer == null) {
            return new ResponseEntity<>(MakeMap("error", "No player logged in"), HttpStatus.FORBIDDEN);
        } if (gamePlayerId > numberGamePlayers()) {
            return new ResponseEntity<>(MakeMap("error", "No such GamePlayer"), HttpStatus.FORBIDDEN);
        }

        GamePlayer gamePlayer = gamePlayerRepository.getOne(gamePlayerId);
        if (gamePlayer.getPlayer().getId() != loggedPlayer.getId()) {
            return new ResponseEntity<>(MakeMap("error", "Unauthorized"), HttpStatus.UNAUTHORIZED);
        }
        dto = gameViewDTO(gamePlayer.getGame(), gamePlayer);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    public int numberGamePlayers() {
        int i = 0;
        for (GamePlayer gamePlayer : gamePlayerRepository.findAll()) {
            i++;
        }
        return i;
    }

    // Endpoint /api/ships_view
    @RequestMapping("/ships_view")
	public List<Map<String, Object>> getShips(){
		return shipRepository.findAll()
				.stream()
				.map(ship -> shipDTO(ship))
				.collect(Collectors.toList());
	}

	// Endpoint /api/leaderBoard
	@RequestMapping("/leaderBoard")
    public List<Map<String, Object>> getLeaderBoard() {
        return playerRepository.findAll()
                .stream()
                .map(player -> scoreDTO(player))
                .collect(Collectors.toList());
    }

    ///////////// GAME

    // Game se mapea para pasar a DTO (Data Transfer Object)
    private Map<String, Object> gameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<>(); // Instancia de Map vacía
        dto.put("id", game.getId());
        dto.put("creationDate", Date.from(game.getCreationDate().atZone(ZoneId.systemDefault()).toInstant())); // Método que convierte LocalDateTime a Date
        dto.put("players", getGamePlayersList(game.getGamePlayers()));
        dto.put("score", getPlayerScore(game.getScores()));
        return dto;
    }

    ///////////// GAMEPLAYERS

    // Se genera un mapa de Game Players (DTO) para llevar al front-end
    private Map<String, Object> gamePlayersDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", gamePlayer.getId());
        dto.put("player", playerDTO(gamePlayer.getPlayer()));
        dto.put("joinDate", Date.from(gamePlayer.getJoinDate().atZone(ZoneId.systemDefault()).toInstant()));
        return dto;
    }
    
    // Se genera una lista de Game Players y se la mapea para convertir en DTO
    private List<Object> getGamePlayersList(List<GamePlayer> gamePlayers) {
        return gamePlayers.stream()
                        .map(gamePlayer -> gamePlayersDTO(gamePlayer))
                        .collect(Collectors.toList());
    }

    ///////////// GAME VIEW

    // Se genera un mapa del Game View que muestre los datos de una partida
    private Map<String, Object> gameViewDTO(Game game, GamePlayer gamePlayer) {
        Map<String, Object> dtoGame = new LinkedHashMap<>();
        dtoGame.put("idGame", game.getId());
        dtoGame.put("creationDate", game.getCreationDate());
        dtoGame.put("gamePlayers", getGamePlayersList(game.getGamePlayers()));
        dtoGame.put("ships", getShipLocation(gamePlayer.getShips()));
        dtoGame.put("salvoes", getAllSalvo(game));
        return dtoGame;
    }

    ///////////// PLAYERS

    // Mapeo los Players
    private Map<String, Object> playerDTO(Player player) {
        Map<String, Object> playerDTO = new LinkedHashMap<>();
        playerDTO.put("idPlayer", player.getId());
        playerDTO.put("userName", player.getUserName());
        playerDTO.put("totalScore", player.getTotalScore());
        playerDTO.put("won", player.getWon());
        playerDTO.put("lost", player.getLost());
        playerDTO.put("tied", player.getTied());
        return playerDTO;
    }

    // Genero una lista de Players
    private List<Object> getPlayersList(List<Player> playersList) {
        return playersList.stream()
                .map(player -> playerDTO(player))
                .collect(Collectors.toList());
    }

    ///////////// SHIP

    // Se mapea Ship para mostrar su type y locaciones
	private Map<String, Object> shipDTO(Ship ship){		
		Map<String, Object> dtoShip = new LinkedHashMap<String, Object>();
		dtoShip.put("shipType", ship.getShipType());
		dtoShip.put("locations", ship.getShipLocations());
		dtoShip.put("player", ship.getGamePlayer().getPlayer().getId());
        return dtoShip;
    }

    // Se genera una lista mapeando Ship
    private List<Map<String, Object>> getShipLocation(List<Ship> ships){
        return ships.stream()
                .map(ship -> shipDTO(ship))
                .collect(Collectors.toList());
    }

    ///////////// SALVO

    // Se genera una lista de todos los Salvoes
    private List<Map<String, Object>> getAllSalvo(Game game) {
        List<Map<String, Object>> salvoList = new ArrayList<>();
        game.getGamePlayers().forEach(gamePlayer -> salvoList.addAll(getSalvoLocation(gamePlayer.getSalvo())));
        return salvoList;
    }

    // Se genera una lista de salvoLocations
    private List<Map<String, Object>> getSalvoLocation(List<Salvo> salvoList) {
        return salvoList.stream()
                    .map(salvo -> salvoDTO(salvo))
                    .collect(Collectors.toList());
    }

    // Se mapea Salvo para mostrar sus atributos
    private Map<String, Object> salvoDTO(Salvo salvo) {
        Map<String, Object> salvoDTO = new LinkedHashMap<>();
        salvoDTO.put("player", salvo.getGamePlayer().getPlayer().getId());
        salvoDTO.put("turn", salvo.getTurn());
        salvoDTO.put("locations", salvo.getSalvoLocations());
        return salvoDTO;
    }

    ///////////// SCORE

    private Map<String, Object> scoreDTO(Player player) {
        Map<String, Object> scoreDTO = new LinkedHashMap<>();
        scoreDTO.put("player", player.getUserName());
        //scoreDTO.put("total", player.getTotalScore());
        scoreDTO.put("won", player.getWon());
        scoreDTO.put("lost", player.getLost());
        scoreDTO.put("tied", player.getTied());
        return scoreDTO;
    }

    // Genero una lista de todos los Scores
    private List<Map<String, Object>> getAllScore(Game game) {
        List<Map<String, Object>> gameList = new ArrayList<>();
        return gameList;
    }

    private List<Map<String, Object>> getPlayerScore(List<Score> scoreList){
        return scoreList.stream()
                .map(score -> playerScoreDTO(score))
                .collect(Collectors.toList());
    }

    private Map<String, Object> playerScoreDTO(Score score) {
        Map<String, Object> playerScoreDTO = new LinkedHashMap<>();
        playerScoreDTO.put("playerId", score.getPlayer().getId());
        playerScoreDTO.put("player", score.getPlayer().getUserName());
        playerScoreDTO.put("score", score.getScore());
        playerScoreDTO.put("finishDate", Date.from(score.getFinishDate().atZone(ZoneId.systemDefault()).toInstant()));
        return playerScoreDTO;
}
}
