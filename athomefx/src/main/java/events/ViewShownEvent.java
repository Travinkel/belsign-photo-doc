package events;

/**
 * Event that is published when a view is shown.
 * This is a sample domain event that demonstrates how the domain event system can be used.
 */
public class ViewShownEvent extends AbstractDomainEvent {
    private final String viewName;
    
    /**
     * Creates a new ViewShownEvent with the specified view name.
     * 
     * @param viewName the name of the view that was shown
     */
    public ViewShownEvent(String viewName) {
        super();
        this.viewName = viewName;
    }
    
    /**
     * Gets the name of the view that was shown.
     * 
     * @return the view name
     */
    public String getViewName() {
        return viewName;
    }
}