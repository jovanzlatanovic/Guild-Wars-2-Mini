package com.example.guildwars2mini;

public class ApiCoinAdapter {
    private Coin coin;

    public ApiCoinAdapter(int apiCoinValue) throws NumberFormatException {
            coin = new Coin(apiCoinValue);
    }

    public Coin getCoin() {
        return coin;
    }
}
