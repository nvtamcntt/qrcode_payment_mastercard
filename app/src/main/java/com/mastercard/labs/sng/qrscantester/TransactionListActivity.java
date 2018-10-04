package com.mastercard.labs.sng.qrscantester;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.mastercard.labs.sng.qrscantester.api.ILoadMore;
import com.mastercard.labs.sng.qrscantester.model.Item;
import com.mastercard.labs.sng.qrscantester.model.TransactionLocal;
import com.mastercard.labs.sng.qrscantester.results.ResultsActivity;
import com.mastercard.labs.sng.qrscantester.results.TransactionAdapter;
import com.mastercard.labs.sng.qrscantester.sql.DatabaseHandler;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nvtamcntt on 2018/10/04.
 */

public class TransactionListActivity extends ResultsActivity {

    private List<Item> items = new ArrayList<>();
    private TransactionAdapter mAdapter;
    private DatabaseHandler mDatabaseHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_list_layout);

        mDatabaseHandler = new DatabaseHandler(this, null);

        ArrayList<TransactionLocal> listTransactions = mDatabaseHandler.getAllTransactions();
        if (listTransactions != null)
            items = mappingObjectToList(listTransactions);

        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new TransactionAdapter(recycler, this, items);

        recycler.setAdapter(mAdapter);

        hideProcessingPaymentLoading();

        //Set Load more event
        mAdapter.setLoadMore(new ILoadMore() {
            @Override
            public void onLoadMore() {
                if (items.size() <= 10){
                    items.add(null);
                    mAdapter.notifyItemInserted(items.size()-1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            items.remove(items.size() -1);
//                            mAdapter.notifyItemRemoved(items.size());

                            mAdapter.notifyDataSetChanged();
                            mAdapter.setLoaded();

                        }
                    }, 2000);
                }else {
                    Toast.makeText(TransactionListActivity.this, "Load data completed !", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public List<Item> mappingObjectToList(ArrayList<TransactionLocal> list) {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Item item = new Item(list.get(i).getId(), list.get(i).getTransaction_amount(),list.get(i).getStore_name());
            items.add(item);
        }
        return items;
    }
}
