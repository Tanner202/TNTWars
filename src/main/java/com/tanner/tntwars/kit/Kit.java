package com.tanner.tntwars.kit;

import com.tanner.tntwars.TNTWars;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.UUID;

public abstract class Kit implements Listener {

    private KitType type;
    private UUID uuid;

    public Kit(TNTWars tntWars, KitType type, UUID uuid) {
        this.type = type;
        this.uuid = uuid;

        Bukkit.getPluginManager().registerEvents(this, tntWars);
    }

    public UUID getUUID() { return uuid; }
    public KitType getType() { return type; }

    public abstract void onStart(Player player);

    public void remove() {
        HandlerList.unregisterAll(this);
    }
}
