package com.kustomer.kustomersdk.Helpers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import java.util.regex.Pattern;

import ru.noties.markwon.Markwon;
import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.spans.SpannableTheme;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSText {

    //region Properties
    private static final String EMAIL_REGEX = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,5}";
    private static final String PHONE_REGEX = "(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}";
    //endregion

    //region Public Methods
    public static void setMarkDownText(@NonNull TextView textView,@Nullable String text) {

        if (text == null)
            return;

        String msg = formatText(text);
        SpannableTheme theme = SpannableTheme.builderWithDefaults(textView.getContext())
                .linkColor(textView.getTextColors().getDefaultColor())
                .build();

        SpannableConfiguration spannableConfiguration = SpannableConfiguration.builder(textView.getContext())
                .theme(theme)
                .build();

        Markwon.setMarkdown(textView, spannableConfiguration, msg);
    }

    @NonNull
    private static String formatText(@NonNull String text) {
        if (!text.contains("\n")) {
            return text;
        }

        StringBuilder updatedString = new StringBuilder();
        int nextIndex = text.contains("\n") ? text.indexOf("\n") : text.length();
        while (nextIndex < text.length()) {
            // if - is after \n
            if (nextIndex + 1 < text.length() && text.charAt(nextIndex + 1) == '-') {
                updatedString.append(text.substring(0, nextIndex + 1));
                text = text.substring(nextIndex + 1);
            }
            // if number is after \n
            else if (nextIndex + 1 < text.length() && Character.isDigit(text.charAt(nextIndex + 1))) {
                int digitRange = nextIndex + 1;
                // find total number length on text
                while (digitRange < text.length() && Character.isDigit(text.charAt(digitRange))) {
                    digitRange += 1;
                }

                // If . is after number then ignore \n as it is.
                if (digitRange < text.length() && text.charAt(digitRange) == '.') {
                    updatedString.append(text.substring(0, digitRange));
                    text = text.substring(digitRange);
                }
                // otherwise replace it with <br />
                else {
                    updatedString.append(text.substring(0, nextIndex)).append("<br />");
                    text = text.substring(nextIndex + 1);
                }
            }
            // Keep replacing \n with <br />
            else {
                updatedString.append(text.substring(0, nextIndex)).append("<br />");
                text = text.substring(nextIndex + 1);
            }
            nextIndex = text.contains("\n") ? text.indexOf("\n") : text.length();
        }

        // Replace multi occurrence of <br /> with \n
        return (updatedString + text).replace("<br /><br />", "\n\n");
    }

    public static boolean isValidEmail(@Nullable String email) {
        if (TextUtils.isEmpty(email))
            return false;
        return Pattern.compile(EMAIL_REGEX).matcher(email).matches();
    }

    public static boolean isValidPhone(@Nullable String phoneNo) {
        if (TextUtils.isEmpty(phoneNo))
            return false;
        return Pattern.compile(PHONE_REGEX).matcher(phoneNo).matches();
    }
    //endregion

}
