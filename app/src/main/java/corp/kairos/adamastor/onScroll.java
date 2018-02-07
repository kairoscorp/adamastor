package corp.kairos.adamastor;

import android.support.design.widget.FloatingActionButton;
import android.widget.AbsListView;

public class onScroll implements AbsListView.OnScrollListener {
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {}

    private int lastVisibleItem = 0;
    private int lastY = 0;
    FloatingActionButton btn;

    public onScroll(FloatingActionButton btn) {
        this.btn = btn;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int top = 0;
        if(view.getChildAt(0) != null){
            top = view.getChildAt(0).getTop();
        }

        if(firstVisibleItem > lastVisibleItem){
            //scroll down
            btn.hide();
        }else if(firstVisibleItem < lastVisibleItem){
            //scroll up
            btn.show();
        }else{
            if(top < lastY){
                //scroll down
                btn.hide();
            }else if(top > lastY){
                //scroll up
                btn.show();
            }
        }

        lastVisibleItem = firstVisibleItem;
        lastY = top;
    }
}
