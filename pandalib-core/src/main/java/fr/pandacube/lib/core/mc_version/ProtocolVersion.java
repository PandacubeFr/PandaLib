package fr.pandacube.lib.core.mc_version;

import fr.pandacube.lib.core.json.Json;
import fr.pandacube.lib.util.Log;
import fr.pandacube.lib.util.StringUtil;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Class handling a relationship table of known Minecraft version and their
 * corresponding protocol version.
 * <p>
 * The data if fetch updated data from an external API on startup. If it fails,
 * it uses the data stored in the current package at build time.
 * <p>
 * The public static methos are used to fetch an instance of {@link ProtocolVersion}
 * based on the provided protocol version (eg. 763) or Minecraft version (eg. "1.20.1").
 * An instance of this class provides information related to a protocol version
 * (the protocol version number and all the corresponding Minecraft versions).
 */
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

    /**
     * Replace the currently used data cache by a new source.
     * <p>
     * <b>Note: </b>this method is not meant to be used by the final user of
     * this class. Use it only if you have a better data source.
     * @param data the data to use instead of the provided (external API or packaged file)
     */
    public static void setRawData(MinecraftVersionList data) {
        versionList.set(data);
    }

    /**
     * Gets the raw data used internally by this class.
     * <p>
     * <b>Note: </b>this method is not meant to be used by the final user of
     * this class. Use it only if you know what you do.
     * @return the current instance of {@link MinecraftVersionUtil} uses
     *         internally by this class.
     */
    public static MinecraftVersionList getRawData() {
        initIfNecessary();
        return versionList.get();
    }

    private static void init() {
        // try online source first
        try {
            HttpResponse<String> response = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build()
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


    private static int getPVNOfVersion(String version) {
        initIfNecessary();
        Integer v = versionList.get().protocolOfVersion().get(version);
        return v == null ? -1 : v;
    }

    private static List<String> getVersionsOfPVN(int pvn) {
        initIfNecessary();
        return versionList.get().versionsOfProtocol().get(pvn);
    }

    /**
     * Gets the {@link ProtocolVersion} associated with the provided Minecraft version.
     * @param version The Minecraft version, in the format "X.X[.X]" (eg. "1.17" or "1.8.8").
     * @return an instance of {@link ProtocolVersion}.
     */
    public static ProtocolVersion ofVersion(String version) {
        int pvn = getPVNOfVersion(version);
        if (pvn == -1)
            return null;
        List<String> versions = getVersionsOfPVN(pvn);
        if (versions == null) {
            versions = List.of(version);
        }
        return new ProtocolVersion(pvn, List.copyOf(versions));
    }

    /**
     * Gets the {@link ProtocolVersion} associated with the provided protocol version number.
     * @param pvn The protocol version number.
     * @return an instance of {@link ProtocolVersion}.
     */
    public static ProtocolVersion ofProtocol(int pvn) {
        List<String> versions = getVersionsOfPVN(pvn);
        if (versions == null) {
            return null;
        }
        return new ProtocolVersion(pvn, List.copyOf(versions));
    }


    /**
     * Returns all the {@link ProtocolVersion} currently known by this class.
     * @return all the {@link ProtocolVersion} currently known by this class.
     */
    public static List<ProtocolVersion> allKnownProtocolVersions() {
        return versionList.get().versionsOfProtocol().keySet().stream()
                .map(ProtocolVersion::ofProtocol)
                .toList();
    }


    /**
     * Generate a string representation of the provided list of version, using
     * {@link StringUtil#joinGrammatically(CharSequence, CharSequence, List)}.
     *
     * @param versions the minecraft versions to list
     * @param finalWordSeparator the word separator between the two last versions in the returned string, like "and",
     *                           "or" or any other word of any language. The spaces before and after are already
     *                           concatenated.
     * @return a string representation of the provided list of version.
     */
    public static String displayOptimizedListOfVersions(List<ProtocolVersion> versions, String finalWordSeparator) {
        return MinecraftVersionUtil.toString(versions.stream().flatMap(pv -> pv.versions.stream()).toList(), finalWordSeparator);
    }








    /**
     * The protocol version number.
     */
    public final int protocolVersionNumber;
    /**
     * All Minecraft version supported by this protocol version number.
     */
    public final List<String> versions;

    private ProtocolVersion(int protocolVersionNumber, List<String> versions) {
        this.protocolVersionNumber = protocolVersionNumber;
        this.versions = versions;
    }

    @Override
    public String toString() {
        return "ProtocolVersion{protocol=" + protocolVersionNumber + ", toString(\"and\")=" + toString("and") + "}";
    }

    /**
     * Returns a string representation of all the Minecraft version of this enum value, using
     * {@link StringUtil#joinGrammatically(CharSequence, CharSequence, List)}.
     *
     * @param finalWordSeparator the word separator between the two last versions in the returned string, like "and",
     *                           "or" or any other word of any language. The spaces before and after are already
     *                           concatenated.
     * @return a string representation of this {@link ProtocolVersion}.
     */
    public String toString(String finalWordSeparator) {
        return displayOptimizedListOfVersions(List.of(this), finalWordSeparator);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ProtocolVersion pv && protocolVersionNumber == pv.protocolVersionNumber;
    }

    @Override
    public int hashCode() {
        return protocolVersionNumber;
    }
}
