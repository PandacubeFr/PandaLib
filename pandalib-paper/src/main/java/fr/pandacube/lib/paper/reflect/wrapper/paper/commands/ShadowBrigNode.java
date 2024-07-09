package fr.pandacube.lib.paper.reflect.wrapper.paper.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperTyped;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

public class ShadowBrigNode extends ReflectWrapperTyped<LiteralCommandNode<CommandSourceStack>> {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("io.papermc.paper.command.brigadier.ShadowBrigNode"));




    protected ShadowBrigNode(Object obj) {
        super(obj);
    }
}
