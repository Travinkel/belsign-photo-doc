package domain.model.order.photodocument;

public enum ApprovalStatus {
    PENDING,
    APPROVED,
    REJECTED;

    public static ApprovalStatus fromString(String status) {
        for (ApprovalStatus approvalStatus : ApprovalStatus.values()) {
            if (approvalStatus.name().equalsIgnoreCase(status)) {
                return approvalStatus;
            }
        }
        throw new IllegalArgumentException("Unknown approval status: " + status);
    }
}
