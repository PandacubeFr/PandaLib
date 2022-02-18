package fr.pandacube.lib.paper.reflect;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;

import fr.pandacube.lib.core.util.Log;
import fr.pandacube.lib.core.util.Reflect;
import fr.pandacube.lib.core.util.Reflect.ReflectClass;
import fr.pandacube.lib.core.util.Reflect.ReflectField;
import fr.pandacube.lib.core.util.Reflect.ReflectMethod;
import net.fabricmc.mappingio.MappingReader;
import net.fabricmc.mappingio.format.MappingFormat;
import net.fabricmc.mappingio.tree.MappingTree;
import net.fabricmc.mappingio.tree.MemoryMappingTree;

public class NMSReflect {
	
	private static ReflectClass<?> PAPER_OBFHELPER_CLASS;

	private static String OBF_NAMESPACE;
	private static String MOJ_NAMESPACE;

	/* package */ static final Map<String, ClassMapping> CLASSES_BY_OBF = new TreeMap<>();
	/* package */ static final Map<String, ClassMapping> CLASSES_BY_MOJ = new TreeMap<>();
	
	private static Boolean IS_SERVER_OBFUSCATED;
	
	
	static {
		try {
			PAPER_OBFHELPER_CLASS = Reflect.ofClass("io.papermc.paper.util.ObfHelper");

			OBF_NAMESPACE = (String) PAPER_OBFHELPER_CLASS.field("SPIGOT_NAMESPACE").getStaticValue();
			MOJ_NAMESPACE = (String) PAPER_OBFHELPER_CLASS.field("MOJANG_PLUS_YARN_NAMESPACE").getStaticValue();
		} catch (ReflectiveOperationException e) {
        	throw new RuntimeException("Unable to find the Paper ofbuscation mapping class or class members.", e);
		}
		
		try {
			
			List<ClassMapping> mappings = loadMappings();
			for (ClassMapping clazz : mappings) {
				CLASSES_BY_OBF.put(clazz.obfName, clazz);
				CLASSES_BY_MOJ.put(clazz.mojName, clazz);
			}
			
			// determine if the runtime server is obfuscated
			ClassNotFoundException exIfUnableToDetermine = null;
			for (ClassMapping clazz : CLASSES_BY_OBF.values()) {
				if (clazz.obfName.equals(clazz.mojName) // avoid direct collision between obf and unobf class names
						|| CLASSES_BY_MOJ.containsKey(clazz.obfName) // avoid indirect collision
						|| CLASSES_BY_OBF.containsKey(clazz.mojName))// avoid indirect collision
					continue;
				
				try {
					Class.forName(clazz.obfName);
					IS_SERVER_OBFUSCATED = true;
					Log.info("NMS classes are obfuscated.");
					break;
				} catch (ClassNotFoundException e) {
					try {
						Class.forName(clazz.mojName);
						IS_SERVER_OBFUSCATED = false;
						Log.info("NMS classes are using mojang mapping.");
						break;
					} catch (ClassNotFoundException ee) {
						ee.addSuppressed(e);
						if (exIfUnableToDetermine == null)
							exIfUnableToDetermine = ee;
					}
				}
			}
			
			if (IS_SERVER_OBFUSCATED == null) {
				CLASSES_BY_MOJ.clear();
				CLASSES_BY_OBF.clear();
				throw exIfUnableToDetermine;
			}
			
			
			for (ClassMapping clazz : mappings) {
				clazz.cacheReflectClass();
			}
		} catch (ReflectiveOperationException e) {
			Log.severe(e);
		}
	}
	
	
	/**
	 * @param mojName the binary name of the desired class, on the mojang mapping.
	 * @throws NullPointerException if there is no mapping for the provided Mojang mapped class.
	 * @throws ClassNotFoundException if there is a mapping, but the runtime class was not found.
	 */
	public static ClassMapping mojClass(String mojName) throws ClassNotFoundException {
		ClassMapping cm = Objects.requireNonNull(CLASSES_BY_MOJ.get(mojName), "Unable to find the Mojang mapped class '" + mojName + "'");
		cm.cacheReflectClass();
		return cm;
	}
	
	
	
	
	
	
	
	
	
	
    private static List<ClassMapping> loadMappings() {
        try (final InputStream mappingsInputStream = PAPER_OBFHELPER_CLASS.get().getClassLoader().getResourceAsStream("META-INF/mappings/reobf.tiny")) {
            if (mappingsInputStream == null) {
            	throw new RuntimeException("Unable to find the ofbuscation mapping file in the Paper jar.");
            }
            
            MemoryMappingTree tree = new MemoryMappingTree();
            MappingReader.read(new InputStreamReader(mappingsInputStream, StandardCharsets.UTF_8), MappingFormat.TINY_2, tree);
            
            List<ClassMapping> classes = new ArrayList<>();
            for (MappingTree.ClassMapping cls : tree.getClasses()) {
                classes.add(new ClassMapping(cls));
            }
            return classes;
        } catch (IOException e) {
        	Log.severe("Failed to load mappings.", e);
            return Collections.emptyList();
        }
    }
	
	
	
