package me.jjm_223.smartgiants.listeners;

import me.jjm_223.smartgiants.SmartGiants;
import me.jjm_223.smartgiants.api.util.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import javax.annotation.Nonnull;
import java.util.List;

public class EntityListener implements Listener {
    private SmartGiants plugin;

    public EntityListener(SmartGiants plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(final EntityDeathEvent event) {
        if (event.getEntityType() == EntityType.GIANT && Configuration.getInstance().handleDrops()) {
            event.getDrops().clear();
            event.getDrops().addAll(plugin.getDropManager().getRandomDrops());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSpawn(final CreatureSpawnEvent event) {
        final SpawnReason reason = event.getSpawnReason();
        if ((reason == SpawnReason.CHUNK_GEN || reason == SpawnReason.NATURAL)
                && event.getEntityType() == EntityType.GIANT
                && !containsIgnoreCase(Configuration.getInstance().worlds(), event.getEntity().getWorld().getName())) {
            event.setCancelled(true);
        }
    }

    private boolean containsIgnoreCase(final @Nonnull List<String> sourceList, final @Nonnull String search) {
        for (final String string : sourceList) {
            if (search.equalsIgnoreCase(string)) {
                return true;
            }
        }

        return false;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPluginSpawn(final CreatureSpawnEvent event) {
        if (plugin.getGiantTools().isSmartGiant(event.getEntity()) || event.getEntityType() != EntityType.GIANT) {
            return;
        }

        event.setCancelled(true);
        plugin.getGiantTools().spawnGiant(event.getLocation(), Configuration.getInstance().isHostile());
    }

    @EventHandler(ignoreCancelled = true)
    public void onGiantDamage(final EntityDamageByEntityEvent event) {
        if (!Configuration.getInstance().damageObeyGameDifficulty()
                && plugin.getGiantTools().isSmartGiant(event.getDamager())) {
            event.setDamage(Configuration.getInstance().attackDamage());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityCombust(final EntityCombustByEntityEvent event) {
        // Disable combustion from arrows
        event.setCancelled(shouldProtectFromArrow(event.getEntity(), event.getCombuster()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onArrowDamage(final EntityDamageByEntityEvent event) {
        Bukkit.getLogger().info(String.valueOf(plugin.getGiantTools().isSimpleArrow(event.getDamager())));
        Bukkit.getLogger().info(String.valueOf(plugin.getGiantTools().isTippedArrow(event.getDamager())));

        event.setCancelled(shouldProtectFromArrow(event.getEntity(), event.getDamager()));
    }

    private boolean shouldProtectFromArrow(final Entity damagedEntity, final Entity damagerEntity) {
        final Configuration config = Configuration.getInstance();

        return plugin.getGiantTools().isSmartGiant(damagedEntity)
                && ((plugin.getGiantTools().isSimpleArrow(damagerEntity) && !config.giantsTakeArrowDamage())
                || (plugin.getGiantTools().isTippedArrow(damagerEntity) && !config.giantsTakeTippedArrowDamage()));
    }
}
