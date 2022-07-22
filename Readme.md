# Pandalib

### Development library for Minecraft server applications and plugins

This repository contains a collection of maven modules that are used for the development of our Minecraft server. Those
modules are made open source so they can be used by other developpers. Each of them provides different functionalities
that are detailed in their respective Readme file (if any).

- `pandalib-util` General purpose utility and helper classes
- `pandalib-chat` A chat API working on top of the Adventure API
- `pandalib-db` An ORM working with a MySQL server through JDBC
- `pandalib-permissions` A general purpose permission system
- `pandalib-reflect` A reflection wrapper to make reflective operation easier
- `pandalib-netapi` A poorly designed, but working TCP network library
- `pandalib-net` A better-designed, packet-based TCP network library (still in development)
- `pandalib-players-standalone` A library to handle classes representing online or offline player
- `pandalib-players-permissible` An extension of `pandalib-players-standalone` with support for the permission system `pandalib-permissions`
- `pandalib-core` A catch-all module for some helper classes that didn’t have their own module yet
- `pandalib-bungee` Utility and helper classes to use in Bungeecord plugins
- `pandalib-bungee-permissions` Integration of the permission system `pandalib-permissions` into Bungeecord
- `pandalib-bungee-players` A partial extension and implementation of `pandalib-players-standalone` for Bungeecord plugin
- `pandalib-paper` Utility and helper classes to use in Spigot/Paper plugins
- `pandalib-paper-reflect` A reflection API to ease access to NMS and OBS stuff in Paper server.
- `pandalib-paper-permissions` Integration of the permission system `pandalib-permissions` into Bukkit/Spigot/Paper permission system
- `pandalib-paper-players`  A partial extension and implementation of `pandalib-players-standalone` for Paper plugin
- `pandalib-cli` Utility and helper classes for a standalone Java application

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
            <version>master-SNAPSHOT</version> <!-- last version on master branch -->
        </dependency>
    </dependencies>
```

You can use the version as provided in the code above, but if you want a stable version, check those available in the
[tag section](https://github.com/PandacubeFr/PandaLib/tags). Don’t forget to take a look at the module’s readme file,
for any details you may need related to that specific module.