package fr.pandacube.lib.paper.commands;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import fr.pandacube.lib.chat.Chat;
import fr.pandacube.lib.commands.BrigadierCommand;
import fr.pandacube.lib.commands.SuggestionsSupplier;
import fr.pandacube.lib.paper.permissions.PandalibPaperPermissions;
import fr.pandacube.lib.paper.reflect.PandalibPaperReflect;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftServer;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftVector;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.VanillaCommandWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.BlockPosArgument;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.Commands;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.ComponentArgument;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.Coordinates;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.EntityArgument;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.EntitySelector;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.Vec3Argument;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.core.BlockPos;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.ServerPlayer;
import fr.pandacube.lib.paper.reflect.wrapper.paper.PaperAdventure;
import fr.pandacube.lib.players.standalone.AbstractOffPlayer;
import fr.pandacube.lib.players.standalone.AbstractOnlinePlayer;
import fr.pandacube.lib.players.standalone.AbstractPlayerManager;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.util.Log;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

/**
 * Abstract class to hold a command to be integrated into a Paper server vanilla command dispatcher.
 */
public abstract class PaperBrigadierCommand extends BrigadierCommand<BukkitBrigadierCommandSource> implements Listener {

    private static final Commands vanillaCommandDispatcher;
    private static final CommandDispatcher<BukkitBrigadierCommandSource> nmsDispatcher;

    static {
        wrapEx(PandalibPaperReflect::init);
        vanillaCommandDispatcher = ReflectWrapper.wrapTyped(Bukkit.getServer(), CraftServer.class)
                .getServer()
                .vanillaCommandDispatcher();
        nmsDispatcher = vanillaCommandDispatcher.dispatcher();
    }

    /**
     * Removes a plugin command that overrides a vanilla command, so the vanilla command functionalities are fully
     * restored (so, not only the usage, but also the suggestions and the command structure sent to the client).
     * @param name the name of the command to restore.
     */
    public static void restoreVanillaCommand(String name) {
        CommandMap bukkitCmdMap = Bukkit.getCommandMap();
        Command bukkitCommand = bukkitCmdMap.getCommand(name);
        if (bukkitCommand != null) {
            if (VanillaCommandWrapper.REFLECT.get().isInstance(bukkitCommand)) {
                //Log.info("Command /" + name + " is already a vanilla command.");
                return;
            }
            Log.info("Removing Bukkit command /" + name + " (" + getCommandIdentity(bukkitCommand) + ")");
            bukkitCmdMap.getKnownCommands().remove(name.toLowerCase(java.util.Locale.ENGLISH));
            bukkitCommand.unregister(bukkitCmdMap);

            LiteralCommandNode<BukkitBrigadierCommandSource> node = (LiteralCommandNode<BukkitBrigadierCommandSource>) getRootNode().getChild(name);
            Command newCommand = new VanillaCommandWrapper(vanillaCommandDispatcher, node).__getRuntimeInstance();
            bukkitCmdMap.getKnownCommands().put(name.toLowerCase(), newCommand);
            newCommand.register(bukkitCmdMap);
        }
    }


    /**
     * Returns the vanilla instance of the Brigadier dispatcher.
     * @return the vanilla instance of the Brigadier dispatcher.
     */
    public static CommandDispatcher<BukkitBrigadierCommandSource> getNMSDispatcher() {
        return nmsDispatcher;
    }

    /**
     * Returns the root command node of the Brigadier dispatcher.
     * @return the root command node of the Brigadier dispatcher.
     */
    protected static RootCommandNode<BukkitBrigadierCommandSource> getRootNode() {
        return nmsDispatcher.getRoot();
    }









    private final Plugin plugin;

    /**
     * The command node of this command.
     */
    protected final LiteralCommandNode<BukkitBrigadierCommandSource> commandNode;

    private final RegistrationPolicy registrationPolicy;

    private Set<String> registeredAliases;

    /**
     * Instantiate this command instance.
     *
     * @param pl the plugin instance.
     * @param regPolicy the registration policy for this command.
     */
    public PaperBrigadierCommand(Plugin pl, RegistrationPolicy regPolicy) {
        plugin = pl;
        registrationPolicy = regPolicy;
        commandNode = buildCommand().build();
        postBuildCommand(commandNode);
        register();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        try {
            PandalibPaperPermissions.addPermissionMapping("minecraft.command." + commandNode.getLiteral().toLowerCase(), getTargetPermission().toLowerCase());
        } catch (NoClassDefFoundError ignored) { }
    }

