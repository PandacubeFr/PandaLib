package fr.pandacube.lib.paper.reflect;

import fr.pandacube.lib.paper.reflect.wrapper.brigadier.CommandNode;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftItemStack;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftMapView;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftNamespacedKey;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftPlayer;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftServer;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftVector;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftWorld;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.RenderData;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.VanillaCommandWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.dataconverter.MCDataConverter;
import fr.pandacube.lib.paper.reflect.wrapper.dataconverter.MCDataType;
import fr.pandacube.lib.paper.reflect.wrapper.dataconverter.MCTypeRegistry;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.DetectedVersion;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.GameVersion;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.SharedConstants;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.WorldVersion;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.BlockPosArgument;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.CommandSourceStack;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.Commands;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.ComponentArgument;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.Coordinates;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.EntityArgument;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.EntitySelector;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.GameProfileArgument;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.ResourceLocationArgument;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.Vec3Argument;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.core.BlockPos;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.core.Vec3i;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.CollectionTag;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.CompoundTag;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.ListTag;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.NbtIo;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.StringTag;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.Tag;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.FriendlyByteBuf;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.chat.Component;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.protocol.ClientboundCustomPayloadPacket;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.protocol.ClientboundGameEventPacket;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.protocol.Packet;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.resources.ResourceLocation;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.ChunkMap;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.DedicatedPlayerList;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.DedicatedServer;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.DedicatedServerProperties;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.MinecraftServer;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.PlayerList;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.ServerChunkCache;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.ServerGamePacketListenerImpl;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.ServerLevel;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.ServerPlayer;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.Settings;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.util.ProgressListener;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.AABB;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.ChunkPos;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.ChunkStorage;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.DamageSource;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.DamageSources;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.Entity;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.ItemStack;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.Level;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.MapItemSavedData;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.PlayerDataStorage;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.SavedData;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.Vec3;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.VoxelShape;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.block.BambooBlock;
import fr.pandacube.lib.paper.reflect.wrapper.netty.ByteBuf;
import fr.pandacube.lib.paper.reflect.wrapper.netty.Unpooled;
import fr.pandacube.lib.paper.reflect.wrapper.paper.AABBVoxelShape;
import fr.pandacube.lib.paper.reflect.wrapper.paper.PaperAdventure;
import fr.pandacube.lib.paper.reflect.wrapper.paper.QueuedChangesMapLong2Object;
import fr.pandacube.lib.paper.reflect.wrapper.paper.configuration.FallbackValue_Int;
import fr.pandacube.lib.paper.reflect.wrapper.paper.configuration.WorldConfiguration;

import static fr.pandacube.lib.reflect.wrapper.WrapperRegistry.initWrapper;

/**
 * Initializer for all the reflect tools in {@code pandalib-paper-reflect} module.
 */
public class PandalibPaperReflect {

    private static boolean isInit = false;

    /**
     * Initializes the reflect tools in {@code pandalib-paper-reflect} module.
     */
    public static void init() {
        NMSReflect.init();
        synchronized (PandalibPaperReflect.class) {
            if (isInit)
                return;
            isInit = true;
        }
        initWrapperClasses();
    }

