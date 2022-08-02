package fr.pandacube.lib.reflect.wrapper;

public interface ReflectWrapperI {
	
	default Class<?> __getRuntimeClass() {
		return WrapperRegistry.getRuntimeClassOfWrapperClass(getClass());
	}
	
	Object __getRuntimeInstance();
	

}
