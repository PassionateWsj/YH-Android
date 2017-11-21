package com.intfocus.syp_template.business.subject.template.one;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.intfocus.syp_template.R;
import com.intfocus.syp_template.business.subject.template.one.table.ModularOneUnitTablesContModeFragment;
import com.intfocus.syp_template.business.subject.template.one.table.TableImpl;
import com.intfocus.syp_template.business.subject.template.one.table.TablePresenter;
import com.intfocus.syp_template.general.data.TempSubData;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 模板一弹出显示的子Table页面
 */
public class ModularOneSubTableActivity extends AppCompatActivity {

    private static final String SU_ROOT_ID = "suRootID";
    private static final String ARG_INDEX = "index";

    @ViewInject(R.id.tv_subtable_name)
    private TextView title;

    @ViewInject(R.id.fl_subtable_container)
    private FrameLayout fl_container;
    private String subData;

    @ViewInject(R.id.imgBtn_ColsPopupWindow_bannerInfo)
    private ImageButton imgBtn_Cols;
    public int suRootID;
    private int mIndex;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modular_one_subtable);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        x.view().inject(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Slide().setDuration(500));
            getWindow().setExitTransition(new Slide().setDuration(500));
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String title = bundle.getString("Title");
            suRootID = bundle.getInt(SU_ROOT_ID);
            mIndex = bundle.getInt(ARG_INDEX);
            this.title.setText(title);
            if (!TempSubData.hasData(mIndex)) {
                finish();
            }

            ModularOneUnitTablesContModeFragment toFragment = ModularOneUnitTablesContModeFragment.newInstance(suRootID, mIndex);
            new TablePresenter(TableImpl.getInstance(), toFragment);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.replace(R.id.fl_subtable_container, toFragment);
            ft.commit();
        }
    }

    @Event(R.id.imgBtn_ColsPopupWindow_bannerInfo)
    private void onViewClick(View view) {
        ActivityCompat.finishAfterTransition(this);
    }
}
