package com.tanner.tntwars.kit;

import com.tanner.tntwars.TNTWars;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.UUID;

public abstract class Kit implements Listener {
    private TNTWarsKitType type;
    private UUID kitOwnerUUID;

    public Kit(TNTWars tntWars, TNTWarsKitType type, UUID uuid) {
        this.type = type;
        this.kitOwnerUUID = uuid;

        Bukkit.getPluginManager().registerEvents(this, tntWars);
    }

    public UUID getKitOwnerUUID() { return kitOwnerUUID; }
    public TNTWarsKitType getType() { return type; }

    public void onStop() {}

    public void remove() {
        HandlerList.unregisterAll(this);
        onStop();
    }
}
