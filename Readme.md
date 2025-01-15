# Pandalib

### Development library for Minecraft server applications and plugins

This repository contains a collection of maven modules that are used for the development of our Minecraft server. Those
modules are made open source, so they can be used by other developers. Each of them provides different functionalities
that are detailed in their respective Readme file (if any).

- `pandalib-util` General purpose utility and helper classes;
- `pandalib-chat` A chat API working on top of the Adventure API;
- `pandalib-db` An ORM working with a MySQL server through JDBC;
- `pandalib-bungee` Utility and helper classes to use in BungeeCord plugins. Also provides platform implementation for `pandalib-players` and `pandalib-commands`;
- `pandalib-paper` Utility and helper classes to use in Spigot/Paper plugins. Also provides platform implementation for `pandalib-players` and `pandalib-commands`;
- `pandalib-reflect` A reflection wrapper to make reflective operation easier;
- `pandalib-permissions` A general purpose permission system;
- `pandalib-bungee-permissions` Integration of the permission system `pandalib-permissions` into BungeeCord;
- `pandalib-paper-permissions` Integration of the permission system `pandalib-permissions` into Bukkit, Vault and WEPIF permission systems;
- `pandalib-players` A library to handle classes representing online or offline players;
- `pandalib-players-permissible` An extension of `pandalib-players` with support for the permission system `pandalib-permissions`;
- `pandalib-netapi` A poorly designed, but working TCP network library;
- `pandalib-config` Utility and helper classes to handle configuration related files and folders;
- `pandalib-commands` An abstract command manager working on top of [Brigadier](https://github.com/Mojang/brigadier);
- `pandalib-cli` Utility and helper classes for a standalone CLI Java application;
- `pandalib-core` A catch-all module for some helper classes that didn't have their own module yet;

### Use in your projects

To use one of the module as a Maven dependency, add the Jitpack repository in the `<repositories>` section in your `pom.xml`:

```xml
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
```

Then, add any module you need in your `<dependencies>` section:
```xml
    <dependencies>
        <dependency>
            <groupId>fr.pandacube.pandalib</groupId>
            <artifactId>pandalib-util</artifactId> <!-- Put here the name of the module you want -->
            <version>master-SNAPSHOT</version> <!-- last version of master branch -->
        </dependency>
    </dependencies>
```

You can use the version as provided in the code above, but if you want a stable version, check those available in the
[tag section](https://github.com/PandacubeFr/PandaLib/tags).