    /**
     * Instantiate this command instance with a registration policy of {@link RegistrationPolicy#ONLY_BASE_COMMAND}.
     * @param pl the plugin instance.
     */
    public PaperBrigadierCommand(Plugin pl) {
        this(pl, RegistrationPolicy.ONLY_BASE_COMMAND);
    }




    private void register() {

        String[] aliases = getAliases();
        if (aliases == null)
            aliases = new String[0];

        String pluginName = plugin.getName().toLowerCase();

        registeredAliases = new HashSet<>();
        registerNode(commandNode, false);
        registerAlias(pluginName + ":" + commandNode.getLiteral(), true);

        for (String alias : aliases) {
            registerAlias(alias, false);
            registerAlias(pluginName + ":" + alias, true);
        }
    }


    private void registerAlias(String alias, boolean prefixed) {
        LiteralCommandNode<BukkitBrigadierCommandSource> node = literal(alias)
                .requires(commandNode.getRequirement())
                .executes(commandNode.getCommand())
                .redirect(commandNode)
                .build();
        registerNode(node, prefixed);
    }


    private void registerNode(LiteralCommandNode<BukkitBrigadierCommandSource> node, boolean prefixed) {
        RootCommandNode<BukkitBrigadierCommandSource> root = getRootNode();
        String name = node.getLiteral();
        boolean isAlias = node.getRedirect() == commandNode;
        boolean forceRegistration = switch (registrationPolicy) {
            case NONE -> false;
            case ONLY_BASE_COMMAND -> prefixed || !isAlias;
            case ALL -> true;
        };

        // nmsDispatcher integration and conflit resolution
        boolean nmsRegister = false, nmsRegistered = false;
        CommandNode<BukkitBrigadierCommandSource> nmsConflicted = root.getChild(name);
        if (nmsConflicted != null) {

            if (isFromThisCommand(nmsConflicted)) {
                // this command is already registered in NMS. Don’t need to register again
                nmsRegistered = true;
            }
            else if (forceRegistration) {
                nmsRegister = true;
                Log.info("Overwriting Brigadier command /" + name);
            }
            else if (prefixed || !isAlias) {
                Log.severe("/" + name + " already in NMS Brigadier instance."
                        + " Wont replace it because registration is not forced for prefixed or initial name of a command.");
            }
            else { // conflict, won't replace, not forced but only an alias anyway
                Log.info("/" + name + " already in NMS Brigadier instance."
                        + " Wont replace it because registration is not forced for a non-prefixed alias.");
            }
        }
        else {
            nmsRegister = true;
        }

        if (nmsRegister) {
            @SuppressWarnings("unchecked")
            var rCommandNode = ReflectWrapper.wrapTyped(root, fr.pandacube.lib.paper.reflect.wrapper.brigadier.CommandNode.class);
            rCommandNode.removeCommand(name);
            root.addChild(node);
            nmsRegistered = true;
        }

        if (!nmsRegistered) {
            return;
        }

        registeredAliases.add(name);

        // bukkit dispatcher conflict resolution
        boolean bukkitRegister = false;
        CommandMap bukkitCmdMap = Bukkit.getCommandMap();
        Command bukkitConflicted = bukkitCmdMap.getCommand(name);
        if (bukkitConflicted != null) {
            if (!isFromThisCommand(bukkitConflicted)) {
                if (forceRegistration) {
                    bukkitRegister = true;
                    Log.info("Overwriting Bukkit command /" + name
                            + " (" + getCommandIdentity(bukkitConflicted) + ")");
                }
                else if (prefixed || !isAlias) {
                    Log.severe("/" + name + " already in Bukkit dispatcher (" + getCommandIdentity(bukkitConflicted) + ")." +
                            " Wont replace it because registration is not forced for prefixed or initial name of a command.");
                }
                else {
                    Log.info("/" + name + " already in Bukkit dispatcher (" + getCommandIdentity(bukkitConflicted) + ")." +
                            " Wont replace it because registration is not forced for a non-prefixed alias.");
                }
            }
        }
        else {
            bukkitRegister = true;
        }

        if (bukkitRegister) {
            bukkitCmdMap.getKnownCommands().remove(name.toLowerCase());
            if (bukkitConflicted != null)
                bukkitConflicted.unregister(bukkitCmdMap);

            Command newCommand = new VanillaCommandWrapper(vanillaCommandDispatcher, node).__getRuntimeInstance();
            bukkitCmdMap.getKnownCommands().put(name.toLowerCase(), newCommand);
            newCommand.register(bukkitCmdMap);
        }

    }

    private boolean isFromThisCommand(CommandNode<BukkitBrigadierCommandSource> node) {
        return node == commandNode || node.getRedirect() == commandNode;
    }

