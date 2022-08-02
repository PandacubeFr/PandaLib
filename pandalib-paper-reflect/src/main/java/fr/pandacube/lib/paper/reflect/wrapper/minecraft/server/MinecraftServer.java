package fr.pandacube.lib.paper.reflect.wrapper.minecraft.server;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.Commands;
import fr.pandacube.lib.reflect.ReflectField;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class MinecraftServer extends ReflectWrapper {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.server.MinecraftServer"));
    private static final ReflectField<?> vanillaCommandDispatcher = wrapEx(() -> MAPPING.runtimeReflect().field("vanillaCommandDispatcher"));

    public Commands vanillaCommandDispatcher() {
        return wrap(wrapReflectEx(() -> vanillaCommandDispatcher.getValue(__getRuntimeInstance())), Commands.class);
    }

    protected MinecraftServer(Object obj) {
        super(obj);
    }
}
