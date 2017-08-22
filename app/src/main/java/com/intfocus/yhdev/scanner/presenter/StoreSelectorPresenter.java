package com.intfocus.yhdev.scanner.presenter;

import android.content.Context;

import com.intfocus.yhdev.data.response.scanner.StoreItem;
import com.intfocus.yhdev.scanner.model.OnStoreSelectorResultListener;
import com.intfocus.yhdev.scanner.model.StoreSelectorImpl;
import com.intfocus.yhdev.scanner.view.StoreSelectorView;

import java.util.List;

/**
 * ****************************************************
 * author: JamesWong
 * created on: 17/08/22 上午09:59
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */

public class StoreSelectorPresenter {
    private Context mContext;
    private StoreSelectorView mStoreSelectorView;
    private StoreSelectorImpl mStoreSelector;

    public StoreSelectorPresenter(Context context, StoreSelectorView storeSelectorView) {
        mContext = context;
        mStoreSelectorView = storeSelectorView;
        mStoreSelector = new StoreSelectorImpl();
    }

    public void loadData() {
        loadData("");
    }

    public void loadData(String keyWord) {
        mStoreSelector.loadData(mContext, keyWord, new OnStoreSelectorResultListener() {
            @Override
            public void onResultFailure(Throwable e) {
                mStoreSelectorView.onResultFailure(e);
            }

            @Override
            public void onResultSuccess(List<StoreItem> data) {
                mStoreSelectorView.onResultSuccess(data);
            }
        });
    }
}
