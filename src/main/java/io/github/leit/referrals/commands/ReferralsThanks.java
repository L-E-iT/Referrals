package io.github.leit.referrals.commands;

import io.github.leit.referrals.Referrals;
import io.github.leit.referrals.database.h2;
import io.github.leit.referrals.rewards.Rewards;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class ReferralsThanks implements CommandExecutor {
    private Logger logger;
    private h2 Database;
    private Referrals plugin;

    public ReferralsThanks(Referrals plugin) {
        this.plugin = plugin;
        logger = plugin.getLogger();
        Database = new h2();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        // Declare Optionals
        User referrerUser;

        // Declare UUIDs
        UUID referrerUUID;
        UUID referredPlayerUUID = null;

        // Declare Players
        Player commandSender = null;

        // If command executed by Console or Command Block
        if (src instanceof ConsoleSource || src instanceof CommandBlockSource) {
            logger.info("Only players can thank other players for a referral!");
            return CommandResult.success();
        }
        // If command executed by player
        else if (src instanceof Player) {
            commandSender = ((Player) src).getPlayer().get();
            referredPlayerUUID = commandSender.getUniqueId();
        }

        // Get User from command
        if (args.getOne("name").isPresent()) {
            referrerUser = (User) args.getOne("name").get();
            referrerUUID = referrerUser.getUniqueId();
        } else {
            commandSender.sendMessage(Text.of(TextColors.RED, "You need to specify a name of a player to thank for your referral."));
            return CommandResult.success();
        }

        // If we find the user on the server but not in the database, create their user in the database.
        try {
            if (!Database.isUser(referrerUUID)) {
                Database.createUser(referrerUUID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (Database.getIsReferred(referredPlayerUUID)) {
                String referredBy = Database.getReferredBy(referredPlayerUUID);
                commandSender.sendMessage(Text.of(TextColors.RED, "You've already set ",TextColors.GOLD, referredBy, TextColors.RED, " as your referrer!"));
            } else {
                if (referrerUUID == referredPlayerUUID) {
                    commandSender.sendMessage(Text.of(TextColors.RED, "You cannot refer yourself!"));
                } else {
                    // Set that the players is now referred
                    Database.setIsReferred(referredPlayerUUID);

                    // Set who the player is referred by
                    Database.setReferredBy(referredPlayerUUID, referrerUUID);

                    // Add 1 to the referrers count
                    Database.addToPlayersReferred(referrerUUID);

                    commandSender.sendMessage(Text.of(TextColors.DARK_GREEN, "You've set ", TextColors.GOLD, referrerUser.getName(), TextColors.DARK_GREEN,  " as your referrer!"));

                    Rewards.GiveRewards(commandSender, plugin, true);
                    Rewards.GiveRewards(referrerUser, plugin, false);

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return CommandResult.success();
    }
}
