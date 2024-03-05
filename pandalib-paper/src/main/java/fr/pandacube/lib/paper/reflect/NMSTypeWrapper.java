package fr.pandacube.lib.paper.reflect;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.paper.reflect.NMSReflect.ClassMapping;

/* package */ class NMSTypeWrapper implements Comparable<NMSTypeWrapper> {
	private final String type;
	private final int arrayDepth;
	
	/* package */ NMSTypeWrapper(String type, int arrayDepth) {
		this.type = type;
		this.arrayDepth = arrayDepth;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof NMSTypeWrapper ot && type.equals(ot.type) && arrayDepth == ot.arrayDepth;
	}
	@Override
	public int hashCode() {
		return type.hashCode() ^ arrayDepth;
	}
	
	@Override
	public int compareTo(NMSTypeWrapper o) {
		return toString().compareTo(o.toString());
	}
	
	Class<?> toClass() throws ClassNotFoundException {
		
		Class<?> cl = switch(type) {
		case "boolean" -> boolean.class;
		case "byte" -> byte.class;
		case "char" -> char.class;
		case "double" -> double.class;
		case "float" -> float.class;
		case "int" -> int.class;
		case "long" -> long.class;
		case "short" -> short.class;
		case "void" -> void.class;
		default -> Class.forName(type);
		};
		
		for (int i = 0; i < arrayDepth; i++) {
			cl = cl.arrayType();
		}
		
		return cl;
	}
	
	/* package */ static NMSTypeWrapper of(Class<?> cl) {
		int arrayDepth = 0;
		while (cl.isArray()) {
			cl = cl.getComponentType();
			arrayDepth++;
		}
		return new NMSTypeWrapper(cl.getName(), arrayDepth);
	}
	
	public static NMSTypeWrapper of(ReflectClass<?> rc) {
		return arrayOf(rc, 0);
	}
	
	public static NMSTypeWrapper arrayOf(ReflectClass<?> rc, int arrayDepth) {
		return new NMSTypeWrapper(rc.get().getName(), arrayDepth);
	}
	
	public static NMSTypeWrapper mojOf(ClassMapping cm) {
		return arrayMojOf(cm, 0);
	}
	
	public static NMSTypeWrapper arrayMojOf(ClassMapping cm, int arrayDepth) {
		return new NMSTypeWrapper(cm.mojName, arrayDepth);
	}
	
	/* package */ static NMSTypeWrapper toType(Object typeObj) {
		Objects.requireNonNull(typeObj, "typeObj cannot be null");
		if (typeObj instanceof Class<?> cl) {
			return of(cl);
		}
		else if (typeObj instanceof ClassMapping cm) {
			return mojOf(cm);
		}
		else if (typeObj instanceof ReflectClass<?> rc) {
			return of(rc);
		}
		else if (typeObj instanceof NMSTypeWrapper t) {
			return t;
		}
		else
			throw new IllegalArgumentException("Unsupported object of type " + typeObj.getClass());
	}

	/* package */ String toHTML(boolean isObfClass) {
		ClassMapping clMapping = (isObfClass ? NMSReflect.CLASSES_BY_OBF : NMSReflect.CLASSES_BY_MOJ).get(type);
    	String typeHTML;
    	if (clMapping != null) {
    		typeHTML = clMapping.toClickableHTML(isObfClass);
    	}
    	else {
	    	String classToPrint = type;
	    	String classSimpleName = classToPrint.substring(classToPrint.lastIndexOf('.') + 1);
	    	String htmlTitle = classSimpleName.equals(classToPrint) ? "" : (" title='" + classToPrint + "'");
    		if (!htmlTitle.isEmpty()) {
    			typeHTML = "<span" + htmlTitle + " class='cl'>" + classSimpleName + "</span>";
        	}
        	else {
        		typeHTML = "<span class='" + (isPrimitive() ? "kw" : "cl") + "'>" + classSimpleName + "</span>";
        	}
    	}
    	
		
		return typeHTML + "[]".repeat(arrayDepth);
	}
	
	public String toString() {
		return type + "[]".repeat(arrayDepth);
	}
	
	
	public boolean isPrimitive() {
		try {
			return toClass().isPrimitive();
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
	
	
	
	
	/* package */ static NMSTypeWrapper parse(StringReader desc) {
		try {
			int arrayDepth = 0;
			char c;
			while ((c = (char) desc.read()) == '[') {
				arrayDepth++;
			}
			String type = switch(c) {
			case 'Z' -> "boolean";
			case 'B' -> "byte";
			case 'C' -> "char";
			case 'D' -> "double";
			case 'F' -> "float";
			case 'I' -> "int";
			case 'J' -> "long";
			case 'S' -> "short";
			case 'L' -> {
				StringBuilder sbClass = new StringBuilder();
				char r;
				while ((r = (char) desc.read()) != ';') {
    				sbClass.append(r);
				}
				yield NMSReflect.binaryClassName(sbClass.toString());
			}
			default -> "void";
			};
			return new NMSTypeWrapper(type, arrayDepth);
		} catch (IOException e) {
			throw new RuntimeException("StringReader read error", e);
		}
	}
	
	

	
	/* package */ static List<NMSTypeWrapper> toTypeList(List<Object> paramsType) {
		List<NMSTypeWrapper> types = new ArrayList<>(paramsType.size());
		for (int i = 0; i < paramsType.size(); i++) {
			Object param = paramsType.get(i);
			try {
				types.add(NMSTypeWrapper.toType(param));
			} catch (NullPointerException|IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid parameterType at index " + i, e);
			}
		}
		return types;
	}
	
	/* package */ static Class<?>[] toClassArray(List<NMSTypeWrapper> types) throws ClassNotFoundException {
		Class<?>[] classes = new Class<?>[types.size()];
		for (int i = 0; i < types.size(); i++) {
			classes[i] = types.get(i).toClass();
		}
		return classes;
	}
	
}