package fr.pandacube.lib.paper.reflect.wrapper.paper.commands;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import org.bukkit.plugin.Plugin;

import java.util.List;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class APICommandMeta extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("io.papermc.paper.command.brigadier.APICommandMeta"));
    private static final ReflectMethod<?> plugin = wrapEx(() -> REFLECT.method("plugin"));
    private static final ReflectMethod<?> description = wrapEx(() -> REFLECT.method("description"));
    private static final ReflectMethod<?> aliases = wrapEx(() -> REFLECT.method("aliases"));


    public Plugin plugin() {
        return (Plugin) wrapReflectEx(() -> plugin.invoke(__getRuntimeInstance()));
    }

    public String description() {
        return (String) wrapReflectEx(() -> description.invoke(__getRuntimeInstance()));
    }

    @SuppressWarnings("unchecked")
    public List<String> aliases() {
        return (List<String>) wrapReflectEx(() -> aliases.invoke(__getRuntimeInstance()));
    }



    protected APICommandMeta(Object obj) {
        super(obj);
    }
}
