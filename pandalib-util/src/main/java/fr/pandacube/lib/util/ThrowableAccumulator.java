package fr.pandacube.lib.util;

import fr.pandacube.lib.util.ThrowableUtil.RunnableException;
import fr.pandacube.lib.util.ThrowableUtil.SupplierException;

import java.util.function.Supplier;

/**
 * A class that delay and accumulate thrown exceptions, that can be thrown later using {@link #throwCaught()}.
 * @param <T> the type of {@link Throwable} to accumulate.
 */
public class ThrowableAccumulator<T extends Throwable> {
    T base = null;
    final Class<T> throwableType;

    /**
     * Creates a new {@link ThrowableAccumulator} with the specified throwable type.
     * @param throwableType The type of the {@link Throwable} to accumulate.
     */
    public ThrowableAccumulator(Class<T> throwableType) {
        this.throwableType = throwableType;
    }

    /**
     * Run the provided {@link RunnableException}, catching an eventual exception to accumulate for later use.
     * @param run the {@link RunnableException} to run.
     * @throws Exception if an exception not handled by this accumulator is thrown.
     */
    public void catchThrowable(RunnableException<Exception> run) throws Exception {
        try {
            run.run();
        } catch (Throwable t) {
            accumulateThrowable(t);
        }
    }

    /**
     * Run the provided {@link SupplierException}, catching an eventual exception to accumulate for later use.
     * @param supp the {@link SupplierException} to run.
     * @param returnValueIfException The value to return if this accumulator catch an exception.
     * @return The return value of the supplier, or {@code returnValueIfException} if this accumulator catch an exception.
     * @throws Exception if an exception not handled by this accumulator is thrown.
     * @param <R> the type of the return value of the supplier.
     */
    public <R> R catchThrowable(SupplierException<R, Exception> supp, R returnValueIfException) throws Exception {
        return catchThrowable(supp, (Supplier<R>) () -> returnValueIfException);
    }

    /**
     * Run the provided {@link SupplierException}, catching an eventual exception to accumulate for later use.
     * @param supp the {@link SupplierException} to run.
     * @param returnValueIfException The value to return if this accumulator catch an exception.
     * @return The return value of the supplier, or the return value of {@code returnValueIfException} if this
     *         accumulator catch an exception.
     * @throws Exception if an exception not handled by this accumulator is thrown.
     * @param <R> the type of the return value of both suppliers.
     */
    public <R> R catchThrowable(SupplierException<R, Exception> supp, Supplier<R> returnValueIfException) throws Exception {
        try {
            return supp.get();
        } catch (Throwable t) {
            accumulateThrowable(t);
        }
        return returnValueIfException.get();
    }


    private void accumulateThrowable(Throwable t) throws Exception {
        if (throwableType.isInstance(t)) {
            synchronized (this) {
                if (base == null)
                    base = throwableType.cast(t);
                else {
                    base.addSuppressed(t);
                }
            }
        }
        else {
            throwEx(t);
        }
    }

    /**
     * Throws an exception if there is at least one caught by this accumulator.
     * If multiple exception where caught, all the exception after the first one are added to the first one as
     * suppressed exceptions.
     * If no exception were caught, this method does nothing.
     * @throws Exception the first accumulated throwable, the other ones being suppressed.
     */
    public void throwCaught() throws Exception {
        synchronized (this) {
            if (base != null)
                throwEx(base);
        }
    }

    private void throwEx(Throwable t) throws Exception {
        if (t instanceof Error e)
            throw e;
        else if (t instanceof Exception e)
            throw e;
    }

}
