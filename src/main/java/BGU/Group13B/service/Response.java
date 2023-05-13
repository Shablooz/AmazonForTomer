package BGU.Group13B.service;

/**
 * The Response class represents a response from the backend.
 * It contains data, a message, and a status.
 *
 * @param <T> the type of the data in the response
 */
public class Response<T> {

    private final T data;
    private final String message;
    private final Status status;

    /**
     * Constructs a new Response object with the given data, message, and status.
     *
     * @param builder the Builder object that contains the data, message, and status
     */
    private Response(Builder<T> builder) {
        this.data = builder.data;
        this.message = builder.message;
        this.status = builder.status;
    }

    /**
     * Returns the data in the response.
     *
     * @return the data in the response
     */
    public T getData() {
        return data;
    }

    /**
     * Returns the message in the response.
     *
     * @return the message in the response
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the status of the response.
     *
     * @return the status of the response
     */
    public Status getStatus() {
        return status;
    }


    /**
     * The Status enum represents the status of the response.
     * It can be SUCCESS, PARTIAL_SUCCESS, or FAILURE.
     */
    public enum Status {
        SUCCESS,
        PARTIAL_SUCCESS,
        FAILURE,
        NO_PERMISSION
    }

    /**
     * The Builder class is used to construct Response objects.
     * It contains methods to set the data, message, and status of the response.
     *
     * @param <T> the type of the data in the response
     */
    public static class Builder<T> {
        private T data;
        private String message;
        private Status status;

        /**
         * Sets the data in the response.
         *
         * @param data the data in the response
         * @return the Builder object
         */
        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        /**
         * Sets the message in the response.
         *
         * @param message the message in the response
         * @return the Builder object
         */
        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        /**
         * Sets the status of the response.
         *
         * @param status the status of the response
         * @return the Builder object
         */
        public Builder<T> status(Status status) {
            this.status = status;
            return this;
        }

        /**
         * Builds a new Response object with the data, message, and status set in the Builder.
         *
         * @return a new Response object
         */
        public Response<T> build() {
            return new Response<>(this);
        }
    }
    /**
     * Returns a response indicating success, along with a default success message.
     *
     * @param data the data to be returned to the response
     * @return a response indicating success
     *
     * @param <T> the type of the data being returned to the response
     *
     * @code
     * Response<User> response = Response.success(user);
     * ```
     */
    public static <T> Response<T> success(T data) {
        return new Builder<T>()
                .data(data)
                .message("Operation completed successfully")
                .status(Status.SUCCESS)
                .build();
    }
    public static Response<VoidResponse> success() {
        return new Builder<VoidResponse>()
                .data(new VoidResponse())
                .message("Operation completed successfully")
                .status(Status.SUCCESS)
                .build();
    }

    /**
     * Returns a response indicating partial success, along with a custom message.
     *
     * @param data    the data to be returned to the response
     * @param message a custom message to be included in the response
     * @return a response indicating partial success
     *
     * @param <T> the type of the data being returned to the response
     *
     * @code
     * Response<User> response = Response.partialSuccess(user, "Some fields were updated successfully.");
     * ```
     */
    public static <T> Response<T> partialSuccess(T data, String message) {
        return new Builder<T>()
                .data(data)
                .message(message)
                .status(Status.PARTIAL_SUCCESS)
                .build();
    }

    /**
     * Returns a response indicating failure, along with a custom message.
     *
     * @param message a custom message to be included in the response
     * @return a response indicating failure
     *
     * @param <T> the type of the data being returned to the response
     *
     * @code
     * Response<User> response = Response.failure("Could not retrieve user data.");
     * ```
     */
    public static <T> Response<T> failure(String message) {
        return new Builder<T>()
                .data(null)
                .message(message)
                .status(Status.FAILURE)
                .build();
    }

    /**
     * Returns a response indicating failure, along with the message from the exception.
     *
     * @param e the exception that caused the failure
     * @return a response indicating failure
     *
     * @param <T> the type of the data being returned to the response
     *
     * @code
     * try {
     *     // some code that may throw an exception
     *     Response<User> response = Response.success(user);
     * } catch (Exception e) {
     *     Response<User> response = Response.exception(e);
     * }
     *
     */
    public static <T> Response<T> exception(Exception e) {
        return new Builder<T>()
                .data(null)
                .message(e.getMessage())
                .status(Status.FAILURE)
                .build();
    }


    /**
     * @return true if the status is not SUCCESS, false otherwise
     *
     * @code
     * inside the ProjectTest class - see ProjectTest::handleResponse()
     * protected <T> T handleResponse(Response<T> response) {
     *         if(response.didntSucceed()) {
     *             fail();
     *         }
     *         return response.getData();
     * }
     *
     * for testing if failed - see StoreAT::addStore_guest_fail()
     * assertTrue(session.someFunction(...).didntSucceed());
     *
     */
    public boolean didntSucceed() {
        return this.status != Status.SUCCESS;
    }


}
