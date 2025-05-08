package com.belman.presentation.binding;

import com.belman.business.domain.shared.Command;
import com.belman.business.core.CommandManager;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.MenuItem;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Builder for creating command bindings.
 * <p>
 * This class provides a fluent API for creating and configuring command bindings.
 *
 * @param <T> the type of result returned by the command
 */
public class CommandBindingBuilder<T> {
    private final Supplier<Command<T>> commandFactory;
    private CommandManager commandManager = CommandManager.getInstance();
    private Consumer<T> resultHandler;
    private Consumer<Throwable> errorHandler;
    private boolean enabled = true;

    /**
     * Creates a new CommandBindingBuilder with the specified command factory.
     *
     * @param commandFactory the factory that creates the command to execute
     */
    private CommandBindingBuilder(Supplier<Command<T>> commandFactory) {
        if (commandFactory == null) {
            throw new IllegalArgumentException("Command factory cannot be null");
        }
        this.commandFactory = commandFactory;
    }

    /**
     * Creates a new CommandBindingBuilder with the specified command.
     *
     * @param command the command to execute
     * @param <T>     the type of result returned by the command
     * @return a new CommandBindingBuilder
     */
    public static <T> CommandBindingBuilder<T> forCommand(Command<T> command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }
        return new CommandBindingBuilder<>(() -> command);
    }

    /**
     * Creates a new CommandBindingBuilder with the specified command factory.
     *
     * @param commandFactory the factory that creates the command to execute
     * @param <T>            the type of result returned by the command
     * @return a new CommandBindingBuilder
     */
    public static <T> CommandBindingBuilder<T> forCommandFactory(Supplier<Command<T>> commandFactory) {
        return new CommandBindingBuilder<>(commandFactory);
    }

    /**
     * Sets the command manager to use for executing commands.
     *
     * @param commandManager the command manager to use
     * @return this builder for method chaining
     */
    public CommandBindingBuilder<T> withCommandManager(CommandManager commandManager) {
        if (commandManager == null) {
            throw new IllegalArgumentException("Command manager cannot be null");
        }
        this.commandManager = commandManager;
        return this;
    }

    /**
     * Sets a handler for the command result.
     *
     * @param resultHandler the handler for the command result
     * @return this builder for method chaining
     */
    public CommandBindingBuilder<T> withResultHandler(Consumer<T> resultHandler) {
        this.resultHandler = resultHandler;
        return this;
    }

    /**
     * Sets a handler for command execution errors.
     *
     * @param errorHandler the handler for command execution errors
     * @return this builder for method chaining
     */
    public CommandBindingBuilder<T> withErrorHandler(Consumer<Throwable> errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    /**
     * Sets whether the binding is enabled.
     *
     * @param enabled whether the binding is enabled
     * @return this builder for method chaining
     */
    public CommandBindingBuilder<T> enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * Builds a command binding.
     *
     * @return a new CommandBinding
     */
    public CommandBinding<T> build() {
        CommandBinding<T> binding = new CommandBinding<>(commandFactory, commandManager);
        
        if (resultHandler != null) {
            binding.onResult(resultHandler);
        }
        
        if (errorHandler != null) {
            binding.onError(errorHandler);
        }
        
        binding.setEnabled(enabled);
        
        return binding;
    }

    /**
     * Builds a command binding and binds it to a button.
     *
     * @param button the button to bind to
     * @return the command binding
     */
    public CommandBinding<T> bindToButton(ButtonBase button) {
        return build().bindToButton(button);
    }

    /**
     * Builds a command binding and binds it to a menu item.
     *
     * @param menuItem the menu item to bind to
     * @return the command binding
     */
    public CommandBinding<T> bindToMenuItem(MenuItem menuItem) {
        return build().bindToMenuItem(menuItem);
    }
}