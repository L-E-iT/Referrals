package io.github.leit.referrals.config;

import com.google.common.reflect.TypeToken;
import io.github.leit.referrals.Referrals;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.asset.Asset;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigManager {
    private static ConfigManager instance = new ConfigManager();
    private Referrals plugin;
    private Logger logger;

    public void init(Referrals plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    public static ConfigManager getInstance() {
        return instance;
    }

    public PluginConfig loadPluginConfig() {
        File configFile;
        configFile = plugin.getDefaultConfig().toFile();
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(configFile).build();
        if (!configFile.exists()) {
            Asset defaultConfig = plugin.getInstance().getAsset("default_config.conf").get();
            try {
                defaultConfig.copyToFile(configFile.toPath());
                loader.save(loader.load());
            } catch (IOException e) {
                logger.warn("Error in loading the default config!" + e.getMessage());
            }
        }

        try {
            CommentedConfigurationNode defaultConfig = loader.load();
            //Load command reward
            CommentedConfigurationNode rewardActions = defaultConfig.getNode("rewardActions");
            List<String> referrerRewardCommands = new ArrayList<>();
            List<String> referredRewardCommands = new ArrayList<>();
            List<String> globalCommands = new ArrayList<>();
            List<String> milestoneCommandsMap = new ArrayList<>();
            try {
                globalCommands = rewardActions.getNode("globalCommands").getList(TypeToken.of(String.class));
                referrerRewardCommands = rewardActions.getNode("referrerRewardCommand").getList(TypeToken.of(String.class));
                referredRewardCommands = rewardActions.getNode("referredRewardCommand").getList(TypeToken.of(String.class));
                milestoneCommandsMap = rewardActions.getNode("milestoneCommands").getList(TypeToken.of(String.class));
            } catch (ObjectMappingException e) {
                e.printStackTrace();
            }

            //Load referral reward cases
            CommentedConfigurationNode rewardConfig = defaultConfig.getNode("rewardConfig");
            boolean rewardReferrer = rewardConfig.getNode("referrerReward").getBoolean();
            boolean rewardReferred = rewardConfig.getNode("referredReward").getBoolean();
            boolean milestoneRewards = rewardConfig.getNode("milestoneRewards").getBoolean();
            boolean globalCommand = rewardConfig.getNode("globalCommand").getBoolean();

            return new PluginConfig(rewardReferrer, rewardReferred, milestoneRewards, globalCommand, referrerRewardCommands, referredRewardCommands, globalCommands, milestoneCommandsMap);

        } catch (IOException e) {
            logger.warn("Error loading config!" + e.getMessage());
        }

        return new PluginConfig(true, true, true,false,
                new ArrayList<String>(Arrays.asList("msg %p Thanks for using Referrals!", "msg %p Make sure to refer other players!")),
                new ArrayList<String>(Arrays.asList("msg %p Thanks for using Referrals!", "msg %p Make sure to refer other players!")),
                new ArrayList<String>(Arrays.asList("1:::msg %p Thanks for referring 1 player!", "5:::msg %p Thanks for referring 5 players!")),
                new ArrayList<String>(Arrays.asList("","")));

    }
}
