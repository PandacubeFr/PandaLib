package fr.pandacube.lib.paper.reflect;

import fr.pandacube.lib.paper.reflect.wrapper.brigadier.CommandNode;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftMapView;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftPlayer;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftServer;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.VanillaCommandWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.DedicatedServer;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.block.BambooStalkBlock;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.block.Block;
import fr.pandacube.lib.paper.reflect.wrapper.paper.commands.APICommandMeta;
import fr.pandacube.lib.paper.reflect.wrapper.paper.commands.BukkitCommandNode;
import fr.pandacube.lib.reflect.ReflectionWrapperBypass;
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

        ReflectionWrapperBypass.enable();

        initWrapperClasses();
    }

    private static void initWrapperClasses() throws Exception {

        ThrowableAccumulator<Throwable> thAcc = new ThrowableAccumulator<>(Throwable.class);

        // brigadier
        thAcc.catchThrowable(() -> initWrapper(CommandNode.class, CommandNode.REFLECT.get()));

        // craftbukkit
        thAcc.catchThrowable(() -> initWrapper(CraftMapView.class, CraftMapView.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(CraftPlayer.class, CraftPlayer.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(CraftServer.class, CraftServer.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(VanillaCommandWrapper.class, VanillaCommandWrapper.REFLECT.get()));

        // minecraft.server
        thAcc.catchThrowable(() -> initWrapper(DedicatedServer.class, DedicatedServer.REFLECT.get()));
        // minecraft.world.block
        thAcc.catchThrowable(() -> initWrapper(Block.class, Block.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(BambooStalkBlock.class, BambooStalkBlock.REFLECT.get()));

        // paper.commands
        thAcc.catchThrowable(() -> initWrapper(APICommandMeta.class, APICommandMeta.REFLECT.get()));
        thAcc.catchThrowable(() -> initWrapper(BukkitCommandNode.class, BukkitCommandNode.REFLECT.get()));


        thAcc.throwCaught();

    }

    private PandalibPaperReflect() {}
}
