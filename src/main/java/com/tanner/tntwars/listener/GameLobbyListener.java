package com.tanner.tntwars.listener;

import com.tanner.tntwars.GameState;
import com.tanner.tntwars.TNTWars;
import com.tanner.tntwars.instance.Arena;
import com.tanner.tntwars.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GameLobbyListener implements Listener {

    private TNTWars tntWars;

    public GameLobbyListener(TNTWars tntWars) {
        this.tntWars = tntWars;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory() != null && e.getCurrentItem() != null &&e.getView().getTitle().contains("Team Selection")) {
            Team team = Team.valueOf(e.getCurrentItem().getItemMeta().getLocalizedName());
            Player player = (Player) e.getWhoClicked();

            Arena arena = tntWars.getArenaManager().getArena(player);
            if (arena != null) {
                if (arena.getTeam(player) == team) {
                    player.sendMessage(ChatColor.RED + "You are already on this team.");
                } else {
                    player.sendMessage(ChatColor.AQUA + "You are now on " + team.getDisplay() + ChatColor.AQUA + " team.");
                    arena.setTeam(player, team);
                }
            }

            player.closeInventory();
            e.setCancelled(true);
        }
    }
}
