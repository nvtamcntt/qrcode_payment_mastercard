package com.mastercard.labs.sng.qrscantester.utils;

import android.content.Context;

import com.mastercard.labs.sng.qrscantester.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by amri on 4/1/18.
 * Retrieves value suggestions for specific tag numbers. (Displayed in individual error messages)
 */

public class SuggestedValueManager {
    private static JSONObject json;

    private static boolean readJSONFile(Context context) {
        InputStream in = context.getResources().openRawResource(R.raw.suggested_tag_value);
        if (in != null) {
            try {
                String strJSON = getStringFromStream(in);
                json = new JSONObject(strJSON.toString());

            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }

    private static String getStringFromStream(InputStream in) {
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        StringBuilder builder = new StringBuilder();
        String line;
        try {
            while ((line = r.readLine()) != null) {
                builder.append(line).append('\n');
            }
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Returns suggested value for a specific PP_TAG.
     *
     * @param strTag  tag number for which to retrieve for
     * @param context
     */
    public static String samplePPForTag(String strTag, Context context) {
        if (json == null) {
            if (!readJSONFile(context)) {
                return null;
            }
        }

        try {
            JSONObject dictPP = json.getJSONObject("pushPaymentData");

            if (strTag.equals("64")) {
                dictPP = json.getJSONObject("main");
                strTag = "64";
            } else if (strTag.equals("62")) {
                dictPP = json.getJSONObject("main");
                strTag = "62";
            } else {
                if ((Integer.parseInt(strTag) > 79) && (Integer.parseInt(strTag) < 100)) {
                    dictPP = json.getJSONObject("main");
                    strTag = "91";
                } else if ((Integer.parseInt(strTag) > 25) && (Integer.parseInt(strTag) < 52)) {
                    dictPP = json.getJSONObject("main");
                    strTag = "26";
                }
            }
            String strValue = dictPP.getString(strTag);
            return strValue;
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * Returns suggested value for a specific tag that is from an AdditionalData instance.
     *
     * @param strTag  tag number for which to retrieve for
     * @param context
     */
    public static String sampleAdditionalDataForTag(String strTag, Context context) {
        if (json == null) {
            if (!readJSONFile(context)) {
                return null;
            }
        }

        try {
            JSONObject dictPP = json.getJSONObject("pushPaymentData");
            JSONObject dictAD = dictPP.getJSONObject("62");
            String strValue = dictAD.getString(strTag);
            return strValue;
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * Returns suggested value for a specific tag that is from an MAIData instance.
     *
     * @param strTag  tag number for which to retrieve for
     * @param context
     */
    public static String sampleMAIDataForTag(String strTag, Context context) {
        if (json == null) {
            if (!readJSONFile(context)) {
                return null;
            }
        }

        try {
            JSONObject dictPP = json.getJSONObject("pushPaymentData");
            JSONObject dictMAID = dictPP.getJSONObject("26");
            String strValue = dictMAID.getString(strTag);
            if (strValue == null) {
                int nTag = Integer.parseInt(strTag);
                if (nTag >= 02 && nTag <= 99) {
                    strValue = dictMAID.getString("01");
                }
            }
            return strValue;
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * Returns description for a specific tag that is from an UnrestrictedData instance.
     *
     * @param strTag  tag number for which to retrieve for
     * @param context
     */
    public static String sampleUnrestrictedDataForTag(String strTag, Context context) {
        if (json == null) {
            if (!readJSONFile(context)) {
                return null;
            }
        }

        try {
            JSONObject dictPP = json.getJSONObject("pushPaymentData");
            JSONObject dictUD = dictPP.getJSONObject("91");
            String strValue = dictUD.getString(strTag);
            if (strValue == null) {
                int nTag = Integer.parseInt(strTag);
                if (nTag >= 02 && nTag <= 99) {
                    strValue = dictUD.getString("01");
                }
            }
            return strValue;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns description for a specific tag that is from an LanguageData instance.
     *
     * @param strTag  tag number for which to retrieve for
     * @param context
     */
    public static String sampleLanguageDataForTag(String strTag, Context context) {
        if (json == null) {
            if (!readJSONFile(context)) {
                return null;
            }
        }

        try {
            JSONObject dictPP = json.getJSONObject("pushPaymentData");
            JSONObject dictLD = dictPP.getJSONObject("64");
            String strValue = dictLD.getString(strTag);
            return strValue;
        } catch (Exception e) {
            return null;
        }
    }
}
