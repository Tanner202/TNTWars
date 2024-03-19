package com.tanner.tntwars.instance;

import com.google.common.collect.TreeMultimap;
import com.tanner.tntwars.GameState;
import com.tanner.tntwars.TNTWars;
import com.tanner.tntwars.manager.ConfigManager;
import com.tanner.tntwars.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Arena {

    private TNTWars tntWars;

    private int id;
    private Location spawn;

    private GameState state;
    private List<UUID> players;
    private HashMap<UUID, Team> teams;
    private Countdown countdown;
    private Game game;

    public Arena(TNTWars tntWars, int id, Location spawn) {
        this.tntWars = tntWars;

        this.id = id;
        this.spawn = spawn;

        this.state = GameState.RECRUITING;
        this.players = new ArrayList<>();
        this.teams = new HashMap<>();
        this.countdown = new Countdown(tntWars, this);
        this.game = new Game(this);
    }

    public void start() {
        game.start();
    }

    public void reset(boolean kickPlayers) {
        if (kickPlayers) {
            Location loc = ConfigManager.getLobbySpawn();
            for (UUID uuid : players) {
                Bukkit.getPlayer(uuid).teleport(loc);
            }
            players.clear();
            teams.clear();
        }
        sendTitle("", "");
        state = GameState.RECRUITING;
        countdown.cancel();
        countdown = new Countdown(tntWars, this);
        game = new Game(this);
    }

    public void sendMessage(String message) {
        for (UUID uuid : players) {
            Bukkit.getPlayer(uuid).sendMessage(message);
        }
    }

    public void sendTitle(String title, String subtitle) {
        for (UUID uuid : players) {
            Bukkit.getPlayer(uuid).sendTitle(title, subtitle);
        }
    }

    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
        player.teleport(spawn);

        TreeMultimap<Integer, Team> teamCount = TreeMultimap.create();
        for (Team team : Team.values()) {
            teamCount.put(getTeamCount(team), team);
        }

        Team lowestPlayerTeam = (Team) teamCount.values().toArray()[0];
        setTeam(player, lowestPlayerTeam);

        player.sendMessage(ChatColor.AQUA + "You have been automatically placed on " + lowestPlayerTeam.getDisplay() + ChatColor.AQUA + " team.");

        if (state.equals(GameState.RECRUITING) && players.size() >= ConfigManager.getRequiredPlayers()) {
            countdown.start();
        }
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
        player.teleport(ConfigManager.getLobbySpawn());
        player.sendTitle("", "");

        removeTeam(player);

        if (state == GameState.COUNTDOWN && players.size() < ConfigManager.getRequiredPlayers()) {
            sendMessage(ChatColor.RED + "There are not enough players. Countdown stopped.");
            reset(false);
            return;
        }

        if (state == GameState.LIVE && players.size() < ConfigManager.getRequiredPlayers()) {
            sendMessage(ChatColor.RED + "The game has ended because too many players have left.");
            reset(false);
        }
    }

    public int getId() { return id; }

    public GameState getState() { return state; }
    public void setState(GameState state) { this.state = state; }
    public Game getGame() { return game; }

    public List<UUID> getPlayers() { return players;}
    public void setTeam(Player player, Team team) {
        removeTeam(player);
        teams.put(player.getUniqueId(), team);
    }

    public void removeTeam(Player player) {
        if (teams.containsKey(player.getUniqueId())) {
            teams.remove(player.getUniqueId());
        }
    }

    public int getTeamCount(Team team) {
        int amount = 0;
        for (Team t : teams.values()) {
            if (t == team) {
                amount++;
            }
        }
        return amount;
    }

    public Team getTeam(Player player) {
        return teams.get(player.getUniqueId());
    }
}
