package gg.moonflower.pollen.api.registry.attachment;

/**
 * Used to generate default {@link RegistryAttachment} values for registry objects that don't already have one defined.
 *
 * @param <T> The type of registry object
 * @param <R> The type of attachment value
 * @author ebo2022
 * @since
 */
@FunctionalInterface
public interface DefaultValueProvider<T, R> {

    /**
     * Generates a default attachment for the specified registry object.
     *
     * @param value The object to create an attachment value for
     * @return A result representing the default value
     */
    ComputationResult<R> compute(T value);

    /**
     * Represents the result of generating a default attachment value.
     *
     * @param <T> The object type
     * @since
     */
    final class ComputationResult<T> {
        private final boolean failure;
        private final T value;
        private final String error;

        private ComputationResult(boolean failure, T value, String error) {
            this.failure = failure;
            this.value = value;
            this.error = error;
        }

        /**
         * Creates a successful computation result.
         *
         * @param value The computed value
         * @return A new {@link ComputationResult} representing the computed value
         */
        public static <V> ComputationResult<V> success(V value) {
            return new ComputationResult<>(false, value, null);
        }

        /**
         * Creates a failed computation result with an error message.
         *
         * @param error The error to print in the log
         * @return A failed {@link ComputationResult}
         */
        public static <V> ComputationResult<V> fail(String error) {
            return new ComputationResult<>(true, null, error);
        }

        /**
         * @return Whether the result is a failure
         */
        public boolean isFailure() {
            return this.failure;
        }

        public T get() {
            if (this.failure)
                throw new IllegalStateException("Invalid get call for failed computation result");
            return this.value;
        }

        public String error() {
            if (!this.failure)
                throw new IllegalStateException("Cannot retrieve error message for a successful computation result");
            return this.error;
        }
    }
}
