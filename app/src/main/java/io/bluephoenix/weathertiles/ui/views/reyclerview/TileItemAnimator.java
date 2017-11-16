package io.bluephoenix.weathertiles.ui.views.reyclerview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import io.bluephoenix.weathertiles.util.AnimListener;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class TileItemAnimator extends DefaultItemAnimator
{
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR =
            new AccelerateInterpolator();

    /**
     * Called when notifyItemChanged or RangeChanged is used and the view holder
     * that was changed is visible on the screen at the time.
     * <p>
     * @param oldHolder copy of the original view holder to use in cross fading.
     * @param newHolder describes an item view and metadata about its place within the
     *                  RecyclerView.
     * @param fromX     left of the old view holder
     * @param fromY     top of the old view holder
     * @param toX       left of the new view holder
     * @param toY       top of the new view holder
     * @return          true if a later call to runPendingAnimations() is requested,
     *                  false otherwise.
     */
    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder,
                                 RecyclerView.ViewHolder newHolder,
                                 int fromX, int fromY, int toX, int toY)
    {
        if(oldHolder == newHolder)
        {
            //Don't know how to run change animations when the same view holder
            //is re-used. Run a move animation to handle position changes.
            dispatchChangeFinished(oldHolder, true);
        }

        animateChangeImpl(oldHolder, newHolder);
        return false;
    }

    /**
     * You must check to make sure both views are not null since we are doing a cross fade.
     * Do not use an animatorSet listener to dispatch both the new and old holder. It will
     * get multiple times and cause glitches.
     * <p>
     * If you don't dispatch the holders correctly meaning ONLY ONCE for each holder
     * then you will get :
     *      isRecyclable decremented below 0: unmatched pair of setIsRecyable() calls
     *      for ViewHolder
     *
     * Use two views here to cross fade therefore do not set canReuseUpdatedViewHolder
     * or else it wont work.
     * <p>
     * In case that was set somehow or the views are identical it will not trigger
     * this method and instead it will try to do an animateMove.
     *
     * @param oldHolder copy of the original view holder to use in cross fading.
     * @param newHolder the holder with the updated data.
     */
    private void animateChangeImpl(RecyclerView.ViewHolder oldHolder,
                                   RecyclerView.ViewHolder newHolder)
    {
        final View oldView = oldHolder == null ? null : oldHolder.itemView;
        final View newView = newHolder != null ? newHolder.itemView : null;

        if(oldView != null && newView != null)
        {
            ObjectAnimator fadeOut = fadeOu(oldView);
            fadeOut.addListener(new AnimListener()
            {
                @Override
                protected void animationStarted(Animator animation)
                { dispatchChangeStarting(oldHolder, true); }

                @Override
                protected void animationEnded(Animator animation)
                {
                    //Make sure to reset or it can leave blank tiles when scrolling up.
                    fadeOut.removeAllListeners();
                    resetViewProperties(oldView);
                    dispatchChangeFinished(oldHolder, true);
                }
            });

            ObjectAnimator fadeIn = fadeIn(newView);
            fadeIn.addListener(new AnimListener()
            {
                @Override
                protected void animationStarted(Animator animation)
                { dispatchChangeStarting(newHolder, false); }

                @Override
                protected void animationEnded(Animator animation)
                {
                    fadeIn.removeAllListeners();
                    resetViewProperties(newView);
                    dispatchChangeFinished(newHolder, false);
                }
            });

            fadeOut.start();
            fadeIn.start();
        }
    }

    private ObjectAnimator fadeIn(View view)
    {
        final ObjectAnimator animatorFadeIn = ObjectAnimator.ofFloat(
                view, "alpha", 0.0F, 1.0F);
        animatorFadeIn.setDuration(450);
        animatorFadeIn.setInterpolator(ACCELERATE_INTERPOLATOR);
        return animatorFadeIn;
    }

    private ObjectAnimator fadeOu(View view)
    {
        final ObjectAnimator animatorFadeOut = ObjectAnimator.ofFloat(
                view, "alpha", 1.0F, 0.0F);
        animatorFadeOut.setDuration(450);
        animatorFadeOut.setInterpolator(ACCELERATE_INTERPOLATOR);
        return animatorFadeOut;
    }

    private void resetViewProperties(View view)
    {
        view.setAlpha(1);
        view.setTranslationX(0);
        view.setTranslationY(0);
    }
}