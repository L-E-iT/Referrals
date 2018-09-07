package io.github.leit.referrals.commands;

import io.github.leit.referrals.Referrals;
import io.github.leit.referrals.database.PlayerData;
import io.github.leit.referrals.database.h2;
import io.github.leit.referrals.rewards.Rewards;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ReferralsThanks implements CommandExecutor {
    private Logger logger;
    private Referrals plugin;

    ReferralsThanks(Referrals plugin) {
        this.plugin = plugin;
        logger = plugin.getLogger();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        PlayerData referredData;
        PlayerData referrerData;
        // Declare Optionals
        User referrerUser;

        // Declare UUIDs
        UUID referrerUUID;

        // Declare Players
        Player commandSender = null;

        // If command executed by Console or Command Block
        if (src instanceof ConsoleSource || src instanceof CommandBlockSource) {
            logger.info("Only players can thank other players for a referral!");
            return CommandResult.success();
        }

        // src is player
        commandSender = ((Player) src).getPlayer().get();
        UUID referredUUID = commandSender.getUniqueId();

        // Get User from command
        if (args.getOne("name").isPresent()) {
            referrerUser = (User) args.getOne("name").get();
            referrerUUID = referrerUser.getUniqueId();
        } else {
            commandSender.sendMessage(Text.of(TextColors.RED, "You need to specify a name of a player to thank for your referral."));
            return CommandResult.success();
        }


        // Create accounts if needed.
        Optional<PlayerData> optionalReferredPlayerData = plugin.getPlayerData(referredUUID);
        referredData = optionalReferredPlayerData.orElseGet(() -> new PlayerData(referredUUID, 0, null,0));

        Optional<PlayerData> optionalReferrerPlayerData = plugin.getPlayerData(referrerUUID);
        referrerData = optionalReferrerPlayerData.orElseGet(() -> new PlayerData(referrerUUID, 0, null,0));

        if (referredData.getIsReferred() == 1) {
            String referredBy = referredData.getReferredBy().toString();
            commandSender.sendMessage(Text.of(TextColors.RED, "You've already set ",TextColors.GOLD, referredBy, TextColors.RED, " as your referrer!"));
        } else {
            if (referrerUUID == referredUUID) {
                commandSender.sendMessage(Text.of(TextColors.RED, "You cannot refer yourself!"));
            } else {
                // Set that the players is now referred
                referredData.setIsReferred(1);

                // Set who the player is referred by
               referredData.setReferredBy(referrerUUID);

                // Add 1 to the referrers count
                referrerData.setPlayersReferred(referrerData.getPlayersReferred() + 1);

                commandSender.sendMessage(Text.of(TextColors.DARK_GREEN, "You've set ", TextColors.GOLD, referrerUser.getName(), TextColors.DARK_GREEN,  " as your referrer!"));

                Rewards.GiveRewards(commandSender, plugin, true);
                Rewards.GiveRewards(referrerUser, plugin, false);

            }
        }
        plugin.saveLocalData(referrerData);
        plugin.saveLocalData(referredData);
        return CommandResult.success();
    }
}
