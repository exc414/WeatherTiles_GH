package io.bluephoenix.weathertiles.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.OnClick;
import io.bluephoenix.weathertiles.R;
import io.bluephoenix.weathertiles.util.Constant;
import io.bluephoenix.weathertiles.util.Util;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class AboutActivity extends BaseActivity
{
    @BindView(R.id.btnWeatherIcons) Button btnWeatherIcons;
    @BindView(R.id.btnOWM) Button btnOpenWeatherMapAPI;
    @BindView(R.id.imgBtnQuoteMachine) ImageButton btnQuoteMachine;
    @BindView(R.id.imgBtnAppUninstaller) ImageButton btnAppUninstaller;
    @BindView(R.id.imgBtnStressCPU) ImageButton btnStressCPU;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setActionBarBackButton(toolbar);
    }

    @OnClick(R.id.btnWeatherIcons)
    public void visitWeatherIconsWebsite()
    {
        Util.openWebsiteInBrowser(this, "http://erikflowers.github.io/weather-icons/");
    }

    @OnClick(R.id.btnOWM)
    public void visitOpenWeatherMapWebsite()
    {
        Util.openWebsiteInBrowser(this, "https://openweathermap.org/api/");
    }

    @OnClick(R.id.imgBtnQuoteMachine)
    public void openGooglePlayListingQuoteMachine()
    {
        Util.openAppInMarket(this, Constant.QUOTE_MACHINE);
    }

    @OnClick(R.id.imgBtnAppUninstaller)
    public void openGooglePlayListingAppUninstaller()
    {
        Util.openAppInMarket(this, Constant.APP_UNINSTALLER);
    }

    @OnClick(R.id.imgBtnStressCPU)
    public void openGooglePlayListingStressCPU()
    {
        Util.openAppInMarket(this, Constant.STRESS_CPU);
    }
}
