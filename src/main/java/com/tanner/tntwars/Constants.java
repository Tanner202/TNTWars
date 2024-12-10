package com.tanner.tntwars;

import org.bukkit.NamespacedKey;

public class Constants {

    public static NamespacedKey SNEAK_ITEM;

    public static void initializeConstants(TNTWars tntWars) {
        SNEAK_ITEM = new NamespacedKey(tntWars, "Sneak_Item");
    }

}
