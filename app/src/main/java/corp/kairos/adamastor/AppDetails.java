package corp.kairos.adamastor;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

public class AppDetails implements Comparable{
    private String label;
    private String name;
    private Drawable icon;
    private Long usageStatistics;

    public AppDetails(String label, String name, Drawable icon) {
        this.label = label;
        this.name = name;
        this.icon = icon;
        this.usageStatistics = 0L;
    }

    public AppDetails(AppDetails appDetail) {
        this.label = appDetail.getLabel();
        this.name = appDetail.getPackageName();
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
        return name;
    }

    public void setPackageName(String name) {
        this.name = name;
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

        AppDetails appDetail = (AppDetails) o;

        if (label != null ? !label.equals(appDetail.label) : appDetail.label != null) return false;
        if (name != null ? !name.equals(appDetail.name) : appDetail.name != null) return false;
        if (usageStatistics != null ? !usageStatistics.equals(appDetail.usageStatistics) : appDetail.usageStatistics != null) return false;
        return icon != null ? icon.equals(appDetail.icon) : appDetail.icon == null;
    }

    @Override
    public int hashCode() {
        int result = label != null ? label.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (usageStatistics != null ? usageStatistics.hashCode() : 0);
        result = 31 * result + (icon != null ? icon.hashCode() : 0);
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
