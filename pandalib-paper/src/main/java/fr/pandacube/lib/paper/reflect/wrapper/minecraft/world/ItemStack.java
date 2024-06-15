package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftServer;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.core.HolderLookupProvider;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.core.RegistryAccess;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.Tag;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import org.bukkit.Bukkit;

import java.util.Optional;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class ItemStack extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.world.item.ItemStack"));
    private static final ReflectMethod<?> parse = wrapEx(() -> REFLECT.method("parse", HolderLookupProvider.REFLECT.get(), Tag.REFLECT.get()));
    private static final ReflectMethod<?> saveWithPrefix = wrapEx(() -> REFLECT.method("save", HolderLookupProvider.REFLECT.get(), Tag.REFLECT.get()));
    private static final ReflectMethod<?> save = wrapEx(() -> REFLECT.method("save", HolderLookupProvider.REFLECT.get()));

    @SuppressWarnings("unchecked")
    public static Optional<ItemStack> parse(HolderLookupProvider registries, Tag nbt) {
        return ((Optional<Object>) wrapReflectEx(() -> parse.invokeStatic(unwrap(registries), unwrap(nbt))))
                .map(o -> wrap(o, ItemStack.class));
    }

    
    public static Optional<ItemStack> parse(Tag nbt) {
        return parse(getRegistries(), nbt);
    }


    protected ItemStack(Object obj) {
        super(obj);
    }


    public Tag save(HolderLookupProvider registries, Tag prefix) {
        return wrap(wrapReflectEx(() -> saveWithPrefix.invoke(__getRuntimeInstance(), unwrap(registries), unwrap(prefix))), Tag.class);
    }


    public Tag save(HolderLookupProvider registries) {
        return wrap(wrapReflectEx(() -> save.invoke(__getRuntimeInstance(), unwrap(registries))), Tag.class);
    }


    public Tag save(Tag prefix) {
        return save(getRegistries(), prefix);
    }


    public Tag save() {
        return save(getRegistries());
    }

    private static RegistryAccess getRegistries() {
        return wrap(Bukkit.getServer(), CraftServer.class).getServer().registryAccess();
    }
}
