package fr.pandacube.lib.paper.reflect.wrapper.brigadier;

import fr.pandacube.lib.core.util.Reflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapperTyped;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.core.util.ThrowableUtil.wrapReflectEx;

public class CommandNode<S> extends ReflectWrapperTyped<com.mojang.brigadier.tree.CommandNode<S>> {
    public static final Reflect.ReflectClass<?> REFLECT = Reflect.ofClass(com.mojang.brigadier.tree.CommandNode.class);
    private static final Reflect.ReflectMethod<?> removeCommand = wrapEx(() -> REFLECT.method("removeCommand", String.class));

    public void removeCommand(String cmd) {
        wrapReflectEx(() -> removeCommand.invoke(__getRuntimeInstance(), cmd));
    }

    protected CommandNode(Object obj) {
        super(obj);
    }
}
