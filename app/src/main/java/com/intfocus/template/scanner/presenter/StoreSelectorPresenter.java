package com.intfocus.template.scanner.presenter;

import android.content.Context;

import com.intfocus.template.scanner.model.OnStoreSelectorResultListener;
import com.intfocus.template.scanner.model.StoreSelectorImpl;
import com.intfocus.template.scanner.view.StoreSelectorView;
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
