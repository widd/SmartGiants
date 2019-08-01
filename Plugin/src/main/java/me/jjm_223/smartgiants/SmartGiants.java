package me.jjm_223.smartgiants;

import me.jjm_223.smartgiants.api.util.Configuration;
import me.jjm_223.smartgiants.api.util.IGiantTools;
import me.jjm_223.smartgiants.api.util.ILoad;
import me.jjm_223.smartgiants.api.util.INaturalSpawns;
import me.jjm_223.smartgiants.commands.*;
import me.jjm_223.smartgiants.listeners.EntityListener;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Copyright (C) 2018  Jacob Martin
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class SmartGiants extends JavaPlugin {
    private boolean errorOnLoad;

    private ILoad load = null;
    private INaturalSpawns naturalSpawns = null;
    private IGiantTools giantTools = null;

    private DropManager dropManager;

    @Override
    public void onLoad() {
        if (!new File(getDataFolder(), Constants.LANG_FILENAME).exists()) {
            saveResource(Constants.LANG_FILENAME, false);
        }

        this.getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        try {
            loadConfig();
        } catch (InvalidConfigurationException | IOException e) {
            getLogger().severe(e.getMessage());

            this.setEnabled(false);
        }

        saveConfig();
        loadGiants();
    }

    private void loadConfig() throws IOException, InvalidConfigurationException {
        Configuration.load(new File(getDataFolder(), Constants.CONFIG_FILENAME));
    }

    @Override
    public void onEnable() {
        if (!errorOnLoad) {
            LangManager.loadMessages(this);
            try {
                dropManager = new DropManager(this);
            } catch (IOException e) {
                getLogger().log(Level.SEVERE, "Unable to load drops.", e);
            }

            registerCommands();
            registerEvents();
        } else {
            getLogger().severe("This version of Spigot isn't supported. Disabling.");

            this.setEnabled(false);
        }
    }

    public boolean reloadDrops() {
        dropManager.shutdown();

        try {
            dropManager = new DropManager(this);
            return true;
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Drops reload error.", e);
            return false;
        }
    }

    private void loadGiants() {
        final String serverVersion = getVersion();

        final Configuration config = Configuration.getInstance();
        final boolean natural = config.naturalSpawns();
        final boolean hostile = config.isHostile();
        final boolean daylight = getConfig().getBoolean("daylight");
        final int frequency = config.frequency() > 0 ? config.frequency() : 5;
        final int minGroupAmount = config.minGroupAmount() > 0 ? config.minGroupAmount() : 1;
        int maxGroupAmount = config.maxGroupAmount();
        if (maxGroupAmount <= 0 || maxGroupAmount < minGroupAmount) {
            maxGroupAmount = minGroupAmount + 1;
        }

        getLogger().log(Level.INFO, "Loading support for version: {0}", serverVersion);
        getLogger().log(Level.INFO, "Spawn naturally?: {0}", natural);
        getLogger().log(Level.INFO, "Spawn during day?: {0}", daylight);
        getLogger().log(Level.INFO, "Giants are hostile?: {0}", hostile);
        getLogger().log(Level.INFO, "Spawn frequency is: {0}", frequency);
        getLogger().log(Level.INFO, "Minimum group amount is: {0}", minGroupAmount);
        getLogger().log(Level.INFO, "Maximum group amount is: {0}", maxGroupAmount);

        try {
            final Class<?> clazzLoad = Class.forName(Constants.PACKAGE_ENTITIES + serverVersion + ".Load");
            final Class<?> clazzNaturalSpawns = Class.forName(Constants.PACKAGE_ENTITIES + serverVersion + ".NaturalSpawns");
            final Class<?> clazzGiantTools = Class.forName(Constants.PACKAGE_ENTITIES + serverVersion + ".GiantTools");

            if (ILoad.class.isAssignableFrom(clazzLoad)
                    && INaturalSpawns.class.isAssignableFrom(clazzNaturalSpawns)
                    && IGiantTools.class.isAssignableFrom(clazzGiantTools)) {
                if (natural) {
                    this.naturalSpawns = (INaturalSpawns) clazzNaturalSpawns.getConstructor().newInstance();
                    this.naturalSpawns.load(hostile, daylight, frequency, minGroupAmount, maxGroupAmount);
                }

                this.giantTools = (IGiantTools) clazzGiantTools.getConstructor().newInstance();
                this.load = (ILoad) clazzLoad.getConstructor().newInstance();
                load.load(hostile);
            }
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "This Spigot version is not supported.", e);
            getLogger().info("Check for updates at https://www.spigotmc.org/resources/smartgiants.4882/");

            errorOnLoad = true;
        }
    }

    private void registerEvents() {
        final PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new EntityListener(this), this);
    }

    private void registerCommands() {
        Optional.ofNullable(getCommand("smartgiants"))
                .ifPresent(c -> c.setExecutor(new CommandBase()));

        new CommandAdd(this);
        new CommandRemove(this);
        new CommandReset(this);
        new CommandReloadDrops(this);
        new CommandReloadConfig();
    }

    @Override
    public void onDisable() {
        if (dropManager != null) {
            dropManager.shutdown();
        }

        if (load != null) {
            load.cleanup();
        }

        if (naturalSpawns != null) {
            naturalSpawns.cleanup();
        }
    }

    public IGiantTools getGiantTools() {
        return giantTools;
    }

    public DropManager getDropManager() {
        return dropManager;
    }

    public String getVersion() {
        final String packageName = this.getServer().getClass().getPackage().getName();
        return packageName.substring(packageName.lastIndexOf('.') + 1).toLowerCase();
    }
}
