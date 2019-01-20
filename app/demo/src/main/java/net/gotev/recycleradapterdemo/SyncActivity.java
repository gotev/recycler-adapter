package net.gotev.recycleradapterdemo;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import net.gotev.recycleradapter.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SyncActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private RecyclerAdapter adapter;
    private Random random;

    public static void show(AppCompatActivity activity) {
        Intent intent = new Intent(activity, SyncActivity.class);
        activity.startActivity(intent);
    }

    private Random getRandom() {
        if (random == null) {
            random = new Random(System.currentTimeMillis());
        }
        return random;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        ButterKnife.bind(this);

        adapter = new RecyclerAdapter();
        adapter.setEmptyItem(new EmptyItem(getString(R.string.empty_list)));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item == null)
            return false;

        switch (item.getItemId()) {
            case R.id.sort_ascending:
                adapter.sort(true);
                return true;

            case R.id.sort_descending:
                adapter.sort(false);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.syncA)
    public void onSyncA() {
        adapter.syncWithItems(getListA());
    }

    @OnClick(R.id.syncB)
    public void onSyncB() {
        adapter.syncWithItems(getListB());
    }

    @OnClick(R.id.syncC)
    public void onSyncC() {
        adapter.syncWithItems(getListC());
    }

    private List<SyncItem> getListA() {
        List<SyncItem> list = new ArrayList<>();

        list.add(new SyncItem(this, 1, "listA"));
        list.add(new SyncItem(this, 2, "listA"));
        list.add(new SyncItem(this, 3, "listA"));

        return list;
    }

    private List<SyncItem> getListB() {
        List<SyncItem> list = new ArrayList<>();

        list.add(new SyncItem(this, 1, "listB"));
        list.add(new SyncItem(this, 4, "listB"));
        list.add(new SyncItem(this, 5, "listB"));

        return list;
    }

    private List<SyncItem> getListC() {
        List<SyncItem> list = new ArrayList<>();

        list.add(new SyncItem(this, 1, "listC"));

        return list;
    }
}
