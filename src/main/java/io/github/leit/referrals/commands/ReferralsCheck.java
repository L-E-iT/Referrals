package io.github.leit.referrals.commands;

import io.github.leit.referrals.Referrals;
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
import java.util.Optional;
import java.util.UUID;

public class ReferralsCheck implements CommandExecutor {
    private Logger logger;
    private h2 Database;

    public ReferralsCheck(Referrals plugin) {
        logger = plugin.getLogger();
        Database = new h2();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        // Declare Player Variables
        Player commandSender = null;

        // Declare String Variables
        String checkName = "";

        // Declare Optional Variables
        Optional<UserStorageService> userStorage = Sponge.getServiceManager().provide(UserStorageService.class);

        if (args.getOne("name").isPresent()) {
            checkName = (String) args.getOne("name").get();
        }

        if (src instanceof ConsoleSource || src instanceof CommandBlockSource) {
            if (checkName == "") {
                logger.info("Please specify a player to look at from the console or command block.");
                return CommandResult.success();
            }
            checkOther(commandSender, checkName, userStorage);
        }
        // If command executed by player
        else if (src instanceof Player) {
            commandSender = ((Player) src).getPlayer().get();
            if (checkName == "") {
                checkName = commandSender.getName();
            }

            if (commandSender.getName().equals(checkName)){
                try {
                    checkSelf(commandSender);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                checkOther(commandSender, checkName, userStorage);
            }
        }

        return CommandResult.success();
    }

    private void checkSelf(Player commandSender) throws SQLException {
        UUID uuid = commandSender.getUniqueId();
        int playersReferred = Database.getPlayersReferred(uuid);
        if (playersReferred == 1) {
            commandSender.sendMessage(Text.of("You have referred ", TextColors.GREEN, playersReferred, TextColors.WHITE ," player"));
        } else {
             commandSender.sendMessage(Text.of("You have referred ", TextColors.GREEN, playersReferred, TextColors.WHITE ," players"));
        }
    }

    private void checkOther(Player commandSender, String checkName, Optional<UserStorageService> userStorage) {
        Player checkPlayer;
        UUID checkUUID = null;
        Optional<User> checkUser;
        Boolean playerFound = true;

        if (Sponge.getServer().getPlayer(checkName).isPresent()) {
            checkPlayer = Sponge.getServer().getPlayer(checkName).get();
            checkUUID = checkPlayer.getUniqueId();
        } else {
            if (userStorage.get().get(checkName).isPresent()) {
                checkUser = userStorage.get().get(checkName);
                checkPlayer = checkUser.get().getPlayer().get();
                checkUUID = checkPlayer.getUniqueId();
            } else {
                commandSender.sendMessage(Text.of(TextColors.RED, "We didn't find that player on this server."));
                playerFound = false;

            }
        }

        if (playerFound) {
            try {
                int playersReferred = Database.getPlayersReferred(checkUUID);
                if (playersReferred == 1) {
                    commandSender.sendMessage(Text.of(String.format(checkName, " has referred", TextColors.GREEN, playersReferred, TextColors.WHITE, "player")));
                } else {
                    commandSender.sendMessage(Text.of(String.format(checkName, " has referred", TextColors.GREEN, playersReferred, TextColors.WHITE, "players")));
                }

            } catch (SQLException e) {
                e.printStackTrace();
                commandSender.sendMessage(Text.of(TextColors.RED, "There was an error looking up this user."));
            }
        }
    }
}
