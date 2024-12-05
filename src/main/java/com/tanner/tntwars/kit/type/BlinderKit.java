package com.tanner.tntwars.kit.type;

import com.tanner.tntwars.TNTWars;
import com.tanner.tntwars.kit.Kit;
import com.tanner.tntwars.kit.TNTWarsKitType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.UUID;

public class BlinderKit extends Kit {

    private float blindPotionThrowPower = 1.5f;
    private float blindPotionThrowHeight = 0.5f;
    private int blindDuration = 100;

    public BlinderKit(TNTWars tntWars, UUID uuid) {
        super(tntWars, TNTWarsKitType.BLINDER, uuid);

        Player player = Bukkit.getPlayer(uuid);
        ItemStack blindPotion = new ItemStack(Material.SPLASH_POTION, 5);
        PotionMeta blindPotionMeta = (PotionMeta) blindPotion.getItemMeta();
        blindPotionMeta.setDisplayName(ChatColor.DARK_GRAY + "Blind Potion");
        blindPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, blindDuration, 1, true, true), true);
        blindPotion.setItemMeta(blindPotionMeta);
        player.getInventory().addItem(blindPotion);
    }

    @EventHandler
    public void onProjectileLaunchEvent(ProjectileLaunchEvent e) {
        if (e.getEntity().getShooter() instanceof Player) {
            Player player = (Player) e.getEntity().getShooter();

            if (player.getUniqueId() != getKitOwnerUUID()) return;
            if (e.getEntity().getType().equals(EntityType.SPLASH_POTION)) {
                Vector playerFacing = player.getEyeLocation().getDirection();

                Vector heightVector = new Vector(0, blindPotionThrowHeight, 0);
                e.getEntity().setVelocity(playerFacing.multiply(blindPotionThrowPower).add(heightVector));
            }
        }
    }
}
