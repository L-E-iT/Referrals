package io.github.leit.referrals.commands;

import io.github.leit.referrals.Referrals;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class ReferralsHelp implements CommandExecutor {
    private Logger logger;

    public ReferralsHelp(Referrals plugin) {
        logger = plugin.getLogger();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player commandSender = null;

        // If Command executed by console or command block
        if (src instanceof ConsoleSource || src instanceof CommandBlockSource){
            sendMenuToConsole();
        }
        // If command executed by player
        else if (src instanceof Player) {
            commandSender = ((Player) src).getPlayer().get();
            sendMenuToPlayer(commandSender);
        }

        return CommandResult.success();
    }

    private void sendMenuToPlayer(Player commandSender) {
        commandSender.sendMessage(Text.of(TextColors.GOLD, TextStyles.ITALIC, "Referrals - ", TextColors.GRAY, TextStyles.RESET, "A Referral Plugin", Text.NEW_LINE,
                TextColors.GRAY ,"-----------------------------------", Text.NEW_LINE,
                TextColors.LIGHT_PURPLE, ">", TextColors.WHITE, "/referrals help", TextColors.AQUA, " - ", TextColors.GRAY, "Get Plugin help", Text.NEW_LINE,
                TextColors.LIGHT_PURPLE, ">", TextColors.WHITE, "/referrals thanks [Player Name]", TextColors.AQUA, " - ", TextColors.GRAY, "Thank a player for referring you", Text.NEW_LINE,
                TextColors.LIGHT_PURPLE, ">", TextColors.WHITE, "/referrals check [Player Name]", TextColors.AQUA, " - ", TextColors.GRAY, "Check the amount of players another player has referred", Text.NEW_LINE,
                TextColors.LIGHT_PURPLE, ">", TextColors.WHITE, "/referrals top [#]", TextColors.AQUA, " - ", TextColors.GRAY, "Check the top Referrers by amount (Defaults to 10)"
                ));
    }

    private void sendMenuToConsole() {
        logger.info("§d>  §f/referrals help §b- §7Get Plugin help");
        logger.info("§d>  §f/referrals thanks [Player Name] §b- §7Thank a player for referring you");
        logger.info("§d>  §f/referrals check [Player Name] §b- §7Check the amount of players another player has referred");
        logger.info("§d>  §f/referrals top [#] §b- §7Check the top Referrers by amount (Defaults to 10)");

    }
}
