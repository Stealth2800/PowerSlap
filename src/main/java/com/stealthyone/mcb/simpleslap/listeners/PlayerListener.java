package com.stealthyone.mcb.simpleslap.listeners;

import com.stealthyone.mcb.simpleslap.SimpleSlap;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerListener implements Listener {

    private SimpleSlap plugin;

    private static EnumSet<Material> fallDamageCancellers = EnumSet.of(
            Material.WATER,
            Material.STATIONARY_WATER,
            Material.LAVA,
            Material.STATIONARY_LAVA,
            Material.WEB,
            Material.LADDER,
            Material.VINE
    );

    private Set<UUID> checkingPlayers = new HashSet<>();

    public PlayerListener(SimpleSlap plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (e.getCause() == DamageCause.FALL && e.getEntity() instanceof Player) {
            //plugin.getLogger().info("(EVENT) PLAYER DAMAGE EVENT");
            checkingPlayers.add(((Player) e.getEntity()).getUniqueId());
            if (plugin.getSlapManager().isDamageCancelled((Player) e.getEntity())) {
                e.setCancelled(true);
            }
            checkingPlayers.remove(((Player) e.getEntity()).getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getFrom().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR && PlayerListener.fallDamageCancellers.contains(e.getTo().getBlock().getRelative(BlockFace.DOWN).getType())) {
            //plugin.getLogger().info("(EVENT) - PLAYER MOVE EVENT");
            //plugin.getLogger().info("----- TO: " + e.getTo().getBlock().getRelative(BlockFace.DOWN).getType() + " FROM: " + e.getFrom().getBlock().getRelative(BlockFace.DOWN).getType());
            if (!checkingPlayers.contains(e.getPlayer().getUniqueId())) {
                plugin.getSlapManager().isDamageCancelled(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        plugin.getSlapManager().isDamageCancelled(e.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        plugin.getSlapManager().isDamageCancelled(e.getPlayer());
    }

}