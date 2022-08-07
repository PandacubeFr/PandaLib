/**
 * Set of class allowing applications to implement almost transparent reflection classes.
 * The goal it to implement the class, methods and fields that the application have only access through reflection, and
 * reflection call when these implementation are called.
 * Each of those reflection classes must extend {@link fr.pandacube.lib.reflect.wrapper.ReflectWrapper} (or, if it’s
 * an interface, must extend {@link fr.pandacube.lib.reflect.wrapper.ReflectWrapperI}). The implemented class wraps
 * the reflected object and redirects the method calls to them using reflection.
 */
package fr.pandacube.lib.reflect.wrapper;