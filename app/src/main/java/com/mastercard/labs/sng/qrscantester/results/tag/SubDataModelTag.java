package com.mastercard.labs.sng.qrscantester.results.tag;

import android.content.Context;
import android.util.Log;

import com.mastercard.mpqr.pushpayment.enums.AdditionalDataTag;
import com.mastercard.mpqr.pushpayment.enums.LanguageTag;
import com.mastercard.mpqr.pushpayment.enums.TemplateTag;
import com.mastercard.mpqr.pushpayment.exception.FormatException;
import com.mastercard.mpqr.pushpayment.model.AbstractDataModel;
import com.mastercard.mpqr.pushpayment.model.AdditionalData;
import com.mastercard.mpqr.pushpayment.model.LanguageData;
import com.mastercard.mpqr.pushpayment.model.TemplateData;
import com.mastercard.mpqr.pushpayment.utils.ITagEnumUtil;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by kaile on 4/1/18.
 * For tags which contain sub-data model instances as values.
 */

public class SubDataModelTag extends Tag {
    private ArrayList<Tag> subData = new ArrayList<>();
    private boolean hasSubDataModels;
    private String subTagWithError;
    private String innerSubTagWithError;

    /**
     * Constructor for a Tag with SubDataModel instance as its value. With a rootTag, subTagWithError and an innerSubTagWithError.
     * Use when tag's error originates from a sub-tag within a sub-tag within a PPTag. (Eg sub-tag 00 of subtag 50 (which is a SubDataModel), of PPTag 62).
     *
     * @param context              context
     * @param dataModel            value of tag
     * @param tagName              name of tag
     * @param tagNumber            number of tag
     * @param rootTag              tag number of root-tag
     * @param tagType              type of tag
     * @param subTagWithError      tag number of sub-tag with error
     * @param innerSubTagWithError tag number of inner-sub-tag with error
     * @param invalidValue         true if error is due to an invalid value, false if not
     * @param hasError             true if tag causes an error, false if not
     */
    private SubDataModelTag(Context context, AbstractDataModel dataModel, String tagName, String tagNumber, Tag rootTag, int tagType, String subTagWithError, String innerSubTagWithError, boolean invalidValue, boolean hasError) {
        super(context, tagName, tagNumber, "", rootTag, tagType, invalidValue, hasError);
        this.subTagWithError = subTagWithError;
        this.innerSubTagWithError = innerSubTagWithError;
        subDataToTagList(dataModel, context);
    }

    /**
     * Constructor for a Tag with SubDataModel instance as its value. With a rootTag and a subTagWithError.
     * Use when tag's error originates from value of a direct sub-tag within a PPTag. (Eg current Tag is subtag 55 of a PPTag, and tag with error is its direct subtag 01).
     *
     * @param context         context
     * @param dataModel       value of tag
     * @param tagName         name of tag
     * @param tagNumber       number of tag
     * @param rootTag         tag number of root-tag
     * @param tagType         type of tag
     * @param subTagWithError tag number of sub-tag with error
     * @param invalidValue    true if error is due to an invalid value, false if not
     * @param hasError        true if tag causes an error, false if not
     */
    private SubDataModelTag(Context context, AbstractDataModel dataModel, String tagName, String tagNumber, Tag rootTag, int tagType, String subTagWithError, boolean invalidValue, boolean hasError) {
        this(context, dataModel, tagName, tagNumber, rootTag, tagType, subTagWithError, "", invalidValue, hasError);
    }

    /**
     * Constructor for a Tag with SubDataModel instance as its value, and without error but with a root-tag (rg: current Tag instance is sub-tag 54 of PPTag 62).
     *
     * @param context   context
     * @param dataModel value of tag
     * @param tagName   name of tag
     * @param tagNumber number of tag
     * @param rootTag   tag number of root-tag
     * @param tagType   type of tag
     */
    private SubDataModelTag(Context context, AbstractDataModel dataModel, String tagName, String tagNumber, Tag rootTag, int tagType) {
        super(context, tagName, tagNumber, "", rootTag, tagType, false, false);
        this.subTagWithError = "";
        this.innerSubTagWithError = "";
        subDataToTagList(dataModel, context);
    }

