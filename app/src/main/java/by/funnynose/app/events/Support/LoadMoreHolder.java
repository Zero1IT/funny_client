package by.funnynose.app.events.Support;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.example.funnynose.R;

public class LoadMoreHolder extends LoadMoreView {
    @Override
    public int getLayoutId() {
        return R.layout.load_more_view;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.loading_view;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.loading_fail;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.loading_end;
    }
}
