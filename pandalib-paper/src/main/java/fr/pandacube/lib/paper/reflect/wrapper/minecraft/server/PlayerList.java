package fr.pandacube.lib.paper.reflect.wrapper.minecraft.server;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.PlayerDataStorage;
import fr.pandacube.lib.reflect.ReflectField;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class PlayerList extends ReflectWrapper {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.server.players.PlayerList"));
    private static final ReflectField<?> playerIo = wrapEx(() -> MAPPING.mojField("playerIo"));

    public PlayerDataStorage playerIo() {
        return wrap(wrapReflectEx(() -> playerIo.getValue(__getRuntimeInstance())), PlayerDataStorage.class);
    }

    protected PlayerList(Object obj) {
        super(obj);
    }
}
