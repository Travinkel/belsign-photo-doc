package domain.model.order;

import java.util.Objects;
import java.util.regex.Pattern;

public record OrderNumber(String value) {
    private static final Pattern VALID_PATTERN = Pattern.compile("\\d{1,2}/\\d{2}-\\d{6}-\\d{8}");

    public OrderNumber {
        Objects.requireNonNull(value, "OrderNumber must not be null");
        if (!VALID_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("OrderNumber must match the pattern 'XX/XX-XXXXXX-XXXXXXXX'");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
