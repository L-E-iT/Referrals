package io.github.leit.referrals.rewards;

import io.github.leit.referrals.Referrals;
import io.github.leit.referrals.config.PluginConfig;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

import java.util.Optional;

public class Rewards {

    public static void GiveRewards(User rewardPlayer, Referrals plugin, boolean isReferredPlayer) {
        PluginConfig pluginConfig = plugin.getPluginConfig();
        boolean rewardReferrer = pluginConfig.isRewardReferrer();
        boolean rewardReferred = pluginConfig.isRewardReferred();
        String referrerRewardCommand = pluginConfig.getReferrerRewardCommand();
        String referredRewardCommand = pluginConfig.getReferredRewardCommand();

        if (isReferredPlayer && rewardReferred) {
            executeRewardCommand(referredRewardCommand, rewardPlayer);
        } else if (!isReferredPlayer && rewardReferrer) {
            executeRewardCommand(referrerRewardCommand, rewardPlayer);
        }
    }

    private static void executeRewardCommand(String rewardCommand, User rewardPlayer) {
        if (!rewardPlayer.isOnline()) {
            Sponge.getPluginManager().getPlugin("referrals").get().getLogger().warn(rewardPlayer.getName() + " may not be online to claim their reward");
        }
        String parsedCommand = rewardCommand.replace("%p", rewardPlayer.getName());
        Sponge.getCommandManager().process(Sponge.getServer().getConsole(), parsedCommand);
    }
}
