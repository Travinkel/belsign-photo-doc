package unit.domain.model.order.photo;

import com.belman.belsign.domain.model.order.photodocument.ImagePath;
import com.belman.belsign.domain.model.order.photodocument.PhotoDocument;
import com.belman.belsign.domain.model.order.photodocument.PhotoId;
import com.belman.belsign.domain.model.shared.Timestamp;
import com.belman.belsign.domain.model.user.Username;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ImagePathTest {
    /**
     * BASIC TEST CASES
     */
    @Test
    void testValidRelativePathInitialization() {
        Path relativePath = Path.of("images/sample.jpg");
        ImagePath imagePath = new ImagePath(relativePath);
        assertNotNull(imagePath, "ImagePath should be initialized successfully with a relative path.");
        assertEquals("sample.jpg", imagePath.filename(), "Filename should extract correctly for relative path.");
    }

    @Test
    void testValidAbsolutePathInitialization() {
        Path absolutePath = Path.of("C:/images/sample.jpg");
        ImagePath imagePath = new ImagePath(absolutePath);
        assertNotNull(imagePath, "ImagePath should be initialized successfully with an absolute path.");
        assertEquals(absolutePath.toString(), imagePath.toString(), "toString should return the correct absolute path.");
        assertEquals("sample.jpg", imagePath.filename(), "Filename should match the file from the absolute path.");
    }

    /**
     * EDGE CASE TESTS
     */
    @Test
    void testNullPathThrowsException() {
        assertThrows(NullPointerException.class, () -> new ImagePath(null), "Creating an ImagePath with null path should throw NullPointerException.");
    }

    @Test
    void testEmptyDirectoryPathReturnsNullFilename() {
        Path directoryPath = Path.of("images/");
        ImagePath imagePath = new ImagePath(directoryPath);
        assertNull(imagePath.filename(), "Filename should be null for a directory path.");
    }

    @Test
    void testRootPathReturnsNullFilename() {
        Path rootPath = Path.of("/");
        ImagePath imagePath = new ImagePath(rootPath);

        assertNull(imagePath.filename(), "Filename should be null for a root directory path.");
    }

    @Test
    void testNestedDirectoryPathReturnsNullFilename() {
        Path nestedDirectoryPath = Path.of("images/nested/");
        ImagePath imagePath = new ImagePath(nestedDirectoryPath);

        assertNull(imagePath.filename(), "Filename should be null for a nested directory path ending with '/'");
    }

    @Test
    void testNoExtensionFilename() {
        Path pathWithoutExtension = Path.of("images/sample");
        ImagePath imagePath = new ImagePath(pathWithoutExtension);
        assertEquals("sample", imagePath.filename(), "Filename should match the full name if no extension is present.");
    }

    @Test
    void testHiddenFile() {
        Path hiddenFilePath = Path.of("images/.hiddenfile.jpg");
        ImagePath imagePath = new ImagePath(hiddenFilePath);
        assertEquals(".hiddenfile.jpg", imagePath.filename(), "Filename should correctly identify hidden files.");
    }

    @Test
    void testFilenameWithSpecialCharacters() {
        Path specialPath = Path.of("images/@#$_special-file!.jpg");
        ImagePath imagePath = new ImagePath(specialPath);
        assertEquals("@#$_special-file!.jpg", imagePath.filename(), "Filename should correctly handle paths with special characters.");
    }

    @Test
    void testLongFilePathHandling() {
        Path longPath = Path.of("images/subdir1/subdir2/dir_with_long_name/sample-verylongfilename_with_multiple_words_and_numbers123456.txt");
        ImagePath imagePath = new ImagePath(longPath);
        assertEquals("sample-verylongfilename_with_multiple_words_and_numbers123456.txt", imagePath.filename(),
                "Filename should correctly extract from long file paths.");
    }

    @Test
    void testUnixStylePathHandling() {
        Path unixPath = Path.of("/home/user/images/file.txt");
        ImagePath imagePath = new ImagePath(unixPath);
        assertEquals("file.txt", imagePath.filename(), "Filename should correctly extract from Unix-style paths.");
    }

    @Test
    void testWindowsStylePathHandling() {
        Path windowsPath = Path.of("C:\\Program Files\\MyApp\\data\\image.bmp");
        ImagePath imagePath = new ImagePath(windowsPath);
        assertEquals("image.bmp", imagePath.filename(), "Filename should correctly extract from Windows-style paths.");
    }

    /**
     * FUNCTIONAL TESTS
     */
    @Test
    void testToStringReturnsCorrectPath() {
        Path testPath = Path.of("data/images/test.jpg");
        ImagePath imagePath = new ImagePath(testPath);
        assertEquals(testPath.toString(), imagePath.toString(), "toString() should return the correct path as a string.");
    }

    @Test
    void testFilenameWithMultipleDots() {
        Path pathWithMultipleDots = Path.of("images/archive/file.backup.20230520.txt");
        ImagePath imagePath = new ImagePath(pathWithMultipleDots);
        assertEquals("file.backup.20230520.txt", imagePath.filename(), "Filename should correctly handle names with multiple dots.");
    }

    @Test
    void testPathEnclosingSpecialCharacters() {
        Path enclosingPath = Path.of("C:\\my[project]⧹resource\\image@versão+.jpg");
        ImagePath imagePath = new ImagePath(enclosingPath);
        assertEquals("image@versão+.jpg", imagePath.filename(),
                "Filename parsing should support special and Unicode characters correctly.");
    }

    /**
     * INTEGRATION TESTS WITH PHOTODOCUMENT
     */
    @Test
    void testPhotoDocumentWithValidImagePath() {
        ImagePath imagePath = new ImagePath(Path.of("images/photo.jpg"));
        PhotoDocument photoDocument = new PhotoDocument(
                new PhotoId(UUID.randomUUID()),
                imagePath,
                new Timestamp(Instant.now()),
                new Username("testuser")
        );
        assertNotNull(photoDocument, "PhotoDocument should initialize correctly with a valid ImagePath.");
        assertEquals(imagePath, photoDocument.getPath(), "PhotoDocument should return the correct ImagePath.");
    }

    @Test
    void testPhotoDocumentWithNullImagePathThrowsException() {
        assertThrows(NullPointerException.class, () -> new PhotoDocument(
                new PhotoId(UUID.randomUUID()),
                null,
                new Timestamp(Instant.now()),
                new Username("nulluser")
        ), "PhotoDocument should throw NullPointerException when initialized with a null ImagePath.");
    }

    @Test
    void testValidIntegrationWithMockedImagePath() {
        ImagePath mockedPath = new ImagePath(Path.of("mocked_images/mock.jpg"));
        PhotoDocument mockPhotoDocument = new PhotoDocument(
                new PhotoId(UUID.randomUUID()),
                mockedPath,
                new Timestamp(Instant.now()),
                new Username("mock_user")
        );
        assertEquals("mock.jpg", mockPhotoDocument.getPath().filename(),
                "Integration with PhotoDocument should return the mocked filename correctly.");
    }
}
