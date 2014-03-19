package com.stealthyone.mcb.simpleslap.backend.cooldowns;

public class Cooldown {

    private int time;

    public Cooldown(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public int countdown() {
        return time--;
    }

}