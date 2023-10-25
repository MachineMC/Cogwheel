package org.machinemc.cogwheel.util.error;

public enum ErrorType {

    /**
     * Error type indicating that a required key was not found.
     */
    KEY_NOT_FOUND,

    /**
     * Error type indicating that an unexpected key was encountered.
     */
    UNEXPECTED_KEY,

    /**
     * Error type indicating that an object could not be serialized.
     */
    SERIALIZER_NOT_FOUND,

    /**
     * Custom error type for user-defined error cases.
     */
    CUSTOM,
    MISMATCHED_TYPES,

}
