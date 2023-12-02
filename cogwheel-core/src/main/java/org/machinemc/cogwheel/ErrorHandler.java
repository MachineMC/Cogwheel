package org.machinemc.cogwheel;

import org.machinemc.cogwheel.serialization.SerializerContext;
import org.machinemc.cogwheel.util.error.ErrorEntry;

import java.util.function.Function;

public interface ErrorHandler {

    ErrorHandler SUPPRESSING = (context, error) -> {};
    ErrorHandler NORMAL = new DefaultErrorHandler();
    Function<Function<String, ? extends RuntimeException>, ErrorHandler> THROW_EXCEPTION = throwable -> (context, error) -> {
        throw throwable.apply(error.type() + ": " + error.message());
    };

    void handle(SerializerContext context, ErrorEntry error);

}
