package com.mastercard.labs.sng.qrscantester;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mastercard.labs.sng.qrscantester.results.ResultsAdapter;
import com.mastercard.labs.sng.qrscantester.results.tag.SubDataModelTag;
import com.mastercard.labs.sng.qrscantester.results.tag.Tag;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link com.mastercard.labs.sng.qrscantester.results.ResultsAdapter.TagClickListener}
 * interface.
 */
public class DetailFragment extends Fragment {

    public static final String TAG_INSTANCE = "TAG_INSTANCE";
    public static final String IS_GENERATE = "IS_GENERATE";

    private ArrayList<Tag> dataSource = new ArrayList<>();
    private ResultsAdapter listAdapter;
    private Bundle bundle;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DetailFragment() {
    }

    @SuppressWarnings("unused")
    public static DetailFragment newInstance(int columnCount) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDataSource(getArguments());
    }


    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            listAdapter = new ResultsAdapter(dataSource, new ResultsAdapter.TagClickListener() {
                @Override
                public void onItemClick(Tag item) {
                    openDetailResult(item);
                }
            });

            recyclerView.setFocusable(false);

            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(listAdapter);
        }

        return view;
    }

    /**
     * Updates tag list of adapter.
     *
     * @param bundle
     */
    public void relayout(Bundle bundle) {
        setupDataSource(bundle);
        listAdapter.setValues(dataSource);
        listAdapter.notifyDataSetChanged();
    }

    /**
     * Updates tag list to display.
     *
     * @param bundle
     */
    void setupDataSource(Bundle bundle) {
        this.bundle = bundle;

        if (bundle != null) {
            SubDataModelTag mainTag = (SubDataModelTag) (bundle.getSerializable(TAG_INSTANCE));
            dataSource = mainTag.getSubData();
        }
    }

    /**
     * Begins activity to display details for selected tag in list.
     *
     * @param tag tag that has been selected
     */
    public void openDetailResult(Tag tag) {
        boolean isGenerate;
        if (bundle != null) {

            isGenerate = bundle.getBoolean(IS_GENERATE);

            Intent openDetailResultIntent = new Intent(getContext(), DetailResultActivity.class);
            Bundle bundle = new Bundle();

            if (!isGenerate) {
                bundle.putSerializable(IS_GENERATE, false);
            } else {
                bundle.putSerializable(IS_GENERATE, true);
            }
            bundle.putSerializable(TAG_INSTANCE, tag);
            openDetailResultIntent.putExtras(bundle);
            startActivity(openDetailResultIntent);
        }
    }
}
