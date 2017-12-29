package io.github.leit.referrals.commands;

import io.github.leit.referrals.Referrals;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;


public class CommandRegister {
    public static void registerCommands(Referrals plugin) {


        // referrals top <#>
        CommandSpec referralTopCommand = CommandSpec.builder()
                .description(Text.of("Shows the top # of referrers (Default 10)"))
                .arguments(GenericArguments.optional(GenericArguments.onlyOne(GenericArguments.integer(Text.of("count")))))
                .executor(new ReferralsTop(plugin))
                .build();

        // referrals check <name>
        CommandSpec referralCheckCommand = CommandSpec.builder()
                .description(Text.of("Shows a players referral count"))
                .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("name"))))
                .executor(new ReferralsCheck(plugin))
                .build();

        // referrals help
        CommandSpec referralHelpCommand = CommandSpec.builder()
                .description(Text.of("Shows the menu for the referrals plugin"))
                .executor(new ReferralsHelp(plugin))
                .build();

        // referrals thanks <name>
        CommandSpec referralThanksCommand = CommandSpec.builder()
                .description(Text.of("Thanks another player for referring you"))
                .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("name"))))
                .executor(new ReferralsThanks(plugin))
                .build();

        // referrals
        CommandSpec Referral = CommandSpec.builder()
                .executor(new ReferralsMainCommand())
                .child(referralHelpCommand, "help")
                .child(referralCheckCommand, "check")
                .child(referralThanksCommand, "thanks")
                .child(referralTopCommand, "top")
                .build();

        Sponge.getCommandManager().register(plugin, Referral, "referrals");
    }

}

// Example Command
//    CommandSpec Referral = CommandSpec.builder()
//            .description(Text.of("Basic command for Referrals plugin"))
//            .permission("referrals.command.referral")
//            .executor(new ReferralsMainCommand())
//            .build();