package com.salvo.SalvoApplication;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    private String shipType;

    @ElementCollection
    @Column(name = "shipLocation")
    private List<String> shipLocations = new ArrayList<>();

    public Ship() {}

    public Ship(String shipType, List<String> shipLocations, GamePlayer gamePlayer) {
        this.shipType = shipType;
        this.shipLocations = shipLocations;
        this.gamePlayer = gamePlayer;
    }

    public long getId() {
        return id;
    }

    public String getShipType() {
        return shipType;
    }

    public void setShipType(String sipThype) {
        this.shipType = shipType;
    }

    public List<String> getShipLocations() {
        return shipLocations;
    }

    public void setShipLocations(List<String> shipLocations) {
        this.shipLocations = shipLocations;
    }

    public GamePlayer setGamePlayer(GamePlayer gamePlayer) {
        return gamePlayer;
    }
}
