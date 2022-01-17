package com.example.memeexplorer.utilities;
import java.util.Locale;

public class AppConfig {

    // if you not use ads you can set this to false
    public static final boolean ENABLE_GDPR = true;

    // force rtl layout direction
    public static final boolean RTL_LAYOUT = false;

    // flag for display ads, change true and false only
    public static final boolean ADS_ALL_ENABLE = true;

    public static final boolean ADS_MAIN_BANNER = ADS_ALL_ENABLE && true;
    public static final boolean ADS_MAIN_INTERSTITIAL = ADS_ALL_ENABLE && true;
    public static final int ADS_MAIN_INTERSTITIAL_INTERVAL = 180; // in second
    public static final boolean ADS_NEWS_INFO_DETAILS = ADS_ALL_ENABLE && true;
    public static final boolean ADS_PRODUCT_DETAILS = ADS_ALL_ENABLE && true;

    // tinting category icon
    public static final boolean TINT_CATEGORY_ICON = true;

    /* Locale.US        -> 2,365.12
     * Locale.GERMANY   -> 2.365,12
     */
    public static final Locale PRICE_LOCAL_FORMAT = Locale.US;

    /* true     -> 2.365,12
     * false    -> 2.365
     */
    public static final boolean PRICE_WITH_DECIMAL = true;

    /* true     -> 2.365,12 USD
     * false    -> USD 2.365
     */
    public static final boolean PRICE_CURRENCY_IN_END = true;
}