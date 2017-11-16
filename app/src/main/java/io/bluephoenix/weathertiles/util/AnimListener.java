package io.bluephoenix.weathertiles.util;

import android.animation.Animator;

/**
 * @author Carlos A. Perez Zubizarreta
 */

public abstract class AnimListener implements Animator.AnimatorListener
{
    protected abstract void animationStarted(Animator animation);
    protected abstract void animationEnded(Animator animation);

    @Override
    public void onAnimationStart(Animator animation) { animationStarted(animation); }

    @Override
    public void onAnimationEnd(Animator animation)
    {
        animationEnded(animation);
    }

    @Override
    public void onAnimationCancel(Animator animation) { }

    @Override
    public void onAnimationRepeat(Animator animation) { }
}
