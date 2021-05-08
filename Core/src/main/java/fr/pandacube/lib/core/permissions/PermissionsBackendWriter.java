package fr.pandacube.lib.core.permissions;

import java.util.Objects;

import com.google.common.base.Preconditions;

import fr.pandacube.lib.core.db.DB;
import fr.pandacube.lib.core.db.DBException;
import fr.pandacube.lib.core.permissions.SQLPermissions.EntityType;

/* package */ class PermissionsBackendWriter {
	
	
	/* package */ void addSelfPermission(String name, EntityType type, String permission, String server, String world) {
		Objects.requireNonNull(name, "name cannot be null");
		Objects.requireNonNull(type, "type cannot be null");
		Objects.requireNonNull(permission, "permission cannot be null");
		Preconditions.checkArgument(world == null || server != null, "world not null but server is null");
		name = name.toLowerCase();
		permission = permission.toLowerCase();
		if (server != null) server = server.toLowerCase();
		if (world != null) world = world.toLowerCase();
		
		if (hasEntry(name, type, "permissions", permission, server, world))
			throw new IllegalStateException("Permission already set");
		addEntry(name, type, "permissions", permission, server, world);
	}
	
	/* package */ void removeSelfPermission(String name, EntityType type, String permission, String server, String world) {
		Objects.requireNonNull(name, "name cannot be null");
		Objects.requireNonNull(type, "type cannot be null");
		Objects.requireNonNull(permission, "permission cannot be null");
		Preconditions.checkArgument(world == null || server != null, "world not null but server is null");
		name = name.toLowerCase();
		permission = permission.toLowerCase();
		if (server != null) server = server.toLowerCase();
		if (world != null) world = world.toLowerCase();
		
		if (!deleteEntry(name, type, "permissions", permission, server, world))
			throw new IllegalStateException("Permission was not set");
	}
	
	
	
	
	
	
	
	
	
	/* package */ void setGroupDefault(String name, boolean deflt) {
		Objects.requireNonNull(name, "name cannot be null");
		name = name.toLowerCase();
		try {
			SQLPermissions entry = DB.getFirst(SQLPermissions.class,
					SQLPermissions.name.like(name)
					.and(SQLPermissions.type.eq(EntityType.Group.getCode()))
					.and(SQLPermissions.key.like("default"))
					);
			if (entry == null && !deflt) {
				return;
			}
			else if (entry != null && deflt) {
				// update
				if ("true".equals(entry.get(SQLPermissions.value)))
					return;
				entry.set(SQLPermissions.value, "true");
				entry.save();
				return;
			}
			else if (entry != null && !deflt) {
				// delete
				entry.delete();
			}
			else {
				// insert
				addEntry(name, EntityType.Group, "default", "true", null, null);
			}
		} catch (DBException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
	
	
	
	/* package */ void setSelfPrefix(String name, EntityType type, String prefix) {
		Objects.requireNonNull(name, "name cannot be null");
		Objects.requireNonNull(type, "type cannot be null");
		name = name.toLowerCase();
		
		try {
			SQLPermissions entry = DB.getFirst(SQLPermissions.class,
					SQLPermissions.name.like(name)
					.and(SQLPermissions.type.eq(type.getCode()))
					.and(SQLPermissions.key.like("prefix"))
					);
			if (entry == null && prefix == null) {
				return;
			}
			else if (entry != null && prefix != null) {
				// update
				entry.set(SQLPermissions.value, prefix);
				entry.save();
				return;
			}
			else if (entry != null && prefix == null) {
				// delete
				entry.delete();
			}
			else {
				// insert
				addEntry(name, type, "prefix", prefix, null, null);
			}
		} catch (DBException e) {
			throw new RuntimeException(e);
		}
	}
	
	/* package */ void setSelfSuffix(String name, EntityType type, String suffix) {
		Objects.requireNonNull(name, "name cannot be null");
		Objects.requireNonNull(type, "type cannot be null");
		name = name.toLowerCase();
		
		try {
			SQLPermissions entry = DB.getFirst(SQLPermissions.class,
					SQLPermissions.name.like(name)
					.and(SQLPermissions.type.eq(type.getCode()))
					.and(SQLPermissions.key.like("suffix"))
					);
			if (entry == null && suffix == null) {
				return;
			}
			else if (entry != null && suffix != null) {
				// update
				entry.set(SQLPermissions.value, suffix);
				entry.save();
				return;
			}
			else if (entry != null && suffix == null) {
				// delete
				entry.delete();
			}
			else {
				// insert
				addEntry(name, type, "suffix", suffix, null, null);
			}
		} catch (DBException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	

	
	/* package */ void addInheritance(String name, EntityType type, String inheritance) {
		Objects.requireNonNull(name, "name cannot be null");
		Objects.requireNonNull(type, "type cannot be null");
		Objects.requireNonNull(inheritance, "inheritance cannot be null");
		name = name.toLowerCase();
		inheritance = inheritance.toLowerCase();
		String key = type == EntityType.Group ? "inheritances" : "groups";
		
		try {
			SQLPermissions entry = DB.getFirst(SQLPermissions.class,
					SQLPermissions.name.like(name)
					.and(SQLPermissions.type.eq(type.getCode()))
					.and(SQLPermissions.key.like(key))
					.and(SQLPermissions.value.like(inheritance))
					);
			if (entry != null)
				throw new IllegalStateException("Inheritance already set");
			addEntry(name, type, key, inheritance, null, null);
		} catch (DBException e) {
			new RuntimeException(e);
		}
		
	}
	
	/* package */ void removeInheritance(String name, EntityType type, String inheritance) {
		Objects.requireNonNull(name, "name cannot be null");
		Objects.requireNonNull(type, "type cannot be null");
		Objects.requireNonNull(inheritance, "inheritance cannot be null");
		name = name.toLowerCase();
		inheritance = inheritance.toLowerCase();
		String key = type == EntityType.Group ? "inheritances" : "groups";
		
		try {
			int deleted = DB.delete(SQLPermissions.class,
					SQLPermissions.name.like(name)
					.and(SQLPermissions.type.eq(type.getCode()))
					.and(SQLPermissions.key.like(key))
					.and(SQLPermissions.value.like(inheritance))
					);
			if (deleted == 0)
				throw new IllegalStateException("Inheritance was not set");
		} catch (DBException e) {
			throw new RuntimeException(e);
		}
	}
	
	/* package */ void setInheritance(String name, EntityType type, String inheritance) {
		Objects.requireNonNull(name, "name cannot be null");
		Objects.requireNonNull(type, "type cannot be null");
		Objects.requireNonNull(inheritance, "inheritance cannot be null");
		name = name.toLowerCase();
		inheritance = inheritance.toLowerCase();
		String key = type == EntityType.Group ? "inheritances" : "groups";
		
		try {
			DB.delete(SQLPermissions.class,
					SQLPermissions.name.like(name)
					.and(SQLPermissions.type.eq(type.getCode()))
					.and(SQLPermissions.key.like(key))
					);
			addEntry(name, type, key, inheritance, null, null);
		} catch (DBException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private boolean deleteEntry(String name, EntityType type,
			String key, String value,
			String server, String world) {
		try {
			return DB.delete(SQLPermissions.class,
					SQLPermissions.name.like(name)
					.and(SQLPermissions.type.eq(type.getCode()))
					.and(SQLPermissions.key.like(key))
					.and(SQLPermissions.value.like(value))
					.and(server == null ? SQLPermissions.server.isNull() : SQLPermissions.server.like(server))
					.and(world == null ? SQLPermissions.world.isNull() : SQLPermissions.world.like(world))
					) >= 1;
		} catch (DBException e) {
			throw new RuntimeException(e);
		}
	}
	
	private boolean hasEntry(String name, EntityType type,
			String key, String value,
			String server, String world) {
		return getEntry(name, type, key, value, server, world) != null;
	}
	
	private SQLPermissions getEntry(String name, EntityType type,
			String key, String value,
			String server, String world) {
		try {
			return DB.getFirst(SQLPermissions.class,
					SQLPermissions.name.like(name)
					.and(SQLPermissions.type.eq(type.getCode()))
					.and(SQLPermissions.key.like(key))
					.and(SQLPermissions.value.like(value))
					.and(server == null ? SQLPermissions.server.isNull() : SQLPermissions.server.like(server))
					.and(world == null ? SQLPermissions.world.isNull() : SQLPermissions.world.like(world))
					);
		} catch (DBException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void addEntry(String name, EntityType type,
			String key, String value,
			String server, String world) {
		SQLPermissions entry = new SQLPermissions()
				.set(SQLPermissions.name, name)
				.set(SQLPermissions.type, type.getCode())
				.set(SQLPermissions.key, key)
				.set(SQLPermissions.value, value)
				.set(SQLPermissions.server, server)
				.set(SQLPermissions.world, world);
		try {
			entry.save();
		} catch (DBException e) {
			throw new RuntimeException(e);
		}
	}
	
}