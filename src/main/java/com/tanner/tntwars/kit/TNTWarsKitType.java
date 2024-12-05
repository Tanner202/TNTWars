package com.tanner.tntwars.kit;

import com.tanner.minigames.Minigames;
import com.tanner.minigames.kit.KitType;
import com.tanner.tntwars.TNTWars;
import com.tanner.tntwars.kit.type.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

public enum TNTWarsKitType implements KitType {
    LAST_CHANCE(ChatColor.GREEN + "Last Chance", Material.EMERALD, "An upwards dash when in a sticky situation"),
    BUILDER(ChatColor.BLUE + "Builder", Material.OAK_PLANKS, "Ability to place a limited amount of blocks"),
    SNEAKY(ChatColor.DARK_PURPLE + "Sneaky", Material.INK_SAC, "Ability to go invisible for a short duration"),
    DEFLECTOR(ChatColor.DARK_GREEN + "Deflector", Material.SHIELD, "Ability to hit tnt back at opponents"),
    BLINDER(ChatColor.DARK_GRAY + "Blinder", Material.ENDER_EYE, "Ability to blind opponents"),
    FREEZER(ChatColor.AQUA + "Freezer", Material.ICE, "Ability to freeze opponents in place");

    private String display, description;
    private Material material;

    TNTWarsKitType(String display, Material material, String description) {
        this.display = display;
        this.material = material;
        this.description = description;
    }

    public String getDisplay() { return display; }
    public Material getMaterial() { return material; }
    public String getDescription() { return description; }
    public String getName() { return name(); }
}