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
import org.spongepowered.api.entity.vehicle.minecart.Minecart;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import javax.swing.text.html.Option;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ReferralsTop implements CommandExecutor {
    private Logger logger;
    private h2 Database;

    public ReferralsTop(Referrals plugin) {
        logger = plugin.getLogger();
        Database = new h2();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        // Set Count of players to retrieve
        int count;
        if (args.getOne("count").isPresent()) {
            count = args.<Integer>getOne("count").get();
        } else {
            count = 10;
        }

        // If Command executed by console or command block
        if (src instanceof ConsoleSource || src instanceof CommandBlockSource){
            sendToConsole(count);
        }
        // If command executed by player
        else if (src instanceof Player) {
            sendToPlayer(src, count);
        }
        return CommandResult.success();
    }

    private void sendToPlayer(CommandSource src, int count) {
        Optional<UserStorageService> userStorage = Sponge.getServiceManager().provide(UserStorageService.class);
        int countI= 0;
        Player commandSender = ((Player) src).getPlayer().get();

        // Get Map of top referrers UUID and Count
        Map<String, Integer> topReferrers = null;
        try {
            topReferrers = Database.getTopReferrers(count);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        assert topReferrers != null;
        Iterator it = topReferrers.entrySet().iterator();

        // Send names and count to player
        commandSender.sendMessage(Text.of("§7----§o§6Top Referrers§r§7----------"));
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (Sponge.getServer().getPlayer(UUID.fromString((String) pair.getKey())).isPresent()) {
                Player onlinePlayer = Sponge.getServer().getPlayer(UUID.fromString((String) pair.getKey())).get();
                commandSender.sendMessage(Text.of(String.format("§d%d§7: %s§f| Players Referred - §7[§2%s§7]", ++countI, onlinePlayer.getName(), pair.getValue())));
            } else {
                Optional<User> offlineUser = userStorage.get().get(UUID.fromString((String) pair.getKey()));
                commandSender.sendMessage(Text.of(String.format("§d%d§7: %s§f| Players Referred - §7[§2%s§7]", ++countI, offlineUser.get().getName(), pair.getValue())));
            }
            it.remove();
        }
    }

    private void sendToConsole(int count) {
        Optional<UserStorageService> userStorage = Sponge.getServiceManager().provide(UserStorageService.class);
        int countI= 0;

        // Get Map of top referrers UUID and Count
        Map<String, Integer> topReferrers = null;
        try {
            topReferrers = Database.getTopReferrers(count);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        assert topReferrers != null;
        Iterator it = topReferrers.entrySet().iterator();

        // Send names and count to player
        logger.info("§7----§o§6Top Referrers§r§7----------");
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (Sponge.getServer().getPlayer(UUID.fromString((String) pair.getKey())).isPresent()) {
                Player onlinePlayer = Sponge.getServer().getPlayer(UUID.fromString((String) pair.getKey())).get();
                logger.info(String.format("§d%d§7: %s§f| Players Referred - §7[§2%s§7]", ++countI, onlinePlayer, pair.getValue()));
            } else {
                Optional<User> offlineUser = userStorage.get().get(UUID.fromString((String) pair.getKey()));
                logger.info(String.format("§d%d§7: %s§f| Players Referred - §7[§2%s§7]", ++countI, offlineUser.get().getName(), pair.getValue()));
            }
            it.remove();
        }
    }
}
