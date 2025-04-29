package application.qcreport;



import domain.model.order.Order;
import domain.model.order.photodocument.PhotoDocument;
import domain.model.user.User;

import java.time.LocalDateTime;
import java.util.List;

public class QCReportBuilder {
    Order order;
    List<PhotoDocument> photos;
    User createdBy;
    LocalDateTime createdOn;

    public QCReportBuilder withOrder(Order order) {
        this.order = order;
        return this;
    }

    public QCReportBuilder withPhotos(List<PhotoDocument> photos) {
        this.photos = photos;
        return this;
    }

    public QCReportBuilder withCreatedBy(User createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public QCReportBuilder withCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
        return this;
    }

    public QCReport build() {
        if (order == null || photos == null || createdBy == null) {
            throw new IllegalStateException("Order, photos, and creator must not be null");
        }
        if (createdOn == null) {
            createdOn = LocalDateTime.now(); // Default if not set manually
        }
        return new QCReport(this);
    }
}