	public static void printHTMLMapping(PrintStream out) {
		String title = "Obfuscation mapping - " + Bukkit.getName() + " version " + Bukkit.getVersion();
		out.println("<!DOCTYPE html><html><head>\n"
				+ "<title>" + title + "</title>\n"
				+ """
				<style>
					html {
						background-color: #2F2F2F;
						color: white;
					}
					table {
						border-collapse: collapse;
						width: 100%;
						margin: auto;
						font-family: monospace;
					}
					tr:nth-child(2n) {
						background-color: #373737;
					}
					tr:hover {
						background-color: #555;
					}
					tr > *:first-child {
						text-align: right;
						padding-right: .5em;
					}
					td {
						padding-top: 0;
						padding-bottom: 0;
					}
					th {
						text-align: left;
						font-size: 1.1em;
						border-top: solid 1px white;
					}
					a, a:visited {
						color: #ddd;
					}
					.kw {
						color: #CC6C1D;
					}
					.cl {
						color: #1290C3;
					}
					.mtd {
						color: #1EB540;
					}
					.fld {
						color: #8DDAF8;
					}
				</style>
				"""
				+ "</head><body>\n"
				+ "<h1>" + title + "</h1>\n"
				+ "<table>");
		out.println("<tr><th>ns</th><th>" + OBF_NAMESPACE + "</th><th>" + MOJ_NAMESPACE + "</th></tr>");
		for (ClassMapping clazz : CLASSES_BY_OBF.values()) {
			clazz.printHTML(out);
		}
		out.println("</table><p>Generated by <a href='https://github.com/marcbal'>marcbal</a>"
				+ " using <a href='https://github.com/PandacubeFr/PandaLib/blob/master/Paper/src/main/java/fr/pandacube/lib/paper/reflect/NMSReflect.java'>this tool</a>"
				+ " running on <a href='https://papermc.io/'>" + Bukkit.getName() + "</a> version " + Bukkit.getVersion() + "</p>"
				+ "</body></html>");
	}
	
	
	
	
	
    public static class ClassMapping {
    	private static int nextID = 0;
    	
    	/* package */ final int id = nextID++;
		/* package */ final String obfName;
		/* package */ final String mojName;

		private final Map<MethodId, MemberMapping<MethodId>> methodsByObf = new TreeMap<>();
		private final Map<MethodId, MemberMapping<MethodId>> methodsByMoj = new TreeMap<>();
		private final Map<String, MemberMapping<String>> fieldsByObf = new TreeMap<>();
		private final Map<String, MemberMapping<String>> fieldsByMoj = new TreeMap<>();
		
		private ReflectClass<?> runtimeReflectClass = null;
		
		private ClassMapping(MappingTree.ClassMapping cls) {
            obfName = binaryClassName(cls.getName(OBF_NAMESPACE));
            mojName = binaryClassName(cls.getName(MOJ_NAMESPACE));
			
			cls.getMethods().stream().map(MemberMapping::of).forEach(method -> {
				methodsByObf.put(method.obfDesc.identifier, method);
				methodsByMoj.put(method.mojDesc.identifier, method);
			});
            cls.getFields().stream().map(MemberMapping::of).forEach(field -> {
				fieldsByObf.put(field.obfDesc.identifier, field);
				fieldsByMoj.put(field.mojDesc.identifier, field);
            });
			
		}
		

		
		private synchronized void cacheReflectClass() throws ClassNotFoundException {
			if (runtimeReflectClass == null)
				runtimeReflectClass = Reflect.ofClass(IS_SERVER_OBFUSCATED ? obfName : mojName);
		}
		
		
		
		
		public ReflectClass<?> runtimeReflect() {
			return runtimeReflectClass;
		}
		
