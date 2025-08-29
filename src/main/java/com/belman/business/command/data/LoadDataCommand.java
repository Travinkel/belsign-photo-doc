package com.belman.business.command.data;

import com.belman.domain.shared.Command;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Command for loading data from a repository.
 * <p>
 * This command loads data from a repository and supports undo functionality
 * by keeping track of the previous state.
 *
 * @param <T> the type of data being loaded
 */
public class LoadDataCommand<T> implements Command<T> {
    private final Supplier<T> loadOperation;
    private final Consumer<T> dataConsumer;
    private final String description;
    private T previousData;
    private T loadedData;

    /**
     * Creates a new LoadDataCommand with the specified operations.
     *
     * @param loadOperation the operation to load the data
     * @param dataConsumer  the consumer that will receive the loaded data
     * @param description   a description of the load operation
     */
    public LoadDataCommand(Supplier<T> loadOperation, Consumer<T> dataConsumer, String description) {
        if (loadOperation == null) {
            throw new IllegalArgumentException("Load operation cannot be null");
        }
        if (dataConsumer == null) {
            throw new IllegalArgumentException("Data consumer cannot be null");
        }

        this.loadOperation = loadOperation;
        this.dataConsumer = dataConsumer;
        this.description = description != null ? description : "Load data";
    }

    @Override
    public CompletableFuture<T> execute() {
        return CompletableFuture.supplyAsync(() -> {
            // Store the current data for undo
            previousData = loadOperation.get();

            // Load the new data
            loadedData = loadOperation.get();

            // Provide the loaded data to the consumer
            dataConsumer.accept(loadedData);

            return loadedData;
        });
    }

    @Override
    public CompletableFuture<Void> undo() {
        if (!canUndo()) {
            return CompletableFuture.failedFuture(
                    new UnsupportedOperationException("Cannot undo load: no previous data"));
        }

        return CompletableFuture.runAsync(() -> {
            // Restore the previous data
            dataConsumer.accept(previousData);
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

    /**
     * Gets the loaded data.
     *
     * @return the loaded data, or null if the command hasn't been executed yet
     */
    public T getLoadedData() {
        return loadedData;
    }
}