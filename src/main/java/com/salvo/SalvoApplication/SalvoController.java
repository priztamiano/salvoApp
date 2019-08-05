package com.salvo.SalvoApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private ScoreRepository scoreRepositry;

    ///////////// ENDPOINTS

    // Endpoint /api/games 
    @RequestMapping("/games")
    public List<Object> getAll() {
        return gameRepository.findAll()
                .stream() // Método de String que gestiona el array y nos brinda métodos como .map y .collect
                .map(game -> gameDTO(game))
                .collect(Collectors.toList());
    }

    // Endpoint /api/game_view/nn
    @RequestMapping("/game_view/{gamePlayerId}")
    public Map<String, Object> getGameView(@PathVariable Long gamePlayerId) {
        GamePlayer gamePlayer = gamePlayerRepository.findOne(gamePlayerId);
        //if (gamePlayer != null) {
            return gameViewDTO(gamePlayer.getGame(), gamePlayer);
        //}
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
        return scoreRepositry.findAll()
                .stream()
                .map(score -> scoreDTO(score))
                .collect(Collectors.toList());
    }

    ///////////// GAME

    // Game se mapea para pasar a DTO (Data Transfer Object)
    private Map<String, Object> gameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<>(); // Instancia de Map vacía
        dto.put("id", game.getId());
        dto.put("creationDate", Date.from(game.getCreationDate().atZone(ZoneId.systemDefault()).toInstant())); // Método que convierte LocalDateTime a Date
        dto.put("players", getGamePlayersList(game.getGamePlayers()));
        dto.put("score", getAllScore(game.getScores()));
        return dto;
    }

    ///////////// GAMEPLAYERS

    // Se genera un mapa de Game Players (DTO) para llevar al front-end
    private Map<String, Object> gamePlayersDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", gamePlayer.getId());
        dto.put("player", gamePlayer.getPlayer());
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

    // Mapeo Score
    private Map<String, Object> scoreDTO(Player player) {
        Map<String, Object> scoreDTO = new LinkedHashMap<>();
        List<GamePlayer> gamePlayerList = player.getGamePlayers();
        List<Score> scoresList = player.getScores();

        scoreDTO.put("playerId", player.getId());
        scoreDTO.put("score", scoresList);
        //scoreDTO.put("totalScore", getTotalScore;
        //scoreDTO.put("wins", getWins(score.getPlayer()));
        scoreDTO.put("finishDate", LocalDateTime.now());
        return scoreDTO;
    }

    // Genero una lista de todos los Scores
    private List<Double> getAllScore(List<Score> scoreList) {
        return scoreList.stream()
                .map(score -> score.getScore())
                .collect(Collectors.toList());
    }

    ///////////// MÉTODOS SCORE

    public double getWins(Player player) {
        double win = 1;
        return ++win;
    }

    public double getLosses(Player player) {
        double loss = 0;
        return ++loss;
    }

    public double getTies(Player player) {
        double tie = 0.5;
        return ++tie;
    }

    public double getTotalScore(Player player) {
        double totalScore;
        totalScore = getWins(player) + getLosses(player) + getTies(player);
        return totalScore;
    }
}
