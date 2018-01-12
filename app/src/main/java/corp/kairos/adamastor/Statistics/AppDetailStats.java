package corp.kairos.adamastor.Statistics;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import corp.kairos.adamastor.AppDetail;


public class AppDetailStats extends AppDetail {
    private long totalTime;

    public AppDetailStats(String label, String name, Drawable icon, long totalTime) {
        super(label, name, icon);
        this.totalTime = totalTime;
    }

    public AppDetailStats(AppDetailStats appDetailStats) {
        super(appDetailStats.getLabel(), appDetailStats.getName(), appDetailStats.getIcon());
        this.totalTime = totalTime;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AppDetailStats that = (AppDetailStats) o;

        return totalTime == that.totalTime;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (totalTime ^ (totalTime >>> 32));
        return result;
    }

    public AppDetailStats clone(){
        return new AppDetailStats(this);
    }

    public int compareTo(@NonNull Object o) {
        if (this == o) return 0;
        if (getClass() != o.getClass()) return o.hashCode() > this.hashCode() ? 1 : 0;

        AppDetailStats appDetail = (AppDetailStats) o;

        return new Long(appDetail.getTotalTime()).compareTo(this.getTotalTime());
    }
}
