package corp.kairos.adamastor;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.Serializable;

public class AppDetails implements Comparable,Serializable,Parcelable{
    private String label;
    private String packageName;
    private Drawable icon;
    private Long usageStatistics;
    private boolean system;

    public AppDetails(String label, String packageName, Drawable icon, boolean system) {
        this.label = label;
        this.packageName = packageName;
        this.icon = icon;
        this.usageStatistics = 0L;
        this.system= system;
    }

    public AppDetails(AppDetails appDetail) {
        this.label = appDetail.getLabel();
        this.packageName = appDetail.getPackageName();
        this.icon = appDetail.getIcon();
        this.usageStatistics = appDetail.getUsageStatistics();
        this.system = appDetail.isSystem();
    }

    public AppDetails(PackageManager pm, String packageName) throws PackageManager.NameNotFoundException {
        ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);

        this.label = (String) pm.getApplicationLabel(ai);
        this.packageName = ai.packageName;
        this.icon = pm.getApplicationIcon(ai);
    }

    protected AppDetails(Parcel in) {
        this.label = in.readString();
        this.packageName = in.readString();
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

    public boolean isSystem() {
        return system;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(label);
        dest.writeString(packageName);
    }

    public static final Parcelable.Creator<AppDetails> CREATOR = new Parcelable.Creator<AppDetails>() {
        @Override
        public AppDetails createFromParcel(Parcel in) {
            return new AppDetails(in);
        }

        @Override
        public AppDetails[] newArray(int size) {
            return new AppDetails[size];
        }
    };

    public void loadIcon(PackageManager packageManager) throws PackageManager.NameNotFoundException {
        this.icon = packageManager.getApplicationIcon(this.packageName);
    }
}