		public Class<?> runtimeClass() {
			return runtimeReflectClass.get();
		}
		
		
		
		
		/**
		 * 
		 * @param mojName the Mojang mapped name of the method.
		 * @param mojParametersType the list of parameters of the method.
		 * Each parameter type must be an instance of one of the following type:
		 * {@link Type}, {@link Class}, {@link ReflectClass} or {@link ClassMapping}.
		 * @return
		 * @throws IllegalArgumentException if one of the parameter has an invalid type
		 * @throws NullPointerException if one of the parameter is null, or if there is no mapping for the provided Mojang mapped method.
		 * @throws ClassNotFoundException if there is no runtime class to represent one of the provided parametersType.
		 * @throws NoSuchMethodException if there is no runtime method to represent the provided method.
		 */
		public ReflectMethod<?> mojMethod(String mojName, Object... mojParametersType) throws ClassNotFoundException, NoSuchMethodException {
			MethodId mId = new MethodId(mojName, Type.toTypeList(Arrays.asList(mojParametersType)));
			MemberMapping<MethodId> mm = methodsByMoj.get(mId);
			Objects.requireNonNull(mm, "Unable to find the Mojang mapped method " + mId);
			
			MethodId reflectId = (IS_SERVER_OBFUSCATED ? mm.obfDesc : mm.mojDesc).identifier;
			return runtimeReflectClass.method(reflectId.name, Type.toClassArray(reflectId.parametersType));
		}
		
		
		

		/**
		 * 
		 * @param mojName the Mojang mapped name of the field.
		 * @return
		 * @throws NullPointerException if there is no mapping for the provided Mojang mapped field.
		 * @throws NoSuchFieldException if there is no runtime method to represent the provided method.
		 */
		public ReflectField<?> mojField(String mojName) throws NoSuchFieldException {
			MemberMapping<String> fm = fieldsByMoj.get(mojName);
			Objects.requireNonNull(fm, "Unable to find the Mojang mapped field '" + mojName + "'");
			return runtimeReflectClass.field((IS_SERVER_OBFUSCATED ? fm.obfDesc : fm.mojDesc).identifier);
		}
		
		
		

		
		/* package */ String toClickableHTML(boolean isObfClass) {
	    	String classToPrint = isObfClass ? obfName : mojName;
	    	String classSimpleName = classToPrint.substring(classToPrint.lastIndexOf('.') + 1);
	    	String htmlTitle = classSimpleName.equals(classToPrint) ? "" : (" title='" + classToPrint + "'");
	    	String typeHTML = "<a href='#c" + id + "'" + htmlTitle + " class='cl'>" + classSimpleName + "</a>";
			
			return typeHTML;
		}
		
		
		
		
		/* package */ Type toType(boolean obf) {
			return new Type(obf ? obfName : mojName, 0);
		}


		private void printHTML(PrintStream out) {
			out.println("<tr id='c" + id + "'><th class='kw'>" + classKind() + "</th><th>" + nameToHTML(true) + "</th><th>" + nameToHTML(false) + "</th></tr>");
			fieldsByObf.values().forEach(f -> f.printHTML(out));
			methodsByObf.values().forEach(m -> m.printHTML(out));
		}
		
		private String nameToHTML(boolean obf) {
			String classToPrint = obf ? obfName : mojName;
			int packageSep = classToPrint.lastIndexOf('.');
	    	String classSimpleName = classToPrint.substring(packageSep + 1);
	    	String classPackages = classToPrint.substring(0, packageSep > 0 ? packageSep : 0);
	    	String classHTML = (packageSep >= 0 ? (classPackages + ".") : "") + "<b class='cl'>" + classSimpleName + "</b>";
			
			Type superClass = superClass(obf);
			String superClassHTML = superClass == null ? "" : (" <span class='kw'>extends</span> " + superClass.toHTML(obf));
			
			List<Type> superInterfaces = superInterfaces(obf);
			String superInterfacesHTML = superInterfaces.isEmpty() ? ""
					: (" <span class='kw'>implements</span> " + superInterfaces.stream().map(t -> t.toHTML(obf)).collect(Collectors.joining(", ")));
			
			return classHTML + superClassHTML + superInterfacesHTML;
		}
		
		private Type superClass(boolean obf) {
			Class<?> superClass = runtimeClass().getSuperclass();
			if (superClass == null || superClass.equals(Object.class) || superClass.equals(Enum.class) || superClass.equals(Record.class))
				return null;
			ClassMapping cm = (IS_SERVER_OBFUSCATED ? CLASSES_BY_OBF : CLASSES_BY_MOJ).get(superClass.getName());
			return (cm != null) ? cm.toType(obf) : Type.of(superClass);
		}
		
