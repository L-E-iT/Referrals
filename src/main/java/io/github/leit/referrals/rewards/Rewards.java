package io.github.leit.referrals.rewards;

import io.github.leit.referrals.Referrals;
import io.github.leit.referrals.config.PluginConfig;
import io.github.leit.referrals.database.PlayerData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rewards {

    private Referrals plugin;
    private PluginConfig pluginConfig;
    private User referrerUser;
    private User referredUser;

    public Rewards(Referrals plugin, User referrerUser, User referredUser) {
        this.plugin = plugin;
        this.pluginConfig = plugin.getPluginConfig();
        this.referredUser = referredUser;
        this.referrerUser = referrerUser;
    }


    public void GiveRewards() {
        if (pluginConfig.isGlobalCommand()){
            executeReferralGlobal();
        }
        if (pluginConfig.isMilestoneRewards()){
            giveMilestoneReward();
        }
        if (pluginConfig.isRewardReferred()){
            giveReferredReward();
        }
        if (pluginConfig.isRewardReferrer()){
            giveReferrerReward();
        }
    }

    public void executeReferralGlobal(){
        List<String> globalCommandList = pluginConfig.getGlobalCommands();
        for (String command : globalCommandList){
            String parsedCommand = command.replace("%pr", referrerUser.getName()).replace("%pd", referredUser.getName());
            Sponge.getCommandManager().process(Sponge.getServer().getConsole(), parsedCommand);
        }
    }

    public void giveReferrerReward(){
        List<String> referrerCommandList = pluginConfig.getReferrerRewardCommand();
        for (String command : referrerCommandList){
            String parsedCommand = command.replace("%p", referrerUser.getName());
            Sponge.getCommandManager().process(Sponge.getServer().getConsole(), parsedCommand);
        }
    }

    public void giveReferredReward(){
        List<String> referredCommandList = pluginConfig.getReferredRewardCommand();
        for (String command : referredCommandList){
            String parsedCommand = command.replace("%p", referredUser.getName());
            Sponge.getCommandManager().process(Sponge.getServer().getConsole(), parsedCommand);
        }
    }

    public void giveMilestoneReward() {
        List<String> milestoneCommandList = pluginConfig.getMilestoneCommands();
        Map<Integer, String> milestoneCommandMap = new HashMap<>();
        for (String milestoneCommand : milestoneCommandList){
            String[] parsedCommand = milestoneCommand.split(":::", 2);
            milestoneCommandMap.put(Integer.parseInt(parsedCommand[0]), parsedCommand[1]);
        }
        if (plugin.getPlayerData(referrerUser.getUniqueId()).isPresent()) {
            PlayerData playerData = plugin.getPlayerData(referrerUser.getUniqueId()).get();
            if (milestoneCommandMap.containsKey(playerData.getPlayersReferred())) {
                String parsedCommand = milestoneCommandMap.get(playerData.getPlayersReferred()).replace("%p", referrerUser.getName());
                Sponge.getCommandManager().process(Sponge.getServer().getConsole(), parsedCommand);
            }
        }
        return;
    }
}
