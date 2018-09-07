package io.github.leit.referrals.rewards;

import io.github.leit.referrals.Referrals;
import io.github.leit.referrals.config.PluginConfig;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Rewards {

    public static void GiveRewards(User rewardPlayer, Referrals plugin, boolean isReferredPlayer) {
        PluginConfig pluginConfig = plugin.getPluginConfig();
        boolean rewardReferrer = pluginConfig.isRewardReferrer();
        boolean rewardReferred = pluginConfig.isRewardReferred();
        List<String> referrerRewardCommand = pluginConfig.getReferrerRewardCommand();
        List<String> referredRewardCommand = pluginConfig.getReferredRewardCommand();

        if (isReferredPlayer && rewardReferred) {
            executeRewardCommand(referredRewardCommand.get(0), rewardPlayer);
        } else if (!isReferredPlayer && rewardReferrer) {
            executeRewardCommand(referrerRewardCommand.get(0), rewardPlayer);
        }
    }

    private static void executeRewardCommand(String rewardCommand, User rewardPlayer) {
        if (!rewardPlayer.isOnline()) {
            Sponge.getPluginManager().getPlugin("referrals").get().getLogger().warn(rewardPlayer.getName() + " may not be online to claim their reward");
        }
        String parsedCommand = rewardCommand.replace("%p", rewardPlayer.getName());
        Sponge.getCommandManager().process(Sponge.getServer().getConsole(), parsedCommand);
    }

    private static void executeReferralGlobal(List<String> globalCommandList){
        return;
    }

    private static void giveReferrerReward(List<String> referrerRewardsList, UUID playerUUID){
        return;
    }

    private static void giveReferredReward(List<String> refereedRewardsList, UUID playerUUID){
        return;
    }

    private static void giveMilestoneReward(Map<Integer, String> milestoneRewardMap, UUID playerUUID) {
        return;
    }
}