    /**
     * Constructor for a Tag with SubDataModel instance as its value. With a subTagWithError, but without a rootTag. (Tag is an instance of PPTag containing subDataModel)
     * Use when tag's error originates from a sub-tag within a sub-tag within a PPTag. (Eg sub-tag 00 of subtag 50 (which is a SubDataModel), of PPTag 62).
     *
     * @param context              context
     * @param dataModel            value of tag
     * @param tagName              name of tag
     * @param tagNumber            number of tag
     * @param tagType              type of tag
     * @param subTagWithError      tag number of sub-tag with error
     * @param innerSubTagWithError tag number of inner-sub-tag with error
     * @param invalidValue         true if error is due to an invalid value, false if not
     * @param hasError             true if tag causes an error, false if not
     */
    public SubDataModelTag(Context context, AbstractDataModel dataModel, String tagName, String tagNumber, int tagType, String subTagWithError, String innerSubTagWithError, boolean invalidValue, boolean hasError) {
        this(context, dataModel, tagName, tagNumber, null, tagType, subTagWithError, innerSubTagWithError, invalidValue, hasError);
    }

    /**
     * Constructor for a Tag with SubDataModel instance as its value. With a subTagWithError, but without a rootTag. (Tag is an instance of PP Tag containing subDataModel)
     * Use when tag's error originates from value of a direct sub-tag within a PPTag. (Eg subtag 01 of PPTag 62).
     *
     * @param context         context
     * @param dataModel       value of tag
     * @param tagName         name of tag
     * @param tagNumber       number of tag
     * @param tagType         type of tag
     * @param subTagWithError tag number of sub-tag with error
     * @param invalidValue    true if error is due to an invalid value, false if not
     * @param hasError        true if tag causes an error, false if not
     */
    public SubDataModelTag(Context context, AbstractDataModel dataModel, String tagName, String tagNumber, int tagType, String subTagWithError, boolean invalidValue, boolean hasError) {
        this(context, dataModel, tagName, tagNumber, null, tagType, subTagWithError, invalidValue, hasError);
    }

    /**
     * Constructor for a Tag with SubDataModel instance as its value, and without error and root-tag (rg: current Tag instance is PPTag 62).
     *
     * @param context   context
     * @param dataModel value of tag
     * @param tagName   name of tag
     * @param tagNumber number of tag
     * @param tagType   type of tag
     */
    public SubDataModelTag(Context context, AbstractDataModel dataModel, String tagName, String tagNumber, int tagType) {
        this(context, dataModel, tagName, tagNumber, null, tagType, "", "", false, false);
    }

    /**
     * Converts tag values from AbstractDataModel into an array of Tag/SubDataModelTag.
     *
     * @param dataModel data model to abstract tag values from
     * @param context
     */
    private void subDataToTagList(AbstractDataModel dataModel, Context context) {
        subData.clear();

        Serializable currentTagValue;
        String currentTagName, currentTagNumber;
        int newTagType;

        for (int i = 0; i < 100; i++) {
            currentTagNumber = formatTagNumber(i);

            try {
                // only add to list if tag exists
                if (dataModel.hasValue(currentTagNumber)) {

                    currentTagValue = dataModel.getValue(currentTagNumber);
                    currentTagName = getCurrentTagName(currentTagNumber, dataModel);
                    newTagType = getNextTagType();

                    //create and add Tag to subData accordingly to type of Tag
                    if (currentTagNumber.equals(subTagWithError)) {
                        if (currentTagValue instanceof AbstractDataModel) {
                            hasSubDataModels = true;
                            subData.add(new SubDataModelTag(context, (AbstractDataModel) currentTagValue, currentTagName, currentTagNumber, this, newTagType, innerSubTagWithError, hasInvalidValue(), true));
                        } else {
                            subData.add(new Tag(context, currentTagName, currentTagNumber, (String) currentTagValue, this, newTagType, hasInvalidValue(), true));
                        }
                    } else {
                        if (currentTagValue instanceof AbstractDataModel) {
                            hasSubDataModels = true;
                            subData.add(new SubDataModelTag(context, (AbstractDataModel) currentTagValue, currentTagName, currentTagNumber, this, newTagType));
                        } else {
                            subData.add(new Tag(context, currentTagName, currentTagNumber, (String) currentTagValue, this, newTagType));
                        }
                    }
                }
            } catch (Exception ex) {
                Log.e("Error at Root-Tag " + super.getTagNumber(), currentTagNumber + " is a invalid sub-tag number to look up");
            }
        }

        calculateConsolidatedValue(dataModel);
    }

