package com.belman.business.command.data;

import com.belman.domain.shared.Command;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Command for saving data to a repository.
 * <p>
 * This command saves data to a repository and supports undo functionality
 * by keeping track of the previous state.
 *
 * @param <T> the type of data being saved
 */
public class SaveDataCommand<T> implements Command<T> {
    private final T data;
    private final Consumer<T> saveOperation;
    private final Supplier<T> loadOperation;
    private final String description;
    private T previousData;

    /**
     * Creates a new SaveDataCommand with the specified data and operations.
     *
     * @param data          the data to save
     * @param saveOperation the operation to save the data
     * @param loadOperation the operation to load the current data (for undo)
     * @param description   a description of the save operation
     */
    public SaveDataCommand(T data, Consumer<T> saveOperation, Supplier<T> loadOperation, String description) {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if (saveOperation == null) {
            throw new IllegalArgumentException("Save operation cannot be null");
        }
        if (loadOperation == null) {
            throw new IllegalArgumentException("Load operation cannot be null");
        }

        this.data = data;
        this.saveOperation = saveOperation;
        this.loadOperation = loadOperation;
        this.description = description != null ? description : "Save data";
    }

    @Override
    public CompletableFuture<T> execute() {
        return CompletableFuture.supplyAsync(() -> {
            // Load the current data for undo
            previousData = loadOperation.get();

            // Save the new data
            saveOperation.accept(data);

            return data;
        });
    }

    @Override
    public CompletableFuture<Void> undo() {
        if (!canUndo()) {
            return CompletableFuture.failedFuture(
                    new UnsupportedOperationException("Cannot undo save: no previous data"));
        }

        return CompletableFuture.runAsync(() -> {
            // Restore the previous data
            saveOperation.accept(previousData);
        });
    }

    @Override
    public boolean canUndo() {
        return previousData != null;
    }

    @Override
    public String getDescription() {
        return description;
    }
}