package fr.pandacube.lib.reflect;

import java.util.Arrays;
import java.util.Objects;

/* package */ record MethodIdentifier(String methodName, Class<?>[] parameters) {
    MethodIdentifier {
        Objects.requireNonNull(methodName);
        parameters = (parameters == null) ? new Class<?>[0] : parameters;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof MethodIdentifier o
                && o.methodName.equals(methodName)
                && Arrays.equals(o.parameters, parameters);
    }

    @Override
    public int hashCode() {
        return methodName.hashCode() ^ Arrays.hashCode(parameters);
    }
}
