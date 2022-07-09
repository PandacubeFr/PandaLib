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

import fr.pandacube.lib.core.chat.Chat;

/**
 * Represents some special mob heads, also support creating player skulls and custom skulls.
 *
 * @author xigsag, SBPrime
 *
 * @see <a href="https://github.com/TigerHix/Hex-Utils/blob/9954159a323d12733b29c287a56980991cee2948/hex/util/Skull.java">github.com/TigerHix/Hex-Utils/hex/util/Skull.java</a>
 */
public enum Skull {

    ARROW_LEFT("MHF_ArrowLeft"),
    ARROW_RIGHT("MHF_ArrowRight"),
    ARROW_UP("MHF_ArrowUp"),
    ARROW_DOWN("MHF_ArrowDown"),
    QUESTION("MHF_Question"),
    EXCLAMATION("MHF_Exclamation"),
    CAMERA("FHG_Cam"),

    ZOMBIE_PIGMAN("MHF_PigZombie"),
    PIG("MHF_Pig"),
    SHEEP("MHF_Sheep"),
    BLAZE("MHF_Blaze"),
    CHICKEN("MHF_Chicken"),
    COW("MHF_Cow"),
    SLIME("MHF_Slime"),
    SPIDER("MHF_Spider"),
    SQUID("MHF_Squid"),
    VILLAGER("MHF_Villager"),
    OCELOT("MHF_Ocelot"),
    HEROBRINE("MHF_Herobrine"),
    LAVA_SLIME("MHF_LavaSlime"),
    MOOSHROOM("MHF_MushroomCow"),
    GOLEM("MHF_Golem"),
    GHAST("MHF_Ghast"),
    ENDERMAN("MHF_Enderman"),
    CAVE_SPIDER("MHF_CaveSpider"),

    CACTUS("MHF_Cactus"),
    CAKE("MHF_Cake"),
    CHEST("MHF_Chest"),
    MELON("MHF_Melon"),
    LOG("MHF_OakLog"),
    PUMPKIN("MHF_Pumpkin"),
    TNT("MHF_TNT"),
    DYNAMITE("MHF_TNT2");

    private final String name;

    Skull(String mcName) {
        name = mcName;
    }

    /**
     * Return the item based on this Skull enum.
     *
     * @return itemstack
     */
    public ItemStack get() {
    	return get(null, null);
    }
    /**
     * Return the item based on this Skull enum, with the provided display name and lore.
     *
     * @return itemstack
     */
    public ItemStack get(Chat dispName, List<Chat> lore) {
        return getFromPlayerName(name, dispName, lore);
    }

    
    
    /**
     * Return a skull of a player based on his name.
     *
     * @param name player's name
     * @return itemstack
     */
	public static ItemStack getFromPlayerName(String name, Chat dispName, List<Chat> lore) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        
        @SuppressWarnings({ "deprecation", "unused" })
        boolean b = meta.setOwner(name);
        
        if (dispName != null)
        	meta.displayName(dispName.getAdv());
        
        if (lore != null)
        	meta.lore(lore.stream().map(Chat::getAdv).collect(Collectors.toList()));
        
        itemStack.setItemMeta(meta);
        return itemStack;
    }
    
    
    
    
	
	
	
	
	
    

    /**
     * Return a skull that has a custom texture specified by url.
     *
     * @param url skin url
     * @return itemstack
     */
    public static ItemStack getFromSkinURL(String url) {
        return getFromSkinURL(url, null, null);
    }

    /**
     * Return a skull that has a custom texture specified by url.
     *
     * @param url skin url
     * @return itemstack
     */
    public static ItemStack getFromSkinURL(String url, Chat name, List<Chat> lore) {
        return getFromBase64String(Base64.getEncoder().encodeToString(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes()), name, lore);
    }
    
    
    
    
    

    

    /**
     * Return a skull that has a custom texture specified by a base64 String.
     *
     * @param str the base64 string from gameprofile informations
     * @return itemstack
     */
    public static ItemStack getFromBase64String(String str) {
    	return getFromBase64String(str, null, null);
    }
    

    /**
     * Return a skull that has a custom texture specified by a base64 String.
     *
     * @param str the base64 string from gameprofile informations
     * @return itemstack
     */
    public static ItemStack getFromBase64String(String str, Chat dispName, List<Chat> lore) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();

        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        profile.setProperty(new ProfileProperty("textures", str));
        headMeta.setPlayerProfile(profile);
        
        if (dispName != null)
        	headMeta.displayName(dispName.getAdv());
        
        if (lore != null)
        	headMeta.lore(lore.stream().map(Chat::getAdv).collect(Collectors.toList()));
        
        head.setItemMeta(headMeta);
        
        return head;
    }
    
    
    
    
    

}
 