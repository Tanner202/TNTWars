package com.tanner.tntwars.kit;

import com.tanner.tntwars.TNTWars;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.UUID;

public abstract class Kit implements Listener {
    private UUID kitOwnerUUID;

    public Kit(TNTWars tntWars, UUID uuid) {
        this.kitOwnerUUID = uuid;

        Bukkit.getPluginManager().registerEvents(this, tntWars);
    }

    public UUID getKitOwnerUUID() { return kitOwnerUUID; }

    public void onStop() {}

    public void remove() {
        HandlerList.unregisterAll(this);
        onStop();
    }
}
