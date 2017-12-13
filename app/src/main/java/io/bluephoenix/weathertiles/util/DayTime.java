package io.bluephoenix.weathertiles.util;

import java.util.Calendar;
import java.util.TimeZone;

import io.bluephoenix.weathertiles.core.data.model.db.SunriseSunset;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class DayTime
{
    private static double sunrise = 0;
    private static double sunset = 0;

    /**
     * Returns whether is day time or not. However, it uses data already
     * calculate from the database to make this determination.
     *
     * @param sunriseSunset an object containing lat/lon and sunset/sunrise information
     *                      about a specific city.
     * @param timezone      a string with the timezone name.
     * @return whether is daytime (true) or nighttime (false).
     *
     */
    public static boolean isDayTime(SunriseSunset sunriseSunset, String timezone)
    {
        Calendar calendar = Calendar.getInstance();
        double now = Util.getTimeNowInDecimals(calendar, timezone);
        return (sunriseSunset.getSunrise() < now && sunriseSunset.getSunset() > now);
    }

    /**
     * Returns whether is day time or not. However, it uses data already
     * calculate from the database to make this determination.
     *
     * @param sunriseSunset an object containing lat/lon and sunset/sunrise information
     *                      about a specific city.
     * @param time          an int with the hour to compare.
     * @return whether is daytime (true) or nighttime (false).
     */
    public static boolean isDayTime(SunriseSunset sunriseSunset, int time)
    {
        return (sunriseSunset.getSunrise() < time && sunriseSunset.getSunset() > time);
    }

    /**
     * Calculates whether is daytime or nighttime based on timezone, lat and longitude.
     *
     * @param lat double
     * @param lon double
     * @param timeZoneId string
     * @return whether is daytime (true) or nighttime (false).
     */
    public static boolean isDayTime(double lat, double lon, String timeZoneId)
    {
        Calendar calendar = Calendar.getInstance();
        double now = Util.getTimeNowInDecimals(calendar, timeZoneId);

        sunrise = getSunsetSunriseTime(sunriseAndSunset(true, lat, lon), lon, timeZoneId);
        sunset = getSunsetSunriseTime(sunriseAndSunset(false, lat, lon), lon, timeZoneId);

        return sunrise < now && sunset > now;
    }

    public static double getSunrise() { return sunrise; }

    public static double getSunset() { return sunset; }

    /**
     * Calculate the hour different between cities. Adjust for DST as needed.
     * @param calendar Calendar object
     * @param timeZoneId String with the timezone id, i.e. (America/New_York).
     * @return an int with the correct hour offset based on the time zone.
     */
    private static int gmtOffset(Calendar calendar, String timeZoneId)
    {
        int gmt = TimeZone.getTimeZone(timeZoneId).getRawOffset() / 3600000;
        if(TimeZone.getTimeZone(timeZoneId).inDaylightTime(calendar.getTime())) { gmt++; }
        return gmt;
    }

    /**
     * Convert the decimal time value to UTC time.
     * @param timeInDecimal time calculate in decimals
     * @param lon longitude
     * @param timeZoneId String with the timezone id, i.e. (America/New_York).
     * @return a double contained the local time.
     */
    private static double getSunsetSunriseTime(double timeInDecimal, double lon,
                                               String timeZoneId)
    {
        Calendar calendar = Calendar.getInstance();
        double utcTime = timeInDecimal - (lon / 15.0);
        return utcTime + gmtOffset(calendar, timeZoneId);
    }

    /**
     * To be more accurate one should use the BigDecimal class. However, in this
     * case using the primitive double more than suffices.
     * Looking at two values for the city of tampa (sunrise)
     *
     * Using BigDecimal = 5.482247037906356 vs double = 5.482247931986613
     *
     * This is more than acceptable for this application. Since in the end once
     * its rounded to minutes the difference goes away. Better to use the primitive
     * as its lighter on memory/faster than using an object.
     *
     * Probably could have gotten away using a float instead of a double.
     *
     * @param sunriseOrSunset boolean
     * @param lat double
     * @param lon double
     * @return a double value representing time as a decimal value.
     */
    private static double sunriseAndSunset(boolean sunriseOrSunset, double lat, double lon)
    {
        final double zenith = 90.8333;
        Calendar calendar = Calendar.getInstance();
        double dayOfTheYear = calendar.get(Calendar.DAY_OF_YEAR);
        double offset = (sunriseOrSunset) ? 6 : 18;

        //longitude / 15
        double hour = lon / 15.0;

        //Formula : t = dayOfYear + ((offset - hour) / 24)
        double rightAddend = ((offset - hour) / 24.0);
        double longitudeHour = dayOfTheYear + rightAddend;

        //Calculate the Sun's mean anomaly
        //M = (0.9856 * t) - 3.289
        double meanAnomaly = 0.9856 * longitudeHour - 3.289;

        //Calculate the Sun's true longitude
        double rAnomaly = Math.toRadians(meanAnomaly);
        double sunLongitude = meanAnomaly + (1.916 * Math.sin(rAnomaly)) +
                (0.020 * Math.sin(2 * rAnomaly)) + 282.634;

        //Correct the sun's longitude
        if(sunLongitude > 360) { sunLongitude = sunLongitude - 360; }

        //Calculate the Sun's right ascension
        //Right ascension value needs to be in the same quadrant as L
        //Right ascension value needs to be converted into hours
        double tanLeft = Math.tan(Math.toRadians(sunLongitude));
        double rightAscension = Math.atan(Math.toRadians(0.91764 * Math.toDegrees(tanLeft)));
        rightAscension = Math.toDegrees(rightAscension);

        if(rightAscension < 0) { rightAscension = rightAscension + 360; }
        else if(rightAscension > 360) { rightAscension = rightAscension - 360; }

        //Right ascension value needs to be in the same quadrant as L
        double lonQuadrant = Math.floor(sunLongitude / 90.0);
        lonQuadrant = lonQuadrant * 90;

        double rightAscensionQuadrant = Math.floor(rightAscension  / 90);
        rightAscensionQuadrant = rightAscensionQuadrant * 90;

        double augend = lonQuadrant - rightAscensionQuadrant;
        double finalRightAscension = (rightAscension + augend) / 15;

        //Calculate the Sun's declination
        double sinDec = 0.39782 * Math.sin(Math.toRadians(sunLongitude));
        double cosDec = Math.cos(Math.asin(sinDec));

        //Calculate the Sun's local hour angle
        double zenithInRads = Math.toRadians(zenith);
        double cosineZenith = Math.cos(zenithInRads);
        double sinLatitude =  Math.sin(Math.toRadians(lat));
        double cosLatitude = Math.cos(Math.toRadians(lat));

        double sinDeclinationTimesSinLat = sinDec * sinLatitude;
        double dividend = cosineZenith - sinDeclinationTimesSinLat;
        double divisor = cosDec * cosLatitude;

        double finalRes = dividend / divisor;

        //Finish calculating H and convert into hours
        //Get Sun Local Hour
        double arcCosineOfCosH = Math.acos(finalRes);
        double localHour = Math.toDegrees(arcCosineOfCosH);

        if(sunriseOrSunset) { localHour = 360 - localHour; }

        double cosineSunLocalHour = localHour / 15;

        //T = H + RA - (0.06571 * t) - 6.622
        double innerParams = longitudeHour * 0.06571;
        double localMeanTime = cosineSunLocalHour + finalRightAscension - innerParams;
        localMeanTime = localMeanTime - 6.622;

        //Correct localMeanTime
        if(localMeanTime < 0) { localMeanTime = localMeanTime + 24; }
        else if(localMeanTime > 24) { localMeanTime = localMeanTime - 24; }

        //Returns sunset or sunrise time in decimals.
        return localMeanTime;
    }
}
