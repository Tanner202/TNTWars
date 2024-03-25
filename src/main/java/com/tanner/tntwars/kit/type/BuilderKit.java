package com.tanner.tntwars.kit.type;

import com.tanner.tntwars.TNTWars;
import com.tanner.tntwars.kit.Kit;
import com.tanner.tntwars.kit.KitType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class BuilderKit extends Kit {
    public BuilderKit(TNTWars tntWars, UUID uuid) {
        super(tntWars, KitType.BUILDER, uuid);
    }

    @Override
    public void onStart(Player player) {
        player.getInventory().addItem(new ItemStack(Material.OAK_PLANKS, 10));
    }
}
