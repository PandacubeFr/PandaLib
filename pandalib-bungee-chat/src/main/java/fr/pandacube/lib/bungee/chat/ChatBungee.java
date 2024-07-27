package fr.pandacube.lib.bungee.chat;

import fr.pandacube.lib.chat.Chat;
import fr.pandacube.lib.chat.Chat.FormatableChat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;

public class ChatBungee {



    /**
     * Creates a {@link FormatableChat} from the provided Bungee {@link BaseComponent}.
     * @param c the {@link BaseComponent}.
     * @return a new {@link FormatableChat}.
     */
    public static FormatableChat chatComponent(BaseComponent c) {
        return Chat.chatComponent(toAdventure(c));
    }



    /**
     * Creates a {@link FormatableChat} from the provided Bungee {@link BaseComponent BaseComponent[]}.
     * @param c the array of {@link BaseComponent}.
     * @return a new {@link FormatableChat}.
     */
    public static FormatableChat chatComponent(BaseComponent[] c) {
        return Chat.chatComponent(toAdventure(c));
    }



    /**
     * Converts the Bungee {@link BaseComponent} array into Adventure {@link Component}.
     * @param components the Bungee {@link BaseComponent} array.
     * @return a {@link Component}.
     */
    public static Component toAdventure(BaseComponent[] components) {
        return BungeeComponentSerializer.get().deserialize(components);
    }
    /**
     * Converts the Bungee {@link BaseComponent} into Adventure {@link Component}.
     * @param component the Bungee {@link BaseComponent}.
     * @return a {@link Component}.
     */
    public static Component toAdventure(BaseComponent component) {
        return toAdventure(new BaseComponent[] { component });
    }

    /**
     * Converts the Adventure {@link Component} into Bungee {@link BaseComponent} array.
     * @param component the Adventure {@link Component}.
     * @return a {@link BaseComponent} array.
     */
    public static BaseComponent[] toBungeeArray(ComponentLike component) {
        return BungeeComponentSerializer.get().serialize(component.asComponent());
    }

    /**
     * Converts the Adventure {@link Component} into Bungee {@link BaseComponent}.
     * @param component the Adventure {@link Component}.
     * @return a {@link BaseComponent}.
     */
    public static BaseComponent toBungee(ComponentLike component) {
        BaseComponent[] arr = toBungeeArray(component);
        return arr.length == 1 ? arr[0] : new net.md_5.bungee.api.chat.TextComponent(arr);
    }
}
