package com.belman.unit.presentation.usecases.worker.photocube;

import com.belman.application.usecase.order.OrderService;
import com.belman.application.usecase.photo.CameraImageProvider;
import com.belman.application.usecase.photo.CameraImageProviderFactory;
import com.belman.application.usecase.photo.PhotoService;
import com.belman.application.usecase.worker.WorkerService;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.presentation.usecases.worker.photocube.PhotoCubeViewModel;
import com.belman.presentation.usecases.worker.photocube.PhotoTemplateStatusViewModel;
import javafx.beans.property.ListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the PhotoCubeViewModel class.
 * These tests verify that the ViewModel correctly handles template filtering
 * and selection, especially in edge cases that could cause IndexOutOfBoundsException.
 */
@ExtendWith(MockitoExtension.class)
public class PhotoCubeViewModelTest {

    @Mock
    private OrderService orderService;

    @Mock
    private PhotoService photoService;

    @Mock
    private WorkerService workerService;

    @Mock
    private CameraImageProvider cameraImageProvider;

    private PhotoCubeViewModel viewModel;

    // Use real PhotoTemplate instances instead of mocks
    private final PhotoTemplate template1 = PhotoTemplate.TOP_VIEW_OF_JOINT;
    private final PhotoTemplate template2 = PhotoTemplate.SIDE_VIEW_OF_WELD;
    private final PhotoTemplate template3 = PhotoTemplate.ANGLED_VIEW_OF_JOINT;

    private ObservableList<PhotoTemplateStatusViewModel> templateStatusList;

    private static MockedStatic<CameraImageProviderFactory> mockedFactory;

    @BeforeAll
    public static void setUpClass() {
        // Mock the static CameraImageProviderFactory.getInstance() method
        mockedFactory = mockStatic(CameraImageProviderFactory.class);
    }

    @BeforeEach
    public void setUp() throws Exception {
        // Configure the mocked factory to return our mock CameraImageProvider
        mockedFactory.when(CameraImageProviderFactory::getInstance).thenReturn(cameraImageProvider);

        // Create the view model manually
        viewModel = new PhotoCubeViewModel();

        // Inject the mocked dependencies using reflection
        injectDependency("orderService", orderService);
        injectDependency("photoService", photoService);
        injectDependency("workerService", workerService);

        // Get the existing templateStatusList from the viewModel
        Field templateStatusListField = PhotoCubeViewModel.class.getDeclaredField("templateStatusList");
        templateStatusListField.setAccessible(true);
        ListProperty<PhotoTemplateStatusViewModel> templateStatusListProperty = 
            (ListProperty<PhotoTemplateStatusViewModel>) templateStatusListField.get(viewModel);

        // Create our test items
        templateStatusList = FXCollections.observableArrayList();
        templateStatusList.add(new PhotoTemplateStatusViewModel(template1, false, false, true));
        templateStatusList.add(new PhotoTemplateStatusViewModel(template2, true, true, true));
        templateStatusList.add(new PhotoTemplateStatusViewModel(template3, false, false, true));

        // Set the items in the ListProperty
        templateStatusListProperty.set(templateStatusList);

        // Initialize showRemainingOnly to false by default
        viewModel.setShowRemainingOnly(false);
    }

    /**
     * Helper method to inject a dependency into the view model using reflection.
     * 
     * @param fieldName the name of the field to inject
     * @param dependency the dependency to inject
     * @throws Exception if the field doesn't exist or can't be accessed
     */
    private void injectDependency(String fieldName, Object dependency) throws Exception {
        Field field = PhotoCubeViewModel.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(viewModel, dependency);
    }

    @Test
    public void testIsLastRemainingTemplate_ReturnsFalse_WhenShowRemainingOnlyIsFalse() {
        // Arrange
        viewModel.setShowRemainingOnly(false);

        // Act
        boolean result = viewModel.isLastRemainingTemplate(template1);

        // Assert
        assertFalse(result, "Should return false when showRemainingOnly is false");
    }

    @Test
    public void testIsLastRemainingTemplate_ReturnsFalse_WhenMultipleTemplatesRemaining() {
        // Arrange
        viewModel.setShowRemainingOnly(true);

        // Act
        boolean result = viewModel.isLastRemainingTemplate(template1);

        // Assert
        assertFalse(result, "Should return false when multiple templates are remaining");
    }