    private boolean isFromThisCommand(Command bukkitCmd) {
        if (VanillaCommandWrapper.REFLECT.get().isInstance(bukkitCmd)) {
            return isFromThisCommand(ReflectWrapper.wrapTyped((BukkitCommand) bukkitCmd, VanillaCommandWrapper.class).vanillaCommand());
        }
        return false;
    }

    private static String getCommandIdentity(Command bukkitCmd) {
        if (bukkitCmd instanceof PluginCommand cmd) {
            return "Bukkit command: /" + cmd.getName() + " from plugin " + cmd.getPlugin().getName();
        }
        else if (VanillaCommandWrapper.REFLECT.get().isInstance(bukkitCmd)) {
            return "Vanilla command: /" + bukkitCmd.getName();
        }
        else
            return bukkitCmd.getClass().getName() + ": /" + bukkitCmd.getName();
    }


    /**
     * Player command sender event handler.
     * @param event the event.
     */
    @EventHandler
    public void onPlayerCommandSend(PlayerCommandSendEvent event) {
        event.getCommands().removeAll(registeredAliases.stream().map(s -> "minecraft:" + s).toList());
    }


    /**
     * Server load event handler.
     * @param event the event.
     */
    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        register();
    }











    /**
     * Returns the permission that should be tested instead of "minecraft.command.cmdName". The conversion from the
     * minecraft prefixed permission node to the returned node is done by the {@code pandalib-paper-permissions} if it
     * is present in the classpath during runtime.
     * @return the permission that should be tested instead of "minecraft.command.cmdName".
     */
    protected abstract String getTargetPermission();














    public boolean isConsole(BukkitBrigadierCommandSource wrapper) {
        return isConsole(getCommandSender(wrapper));
    }
    public boolean isPlayer(BukkitBrigadierCommandSource wrapper) {
        return isPlayer(getCommandSender(wrapper));
    }
    public Predicate<BukkitBrigadierCommandSource> hasPermission(String permission) {
        return wrapper -> getCommandSender(wrapper).hasPermission(permission);
    }







    /**
     * Tells if the provided command sender is the console.
     * @param sender the sender to test if it’s the console or not.
     * @return true if the sender is the console, false otherwise.
     */
    public boolean isConsole(CommandSender sender) {
        return sender instanceof ConsoleCommandSender;
    }

    /**
     * Tells if the provided command sender is a player.
     * @param sender the sender to test if it’s a player or not.
     * @return true if the sender is a player, false otherwise.
     */
    public boolean isPlayer(CommandSender sender) {
        return sender instanceof Player;
    }


    /**
     * Gets the Bukkit command sender from the provided context.
     * @param context the command context from which to get the Bukkit command sender.
     * @return the Bukkit command sender.
     */
    public static CommandSender getCommandSender(CommandContext<BukkitBrigadierCommandSource> context) {
        return getCommandSender(context.getSource());
    }

    /**
     * Gets the Bukkit command sender from the provided wrapper.
     * @param wrapper the wrapper from which to get the Bukkit command sender.
     * @return the Bukkit command sender.
     */
    public static CommandSender getCommandSender(BukkitBrigadierCommandSource wrapper) {
        return wrapper.getBukkitSender();
    }

    /**
     * Gets a new instance of a command sender wrapper for the provided command sender.
     * @param sender the command sender.
     * @return a new instance of a command sender wrapper for the provided command sender.
     */
    public static BukkitBrigadierCommandSource getBrigadierCommandSource(CommandSender sender) {
        return VanillaCommandWrapper.getListener(sender);
    }


    /**
     * A suggestion supplier that suggests the names of the currently connected players (that the command sender can see).
     */
    public static final SuggestionsSupplier<CommandSender> TAB_PLAYER_CURRENT_SERVER =  (sender, ti, token, a) -> {
        @SuppressWarnings("unchecked")
        AbstractPlayerManager<AbstractOnlinePlayer, AbstractOffPlayer> pm = (AbstractPlayerManager<AbstractOnlinePlayer, AbstractOffPlayer>) AbstractPlayerManager.getInstance();
        Stream<String> plStream;
        if (pm == null)
            plStream = Bukkit.getOnlinePlayers().stream().filter(p -> !(sender instanceof Player senderP) || senderP.canSee(p)).map(Player::getName);
        else
            plStream = pm.getNamesOnlyVisible(sender instanceof Player senderP ? pm.getOffline(senderP.getUniqueId()) : null).stream();
        return SuggestionsSupplier.collectFilteredStream(plStream, token);
    };

    /**
     * A suggestion supplier that suggests the names of the worlds currently loaded on this server.
     */
    public static final SuggestionsSupplier<CommandSender> TAB_WORLDS = SuggestionsSupplier.fromStreamSupplier(() -> Bukkit.getWorlds().stream().map(World::getName));


    /**
     * Wraps the provided {@link SuggestionsSupplier} into a Brigadier’s {@link SuggestionProvider}.
     * @param suggestions the suggestions to wrap.
     * @return a {@link SuggestionProvider} generating the suggestions from the provided {@link SuggestionsSupplier}.
     */
    protected SuggestionProvider<BukkitBrigadierCommandSource> wrapSuggestions(SuggestionsSupplier<CommandSender> suggestions) {
        return wrapSuggestions(suggestions, PaperBrigadierCommand::getCommandSender);
    }


    /**
     * Wraps the provided brigadier command executor in another one that logs eventual throw exceptions and informs the
     * player.
     * The default behaviour of the vanilla instance of the Brigadier dispatcher is to ignore any unchecked exception
     * thrown by a command executor.
     * @param cmd the command executor to wrap.
     * @return a wrapper command executor.
     */
    protected static com.mojang.brigadier.Command<BukkitBrigadierCommandSource> wrapCommand(com.mojang.brigadier.Command<BukkitBrigadierCommandSource> cmd) {
        return context -> {
            try {
                return cmd.run(context);
            } catch(CommandSyntaxException e) {
                throw e;
            } catch (Throwable t) {
                Log.severe(t);
                getCommandSender(context).sendMessage(Chat.failureText("Error while executing the command: " + t));
                return 0;
            }
        };
    }








    /*
     * Minecraft's argument type
     */


    /**
     * Creates a new instance of the Brigadier argument type {@code minecraft:entity}.
     * @param singleTarget if this argument takes only a single target.
     * @param playersOnly if this argument takes players only.
     * @return the {@code minecraft:entity} argument type with the specified parameters.
     */
    public static ArgumentType<Object> argumentMinecraftEntity(boolean singleTarget, boolean playersOnly) {
        if (playersOnly) {
            return singleTarget ? EntityArgument.player() : EntityArgument.players();
        }
        else {
            return singleTarget ? EntityArgument.entity() : EntityArgument.entities();
        }
    }

    /**
     * Gets the value of the provided argument of type {@code minecraft:entity} (list of entities), from the provided context.
     * @param context the command execution context.
     * @param argument the argument name.
     * @return the value of the argument, or null if not found.
     */
    public List<Entity> tryGetMinecraftEntityArgument(CommandContext<BukkitBrigadierCommandSource> context, String argument) {
        EntitySelector es = ReflectWrapper.wrap(tryGetArgument(context, argument, EntitySelector.MAPPING.runtimeClass()), EntitySelector.class);
        if (es == null)
            return null;
        List<fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.Entity> nmsEntityList = es.findEntities(context.getSource());
        List<Entity> entityList = new ArrayList<>(nmsEntityList.size());
        for (fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.Entity nmsEntity : nmsEntityList) {
            entityList.add(nmsEntity.getBukkitEntity());
        }
        return entityList;
    }

    /**
     * Gets the value of the provided argument of type {@code minecraft:entity} (list of players), from the provided context.
     * @param context the command execution context.
     * @param argument the argument name.
     * @return the value of the argument, or null if not found.
     */
    public List<Player> tryGetMinecraftEntityArgumentPlayers(CommandContext<BukkitBrigadierCommandSource> context, String argument) {
        EntitySelector es = ReflectWrapper.wrap(tryGetArgument(context, argument, EntitySelector.MAPPING.runtimeClass()), EntitySelector.class);
        if (es == null)
            return null;
        List<ServerPlayer> nmsPlayerList = es.findPlayers(context.getSource());
        List<Player> playerList = new ArrayList<>(nmsPlayerList.size());
        for (ServerPlayer nmsPlayer : nmsPlayerList) {
            playerList.add(nmsPlayer.getBukkitEntity());
        }
        return playerList;
    }

    /**
     * Gets the value of the provided argument of type {@code minecraft:entity} (one entity), from the provided context.
     * @param context the command execution context.
     * @param argument the argument name.
     * @return the value of the argument, or null if not found.
     */
    public Entity tryGetMinecraftEntityArgumentOneEntity(CommandContext<BukkitBrigadierCommandSource> context, String argument) {
        EntitySelector es = ReflectWrapper.wrap(tryGetArgument(context, argument, EntitySelector.MAPPING.runtimeClass()), EntitySelector.class);
        if (es == null)
            return null;
        fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.Entity nmsEntity = es.findSingleEntity(context.getSource());
        return nmsEntity == null ? null : nmsEntity.getBukkitEntity();
    }

    /**
     * Gets the value of the provided argument of type {@code minecraft:entity} (one player), from the provided context.
     * @param context the command execution context.
     * @param argument the argument name.
     * @return the value of the argument, or null if not found.
     */
    public Player tryGetMinecraftEntityArgumentOnePlayer(CommandContext<BukkitBrigadierCommandSource> context, String argument) {
        EntitySelector es = ReflectWrapper.wrap(tryGetArgument(context, argument, EntitySelector.MAPPING.runtimeClass()), EntitySelector.class);
        if (es == null)
            return null;
        ServerPlayer nmsPlayer = es.findSinglePlayer(context.getSource());
        return nmsPlayer == null ? null : nmsPlayer.getBukkitEntity();
    }






    /**
     * Creates a new instance of the Brigadier argument type {@code minecraft:block_pos}.
     * @return the {@code minecraft:block_pos} argument type.
     */
    public static ArgumentType<Object> argumentMinecraftBlockPosition() {
        return BlockPosArgument.blockPos();
    }

    /**
     * Gets the value of the provided argument of type {@code minecraft:block_pos}, from the provided context.
     * @param context the command execution context.
     * @param argument the argument name.
     * @param deflt a default value if the argument is not found.
     * @return the value of the argument.
     */
    public BlockVector tryGetMinecraftBlockPositionArgument(CommandContext<BukkitBrigadierCommandSource> context,
                                                            String argument, BlockVector deflt) {
        return tryGetArgument(context, argument, Coordinates.MAPPING.runtimeClass(), nmsCoordinate -> {
            BlockPos bp = ReflectWrapper.wrap(nmsCoordinate, Coordinates.class).getBlockPos(context.getSource());
            return new BlockVector(bp.getX(), bp.getY(), bp.getZ());
        }, deflt);
    }




    /**
     * Creates a new instance of the Brigadier argument type {@code minecraft:vec3}.
     * @return the {@code minecraft:vec3} argument type.
     */
    public static ArgumentType<Object> argumentMinecraftVec3() {
        return Vec3Argument.vec3(true);
    }

    /**
     * Gets the value of the provided argument of type {@code minecraft:vec3}, from the provided context.
     * @param context the command execution context.
     * @param argument the argument name.
     * @param deflt a default value if the argument is not found.
     * @return the value of the argument.
     */
    public Vector tryGetMinecraftVec3Argument(CommandContext<BukkitBrigadierCommandSource> context, String argument,
                                              Vector deflt) {
        return tryGetArgument(context, argument, Coordinates.MAPPING.runtimeClass(),
                nmsCoordinate -> CraftVector.toBukkit(
                        ReflectWrapper.wrap(nmsCoordinate, Coordinates.class).getPosition(context.getSource())
                ),
                deflt);
    }




    /**
     * Creates a new instance of the Brigadier argument type {@code minecraft:component}.
     * @return the {@code minecraft:component} argument type.
     */
    public static ArgumentType<Object> argumentMinecraftChatComponent() {
        return ComponentArgument.textComponent();
    }

    /**
     * Gets the value of the provided argument of type {@code minecraft:component}, from the provided context.
     * @param context the command execution context.
     * @param argument the argument name.
     * @param deflt a default value if the argument is not found.
     * @return the value of the argument.
     */
    public Component tryGetMinecraftChatComponentArgument(CommandContext<BukkitBrigadierCommandSource> context,
                                                          String argument, Component deflt) {
        return tryGetArgument(context, argument,
                fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.chat.Component.MAPPING.runtimeClass(),
                nmsComp -> PaperAdventure.asAdventure(
                        ReflectWrapper.wrap(nmsComp,
                                fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.chat.Component.class)
                ),
                deflt);

    }


    /**
     * All possible choices on how to force the registration of a command, based on certain conditions.
     */
    public enum RegistrationPolicy {
        /**
         * Do not force to register a command node or an alias if there is already a command with that name in the
         * vanilla Brigadier dispatcher.
         * Note that all plugin-name-prefixed aliases will be registered anyway.
         */
        NONE,
        /**
         * Force only the base command (but not the aliases) to be registered, even if a command with that name already
         * exists in the vanilla Brigadier dispatcher.
         */
        ONLY_BASE_COMMAND,
        /**
         * Force the command and all of its aliases to be registered, even if a command with the same name or alias
         * already exists in the vanilla Brigadier dispatcher.
         */
        ALL
    }

}
