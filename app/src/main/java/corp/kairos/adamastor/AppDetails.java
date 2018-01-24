package corp.kairos.adamastor;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

public class AppDetails implements Comparable{
    private String label;
    private String packageName;
    private Drawable icon;
    private Long usageStatistics;

    public AppDetails(String label, String packageName, Drawable icon) {
        this.label = label;
        this.packageName = packageName;
        this.icon = icon;
        this.usageStatistics = 0L;
    }

    public AppDetails(AppDetails appDetail) {
        this.label = appDetail.getLabel();
        this.packageName = appDetail.getPackageName();
        this.icon = appDetail.getIcon();
        this.usageStatistics = appDetail.getUsageStatistics();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public Long getUsageStatistics() {
        return usageStatistics;
    }

    public void setUsageStatistics(Long usageStatistics) {
        this.usageStatistics = usageStatistics;
    }


    public AppDetails clone() {
        return new AppDetails(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppDetails that = (AppDetails) o;

        if (!label.equals(that.label)) return false;
        if (!packageName.equals(that.packageName)) return false;
        return usageStatistics != null ? usageStatistics.equals(that.usageStatistics) : that.usageStatistics == null;
    }

    @Override
    public int hashCode() {
        int result = label.hashCode();
        result = 31 * result + packageName.hashCode();
        result = 31 * result + (usageStatistics != null ? usageStatistics.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        if (this == o) return 0;
        if (getClass() != o.getClass()) return o.hashCode() > this.hashCode() ? 1 : 0;

        AppDetails appDetail = (AppDetails) o;

        return this.getLabel().toLowerCase().compareTo(appDetail.getLabel().toLowerCase());
    }
}
