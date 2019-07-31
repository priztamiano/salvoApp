package com.salvo.SalvoApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository,
									  GameRepository gameRepository,
									  GamePlayerRepository gamePlayerRepository,
									  ShipRepository shipRepository) {
		return(String... args) -> {
			Game game1 = new Game();
			LocalDateTime date1 = LocalDateTime.now();
			game1.setCreationDate(date1);

			Game game2 = new Game();
			LocalDateTime date2 = LocalDateTime.now().plusHours(1);
			game1.setCreationDate(date2);

			Game game3 = new Game();
			LocalDateTime date3 = LocalDateTime.now().plusHours(2);
			game1.setCreationDate(date3);

			Player player1 = new Player("sarasa@outlook.com", "loremIpsum234");
			Player player2 = new Player("priz@gmail.com", "kittenS0123");
			Player player3 = new Player("12345@gmail.com", "666");
			Player player4 = new Player("6789@gmail.com", "333");

			GamePlayer gamePlayer1 = new GamePlayer(game1, player1);
			GamePlayer gamePlayer2 = new GamePlayer(game1, player2);
			GamePlayer gamePlayer3 = new GamePlayer(game2, player3);
			GamePlayer gamePlayer4 = new GamePlayer(game2, player4);

			// Definición de Locaciones en forma de Lista (devuelve un array)
			List<String> location1 = new ArrayList<>();
			location1.add("A2");
			location1.add("A3");
			location1.add("A4");

			List<String> location2 = new ArrayList<>();
			location2.add("F4");
			location2.add("F5");
			location2.add("F6");
			location2.add("F7");

			List<String> location3 = new ArrayList<>();
			location3.add("C1");
			location3.add("C2");
			location3.add("C3");
			location3.add("C4");

			String shipType1 = "Submarine";
			String shipType2 = "Boat";
			String shipType3 = "Other Ship";

			Ship ship1 = new Ship();
			ship1.setShipLocations(location1);
			ship1.setShipType(shipType1);

			Ship ship2 = new Ship();
			ship2.setShipLocations(location2);
			ship2.setShipType(shipType2);

			Ship ship3 = new Ship();
			ship3.setShipLocations(location3);
			ship3.setShipType(shipType3);

			Ship ship4 = new Ship();
			ship4.setShipLocations(location1);
			ship4.setShipType(shipType2);

			gamePlayer1.addShip(ship1);
			gamePlayer1.addShip(ship2);
			gamePlayer2.addShip(ship3);
			gamePlayer2.addShip(ship4);

			gameRepository.save(game1);
			gameRepository.save(game2);
			gameRepository.save(game3);

			playerRepository.save(player1);
			playerRepository.save(player2);
			playerRepository.save(player3);
			playerRepository.save(player4);

			gamePlayerRepository.save(gamePlayer1);
			gamePlayerRepository.save(gamePlayer2);
			gamePlayerRepository.save(gamePlayer3);
			gamePlayerRepository.save(gamePlayer4);

			shipRepository.save(ship1);
			shipRepository.save(ship2);
			shipRepository.save(ship3);
			shipRepository.save(ship4);

		};
	}

}
