package com.mastercard.labs.sng.qrscantester;

import com.mastercard.mpqr.pushpayment.enums.ITag;
import com.mastercard.mpqr.pushpayment.exception.ConflictiveTagException;
import com.mastercard.mpqr.pushpayment.exception.InvalidTagValueException;
import com.mastercard.mpqr.pushpayment.exception.MissingTagException;
import com.mastercard.mpqr.pushpayment.exception.RFUTagException;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by kaile on 9/1/18.
 * Contains details regarding exception that has occurred.
 * ExceptionDetail instances are mostly created for ConflictiveTagException (from PPData), RFUException (from PPData)
 * and InvalidTagValueException (PPData if non-subtag, still with PPData as root-tag if sub-tag error)
 */

public class ExceptionDetails implements Serializable {

    /**
     * Number of tag with error
     */
    private ArrayList<String> ppTagsWithErrors = new ArrayList<>();

    /**
     * True if exception is due to invalid value, false if not.
     */
    private boolean isInvalid = false;

    /**
     * Tag number of sub-tag (lowest instance in hierarchy) with error (if any).
     * Eg: if error is derived from sub-tag 00 in sub-tag 50 of PPTag 62, subtagNumber = 00, but if error is from sub-tag 01 of PPTag, subtagNumber = 01
     */
    private String subtagNumber = null;

    /**
     * If the error is from a subtag within a sub-data instance within a PPTag, this contains the tag-number of the subData instance
     * Eg: rootTag == 50 if error is from PPTag62 -> sub-tag 50 -> subtag 00
     */
    private String rootTag = null;

    /**
     * Main error message caught from exception.
     */
    private String errorMessage = null;

    public ExceptionDetails(Exception ex) {
        errorMessage = ex.getMessage();
        dissectException(ex);
    }

    /**
     * Picks apart the caught exception to fill the variables in ExceptionDetails.
     * Stores values accordingly to the type of exception caught.
     *
     * @param ex exception caught.
     */
    private void dissectException(Exception ex) {
        if (ex instanceof ConflictiveTagException) {
            // ConflictiveTagErrors are only caused by direct PPTags
            ITag[] errList = ((ConflictiveTagException) ex).getConflictiveTags();
            for (ITag tag : errList) {
                ppTagsWithErrors.add(tag.getTag());
            }
        } else if (ex instanceof InvalidTagValueException) {
            getInvalidExceptionTags(ex);
        } else if (ex instanceof RFUTagException) {
            ppTagsWithErrors.add(((RFUTagException) ex).getTag());
        } else if (ex instanceof MissingTagException) {
            getMissingExceptionTags(ex);
        }
    }

    /**
     * Extracts tag numbers from a InvalidValueException.
     * <p>
     * If exception is from a direct sub-tag of a PP-tag, direct sub-tag number = subtagNumber and PPTag is stored in errorTagArray.
     * But if exception is from a sub-tag within a direct sub-tag of a PP-tag, the indirect sub-tag number = subtagNumber and 62 is stored in errorTagArray, while the direct sub-tag = rootTag.
     * <p>
     * Eg: For PP 62 > sub-tag 50 > sub-tag 00, rootTag = 50, tag in ppTagsWithErrors = 60, subtagNumber = 00
     *
     * @param ex exception caught.
     */
    private void getInvalidExceptionTags(Exception ex) {
        InvalidTagValueException ix = ((InvalidTagValueException) ex);
        if (ix.getRootTagString() != null) {

            String rootTagStored = ix.getRootTagString();
            if (rootTagStored.contains("(in main TAG_62_ADDITIONAL_DATA)")) {
                ppTagsWithErrors.add("62");
                rootTag = rootTagStored.replace(" (in main TAG_62_ADDITIONAL_DATA)", "");
            } else {
                ppTagsWithErrors.add(ix.getRootTagString());
            }

            subtagNumber = ix.getTagString();
        } else {
            ppTagsWithErrors.add(((InvalidTagValueException) ex).getTagString());
        }
        isInvalid = true;
    }

    /**
     * Extracts tag numbers from a MissingException.
     * <p>
     * If exception is from a direct sub-tag of a PP-tag, direct sub-tag number = subtagNumber and PPTag is stored in errorTagArray.
     * But if exception is from a sub-tag within a direct sub-tag of a PP-tag, the indirect sub-tag number = subtagNumber and 62 is stored in errorTagArray, while the direct sub-tag = rootTag.
     * <p>
     * Eg: For PP 62 > sub-tag 50 > sub-tag 00, rootTag = 50, tag in ppTagsWithErrors = 60, subtagNumber = 00
     *
     * @param ex exception caught.
     */
    private void getMissingExceptionTags(Exception ex) {
        MissingTagException mx = ((MissingTagException) ex);
        ITag[] errList = mx.getMissingTags();
        for (ITag tag : errList) {

            // if root-tag not null, means problem is with sub-data of PPTag
            if (mx.getRootTag() != null) {
                String rootTagStored = mx.getRootTag();

                if (rootTagStored.contains("(in main TAG_62_ADDITIONAL_DATA)")) {
                    ppTagsWithErrors.add("62");
                    rootTag = rootTagStored.replace(" (in main TAG_62_ADDITIONAL_DATA)", "");
                } else {
                    ppTagsWithErrors.add(mx.getRootTag());
                }
                subtagNumber = tag.getTag();

            } else {
                // else, means problem is with a direct PPTag
                ppTagsWithErrors.add(tag.getTag());
            }
        }
    }

    public ArrayList<String> getTagNumbers() {
        return ppTagsWithErrors;
    }

    public boolean isInvalid() {
        return isInvalid;
    }

    public String getSubTagNumber() {
        return subtagNumber;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getRootTag() {
        return rootTag;
    }
}
