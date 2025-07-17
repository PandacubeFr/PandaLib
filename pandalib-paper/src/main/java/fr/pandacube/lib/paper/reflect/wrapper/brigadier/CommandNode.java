package fr.pandacube.lib.paper.reflect.wrapper.brigadier;

import fr.pandacube.lib.paper.reflect.wrapper.paper.commands.APICommandMeta;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectField;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperTyped;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class CommandNode<S> extends ReflectWrapperTyped<com.mojang.brigadier.tree.CommandNode<S>> {
    public static final ReflectClass<?> REFLECT = Reflect.ofClass(com.mojang.brigadier.tree.CommandNode.class);
    private static final ReflectMethod<?> removeCommand = wrapEx(() -> REFLECT.method("removeCommand", String.class));
    private static final ReflectField<?> apiCommandMeta = wrapEx(() -> REFLECT.field("apiCommandMeta"));

    public void removeCommand(String cmd) {
        wrapReflectEx(() -> removeCommand.invoke(__getRuntimeInstance(), cmd));
    }

    public APICommandMeta apiCommandMeta() {
        return wrap(wrapReflectEx(() -> apiCommandMeta.getValue(__getRuntimeInstance())), APICommandMeta.class);
    }

    public void apiCommandMeta(APICommandMeta meta) {
        wrapReflectEx(() -> apiCommandMeta.setValue(__getRuntimeInstance(), unwrap(meta)));
    }

    protected CommandNode(Object obj) {
        super(obj);
    }
}
