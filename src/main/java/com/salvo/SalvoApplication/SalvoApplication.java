package com.salvo.SalvoApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import java.time.LocalDateTime;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository,
									  GameRepository gameRepository,
									  GamePlayerRepository gamePlayerRepository) {
		return(String... args) -> {
			Game game1 = new Game();
			Player player1 = new Player("sarasa@outlook.com", "loremIpsum234");
			GamePlayer gamePlayer1 = new GamePlayer(game1, player1, LocalDateTime.now());

			Game game2 = new Game();
			Player player2 = new Player("priz@gmail.com", "kittenS0123");
			GamePlayer gamePlayer2 = new GamePlayer(game2, player2, LocalDateTime.now().plusHours(2));

			gameRepository.save(game1);
			gameRepository.save(game2);

			playerRepository.save(player1);
			playerRepository.save(player2);

			gamePlayerRepository.save(gamePlayer1);
			gamePlayerRepository.save(gamePlayer2);
		};
	}

}
