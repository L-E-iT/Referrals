package io.github.leit.referrals.rewards;

import io.github.leit.referrals.Referrals;
import io.github.leit.referrals.config.PluginConfig;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public class Rewards {

    public static void GiveRewards(Optional<Player> rewardPlayer, Referrals plugin, boolean isReferredPlayer) {
        PluginConfig pluginConfig = plugin.getPluginConfig();
        boolean rewardReferrer = pluginConfig.isRewardReferrer();
        boolean rewardReferred = pluginConfig.isRewardReferred();
        String rewardCommand = pluginConfig.getRewardCommand();

        if (isReferredPlayer && rewardReferred) {
            executeRewardCommand(rewardCommand, rewardPlayer);
        } else if (!isReferredPlayer && rewardReferrer) {
            executeRewardCommand(rewardCommand, rewardPlayer);
        }

    }

    private static void executeRewardCommand(String rewardCommand, Optional<Player> rewardPlayer) {
        String parsedCommand = rewardCommand.replace("%p", rewardPlayer.get().getName());
        Sponge.getCommandManager().process(Sponge.getServer().getConsole(), parsedCommand);
    }
}
