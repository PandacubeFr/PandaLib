package fr.pandacube.lib.paper.reflect;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
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
import fr.pandacube.lib.core.util.Reflect.ReflectMember;
import fr.pandacube.lib.core.util.Reflect.ReflectMethod;
import net.fabricmc.mappingio.MappingReader;
import net.fabricmc.mappingio.format.MappingFormat;
import net.fabricmc.mappingio.tree.MappingTree;
import net.fabricmc.mappingio.tree.MemoryMappingTree;

public class NMSReflect {
	

	private static String OBF_NAMESPACE;
	private static String MOJ_NAMESPACE;

	/* package */ static final Map<String, ClassMapping> CLASSES_BY_OBF = new TreeMap<>();
	/* package */ static final Map<String, ClassMapping> CLASSES_BY_MOJ = new TreeMap<>();
	
	private static Boolean IS_SERVER_OBFUSCATED;
	
	private static boolean isInit = false;
	
	public static void init() {
		
		synchronized (NMSReflect.class) {
			if (isInit)
				return;
			isInit = true;
		}
		
		Log.info("[NMSReflect] Initializing NMS obfuscation mapping...");
		
		try {
			ReflectClass<?> obfHelperClass;
			try {
				obfHelperClass = Reflect.ofClass("io.papermc.paper.util.ObfHelper");
	
				OBF_NAMESPACE = (String) obfHelperClass.field("SPIGOT_NAMESPACE").getStaticValue();
				MOJ_NAMESPACE = (String) obfHelperClass.field("MOJANG_PLUS_YARN_NAMESPACE").getStaticValue();
			} catch (ReflectiveOperationException e) {
				throw new ReflectiveOperationException("Unable to find the Paper ofbuscation mapping class or class members.", e);
			}
			
			List<ClassMapping> mappings = loadMappings(obfHelperClass);
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
					break;
				} catch (ClassNotFoundException e) {
					try {
						Class.forName(clazz.mojName);
						IS_SERVER_OBFUSCATED = false;
						break;
					} catch (ClassNotFoundException ee) {
						ee.addSuppressed(e);
						if (exIfUnableToDetermine == null)
							exIfUnableToDetermine = ee;
					}
				}
			}
			
			if (IS_SERVER_OBFUSCATED == null) {
				throw new IllegalStateException("Unable to determine if this server is obfuscated or not", exIfUnableToDetermine);
			}
			if (IS_SERVER_OBFUSCATED) {
				Log.info("[NMSReflect] NMS runtime classes are obfuscated.");
			}
			else {
				Log.info("[NMSReflect] NMS runtime classes are mojang mapped.");
			}
			
			int missingRuntimeClasses = 0;
			for (ClassMapping clazz : mappings) {
				try {
					clazz.cacheReflectClass();
				} catch (Throwable e) {
					missingRuntimeClasses++;
					if (e instanceof ClassNotFoundException cnfe) {
						Log.warning("[NMSReflect] Missing runtime class " + cnfe.getMessage() + (IS_SERVER_OBFUSCATED ? (" (moj class: " + clazz.mojName + ")") : ""));
					}
					else {
						Log.warning("[NMSReflect] Unable to load runtime class " + (IS_SERVER_OBFUSCATED ? (clazz.obfName + " (moj class: " + clazz.mojName + ")") : clazz.mojName));
						Log.warning(e); // throwable on separate log message due to sometimes the message not showing at all because of this exception
					}
					CLASSES_BY_OBF.remove(clazz.obfName);
					CLASSES_BY_MOJ.remove(clazz.mojName);
				}
			}
			
