package org.machinemc.cogwheel.util.error;

import org.jetbrains.annotations.Nullable;
import org.machinemc.cogwheel.serialization.SerializerContext;
import org.jetbrains.annotations.NotNull;
import org.machinemc.cogwheel.ErrorHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ErrorContainer implements Iterable<ErrorEntry> {

    private final @Nullable ErrorContainer parent;
    private final List<ErrorEntry> entries = new ArrayList<>();

    public ErrorContainer() {
        this(null);
    }

    public ErrorContainer(@Nullable ErrorContainer parent) {
        this.parent = parent;
    }

    public void error(String message) {
        error(ErrorType.CUSTOM, message);
    }

    public void error(ErrorType type, String message) {
        error(new ErrorEntry(type, message));
    }

    public void error(ErrorEntry entry) {
        entries.add(entry);
        if (parent != null)
            parent.error(entry);
    }

    public void handleErrors(SerializerContext context) {
        if (!hasErrors()) return;
        ErrorHandler handler = context.properties().errorHandler();
        entries.removeIf(entry -> {
            handler.handle(context, entry);
            if (parent != null)
                parent.entries.remove(entry);
            return true;
        });
    }

    public boolean hasErrors() {
        return !entries.isEmpty();
    }

    @Override
    public @NotNull Iterator<ErrorEntry> iterator() {
        return entries.iterator();
    }

}
