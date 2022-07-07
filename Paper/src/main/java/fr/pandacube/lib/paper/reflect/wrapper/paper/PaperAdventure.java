package fr.pandacube.lib.paper.reflect.wrapper.paper;

import fr.pandacube.lib.core.util.Reflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.chat.Component;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.core.util.ThrowableUtil.wrapReflectEx;

public class PaperAdventure extends ReflectWrapper {
    public static final Reflect.ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("io.papermc.paper.adventure.PaperAdventure"));
    private static final Reflect.ReflectMethod<?> asAdventure = wrapEx(() -> REFLECT.method("asAdventure", Component.MAPPING.runtimeClass()));

    public static net.kyori.adventure.text.Component asAdventure(Component component) {
        return (net.kyori.adventure.text.Component) wrapReflectEx(() -> asAdventure.invokeStatic(unwrap(component)));
    }




    protected PaperAdventure(Object obj) {
        super(obj);
    }
}
