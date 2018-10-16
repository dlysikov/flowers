package lu.luxtrust.flowers.model;

import java.util.ArrayList;
import java.util.List;

public class ImportResult<T> {
    private int successful;
    private int failed;
    private List<T> failedDetails;

    public ImportResult() {
        this.failedDetails = new ArrayList<>();
    }

    public void incSuccessFul() {
        this.successful++;
    }

    public void incFailed() {
        this.failed++;
    }

    public void incSuccessFul(int inc) {
        this.successful += inc;
    }

    public void incFailed(int inc) {
        this.failed += inc;
    }

    public void addFailedDetails(T details) {
        this.failedDetails.add(details);
    }

    public void addFailedDetails(List<T> details) {
        this.failedDetails.addAll(details);
    }

    public int getSuccessful() {
        return successful;
    }

    public int getFailed() {
        return failed;
    }

    public List<T> getFailedDetails() {
        return failedDetails;
    }
}
