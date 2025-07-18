package fr.pandacube.lib.paper.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import fr.pandacube.lib.chat.Chat;
import fr.pandacube.lib.commands.BadCommandUsage;
import fr.pandacube.lib.commands.BrigadierCommand;
import fr.pandacube.lib.commands.SuggestionsSupplier;
import fr.pandacube.lib.paper.PandaLibPaper;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftVector;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.VanillaCommandWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.Coordinates;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.Vec3Argument;
import fr.pandacube.lib.paper.reflect.wrapper.paper.commands.APICommandMeta;
import fr.pandacube.lib.paper.reflect.wrapper.paper.commands.BukkitCommandNode;
import fr.pandacube.lib.players.standalone.AbstractOffPlayer;
import fr.pandacube.lib.players.standalone.AbstractOnlinePlayer;
import fr.pandacube.lib.players.standalone.AbstractPlayerManager;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.util.log.Log;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static fr.pandacube.lib.reflect.wrapper.ReflectWrapper.wrap;

/**
 * Abstract class to hold a command to be integrated into a Paper server vanilla command dispatcher.
 */
@SuppressWarnings("UnstableApiUsage")
public abstract class PaperBrigadierCommand extends BrigadierCommand<CommandSourceStack> implements Listener {

    private static CommandDispatcher<CommandSourceStack> vanillaPaperDispatcher = null;

    /**
     * Gets the Brigadier dispatcher provided by paper API during {@link LifecycleEvents#COMMANDS}.
     * <p>
     * This Dispatcher is not the vanilla one. Instead, Paper implementation wraps the vanilla one to handle proper registration
     * of commands from plugins.
     * @return the Brigadier dispatcher.
     */
    public static CommandDispatcher<CommandSourceStack> getVanillaPaperDispatcher() {
        return vanillaPaperDispatcher;
    }

    /**
     * Gets the root node of the dispatcher from {@link #getVanillaPaperDispatcher()}.
     * @return the root node, or null if {@link #getVanillaPaperDispatcher()} is also null.
     */
    public static RootCommandNode<CommandSourceStack> getRootNode() {
        return vanillaPaperDispatcher == null ? null : vanillaPaperDispatcher.getRoot();
    }

    private static void updateVanillaPaperDispatcher(CommandDispatcher<CommandSourceStack> newDispatcher) {
        if (vanillaPaperDispatcher == null || newDispatcher != vanillaPaperDispatcher) {
            vanillaPaperDispatcher = newDispatcher;

            // vanillaPaperDispatcher.getRoot() is not the real root but a wrapped root. Trying to map the fake root with the real one to trick the Paper/Brigadier (un)wrapper
            RootCommandNode<CommandSourceStack> wrappedRoot = vanillaPaperDispatcher.getRoot();
            ReflectClass<?> apiMirrorRootNodeClass = Reflect.ofClassOfInstance(wrappedRoot);
            try {
                RootCommandNode<?> unwrappedRoot = ((CommandDispatcher<?>) apiMirrorRootNodeClass.method("getDispatcher").invoke(wrappedRoot)).getRoot();

                Reflect.ofClass(CommandNode.class).field("unwrappedCached").setValue(wrappedRoot, unwrappedRoot);
                Reflect.ofClass(CommandNode.class).field("wrappedCached").setValue(unwrappedRoot, wrappedRoot);

            } catch (InvocationTargetException|IllegalAccessException|NoSuchMethodException|NoSuchFieldException e) {
                Log.severe("Unable to trick the Paper/Brigadier unwrapper to properly handle commands redirecting to root command node.", e);
            }
        }
    }



    /**
     * Removes a plugin command that overrides a vanilla command, so the vanilla command functionalities are fully
     * restored (so, not only the usage, but also the suggestions and the command structure sent to the client).
     * @param name the name of the command to restore.
     */
    public static void restoreVanillaCommand(String name) {

        PandaLibPaper.getPlugin().getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                event -> updateVanillaPaperDispatcher(event.registrar().getDispatcher()));


