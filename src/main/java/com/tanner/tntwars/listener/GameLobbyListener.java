package com.tanner.tntwars.listener;

import com.tanner.tntwars.GameState;
import com.tanner.tntwars.TNTWars;
import com.tanner.tntwars.instance.Arena;
import com.tanner.tntwars.kit.KitType;
import com.tanner.tntwars.kit.KitUI;
import com.tanner.tntwars.team.Team;
import com.tanner.tntwars.team.TeamUI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class GameLobbyListener implements Listener {

    private TNTWars tntWars;

    public GameLobbyListener(TNTWars tntWars) {
        this.tntWars = tntWars;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Arena arena = tntWars.getArenaManager().getArena(player);
        if (arena == null) return;
        e.setCancelled(true);

        if (e.getCurrentItem() != null && e.getView().getTitle().contains("Team Selection")) {
            ItemMeta itemMeta = e.getCurrentItem().getItemMeta();
            if (itemMeta == null) return;

            Team team = null;
            for (Team t : Team.values()) {
                if (t.name().equals(itemMeta.getLocalizedName())) {
                    team = t;
                }
            }
            if (team == null) return;

            if (arena.getTeam(player) == team) {
                player.sendMessage(ChatColor.RED + "You are already on this team.");
            } else {
                player.sendMessage(ChatColor.AQUA + "You are now on " + team.getDisplay() + ChatColor.AQUA + " team.");
                arena.setTeam(player, team);
            }

            player.closeInventory();
        } else if (e.getCurrentItem() != null && e.getView().getTitle().contains("Kit Selection")) {
            ItemMeta itemMeta = e.getCurrentItem().getItemMeta();
            if (itemMeta == null) return;

            KitType kitType = null;
            for (KitType type : KitType.values()) {
                if (type.name().equals(itemMeta.getLocalizedName())) {
                    kitType = type;
                }
            }
            if (kitType == null) return;


            KitType activated = arena.getKit(player);
            if (activated != null && activated == kitType) {
                player.sendMessage(ChatColor.RED + "You already have this kit selected.");
            } else {
                player.sendMessage(ChatColor.GREEN + "You have selected the " + kitType.getDisplay() + ChatColor.GREEN + " kit!");
                arena.setKit(player.getUniqueId(), kitType);
            }

            player.closeInventory();
        }
        player.updateInventory();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Arena arena = tntWars.getArenaManager().getArena(player);

        if (arena == null) return;
        if (!arena.getState().equals(GameState.RECRUITING) && !arena.getState().equals(GameState.COUNTDOWN)) return;

        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemMeta itemMeta = e.getPlayer().getInventory().getItemInMainHand().getItemMeta();
            if (itemMeta == null) return;

            if (itemMeta.getLocalizedName().equals("Team Selection")) {
                e.setCancelled(true);
                player.updateInventory();
                new TeamUI(arena, player);
            } else if (itemMeta.getLocalizedName().equals("Kit Selection")) {
                e.setCancelled(true);
                new KitUI(player);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        Arena arena = tntWars.getArenaManager().getArena(e.getPlayer());
        if (arena == null) return;

        e.setCancelled(true);
    }
}
