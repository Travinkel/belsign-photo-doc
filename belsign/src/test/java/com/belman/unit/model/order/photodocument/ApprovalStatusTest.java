package com.belman.unit.model.order.photodocument;

import com.belman.domain.enums.ApprovalStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ApprovalStatusTest {

    @Test
    void fromStringShouldReturnCorrectEnum() {
        assertEquals(ApprovalStatus.PENDING, ApprovalStatus.fromString("pending"));
        assertEquals(ApprovalStatus.APPROVED, ApprovalStatus.fromString("APPROVED"));
        assertEquals(ApprovalStatus.REJECTED, ApprovalStatus.fromString("ReJeCtEd"));
    }

    @Test
    void fromStringShouldThrowExceptionOnInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> ApprovalStatus.fromString("invalid-status"));
        assertThrows(IllegalArgumentException.class, () -> ApprovalStatus.fromString(""));
        assertThrows(IllegalArgumentException.class, () -> ApprovalStatus.fromString("123"));
    }
}