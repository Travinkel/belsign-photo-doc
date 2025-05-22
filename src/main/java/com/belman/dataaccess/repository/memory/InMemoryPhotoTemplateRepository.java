package com.belman.dataaccess.repository.memory;

import com.belman.dataaccess.repository.BaseRepository;
import com.belman.domain.order.OrderId;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.photo.PhotoTemplateRepository;
import com.belman.domain.services.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of the PhotoTemplateRepository interface.
 * This implementation stores photo templates in memory and is used as a fallback
 * when a database is not available or for testing purposes.
 */
public class InMemoryPhotoTemplateRepository extends BaseRepository<PhotoTemplate, String> implements PhotoTemplateRepository {

    private final Map<String, PhotoTemplate> templates = new ConcurrentHashMap<>();
    private final Map<OrderId, Map<String, Boolean>> orderTemplates = new ConcurrentHashMap<>();

    /**
     * Creates a new InMemoryPhotoTemplateRepository with the specified logger factory.
     *
     * @param loggerFactory the logger factory to use for logging
     */
    public InMemoryPhotoTemplateRepository(LoggerFactory loggerFactory) {
        super(loggerFactory);
        initializeDefaultTemplates();
    }

    /**
     * Initializes the repository with default templates.
     */
    private void initializeDefaultTemplates() {
        // Add all predefined templates from the PhotoTemplate class
        addTemplate(PhotoTemplate.TOP_VIEW_OF_JOINT);
        addTemplate(PhotoTemplate.SIDE_VIEW_OF_WELD);
        addTemplate(PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY);
        addTemplate(PhotoTemplate.BACK_VIEW_OF_ASSEMBLY);
        addTemplate(PhotoTemplate.LEFT_VIEW_OF_ASSEMBLY);
        addTemplate(PhotoTemplate.RIGHT_VIEW_OF_ASSEMBLY);
        addTemplate(PhotoTemplate.BOTTOM_VIEW_OF_ASSEMBLY);
        addTemplate(PhotoTemplate.CLOSE_UP_OF_WELD);
        addTemplate(PhotoTemplate.ANGLED_VIEW_OF_JOINT);
        addTemplate(PhotoTemplate.OVERVIEW_OF_ASSEMBLY);
        addTemplate(PhotoTemplate.CUSTOM);
    }

    /**
     * Adds a template to the repository.
     *
     * @param template the template to add
     */
    private void addTemplate(PhotoTemplate template) {
        templates.put(template.name(), template);
    }

    @Override
    protected String getId(PhotoTemplate photoTemplate) {
        return photoTemplate.name();
    }

    @Override
    protected PhotoTemplate createCopy(PhotoTemplate photoTemplate) {
        // PhotoTemplate is immutable, so we can return the original
        return photoTemplate;
    }

    @Override
    protected Optional<PhotoTemplate> doFindById(String id) {
        return Optional.ofNullable(templates.get(id));
    }

    @Override
    protected PhotoTemplate doSave(PhotoTemplate photoTemplate) {
        templates.put(photoTemplate.name(), photoTemplate);
        return photoTemplate;
    }

    @Override
    protected void doDelete(PhotoTemplate photoTemplate) {
        templates.remove(photoTemplate.name());
        // Also remove any associations with orders
        for (Map<String, Boolean> orderTemplateMap : orderTemplates.values()) {
            orderTemplateMap.remove(photoTemplate.name());
        }
    }

    @Override
    protected List<PhotoTemplate> doFindAll() {
        return new ArrayList<>(templates.values());
    }

    @Override
    protected boolean doExistsById(String id) {
        return templates.containsKey(id);
    }

    @Override
    protected long doCount() {
        return templates.size();
    }

    @Override
    public List<PhotoTemplate> findByOrderId(OrderId orderId) {
        System.out.println("[DEBUG_LOG] InMemoryPhotoTemplateRepository: Finding templates for order ID: " + orderId.id());

        Map<String, Boolean> templateMap = orderTemplates.get(orderId);
        if (templateMap == null || templateMap.isEmpty()) {
            // If no templates are associated with this order, return an empty list
            // This will trigger the DefaultPhotoTemplateService to create and associate default templates
            System.out.println("[DEBUG_LOG] InMemoryPhotoTemplateRepository: No templates found for order ID: " + orderId.id());
            return Collections.emptyList();
        }

        List<PhotoTemplate> result = templateMap.keySet().stream()
                .map(templates::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        System.out.println("[DEBUG_LOG] InMemoryPhotoTemplateRepository: Found " + result.size() + " templates for order ID: " + orderId.id());
        for (int i = 0; i < result.size(); i++) {
            PhotoTemplate template = result.get(i);
            System.out.println("[DEBUG_LOG] InMemoryPhotoTemplateRepository: Template " + (i+1) + ": " + 
                              "Name=" + template.name() + ", " +
                              "Description=" + template.description());
        }

        return result;
    }

    @Override
    public boolean associateWithOrder(OrderId orderId, String templateId, boolean required) {
        System.out.println("[DEBUG_LOG] InMemoryPhotoTemplateRepository: Associating template " + templateId + " with order " + orderId.id() + ", required: " + required);

        if (!templates.containsKey(templateId)) {
            System.out.println("[DEBUG_LOG] InMemoryPhotoTemplateRepository: Template " + templateId + " not found in templates map");
            return false;
        }

        // Get or create the template map for this order
        Map<String, Boolean> templateMap = orderTemplates.computeIfAbsent(orderId, k -> {
            System.out.println("[DEBUG_LOG] InMemoryPhotoTemplateRepository: Creating new template map for order " + orderId.id());
            return new HashMap<>();
        });

        // Add the template to the map
        templateMap.put(templateId, required);

        // Log the current state of the template map for this order
        System.out.println("[DEBUG_LOG] InMemoryPhotoTemplateRepository: Order " + orderId.id() + " now has " + templateMap.size() + " templates");

        return true;
    }

    @Override
    public boolean removeFromOrder(OrderId orderId, String templateId) {
        Map<String, Boolean> templateMap = orderTemplates.get(orderId);
        if (templateMap == null) {
            return false;
        }

        Boolean removed = templateMap.remove(templateId);
        return removed != null;
    }

    @Override
    public boolean isRequiredForOrder(OrderId orderId, String templateId) {
        Map<String, Boolean> templateMap = orderTemplates.get(orderId);
        if (templateMap == null) {
            return false;
        }

        return Boolean.TRUE.equals(templateMap.get(templateId));
    }
}
