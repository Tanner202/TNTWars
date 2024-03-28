package com.tanner.tntwars.kit.type;

import com.tanner.tntwars.TNTWars;
import com.tanner.tntwars.kit.Kit;
import com.tanner.tntwars.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class BuilderKit extends Kit {
    private TNTWars tntWars;

    private int giveBlockDelay = 100;

    private int giveBlockRunnableID;

    public BuilderKit(TNTWars tntWars, UUID uuid) {
        super(tntWars, KitType.BUILDER, uuid);
        this.tntWars = tntWars;
    }

    @Override
    public void onStart(Player player) {
        player.getInventory().addItem(new ItemStack(Material.OAK_PLANKS, 10));
        giveBlockRunnableID = Bukkit.getScheduler().scheduleSyncRepeatingTask(tntWars, () -> GivePlayerBlocks(player), giveBlockDelay, giveBlockDelay);
    }

    @Override
    public void onStop() {
        Bukkit.getScheduler().cancelTask(giveBlockRunnableID);
    }

    private void GivePlayerBlocks(Player player) {
        player.getInventory().addItem(new ItemStack(Material.OAK_PLANKS, 1));
    }
}
