package fr.pandacube.lib.paper.reflect;

import fr.pandacube.lib.paper.reflect.wrapper.WrapperRegistry;

public class PandalibPaperReflect {

    public static void init() {
        NMSReflect.init();
        WrapperRegistry.init();
    }
}
