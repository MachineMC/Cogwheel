package org.machinemc.cogwheel.util.error;

import org.machinemc.cogwheel.serialization.SerializerContext;
import org.jetbrains.annotations.NotNull;
import org.machinemc.cogwheel.ErrorHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ErrorContainer implements Iterable<ErrorEntry> {

    private final List<ErrorEntry> entries = new ArrayList<>();

    public void error(String message) {
        error(ErrorType.CUSTOM, message);
    }

    public void error(ErrorType type, String message) {
        error(new ErrorEntry(type, message));
    }

    public void error(ErrorEntry entry) {
        entries.add(entry);
    }

    public void handleErrors(SerializerContext context) {
        if (!hasErrors()) return;
        ErrorHandler handler = context.properties().errorHandler();
        entries.removeIf(entry -> {
            handler.handle(context, entry);
            return true;
        });
    }

    public boolean hasErrors() {
        return !entries.isEmpty();
    }

    public void merge(ErrorContainer other) {
        entries.addAll(other.entries);
    }

    @Override
    public @NotNull Iterator<ErrorEntry> iterator() {
        return entries.iterator();
    }

}
