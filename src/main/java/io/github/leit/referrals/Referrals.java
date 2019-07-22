package io.github.leit.referrals;

import com.google.inject.Inject;
import io.github.leit.referrals.commands.CommandRegister;
import io.github.leit.referrals.config.PluginConfig;
import io.github.leit.referrals.database.PlayerData;
import io.github.leit.referrals.database.h2;
import org.slf4j.Logger;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;


import java.nio.file.Path;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Plugin(
        id = "referrals",
        name = "Referrals",
        description = "Let you users refer other players to your server, and get rewards for doing so!",
        authors = {
                "BranFlakes"
        }
)
public class Referrals {



    @Inject
    private Logger logger;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path defaultConfig;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path defaultConfigDir;

    @Inject
    private PluginContainer instance;

    private List<PlayerData> playerDataList;
    private PluginConfig pluginConfig;
    private h2 database;

    public Logger getLogger() {
        return logger;
    }
    public PluginContainer getInstance() {
        return instance;
    }
    public Path getDefaultConfigDir() {
        return defaultConfigDir;
    }
    public Path getDefaultConfig() { return defaultConfig; }
    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }
    public h2 getDatabase() {
        return database;
    }
    public List<PlayerData> getPlayerDataList() {
        return playerDataList;
    }

    @Listener
    public void onGameInit(GameInitializationEvent event){
        io.github.leit.referrals.config.ConfigManager configManager = io.github.leit.referrals.config.ConfigManager.getInstance();
        configManager.init(this);

        this.pluginConfig = configManager.loadPluginConfig();

    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) throws SQLException {
        // Plugin message to console on server start
        logger.info("Referrals is booting up!");

        // Create the Databases if they don't exist
        database = new h2();
        database.createDatabase();
        loadData();
        saveDataScheduler();

        // Register our commands
        CommandRegister.registerCommands(this);
    }

    private void loadData(){
        logger.info("Referrals is loading its data from the database!");
        this.playerDataList = this.database.loadData().orElse(new ArrayList<>());
    }

    private void saveDataScheduler(){
        Task.Builder taskBuilder = Task.builder();
        taskBuilder.execute(() -> saveData(this.playerDataList)).interval(30, TimeUnit.SECONDS).async().name("Referrals - Save Data To Database").submit(this);
    }

    private void saveData(List<PlayerData> playerDataList){
        logger.debug("Referrals is saving its data to the database!");
        database.saveData(playerDataList);
        logger.debug("Referrals is done saving data!");
    }

    public void saveLocalData(PlayerData playerDataToSave){
        for (PlayerData playerData : this.playerDataList){
            if (playerData.getPlayerUUID().equals(playerDataToSave.getPlayerUUID())){
                int index = this.playerDataList.indexOf(playerData);
                this.playerDataList.set(index, playerDataToSave);
                return;
            }
        }
        this.playerDataList.add(playerDataToSave);
    }

    public Optional<PlayerData> getPlayerData(UUID uuid){
        for (PlayerData playerData : this.playerDataList){
            if (playerData.getPlayerUUID() == uuid) {
                return Optional.of(playerData);
            }
        }
        return Optional.empty();
    }

}
