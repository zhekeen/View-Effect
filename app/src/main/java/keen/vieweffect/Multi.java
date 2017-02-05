package keen.vieweffect;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ViewSwitcher;

import keen.lib.Transition;
import keen.lib.ViewEffect;

/**
 * Created by apple on 05/02/17.
 */

public class Multi extends AppCompatActivity implements ViewEffect.TransitionListener {

    private static final int TRANSITIONS_TO_SWITCH = 3;

    private ViewSwitcher mViewSwitcher;

    private int mTransitionsCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multi);

        mViewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);

        ViewEffect img1 = (ViewEffect) findViewById(R.id.img1);
        img1.setTransitionListener(this);

        ViewEffect img2 = (ViewEffect) findViewById(R.id.img2);
        img2.setTransitionListener(this);
    }

    @Override
    public void onTransitionStart(Transition transition) {

    }

    @Override
    public void onTransitionEnd(Transition transition) {
        mTransitionsCount++;
        if (mTransitionsCount == TRANSITIONS_TO_SWITCH) {
            mViewSwitcher.showNext();
            mTransitionsCount = 0;
        }
    }
}
