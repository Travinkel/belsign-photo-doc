package unit.domain.model.order.photodocument;

import domain.model.order.photodocument.ImagePath;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImagePathTest {

    @Test
    void validPathShouldBeAccepted() {
        ImagePath path = new ImagePath("/images/photo.jpg");
        assertEquals("/images/photo.jpg", path.getPath());
    }

    @Test
    void nullPathShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new ImagePath(null));
    }

    @Test
    void emptyPathShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new ImagePath(""));
    }

    @Test
    void blankPathShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new ImagePath("   "));
    }

    @Test
    void equalPathsShouldBeEqual() {
        ImagePath path1 = new ImagePath("/images/photo.jpg");
        ImagePath path2 = new ImagePath("/images/photo.jpg");
        
        assertEquals(path1, path2);
        assertEquals(path1.hashCode(), path2.hashCode());
    }

    @Test
    void differentPathsShouldNotBeEqual() {
        ImagePath path1 = new ImagePath("/images/photo1.jpg");
        ImagePath path2 = new ImagePath("/images/photo2.jpg");
        
        assertNotEquals(path1, path2);
    }
}