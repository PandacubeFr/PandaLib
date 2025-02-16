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
    ARROW_LEFT("http://textures.minecraft.net/texture/3625902b389ed6c147574e422da8f8f361c8eb57e7631676a72777e7b1d"),
    /** Jungle wood arrow right. */
    ARROW_RIGHT("http://textures.minecraft.net/texture/d4be8aeec11849697adc6fd1f189b16642dff19f2955c05deaba68c9dff1be"),
    /** Jungle wood arrow up. */
    ARROW_UP("http://textures.minecraft.net/texture/88c0f37dec764d6e26b57aa8212572fbace5ee8f27f7b61c1fdaa47dd4c893"),
    /** Jungle wood arrow down. */
    ARROW_DOWN("http://textures.minecraft.net/texture/751ced2e647366f8f3ad2dfe415cca85651bfaf9739a95cd57b6f21cba053"),
    /** Jungle wood question mark. */
    QUESTION("http://textures.minecraft.net/texture/b4d7cc4dca986a53f1d6b52aaf376dc6acc73b8b287f42dc8fef5808bb5d76"),
    /** Jungle wood exclamation mark. */
    EXCLAMATION("http://textures.minecraft.net/texture/e869dc405a3155f281c16a3e8d9ff54afc1599153b4d9385c9b7bab88680f0");

    private final String skinUrl;

    Skull(String skinUrl) {
        this.skinUrl = skinUrl;
    }

    /**
     * Return the item based on this Skull enum.
     * @return the item stack.
     */
    public ItemStack get() {
        return getFromSkinURL(skinUrl);
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






    /**
     * Return a skull that has a custom texture specified by url.
     * @param url skin url.
     * @return item stack
     */
    public static ItemStack getFromSkinURL(String url) {
        return getFromProfile(ResolvableProfile.resolvableProfile().addProperty(getTexturesProperty(url)).build());
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
 