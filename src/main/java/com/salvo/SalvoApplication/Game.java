package com.salvo.SalvoApplication;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity // Genera una tabla para esta clase
public class Game {

    @Id // Genera un único ID
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native") // JPA genera un valor único
    @GenericGenerator(name = "native", strategy = "native") // Hiibernate genera un valor único (Convención, puede funcionar sin H2)
    private long id;

    private LocalDateTime creationDate;

    @JsonIgnore // Para el loop infinito
    @OneToMany(mappedBy = "game", fetch=FetchType.EAGER)
    List<GamePlayer> gamePlayers = new ArrayList<>();

    public Game() {
        this.creationDate =  LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setGame(this);
        this.gamePlayers.add(gamePlayer);
    }

    @JsonIgnore
	public List<GamePlayer> getGamePlayers(){
		return gamePlayers;
    }
    
}
