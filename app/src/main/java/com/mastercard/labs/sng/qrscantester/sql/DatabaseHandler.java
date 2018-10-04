package com.mastercard.labs.sng.qrscantester.sql;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.mastercard.labs.sng.qrscantester.model.TransactionLocal;

import java.security.Key;
import java.util.ArrayList;

/**
 * Created by nvtamcntt on 2018/10/03.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "example";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "tb_transaction";

    private static final String ID = "id";
    private static final String KEY_ID = "key_id";
    private static final String TRANSFER_REFERENCE = "transfer_reference";
    private static final String STATUS = "status";
    private static final String RESOURCE_TYPE = "resource_type";
    private static final String SENDER_ACCOUNT_URI = "sender_account_uri";
    private static final String ORIGINAL_STATUS = "original_status";
    private static final String TRANSACTION_AMOUNT = "transfer_amount";
    private static final String STORE_NAME = "store_name";

    public DatabaseHandler(Context context,SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQLQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_ID + " TEXT, " +
                TRANSFER_REFERENCE + " VARCHAR(200), " +
                STATUS + " TEXT, " +
                RESOURCE_TYPE + " VARCHAR(200), " +
                SENDER_ACCOUNT_URI + " VARCHAR(200), " +
                ORIGINAL_STATUS + " VARCHAR(200), " +
                TRANSACTION_AMOUNT + " VARCHAR(200), " +
                STORE_NAME + " VARCHAR(200) )";
        System.out.print("@@@" + SQLQuery);
        sqLiteDatabase.execSQL(SQLQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String create_transaction_table = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);
        sqLiteDatabase.execSQL(create_transaction_table);

        onCreate(sqLiteDatabase);
    }
    public boolean addTransaction (TransactionLocal transactionLocal){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, transactionLocal.getId());
        values.put(TRANSFER_REFERENCE, transactionLocal.getTransfer_reference());
        values.put(STATUS, transactionLocal.getStatus());
        values.put(RESOURCE_TYPE, transactionLocal.getResource_type());
        values.put(SENDER_ACCOUNT_URI, transactionLocal.getSender_account_uri());
        values.put(ORIGINAL_STATUS, transactionLocal.getOriginal_status());
        values.put(TRANSACTION_AMOUNT, transactionLocal.getTransaction_amount());
        values.put(STORE_NAME, transactionLocal.getStore_name());

        db.insert(TABLE_NAME, null, values);
        db.close();
        return true;
    }

    public  TransactionLocal getTransaction (String id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, null, KEY_ID + " =? ", new String[]{id},null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
            TransactionLocal local = new TransactionLocal(
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8));
            return local;
        }
        return null;
    }

    public ArrayList<TransactionLocal> getAllTransactions()
    {
        ArrayList<TransactionLocal> array_list = new ArrayList<TransactionLocal>();

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from " + TABLE_NAME;
        Cursor cursor =  db.rawQuery( sql, null );
        cursor.moveToFirst();

        while(cursor.isAfterLast() == false){
            TransactionLocal local = new TransactionLocal(
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8));
            array_list.add(local);
            cursor.moveToNext();
        }
        return array_list;
    }
}
