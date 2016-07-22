package com.newunion.newunionapp.ui.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * <p> Add drawing between items on RecycleView
 * </p>
 * Created by Nguyen Ngoc Hoang on 7/20/2016.
 *
 * @author Nguyen Ngoc Hoang (www.hoangvnit.com)
 */
public class ItemSpaceDecoration extends RecyclerView.ItemDecoration {
    private int mSpace;

    public ItemSpaceDecoration(int space) {
        mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = mSpace;
        outRect.left = mSpace;
        outRect.right = mSpace;
    }
}
