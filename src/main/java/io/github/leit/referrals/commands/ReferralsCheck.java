package io.github.leit.referrals.commands;

import io.github.leit.referrals.Referrals;
import io.github.leit.referrals.database.PlayerData;
import io.github.leit.referrals.database.h2;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class ReferralsCheck implements CommandExecutor {
    private Logger logger;
    private Referrals plugin;
    private List<PlayerData> playerDataList;

    public ReferralsCheck(Referrals plugin) {
        this.plugin = plugin;
        logger = plugin.getLogger();
        playerDataList = plugin.getPlayerDataList();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        // Declare Player Variables
        Player commandSender = null;

        // Declare String Variables
        User checkUser = null;

        if (args.getOne("name").isPresent()) {
            checkUser = (User) args.getOne("name").get();
        }

        if (src instanceof ConsoleSource || src instanceof CommandBlockSource) {
            if (checkUser == null || Objects.equals(checkUser.getName(), "")) {
                logger.info("Please specify a player to look at from the console or command block.");
                return CommandResult.success();
            }
            checkOther(commandSender, checkUser);
        }
        // If command executed by player
        else if (src instanceof Player) {
            commandSender = ((Player) src).getPlayer().get();
            if (checkUser == null || Objects.equals(checkUser.getName(), "")) checkUser = commandSender;

            // Permission check
            if (!commandSender.hasPermission("referrals.check")) {
                commandSender.sendMessage(Text.of(TextColors.RED, "You do not have permission to use that command"));
                return CommandResult.success();
            } else {
                if (commandSender.getName().equals(checkUser.getName())) {
                    if (!commandSender.hasPermission("referrals.check.self")) {
                        commandSender.sendMessage(Text.of(TextColors.RED, "You do not have permission to use that command"));
                        return CommandResult.success();
                    }
                } else if (!commandSender.hasPermission("referrals.check.other")) {
                    commandSender.sendMessage(Text.of(TextColors.RED, "You do not have permission to use that command"));
                    return CommandResult.success();
                }
            }

            if (commandSender.getName().equals(checkUser.getName())){
                try {
                    checkSelf(commandSender);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                checkOther(commandSender, checkUser);
            }
        }

        return CommandResult.success();
    }

    private void checkSelf(Player commandSender) throws SQLException {
        UUID uuid = commandSender.getUniqueId();
        int playersReferred = 0;
        if (plugin.getPlayerData(uuid).isPresent()){
            playersReferred = plugin.getPlayerData(uuid).get().getPlayersReferred();
        }

        if (playersReferred == 1) {
            commandSender.sendMessage(Text.of("You have referred ", TextColors.GREEN, playersReferred, TextColors.WHITE ," player"));
        } else {
             commandSender.sendMessage(Text.of("You have referred ", TextColors.GREEN, playersReferred, TextColors.WHITE ," players"));
        }
    }

    private void checkOther(Player commandSender, User checkUser) {
        UUID checkUUID = checkUser.getUniqueId();
        int playersReferred = 0;
        if (plugin.getPlayerData(checkUUID).isPresent()){
            playersReferred = plugin.getPlayerData(checkUUID).get().getPlayersReferred();
        }
        if (commandSender == null) {
            logger.info(checkUser.getName(), " has referred", TextColors.GREEN, " " ,playersReferred, " ", TextColors.WHITE, "players");
        }

        if (playersReferred == 1) {
            commandSender.sendMessage(Text.of(checkUser.getName(), " has referred", TextColors.GREEN," " ,playersReferred," ", TextColors.WHITE, "player"));
        } else {
            commandSender.sendMessage(Text.of(checkUser.getName(), " has referred", TextColors.GREEN, " " ,playersReferred, " ", TextColors.WHITE, "players"));
        }
    }
}
