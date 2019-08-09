package com.salvo.SalvoApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
									  ShipRepository shipRepository,
									  SalvoRepository salvoRepository,
									  ScoreRepository scoreRepository) {
		return(String... args) -> {
			// Instancias de Game con su info correspondiente
			Game game1 = new Game();
			LocalDateTime date1 = LocalDateTime.now();
			game1.setCreationDate(date1);

			Game game2 = new Game();
			LocalDateTime date2 = LocalDateTime.now().plusHours(1);
			game1.setCreationDate(date2);

			Game game3 = new Game();
			LocalDateTime date3 = LocalDateTime.now().plusHours(2);
			game1.setCreationDate(date3);

			// Instancias de Player
			Player player1 = new Player("sarasa@outlook.com", "666");
			Player player2 = new Player("priz@gmail.com", "666");
			Player player3 = new Player("12345@gmail.com", "666");
			Player player4 = new Player("6789@gmail.com", "666");

			// Instancias de GamePlayer
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

			// Asignación de ShipTypes
			String shipType1 = "Aircraft Carrier";
			String shipType2 = "Battleship";
			String shipType3 = "Submarine";
			String shipType4 = "Destroyer";
			String shipType5 = "Patrol Boat";

			// Instancias de Ship
			Ship ship1 = new Ship(location1, shipType1);
			Ship ship2 = new Ship(location2, shipType2);
			Ship ship3 = new Ship(location3, shipType3);
			Ship ship4 = new Ship(location1, shipType4);

			// Asignación de Ship para cada gamePlayer
			gamePlayer1.addShip(ship1);
			gamePlayer1.addShip(ship2);
			gamePlayer2.addShip(ship3);
			gamePlayer2.addShip(ship4);

			// Asignación de Locaciones Salvo
			List<String> salvoLocation1 = new ArrayList<>();
			salvoLocation1.add("A4");
			salvoLocation1.add("A5");
			salvoLocation1.add("A6");

			List<String> salvoLocation2 = new ArrayList<>();
			salvoLocation2.add("A1");
			salvoLocation2.add("B1");
			salvoLocation2.add("C1");
			salvoLocation2.add("D1");

			List<String> salvoLocation3 = new ArrayList<>();
			salvoLocation3.add("C6");
			salvoLocation3.add("C7");

			List<String> salvoLocation4 = new ArrayList<>();
			salvoLocation4.add("D2");
			salvoLocation4.add("E2");
			salvoLocation4.add("F2");

			// Instancias de Salvo
			Salvo salvo1 = new Salvo(1, salvoLocation1);
			Salvo salvo2 = new Salvo(2, salvoLocation2);
			Salvo salvo3 = new Salvo(3, salvoLocation3);
			Salvo salvo4 = new Salvo(4, salvoLocation4);

			// Asignación de Salvo a cada gamePlayer
			gamePlayer1.addSalvo(salvo1);
			gamePlayer1.addSalvo(salvo3);
			gamePlayer2.addSalvo(salvo2);
			gamePlayer2.addSalvo(salvo4);

			// Instancias de Score
			Score score1 = new Score(game1, player1, 1.0, LocalDateTime.now());
			Score score2 = new Score(game1, player1, 1.0, LocalDateTime.now());
			Score score3 = new Score(game1, player2, 0.5, LocalDateTime.now());
			Score score4 = new Score(game1, player2, 0.0, LocalDateTime.now());
			Score score5 = new Score(game2, player3, 0.5, LocalDateTime.now());
			Score score6 = new Score(game2, player3, 0.0, LocalDateTime.now());
			Score score7 = new Score(game2, player4, 1.0, LocalDateTime.now());
			Score score8 = new Score(game2, player4, 0.5, LocalDateTime.now());

			// Guardado de cada Instancia a su respectivo Repository
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

			salvoRepository.save(salvo1);
			salvoRepository.save(salvo2);
			salvoRepository.save(salvo3);
			salvoRepository.save(salvo4);

			scoreRepository.save(score1);
			scoreRepository.save(score2);
			scoreRepository.save(score3);
			scoreRepository.save(score4);
			scoreRepository.save(score5);
			scoreRepository.save(score6);
			scoreRepository.save(score7);
			scoreRepository.save(score8);
		};
	}

}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository playerRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputName-> {
			Player player = playerRepository.findByUserName(inputName);
			if (player != null) {
				return new User(player.getUserName(), player.getPassword(),
						AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Unknown user: " + inputName);
			}
		});
	}
}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/web/**").permitAll()
				.antMatchers("/api/games/**").permitAll()
				.antMatchers("/api/players").permitAll()
				.antMatchers("/api/game_view/*").hasAuthority("USER")
				.antMatchers("/rest/*").permitAll()
				.anyRequest().permitAll();
		http.formLogin()
				.usernameParameter("name")
				.passwordParameter("pwd")
				.loginPage("/api/login");
		http.logout().logoutUrl("/api/logout");
		// turn off checking for CSRF tokens
		http.csrf().disable();
		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));
		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));
		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));
		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}
	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}
}
