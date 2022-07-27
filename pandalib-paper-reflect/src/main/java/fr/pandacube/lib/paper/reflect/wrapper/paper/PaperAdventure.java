package fr.pandacube.lib.paper.reflect.wrapper.paper;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.chat.Component;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class PaperAdventure extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("io.papermc.paper.adventure.PaperAdventure"));
    private static final ReflectMethod<?> asAdventure = wrapEx(() -> REFLECT.method("asAdventure", Component.MAPPING.runtimeClass()));

    public static net.kyori.adventure.text.Component asAdventure(Component component) {
        return (net.kyori.adventure.text.Component) wrapReflectEx(() -> asAdventure.invokeStatic(unwrap(component)));
    }




    protected PaperAdventure(Object obj) {
        super(obj);
    }
}
