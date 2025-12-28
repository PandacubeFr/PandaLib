package fr.pandacube.lib.paper.reflect.wrapper.minecraft.server;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectConstructor;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import java.util.UUID;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

public class NameAndId extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.server.players.NameAndId"));
    private static final ReflectConstructor<?> CONSTRUCTOR = wrapEx(() -> REFLECT.constructor(UUID.class, String.class));

    public NameAndId(UUID id, String name) {
        super(wrapEx(() -> CONSTRUCTOR.instantiate(id, name)));
    }


    protected NameAndId(Object obj) {
        super(obj);
    }
}
