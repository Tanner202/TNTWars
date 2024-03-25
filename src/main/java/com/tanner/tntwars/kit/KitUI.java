package com.tanner.tntwars.kit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class KitUI {

    public KitUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, ChatColor.BLUE + "Kit Selection");

        for (KitType type : KitType.values()) {
            ItemStack itemStack = new ItemStack(type.getMaterial());
            ItemMeta isMeta = itemStack.getItemMeta();
            isMeta.setDisplayName(type.getDisplay());
            isMeta.setLore(Arrays.asList(type.getDescription()));
            isMeta.setLocalizedName(type.name());
            itemStack.setItemMeta(isMeta);

            gui.addItem(itemStack);
        }


        player.openInventory(gui);
    }
}
