package net.gotev.recycleradapterdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
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
