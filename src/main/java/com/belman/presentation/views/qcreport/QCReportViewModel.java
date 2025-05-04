package com.belman.presentation.views.qcreport;

import com.belman.application.qcreport.QCReport;
import com.belman.presentation.core.BaseViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * ViewModel for QC Report view.
 * This class follows the MVVM pattern and provides properties for binding to the view.
 */
public class QCReportViewModel extends BaseViewModel {
    private final ObjectProperty<QCReport> qcReport = new SimpleObjectProperty<>();

    /**
     * Gets the QC report property.
     * 
     * @return the QC report property
     */
    public ObjectProperty<QCReport> qcReportProperty() { 
        return qcReport;
    }

    /**
     * Sets the QC report.
     * 
     * @param qcReport the QC report to set
     */
    public void setQCReport(QCReport qcReport) { 
        this.qcReport.set(qcReport); 
    }
}