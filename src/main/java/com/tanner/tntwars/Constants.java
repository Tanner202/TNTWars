package com.tanner.tntwars;

import org.bukkit.NamespacedKey;

public class Constants {

    public static NamespacedKey SNEAK_ITEM;

    public Constants(TNTWars tntWars) {
        SNEAK_ITEM = new NamespacedKey(tntWars, "Sneak_Item");
    }

}
