package org.machinemc.cogwheel;

import org.machinemc.cogwheel.serialization.SerializerContext;
import org.machinemc.cogwheel.util.error.ErrorEntry;

class DefaultErrorHandler implements ErrorHandler {

    @Override
    public void handle(SerializerContext context, ErrorEntry error) {
        System.err.println(error.type() + ": " + error.message());
    }

}
