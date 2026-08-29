package fr.pandacube.lib.paper.reflect.wrapper.paper.commands;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import org.bukkit.plugin.Plugin;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class APICommandMeta extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("io.papermc.paper.command.brigadier.APICommandMeta"));
    private static final ReflectMethod<?> plugin = wrapEx(() -> REFLECT.method("plugin"));


    public Plugin plugin() {
        return (Plugin) wrapReflectEx(() -> plugin.invoke(__getRuntimeInstance()));
    }


    protected APICommandMeta(Object obj) {
        super(obj);
    }
}
