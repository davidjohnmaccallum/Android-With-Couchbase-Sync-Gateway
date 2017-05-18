package za.co.einsight.couchbaseandroid;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Mapper;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private Database mDatabase;
    private LiveQuery listsLiveQuery;
    private ListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.list);
        mDatabase = ((Application)getApplication()).getDatabase();

        setupViewAndQuery();

        mAdapter = new ListAdapter(this, listsLiveQuery);
        listView.setAdapter(mAdapter);

    }

    private void setupViewAndQuery() {
        if (mDatabase == null) {
            return;
        }
        com.couchbase.lite.View listsView = mDatabase.getView("list/listsByTitle");

        if (listsView.getMap() == null) {

            listsView.setMap(new Mapper() {

                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    emitter.emit(document.get("title"), null);
                }

            }, "1.0");

        }

        listsLiveQuery = listsView.createQuery().toLiveQuery();

    }

    private class ListAdapter extends LiveQueryAdapter {
        public ListAdapter(Context context, LiveQuery query) {
            super(context, query);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(android.R.layout.simple_list_item_2, null);
            }

            final Document doc = (Document) getItem(position);
            String key = (String) getKey(position);

            TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
            TextView text2 = (TextView) convertView.findViewById(android.R.id.text2);

            text1.setText((String)doc.getProperty("title"));
            text2.setText((String)doc.getProperty("user"));

            return convertView;
        }

    }

}

