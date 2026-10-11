package fr.pandacube.lib.cli.commands;

import fr.pandacube.lib.chat.Chat;
import fr.pandacube.lib.util.log.Log;
import net.kyori.adventure.text.Component;

/**
 * The console command sender.
 */
public class CLIConsoleCommandSender implements CLICommandSender {

    /**
     * Creates a new console command sender.
     */
    protected CLIConsoleCommandSender() {}

    public String getName() {
        return "Console";
    }

    public boolean isPlayer() {
        return false;
    }

    public boolean isConsole() {
        return true;
    }

    public boolean hasPermission(String permission) {
        return true;
    }

    public void sendMessage(String message) {
        Log.info(message);
    }

    @Override
    public void sendMessage(Component message) {
        sendMessage(Chat.chatComponent(message).getLegacyText());
    }
}
