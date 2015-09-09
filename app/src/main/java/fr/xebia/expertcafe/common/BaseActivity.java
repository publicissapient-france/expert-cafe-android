package fr.xebia.expertcafe.common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

public class BaseActivity extends AppCompatActivity {

    private final int layoutId;
    private final boolean bind;

    public BaseActivity(int layoutId, boolean bind) {
        this.layoutId = layoutId;
        this.bind = bind;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId);
        if (bind) {
            ButterKnife.bind(this);
        }
    }

}