    private static void initWrapperClasses() {
        // brigadier
        initWrapper(CommandNode.class, CommandNode.REFLECT.get());

        // craftbukkit
        initWrapper(CraftItemStack.class, CraftItemStack.REFLECT.get());
        initWrapper(CraftMapView.class, CraftMapView.REFLECT.get());
        initWrapper(CraftNamespacedKey.class, CraftNamespacedKey.REFLECT.get());
        initWrapper(CraftPlayer.class, CraftPlayer.REFLECT.get());
        initWrapper(CraftServer.class, CraftServer.REFLECT.get());
        initWrapper(CraftVector.class, CraftVector.REFLECT.get());
        initWrapper(CraftWorld.class, CraftWorld.REFLECT.get());
        initWrapper(RenderData.class, RenderData.REFLECT.get());
        initWrapper(VanillaCommandWrapper.class, VanillaCommandWrapper.REFLECT.get());

        // dataconverter
        initWrapper(MCDataConverter.class, MCDataConverter.REFLECT.get());
        initWrapper(MCDataType.class, MCDataType.REFLECT.get());
        initWrapper(MCTypeRegistry.class, MCTypeRegistry.REFLECT.get());

        // minecraft.commands
        initWrapper(BlockPosArgument.class, BlockPosArgument.MAPPING.runtimeClass());
        initWrapper(Commands.class, Commands.MAPPING.runtimeClass());
        initWrapper(CommandSourceStack.class, CommandSourceStack.MAPPING.runtimeClass());
        initWrapper(ComponentArgument.class, ComponentArgument.MAPPING.runtimeClass());
        initWrapper(Coordinates.class, Coordinates.MAPPING.runtimeClass());
        initWrapper(EntityArgument.class, EntityArgument.MAPPING.runtimeClass());
        initWrapper(EntitySelector.class, EntitySelector.MAPPING.runtimeClass());
        initWrapper(GameProfileArgument.class, GameProfileArgument.MAPPING.runtimeClass());
        initWrapper(ResourceLocationArgument.class, ResourceLocationArgument.MAPPING.runtimeClass());
        initWrapper(Vec3Argument.class, Vec3Argument.MAPPING.runtimeClass());
        // minecraft.core
        initWrapper(BlockPos.class, BlockPos.MAPPING.runtimeClass());
        initWrapper(Vec3i.class, Vec3i.MAPPING.runtimeClass());
        // minecraft.nbt
        initWrapper(CollectionTag.class, CollectionTag.MAPPING.runtimeClass());
        initWrapper(CompoundTag.class, CompoundTag.MAPPING.runtimeClass());
        initWrapper(ListTag.class, ListTag.MAPPING.runtimeClass());
        initWrapper(NbtIo.class, NbtIo.MAPPING.runtimeClass());
        initWrapper(StringTag.class, StringTag.MAPPING.runtimeClass());
        initWrapper(Tag.class, Tag.MAPPING.runtimeClass());
        // minecraft.network.chat
        initWrapper(Component.class, Component.MAPPING.runtimeClass());
        // minecraft.network.protocol
        initWrapper(ClientboundCustomPayloadPacket.class, ClientboundCustomPayloadPacket.MAPPING.runtimeClass());
        initWrapper(ClientboundGameEventPacket.class, ClientboundGameEventPacket.MAPPING.runtimeClass());
        initWrapper(ClientboundGameEventPacket.Type.class, ClientboundGameEventPacket.Type.MAPPING.runtimeClass());
        initWrapper(Packet.class, Packet.MAPPING.runtimeClass());
        // minecraft.network
        initWrapper(FriendlyByteBuf.class, FriendlyByteBuf.MAPPING.runtimeClass());
        // minecraft.resources
        initWrapper(ResourceLocation.class, ResourceLocation.MAPPING.runtimeClass());
        // minecraft.server
        initWrapper(ChunkMap.class, ChunkMap.MAPPING.runtimeClass());
        initWrapper(DedicatedPlayerList.class, DedicatedPlayerList.MAPPING.runtimeClass());
        initWrapper(DedicatedServer.class, DedicatedServer.MAPPING.runtimeClass());
        initWrapper(DedicatedServerProperties.class, DedicatedServerProperties.MAPPING.runtimeClass());
        initWrapper(MinecraftServer.class, MinecraftServer.MAPPING.runtimeClass());
        initWrapper(PlayerList.class, PlayerList.MAPPING.runtimeClass());
        initWrapper(ServerChunkCache.class, ServerChunkCache.MAPPING.runtimeClass());
        initWrapper(ServerGamePacketListenerImpl.class, ServerGamePacketListenerImpl.MAPPING.runtimeClass());
        initWrapper(ServerLevel.class, ServerLevel.MAPPING.runtimeClass());
        initWrapper(ServerPlayer.class, ServerPlayer.MAPPING.runtimeClass());
        initWrapper(Settings.class, Settings.MAPPING.runtimeClass());
        // minecraft.util
        initWrapper(ProgressListener.class, ProgressListener.MAPPING.runtimeClass());
        // minecraft.world.block
        initWrapper(BambooBlock.class, BambooBlock.MAPPING.runtimeClass());
        // minecraft.world
        initWrapper(AABB.class, AABB.MAPPING.runtimeClass());
        initWrapper(ChunkPos.class, ChunkPos.MAPPING.runtimeClass());
        initWrapper(ChunkStorage.class, ChunkStorage.MAPPING.runtimeClass());
        initWrapper(DamageSource.class, DamageSource.MAPPING.runtimeClass());
        initWrapper(DamageSources.class, DamageSources.MAPPING.runtimeClass());
        initWrapper(Entity.class, Entity.MAPPING.runtimeClass());
        initWrapper(ItemStack.class, ItemStack.MAPPING.runtimeClass());
        initWrapper(Level.class, Level.MAPPING.runtimeClass());
        initWrapper(MapItemSavedData.class, MapItemSavedData.MAPPING.runtimeClass());
        initWrapper(PlayerDataStorage.class, PlayerDataStorage.MAPPING.runtimeClass());
        initWrapper(SavedData.class, SavedData.MAPPING.runtimeClass());
        initWrapper(Vec3.class, Vec3.MAPPING.runtimeClass());
        initWrapper(VoxelShape.class, VoxelShape.MAPPING.runtimeClass());
        // minecraft
        initWrapper(DetectedVersion.class, DetectedVersion.MAPPING.runtimeClass());
        initWrapper(GameVersion.class, GameVersion.REFLECT.get());
        initWrapper(SharedConstants.class, SharedConstants.MAPPING.runtimeClass());
        initWrapper(WorldVersion.class, WorldVersion.MAPPING.runtimeClass());

        // netty
        initWrapper(ByteBuf.class, ByteBuf.REFLECT.get());
        initWrapper(Unpooled.class, Unpooled.REFLECT.get());

        // paper.configuration
        initWrapper(FallbackValue_Int.class, FallbackValue_Int.REFLECT.get());
        initWrapper(WorldConfiguration.class, WorldConfiguration.REFLECT.get());
        initWrapper(WorldConfiguration.Chunks.class, WorldConfiguration.Chunks.REFLECT.get());
        // paper
        initWrapper(AABBVoxelShape.class, AABBVoxelShape.REFLECT.get());
        initWrapper(PaperAdventure.class, PaperAdventure.REFLECT.get());
        initWrapper(QueuedChangesMapLong2Object.class, QueuedChangesMapLong2Object.REFLECT.get());
    }
}
