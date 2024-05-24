package com.email.mailclient.visuals;

public enum FontSize {
    SMALL,
    MEDIUM,
    BIG;

    public static String getCssPath(FontSize fontSize) {
        String cssDirectory = "/com/email/mailclient/css/";

        switch (fontSize) {
            case SMALL:
                return String.format("%sfontSmall.css", cssDirectory);
            case MEDIUM:
                return String.format("%sfontMedium.css", cssDirectory);
            case BIG:
                return String.format("%sfontBig.css", cssDirectory);
            default:
                return null;
        }
    }
}
