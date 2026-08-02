package fr.pandacube.lib.paper.inventory;

import com.destroystokyo.paper.profile.ProfileProperty;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Represents some special mob heads, also support creating player skulls and custom skulls.
 */
public enum Skull {

    /** Jungle wood arrow left. */
    ARROW_LEFT("3625902b389ed6c147574e422da8f8f361c8eb57e7631676a72777e7b1d"),
    /** Jungle wood arrow right. */
    ARROW_RIGHT("d4be8aeec11849697adc6fd1f189b16642dff19f2955c05deaba68c9dff1be"),
    /** Jungle wood arrow up. */
    ARROW_UP("88c0f37dec764d6e26b57aa8212572fbace5ee8f27f7b61c1fdaa47dd4c893"),
    /** Jungle wood arrow down. */
    ARROW_DOWN("751ced2e647366f8f3ad2dfe415cca85651bfaf9739a95cd57b6f21cba053"),
    /** Jungle wood question mark. */
    QUESTION("b4d7cc4dca986a53f1d6b52aaf376dc6acc73b8b287f42dc8fef5808bb5d76"),
    /** Jungle wood question mark. */
    RED_ON_WHITE_QUESTION("ecc58cb55b1a11e6d88c2d4d1a6366c23887dee26304bda412c4a51825f199"),
    /** Jungle wood exclamation mark. */
    EXCLAMATION("e869dc405a3155f281c16a3e8d9ff54afc1599153b4d9385c9b7bab88680f0"),

    /** Plain color of the named chat color <b>BLACK</b>. */
    CHAT_COLOR_BLACK("967a2f218a6e6e38f2b545f6c17733f4ef9bbb288e75402949c052189ee"),
    /** Plain color of the named chat color <b>AQUA</b>. */
    CHAT_COLOR_AQUA("07c78f3ee783feecd2692eba54851da5c4323055ebd2f683cd3e8302fea7c"),
    /** Plain color of the named chat color <b>BLUE</b>. */
    CHAT_COLOR_BLUE("f8157b4dc5efc217352894471c116d39a034fc397c24539a9d0eeb2a465ca"),
    /** Plain color of the named chat color <b>DARK_AQUA</b>. */
    CHAT_COLOR_DARK_AQUA("1fdef6929ebaf394bce2ee7ea2adbbb5c83d7ce17e3b8615a928aefabf85b"),
    /** Plain color of the named chat color <b>DARK_BLUE</b>. */
    CHAT_COLOR_DARK_BLUE("6a46053012c68f289abcfb17ab8042d5afba95dcaa99c99c1e0360886d35"),
    /** Plain color of the named chat color <b>DARK_GRAY</b>. */
    CHAT_COLOR_DARK_GRAY("608f323462fb434e928bd6728638c944ee3d812e162b9c6ba070fcac9bf9"),
    /** Plain color of the named chat color <b>DARK_GREEN</b>. */
    CHAT_COLOR_DARK_GREEN("2c9e601ed9198dbb34c51ddf323929f01a5f958ab11133e3e0407b698393b3f"),
    /** Plain color of the named chat color <b>DARK_PURPLE</b>. */
    CHAT_COLOR_DARK_PURPLE("a5fc2e5d75106d491154450152bf4223e9dc92916c52118f64a812436c736"),
    /** Plain color of the named chat color <b>DARK_RED</b>. */
    CHAT_COLOR_DARK_RED("df4dc3c3753bf5b0b7f081cdb49b83d37428a12e4187f6346dec06fac54ce"),
    /** Plain color of the named chat color <b>GOLD</b>. */
    CHAT_COLOR_GOLD("97c2d5eee84bba1d7e94f933a0a556ed7ea4e4fa65e8e9f56325813b"),
    /** Plain color of the named chat color <b>GRAY</b>. */
    CHAT_COLOR_GRAY("38e2957699bc98a4b5d634ab71867eeb186b934bdb65d2c4b9dcc2b613cf5"),
    /** Plain color of the named chat color <b>GREEN</b>. */
    CHAT_COLOR_GREEN("d27ca46f6a9bb89a24fcaf4cc0acf5e8285a66db7521378ed2909ae449697f"),
    /** Plain color of the named chat color <b>LIGHT_PURPLE</b>. */
    CHAT_COLOR_LIGHT_PURPLE("73b0af83d0c728aeeca470f08a1d75f41cee253a3573ba4157ca2433e6c36"),
    /** Plain color of the named chat color <b>RED</b>. */
    CHAT_COLOR_RED("3c4d7a3bc3de833d3032e85a0bf6f2bef7687862b3c6bc40ce731064f615dd9d"),
    /** Plain color of the named chat color <b>WHITE</b>. */
    CHAT_COLOR_WHITE("366a5c98928fa5d4b5d5b8efb490155b4dda3956bcaa9371177814532cfc"),
    /** Plain color of the named chat color <b>YELLOW</b>. */
    CHAT_COLOR_YELLOW("c641682f43606c5c9ad26bc7ea8a30ee47547c9dfd3c6cda49e1c1a2816cf0ba");

