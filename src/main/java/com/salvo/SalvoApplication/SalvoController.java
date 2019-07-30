package com.salvo.SalvoApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api") // Gestión de rutas
public class SalvoController {

    @Autowired // Gestión de repositorio desde Spring
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private ShipRepository shipRepository;

    @RequestMapping("/games")
    public List<Object> getAll() {
        return gameRepository.findAll()
                .stream() // Método de String que gestiona el array y nos brinda métodos como .map y .collect
                .map(game -> gameDTO(game))
                .collect(Collectors.toList());
    }

    private Map<String, Object> gameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<>(); // Interface de Map
        dto.put("id", game.getId());
        dto.put("creationDate", Date.from(game.getCreationDate().atZone(ZoneId.systemDefault()).toInstant()));
        dto.put("players", getGamePlayersList(game.getGamePlayers()));
        return dto;
    }

    private List<Object> getGamePlayersList(List<GamePlayer> gamePlayers) {
        return gamePlayers
                .stream()
                .map(gamePlayer -> gamePlayersDTO(gamePlayer))
                .collect(Collectors.toList());
    }

    private Map<String, Object> gamePlayersDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", gamePlayer.getId());
        dto.put("player", gamePlayer.getPlayer());
        return dto;
    }

    @RequestMapping("/game_view/{gamePlayerId}")
    public Map<String, Object> getGameView(@PathVariable Long gamePlayerId) {
        GamePlayer gamePlayer = gamePlayerRepository.findOne(gamePlayerId);
        return getGameViewDTO(gamePlayer.getGame(), gamePlayer);
    }

    private Map<String, Object> getGameViewDTO(Game game, GamePlayer gamePlayer) {
        Map<String, Object> dtoGame = new LinkedHashMap<>();
        dtoGame.put("idGame", game.getId());
        dtoGame.put("creationDate", game.getCreationDate());
        dtoGame.put("gamePlayers", getGamePlayersList(game.getGamePlayers()));
        dtoGame.put("ships", getLocationShips(gamePlayer.getShips()));
        return dtoGame;
    }

    private List<Map<String, Object>> getGamePlayers(List<GamePlayer> gamePlayers) {		
        return gamePlayers
            .stream()
            .map(gamePlayer -> gamePlayerToDTO(gamePlayer))
            .collect(Collectors.toList());
    } 
    
    private Map<String, Object> gamePlayerToDTO(GamePlayer gamePlayer) {		
		Map<String, Object> dtoGamePlayer = new LinkedHashMap<String, Object>();		
		dtoGamePlayer.put("idGamePlayer", gamePlayer.getId());
		dtoGamePlayer.put("player", playerToDTO(gamePlayer.getPlayer()));
		dtoGamePlayer.put("joinDate", gamePlayer.getJoinDate());
		return dtoGamePlayer;
	}
		
	private Map<String, Object> playerToDTO(Player player) {		
		Map<String, Object> dtoPlayer = new LinkedHashMap<String, Object>();
		dtoPlayer.put("idPlayer", player.getId());
		dtoPlayer.put("email", player.getUserName());
		return dtoPlayer;
	}
	
	private List<Map<String, Object>> getLocationShips(List<Ship> ships){
        return ships
            .stream()
			.map(ship -> shipToDTO(ship))
			.collect(Collectors.toList());		
	}
	
	private Map<String, Object> shipToDTO(Ship ship){
		
		Map<String, Object> dtoShip = new LinkedHashMap<String, Object>();
		dtoShip.put("shipType", ship.getShipType());
		dtoShip.put("locations", ship.getShipLocations());
		return dtoShip;
	}
	
	@RequestMapping("/ships_view")
	public List<Map<String, Object>> getShips(){
		return shipRepository.findAll()
				.stream()
				.map(ship -> shipToDTO(ship))
				.collect(Collectors.toList());
	}
}
