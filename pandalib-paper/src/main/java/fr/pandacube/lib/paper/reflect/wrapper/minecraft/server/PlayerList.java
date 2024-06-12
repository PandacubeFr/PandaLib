package fr.pandacube.lib.paper.reflect.wrapper.minecraft.server;

import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.PlayerDataStorage;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectField;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class PlayerList extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.server.players.PlayerList"));
    private static final ReflectField<?> playerIo = wrapEx(() -> REFLECT.field("playerIo"));

    public PlayerDataStorage playerIo() {
        return wrap(wrapReflectEx(() -> playerIo.getValue(__getRuntimeInstance())), PlayerDataStorage.class);
    }

    protected PlayerList(Object obj) {
        super(obj);
    }
}
