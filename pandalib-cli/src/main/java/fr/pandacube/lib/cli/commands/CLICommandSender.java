package fr.pandacube.lib.cli.commands;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * A command sender.
 */
public interface CLICommandSender extends Audience {
    /**
     * Gets the name of the sender.
     * @return The name of the sender.
     */
    String getName();

    /**
     * Tells if the sender is a player.
     * @return true if the sender is a player, false otherwise.
     */
    boolean isPlayer();

    /**
     * Tells if the sender is on the console.
     * @return true if the sender is on the console, false otherwise.
     */
    boolean isConsole();

    /**
     * Tells if the sender has the specified permission.
     * @param permission the permission to test on the sender.
     * @return true if the sender has the specified permission.
     */
    boolean hasPermission(String permission);

    /**
     * Sends the provided message to the sender.
     * @param message the message to send.
     */
    void sendMessage(String message);

    @Override // force implementation of super-interface default method
    void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type);
}