		private List<Type> superInterfaces(boolean obf) {
			Class<?>[] interfaces = runtimeClass().getInterfaces();
			List<Type> types = new ArrayList<>(interfaces.length);
			for (Class<?> interfce : interfaces) {
				ClassMapping cm = (IS_SERVER_OBFUSCATED ? CLASSES_BY_OBF : CLASSES_BY_MOJ).get(interfce.getName());
				types.add((cm != null) ? cm.toType(obf) : Type.of(interfce));
			}
			return types;
		}
		
		private String classKind() {
			Class<?> clazz = runtimeClass();
			if (clazz.isEnum())
				return "enum";
			if (clazz.isAnnotation())
				return "annotation";
			if (clazz.isInterface())
				return "interface";
			if (clazz.isRecord())
				return "record";
			if (clazz.isPrimitive())
				return "primitive";
			return "Class";
		}
    }
    
    
    
    
    
    
    
    
    private static record MethodId(String name, List<Type> parametersType) implements Comparable<MethodId> {
    	@Override
    	public int compareTo(MethodId o) {
    		int cmp = name.compareTo(o.name);
    		if (cmp != 0)
    			return cmp;
    		return toString().compareTo(o.toString());
    	}
    	
		private String toHTML(boolean isObfClass) {
				String paramsHTML = parametersType.stream().map(p -> p.toHTML(isObfClass)).collect(Collectors.joining(", "));
				String identifierHTML = "<b class='mtd'>" + name + "</b>(" + paramsHTML + ")";
			return identifierHTML;
		}
		
		public String toString() {
			String paramsStr = parametersType.stream().map(Type::toString).collect(Collectors.joining(", "));
			return name + "(" + paramsStr + ")";
		}
    	
    }


    
    private static record MemberDesc<I extends Comparable<I>>(I identifier, Type returnType) {
		private String toHTML(boolean isObfClass) {
			String identifierHTML = "";
			if (identifier instanceof MethodId mId)
				identifierHTML = mId.toHTML(isObfClass);
			else if (identifier instanceof String n)
				identifierHTML = "<b class='fld'>" + n + "</b>";
			return returnType.toHTML(isObfClass) + " " + identifierHTML;
		}
		
		private static MemberDesc<MethodId> of(MappingTree.MethodMapping member, String namespace) {
			String desc = member.getDesc(namespace);
			try (StringReader descReader = new StringReader(desc)) {
				char r = (char) descReader.read();
				if (r != '(')
					throw new IllegalArgumentException("Invalid method description '" + desc + "'. Must start with '('.");
				
				List<Type> paramsType = new ArrayList<>();
				
				while ((r = (char) descReader.read()) != ')') {
					descReader.skip(-1);
					paramsType.add(Type.parse(descReader));
				}
				
				Type retType = Type.parse(descReader);
				return new MemberDesc<>(new MethodId(member.getName(namespace), Collections.unmodifiableList(paramsType)), retType);
			} catch (IOException e) {
				throw new RuntimeException("StringReader read error", e);
			}
			
		}

		
		private static MemberDesc<String> of(MappingTree.FieldMapping member, String namespace) {
			StringReader descReader = new StringReader(member.getDesc(namespace));
			return new MemberDesc<>(member.getName(namespace), Type.parse(descReader));
		}
    }
    
    
    
    
    private static class MemberMapping<I extends Comparable<I>> {
    	private String type;
    	/* package */ MemberDesc<I> obfDesc, mojDesc;
    	private MemberMapping(String type, MemberDesc<I> obfDesc, MemberDesc<I> mojDesc) {
    		this.type = type;
    		this.obfDesc = obfDesc;
    		this.mojDesc = mojDesc;
		}
    	
		/* package */ void printHTML(PrintStream out) {
			out.println("<tr><td>" + type + "</td><td>" + obfDesc.toHTML(true) + "</td><td>" + mojDesc.toHTML(false) + "</td></tr>");
		}
		
		private static MemberMapping<MethodId> of(MappingTree.MethodMapping mioMapping) {
    		return new MemberMapping<>("Method", MemberDesc.of(mioMapping, OBF_NAMESPACE), MemberDesc.of(mioMapping, MOJ_NAMESPACE));
		}
		
		private static MemberMapping<String> of(MappingTree.FieldMapping mioMapping) {
    		return new MemberMapping<>("Field", MemberDesc.of(mioMapping, OBF_NAMESPACE), MemberDesc.of(mioMapping, MOJ_NAMESPACE));
		}
    	
    }
    
    
    
    
    
    
    
    /* package */ static String binaryClassName(String cl) {
    	return cl.replace('/', '.');
    }
    
    
    
    
}
