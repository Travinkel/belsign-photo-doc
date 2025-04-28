package unit.domain.model.order.photodocument;

import com.belman.belsign.domain.model.order.photodocument.PhotoAngle;
import com.belman.belsign.domain.model.order.photodocument.PhotoAngle.NamedAngle;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PhotoAngleTest {

    @Test
    void validAngleShouldBeCreated() {
        PhotoAngle angle = new PhotoAngle(90.0);
        assertEquals(90.0, angle.getDegrees());
        assertFalse(angle.isNamedAngle());
        assertNull(angle.getNamedAngle());
    }

    @Test
    void zeroAngleShouldBeValid() {
        PhotoAngle angle = new PhotoAngle(0.0);
        assertEquals(0.0, angle.getDegrees());
    }

    @Test
    void almostMaxAngleShouldBeValid() {
        PhotoAngle angle = new PhotoAngle(359.9);
        assertEquals(359.9, angle.getDegrees());
    }

    @Test
    void negativeAngleShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new PhotoAngle(-1.0));
    }

    @Test
    void tooLargeAngleShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new PhotoAngle(360.0));
    }

    @Test
    void equalAnglesShouldBeEqual() {
        PhotoAngle angle1 = new PhotoAngle(45.0);
        PhotoAngle angle2 = new PhotoAngle(45.0);
        assertEquals(angle1, angle2);
        assertEquals(angle1.hashCode(), angle2.hashCode());
    }

    @Test
    void differentAnglesShouldNotBeEqual() {
        PhotoAngle angle1 = new PhotoAngle(45.0);
        PhotoAngle angle2 = new PhotoAngle(90.0);
        assertNotEquals(angle1, angle2);
    }

    @Test
    void namedAngleShouldBeCreated() {
        PhotoAngle angle = new PhotoAngle(NamedAngle.FRONT);
        assertEquals(0.0, angle.getDegrees());
        assertTrue(angle.isNamedAngle());
        assertEquals(NamedAngle.FRONT, angle.getNamedAngle());
    }

    @Test
    void namedAngleShouldHaveCorrectDegrees() {
        assertEquals(0.0, new PhotoAngle(NamedAngle.FRONT).getDegrees());
        assertEquals(90.0, new PhotoAngle(NamedAngle.RIGHT).getDegrees());
        assertEquals(180.0, new PhotoAngle(NamedAngle.BACK).getDegrees());
        assertEquals(270.0, new PhotoAngle(NamedAngle.LEFT).getDegrees());
    }

    @Test
    void namedAngleCannotBeNull() {
        assertThrows(NullPointerException.class, () -> new PhotoAngle(null));
    }

    @Test
    void displayNameShouldBeCorrect() {
        assertEquals("FRONT", new PhotoAngle(NamedAngle.FRONT).getDisplayName());
        assertEquals("45.5°", new PhotoAngle(45.5).getDisplayName());
    }

    @Test
    void namedAngleAndEquivalentDegreesShouldBeEqual() {
        PhotoAngle namedAngle = new PhotoAngle(NamedAngle.RIGHT);
        PhotoAngle degreeAngle = new PhotoAngle(90.0);
        assertEquals(namedAngle, degreeAngle);
        assertEquals(namedAngle.hashCode(), degreeAngle.hashCode());
    }
}
