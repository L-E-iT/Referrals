package io.github.leit.referrals.commands;

import io.github.leit.referrals.Referrals;
import io.github.leit.referrals.database.PlayerData;
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

import java.util.*;

public class ReferralsTop implements CommandExecutor {
    private Logger logger;
    private List<PlayerData> playerDataList;

    ReferralsTop(Referrals plugin) {
        logger = plugin.getLogger();
        playerDataList = plugin.getPlayerDataList();
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

            // Check permissions
            if (!src.hasPermission("referrals.top")) {
                src.sendMessage(Text.of(TextColors.RED, "You do not have permission to use that command"));
                return CommandResult.success();
            }
            sendToPlayer(src, count);
        }
        return CommandResult.success();
    }

    private void sendToPlayer(CommandSource src, int count) {
        Optional<UserStorageService> userStorage = Sponge.getServiceManager().provide(UserStorageService.class);
        int countI= 0;
        Player commandSender = ((Player) src).getPlayer().get();

        // Get Map of top referrers UUID and Count
        Map<String, Integer> topReferrers = getTopReferrers(count, playerDataList);

        if (count > 20) {
            commandSender.sendMessage(Text.of(TextColors.RED, count, " is a rather large number, limiting results to 20."));
            count = 20;
        }

        assert topReferrers != null;
        Iterator it = topReferrers.entrySet().iterator();

        // Send names and count to player
        commandSender.sendMessage(Text.of(TextColors.GRAY, "---- ", TextColors.GOLD, "Top " , count, " Referrers", TextColors.GRAY, " ----------"));
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (Sponge.getServer().getPlayer(UUID.fromString((String) pair.getKey())).isPresent()) {
                Player onlinePlayer = Sponge.getServer().getPlayer(UUID.fromString((String) pair.getKey())).get();
                commandSender.sendMessage(Text.of(TextColors.LIGHT_PURPLE , ++countI, TextColors.GRAY," : ",onlinePlayer.getName(),
                        TextColors.WHITE,"| Players Referred - ",TextColors.GRAY,"[",TextColors.DARK_GREEN,pair.getValue(),TextColors.GRAY,"]"));
            } else {
                User offlineUser = userStorage.get().get(UUID.fromString((String) pair.getKey())).get();
                commandSender.sendMessage(Text.of(TextColors.LIGHT_PURPLE , ++countI, TextColors.GRAY," : ", offlineUser.getName(),
                        TextColors.WHITE,"| Players Referred - ",TextColors.GRAY,"[",TextColors.DARK_GREEN,pair.getValue(),TextColors.GRAY,"]"));
            }
            it.remove();
        }
    }

    private void sendToConsole(int count) {
        Optional<UserStorageService> userStorage = Sponge.getServiceManager().provide(UserStorageService.class);
        int countI= 0;

        // Get Map of top referrers UUID and Count
        Map<String, Integer> topReferrers = getTopReferrers(count, playerDataList);

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

    // TODO - SORT OUT ORDERING
    private Map<String, Integer> getTopReferrers(int count, List<PlayerData> playerDataList){
        Map<String, Integer> topReferrersList = new HashMap<String, Integer>();
        if (count > playerDataList.size()){
            count = playerDataList.size();
        }
        playerDataList.sort(Comparator.comparingInt(PlayerData::getPlayersReferred));
        List<PlayerData> newPlayerDataList = playerDataList.subList(playerDataList.size() - count, playerDataList.size());
        for (PlayerData playerData: newPlayerDataList){
                topReferrersList.put(playerData.getPlayerUUID().toString(), playerData.getPlayersReferred());
        }
        return topReferrersList;

    }
}
