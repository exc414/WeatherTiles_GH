package io.bluephoenix.weathertiles.ui.dialogs;

import android.app.Activity;
import android.text.InputType;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import io.bluephoenix.weathertiles.R;
import io.bluephoenix.weathertiles.util.Util;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class Dialogs
{
    private Activity activity;
    private DialogsPublish dialogsPublish;
    private DialogsPublish.Preferences preferences;
    private int dayNight = Util.getColorFromResource(R.color.dayNight);
    private int colorAccentMain = Util.getColorFromResource(R.color.colorAccentMain);
    private int colorPrimary = Util.getColorFromResource(R.color.colorPrimary);
    private int colorAccentForExtremeDark = Util.getColorFromResource(R.color.colorAccentForExtremeDark);
    private MaterialDialog downloadingData;

    //This needs the activity instance itself not the application context.
    //It will allow you to put the normal context but then throw :
    //Bad window token, you cannot show a dialog before an Activity is
    //created or after it's hidden.
    public Dialogs(Activity activity) { this.activity = activity; }

    public void registerDialogPublishCallback(DialogsPublish dialogsPublish)
    {
        this.dialogsPublish = dialogsPublish;
    }

    public void registerDialogPublishCallback(DialogsPublish.Preferences preferences)
    {
        this.preferences = preferences;
    }

    public void inputData(int title, String hint, String currentMaxTileNumberPrefill)
    {
        new MaterialDialog.Builder(activity)
                .title(title)
                .titleColor(colorAccentMain)
                .backgroundColor(colorPrimary)
                .theme(Theme.DARK)
                .positiveColor(dayNight)
                .widgetColor(dayNight)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input(hint, currentMaxTileNumberPrefill, false, (dialog, input) ->
                        preferences.setPreferences(Integer.valueOf(input.toString()))).show();
    }

    public void downloadingData(int titleRes, int messageRes, int iconRes)
    {
        try
        {
            downloadingData = new MaterialDialog.Builder(activity)
                    .title(titleRes)
                    .titleColor(colorAccentMain)
                    .iconRes(iconRes)
                    .content(messageRes)
                    .contentColor(colorAccentForExtremeDark)
                    .positiveColor(dayNight)
                    .negativeColor(dayNight)
                    .backgroundColor(colorPrimary)
                    .theme(Theme.DARK)
                    .canceledOnTouchOutside(false)
                    .widgetColor(dayNight)
                    .progress(true, 0)
                    .show();

            downloadingData.getWindow().setLayout(Util.dialogWidth(),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        catch(NullPointerException ex) { ex.printStackTrace(); }
    }

    public void dismissDownloadingData()
    {
        if(downloadingData != null) { downloadingData.dismiss(); }
    }

    public void notifyUser(int titleRes, int messageRes, int iconRes)
    {
        try
        {
            new MaterialDialog.Builder(activity)
                    .title(titleRes)
                    .titleColor(colorAccentMain)
                    .iconRes(iconRes)
                    .content(messageRes)
                    .contentColor(colorAccentForExtremeDark)
                    .positiveColor(dayNight)
                    .negativeColor(dayNight)
                    .backgroundColor(colorPrimary)
                    .theme(Theme.DARK)
                    .canceledOnTouchOutside(false)
                    .positiveText("Okay")
                    .show().getWindow().setLayout(Util.dialogWidth(),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        catch(NullPointerException ex) { ex.printStackTrace(); }
    }

    //-1 no, 0+ selects an item depending on its place on the array
    @SuppressWarnings("ConstantConditions")
    public void openSortMenu(int preselectItem)
    {
        try
        {
            new MaterialDialog.Builder(activity)
                    .title(R.string.sort_options_title)
                    .titleColor(colorAccentMain)
                    .items(R.array.sort_options_array)
                    .iconRes(R.drawable.ic_sort_grey_300_24dp)
                    .itemsColor(colorAccentForExtremeDark)
                    .contentColor(colorAccentForExtremeDark)
                    .positiveColor(dayNight)
                    .negativeColor(dayNight)
                    .backgroundColor(colorPrimary)
                    .widgetColor(dayNight)
                    .choiceWidgetColor(Util.getColorStateListFromResource(
                            R.color.radio_btn_color_state_list))
                    .theme(Theme.DARK)
                    .itemsCallbackSingleChoice(
                            preselectItem, (dialog, view1, which, text) ->
                            {
                                dialogsPublish.setUserSortSelection(which);
                                return true;
                            })
                    .positiveText("Okay")
                    .negativeText("Cancel")
                    .show().getWindow().setLayout(Util.dialogWidth(),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        catch(NullPointerException ex) { ex.printStackTrace(); }
    }
}