			if (missingRuntimeClasses > 0) {
				Log.warning("[NMSReflect] " + missingRuntimeClasses + " class have been removed from the mapping data due to the previously stated errors.");
			}
			
		} catch (Throwable t) {
			CLASSES_BY_OBF.clear();
			CLASSES_BY_MOJ.clear();
			Log.severe("[NMSReflect] The plugin will not have access to NMS stuff due to an error while loading the obfuscation mapping.", t);
		}
		Log.info("[NMSReflect] Obfuscation mapping loaded for " + CLASSES_BY_OBF.size() + " classes.");
	}
	
	
	/**
	 * @param mojName the binary name of the desired class, on the mojang mapping.
	 * @throws NullPointerException if there is no mapping for the provided Mojang mapped class.
	 */
	public static ClassMapping mojClass(String mojName) {
		return Objects.requireNonNull(CLASSES_BY_MOJ.get(mojName), "Unable to find the Mojang mapped class '" + mojName + "'");
	}
	
	
	
	
	
	
	
	
	
	
    private static List<ClassMapping> loadMappings(ReflectClass<?> obfHelperClass) throws IOException {
        try (final InputStream mappingsInputStream = obfHelperClass.get().getClassLoader().getResourceAsStream("META-INF/mappings/reobf.tiny")) {
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
						font-size: 14px;
						font-family: Consolas, monospace;
					}
					a:not(.cl) {
						color: #1290C3;
					}
					table {
						border-collapse: collapse;
						width: 100%;
						margin: auto;
					}
					tr:nth-child(2n) {
						background-color: #373737;
					}
					tr:hover {
						background-color: #555;
					}
					tr > *:first-child {
						padding-right: .5em;
						white-space: nowrap;
					}
					
					b.pu {
						color: #0C0;
					}
					b.pt {
						color: #FC0;
					}
					b.pv {
						color: #F00;
					}
					b.pk {
						color: #66F;
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
					.st {
						font-style: italic;
					}
					.st.fn {
						font-weight: bold;
					}
				</style>
				</head><body>
				"""
				+ "<h1>" + title + "</h1>\n"
				+ """
				<p>
					<b>C</b>: <span class='kw'>class</span>&nbsp;&nbsp;
					<b>E</b>: <span class='kw'>enum</span>&nbsp;&nbsp;
					<b>I</b>: <span class='kw'>interface</span>&nbsp;&nbsp;
					<b>@</b>: <span class='kw'>@interface</span>&nbsp;&nbsp;
					<b>R</b>: <span class='kw'>record</span><br>
					<b>●</b>: field&nbsp;&nbsp;
					<b>c</b>: constructor&nbsp;&nbsp;
					<b>⬤</b>: method<br>
					<b class='pu'>⬤</b>: <span class='kw'>public</span>&nbsp;&nbsp;
					<b class='pt'>⬤</b>: <span class='kw'>protected</span>&nbsp;&nbsp;
					<b class='pk'>⬤</b>: package&nbsp;&nbsp;
					<b class='pv'>⬤</b>: <span class='kw'>private</span><br>
					<sup>S</sup>: <span class='kw'>static</span>&nbsp;&nbsp;
					<sup>A</sup>: <span class='kw'>abstract</span>&nbsp;&nbsp;
					<sup>F</sup>: <span class='kw'>final</span>
				</p>
				<table>
				""");
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

		private final Map<MethodId, MemberMapping<MethodId, ReflectMethod<?>>> methodsByObf = new TreeMap<>();
		private final Map<MethodId, MemberMapping<MethodId, ReflectMethod<?>>> methodsByMoj = new TreeMap<>();
		private final Map<String, MemberMapping<String, ReflectField<?>>> fieldsByObf = new TreeMap<>();
		private final Map<String, MemberMapping<String, ReflectField<?>>> fieldsByMoj = new TreeMap<>();
		
		private ReflectClass<?> runtimeReflectClass = null;
		
		private ClassMapping(MappingTree.ClassMapping cls) {
            obfName = binaryClassName(cls.getName(OBF_NAMESPACE));
            mojName = binaryClassName(cls.getName(MOJ_NAMESPACE));
			
			cls.getMethods().stream().map(MemberMapping::of).forEach(method -> {
				method.declaringClass = this;
				methodsByObf.put(method.obfDesc.identifier, method);
				methodsByMoj.put(method.mojDesc.identifier, method);
			});
            cls.getFields().stream().map(MemberMapping::of).forEach(field -> {
            	field.declaringClass = this;
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
		 * @throws IllegalArgumentException if one of the parameter has an invalid type
		 * @throws NullPointerException if one of the parameter is null, or if there is no mapping for the provided Mojang mapped method.
		 * @throws ClassNotFoundException if there is no runtime class to represent one of the provided parametersType.
		 * @throws NoSuchMethodException if there is no runtime method to represent the provided method.
		 */
		public ReflectMethod<?> mojMethod(String mojName, Object... mojParametersType) throws ClassNotFoundException, NoSuchMethodException {
			MethodId mId = new MethodId(mojName, Type.toTypeList(Arrays.asList(mojParametersType)));
			MemberMapping<MethodId, ReflectMethod<?>> mm = methodsByMoj.get(mId);
			Objects.requireNonNull(mm, "Unable to find the Mojang mapped method " + mId);
			
			try {
				return mm.getReflectMember();
			} catch (ReflectiveOperationException e) {
				if (e instanceof ClassNotFoundException cnfe)
					throw cnfe;
				if (e instanceof NoSuchMethodException nsme)
					throw nsme;
				// should not have another exception
				throw new RuntimeException(e);
			}
		}
		
		
		

		/**
		 * 
		 * @param mojName the Mojang mapped name of the field.
		 * @throws NullPointerException if there is no mapping for the provided Mojang mapped field.
		 * @throws NoSuchFieldException if there is no runtime method to represent the provided method.
		 */
		public ReflectField<?> mojField(String mojName) throws NoSuchFieldException {
			MemberMapping<String, ReflectField<?>> fm = fieldsByMoj.get(mojName);
			Objects.requireNonNull(fm, "Unable to find the Mojang mapped field '" + mojName + "'");
			try {
				return fm.getReflectMember();
			} catch (ReflectiveOperationException e) {
				if (e instanceof NoSuchFieldException nsfe)
					throw nsfe;
				// should not have another exception
				throw new RuntimeException(e);
			}
		}
		
		
		

		
		/* package */ String toClickableHTML(boolean isObfClass) {
	    	String classToPrint = isObfClass ? obfName : mojName;
	    	String classSimpleName = classToPrint.substring(classToPrint.lastIndexOf('.') + 1);
	    	String htmlTitle = classSimpleName.equals(classToPrint) ? "" : (" title='" + classToPrint + "'");
			return "<a href='#c" + id + "'" + htmlTitle + " class='cl'>" + classSimpleName + "</a>";
		}
		
		
		
		
		/* package */ Type toType(boolean obf) {
			return new Type(obf ? obfName : mojName, 0);
		}


		private void printHTML(PrintStream out) {
			String modifiersHTML = classModifiersToHTML(runtimeClass());
			out.println("<tr id='c" + id + "'><th>" + modifiersHTML + "</th><th>" + nameToHTML(true) + "</th><th>" + nameToHTML(false) + "</th></tr>");
			fieldsByObf.values().stream().filter(mm -> mm.isStatic()).forEach(f -> f.printHTML(out));
			methodsByObf.values().stream().filter(mm -> mm.isStatic()).forEach(m -> m.printHTML(out));
			printConstructorsHTML(out);
			fieldsByObf.values().stream().filter(mm -> !mm.isStatic()).forEach(f -> f.printHTML(out));
			methodsByObf.values().stream().filter(mm -> !mm.isStatic()).forEach(m -> m.printHTML(out));
		}
		
		private String nameToHTML(boolean obf) {
			String classToPrint = obf ? obfName : mojName;
			int packageSep = classToPrint.lastIndexOf('.');
	    	String classSimpleName = classToPrint.substring(packageSep + 1);
	    	String classPackages = classToPrint.substring(0, Math.max(packageSep, 0));
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
		
	    
	    private void printConstructorsHTML(PrintStream out) {
	    	String classObfSimpleName = obfName.substring(obfName.lastIndexOf('.') + 1);
	    	String classMojSimpleName = mojName.substring(mojName.lastIndexOf('.') + 1);
			for (Constructor<?> ct : runtimeClass().getDeclaredConstructors()) {
				List<Type> obfParams = new ArrayList<>();
				List<Type> mojParams = new ArrayList<>();
				for (Class<?> param : ct.getParameterTypes()) {
					ClassMapping cm = (IS_SERVER_OBFUSCATED ? CLASSES_BY_OBF : CLASSES_BY_MOJ).get(param.getName());
					if (cm == null) {
						Type t = Type.of(param);
						obfParams.add(t);
						mojParams.add(t);
					}
					else {
						obfParams.add(cm.toType(true));
						mojParams.add(cm.toType(false));
					}
				}
				out.println("<tr>"
						+ "<td>" + elementModifiersToHTML("c", ct.getModifiers()) + "</td>"
						+ "<td><b class='mtd'>" + classObfSimpleName + "</b>(" + obfParams.stream().map(t -> t.toHTML(true)).collect(Collectors.joining(", ")) + ")</td>"
						+ "<td><b class='mtd'>" + classMojSimpleName + "</b>(" + mojParams.stream().map(t -> t.toHTML(false)).collect(Collectors.joining(", ")) + ")</td>"
						+ "</tr>");
			}
	    	
	    }
    }
    
    
    
    
    
    
    
    
    private record MethodId(String name, List<Type> parametersType) implements Comparable<MethodId> {
    	@Override
    	public int compareTo(MethodId o) {
    		int cmp = name.compareTo(o.name);
    		if (cmp != 0)
    			return cmp;
    		return toString().compareTo(o.toString());
    	}
    	
		private String toHTML(boolean isObfClass, boolean isStatic, boolean isFinal) {
			String paramsHTML = parametersType.stream().map(p -> p.toHTML(isObfClass)).collect(Collectors.joining(", "));
			String cl = "mtd";
			if (isStatic)
				cl += " st";
			if (isFinal)
				cl += " fn";
			return "<span class='" + cl + "'>" + name + "</span>(" + paramsHTML + ")";
		}
		
		public String toString() {
			String paramsStr = parametersType.stream().map(Type::toString).collect(Collectors.joining(", "));
			return name + "(" + paramsStr + ")";
		}
    	
    }


    
    private record MemberDesc<I extends Comparable<I>>(I identifier, Type returnType) {
		private String toHTML(boolean isObfClass, boolean isStatic, boolean isFinal) {
			String identifierHTML = "";
			if (identifier instanceof MethodId mId)
				identifierHTML = mId.toHTML(isObfClass, isStatic, isFinal);
			else if (identifier instanceof String n) {
				String cl = "fld";
				if (isStatic)
					cl += " st";
				if (isFinal)
					cl += " fn";
				identifierHTML = "<span class='" + cl + "'>" + n + "</span>";
			}
			return returnType.toHTML(isObfClass) + " " + identifierHTML;
		}
		
		private static MemberDesc<MethodId> of(MappingTree.MethodMapping member, String namespace) {
			String desc = member.getDesc(namespace);
			try (StringReader descReader = new StringReader(desc)) {
				char r = (char) descReader.read();
				if (r != '(')
					throw new IllegalArgumentException("Invalid method description '" + desc + "'. Must start with '('.");
				
				List<Type> paramsType = new ArrayList<>();
				
				while (((char) descReader.read()) != ')') {
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
    
    
    
    
    private static abstract class MemberMapping<I extends Comparable<I>, R extends ReflectMember<?, ?, ?, ?>> {
    	private final String htmlTypeChar;
    	/* package */ final MemberDesc<I> obfDesc, mojDesc;
    	/* package */ ClassMapping declaringClass;
    	private MemberMapping(String htmlType, MemberDesc<I> obfDesc, MemberDesc<I> mojDesc) {
    		htmlTypeChar = htmlType;
    		this.obfDesc = obfDesc;
    		this.mojDesc = mojDesc;
		}
    	
		/* package */ void printHTML(PrintStream out) {
			int mod = 0;
			try {
				mod = getReflectMember().getModifiers();
			} catch (ReflectiveOperationException e) {
				// ignore
			}
			boolean isStatic = Modifier.isStatic(mod);
			boolean isFinal = Modifier.isFinal(mod);
			out.println("<tr>"
					+ "<td>" + elementModifiersToHTML(htmlTypeChar, mod) + "</td>"
					+ "<td>" + obfDesc.toHTML(true, isStatic, isFinal) + "</td>"
					+ "<td>" + mojDesc.toHTML(false, isStatic, isFinal) + "</td>"
					+ "</tr>");
		}
		
		/* package */ MemberDesc<I> getReflectDesc() {
			return (IS_SERVER_OBFUSCATED ? obfDesc : mojDesc);
		}
		
		/* package */ abstract R getReflectMember() throws ReflectiveOperationException;
		
		/* package */ boolean isStatic() {
			try {
				return Modifier.isStatic(getReflectMember().getModifiers());
			} catch (ReflectiveOperationException e) {
				Log.severe(e);
				return false;
			}
		}
		
		private static MemberMapping<MethodId, ReflectMethod<?>> of(MappingTree.MethodMapping mioMapping) {
    		return new MemberMapping<>("⬤", MemberDesc.of(mioMapping, OBF_NAMESPACE), MemberDesc.of(mioMapping, MOJ_NAMESPACE)) {
				@Override
				ReflectMethod<?> getReflectMember() throws ClassNotFoundException, NoSuchMethodException {
					MethodId id = getReflectDesc().identifier;
					return declaringClass.runtimeReflectClass.method(id.name, Type.toClassArray(id.parametersType));
				}
    		};
		}
		
		private static MemberMapping<String, ReflectField<?>> of(MappingTree.FieldMapping mioMapping) {
    		return new MemberMapping<>("●", MemberDesc.of(mioMapping, OBF_NAMESPACE), MemberDesc.of(mioMapping, MOJ_NAMESPACE)) {
				@Override
				ReflectField<?> getReflectMember() throws NoSuchFieldException {
					String id = getReflectDesc().identifier;
					return declaringClass.runtimeReflectClass.field(id);
				}
    		};
		}
		
		
    	
    }
    
    
    
    
    
    /* package */ static String binaryClassName(String cl) {
    	return cl.replace('/', '.');
    }
    
	
	
	private static String classModifiersToHTML(Class<?> clazz) {
		String elementHTMLType;

		if (clazz.isEnum())
			elementHTMLType = "E";
		else if (clazz.isAnnotation())
			elementHTMLType = "@";
		else if (clazz.isInterface())
			elementHTMLType = "I";
		else if (clazz.isRecord())
			elementHTMLType = "R";
		else if (clazz.isPrimitive())
			elementHTMLType = "";
		else 
			elementHTMLType = "C";
		
		return elementModifiersToHTML(elementHTMLType, clazz.getModifiers());
	}
	

	
	private static String elementModifiersToHTML(String elementHTMLType, int elModifiers) {
		String html = "<b class='";
		
		if (Modifier.isPublic(elModifiers))
			html += "pu";
		else if (Modifier.isProtected(elModifiers))
			html += "pt";
		else if (Modifier.isPrivate(elModifiers))
			html += "pv";
		else
			html += "pk";
		
		html += "'>" + elementHTMLType + "</b>";

		boolean isStatic = Modifier.isStatic(elModifiers);
		boolean isAbstract = Modifier.isAbstract(elModifiers);
		boolean isFinal = Modifier.isFinal(elModifiers);

		if (isStatic || isAbstract || isFinal) {
			html += "<sup>";
			if (isStatic)
				html += "S";
			if (isAbstract)
				html += "A";
			if (isFinal)
				html += "F";
			html += "</sup>";
		}
		
		return html;
	}
    
    // ● (field)
	// ⬤ (method)
    
}
