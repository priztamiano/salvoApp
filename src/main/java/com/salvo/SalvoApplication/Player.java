package com.salvo.SalvoApplication;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @JsonIgnore
    @OneToMany(mappedBy = "player", fetch=FetchType.EAGER)
    List<GamePlayer> gamePlayers = new ArrayList<>();

    @JsonIgnore
    public List<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    List<Score> scores = new ArrayList<>();

    @JsonIgnore
    public List<Score> getScores() { return scores; }

    public void setScores(Game game) {
        this.scores = scores;
    }

    private String userName;
    private String password;

    public Player() {   }

    public Player(String userName, String password){
        this.userName = userName;
        this.password = password;
    }

    public long getId(){
        return id;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getUserName(){
        return userName;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setPlayer(this);
        this.gamePlayers.add(gamePlayer);
    }
}
