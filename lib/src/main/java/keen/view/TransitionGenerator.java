package keen.view;

import android.graphics.RectF;

/**
 * Created by apple on 05/02/17.
 */

public interface TransitionGenerator {
    public Transition generateNextTransition(RectF drawableBounds, RectF viewport);

}
