package io.bluephoenix.weathertiles.ui.views;

import android.content.Context;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class CityAutoCompleteView extends AppCompatAutoCompleteTextView
        implements TextView.OnEditorActionListener
{
    public CityAutoCompleteView(Context context)
    {
        super(context);
    }

    public CityAutoCompleteView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CityAutoCompleteView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    /*
     * Note about this method. For some reason when this method is used the
     * dropdown flashes in and out every time a char is typed in the edit box. Two
     * it does not stop the user from typing invalid characters in the edit box.
     * It however, does preserved the formatting of the text (underline/bold .etc).
     * Using setFilter on the textView will actually stop the user from typing
     * the invalid chars in. Also no in/out flashing with the drop down.
     * Doing this will remove the formatting.
     */
    @Override
    protected void performFiltering(CharSequence text, int keyCode)
    {
        String filterText = ""; //Disable filtering
        super.performFiltering(filterText, keyCode);
    }

    @Override
    protected void replaceText(final CharSequence text)
    {
        //Capture value and append it to the existing text.
        super.replaceText(text);
    }

    /**
     * Called when an action is being performed.
     *
     * @param view     The view that was clicked.
     * @param actionId Identifier of the action.  This will be either the
     *                 identifier you supplied, or {@link EditorInfo#IME_NULL
     *                 EditorInfo.IME_NULL} if being called due to the enter key
     *                 being pressed.
     * @param event    If triggered by an enter key, this is the event;
     *                 otherwise, this is null.
     * @return Return true if you have consumed the action, else false.
     */
    @Override
    public boolean onEditorAction(TextView view, int actionId, KeyEvent event)
    {
        //Dismisses keyboard when the Enter(Done) key is pressed.
        return false;
    }
}
