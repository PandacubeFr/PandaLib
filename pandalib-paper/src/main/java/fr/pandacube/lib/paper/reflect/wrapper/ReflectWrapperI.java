package fr.pandacube.lib.paper.reflect.wrapper;

public interface ReflectWrapperI {
	
	default Class<?> __getRuntimeClass() {
		return WrapperRegistry.getRuntimeClassOfWrapperClass(getClass());
	}
	
	Object __getRuntimeInstance();
	

}
