package core;

public abstract class BaseController<T extends BaseViewModel<?>> {
    private T viewModel;

    public void setViewModel(T viewModel) {
        this.viewModel = viewModel;
    }

    public T getViewModel() {
        return viewModel;
    }

    public void initializeBinding() {
        // Optional: Override in subclasses to bind ViewModel to the view
    }
}