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

        if (args.getOne("name").isPresent())
            checkName = args.getOne("name").toString();
        else{
            logger.info("I could not find that player on this server");
            return CommandResult.success();
        }

        if (src instanceof ConsoleSource || src instanceof CommandBlockSource) {
            checkOther(commandSender, checkName, userStorage);
        }
        // If command executed by player
        else if (src instanceof Player) {
            commandSender = ((Player) src).getPlayer().get();

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
            commandSender.sendMessage(Text.of(String.format("You have referred §2%d §fplayer", playersReferred)));
        } else {
             commandSender.sendMessage(Text.of(String.format("You have referred §2%d §fplayers", playersReferred)));
        }
    }

    private void checkOther(Player commandSender, String checkName, Optional<UserStorageService> userStorage) {
        Player checkPlayer;
        UUID checkUUID = null;
        Optional<User> checkUser;

        if (Sponge.getServer().getPlayer(checkName).isPresent()) {
            checkPlayer = Sponge.getServer().getPlayer(checkName).get();
            checkUUID = checkPlayer.getUniqueId();
        } else {
            checkUser = userStorage.get().get(checkName);
            if (checkUser.get().getPlayer().isPresent()) {
                checkPlayer = checkUser.get().getPlayer().get();
                checkUUID = checkPlayer.getUniqueId();
            } else {
                commandSender.sendMessage(Text.of("We didn't find that player on this server."));
            }
        }

        try {
            int playersReferred = Database.getPlayersReferred(checkUUID);
            if (playersReferred == 1) {
                commandSender.sendMessage(Text.of(String.format("%s has referred §2%d §fplayer", checkName, playersReferred)));
            } else {
                commandSender.sendMessage(Text.of(String.format("%s has referred §2%d §fplayers", checkName, playersReferred)));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            commandSender.sendMessage(Text.of("§cThere was an error looking up this user."));
        }
    }
}
