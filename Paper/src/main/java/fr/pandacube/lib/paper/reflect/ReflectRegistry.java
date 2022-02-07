package fr.pandacube.lib.paper.reflect;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.mojang.brigadier.tree.CommandNode;

import fr.pandacube.lib.core.util.Reflect;
import fr.pandacube.lib.core.util.Reflect.ReflectClass;
import fr.pandacube.lib.core.util.Reflect.ReflectConstructor;
import fr.pandacube.lib.core.util.Reflect.ReflectField;
import fr.pandacube.lib.core.util.Reflect.ReflectMethod;
import fr.pandacube.lib.paper.reflect.NMSReflect.ClassMapping;

public class ReflectRegistry {

	public static final ClassMapping NMS_SHAREDCONSTANTS = wrapEx(() -> NMSReflect.mojClass("net.minecraft.SharedConstants"));
	public static final ReflectMethod<?> NMS_SHAREDCONSTANTS_GETCURRENTVERSION = wrapEx(() -> NMS_SHAREDCONSTANTS.mojMethod("getCurrentVersion"));
	
	
	public static final ClassMapping NMS_WORLDVERSION = wrapEx(() -> NMSReflect.mojClass("net.minecraft.WorldVersion"));
	public static final ReflectMethod<?> NMS_WORLDVERSION_GETPROTOCOLVERSION = wrapEx(() -> NMS_WORLDVERSION.runtimeReflect().method("getProtocolVersion"));
	
	
	public static final ClassMapping NMS_DEDICATEDSERVER = wrapEx(() -> NMSReflect.mojClass("net.minecraft.server.dedicated.DedicatedServer"));
	public static final ReflectField<?> NMS_DEDICATEDSERVER_VANILLACOMMANDDISPATCHER = wrapEx(() -> NMS_DEDICATEDSERVER.runtimeReflect().field("vanillaCommandDispatcher"));
	
	
	public static final ClassMapping NMS_COMMANDS = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.Commands"));
	public static final ReflectField<?> NMS_COMMANDS_DISPATCHER = wrapEx(() -> NMS_DEDICATEDSERVER.mojField("dispatcher"));
	

	public static final ClassMapping NMS_VEC3 = wrapEx(() -> NMSReflect.mojClass("net.minecraft.world.phys.Vec3"));
	
	
	public static final ClassMapping NMS_COMPONENT = wrapEx(() -> NMSReflect.mojClass("net.minecraft.network.chat.Component"));
	

	public static final ClassMapping NMS_COMMANDSOURCESTACK = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.CommandSourceStack"));
	
	
	public static final ClassMapping NMS_ENTITYARGUMENT = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.EntityArgument"));
	public static final ReflectConstructor<?> NMS_ENTITYARGUMENT_CONSTRUCTOR = wrapEx(() -> NMS_ENTITYARGUMENT.runtimeReflect().constructor(boolean.class, boolean.class));
	
	
	public static final ClassMapping NMS_ENTITYSELECTOR = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.selector.EntitySelector"));
	public static final ReflectMethod<?> NMS_ENTITYSELECTOR_FINDENTITIES = wrapEx(() -> NMS_ENTITYSELECTOR.mojMethod("findEntities", NMS_COMMANDSOURCESTACK));
	public static final ReflectMethod<?> NMS_ENTITYSELECTOR_FINDPLAYERS = wrapEx(() -> NMS_ENTITYSELECTOR.mojMethod("findPlayers", NMS_COMMANDSOURCESTACK));
	public static final ReflectMethod<?> NMS_ENTITYSELECTOR_FINDSINGLEENTITY = wrapEx(() -> NMS_ENTITYSELECTOR.mojMethod("findSingleEntity", NMS_COMMANDSOURCESTACK));
	public static final ReflectMethod<?> NMS_ENTITYSELECTOR_FINDSINGLEPLAYER = wrapEx(() -> NMS_ENTITYSELECTOR.mojMethod("findSinglePlayer", NMS_COMMANDSOURCESTACK));
	
	
	public static final ClassMapping NMS_ENTITY = wrapEx(() -> NMSReflect.mojClass("net.minecraft.world.entity.Entity"));
	public static final ReflectMethod<?> NMS_ENTITY_GETBUKKITENTITY = wrapEx(() -> NMS_ENTITY.runtimeReflect().method("getBukkitEntity"));

	

	public static final ClassMapping NMS_COMPONENTARGUMENT = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.ComponentArgument"));
	public static final ReflectConstructor<?> NMS_COMPONENTARGUMENT_CONSTRUCTOR = wrapEx(() -> NMS_COMPONENTARGUMENT.runtimeReflect().constructor());
	

	public static final ClassMapping NMS_BLOCKSTATEARGUMENT = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.blocks.BlockStateArgument"));
	public static final ReflectConstructor<?> NMS_BLOCKSTATEARGUMENT_CONSTRUCTOR = wrapEx(() -> NMS_BLOCKSTATEARGUMENT.runtimeReflect().constructor());
	

