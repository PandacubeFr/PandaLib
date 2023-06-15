package fr.pandacube.lib.core.mc_version;

import fr.pandacube.lib.core.json.Json;
import fr.pandacube.lib.util.Log;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ProtocolVersion {

    private static final String ONLINE_DATA_URL = "https://api.pandacube.fr/rest/mcversion";

    private static final AtomicReference<MinecraftVersionList> versionList = new AtomicReference<>();

    private static void initIfNecessary() {
        synchronized (versionList) {
            if (versionList.get() == null) {
                init();
            }
        }
    }


    public static void setRawData(MinecraftVersionList data) {
        versionList.set(data);
    }
    public static MinecraftVersionList getRawData() {
        initIfNecessary();
        return versionList.get();
    }

    private static void init() {
        // try online source first
        try {
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(HttpRequest.newBuilder(URI.create(ONLINE_DATA_URL)).build(),
                            BodyHandlers.ofString()
                    );
            if (response.statusCode() == 200) {
                MinecraftVersionList data = Json.gson.fromJson(response.body(), MinecraftVersionList.class);
                versionList.set(data);
            }
        } catch (Exception e) {
            Log.warning(e);
        }

        if (versionList.get() != null) {
            return;
        }

        Log.warning("Unable to get minecraft version data from API. Using local data instead.");
        // try local source
        try (InputStream is = ProtocolVersion.class.getResourceAsStream("mcversion.json");
                InputStreamReader isr = new InputStreamReader(is)) {
            MinecraftVersionList data = Json.gson.fromJson(isr, MinecraftVersionList.class);
            versionList.set(data);
        } catch (Exception e) {
            Log.severe("Unable to get Minecraft versions data from classpath. Using empty data instead.");
            versionList.set(new MinecraftVersionList());
        }

    }




    public static int ofVersion(String versionId) {
        initIfNecessary();
        Integer v = versionList.get().protocolOfVersion().get(versionId);
        return v == null ? -1 : v;
    }


    public static List<String> getVersionsOfProtocol(int protocolVersion) {
        initIfNecessary();
        return versionList.get().versionsOfProtocol().get(protocolVersion);
    }


}
