package com.tanner.tntwars.kit.type;

import com.tanner.tntwars.TNTWars;
import com.tanner.tntwars.kit.Kit;
import com.tanner.tntwars.kit.TNTWarsKitType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.UUID;

public class FreezerKit extends Kit {

    private TNTWars tntWars;

    private int reachDistance = 100;
    private int freezeDuration = 100;

    public FreezerKit(TNTWars tntWars, UUID uuid) {
        super(tntWars, uuid);
        this.tntWars = tntWars;

        Player player = Bukkit.getPlayer(uuid);
        player.getInventory().addItem(new ItemStack(Material.ICE, 3));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (player.getUniqueId() != getKitOwnerUUID()) return;

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand.getType() == Material.ICE) {
            itemInMainHand.setAmount(itemInMainHand.getAmount() - 1);
            Vector playerFacing = player.getEyeLocation().getDirection();
            RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(player.getEyeLocation().add(playerFacing.normalize()), playerFacing, reachDistance);

            if (rayTraceResult == null) return;
            Entity hitEntity = rayTraceResult.getHitEntity();

            Vector zero = new Vector(0, 0, 0);
            if (hitEntity != null && hitEntity.getType().equals(EntityType.PLAYER)) {
                Player hitPlayer = (Player) hitEntity;
                hitPlayer.sendMessage(ChatColor.AQUA + "You have been temporarily frozen!");
                int taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(tntWars, () -> hitEntity.setVelocity(zero), 0, 1);
                Bukkit.getScheduler().runTaskLater(tntWars, () -> Bukkit.getScheduler().cancelTask(taskID), freezeDuration);
            }
        }
    }
}