    @Test
    public void testIsLastRemainingTemplate_ReturnsTrue_WhenOnlyOneTemplateRemainingAndMatches() throws Exception {
        // Arrange
        // Create a new list with only one uncaptured template
        ObservableList<PhotoTemplateStatusViewModel> singleTemplateList = FXCollections.observableArrayList();
        singleTemplateList.add(new PhotoTemplateStatusViewModel(template1, false, false, true));
        singleTemplateList.add(new PhotoTemplateStatusViewModel(template2, true, true, true));

        // Get the existing templateStatusList from the viewModel
        Field field = PhotoCubeViewModel.class.getDeclaredField("templateStatusList");
        field.setAccessible(true);
        ListProperty<PhotoTemplateStatusViewModel> templateStatusListProperty = 
            (ListProperty<PhotoTemplateStatusViewModel>) field.get(viewModel);

        // Set the items in the ListProperty
        templateStatusListProperty.set(singleTemplateList);

        viewModel.setShowRemainingOnly(true);

        // Act
        boolean result = viewModel.isLastRemainingTemplate(template1);

        // Assert
        assertTrue(result, "Should return true when only one template is remaining and it matches");
    }

    @Test
    public void testIsLastRemainingTemplate_ReturnsFalse_WhenOnlyOneTemplateRemainingButDoesNotMatch() throws Exception {
        // Arrange
        // Create a new list with only one uncaptured template
        ObservableList<PhotoTemplateStatusViewModel> singleTemplateList = FXCollections.observableArrayList();
        singleTemplateList.add(new PhotoTemplateStatusViewModel(template1, false, false, true));
        singleTemplateList.add(new PhotoTemplateStatusViewModel(template2, true, true, true));

        // Get the existing templateStatusList from the viewModel
        Field field = PhotoCubeViewModel.class.getDeclaredField("templateStatusList");
        field.setAccessible(true);
        ListProperty<PhotoTemplateStatusViewModel> templateStatusListProperty = 
            (ListProperty<PhotoTemplateStatusViewModel>) field.get(viewModel);

        // Set the items in the ListProperty
        templateStatusListProperty.set(singleTemplateList);

        viewModel.setShowRemainingOnly(true);

        // Act
        boolean result = viewModel.isLastRemainingTemplate(template3); // template3 is not in the list

        // Assert
        assertFalse(result, "Should return false when only one template is remaining but it doesn't match");
    }

    @Test
    public void testUpdateFilteredTemplateList_ShowsAllTemplates_WhenShowRemainingOnlyIsFalse() throws Exception {
        // Arrange
        viewModel.setShowRemainingOnly(false);

        // Get the filteredTemplateStatusList using reflection
        Field field = PhotoCubeViewModel.class.getDeclaredField("filteredTemplateStatusList");
        field.setAccessible(true);
        ObservableList<PhotoTemplateStatusViewModel> filteredList = 
            (ObservableList<PhotoTemplateStatusViewModel>) field.get(viewModel);

        // Act - updateFilteredTemplateList is called by setShowRemainingOnly

        // Assert
        assertEquals(3, filteredList.size(), "Should show all templates when showRemainingOnly is false");
        assertTrue(filteredList.stream().anyMatch(vm -> vm.getTemplate() == template1));
        assertTrue(filteredList.stream().anyMatch(vm -> vm.getTemplate() == template2));
        assertTrue(filteredList.stream().anyMatch(vm -> vm.getTemplate() == template3));
    }

    @Test
    public void testUpdateFilteredTemplateList_ShowsOnlyUncapturedTemplates_WhenShowRemainingOnlyIsTrue() throws Exception {
        // Arrange
        viewModel.setShowRemainingOnly(true);

        // Get the filteredTemplateStatusList using reflection
        Field field = PhotoCubeViewModel.class.getDeclaredField("filteredTemplateStatusList");
        field.setAccessible(true);
        ObservableList<PhotoTemplateStatusViewModel> filteredList = 
            (ObservableList<PhotoTemplateStatusViewModel>) field.get(viewModel);

        // Act - updateFilteredTemplateList is called by setShowRemainingOnly

        // Assert
        assertEquals(2, filteredList.size(), "Should show only uncaptured templates when showRemainingOnly is true");
        assertTrue(filteredList.stream().anyMatch(vm -> vm.getTemplate() == template1));
        assertFalse(filteredList.stream().anyMatch(vm -> vm.getTemplate() == template2));
        assertTrue(filteredList.stream().anyMatch(vm -> vm.getTemplate() == template3));
    }

