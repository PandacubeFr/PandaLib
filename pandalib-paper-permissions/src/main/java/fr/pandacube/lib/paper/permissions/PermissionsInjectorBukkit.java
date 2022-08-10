package fr.pandacube.lib.paper.permissions;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import fr.pandacube.lib.permissions.Permissions;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.util.Log;

/* package */ class PermissionsInjectorBukkit
{
	
	// to be called : onEnable for console, onPlayerLogin (not Join) (Priority LOWEST) for players
	public static void inject(CommandSender sender) {
        Permissible oldP = getPermissible(sender);
        if (oldP instanceof PandaPermissible)
        	return;
        if (!oldP.getClass().equals(PermissibleBase.class)) {
        	Log.warning("Another plugin is already injecting permissions into Bukkit for " + sender.getName() + ": " + oldP.getClass().getName());
        	Log.warning("Not injecting our own permissions.");
        }
		PandaPermissible p = new PandaPermissible(sender, getPermissible(sender));
		setPermissible(p.sender, p);
		p.recalculatePermissions();
	}
	
	
	
    // to be called : onDisable for console, onPlayerQuit () and onDisable for players
    public static void uninject(CommandSender sender)
    {
        Permissible perm = getPermissible(sender);
        if (perm instanceof PandaPermissible p) {
            setPermissible(sender, p.oldPermissible);
            p.oldPermissible.recalculatePermissions();
        }
    }

    private static void setPermissible(CommandSender sender, Permissible newpermissible)
    {
        try {
            Field perm = getPermField(sender);
            if (perm == null)
                return;
            perm.setAccessible(true);
            perm.set(sender, newpermissible);
        }
        catch (Exception e) {
            Log.severe(e);
        }
    }

    /* package */ static Permissible getPermissible(CommandSender sender)
    {
        Field perm = getPermField(sender);
        if (perm == null)
            return null;
        try {
            perm.setAccessible(true);
            Permissible p = (Permissible) perm.get(sender);
            if (p == null) {
            	Log.warning("Null permissible instance found in provided CommandSender: " + sender, new Throwable());
            }
            return p;
        }
        catch (Exception e) {
            Log.severe(e);
        }
        return null;
    }

    private static Field getPermField(CommandSender sender)
    {
    	if (sender == null) {
    		throw new IllegalArgumentException("sender cannot be null");
    	}
        try {
            if (sender instanceof Player || sender instanceof ConsoleCommandSender)
                return Reflect.ofClassOfInstance(sender).field("perm").get();
            else
            	throw new IllegalArgumentException("Unsupported type for sender: " + sender.getClass());
        }
        catch (Exception e) {
            Log.severe(e);
        }
        return null;
    }
    
    /* package */ static class PandaPermissible extends PermissibleBase
    {
        private final CommandSender sender;
        private final Permissible oldPermissible;
        
        /* package */ final LoadingCache<String, List<Permission>> superPermsPermissionCache = CacheBuilder.newBuilder()
        		.build(CacheLoader.from(PandalibPaperPermissions.SUPERPERMS_PARENT_PERMISSION_GETTER::apply));

        @SuppressWarnings("UnusedAssignment")
        private boolean init = false;
        /* assigment to false is necessary because of super class constructor calling the method recalculatePermission()
         * and we don’t want that.
         */

    	private PandaPermissible(CommandSender sender, Permissible oldPermissible)
        {
            super(sender);
            this.sender = sender;
            this.oldPermissible = oldPermissible;
            init = true;
            recalculatePermissions();
        }
    	
    	private Boolean hasPermissionOnServerInWorld(String permission) {
    		if (sender instanceof Player player) {
                String world = player.getWorld().getName();
        		return Permissions.getPlayer(player.getUniqueId()).hasPermission(permission, PandalibPaperPermissions.serverName, world);
    		}
    		return true;
    	}

        @Override
        public boolean hasPermission(String permission)
        {
        	/*
        	 * WARNING: don’t call PermissibleOnlinePlayer#hasPermission(String) here or it will result on a stack overflow
        	 */
        	
        	if (permission.toLowerCase().startsWith("minecraft.command."))
        		permission = PandalibPaperPermissions.permissionMap.getOrDefault(permission.toLowerCase(), permission);
        	
        	Boolean res = hasPermissionOnServerInWorld(permission); // supports negative permission
        	if (res != null)
        		return res;
        	
        	res = PandalibPaperPermissions.hasSuperPermsPermission(sender, permission, this::hasPermission, this); // supports negative permission
        	if (res != null)
        		return res;
        	
        	boolean reversed = permission.startsWith("-");
    		if (reversed) {
    			permission = permission.substring(1);
    		}
        	
        	return oldPermissible.hasPermission(permission) != reversed;
        }

        @Override
        public boolean hasPermission(Permission permission)
        {
        	if (permission.getName().toLowerCase().startsWith("minecraft.command.") && PandalibPaperPermissions.permissionMap.containsKey(permission.getName().toLowerCase())) {
        		return hasPermission(PandalibPaperPermissions.permissionMap.get(permission.getName().toLowerCase()));
        	}
        	
        	Boolean res = hasPermissionOnServerInWorld(permission.getName()); // supports negative permission
        	if (res != null)
        		return res;
        	
        	res = PandalibPaperPermissions.hasSuperPermsPermission(sender, permission.getName(), this::hasPermission, this); // supports negative permission
        	if (res != null)
        		return res;

        	return oldPermissible.hasPermission(permission); // doesn’t need to manage negative permission (should not happend)
        }

        @Override
        public void recalculatePermissions()
        {
        	// need this check because super class constructor calls this method,
        	// thus before oldPermissible has its value assigned
        	if (!init)
        		return;
        	
            oldPermissible.recalculatePermissions();
            
            superPermsPermissionCache.invalidateAll();
            
            effectivePermissionsListCache.invalidateAll();
        }

    	
    	private Map<String, Boolean> getEffectivePermissionsOnServerInWorld() {
    		if (sender instanceof Player player) {
                String world = player.getWorld().getName();
        		return Permissions.getPlayer(player.getUniqueId()).listEffectivePermissions(PandalibPaperPermissions.serverName, world);
    		}
    		return new HashMap<>();
    	}
    	

    	// key is world
    	private final Cache<String, Set<PermissionAttachmentInfo>> effectivePermissionsListCache = CacheBuilder.newBuilder()
    			.expireAfterAccess(10, TimeUnit.MINUTES)
    			.build();

        @Override
        public Set<PermissionAttachmentInfo> getEffectivePermissions()
        {
        	// PlotSquared uses this method to optimize permission range (plots.limit.10 for example)
        	// MobArena uses this method when a player leave the arena
        	// LibsDisguises uses this method (and only this one) to parse all the permissions
        	
        	//Log.warning("There is a plugin calling CommandSender#getEffectivePermissions(). See the stacktrace to understand the reason for that.", new Throwable());

        	String world = null;
    		if (sender instanceof Player player) {
                world = player.getWorld().getName();
    		}
        	
    		try {
				return effectivePermissionsListCache.get(world, () -> {
					// first get the superperms effective permissions (taht take isOp into accound)
					Map<String, PermissionAttachmentInfo> perms = oldPermissible.getEffectivePermissions().stream()
							.collect(Collectors.toMap(PermissionAttachmentInfo::getPermission, Function.identity()));
					
					// then override them with the permissions from our permission system (that has priority, and take current world into account)
					for (Map.Entry<String, Boolean> permE : getEffectivePermissionsOnServerInWorld().entrySet()) {
						perms.put(permE.getKey(), new PermissionAttachmentInfo(this, permE.getKey(), null, permE.getValue()));
					}
					
				    return new LinkedHashSet<>(perms.values());
				});
			} catch (ExecutionException e) {
				Log.severe(e);
				return oldPermissible.getEffectivePermissions();
			}
        	
        }

        @Override
        public boolean isOp()
        {
            return oldPermissible.isOp();
        }

        @Override
        public void setOp(boolean value)
        {
            oldPermissible.setOp(value);
        }

        @Override
        public boolean isPermissionSet(String permission)
        {
        	Boolean res = hasPermissionOnServerInWorld(permission);
        	if (res != null)
        		return true;
        	return oldPermissible.isPermissionSet(permission);
        }

        @Override
        public boolean isPermissionSet(Permission permission)
        {
        	Boolean res = hasPermissionOnServerInWorld(permission.getName());
        	if (res != null)
        		return true;
        	return oldPermissible.isPermissionSet(permission);
        }

        @Override
        public PermissionAttachment addAttachment(Plugin plugin)
        {
            return oldPermissible.addAttachment(plugin);
        }

        @Override
        public PermissionAttachment addAttachment(Plugin plugin, int ticks)
        {
            return oldPermissible.addAttachment(plugin, ticks);
        }

        @Override
        public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value)
        {
            return oldPermissible.addAttachment(plugin, name, value);
        }

        @Override
        public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks)
        {
            return oldPermissible.addAttachment(plugin, name, value, ticks);
        }

        @Override
        public void removeAttachment(PermissionAttachment attachment)
        {
            oldPermissible.removeAttachment(attachment);
        }

        @Override
        public synchronized void clearPermissions()
        {
            if (oldPermissible instanceof PermissibleBase)
                ((PermissibleBase) oldPermissible).clearPermissions();
        }
    }
}
