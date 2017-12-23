package corp.kairos.adamastor.AllApps;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

public class AppDetail implements Comparable{
    private String label;
    private String name;
    private Drawable icon;

    public AppDetail(String label, String name, Drawable icon) {
        this.label = label;
        this.name = name;
        this.icon = icon;
    }

    public AppDetail(AppDetail appDetail) {
        this.label = appDetail.getLabel();
        this.name = appDetail.getName();
        this.icon = appDetail.getIcon();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public AppDetail clone() {
        return new AppDetail(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppDetail appDetail = (AppDetail) o;

        if (label != null ? !label.equals(appDetail.label) : appDetail.label != null) return false;
        if (name != null ? !name.equals(appDetail.name) : appDetail.name != null) return false;
        return icon != null ? icon.equals(appDetail.icon) : appDetail.icon == null;
    }

    @Override
    public int hashCode() {
        int result = label != null ? label.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (icon != null ? icon.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        if (this == o) return 0;
        if (getClass() != o.getClass()) return o.hashCode() > this.hashCode() ? 1 : 0;

        AppDetail appDetail = (AppDetail) o;

        return appDetail.getLabel().compareTo(this.getLabel());
    }
}
