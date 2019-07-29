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
			Player player1 = new Player("sarasa@outlook.com", "loremIpsum234");
			GamePlayer gamePlayer1 = new GamePlayer(game1, player1, LocalDateTime.now());

			Game game2 = new Game();
			Player player2 = new Player("priz@gmail.com", "kittenS0123");
			GamePlayer gamePlayer2 = new GamePlayer(game2, player2, LocalDateTime.now().plusHours(1));

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

			Ship ship1 = new Ship(shipType1, location1, gamePlayer1);
			Ship ship2 = new Ship(shipType2, location2, gamePlayer2);
			Ship ship3 = new Ship (shipType3, location3, gamePlayer2);

			gameRepository.save(game1);
			gameRepository.save(game2);

			playerRepository.save(player1);
			playerRepository.save(player2);

			gamePlayerRepository.save(gamePlayer1);
			gamePlayerRepository.save(gamePlayer2);

			shipRepository.save(ship1);
			shipRepository.save(ship2);
			shipRepository.save(ship3);

			gamePlayer1.addShip(ship1);
		};
	}

}
