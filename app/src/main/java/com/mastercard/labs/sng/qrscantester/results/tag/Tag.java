package com.mastercard.labs.sng.qrscantester.results.tag;

import android.content.Context;

import com.mastercard.labs.sng.qrscantester.utils.SuggestedValueManager;
import com.mastercard.labs.sng.qrscantester.utils.TagDescriptionManager;

import java.io.Serializable;

/**
 * Created by kaile on 4/1/18.
 */

public class Tag implements Serializable {

    public static final int PP_TAG = 1;
    public static final int ADD_TAG = 2;
    public static final int LANG_TAG = 3;
    public static final int MAI_TAG = 4;
    public static final int UN_TAG = 5;

    /**
     * Number of the tag.
     */
    private String tagNumber;

    /**
     * Stored name of the tag eg: TAG_00_PAYMENT_FORMAT_INDICATOR.
     */
    private String tagName;

    /**
     * Stored description of the tag.
     */
    private String tagDesc;

    /**
     * Stored validExample of the tag. Only to be used if tag has a invalid value.
     */
    private String errorMessage;

    /**
     * Only use if tag is an instance of subtag, else value should be null.
     */
    private Tag rootTag;

    /**
     * Indicates if tag has errors.
     */
    private boolean hasError;

    /**
     * Indicates if tag has errors due to invalid values.
     */
    private boolean invalidValue;

    /**
     * Indicates the tag's type.
     */
    protected int tagType;

    /**
     * Value that is displayed on app.
     * Equivalent to the actual value if it is an instance of a regular tag, but if is an instance of SubDataModelTag,
     * contains a consolidated overview of sub-tags instead
     */
    protected String tagValue;

    /**
     * Constructor for Tag without root-tag.
     * @param context context
     * @param tagName name of tag
     * @param tagNumber tag number
     * @param tagValue value of tag
     * @param tagType type of tag
     * @param invalidValue true is tag has error due to an invalid value, false if not.
     * @param hasError true is tag has error, false if not.
     */
    public Tag (Context context, String tagName, String tagNumber, String tagValue, int tagType, boolean invalidValue, boolean hasError) {
        this.tagNumber = tagNumber;
        this.tagValue = tagValue;
        this.invalidValue = invalidValue;
        this.hasError = hasError;
        this.rootTag = null;
        this.tagType = tagType;
        this.tagName = tagName;

        assignDetails(context);
    }

    /**
     * Constructor for Tag with root-tag.
     * @param context context
     * @param tagName name of tag
     * @param tagNumber tag number
     * @param tagValue value of tag
     * @param tagType type of tag
     * @param rootTag direct root-tag of tag
     * @param invalidValue true is tag has error due to an invalid value, false if not.
     * @param hasError true is tag has error, false if not.
     */
    public Tag (Context context, String tagName, String tagNumber, String tagValue, Tag rootTag, int tagType, boolean invalidValue, boolean hasError) {
        this(context, tagName, tagNumber, tagValue, tagType, invalidValue, hasError);
        this.rootTag = rootTag;
    }

    /**
     * Constructor for Tag with root-tag and without error.
     * @param context context
     * @param tagName name of tag
     * @param tagNumber tag number
     * @param tagValue value of tag
     * @param tagType type of tag
     * @param rootTag direct root-tag of tag
     */
    public Tag (Context context, String tagName, String tagNumber, String tagValue, Tag rootTag, int tagType) {
        this(context, tagName, tagNumber, tagValue, tagType, false, false);
        this.rootTag = rootTag;
    }

    /**
     * Retrieves stored error messages and tag descriptions values from JSON files.
     * @param context
     */
    private void assignDetails(Context context) {
        errorMessage = "Valid format example: ";
        switch (tagType) {
            case PP_TAG: {
                tagDesc = TagDescriptionManager.descriptionPPForTag(tagNumber, context);

                if (SuggestedValueManager.samplePPForTag(tagNumber, context) == null) {
                    errorMessage = "";
                } else {
                    errorMessage += SuggestedValueManager.samplePPForTag(tagNumber, context);
                }
            }
            break;
            case ADD_TAG: {
                tagDesc = TagDescriptionManager.descriptionAdditionalDataForTag(tagNumber, context);
                if (SuggestedValueManager.sampleAdditionalDataForTag(tagNumber, context) == null) {
                    errorMessage = "";
                } else {
                    errorMessage += SuggestedValueManager.sampleAdditionalDataForTag(tagNumber, context);
                }
            }
            break;
            case LANG_TAG: {
                tagDesc = TagDescriptionManager.descriptionLanguageDataForTag(tagNumber, context);
                if (SuggestedValueManager.sampleLanguageDataForTag(tagNumber, context) == null) {
                    errorMessage = "";
                } else {
                    errorMessage += SuggestedValueManager.sampleLanguageDataForTag(tagNumber, context);
                }
            }
            break;
            case MAI_TAG: {
                tagDesc = TagDescriptionManager.descriptionMAIDataForTag(tagNumber, context);
                if (SuggestedValueManager.sampleMAIDataForTag(tagNumber, context) == null) {
                    errorMessage = "";
                } else {
                    errorMessage += SuggestedValueManager.sampleMAIDataForTag(tagNumber, context);
                }
            }
            break;
            case UN_TAG: {
                tagDesc = TagDescriptionManager.descriptionUnrestrictedDataForTag(tagNumber, context);
                if (SuggestedValueManager.sampleUnrestrictedDataForTag(tagNumber, context) == null) {
                    errorMessage = "";
                } else {
                    errorMessage += SuggestedValueManager.sampleUnrestrictedDataForTag(tagNumber, context);
                }
            }
            break;
        }

        if (tagDesc == null) {
            tagDesc = "";
        }

        if (errorMessage.equals("Valid format example: ")) {
            errorMessage = "";
        }
    }

    public String getTagNumber() {
        return tagNumber;
    }

    public String getTagName() {
        return tagName;
    }

    public String getTagDesc() {
        return tagDesc;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Tag getRootTag() {
        return rootTag;
    }

    public String getTagValue() {
        return tagValue;
    }

    public boolean hasInvalidValue() {
        return invalidValue;
    }

    public boolean hasError() {
        return hasError;
    }

}
