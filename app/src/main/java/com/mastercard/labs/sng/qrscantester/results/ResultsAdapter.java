package com.mastercard.labs.sng.qrscantester.results;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mastercard.labs.sng.qrscantester.R;
import com.mastercard.labs.sng.qrscantester.results.tag.SubDataModelTag;
import com.mastercard.labs.sng.qrscantester.results.tag.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaile on 4/1/18.
 * Adapter for showing list of tags in a RecyclerView.
 */

public class ResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ERROR_TAG = 0;
    private static final int REGULAR_TAG = 1;
    private static final int REGULAR_SUBDATA_TAG = 1;
    private final TagClickListener listener;

    private List<Tag> tagList;

    public ResultsAdapter(List<Tag> tagList, TagClickListener listener) {
        this.tagList = tagList;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        // Layout is inflated created accordingly to type of tag (viewType).
        if (viewType == ERROR_TAG) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.error_row_template, parent, false);
            return new ErrorViewHolder(itemView);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.regular_row_template, parent, false);
            return new RegularViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        final Tag tag = getTag(position);
        if (type == ERROR_TAG) {
            ((ErrorViewHolder) holder).bindData(tag);
            ((ErrorViewHolder) holder).bind(tag, listener);
        } else {
            ((RegularViewHolder) holder).bindData(tag);
            ((RegularViewHolder) holder).bind(tag, listener);

        }
    }

    @Override
    public int getItemCount() {
        if (tagList != null)
            return tagList.size();
        else return 0;
    }

    @Override
    public int getItemViewType(int position) {
        Tag tag = getTag(position);

        if (tag.hasError()) {
            return ERROR_TAG;
        } else if (tag instanceof SubDataModelTag)
            return REGULAR_SUBDATA_TAG;
        else return REGULAR_TAG;

    }

    public void setValues(ArrayList<Tag> values) {
        tagList = values;
    }

    private Tag getTag(int position) {
        return tagList.get(position);
    }

    /**
     * ViewHolder for regular tags.
     */
    private class RegularViewHolder extends RecyclerView.ViewHolder {
        private TextView tagNumber, tagName, moreDetails, tagValue;

        private RegularViewHolder(View view) {
            super(view);
            tagNumber = (TextView) view.findViewById(R.id.tag_number);
            tagName = (TextView) view.findViewById(R.id.tag_name);
            tagValue = (TextView) view.findViewById(R.id.tag_value);
            moreDetails = (TextView) view.findViewById(R.id.more_details);
        }

        /**
         * Controls display accordingly to values of <code>tag<code/>.
         *
         * @param tag that is being displayed
         */
        protected void bindData(Tag tag) {
            tagNumber.setText(tag.getTagNumber());
            tagName.setText(tag.getTagName());
            moreDetails.setVisibility(View.GONE);

            if (tag.getTagValue().isEmpty()) {
                tagValue.setText("Mandatory value(s) missing");
                tagValue.setTypeface(null, Typeface.ITALIC);
                tagValue.setTextColor(Color.GRAY);
                tagValue.setTextSize(16);
            } else {
                tagValue.setText(tag.getTagValue());
            }

            if (tag instanceof SubDataModelTag) {
                if (((SubDataModelTag) tag).hasSubDataModels()) {
                    moreDetails.setVisibility(View.VISIBLE);
                }
            }
        }

        public void bind(final Tag item, final TagClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    /**
     * ViewHolder for tags that cause an exception.
     */
    private class ErrorViewHolder extends RegularViewHolder {
        private TextView error;

        private ErrorViewHolder(View view) {
            super(view);
            error = (TextView) view.findViewById(R.id.error);
        }

        @Override
        protected void bindData(Tag tag) {
            super.bindData(tag);
            error.setText(tag.getErrorMessage());

            if (tag.hasInvalidValue()) {
                if (tag.getErrorMessage().isEmpty() || tag.getErrorMessage() == null) {
                    error.setVisibility(View.GONE);
                } else {
                    error.setVisibility(View.VISIBLE);
                    error.setText(tag.getErrorMessage());
                }
            } else error.setVisibility(View.GONE);
        }

        public void bind(final Tag item, final TagClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    public interface TagClickListener {
        void onItemClick(Tag item);
    }
}
