package com.stealthyone.mcb.simpleslap.listeners;

import com.stealthyone.mcb.simpleslap.SimpleSlap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private SimpleSlap plugin;

    public PlayerListener(SimpleSlap plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (e.getCause() == DamageCause.FALL && e.getEntity() instanceof Player) {
            if (plugin.getSlapManager().isDamageCancelled((Player) e.getEntity())) {
                e.setCancelled(true);
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