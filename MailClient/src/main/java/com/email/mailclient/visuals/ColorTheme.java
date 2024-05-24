package com.email.mailclient.visuals;

public enum ColorTheme {
    DEFAULT,
    LIGHT,
    DARK;

    public static String getCssPath(ColorTheme colorTheme) {
        String cssDirectory = "/com/email/mailclient/css/";

        switch (colorTheme) {
            case LIGHT:
                return String.format("%sthemeLight.css", cssDirectory);
            case DEFAULT:
                return String.format("%sthemeDefault.css", cssDirectory);
            case DARK:
                return String.format("%sthemeDark.css", cssDirectory);
            default:
                return null;
        }
    }

}
