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
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.DataVersion;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.Entity;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.ItemStack;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.Level;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.MapItemSavedData;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.PlayerDataStorage;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.SavedData;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.Vec3;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.VoxelShape;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.block.BambooStalkBlock;
import fr.pandacube.lib.paper.reflect.wrapper.netty.ByteBuf;
import fr.pandacube.lib.paper.reflect.wrapper.netty.Unpooled;
import fr.pandacube.lib.paper.reflect.wrapper.paper.AABBVoxelShape;
import fr.pandacube.lib.paper.reflect.wrapper.paper.PaperAdventure;
import fr.pandacube.lib.paper.reflect.wrapper.paper.QueuedChangesMapLong2Object;
import fr.pandacube.lib.paper.reflect.wrapper.paper.configuration.FallbackValue_Int;
import fr.pandacube.lib.paper.reflect.wrapper.paper.configuration.WorldConfiguration;
import fr.pandacube.lib.util.ThrowableAccumulator;

import static fr.pandacube.lib.reflect.wrapper.WrapperRegistry.initWrapper;

/**
 * Initializer for all the reflection tools in {@code pandalib-paper-reflect} module.
 */
public class PandalibPaperReflect {

    private static boolean isInit = false;

    /**
     * Initializes the reflection tools in {@code pandalib-paper-reflect} module.
     * @throws Exception if a problem occurs when initializing wrapper classes.
     */
    public static void init() throws Exception {
        NMSReflect.init();
        synchronized (PandalibPaperReflect.class) {
            if (isInit)
                return;
            isInit = true;
        }
        initWrapperClasses();
    }

    private static void initWrapperClasses() throws Exception {

        ThrowableAccumulator<Throwable> thAcc = new ThrowableAccumulator<>(Throwable.class);

        // brigadier
        thAcc.catchThrowable(() -> initWrapper(CommandNode.class, CommandNode.REFLECT.get()));

        // craftbukkit
        thAcc.catchThrowable(() -> initWrapper(CraftItemStack.class, CraftItemStack.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(CraftMapView.class, CraftMapView.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(CraftNamespacedKey.class, CraftNamespacedKey.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(CraftPlayer.class, CraftPlayer.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(CraftServer.class, CraftServer.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(CraftVector.class, CraftVector.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(CraftWorld.class, CraftWorld.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(RenderData.class, RenderData.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(VanillaCommandWrapper.class, VanillaCommandWrapper.REFLECT.get()));

        // data-converter
        thAcc.catchThrowable(() -> initWrapper(MCDataConverter.class, MCDataConverter.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(MCDataType.class, MCDataType.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(MCTypeRegistry.class, MCTypeRegistry.REFLECT.get()));

        // minecraft.commands
        thAcc.catchThrowable(() -> initWrapper(BlockPosArgument.class, BlockPosArgument.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(Commands.class, Commands.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(CommandSourceStack.class, CommandSourceStack.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(ComponentArgument.class, ComponentArgument.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(Coordinates.class, Coordinates.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(EntityArgument.class, EntityArgument.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(EntitySelector.class, EntitySelector.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(GameProfileArgument.class, GameProfileArgument.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(ResourceLocationArgument.class, ResourceLocationArgument.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(Vec3Argument.class, Vec3Argument.MAPPING.runtimeClass()));
        // minecraft.core
        thAcc.catchThrowable(() -> initWrapper(BlockPos.class, BlockPos.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(Vec3i.class, Vec3i.MAPPING.runtimeClass()));
        // minecraft.nbt
        thAcc.catchThrowable(() -> initWrapper(CollectionTag.class, CollectionTag.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(CompoundTag.class, CompoundTag.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(ListTag.class, ListTag.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(NbtIo.class, NbtIo.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(StringTag.class, StringTag.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(Tag.class, Tag.MAPPING.runtimeClass()));
        // minecraft.network.chat
        thAcc.catchThrowable(() -> initWrapper(Component.class, Component.MAPPING.runtimeClass()));
        // minecraft.network.protocol
        thAcc.catchThrowable(() -> initWrapper(ClientboundCustomPayloadPacket.class, ClientboundCustomPayloadPacket.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(ClientboundGameEventPacket.class, ClientboundGameEventPacket.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(ClientboundGameEventPacket.Type.class, ClientboundGameEventPacket.Type.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(Packet.class, Packet.MAPPING.runtimeClass()));
        // minecraft.network
        thAcc.catchThrowable(() -> initWrapper(FriendlyByteBuf.class, FriendlyByteBuf.MAPPING.runtimeClass()));
        // minecraft.resources
        thAcc.catchThrowable(() -> initWrapper(ResourceLocation.class, ResourceLocation.MAPPING.runtimeClass()));
        // minecraft.server
        thAcc.catchThrowable(() -> initWrapper(ChunkMap.class, ChunkMap.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(DedicatedPlayerList.class, DedicatedPlayerList.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(DedicatedServer.class, DedicatedServer.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(DedicatedServerProperties.class, DedicatedServerProperties.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(MinecraftServer.class, MinecraftServer.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(PlayerList.class, PlayerList.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(ServerChunkCache.class, ServerChunkCache.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(ServerGamePacketListenerImpl.class, ServerGamePacketListenerImpl.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(ServerLevel.class, ServerLevel.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(ServerPlayer.class, ServerPlayer.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(Settings.class, Settings.MAPPING.runtimeClass()));
        // minecraft.util
        thAcc.catchThrowable(() -> initWrapper(ProgressListener.class, ProgressListener.MAPPING.runtimeClass()));
        // minecraft.world.block
        thAcc.catchThrowable(() -> initWrapper(BambooStalkBlock.class, BambooStalkBlock.MAPPING.runtimeClass()));
        // minecraft.world
        thAcc.catchThrowable(() -> initWrapper(AABB.class, AABB.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(ChunkPos.class, ChunkPos.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(ChunkStorage.class, ChunkStorage.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(DamageSource.class, DamageSource.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(DamageSources.class, DamageSources.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(DataVersion.class, DataVersion.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(Entity.class, Entity.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(ItemStack.class, ItemStack.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(Level.class, Level.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(MapItemSavedData.class, MapItemSavedData.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(PlayerDataStorage.class, PlayerDataStorage.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(SavedData.class, SavedData.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(Vec3.class, Vec3.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(VoxelShape.class, VoxelShape.MAPPING.runtimeClass()));
        // minecraft
        thAcc.catchThrowable(() -> initWrapper(DetectedVersion.class, DetectedVersion.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(SharedConstants.class, SharedConstants.MAPPING.runtimeClass()));
        thAcc.catchThrowable(() -> initWrapper(WorldVersion.class, WorldVersion.MAPPING.runtimeClass()));

        // netty
        thAcc.catchThrowable(() -> initWrapper(ByteBuf.class, ByteBuf.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(Unpooled.class, Unpooled.REFLECT.get()));

        // paper.configuration
        thAcc.catchThrowable(() -> initWrapper(FallbackValue_Int.class, FallbackValue_Int.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(WorldConfiguration.class, WorldConfiguration.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(WorldConfiguration.Chunks.class, WorldConfiguration.Chunks.REFLECT.get()));
        // paper
        thAcc.catchThrowable(() -> initWrapper(AABBVoxelShape.class, AABBVoxelShape.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(PaperAdventure.class, PaperAdventure.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(QueuedChangesMapLong2Object.class, QueuedChangesMapLong2Object.REFLECT.get()));


        thAcc.throwCaught();

    }
}
