package za.co.einsight.couchbaseandroid;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseOptions;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.replicator.Replication;
import com.couchbase.lite.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Application extends android.app.Application implements Replication.ChangeListener {

    private static final String LOG_TAG = "Workflow";
    private Database database;
    private Replication pushReplication;
    private Replication pullReplication;

    @Override
    public void onCreate() {
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                Log.e(LOG_TAG, "Uncaught exception", throwable);
            }
        });

        Manager.enableLogging(LOG_TAG, Log.VERBOSE);
        Manager.enableLogging(Log.TAG, Log.VERBOSE);
        Manager.enableLogging(Log.TAG_SYNC_ASYNC_TASK, Log.VERBOSE);
        Manager.enableLogging(Log.TAG_SYNC, Log.VERBOSE);
        Manager.enableLogging(Log.TAG_QUERY, Log.VERBOSE);
        Manager.enableLogging(Log.TAG_VIEW, Log.VERBOSE);
        Manager.enableLogging(Log.TAG_DATABASE, Log.VERBOSE);

        DatabaseOptions options = new DatabaseOptions();
        options.setCreate(true);
        try {
            Manager manager = new Manager(new AndroidContext(getApplicationContext()), Manager.DEFAULT_OPTIONS);
            database = manager.openDatabase("db", options);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        dumpDb();

        URL syncUrl;
        try {
            syncUrl = new URL(getString(R.string.sync_gateway_url));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        pullReplication = database.createPullReplication(syncUrl);
        List<String> channels = new ArrayList<>();
        channels.add("david.maccallum@einsight.co.za");
        pullReplication.setChannels(channels);
        pullReplication.setContinuous(true);

        pushReplication = database.createPushReplication(syncUrl);
        pushReplication.setContinuous(true);

        pullReplication.start();
        pushReplication.start();

        pullReplication.addChangeListener(this);
        pushReplication.addChangeListener(this);

    }

    private void dumpDb() {

        try {

            Log.i(LOG_TAG, "Doc count:" + database.getDocumentCount());

            Query query = database.createAllDocumentsQuery();
            query.setAllDocsMode(Query.AllDocsMode.INCLUDE_DELETED);
            QueryEnumerator result = query.run();
            for ( Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Log.i(LOG_TAG, row.toString());
            }

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

    }

    public Database getDatabase() {
        return database;
    }

    @Override
    public void changed(Replication.ChangeEvent event) {

        Log.i(LOG_TAG, event.toString());

        ReplicationEvent revent = new ReplicationEvent();

        revent.setActive(
                (pullReplication.getStatus() == Replication.ReplicationStatus.REPLICATION_ACTIVE) ||
                        (pushReplication.getStatus() == Replication.ReplicationStatus.REPLICATION_ACTIVE));

        if (revent.isActive()) {
            int total = pushReplication.getCompletedChangesCount() + pullReplication.getCompletedChangesCount();
            revent.setTotal(total);
            revent.setProgress(pushReplication.getChangesCount() + pullReplication.getChangesCount());
        }

        EventBus.getDefault().post(revent);

    }

}
