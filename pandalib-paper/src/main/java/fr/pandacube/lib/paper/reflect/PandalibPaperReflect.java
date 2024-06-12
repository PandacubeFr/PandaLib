package fr.pandacube.lib.paper.reflect;

import fr.pandacube.lib.paper.reflect.wrapper.brigadier.CommandNode;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftItemStack;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftMapView;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftMetaItem;
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
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.CommandSourceStack;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.Commands;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.Coordinates;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.GameProfileArgument;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.ResourceLocationArgument;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.Vec3Argument;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.core.BlockPos;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.core.Vec3i;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.CollectionTag;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.CompoundTag;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.ListTag;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.NbtAccounter;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.NbtIo;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.StringTag;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.Tag;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.FriendlyByteBuf;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.chat.Component;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.protocol.ClientboundCustomPayloadPacket;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.protocol.ClientboundGameEventPacket;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.protocol.Packet;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.protocol.custom.BrandPayload;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.protocol.custom.CustomPacketPayload;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.resources.ResourceLocation;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.ChunkMap;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.DedicatedPlayerList;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.DedicatedServer;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.DedicatedServerProperties;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.MinecraftServer;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.PlayerList;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.ServerChunkCache;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.ServerCommonPacketListenerImpl;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.ServerGamePacketListenerImpl;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.ServerLevel;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.ServerPlayer;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.Settings;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.util.ProgressListener;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.AABB;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.ChunkPos;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.ChunkStorage;
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
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.block.Block;
import fr.pandacube.lib.paper.reflect.wrapper.netty.ByteBuf;
import fr.pandacube.lib.paper.reflect.wrapper.netty.Unpooled;
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
        thAcc.catchThrowable(() -> initWrapper(CraftMetaItem.class, CraftMetaItem.REFLECT.get()));
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
        thAcc.catchThrowable(() -> initWrapper(Commands.class, Commands.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(CommandSourceStack.class, CommandSourceStack.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(Coordinates.class, Coordinates.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(GameProfileArgument.class, GameProfileArgument.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(ResourceLocationArgument.class, ResourceLocationArgument.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(Vec3Argument.class, Vec3Argument.REFLECT.get()));
        // minecraft.core
        thAcc.catchThrowable(() -> initWrapper(BlockPos.class, BlockPos.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(Vec3i.class, Vec3i.REFLECT.get()));
        // minecraft.nbt
        thAcc.catchThrowable(() -> initWrapper(CollectionTag.class, CollectionTag.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(CompoundTag.class, CompoundTag.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(ListTag.class, ListTag.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(NbtAccounter.class, NbtAccounter.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(NbtIo.class, NbtIo.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(StringTag.class, StringTag.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(Tag.class, Tag.REFLECT.get()));
        // minecraft.network.chat
        thAcc.catchThrowable(() -> initWrapper(Component.class, Component.REFLECT.get()));
        // minecraft.network.protocol.custom
        thAcc.catchThrowable(() -> initWrapper(BrandPayload.class, BrandPayload.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(CustomPacketPayload.class, CustomPacketPayload.REFLECT.get()));
        // minecraft.network.protocol
        thAcc.catchThrowable(() -> initWrapper(ClientboundCustomPayloadPacket.class, ClientboundCustomPayloadPacket.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(ClientboundGameEventPacket.class, ClientboundGameEventPacket.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(ClientboundGameEventPacket.Type.class, ClientboundGameEventPacket.Type.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(Packet.class, Packet.REFLECT.get()));
        // minecraft.network
        thAcc.catchThrowable(() -> initWrapper(FriendlyByteBuf.class, FriendlyByteBuf.REFLECT.get()));
        // minecraft.resources
        thAcc.catchThrowable(() -> initWrapper(ResourceLocation.class, ResourceLocation.REFLECT.get()));
        // minecraft.server
        thAcc.catchThrowable(() -> initWrapper(ChunkMap.class, ChunkMap.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(DedicatedPlayerList.class, DedicatedPlayerList.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(DedicatedServer.class, DedicatedServer.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(DedicatedServerProperties.class, DedicatedServerProperties.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(MinecraftServer.class, MinecraftServer.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(PlayerList.class, PlayerList.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(ServerChunkCache.class, ServerChunkCache.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(ServerCommonPacketListenerImpl.class, ServerCommonPacketListenerImpl.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(ServerGamePacketListenerImpl.class, ServerGamePacketListenerImpl.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(ServerLevel.class, ServerLevel.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(ServerPlayer.class, ServerPlayer.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(Settings.class, Settings.REFLECT.get()));
        // minecraft.util
        thAcc.catchThrowable(() -> initWrapper(ProgressListener.class, ProgressListener.REFLECT.get()));
        // minecraft.world.block
        thAcc.catchThrowable(() -> initWrapper(Block.class, Block.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(BambooStalkBlock.class, BambooStalkBlock.REFLECT.get()));
        // minecraft.world
        thAcc.catchThrowable(() -> initWrapper(AABB.class, AABB.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(ChunkPos.class, ChunkPos.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(ChunkStorage.class, ChunkStorage.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(DataVersion.class, DataVersion.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(Entity.class, Entity.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(ItemStack.class, ItemStack.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(Level.class, Level.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(MapItemSavedData.class, MapItemSavedData.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(PlayerDataStorage.class, PlayerDataStorage.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(SavedData.class, SavedData.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(Vec3.class, Vec3.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(VoxelShape.class, VoxelShape.REFLECT.get()));
        // minecraft
        thAcc.catchThrowable(() -> initWrapper(DetectedVersion.class, DetectedVersion.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(SharedConstants.class, SharedConstants.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(WorldVersion.class, WorldVersion.REFLECT.get()));

        // netty
        thAcc.catchThrowable(() -> initWrapper(ByteBuf.class, ByteBuf.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(Unpooled.class, Unpooled.REFLECT.get()));

        // paper.configuration
        thAcc.catchThrowable(() -> initWrapper(FallbackValue_Int.class, FallbackValue_Int.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(WorldConfiguration.class, WorldConfiguration.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(WorldConfiguration.Chunks.class, WorldConfiguration.Chunks.REFLECT.get()));
        // paper
        thAcc.catchThrowable(() -> initWrapper(PaperAdventure.class, PaperAdventure.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(QueuedChangesMapLong2Object.class, QueuedChangesMapLong2Object.REFLECT.get()));


        thAcc.throwCaught();

    }
}
