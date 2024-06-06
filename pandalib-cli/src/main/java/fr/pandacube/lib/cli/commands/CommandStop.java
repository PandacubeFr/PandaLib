package fr.pandacube.lib.cli.commands;


import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fr.pandacube.lib.cli.CLIApplication;

/**
 * the {@code stop} (or {@code end}) command for a {@link CLIApplication}.
 */
public class CommandStop extends CLIBrigadierCommand {

	/**
	 * Initializes the admin command.
	 */
	public CommandStop() {}

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
