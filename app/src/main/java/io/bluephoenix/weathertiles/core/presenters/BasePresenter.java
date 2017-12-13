package io.bluephoenix.weathertiles.core.presenters;

import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public abstract class BasePresenter<V>
{
    V publishToView;

    public final void attachView(@NonNull V view, boolean register)
    {
        this.publishToView = view;
        if(register) { EventBus.getDefault().register(this); }
    }

    public final void detachView(boolean deregister)
    {
        this.publishToView = null;
        if(deregister) { EventBus.getDefault().unregister(this); }
    }

    protected final boolean isViewAttached()
    {
        return publishToView != null;
    }
}
