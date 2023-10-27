package org.machinemc.cogwheel;

import org.machinemc.cogwheel.serialization.SerializerContext;
import org.machinemc.cogwheel.util.error.ErrorEntry;

public interface ErrorHandler {

    ErrorHandler SUPPRESSING = (context, error) -> {};
    ErrorHandler NORMAL = new DefaultErrorHandler();

    void handle(SerializerContext context, ErrorEntry error);

}
