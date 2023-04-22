package fr.pandacube.lib.cli.commands;


import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fr.pandacube.lib.cli.CLIApplication;

/**
 * /stop (/end) command.
 */
public class CommandStop extends CLIBrigadierCommand {

	@Override
	protected LiteralArgumentBuilder<CLICommandSender> buildCommand() {
		return literal("stop")
				.executes(context -> {
					CLIApplication.getInstance().stop();
					return 1;
				});
	}

	@Override
	protected String[] getAliases() {
		return new String[] { "end" };
	}
}
