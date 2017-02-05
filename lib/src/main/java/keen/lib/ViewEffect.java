package keen.lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by apple on 05/02/17.
 */

public class ViewEffect extends ImageView{

    private static final long FRAME_DELAY = 1000 / 60;

    private final Matrix mMatrix = new Matrix();

    private TransitionGenerator mTransGen = new RandomTransitionGenerator();

    private TransitionListener mTransitionListener;

    private Transition mCurrentTrans;

    private final RectF mViewportRect = new RectF();
    private RectF mDrawableRect;

    private long mElapsedTime;

    private long mLastFrameTime;

    private boolean mPaused;

    private boolean mInitialized;


    public ViewEffect(Context context) {
        this(context, null);
    }


    public ViewEffect(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public ViewEffect(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mInitialized = true;
        // Attention to the super call here!
        super.setScaleType(ImageView.ScaleType.MATRIX);
    }


    @Override
    public void setScaleType(ScaleType scaleType) {
        // It'll always be center-cropped by default.
    }


    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        /* When not visible, onDraw() doesn't get called,
           but the time elapses anyway. */
        switch (visibility) {
            case VISIBLE:
                resume();
                break;
            default:
                pause();
                break;
        }
    }


    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        handleImageChange();
    }


    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        handleImageChange();
    }


    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        handleImageChange();
    }


    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        handleImageChange();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        restart();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        Drawable d = getDrawable();
        if (!mPaused && d != null) {
            if (mDrawableRect.isEmpty()) {
                updateDrawableBounds();
            } else if (hasBounds()) {
                if (mCurrentTrans == null) { // Starting the first transition.
                    startNewTransition();
                }

                if (mCurrentTrans.getDestinyRect() != null) { // If null, it's supposed to stop.
                    mElapsedTime += System.currentTimeMillis() - mLastFrameTime;
                    RectF currentRect = mCurrentTrans.getInterpolatedRect(mElapsedTime);

                    float widthScale = mDrawableRect.width() / currentRect.width();
                    float heightScale = mDrawableRect.height() / currentRect.height();
                    // Scale to make the current rect match the smallest drawable dimension.
                    float currRectToDrwScale = Math.min(widthScale, heightScale);
                    // Scale to make the current rect match the viewport bounds.
                    float vpWidthScale = mViewportRect.width() / currentRect.width();
                    float vpHeightScale = mViewportRect.height() / currentRect.height();
                    float currRectToVpScale = Math.min(vpWidthScale, vpHeightScale);
                    // Combines the two scales to fill the viewport with the current rect.
                    float totalScale = currRectToDrwScale * currRectToVpScale;

                    float translX = totalScale * (mDrawableRect.centerX() - currentRect.left);
                    float translY = totalScale * (mDrawableRect.centerY() - currentRect.top);

                    /* Performs matrix transformations to fit the content
                       of the current rect into the entire view. */
                    mMatrix.reset();
                    mMatrix.postTranslate(-mDrawableRect.width() / 2, -mDrawableRect.height() / 2);
                    mMatrix.postScale(totalScale, totalScale);
                    mMatrix.postTranslate(translX, translY);

                    setImageMatrix(mMatrix);

                    // Current transition is over. It's time to start a new one.
                    if (mElapsedTime >= mCurrentTrans.getDuration()) {
                        fireTransitionEnd(mCurrentTrans);
                        startNewTransition();
                    }
                } else { // Stopping? A stop event has to be fired.
                    fireTransitionEnd(mCurrentTrans);
                }
            }
            mLastFrameTime = System.currentTimeMillis();
            postInvalidateDelayed(FRAME_DELAY);
        }
        super.onDraw(canvas);
    }


    private void startNewTransition() {
        if (!hasBounds()) {
            return; // Can't start transition if the drawable has no bounds.
        }
        mCurrentTrans = mTransGen.generateNextTransition(mDrawableRect, mViewportRect);
        mElapsedTime = 0;
        mLastFrameTime = System.currentTimeMillis();
        fireTransitionStart(mCurrentTrans);
    }

    public void restart() {
        int width = getWidth();
        int height = getHeight();

        if (width == 0 || height == 0) {
            return; // Can't call restart() when view area is zero.
        }

        updateViewport(width, height);
        updateDrawableBounds();

        startNewTransition();
    }

    private boolean hasBounds() {
        return !mViewportRect.isEmpty();
    }

    private void fireTransitionStart(Transition transition) {
        if (mTransitionListener != null && transition != null) {
            mTransitionListener.onTransitionStart(transition);
        }
    }

    private void fireTransitionEnd(Transition transition) {
        if (mTransitionListener != null && transition != null) {
            mTransitionListener.onTransitionEnd(transition);
        }
    }

    public void setTransitionGenerator(TransitionGenerator transgen) {
        mTransGen = transgen;
        startNewTransition();
    }

    private void updateViewport(float width, float height) {
        mViewportRect.set(0, 0, width, height);
    }

    private void updateDrawableBounds() {
        if (mDrawableRect == null) {
            mDrawableRect = new RectF();
        }
        Drawable d = getDrawable();
        if (d != null && d.getIntrinsicHeight() > 0 && d.getIntrinsicWidth() > 0) {
            mDrawableRect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        }
    }

    private void handleImageChange() {
        updateDrawableBounds();
        /* Don't start a new transition if this event
         was fired during the super constructor execution.
         The view won't be ready at this time. Also,
         don't start it if this view size is still unknown. */
        if (mInitialized) {
            startNewTransition();
        }
    }


    public void setTransitionListener(TransitionListener transitionListener) {
        mTransitionListener = transitionListener;
    }

    public void pause() {
        mPaused = true;
    }

    public void resume() {
        mPaused = false;
        // This will make the animation to continue from where it stopped.
        mLastFrameTime = System.currentTimeMillis();
        invalidate();
    }

    public interface TransitionListener {
        public void onTransitionStart(Transition transition);

        public void onTransitionEnd(Transition transition);
    }
}
