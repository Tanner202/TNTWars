package com.tanner.tntwars.kit.type;

import com.tanner.tntwars.TNTWars;
import com.tanner.tntwars.kit.Kit;
import com.tanner.tntwars.kit.KitType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.UUID;

public class DeflectorKit extends Kit {

    private TNTWars tntWars;

    private float tntLaunchPower = 1.5f;
    private float tntHeight = 0.5f;
    private int fuseTime = 45;

    private int reachDistance = 3;

    public DeflectorKit(TNTWars tntWars, UUID uuid) {
        super(tntWars, KitType.DEFLECTOR, uuid);
        this.tntWars = tntWars;
    }

    @Override
    public void onStart(Player player) {

    }

    @EventHandler
    public void onPlayerAnimation(PlayerAnimationEvent e)
    {
        Player player = e.getPlayer();
        if (!tntWars.getArenaManager().getArena(player).getKit(player).equals(KitType.DEFLECTOR)) return;
        if (!e.getAnimationType().equals(PlayerAnimationType.ARM_SWING)) return;

        Vector playerFacing = player.getEyeLocation().getDirection();
        RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(player.getEyeLocation().add(playerFacing.normalize()), playerFacing, reachDistance);

        if (rayTraceResult == null) return;
        Entity hitEntity = rayTraceResult.getHitEntity();

        if (hitEntity != null && hitEntity.getType().equals(EntityType.PRIMED_TNT)) {
            Vector heightVector = new Vector(0, tntHeight, 0);
            hitEntity.setVelocity(playerFacing.multiply(tntLaunchPower).add(heightVector));
            TNTPrimed primedTNT = (TNTPrimed) hitEntity;
            primedTNT.setFuseTicks(fuseTime);
        }
    }
}
