package com.belman.belsign.application.qcreport;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class QCReportViewModel {
    private final ObjectProperty<QCReport> qcReport = new SimpleObjectProperty<>();

    public ObjectProperty<QCReport> qcReportProperty() { return qcReport;}
    public void setQCReport(QCReport qcReport) { this.qcReport.set(qcReport); }
}
