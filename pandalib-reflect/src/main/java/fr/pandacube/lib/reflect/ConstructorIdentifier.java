package fr.pandacube.lib.reflect;

import java.util.Arrays;

/* package */ record ConstructorIdentifier(Class<?>[] parameters) {
    ConstructorIdentifier {
        parameters = (parameters == null) ? new Class<?>[0] : parameters;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ConstructorIdentifier o
                && Arrays.equals(o.parameters, parameters);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(parameters);
    }
}
