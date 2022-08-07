package fr.pandacube.lib.paper.commands;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import fr.pandacube.lib.chat.Chat;
import fr.pandacube.lib.commands.BrigadierCommand;
import fr.pandacube.lib.commands.SuggestionsSupplier;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftNamespacedKey;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftServer;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftVector;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.VanillaCommandWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.BlockPosArgument;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.Commands;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.ComponentArgument;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.Coordinates;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.EntityArgument;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.EntitySelector;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.GameProfileArgument;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.ResourceLocationArgument;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.Vec3Argument;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.core.BlockPos;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.resources.ResourceLocation;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.ServerPlayer;
import fr.pandacube.lib.paper.reflect.wrapper.paper.PaperAdventure;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.util.Log;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class PaperBrigadierCommand extends BrigadierCommand<BukkitBrigadierCommandSource> implements Listener {

	protected static final Commands vanillaCommandDispatcher;
	private static final CommandDispatcher<BukkitBrigadierCommandSource> nmsDispatcher;
	
	static {
		vanillaCommandDispatcher = ReflectWrapper.wrapTyped(Bukkit.getServer(), CraftServer.class)
				.getServer()
				.vanillaCommandDispatcher();
		nmsDispatcher = vanillaCommandDispatcher.dispatcher();
	}


	public static CommandDispatcher<BukkitBrigadierCommandSource> getNMSDispatcher() {
		return nmsDispatcher;
	}

	protected static RootCommandNode<BukkitBrigadierCommandSource> getRootNode() {
		return nmsDispatcher.getRoot();
	}
	
	protected Plugin plugin;
	
	protected LiteralCommandNode<BukkitBrigadierCommandSource> commandNode;
	
	private Set<String> allAliases;
	
	public PaperBrigadierCommand(Plugin pl) {
		plugin = pl;
		commandNode = buildCommand().build();
		postBuildCommand(commandNode);
		register();
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	

	
	
	private void register() {
		
		String[] aliases = getAliases();
		if (aliases == null)
			aliases = new String[0];
		
		String pluginName = plugin.getName().toLowerCase();
		
		allAliases = new HashSet<>();
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
		//boolean isAlias = node.getRedirect() == commandNode;
		boolean forceRegistration = true;//prefixed || !isAlias;
		
		// nmsDispatcher integration and conflit resolution
		boolean nmsRegister = false, nmsRegistered = false;
		CommandNode<BukkitBrigadierCommandSource> nmsConflited = root.getChild(name);
		if (nmsConflited != null) {
			
			if (isFromThisCommand(nmsConflited)) {
				// this command is already registered in NMS
				// don’t need to register again
				nmsRegistered = true;
			}
			else if (forceRegistration) {
				nmsRegister = true;
				Log.info("Overwriting Brigadier command /" + name);
			}
			else {
				Log.severe("/" + name + " already in NMS Brigadier instance."
						+ " Wont replace it because registration is not forced.");
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
		
		allAliases.add(name);
		
		// bukkit dispatcher conflict resolution
		boolean bukkitRegister = false;
		CommandMap bukkitCmdMap = Bukkit.getCommandMap();
		Command bukkitConflicted = bukkitCmdMap.getCommand(name);
		if (bukkitConflicted != null) {
			if (isFromThisCommand(bukkitConflicted)) {
				// nothing to do, already good
			}
			else if (forceRegistration) {
				bukkitRegister = true;
				Log.info("Overwriting Bukkit command /" + name
						+ " (" + getCommandIdentity(bukkitConflicted) + ")");
			}
			else {
				Log.severe("/" + name + " already in Bukkit"
						+ " dispatcher (" + getCommandIdentity(bukkitConflicted)
						+ "). Wont replace it because registration is not forced.");
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
	
	protected static String getCommandIdentity(Command bukkitCmd) {
		if (bukkitCmd instanceof PluginCommand cmd) {
			return "Bukkit command: /" + cmd.getName() + " from plugin " + cmd.getPlugin().getName();
		}
		else if (VanillaCommandWrapper.REFLECT.get().isInstance(bukkitCmd)) {
			return "Vanilla command: /" + bukkitCmd.getName();
		}
		else
			return bukkitCmd.getClass().getName() + ": /" + bukkitCmd.getName();
	}
	
	
	
	
	@EventHandler
	public void onPlayerCommandSend(PlayerCommandSendEvent event) {
		event.getCommands().removeAll(allAliases.stream().map(s -> "minecraft:" + s).toList());
	}
	
	
	@EventHandler
	public void onServerLoad(ServerLoadEvent event) {
		register();
	}
	

	
	/**
	 * The permission that should be tested instead of "minecraft.command.cmdName".
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


	public boolean isConsole(CommandSender sender) {
		return sender instanceof ConsoleCommandSender;
	}
	public boolean isPlayer(CommandSender sender) {
		return sender instanceof Player;
	}


	

	public static CommandSender getCommandSender(CommandContext<BukkitBrigadierCommandSource> context) {
		return getCommandSender(context.getSource());
	}
	public static CommandSender getCommandSender(BukkitBrigadierCommandSource wrapper) {
		return wrapper.getBukkitSender();
	}
	public static BukkitBrigadierCommandSource getBrigadierCommandSource(CommandSender sender) {
		return VanillaCommandWrapper.getListener(sender);
	}



	
	
	
	
	
	

	




	public static final SuggestionsSupplier<CommandSender> TAB_WORLDS = (s, ti, token, a) -> SuggestionsSupplier.collectFilteredStream(Bukkit.getWorlds().stream().map(World::getName), token);

	
	
	protected SuggestionProvider<BukkitBrigadierCommandSource> wrapSuggestions(SuggestionsSupplier<CommandSender> suggestions) {
		return wrapSuggestions(suggestions, PaperBrigadierCommand::getCommandSender);
	}
	
	
	
	
	protected static com.mojang.brigadier.Command<BukkitBrigadierCommandSource> wrapCommand(com.mojang.brigadier.Command<BukkitBrigadierCommandSource> cmd) {
		return context -> {
			try {
				return cmd.run(context);
			} catch(CommandSyntaxException e) {
				throw e;
			} catch (Throwable t) {
				Log.severe(t);
				getCommandSender(context).sendMessage(Chat.failureText("Error while using the command: " + t));
				return 0;
			}
		};
	}
	
	
	
	
	
	
	

	/*
	 * Minecraft argument type
	 */


	public static ArgumentType<Object> argumentMinecraftEntity(boolean singleTarget, boolean playersOnly) {
		if (playersOnly) {
			return singleTarget ? EntityArgument.player() : EntityArgument.players();
		}
		else {
			return singleTarget ? EntityArgument.entity() : EntityArgument.entities();
		}
	}
	
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
	
	public Entity tryGetMinecraftEntityArgumentOneEntity(CommandContext<BukkitBrigadierCommandSource> context, String argument) {
		EntitySelector es = ReflectWrapper.wrap(tryGetArgument(context, argument, EntitySelector.MAPPING.runtimeClass()), EntitySelector.class);
		if (es == null)
			return null;
		fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.Entity nmsEntity = es.findSingleEntity(context.getSource());
		return nmsEntity == null ? null : nmsEntity.getBukkitEntity();
	}
	
	public Player tryGetMinecraftEntityArgumentOnePlayer(CommandContext<BukkitBrigadierCommandSource> context, String argument) {
		EntitySelector es = ReflectWrapper.wrap(tryGetArgument(context, argument, EntitySelector.MAPPING.runtimeClass()), EntitySelector.class);
		if (es == null)
			return null;
		ServerPlayer nmsPlayer = es.findSinglePlayer(context.getSource());
		return nmsPlayer == null ? null : nmsPlayer.getBukkitEntity();
	}
	
	
	

	public static ArgumentType<Object> argumentMinecraftGameProfile() {
		return GameProfileArgument.gameProfile();
	}
	
	

	public static ArgumentType<Object> argumentMinecraftResourceLocation() {
		return ResourceLocationArgument.id();
	}
	public NamespacedKey tryGetMinecraftResourceLocationArgument(CommandContext<BukkitBrigadierCommandSource> context, String argument, NamespacedKey deflt) {
		return tryGetArgument(context, argument, ResourceLocation.MAPPING.runtimeClass(),
				nmsKey -> CraftNamespacedKey.fromMinecraft(ReflectWrapper.wrap(nmsKey, ResourceLocation.class)),
				deflt);
	}
	



	public static ArgumentType<Object> argumentMinecraftBlockPosition() {
		return BlockPosArgument.blockPos();
	}
	public BlockVector tryGetMinecraftBlockPositionArgument(CommandContext<BukkitBrigadierCommandSource> context, String argument) {
		Coordinates coord = ReflectWrapper.wrap(tryGetArgument(context, argument, Coordinates.MAPPING.runtimeClass()), Coordinates.class);
		if (coord == null)
			return null;
		BlockPos bp = coord.getBlockPos(context.getSource());
		return new BlockVector(bp.getX(), bp.getY(), bp.getZ());

	}
	
	
	

	public static ArgumentType<Object> argumentMinecraftVec3() {
		return Vec3Argument.vec3(true);
	}
	public Vector tryGetMinecraftVec3Argument(CommandContext<BukkitBrigadierCommandSource> context, String argument) {
		Coordinates coord = ReflectWrapper.wrap(tryGetArgument(context, argument, Coordinates.MAPPING.runtimeClass()), Coordinates.class);
		return coord == null ? null : CraftVector.toBukkit(coord.getPosition(context.getSource()));

	}
	



	public static ArgumentType<Object> argumentMinecraftChatComponent() {
		return ComponentArgument.textComponent();
	}
	public Component tryGetMinecraftChatComponentArgument(CommandContext<BukkitBrigadierCommandSource> context, String argument) {
		var nmsComponent = ReflectWrapper.wrap(
				tryGetArgument(context, argument, fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.chat.Component.MAPPING.runtimeClass()),
				fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.chat.Component.class
		);
		return nmsComponent == null ? null : PaperAdventure.asAdventure(nmsComponent);
	}





}
