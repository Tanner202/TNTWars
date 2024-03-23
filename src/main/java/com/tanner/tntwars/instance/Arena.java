package com.tanner.tntwars.instance;

import com.google.common.collect.TreeMultimap;
import com.tanner.tntwars.GameState;
import com.tanner.tntwars.TNTWars;
import com.tanner.tntwars.manager.ConfigManager;
import com.tanner.tntwars.team.Team;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class Arena {

    private TNTWars tntWars;

    private int id;
    private Location spawn;
    private World world;

    private GameState state;
    private List<UUID> players;
    private HashMap<UUID, Team> teams;
    private Countdown countdown;
    private Game game;
    private boolean canJoin;

    public Arena(TNTWars tntWars, int id) {
        this.tntWars = tntWars;

        this.id = id;
        setSpawnLocation();
        this.world = spawn.getWorld();

        this.state = GameState.RECRUITING;
        this.players = new ArrayList<>();
        this.teams = new HashMap<>();
        this.countdown = new Countdown(tntWars, this);
        this.game = new Game(this, tntWars);
        canJoin = true;
    }

    public void start() {
        game.start();
    }

    public void reset() {
        if (state.equals(GameState.LIVE)) {
            game.end();
            canJoin = false;

            Location loc = ConfigManager.getLobbySpawn();
            for (UUID uuid : players) {
                Bukkit.getPlayer(uuid).teleport(loc);
            }
            players.clear();
            teams.clear();

            String worldName = world.getName();
            Bukkit.unloadWorld(worldName, false);

            World worldCopy = Bukkit.createWorld(new WorldCreator(worldName));
            worldCopy.setAutoSave(false);
        }

        sendTitle("", "");
        state = GameState.RECRUITING;
        countdown.cancel();
        countdown = new Countdown(tntWars, this);
        game = new Game(this, tntWars);
    }

    private void setSpawnLocation()
    {
        FileConfiguration config = tntWars.getConfig();
        spawn = new Location(
                Bukkit.getWorld(config.getString("arenas." + id + ".lobby-spawn.world")),
                config.getDouble("arenas." + id + ".lobby-spawn.x"),
                config.getDouble("arenas." + id + ".lobby-spawn.y"),
                config.getDouble("arenas." + id + ".lobby-spawn.z"),
                (float) config.getDouble("arenas." + id + ".lobby-spawn.yaw"),
                (float) config.getDouble("arenas." + id + ".lobby-spawn.pitch"));
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
            reset();
            return;
        }

        if (state == GameState.LIVE && players.size() < ConfigManager.getRequiredPlayers()) {
            sendMessage(ChatColor.RED + "The game has ended because too many players have left.");
            reset();
        }
    }

    public int getId() { return id; }

    public World getWorld() { return world; }
    public GameState getState() { return state; }
    public void setState(GameState state) { this.state = state; }
    public Game getGame() { return game; }
    public boolean canJoin() { return canJoin; }
    public void setCanJoin(boolean canJoin) { this.canJoin = canJoin; }
    public Location getSpawn() { return spawn; }

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

    public Collection<Team> getTeams() {
        return teams.values();
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

    public void save() {
        canJoin = false;
        for (Player p : getWorld().getPlayers()) {
            p.teleport(ConfigManager.getLobbySpawn());
        }

        String worldName = world.getName();
        Bukkit.unloadWorld(worldName, true);
        World worldCopy = Bukkit.createWorld(new WorldCreator(worldName));
        worldCopy.setAutoSave(false);
    }

    public boolean isPlayerPlaying(Player player) {
        return state == GameState.LIVE && game.getRemainingPlayers().contains(player.getUniqueId());
    }
}
