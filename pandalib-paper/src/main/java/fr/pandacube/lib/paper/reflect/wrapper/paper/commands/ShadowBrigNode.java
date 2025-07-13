package fr.pandacube.lib.paper.reflect.wrapper.paper.commands;

import fr.pandacube.lib.paper.reflect.wrapper.brigadier.CommandNode;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

public class ShadowBrigNode<S> extends CommandNode<S> {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("io.papermc.paper.command.brigadier.ShadowBrigNode"));




    protected ShadowBrigNode(Object obj) {
        super(obj);
    }
}