	public static final ClassMapping NMS_VEC3ARGUMENT = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.coordinates.Vec3Argument"));
	public static final ReflectMethod<?> NMS_VEC3ARGUMENT_VEC3 = wrapEx(() -> NMS_VEC3ARGUMENT.mojMethod("vec3", boolean.class));

	
	public static final ClassMapping NMS_COORDINATES = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.coordinates.Coordinates"));
	public static final ReflectMethod<?> NMS_COORDINATES_GETPOSITION = wrapEx(() -> NMS_COORDINATES.mojMethod("getPosition", NMS_COMMANDSOURCESTACK));
	public static final ReflectMethod<?> NMS_COORDINATES_GETBLOCKPOS = wrapEx(() -> NMS_COORDINATES.mojMethod("getBlockPos", NMS_COMMANDSOURCESTACK));
	
	
	public static final ClassMapping NMS_BLOCKPOSARGUMENT = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.coordinates.BlockPosArgument"));
	public static final ReflectMethod<?> NMS_BLOCKPOSARGUMENT_BLOCKPOS = wrapEx(() -> NMS_BLOCKPOSARGUMENT.mojMethod("blockPos"));

	
	public static final ClassMapping NMS_BLOCKPOS = wrapEx(() -> NMSReflect.mojClass("net.minecraft.core.BlockPos"));
	public static final ReflectMethod<?> NMS_BLOCKPOS_GETX = wrapEx(() -> NMS_BLOCKPOS.mojMethod("getX"));
	public static final ReflectMethod<?> NMS_BLOCKPOS_GETY = wrapEx(() -> NMS_BLOCKPOS.mojMethod("getY"));
	public static final ReflectMethod<?> NMS_BLOCKPOS_GETZ = wrapEx(() -> NMS_BLOCKPOS.mojMethod("getZ"));
	
	
	public static final ClassMapping NMS_RESOURCELOCATIONARGUMENT = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.ResourceLocationArgument"));
	public static final ReflectConstructor<?> NMS_RESOURCELOCATIONARGUMENT_CONSTRUCTOR = wrapEx(() -> NMS_RESOURCELOCATIONARGUMENT.runtimeReflect().constructor());
	
	
	public static final ClassMapping NMS_RESOURCELOCATION = wrapEx(() -> NMSReflect.mojClass("net.minecraft.resources.ResourceLocation"));
	
	
	public static final ClassMapping NMS_GAMEPROFILEARGUMENT = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.GameProfileArgument"));
	public static final ReflectConstructor<?> NMS_GAMEPROFILEARGUMENT_CONSTRUCTOR = wrapEx(() -> NMS_GAMEPROFILEARGUMENT.runtimeReflect().constructor());
	
	
	
	public static final ReflectClass<?> OBC_CRAFTSERVER = wrapEx(() -> OBCReflect.ofClass("CraftServer"));
	public static final ReflectMethod<?> OBC_CRAFTSERVER_GETSERVER = wrapEx(() -> OBC_CRAFTSERVER.method("getServer"));
	
	
	public static final ReflectClass<?> OBC_VANILLACOMMANDWRAPPER = wrapEx(() -> OBCReflect.ofClass("command.VanillaCommandWrapper"));
	@SuppressWarnings("unchecked")
	public static final ReflectConstructor<? extends Command> OBC_VANILLACOMMANDWRAPPER_CONSTRUCTOR =
			(ReflectConstructor<? extends Command>) wrapEx(() -> OBC_VANILLACOMMANDWRAPPER.constructor(
					NMS_COMMANDS.runtimeClass(),
					CommandNode.class
					));
	public static final ReflectField<?> OBC_VANILLACOMMANDWRAPPER_VANILLACOMMAND = wrapEx(() -> OBC_VANILLACOMMANDWRAPPER.field("vanillaCommand"));
	public static final ReflectMethod<?> OBC_VANILLACOMMANDWRAPPER_GETLISTENER = wrapEx(() -> OBC_VANILLACOMMANDWRAPPER.method("getListener", CommandSender.class));
	
	
	public static final ReflectClass<?> OBC_CRAFTNAMESPACEDKEY = wrapEx(() -> OBCReflect.ofClass("util.CraftNamespacedKey"));
	public static final ReflectMethod<?> OBC_CRAFTNAMESPACEDKEY_TOMINECRAFT = wrapEx(() -> OBC_CRAFTNAMESPACEDKEY.method("toMinecraft", NamespacedKey.class));
	public static final ReflectMethod<?> OBC_CRAFTNAMESPACEDKEY_FROMMINECRAFT = wrapEx(() -> OBC_CRAFTNAMESPACEDKEY.method("fromMinecraft", NMS_RESOURCELOCATION.runtimeClass()));
	
	
	public static final ReflectClass<?> OBC_CRAFTVECTOR = wrapEx(() -> OBCReflect.ofClass("util.CraftVector"));
	public static final ReflectMethod<?> OBC_CRAFTVECTOR_TOBUKKIT_VEC3 = wrapEx(() -> OBC_VANILLACOMMANDWRAPPER.method("toBukkit", NMS_VEC3.runtimeClass()));
	

	public static final ReflectClass<?> PAPER_PAPERADVENTURE = wrapEx(() -> Reflect.ofClass("io.papermc.paper.adventure.PaperAdventure"));
	public static final ReflectMethod<?> PAPER_PAPERADVENTURE_ASADVENTURE = wrapEx(() -> PAPER_PAPERADVENTURE.method("asAdventure", NMS_COMPONENT.runtimeClass()));


	public static final ReflectClass<?> BRIGADIER_COMMANDNODE = Reflect.ofClass(CommandNode.class);
	public static final ReflectMethod<?> BRIGADIER_COMMANDNODE_REMOVECOMMAND = wrapEx(() -> BRIGADIER_COMMANDNODE.method("removeCommand", String.class));
	
	
	
	
	private interface SupplierException<T> {
		public T get() throws Exception;
	}
	
	private static <T> T wrapEx(SupplierException<T> prv) {
		try {
			return prv.get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
