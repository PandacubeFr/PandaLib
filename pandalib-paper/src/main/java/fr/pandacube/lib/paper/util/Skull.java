package fr.pandacube.lib.paper.util;

import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

import fr.pandacube.lib.chat.Chat;

/**
 * Represents some special mob heads, also support creating player skulls and custom skulls.
 *
 * @author xigsag, SBPrime
 *
 * @see <a href="https://github.com/TigerHix/Hex-Utils/blob/9954159a323d12733b29c287a56980991cee2948/hex/util/Skull.java">github.com/TigerHix/Hex-Utils/hex/util/Skull.java</a>
 */
public enum Skull {

    /** Standard skull of player MHF_ArrowLeft. */
    ARROW_LEFT("MHF_ArrowLeft"),
    /** Standard skull of player MHF_ArrowRight. */
    ARROW_RIGHT("MHF_ArrowRight"),
    /** Standard skull of player MHF_ArrowUp. */
    ARROW_UP("MHF_ArrowUp"),
    /** Standard skull of player MHF_ArrowDown. */
    ARROW_DOWN("MHF_ArrowDown"),
    /** Standard skull of player MHF_Question. */
    QUESTION("MHF_Question"),
    /** Standard skull of player MHF_Exclamation. */
    EXCLAMATION("MHF_Exclamation"),
    /** Standard skull of player FHG_Cam. */
    CAMERA("FHG_Cam"),

    /** Standard skull of player MHF_PigZombie. */
    ZOMBIE_PIGMAN("MHF_PigZombie"),
    /** Standard skull of player MHF_Pig. */
    PIG("MHF_Pig"),
    /** Standard skull of player MHF_Sheep. */
    SHEEP("MHF_Sheep"),
    /** Standard skull of player MHF_Blaze. */
    BLAZE("MHF_Blaze"),
    /** Standard skull of player MHF_Chicken. */
    CHICKEN("MHF_Chicken"),
    /** Standard skull of player MHF_Cow. */
    COW("MHF_Cow"),
    /** Standard skull of player MHF_Slime. */
    SLIME("MHF_Slime"),
    /** Standard skull of player MHF_Spider. */
    SPIDER("MHF_Spider"),
    /** Standard skull of player MHF_Squid. */
    SQUID("MHF_Squid"),
    /** Standard skull of player MHF_Villager. */
    VILLAGER("MHF_Villager"),
    /** Standard skull of player MHF_Ocelot. */
    OCELOT("MHF_Ocelot"),
    /** Standard skull of player MHF_Herobrine. */
    HEROBRINE("MHF_Herobrine"),
    /** Standard skull of player MHF_LavaSlime. */
    LAVA_SLIME("MHF_LavaSlime"),
    /** Standard skull of player MHF_MushroomCow. */
    MOOSHROOM("MHF_MushroomCow"),
    /** Standard skull of player MHF_Golem. */
    GOLEM("MHF_Golem"),
    /** Standard skull of player MHF_Ghast. */
    GHAST("MHF_Ghast"),
    /** Standard skull of player MHF_Enderman. */
    ENDERMAN("MHF_Enderman"),
    /** Standard skull of player MHF_CaveSpider. */
    CAVE_SPIDER("MHF_CaveSpider"),

    /** Standard skull of player MHF_Cactus. */
    CACTUS("MHF_Cactus"),
    /** Standard skull of player MHF_Cake. */
    CAKE("MHF_Cake"),
    /** Standard skull of player MHF_Chest. */
    CHEST("MHF_Chest"),
    /** Standard skull of player MHF_Melon. */
    MELON("MHF_Melon"),
    /** Standard skull of player MHF_OakLog. */
    LOG("MHF_OakLog"),
    /** Standard skull of player MHF_Pumpkin. */
    PUMPKIN("MHF_Pumpkin"),
    /** Standard skull of player MHF_TNT. */
    TNT("MHF_TNT"),
    /** Standard skull of player MHF_TNT2. */
    DYNAMITE("MHF_TNT2");

    private final String name;

    Skull(String mcName) {
        name = mcName;
    }

    /**
     * Return the item based on this Skull enum.
     *
     * @return item stack
     */
    public ItemStack get() {
    	return get(null, null);
    }
    /**
     * Return the item based on this Skull enum, with the provided display name and lore.
     * @param displayName the display name to add to the returned skull.
     * @param lore the lore to add to the returned skull.
     * @return item stack
     */
    public ItemStack get(Chat displayName, List<Chat> lore) {
        return getFromPlayerName(name, displayName, lore);
    }

    
    
    /**
     * Return a skull of a player based on their name.
     *
     * @param name player's name
     * @param displayName the display name to add to the returned skull.
     * @param lore the lore to add to the returned skull.
     * @return item stack
     */
	public static ItemStack getFromPlayerName(String name, Chat displayName, List<Chat> lore) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        
        @SuppressWarnings({ "deprecation", "unused" })
        boolean b = meta.setOwner(name);
        
        if (displayName != null)
        	meta.displayName(displayName.get());
        
        if (lore != null)
        	meta.lore(lore.stream().map(Chat::get).collect(Collectors.toList()));
        
        itemStack.setItemMeta(meta);
        return itemStack;
    }
    
    
    
    
	
	
	
	
	
    

    /**
     * Return a skull that has a custom texture specified by url.
     * @param url skin url.
     * @return item stack
     */
    public static ItemStack getFromSkinURL(String url) {
        return getFromSkinURL(url, null, null);
    }

    /**
     * Return a skull that has a custom texture specified by url.
     *
     * @param url the skin full url.
     * @param displayName the display name to add to the returned skull.
     * @param lore the lore to add to the returned skull.
     * @return item stack
     */
    public static ItemStack getFromSkinURL(String url, Chat displayName, List<Chat> lore) {
        return getFromBase64String(Base64.getEncoder().encodeToString(String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", url).getBytes()), displayName, lore);
    }
    
    
    
    
    

    

    /**
     * Return a skull that has a custom texture specified by a base64 String.
     *
     * @param str the base64 string from game profile information.
     * @return item stack
     */
    public static ItemStack getFromBase64String(String str) {
    	return getFromBase64String(str, null, null);
    }
    

    /**
     * Return a skull that has a custom texture specified by a base64 String.
     *
     * @param str the base64 string from game profile information.
     * @param displayName the display name to add to the returned skull.
     * @param lore the lore to add to the returned skull.
     * @return item stack
     */
    public static ItemStack getFromBase64String(String str, Chat displayName, List<Chat> lore) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();

        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes(str.getBytes()));
        profile.setProperty(new ProfileProperty("textures", str));
        headMeta.setPlayerProfile(profile);
        
        if (displayName != null)
        	headMeta.displayName(displayName.get());
        
        if (lore != null)
        	headMeta.lore(lore.stream().map(Chat::get).collect(Collectors.toList()));
        
        head.setItemMeta(headMeta);
        
        return head;
    }
    
    
    
    
    

}
 