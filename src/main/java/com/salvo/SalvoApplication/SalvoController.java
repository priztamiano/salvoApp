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
        //dtoGame.put("ships", getLocationShips(gamePlayer.getShips()));
        return dtoGame;
    }

    @RequestMapping("/ships_view")
    public List<Map<String, Object>> getShips() {

    }
}
