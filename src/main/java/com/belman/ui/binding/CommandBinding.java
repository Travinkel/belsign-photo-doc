package com.belman.ui.binding;

import com.belman.domain.shared.Command;
import com.belman.service.command.CommandManager;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.MenuItem;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Binds a command to a UI control.
 * <p>
 * This class provides methods for binding commands to UI controls such as buttons
 * and menu items. It also supports automatic enabling/disabling of UI elements
 * based on command state.
 *
 * @param <T> the type of result returned by the command
 */
public class CommandBinding<T> {
    private final Supplier<Command<T>> commandFactory;
    private final CommandManager commandManager;
    private final BooleanProperty enabledProperty = new SimpleBooleanProperty(true);
    private Consumer<T> resultHandler;
    private Consumer<Throwable> errorHandler;

    /**
     * Creates a new CommandBinding with the specified command factory.
     *
     * @param commandFactory the factory that creates the command to execute
     */
    public CommandBinding(Supplier<Command<T>> commandFactory) {
        this(commandFactory, CommandManager.getInstance());
    }

    /**
     * Creates a new CommandBinding with the specified command factory and command manager.
     *
     * @param commandFactory the factory that creates the command to execute
     * @param commandManager the command manager to use for executing commands
     */
    public CommandBinding(Supplier<Command<T>> commandFactory, CommandManager commandManager) {
        if (commandFactory == null) {
            throw new IllegalArgumentException("Command factory cannot be null");
        }
        if (commandManager == null) {
            throw new IllegalArgumentException("Command manager cannot be null");
        }

        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    /**
     * Binds this command to a button.
     *
     * @param button the button to bind to
     * @return this binding for method chaining
     */
    public CommandBinding<T> bindToButton(ButtonBase button) {
        if (button == null) {
            throw new IllegalArgumentException("Button cannot be null");
        }

        // Bind the button's disabled property to the negation of the enabled property
        button.disableProperty().bind(enabledProperty.not());

        // Set the button's action to execute the command
        button.setOnAction(this::handleAction);

        return this;
    }

    /**
     * Handles the action event from a UI control.
     *
     * @param event the action event
     */
    private void handleAction(ActionEvent event) {
        execute();
    }

    /**
     * Executes the command.
     */
    public void execute() {
        if (!isEnabled()) {
            return;
        }

        try {
            Command<T> command = commandFactory.get();

            if (command == null) {
                throw new IllegalStateException("Command factory returned null");
            }

            if (!command.canExecute()) {
                return;
            }

            commandManager.execute(command)
                    .thenAccept(result -> {
                        if (resultHandler != null) {
                            resultHandler.accept(result);
                        }
                    })
                    .exceptionally(ex -> {
                        if (errorHandler != null) {
                            errorHandler.accept(ex);
                        }
                        return null;
                    });
        } catch (Exception ex) {
            if (errorHandler != null) {
                errorHandler.accept(ex);
            }
        }
    }

    /**
     * Checks if this binding is enabled.
     *
     * @return true if this binding is enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabledProperty.get();
    }

    /**
     * Sets whether this binding is enabled.
     *
     * @param enabled whether this binding is enabled
     */
    public void setEnabled(boolean enabled) {
        enabledProperty.set(enabled);
    }

    /**
     * Binds this command to a menu item.
     *
     * @param menuItem the menu item to bind to
     * @return this binding for method chaining
     */
    public CommandBinding<T> bindToMenuItem(MenuItem menuItem) {
        if (menuItem == null) {
            throw new IllegalArgumentException("Menu item cannot be null");
        }

        // Bind the menu item's disabled property to the negation of the enabled property
        menuItem.disableProperty().bind(enabledProperty.not());

        // Set the menu item's action to execute the command
        menuItem.setOnAction(this::handleAction);

        return this;
    }

    /**
     * Sets a handler for the command result.
     *
     * @param resultHandler the handler for the command result
     * @return this binding for method chaining
     */
    public CommandBinding<T> onResult(Consumer<T> resultHandler) {
        this.resultHandler = resultHandler;
        return this;
    }

    /**
     * Sets a handler for command execution errors.
     *
     * @param errorHandler the handler for command execution errors
     * @return this binding for method chaining
     */
    public CommandBinding<T> onError(Consumer<Throwable> errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    /**
     * Gets the enabled property of this binding.
     *
     * @return the enabled property
     */
    public BooleanProperty enabledProperty() {
        return enabledProperty;
    }
}