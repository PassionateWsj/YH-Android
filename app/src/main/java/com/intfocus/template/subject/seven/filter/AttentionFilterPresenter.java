package com.intfocus.template.subject.seven.filter;

import android.content.Context;

import com.intfocus.template.model.response.scanner.StoreItem;

import java.util.List;

/**
 * ****************************************************
 *
 * @author JamesWong
 *         created on: 17/08/22 上午09:59
 *         e-mail: PassionateWsj@outlook.com
 *         name:
 *         desc:
 *         ****************************************************
 */

public class AttentionFilterPresenter {
    private Context mContext;
    private AttentionFilterView mAttentionFilterView;
    private AttentionFilterImpl mAttentionFilter;

    public AttentionFilterPresenter(Context context, AttentionFilterView attentionFilterView) {
        mContext = context;
        mAttentionFilterView = attentionFilterView;
        mAttentionFilter = new AttentionFilterImpl();
    }

    public void loadData() {
        loadData("");
    }

    public void loadData(String keyWord) {
        mAttentionFilter.loadData(mContext, keyWord, new OnAttentionFilterResultListener() {
            @Override
            public void onResultFailure(Throwable e) {
                mAttentionFilterView.onResultFailure(e);
            }

            @Override
            public void onResultSuccess(List<StoreItem> data) {
                mAttentionFilterView.onResultSuccess(data);
            }
        });
    }
}
