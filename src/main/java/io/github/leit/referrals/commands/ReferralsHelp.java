package io.github.leit.referrals.commands;

import io.github.leit.referrals.Referrals;
import io.github.leit.referrals.database.h2;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class ReferralsHelp implements CommandExecutor {
    private Logger logger;
    private h2 Database;

    public ReferralsHelp(Referrals plugin) {
        logger = plugin.getLogger();
        Database = new h2();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        return CommandResult.success();
    }
}