    @Test
    public void testUpdateFilteredTemplateList_ShowsEmptyList_WhenAllTemplatesCapturedAndShowRemainingOnlyIsTrue() throws Exception {
        // Arrange
        // Create a new list with all templates captured
        ObservableList<PhotoTemplateStatusViewModel> allCapturedList = FXCollections.observableArrayList();
        allCapturedList.add(new PhotoTemplateStatusViewModel(template1, true, true, true));
        allCapturedList.add(new PhotoTemplateStatusViewModel(template2, true, true, true));
        allCapturedList.add(new PhotoTemplateStatusViewModel(template3, true, true, true));

        // Get the existing templateStatusList from the viewModel
        Field templateStatusField = PhotoCubeViewModel.class.getDeclaredField("templateStatusList");
        templateStatusField.setAccessible(true);
        ListProperty<PhotoTemplateStatusViewModel> templateStatusListProperty = 
            (ListProperty<PhotoTemplateStatusViewModel>) templateStatusField.get(viewModel);

        // Set the items in the ListProperty
        templateStatusListProperty.set(allCapturedList);

        viewModel.setShowRemainingOnly(true);

        // Get the filteredTemplateStatusList using reflection
        Field filteredField = PhotoCubeViewModel.class.getDeclaredField("filteredTemplateStatusList");
        filteredField.setAccessible(true);
        ObservableList<PhotoTemplateStatusViewModel> filteredList = 
            (ObservableList<PhotoTemplateStatusViewModel>) filteredField.get(viewModel);

        // Act - updateFilteredTemplateList is called by setShowRemainingOnly

        // Assert
        assertEquals(0, filteredList.size(), "Should show empty list when all templates are captured and showRemainingOnly is true");
    }

    @Test
    public void testUpdateFilteredTemplateList_ShowsAllTemplates_WhenNoTemplatesCapturedAndShowRemainingOnlyIsTrue() throws Exception {
        // Arrange
        // Create a new list with no templates captured
        ObservableList<PhotoTemplateStatusViewModel> noneCapturedList = FXCollections.observableArrayList();
        noneCapturedList.add(new PhotoTemplateStatusViewModel(template1, false, false, true));
        noneCapturedList.add(new PhotoTemplateStatusViewModel(template2, false, false, true));
        noneCapturedList.add(new PhotoTemplateStatusViewModel(template3, false, false, true));

        // Get the existing templateStatusList from the viewModel
        Field templateStatusField = PhotoCubeViewModel.class.getDeclaredField("templateStatusList");
        templateStatusField.setAccessible(true);
        ListProperty<PhotoTemplateStatusViewModel> templateStatusListProperty = 
            (ListProperty<PhotoTemplateStatusViewModel>) templateStatusField.get(viewModel);

        // Set the items in the ListProperty
        templateStatusListProperty.set(noneCapturedList);

        viewModel.setShowRemainingOnly(true);

        // Get the filteredTemplateStatusList using reflection
        Field filteredField = PhotoCubeViewModel.class.getDeclaredField("filteredTemplateStatusList");
        filteredField.setAccessible(true);
        ObservableList<PhotoTemplateStatusViewModel> filteredList = 
            (ObservableList<PhotoTemplateStatusViewModel>) filteredField.get(viewModel);

        // Act - updateFilteredTemplateList is called by setShowRemainingOnly

        // Assert
        assertEquals(3, filteredList.size(), "Should show all templates when no templates are captured and showRemainingOnly is true");
        assertTrue(filteredList.stream().anyMatch(vm -> vm.getTemplate() == template1));
        assertTrue(filteredList.stream().anyMatch(vm -> vm.getTemplate() == template2));
        assertTrue(filteredList.stream().anyMatch(vm -> vm.getTemplate() == template3));
    }
}
