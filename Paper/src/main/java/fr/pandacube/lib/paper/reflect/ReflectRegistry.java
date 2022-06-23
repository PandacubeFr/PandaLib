package fr.pandacube.lib.paper.reflect;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.UUID;

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
		
		public static final class ProgressListener {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.util.ProgressListener"));
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
			public static final ReflectMethod<?> getLevelIdName = wrapEx(() -> MAPPING.mojMethod("getLevelIdName"));
			public static final ReflectMethod<?> getProperties = wrapEx(() -> MAPPING.mojMethod("getProperties"));
		}
		
		public static final class Settings {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.server.dedicated.Settings"));
			public static final ReflectField<?> properties = wrapEx(() -> MAPPING.mojField("properties"));
			
		}
		
		public static final class DedicatedServerProperties {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.server.dedicated.DedicatedServerProperties"));
		}
		
		public static final class DedicatedPlayerList {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.server.dedicated.DedicatedPlayerList"));
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
			public static final ReflectMethod<?> getBukkitEntity = wrapEx(() -> MAPPING.runtimeReflect().method("getBukkitEntity")); // spigot field
		}

		public static final class ComponentArgument {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.ComponentArgument"));
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
		
		public static final class ChunkPos {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.world.level.ChunkPos"));
			public static final ReflectConstructor<?> CONSTRUCTOR = wrapEx(() -> MAPPING.runtimeReflect().constructor(int.class, int.class));
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
		
		public static final class Level {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.world.level.Level"));
			public static final ReflectMethod<?> getGameTime = wrapEx(() -> MAPPING.mojMethod("getGameTime"));
			public static final ReflectField<?> paperConfig = wrapEx(() -> MAPPING.runtimeReflect().field("paperConfig")); // paper field
		}
		
		public static final class ServerLevel {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.server.level.ServerLevel"));
			public static final ReflectMethod<?> getFreeMapId = wrapEx(() -> MAPPING.mojMethod("getFreeMapId"));
			public static final ReflectMethod<?> save = wrapEx(() -> MAPPING.mojMethod("save", ProgressListener.MAPPING, boolean.class, boolean.class));
			public static final ReflectMethod<?> getChunkSource = wrapEx(() -> MAPPING.mojMethod("getChunkSource"));
		}
		
		public static final class ServerChunkCache {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.server.level.ServerChunkCache"));
			public static final ReflectField<?> chunkMap = wrapEx(() -> MAPPING.mojField("chunkMap"));
		}
		
		public static final class ChunkMap {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.server.level.ChunkMap"));
			public static final ReflectField<?> updatingChunks = wrapEx(() -> MAPPING.runtimeReflect().field("updatingChunks")); // spigot/paper field
			public static final ReflectField<?> autoSaveQueue = wrapEx(() -> MAPPING.runtimeReflect().field("autoSaveQueue")); // spigot/paper field
			public static final ReflectField<?> level = wrapEx(() -> MAPPING.mojField("level"));
			public static final ReflectField<?> pendingUnloads = wrapEx(() -> MAPPING.mojField("pendingUnloads"));
			public static final ReflectField<?> toDrop = wrapEx(() -> MAPPING.mojField("toDrop"));
		}
		
		public static final class ChunkStorage {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.world.level.chunk.storage.ChunkStorage"));
			public static final ReflectMethod<?> read = wrapEx(() -> MAPPING.mojMethod("read", ChunkPos.MAPPING));
		}
		
		
		
		
		
		public static final class ServerPlayer {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.server.level.ServerPlayer"));
			public static final ReflectField<?> connection = wrapEx(() -> MAPPING.mojField("connection"));
			public static final ReflectMethod<?> hurt = wrapEx(() -> MAPPING.mojMethod("hurt", DamageSource.MAPPING, float.class));
			public static final ReflectMethod<?> isTextFilteringEnabled = wrapEx(() -> MAPPING.mojMethod("isTextFilteringEnabled"));
			public static final ReflectMethod<?> allowsListing = wrapEx(() -> MAPPING.mojMethod("allowsListing"));
		}
		
		public static final class DamageSource {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.world.damagesource.DamageSource"));
			public static final ReflectField<?> OUT_OF_WORLD = wrapEx(() -> MAPPING.mojField("OUT_OF_WORLD"));
		}
		
		
		
		public static final class ServerGamePacketListenerImpl {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.server.network.ServerGamePacketListenerImpl"));
			public static final ReflectMethod<?> sendPacket = wrapEx(() -> MAPPING.mojMethod("send", NMS.Protocol.Packet.MAPPING));
		}
		
		public static final class Protocol {
			public static final class Packet {
				public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.network.protocol.Packet"));
			}
			
			public static final class ClientboundGameEventPacket {
				public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.network.protocol.game.ClientboundGameEventPacket"));
				public static final ReflectConstructor<?> CONSTRUCTOR = wrapEx(() -> MAPPING.runtimeReflect().constructor(Type.MAPPING.runtimeClass(), float.class));
				public static final ReflectField<?> RAIN_LEVEL_CHANGE = wrapEx(() -> MAPPING.mojField("RAIN_LEVEL_CHANGE"));
				public static final ReflectField<?> THUNDER_LEVEL_CHANGE = wrapEx(() -> MAPPING.mojField("THUNDER_LEVEL_CHANGE"));
				
				
				public static final class Type {
					public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.network.protocol.game.ClientboundGameEventPacket$Type"));
					public static final ReflectField<?> TYPES = wrapEx(() -> MAPPING.mojField("TYPES"));
				}
			}
			
			public static final class ClientboundCustomPayloadPacket {
				public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket"));
				public static final ReflectConstructor<?> CONSTRUCTOR = wrapEx(() -> MAPPING.runtimeReflect().constructor(ResourceLocation.MAPPING.runtimeClass(), FriendlyByteBuf.MAPPING.runtimeClass()));
				public static final ReflectField<?> BRAND = wrapEx(() -> MAPPING.mojField("BRAND"));
			}
		}
		
		public static final class FriendlyByteBuf {
			public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.network.FriendlyByteBuf"));
			public static final ReflectConstructor<?> CONSTRUCTOR = wrapEx(() -> MAPPING.runtimeReflect().constructor(Netty.ByteBuf.REFLECT.get()));
			public static final ReflectMethod<?> writeUtf = wrapEx(() -> MAPPING.mojMethod("writeUtf", String.class));
		}
		
		public static final class Block {
			public static final class BambooBlock {
				public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.world.level.block.BambooBlock"));
				public static final ReflectField<?> COLLISION_SHAPE = wrapEx(() -> MAPPING.mojField("COLLISION_SHAPE"));
			}
		}
		
		public static final class AABB {
				public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.world.phys.AABB"));
				public static final ReflectConstructor<?> CONSTRUCTOR = wrapEx(() -> MAPPING.runtimeReflect().constructor(double.class, double.class, double.class, double.class, double.class, double.class));
		}
		
		public static final class SavedData {
				public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.world.level.saveddata.SavedData"));
				public static final ReflectMethod<?> setDirty = wrapEx(() -> MAPPING.mojMethod("setDirty"));
		}
		
		public static final class MapItemSavedData {
				public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.world.level.saveddata.maps.MapItemSavedData"));
				public static final ReflectField<?> colors = wrapEx(() -> MAPPING.mojField("colors"));
				public static final ReflectField<?> locked = wrapEx(() -> MAPPING.mojField("locked"));
		}
		
		
		
		public static final class Nbt {
			
			public static final class NbtIo {
				public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.nbt.NbtIo"));
				public static final ReflectMethod<?> readCompressed = wrapEx(() -> MAPPING.mojMethod("readCompressed", File.class));
				public static final ReflectMethod<?> writeCompressed = wrapEx(() -> MAPPING.mojMethod("writeCompressed", CompoundTag.MAPPING, File.class));
			}
			
			public static final class Tag {
				public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.nbt.Tag"));
				public static final ReflectMethod<?> getAsString = wrapEx(() -> MAPPING.mojMethod("getAsString"));
			}

			public static final class StringTag {
				public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.nbt.StringTag"));
			}
			
			public static final class CompoundTag {
				public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.nbt.CompoundTag"));
				public static final ReflectMethod<?> putBoolean = wrapEx(() -> MAPPING.mojMethod("putBoolean", String.class, boolean.class));
				public static final ReflectMethod<?> putByte = wrapEx(() -> MAPPING.mojMethod("putByte", String.class, byte.class));
				public static final ReflectMethod<?> putByteArray = wrapEx(() -> MAPPING.mojMethod("putByteArray", String.class, byte[].class));
				public static final ReflectMethod<?> putByteArray_List = wrapEx(() -> MAPPING.mojMethod("putByteArray", String.class, List.class));
				public static final ReflectMethod<?> putDouble = wrapEx(() -> MAPPING.mojMethod("putDouble", String.class, double.class));
				public static final ReflectMethod<?> putFloat = wrapEx(() -> MAPPING.mojMethod("putFloat", String.class, float.class));
				public static final ReflectMethod<?> putInt = wrapEx(() -> MAPPING.mojMethod("putInt", String.class, int.class));
				public static final ReflectMethod<?> putIntArray = wrapEx(() -> MAPPING.mojMethod("putIntArray", String.class, int[].class));
				public static final ReflectMethod<?> putIntArray_List = wrapEx(() -> MAPPING.mojMethod("putIntArray", String.class, List.class));
				public static final ReflectMethod<?> putString = wrapEx(() -> MAPPING.mojMethod("putString", String.class, String.class));
				public static final ReflectMethod<?> putUUID = wrapEx(() -> MAPPING.mojMethod("putUUID", String.class, UUID.class));
				public static final ReflectMethod<?> putLong = wrapEx(() -> MAPPING.mojMethod("putLong", String.class, long.class));
				public static final ReflectMethod<?> putLongArray = wrapEx(() -> MAPPING.mojMethod("putLongArray", String.class, long[].class));
				public static final ReflectMethod<?> putLongArray_List = wrapEx(() -> MAPPING.mojMethod("putLongArray", String.class, List.class));
				public static final ReflectMethod<?> putShort = wrapEx(() -> MAPPING.mojMethod("putShort", String.class, short.class));
				public static final ReflectMethod<?> put = wrapEx(() -> MAPPING.mojMethod("put", String.class, Tag.MAPPING));

				public static final ReflectMethod<?> getTagType = wrapEx(() -> MAPPING.mojMethod("getTagType", String.class));
				public static final ReflectMethod<?> getByte = wrapEx(() -> MAPPING.mojMethod("getByte", String.class));
				public static final ReflectMethod<?> getShort = wrapEx(() -> MAPPING.mojMethod("getShort", String.class));
				public static final ReflectMethod<?> getInt = wrapEx(() -> MAPPING.mojMethod("getInt", String.class));
				public static final ReflectMethod<?> getLong = wrapEx(() -> MAPPING.mojMethod("getLong", String.class));
				public static final ReflectMethod<?> getFloat = wrapEx(() -> MAPPING.mojMethod("getFloat", String.class));
				public static final ReflectMethod<?> getDouble = wrapEx(() -> MAPPING.mojMethod("getDouble", String.class));
				public static final ReflectMethod<?> getString = wrapEx(() -> MAPPING.mojMethod("getString", String.class));
				public static final ReflectMethod<?> getByteArray = wrapEx(() -> MAPPING.mojMethod("getByteArray", String.class));
				public static final ReflectMethod<?> getIntArray = wrapEx(() -> MAPPING.mojMethod("getIntArray", String.class));
				public static final ReflectMethod<?> getLongArray = wrapEx(() -> MAPPING.mojMethod("getLongArray", String.class));
				public static final ReflectMethod<?> getCompound = wrapEx(() -> MAPPING.mojMethod("getCompound", String.class));
				public static final ReflectMethod<?> getBoolean = wrapEx(() -> MAPPING.mojMethod("getBoolean", String.class));
				
				public static final ReflectMethod<?> get = wrapEx(() -> MAPPING.mojMethod("get", String.class));
				public static final ReflectMethod<?> getAllKeys = wrapEx(() -> MAPPING.mojMethod("getAllKeys"));
				public static final ReflectMethod<?> entries = wrapEx(() -> MAPPING.mojMethod("entries"));
				public static final ReflectMethod<?> size = wrapEx(() -> MAPPING.mojMethod("size"));
				public static final ReflectMethod<?> contains = wrapEx(() -> MAPPING.mojMethod("contains", String.class));
			}
		}
		
		
		
	}
	
	
	
	
	
	
	
	
	
	public static final class OBC {
		
		public static final class CraftServer {
			public static final ReflectClass<?> REFLECT = wrapEx(() -> OBCReflect.ofClass("CraftServer"));
			public static final ReflectMethod<?> getServer = wrapEx(() -> REFLECT.method("getServer"));
			public static final ReflectMethod<?> getHandle = wrapEx(() -> REFLECT.method("getHandle"));
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
		
		public static final class CraftWorld {
			public static final ReflectClass<?> REFLECT = wrapEx(() -> OBCReflect.ofClass("CraftWorld"));
			public static final ReflectMethod<?> getHandle = wrapEx(() -> REFLECT.method("getHandle"));
		}
		
		public static final class CraftPlayer {
			public static final ReflectClass<?> REFLECT = wrapEx(() -> OBCReflect.ofClass("entity.CraftPlayer"));
			public static final ReflectMethod<?> getHandle = wrapEx(() -> REFLECT.method("getHandle"));
		}
		
		public static final class CraftMapView {
			public static final ReflectClass<?> REFLECT = wrapEx(() -> OBCReflect.ofClass("map.CraftMapView"));
			public static final ReflectField<?> worldMap = wrapEx(() -> REFLECT.field("worldMap"));
			public static final ReflectMethod<?> render = wrapEx(() -> REFLECT.method("render", CraftPlayer.REFLECT.get()));
		}
		
		public static final class RenderData {
			public static final ReflectClass<?> REFLECT = wrapEx(() -> OBCReflect.ofClass("map.RenderData"));
			public static final ReflectField<?> buffer = wrapEx(() -> REFLECT.field("buffer"));
		}
		
		
		
	}
	
	
	
	
	
	
	
	
	public static final class Paper {
		
		public static final class PaperAdventure {
			public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("io.papermc.paper.adventure.PaperAdventure"));
			public static final ReflectMethod<?> asAdventure = wrapEx(() -> REFLECT.method("asAdventure", NMS.Component.MAPPING.runtimeClass()));
		}
		
		public static final class AABBVoxelShape {
			public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("io.papermc.paper.voxel.AABBVoxelShape"));
			public static final ReflectConstructor<?> CONSTRUCTOR = wrapEx(() -> REFLECT.constructor(NMS.AABB.MAPPING.runtimeClass()));
		}
		
		public static final class QueuedChangesMapLong2Object {
			public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("com.destroystokyo.paper.util.map.QueuedChangesMapLong2Object"));
			public static final ReflectMethod<?> getVisibleMap = wrapEx(() -> REFLECT.method("getVisibleMap"));
		}
		
		public static final class WorldConfiguration {
			public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("io.papermc.paper.configuration.WorldConfiguration"));
			public static final ReflectField<?> chunks = wrapEx(() -> REFLECT.field("chunks"));
			
			public static final class Chunks {
				public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("io.papermc.paper.configuration.WorldConfiguration$Chunks"));
				public static final ReflectField<?> autoSavePeriod = wrapEx(() -> REFLECT.field("autoSaveInterval"));
			}
		}
		
		public static final class FallbackValue_Int {
			public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("io.papermc.paper.configuration.type.fallback.FallbackValue$Int"));
			public static final ReflectMethod<?> value = wrapEx(() -> REFLECT.method("value"));
		}
		
	}
	
	
	
	
	
	
	
	
	public static final class Brigadier {
		
		public static final class CommandNode {
			public static final ReflectClass<?> REFLECT = Reflect.ofClass(com.mojang.brigadier.tree.CommandNode.class);
			public static final ReflectMethod<?> removeCommand = wrapEx(() -> REFLECT.method("removeCommand", String.class));
		}
		
	}
	
	
	
	
	
	public static final class Netty {
		
		public static final class ByteBuf {
			public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("io.netty.buffer.ByteBuf"));
		}
		
		public static final class Unpooled {
			public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("io.netty.buffer.Unpooled"));
			public static final ReflectMethod<?> buffer = wrapEx(() -> REFLECT.method("buffer"));
		}
		
	}
	
	
	
	
	
	
	
	
	/*
	 * Initialization stuff
	 */
	
	public static void init() {
		NMSReflect.init();
		initRecursively(ReflectRegistry.class);
	}
	
	private static void initRecursively(Class<?> cl) {
		try {
			for (Field f : Reflect.ofClass(cl).get().getDeclaredFields()) {
				if (Modifier.isStatic(f.getModifiers()))
					f.get(null);
			}
		} catch (Throwable t) {
			if (t instanceof ExceptionInInitializerError eiie && eiie.getCause() != null) {
				t = eiie.getCause();
			}
			Log.severe("Error while initilizing a ReflectRegistry entry at " + cl.getName(), t);
		}
		for (Class<?> declaredClass : cl.getDeclaredClasses()) {
			initRecursively(declaredClass);
		}
	}

}
