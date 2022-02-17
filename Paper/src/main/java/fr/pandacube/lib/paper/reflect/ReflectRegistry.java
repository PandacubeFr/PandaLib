package fr.pandacube.lib.paper.reflect;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.mojang.brigadier.tree.CommandNode;

import fr.pandacube.lib.core.util.Log;
import fr.pandacube.lib.core.util.Reflect;
import fr.pandacube.lib.core.util.Reflect.ReflectClass;
import fr.pandacube.lib.core.util.Reflect.ReflectConstructor;
import fr.pandacube.lib.core.util.Reflect.ReflectField;
import fr.pandacube.lib.core.util.Reflect.ReflectMethod;
import fr.pandacube.lib.paper.reflect.NMSReflect.ClassMapping;

/**
 * Collection of static properties to ease access to commonly used server internals and private class members.
 */
public class ReflectRegistry {
	
	
	public static final class NMS {
		
		public static final class SharedConstants {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.SharedConstants"));
			public static final ReflectMethod<?> getCurrentVersion = wrapEx(() -> MAPPING.mojMethod("getCurrentVersion"));
			public static final ReflectMethod<?> getProtocolVersion = wrapEx(() -> MAPPING.mojMethod("getProtocolVersion"));
		}
		
		public static final class WorldVersion {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.WorldVersion"));
		}
		
		public static final class DetectedVersion {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.DetectedVersion"));
		}
		
		public static final class DedicatedServer {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.server.dedicated.DedicatedServer"));
			public static final ReflectField<?> vanillaCommandDispatcher = wrapEx(() -> MAPPING.runtimeReflect().field("vanillaCommandDispatcher"));
		}
		
		public static final class Commands {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.Commands"));
			public static final ReflectField<?> dispatcher = wrapEx(() -> MAPPING.mojField("dispatcher"));
		}
		
		public static final class Vec3 {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.world.phys.Vec3"));
		}
		
		public static final class Component {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.network.chat.Component"));
		}
		
		public static final class CommandSourceStack {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.CommandSourceStack"));
		}
		
		public static final class EntityArgument {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.EntityArgument"));
			public static final ReflectConstructor<?> CONSTRUCTOR = wrapEx(() -> MAPPING.runtimeReflect().constructor(boolean.class, boolean.class));
		}

		public static final class EntitySelector {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.selector.EntitySelector"));
			public static final ReflectMethod<?> findEntities = wrapEx(() -> MAPPING.mojMethod("findEntities", CommandSourceStack.MAPPING));
			public static final ReflectMethod<?> findPlayers = wrapEx(() -> MAPPING.mojMethod("findPlayers", CommandSourceStack.MAPPING));
			public static final ReflectMethod<?> findSingleEntity = wrapEx(() -> MAPPING.mojMethod("findSingleEntity", CommandSourceStack.MAPPING));
			public static final ReflectMethod<?> findSinglePlayer = wrapEx(() -> MAPPING.mojMethod("findSinglePlayer", CommandSourceStack.MAPPING));
		}
		
		public static final class Entity {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.world.entity.Entity"));
			public static final ReflectMethod<?> getBukkitEntity = wrapEx(() -> MAPPING.runtimeReflect().method("getBukkitEntity"));
		}

		public static final class ComponentArgument {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.ComponentArgument"));
			public static final ReflectConstructor<?> CONSTRUCTOR = wrapEx(() -> MAPPING.runtimeReflect().constructor());
		}
		
		public static final class BlockStateArgument {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.blocks.BlockStateArgument"));
			public static final ReflectConstructor<?> CONSTRUCTOR = wrapEx(() -> MAPPING.runtimeReflect().constructor());
		}
		
		public static final class Vec3Argument {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.coordinates.Vec3Argument"));
			public static final ReflectMethod<?> vec3 = wrapEx(() -> MAPPING.mojMethod("vec3", boolean.class));
		}

		public static final class Coordinates {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.coordinates.Coordinates"));
			public static final ReflectMethod<?> getPosition = wrapEx(() -> MAPPING.mojMethod("getPosition", CommandSourceStack.MAPPING));
			public static final ReflectMethod<?> getBlockPos = wrapEx(() -> MAPPING.mojMethod("getBlockPos", CommandSourceStack.MAPPING));
		}
		
		public static final class BlockPosArgument {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.coordinates.BlockPosArgument"));
			public static final ReflectMethod<?> blockPos = wrapEx(() -> MAPPING.mojMethod("blockPos"));
		}
		