    private final ResolvableProfile skullProfile;

    Skull(String skinUrl) {
        skullProfile = getProfileFromSkinURL(skinUrl);
    }

    /**
     * Return the item based on this Skull enum.
     * @return the item stack.
     */
    public ItemStack get() {
        return getFromProfile(skullProfile);
    }

    /**
     * Return an item stack builder already containing the skull.
     * @return an item stack builder already containing the skull.
     */
    public ItemStackBuilder builder() {
        return ItemStackBuilder.wrap(get());
    }








    /**
     * Return a skull of a player based on their name.
     *
     * @param name player's name
     * @return item stack
     */
    public static ItemStack getFromPlayerName(String name) {
        return getFromProfile(ResolvableProfile.resolvableProfile().name(name).build());
    }






    private static ResolvableProfile getProfileFromSkinURL(String url) {
        return ResolvableProfile.resolvableProfile().addProperty(getTexturesProperty(url)).build();
    }

    /**
     * Return a skull that has a custom texture specified by url.
     * @param url skin url.
     * @return item stack
     */
    public static ItemStack getFromSkinURL(String url) {
        return getFromProfile(getProfileFromSkinURL(url));
    }



    private static ItemStack getFromProfile(ResolvableProfile profile) {
        return ItemStackBuilder.of(Material.PLAYER_HEAD).profile(profile).build();
    }


    /**
     * The URL prefix for all the player related textures (skin, cape)
     */
    public static final String TEXTURE_URL_PREFIX = "http://textures.minecraft.net/texture/";

    private static final Pattern textureIdMatcher = Pattern.compile("^[0-9a-fA-F]+$");

    /**
     * Generate the base64 value of the "textures" profile property, based on the provided skin url!
     * @param skinURL the URL of the skin. The "https" will be replaced by "http" because this is the protocol used in
     *                the profile property url. If only the texture id part is provided, {@link #TEXTURE_URL_PREFIX} is
     *                prepended.
     * @return the base64 encoded texture data.
     */
    private static String encodeTextureBase64String(String skinURL) {
        if (skinURL.startsWith("https://")) // secure url is not the url found in texture data (even if it actually works in the browser)
            skinURL = "http://" + skinURL.substring("https://".length());
        if (!skinURL.startsWith(TEXTURE_URL_PREFIX)) { // accept taking only the texture id part ()
            if (textureIdMatcher.matcher(skinURL).matches())
                skinURL = TEXTURE_URL_PREFIX + skinURL;
            else
                throw new IllegalArgumentException("Invalid skin URL. Must be from " + TEXTURE_URL_PREFIX + ".");
        }
        return Base64.getEncoder().encodeToString(String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", skinURL).getBytes());
    }


    private static ProfileProperty getTexturesProperty(String skinURL) {
        return new ProfileProperty("textures", encodeTextureBase64String(skinURL));
    }
    
    
    
    
    

}
 