        Bukkit.getServer().getScheduler().runTask(PandaLibPaper.getPlugin(), () -> {
            if (vanillaPaperDispatcher == null)
                return;

            CommandNode<CommandSourceStack> targetCommand = vanillaPaperDispatcher.getRoot().getChild("minecraft:" + name);
            if (targetCommand == null) {
                Log.warning("There is no vanilla command '" + name + "' to restore.");
                return;
            }

            CommandNode<CommandSourceStack> eventuallyBadCommandToReplace = vanillaPaperDispatcher.getRoot().getChild(name);
            Boolean isPluginCommand = isPluginCommand(eventuallyBadCommandToReplace);
            if (isPluginCommand != null && isPluginCommand) {
                Log.info(getCommandIdentity(eventuallyBadCommandToReplace) + " found in the dispatcher. Restoring the vanilla command.");
                vanillaPaperDispatcher.getRoot().getChildren().removeIf(c -> c.getName().equals(name));
                vanillaPaperDispatcher.getRoot().addChild(getAliasNode(targetCommand, name));
            }
            /*else if (isPluginCommand == null) {
                Log.info(getCommandIdentity(eventuallyBadCommandToReplace) + " found in the dispatcher. Unsure if we restore the vanilla command.");
            }*/
        });
    }





    private final Plugin plugin;

    /**
     * The command node of this command.
     */
    protected LiteralCommandNode<CommandSourceStack> commandNode;
    /**
     * The command requested aliases.
     */
    protected final String[] aliases;

    /**
     * The command description.
     */
    protected final String description;

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
        String[] aliasesTmp = getAliases();
        aliases = aliasesTmp == null ? new String[0] : aliasesTmp;
        description = getDescription();
        register();
        //try {
        //    PandalibPaperPermissions.addPermissionMapping("minecraft.command." + commandNode.getLiteral().toLowerCase(), getTargetPermission().toLowerCase());
        //} catch (NoClassDefFoundError ignored) { }
    }

    /**
     * Instantiate this command instance with a registration policy of {@link RegistrationPolicy#ONLY_BASE_COMMAND}.
     * @param pl the plugin instance.
     */
    public PaperBrigadierCommand(Plugin pl) {
        this(pl, RegistrationPolicy.ONLY_BASE_COMMAND);
    }




    private void register() {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            updateVanillaPaperDispatcher(event.registrar().getDispatcher());

            commandNode = buildCommand().build();
            postBuildCommand(commandNode);

            if (vanillaPaperDispatcher.getRoot().getChild(commandNode.getName()) != null) {
                Log.info("Command /" + commandNode.getName() + " found in the vanilla dispatcher during initial command registration. Replacing it by force.");
                vanillaPaperDispatcher.getRoot().getChildren().removeIf(c -> c.getName().equals(commandNode.getName()));
            }

            registeredAliases = new HashSet<>(event.registrar().register(commandNode, description, List.of(aliases)));
            doPostRegistrationFixes();

            @SuppressWarnings("unchecked")
            fr.pandacube.lib.paper.reflect.wrapper.brigadier.CommandNode<CommandSourceStack> registeredNode = wrap(vanillaPaperDispatcher.getRoot().getChild(commandNode.getName()), fr.pandacube.lib.paper.reflect.wrapper.brigadier.CommandNode.class);


            if (registrationPolicy == RegistrationPolicy.ALL) {
                // enforce registration of aliases
                for (String alias : aliases) {
                    if (!registeredAliases.contains(alias)) {
                        Log.info("Command /" + commandNode.getName() + ": forcing registration of alias " + alias);
                        registeredAliases.addAll(event.registrar().register(getAliasNode(commandNode, alias), description));
                    }
                }
            }


            Bukkit.getServer().getScheduler().runTask(plugin, () -> {
                if (vanillaPaperDispatcher == null)
                    return;

                Set<String> forceRegistrationAgain = new HashSet<>();
                forceRegistrationAgain.add(commandNode.getName());
                if (registrationPolicy == RegistrationPolicy.ALL)
                    forceRegistrationAgain.addAll(List.of(aliases));

                for (String aliasToForce : forceRegistrationAgain) {
                    CommandNode<CommandSourceStack> actualNode = vanillaPaperDispatcher.getRoot().getChild(aliasToForce);
                    @SuppressWarnings("unchecked")
                    fr.pandacube.lib.paper.reflect.wrapper.brigadier.CommandNode<CommandSourceStack> wrappedCommandNode = wrap(actualNode, fr.pandacube.lib.paper.reflect.wrapper.brigadier.CommandNode.class);
                    if (actualNode != null) {
                        if (wrappedCommandNode.apiCommandMeta() != null) {
                            APICommandMeta meta = wrappedCommandNode.apiCommandMeta();
                            if (meta.plugin().equals(plugin))
                                return;
                        }
                        else if (BukkitCommandNode.REFLECT.get().isInstance(actualNode)) {
                            BukkitCommandNode bcn = wrap(actualNode, BukkitCommandNode.class);
                            if (bcn.getBukkitCommand() instanceof PluginCommand pc && pc.getPlugin().equals(plugin))
                                return;
                        }
                        Log.warning("Forcing registration of alias /" + aliasToForce + " for command /" + commandNode.getName() + ": replacing " + getCommandIdentity(actualNode));
                        vanillaPaperDispatcher.getRoot().getChildren().removeIf(c -> c.getName().equals(aliasToForce));
                    }
                    /*else {
                        Log.info("Forcing registration of alias /" + aliasToForce + " for command /" + commandNode.getName() + ": no command found for alias. Adding alias.");
                    }*/
                    LiteralCommandNode<CommandSourceStack> newPCN = getAliasNode(commandNode, aliasToForce);
                    @SuppressWarnings("unchecked")
                    fr.pandacube.lib.paper.reflect.wrapper.brigadier.CommandNode<CommandSourceStack> wrappedNewPCN = wrap(newPCN, fr.pandacube.lib.paper.reflect.wrapper.brigadier.CommandNode.class);
                    wrappedNewPCN.apiCommandMeta(registeredNode.apiCommandMeta());
                    vanillaPaperDispatcher.getRoot().addChild(newPCN);
                }
            });

        });

    }




    private void doPostRegistrationFixes() {
        postRegistrationFixNode(new HashSet<>(), commandNode);
    }

    private void postRegistrationFixNode(Set<CommandNode<CommandSourceStack>> fixedNodes, CommandNode<CommandSourceStack> originalNode) {
        if (originalNode instanceof RootCommandNode)
            return;
        if (fixedNodes.contains(originalNode))
            return;
        fixedNodes.add(originalNode);
        if (originalNode.getRedirect() != null) {
            try {
                @SuppressWarnings("rawtypes")
                ReflectClass<CommandNode> cmdNodeClass = Reflect.ofClass(CommandNode.class);
                @SuppressWarnings("unchecked")
                CommandNode<CommandSourceStack> unwrappedNode = (CommandNode<CommandSourceStack>) cmdNodeClass.field("unwrappedCached").getValue(originalNode);
                if (unwrappedNode != null) {
                    cmdNodeClass.field("modifier").setValue(unwrappedNode, cmdNodeClass.field("modifier").getValue(originalNode));
                    cmdNodeClass.field("forks").setValue(unwrappedNode, cmdNodeClass.field("forks").getValue(originalNode));
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }

            postRegistrationFixNode(fixedNodes, originalNode.getRedirect());
        }
        else {
            try {
                for (CommandNode<CommandSourceStack> child : originalNode.getChildren())
                    postRegistrationFixNode(fixedNodes, child);
            } catch (UnsupportedOperationException ignored) {
                // in case getChildren is not possible (vanilla commands are wrapped by Paper API)
            }
        }
    }


    private static LiteralCommandNode<CommandSourceStack> getAliasNode(CommandNode<CommandSourceStack> commandNode, String alias) {
         return LiteralArgumentBuilder.<CommandSourceStack>literal(alias)
                .requires(commandNode.getRequirement())
                .executes(commandNode.getCommand())
                .redirect(commandNode)
                .build();
    }

    private static String getCommandIdentity(CommandNode<CommandSourceStack> command) {

        if (BukkitCommandNode.REFLECT.get().isInstance(command)) {
            BukkitCommandNode wrappedBCN = wrap(command, BukkitCommandNode.class);
            Command bukkitCmd = wrappedBCN.getBukkitCommand();
            if (bukkitCmd instanceof PluginCommand cmd) {
                return "Node /" + command.getName() + " wrapping Bukkit command /" + bukkitCmd.getName() + " from plugin " + cmd.getPlugin().getName();
            }
            else if (VanillaCommandWrapper.REFLECT.get().isInstance(bukkitCmd)) {
                VanillaCommandWrapper vcw = wrap(bukkitCmd, VanillaCommandWrapper.class);
                CommandNode<CommandSourceStack> vanillaCmd = vcw.vanillaCommand();
                if (vanillaCmd != command)
                    return "Node /" + command.getName() + " wrapping non-plugin command /" + bukkitCmd.getName() + " wrapping: " + getCommandIdentity(vcw.vanillaCommand());
                else
                    return "Node /" + command.getName() + " wrapping non-plugin command /" + bukkitCmd.getName() + " wrapping back the node (risk of StackOverflow?)";
            }
            else
                return "Node /" + command.getName() + " wrapping " + bukkitCmd.getClass().getName() + " /" + bukkitCmd.getName();
        }
        else {
            @SuppressWarnings("unchecked")
            fr.pandacube.lib.paper.reflect.wrapper.brigadier.CommandNode<CommandSourceStack> wrappedCommandNode = wrap(command, fr.pandacube.lib.paper.reflect.wrapper.brigadier.CommandNode.class);

            if (wrappedCommandNode.apiCommandMeta() != null) {
                APICommandMeta meta = wrappedCommandNode.apiCommandMeta();
                return "Node /" + command.getName() + " from plugin " + meta.plugin().getName();
            }
            else {
                return "Node /" + command.getName() + " (unspecific)";
            }
        }
    }


    private static Boolean isPluginCommand(CommandNode<CommandSourceStack> command) {
        if (BukkitCommandNode.REFLECT.get().isInstance(command)) {
            BukkitCommandNode wrappedBCN = wrap(command, BukkitCommandNode.class);
            Command bukkitCmd = wrappedBCN.getBukkitCommand();
            if (bukkitCmd instanceof PluginCommand) {
                return true;
            }
            else if (VanillaCommandWrapper.REFLECT.get().isInstance(bukkitCmd)) {
                VanillaCommandWrapper vcw = wrap(bukkitCmd, VanillaCommandWrapper.class);
                CommandNode<CommandSourceStack> vanillaCmd = vcw.vanillaCommand();
                if (vanillaCmd != command)
                    return isPluginCommand(vcw.vanillaCommand());
                else
                    return false;
            }
            else
                return null;
        }
        else {
            @SuppressWarnings("unchecked")
            fr.pandacube.lib.paper.reflect.wrapper.brigadier.CommandNode<CommandSourceStack> wrappedCommandNode = wrap(command, fr.pandacube.lib.paper.reflect.wrapper.brigadier.CommandNode.class);
            return wrappedCommandNode.apiCommandMeta() != null;
        }
    }

    /**
     * Gets the aliases that are actually registered in the server.
     * @return the actually registered aliases.
     */
    protected Set<String> getRegisteredAliases() {
        return Set.copyOf(registeredAliases);
    }












    /**
     * Returns the permission that should be tested instead of "minecraft.command.cmdName". The conversion from the
     * minecraft prefixed permission node to the returned node is done by the {@code pandalib-paper-permissions} if it
     * is present in the classpath during runtime.
     * @return the permission that should be tested instead of "minecraft.command.cmdName".
     */
    protected abstract String getTargetPermission();

    /**
     * Returns the permission that should be tested instead of "minecraft.command.cmdName". The conversion from the
     * minecraft prefixed permission node to the returned node is done by the {@code pandalib-paper-permissions} if it
     * is present in the classpath during runtime.
     * @return the permission that should be tested instead of "minecraft.command.cmdName".
     */
    protected String getDescription() {
        return "A command from " + plugin.getName();
    }














    @Override
    public boolean isConsole(CommandSourceStack wrapper) {
        return isConsole(getCommandSender(wrapper));
    }
    @Override
    public boolean isPlayer(CommandSourceStack wrapper) {
        return isPlayer(getCommandSender(wrapper));
    }
    @Override
    public Predicate<CommandSourceStack> hasPermission(String permission) {
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
    public static CommandSender getCommandSender(CommandContext<CommandSourceStack> context) {
        return getCommandSender(context.getSource());
    }

    /**
     * Gets the Bukkit command sender from the provided wrapper.
     * @param wrapper the wrapper from which to get the Bukkit command sender.
     * @return the Bukkit command sender.
     */
    public static CommandSender getCommandSender(CommandSourceStack wrapper) {
        return wrapper.getSender();
    }

    /**
     * Gets a new instance of a command sender wrapper for the provided command sender.
     * @param sender the command sender.
     * @return a new instance of a command sender wrapper for the provided command sender.
     */
    public static CommandSourceStack getBrigadierCommandSource(CommandSender sender) {
        throw new UnsupportedOperationException("The 1.20.6 Paper API update uses a different wrapper for Brigadier command sender.");
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
    public SuggestionProvider<CommandSourceStack> wrapSuggestions(SuggestionsSupplier<CommandSender> suggestions) {
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
    protected static com.mojang.brigadier.Command<CommandSourceStack> wrapCommand(com.mojang.brigadier.Command<CommandSourceStack> cmd) {
        return context -> {
            try {
                return cmd.run(context);
            } catch(CommandSyntaxException e) {
                throw e;
            } catch (BadCommandUsage e) {
                getCommandSender(context).sendMessage(Chat.failureText("Error while using the command: " + e.getMessage()));
                return 0;
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
    public Vector tryGetMinecraftVec3Argument(CommandContext<CommandSourceStack> context, String argument,
                                              Vector deflt) {
        return tryGetArgument(context, argument, Coordinates.REFLECT.get(),
                nmsCoordinate -> CraftVector.toBukkit(
                        wrap(nmsCoordinate, Coordinates.class).getPosition(context.getSource())
                ),
                deflt);
    }





    /**
     * All possible choices on how to force the registration of a command, based on certain conditions.
     */
    public enum RegistrationPolicy {
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