    /**
     * Gets corresponding name of specific tag number accordingly to type of data model.
     * For example, the same tag number '01' yields TAG_01_BILL_NUMBER for AdditionalData data models, but returns TAG_01_ALTERNATE_MERCHANT_NAME for LanguageData instances.
     *
     * @param dataModel        the type of data model this SubDataModelTag contains, affects the name retrieved
     * @param currentTagNumber the tag number to retrieve a name for
     * @return name of tag accordingly to the data model type and number, if not found, returns a default name containing tag number instead.
     */
    private String getCurrentTagName(String currentTagNumber, AbstractDataModel dataModel) {
        String nameToReturn = null;

        //get name if any
        try {
            if (dataModel instanceof AdditionalData) {
                if (ITagEnumUtil.getTag(currentTagNumber, AdditionalDataTag.class) != null)
                    nameToReturn = (ITagEnumUtil.getTag(currentTagNumber, AdditionalDataTag.class)).name();
            } else if (dataModel instanceof TemplateData) {
                if (ITagEnumUtil.getTag(currentTagNumber, TemplateTag.class) != null) {
                    nameToReturn = (ITagEnumUtil.getTag(currentTagNumber, TemplateTag.class)).name();
                }
            } else if (dataModel instanceof LanguageData) {
                if (ITagEnumUtil.getTag(currentTagNumber, LanguageTag.class) != null) {
                    nameToReturn = (ITagEnumUtil.getTag(currentTagNumber, LanguageTag.class)).name();
                }
            }
        } catch (Exception ex) {
            Log.d("At Root-Tag" + super.getTagNumber(), currentTagNumber + " is a sub-tag w/o a known name");
            nameToReturn = null;
        }

        // if name is null yet number is valid, means it is an undocumented dynamic tag
        if (nameToReturn == null) {
            nameToReturn = "TAG_" + currentTagNumber + "_CONTEXT_SPECIFIC_DATA";
        }

        return nameToReturn;
    }

    /**
     * Used when converting data model values to a tag list, assigns a sub-tag within the data model of <code>this</code> to a type, depending on the type of <code>this</code>.
     * For example, if <code>this</> is an instance of a PPTag, and <code>this</> is of tag number '64', we can infer that any sub-data within this tag contains information pertaining to language, and thus is assigned as a LANG_TAG.
     *
     * @return the type of tag, constants as corresponding to those declared in <code>Tag</> Class.
     */
    private int getNextTagType() {
        String currentTagNumber = getTagNumber();
        if (super.tagType == PP_TAG) {
            if (currentTagNumber.equals("64")) {
                //any sub-tags under PP_TAG 64 would be LANG_TAG
                return LANG_TAG;
            } else if (currentTagNumber.equals("62")) {
                //any sub-tags under PP_TAG 62 would be ADD_TAG
                return ADD_TAG;
            } else {
                //any sub-tags under PP_TAG 80-99 would be UN_TAG
                if ((Integer.parseInt(currentTagNumber) > 79) && (Integer.parseInt(currentTagNumber) < 100)) {
                    return UN_TAG;
                } else if ((Integer.parseInt(currentTagNumber) > 25) && (Integer.parseInt(currentTagNumber) < 52)) {
                    return MAI_TAG;
                }
            }
        } else if (super.tagType == ADD_TAG) {
            // because any instance with an ADD_TAG as parent and that contains SubData would be an unrestrictedData instance
            return UN_TAG;
        }
        return 0;
    }

    /**
     * Building a string value for display as this abstract data model's value.
     * Only display sub-tags that do not have subDataModel instances as values.
     */
    private void calculateConsolidatedValue(AbstractDataModel dataModel) {
        String currentTagNumber;
        Serializable currentTagValue;

        StringBuilder sb = new StringBuilder();
        for (Tag tag : subData) {
            currentTagNumber = tag.getTagNumber();
            try {
                currentTagValue = dataModel.getValue(currentTagNumber);
                if (!(currentTagValue instanceof AbstractDataModel)) {
                    if (!sb.toString().trim().equals("")) {
                        sb.append("\n");
                    }
                    sb.append(currentTagNumber + " = " + currentTagValue);
                }
            } catch (FormatException ex) {
                Log.e("Error", "Tag Number does not exist in dataModel");
            }
        }
        this.tagValue = sb.toString();
    }

    /**
     * Pads a string with zeroes if needed to format it correctly as a tag value.
     *
     * @param i the integer to format
     * @return a formatted tag number.
     */
    private String formatTagNumber(int i) {
        if (i < 10) {
            return "0" + String.valueOf(i);
        } else return String.valueOf(i);
    }

    public boolean hasSubDataModels() {
        return hasSubDataModels;
    }

    public ArrayList<Tag> getSubData() {
        return subData;
    }
}