		public static final class BlockPos {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.core.BlockPos"));
			public static final ClassMapping MAPPING_SUPERCLASS = wrapEx(() -> NMSReflect.mojClass("net.minecraft.core.Vec3i"));
			public static final ReflectMethod<?> getX = wrapEx(() -> MAPPING_SUPERCLASS.mojMethod("getX")); // these 3 methods are declared in the superclass
			public static final ReflectMethod<?> getY = wrapEx(() -> MAPPING_SUPERCLASS.mojMethod("getY")); // so the mapping is only referenced in the superclass
			public static final ReflectMethod<?> getZ = wrapEx(() -> MAPPING_SUPERCLASS.mojMethod("getZ"));
		}
		
		public static final class ResourceLocationArgument {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.ResourceLocationArgument"));
			public static final ReflectConstructor<?> CONSTRUCTOR = wrapEx(() -> MAPPING.runtimeReflect().constructor());
		}
		
		public static final class ResourceLocation {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.resources.ResourceLocation"));
		}
		
		public static final class GameProfileArgument {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.GameProfileArgument"));
			public static final ReflectConstructor<?> CONSTRUCTOR = wrapEx(() -> MAPPING.runtimeReflect().constructor());
		}
		
	}
	
	
	public static final class OBC {
		
		public static final class CraftServer {
			public static final ReflectClass<?> REFLECT = wrapEx(() -> OBCReflect.ofClass("CraftServer"));
			public static final ReflectMethod<?> getServer = wrapEx(() -> REFLECT.method("getServer"));
		}
		
		public static final class VanillaCommandWrapper {
			public static final ReflectClass<?> REFLECT = wrapEx(() -> OBCReflect.ofClass("command.VanillaCommandWrapper"));
			@SuppressWarnings("unchecked")
			public static final ReflectConstructor<? extends Command> CONSTRUTOR =
					(ReflectConstructor<? extends Command>) wrapEx(() -> REFLECT.constructor(
							NMS.Commands.MAPPING.runtimeClass(),
							CommandNode.class
							));
			public static final ReflectField<?> vanillaCommand = wrapEx(() -> REFLECT.field("vanillaCommand"));
			public static final ReflectMethod<?> getListener = wrapEx(() -> REFLECT.method("getListener", CommandSender.class));
		}
		
		public static final class CraftNamespacedKey {
			public static final ReflectClass<?> REFLECT = wrapEx(() -> OBCReflect.ofClass("util.CraftNamespacedKey"));
			public static final ReflectMethod<?> toMinecraft = wrapEx(() -> REFLECT.method("toMinecraft", NamespacedKey.class));
			public static final ReflectMethod<?> fromMinecraft = wrapEx(() -> REFLECT.method("fromMinecraft", NMS.ResourceLocation.MAPPING.runtimeClass()));
		}
		
		public static final class CraftVector {
			public static final ReflectClass<?> REFLECT = wrapEx(() -> OBCReflect.ofClass("util.CraftVector"));
			public static final ReflectMethod<?> toBukkit_vec3 = wrapEx(() -> REFLECT.method("toBukkit", NMS.Vec3.MAPPING.runtimeClass()));
		}
		
	}
	
	
	public static final class Paper {
		
		public static final class PaperAdventure {
			public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("io.papermc.paper.adventure.PaperAdventure"));
			public static final ReflectMethod<?> asAdventure = wrapEx(() -> REFLECT.method("asAdventure", NMS.Component.MAPPING.runtimeClass()));
		}
		
	}
	
	
	public static final class Brigadier {
		
		public static final class CommandNode {
			public static final ReflectClass<?> REFLECT = Reflect.ofClass(com.mojang.brigadier.tree.CommandNode.class);
			public static final ReflectMethod<?> removeCommand = wrapEx(() -> REFLECT.method("removeCommand", String.class));
		}
		
	}
	
	
	
	/*
	 * Initialization stuff
	 */
	
	public static void init() {
		initRecursively(ReflectRegistry.class);
	}
	
	private static void initRecursively(Class<?> cl) {
		try {
			Class.forName(cl.getName()); // force initializing the member classes
		} catch (ExceptionInInitializerError e) {
			Log.severe("Error while initilizing a ReflectRegistry entry at " + cl.getName(), e.getCause());
		} catch (NoClassDefFoundError e) {
			// if a previously initialized class failed to actually initialize
			Log.severe("Error while initilizing a ReflectRegistry entry at " + cl.getName() + " due to a previously failed initialization (" + e.getMessage() + ")");
		} catch (ClassNotFoundException e) {
			Log.severe("Wut? (should not append)", e);
		}
		for (Class<?> declaredClass : cl.getDeclaredClasses()) {
			initRecursively(declaredClass);
		}
	}

}
