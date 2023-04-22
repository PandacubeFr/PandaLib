package fr.pandacube.lib.cli.commands;

import fr.pandacube.lib.chat.Chat;
import fr.pandacube.lib.util.Log;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;

/**
 * The console command sender.
 */
public class CLIConsoleCommandSender implements CLICommandSender {
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
    public void sendMessage(Identity source, Component message, MessageType type) {
        sendMessage(Chat.chatComponent(message).getLegacyText());
    }
}
