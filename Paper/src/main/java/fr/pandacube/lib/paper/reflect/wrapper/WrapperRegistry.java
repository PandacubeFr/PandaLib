package fr.pandacube.lib.paper.reflect.wrapper;

import fr.pandacube.lib.core.util.Log;
import fr.pandacube.lib.core.util.Reflect;
import fr.pandacube.lib.core.util.Reflect.ReflectConstructor;
import fr.pandacube.lib.paper.reflect.wrapper.brigadier.CommandNode;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftMapView;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftNamespacedKey;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftPlayer;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftServer;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftVector;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftWorld;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.RenderData;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.VanillaCommandWrapper;
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
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.CompoundTag;
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
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.ServerChunkCache;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.ServerGamePacketListenerImpl;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.ServerLevel;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.ServerPlayer;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.util.ProgressListener;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.AABB;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.ChunkPos;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.ChunkStorage;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.DamageSource;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.Entity;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.Level;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.MapItemSavedData;
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

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class WrapperRegistry {

    public static void init() {

        // craftbukkit
        initWrapper(CommandNode.class, CommandNode.REFLECT.get());

        // craftbukkit
        initWrapper(CraftMapView.class, CraftMapView.REFLECT.get());
        initWrapper(CraftNamespacedKey.class, CraftNamespacedKey.REFLECT.get());
        initWrapper(CraftPlayer.class, CraftPlayer.REFLECT.get());
        initWrapper(CraftServer.class, CraftServer.REFLECT.get());
        initWrapper(CraftVector.class, CraftVector.REFLECT.get());
        initWrapper(CraftWorld.class, CraftWorld.REFLECT.get());
        initWrapper(RenderData.class, RenderData.REFLECT.get());
        initWrapper(VanillaCommandWrapper.class, VanillaCommandWrapper.REFLECT.get());

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
        initWrapper(CompoundTag.class, CompoundTag.MAPPING.runtimeClass());
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
        initWrapper(ServerChunkCache.class, ServerChunkCache.MAPPING.runtimeClass());
        initWrapper(ServerGamePacketListenerImpl.class, ServerGamePacketListenerImpl.MAPPING.runtimeClass());
        initWrapper(ServerLevel.class, ServerLevel.MAPPING.runtimeClass());
        initWrapper(ServerPlayer.class, ServerPlayer.MAPPING.runtimeClass());
        // minecraft.util
        initWrapper(ProgressListener.class, ProgressListener.MAPPING.runtimeClass());
        // minecraft.world.block
        initWrapper(BambooBlock.class, BambooBlock.MAPPING.runtimeClass());
        // minecraft.world
        initWrapper(AABB.class, AABB.MAPPING.runtimeClass());
        initWrapper(ChunkPos.class, ChunkPos.MAPPING.runtimeClass());
        initWrapper(ChunkStorage.class, ChunkStorage.MAPPING.runtimeClass());
        initWrapper(DamageSource.class, DamageSource.MAPPING.runtimeClass());
        initWrapper(Entity.class, Entity.MAPPING.runtimeClass());
        initWrapper(Level.class, Level.MAPPING.runtimeClass());
        initWrapper(MapItemSavedData.class, MapItemSavedData.MAPPING.runtimeClass());
        initWrapper(SavedData.class, SavedData.MAPPING.runtimeClass());
        initWrapper(Vec3.class, Vec3.MAPPING.runtimeClass());
        initWrapper(VoxelShape.class, VoxelShape.MAPPING.runtimeClass());
        // minecraft
        initWrapper(DetectedVersion.class, DetectedVersion.MAPPING.runtimeClass());
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


    /* package */ static Class<? extends ReflectWrapperI> getWrapperOfRuntimeClass(Class<?> runtime) {
        RegistryEntry e = WRAPPER_DATA_BY_RUNTIME_CLASS.get(runtime);
        return e == null ? null : e.wrapperClass;
    }

    /* package */ static Class<?> getRuntimeClassOfWrapperClass(Class<? extends ReflectWrapperI> wrapperClass) {
        RegistryEntry e = WRAPPER_DATA_BY_WRAPPER_CLASS.get(wrapperClass);
        return e == null ? null : e.runtimeClass;
    }

    /* package */ static ReflectConstructor<? extends ReflectWrapperI> getWrapperConstructorOfWrapperClass(Class<? extends ReflectWrapperI> wrapperClass) {
        RegistryEntry e = WRAPPER_DATA_BY_WRAPPER_CLASS.get(wrapperClass);
        return e == null ? null : e.objectWrapperConstructor;
    }




    private static final Map<Class<?>, RegistryEntry> WRAPPER_DATA_BY_RUNTIME_CLASS = new HashMap<>();
    private static final Map<Class<? extends ReflectWrapperI>, RegistryEntry> WRAPPER_DATA_BY_WRAPPER_CLASS = new HashMap<>();








    public static void initWrapper(Class<? extends ReflectWrapperI> wrapper, Class<?> runtime) {
        Class<? extends ReflectWrapperI> concreteWrapper = wrapper;
        ReflectConstructor<? extends ReflectWrapperI> objectWrapperConstructor;
        if (wrapper.isInterface() || Modifier.isAbstract(wrapper.getModifiers())) {
            ConcreteWrapper concreteWrapperAnnotation = wrapper.getAnnotation(ConcreteWrapper.class);
            if (concreteWrapperAnnotation == null || concreteWrapperAnnotation.value() == null) {
                Log.severe("The provided non-concrete (interface or abstract class) wrapper " + wrapper + " does not" +
                        " provide any concrete wrapper.");
                return;
            }
            concreteWrapper = concreteWrapperAnnotation.value();
            if (!wrapper.isAssignableFrom(concreteWrapper)) {
                Log.severe("The concrete wrapper " + concreteWrapper + " does not extends or implements " + wrapper + ".");
                return;
            }
        }
        try {
            objectWrapperConstructor = Reflect.ofClass(concreteWrapper).constructor(Object.class);
        } catch (NoSuchMethodException e) {
            Log.severe("The wrapper " + concreteWrapper + " does not provide a constructor that takes a unique" +
                    " Object parameter.", e);
            return;
        }
        RegistryEntry e = new RegistryEntry(runtime, wrapper, concreteWrapper, objectWrapperConstructor);
        WRAPPER_DATA_BY_RUNTIME_CLASS.put(runtime, e);
        WRAPPER_DATA_BY_WRAPPER_CLASS.put(wrapper, e);
        if (concreteWrapper != wrapper) {
            WRAPPER_DATA_BY_WRAPPER_CLASS.put(concreteWrapper, e);
        }
    }







    private static class RegistryEntry {
        Class<?> runtimeClass;
        Class<? extends ReflectWrapperI> wrapperClass;
        Class<? extends ReflectWrapperI> concreteWrapperClass;
        ReflectConstructor<? extends ReflectWrapperI> objectWrapperConstructor;

        public RegistryEntry(Class<?> runtimeClass, Class<? extends ReflectWrapperI> wrapperClass, Class<? extends ReflectWrapperI> concreteWrapperClass, ReflectConstructor<? extends ReflectWrapperI> objectWrapperConstructor) {
            this.runtimeClass = runtimeClass;
            this.wrapperClass = wrapperClass;
            this.concreteWrapperClass = concreteWrapperClass;
            this.objectWrapperConstructor = objectWrapperConstructor;
        }
    }

}
