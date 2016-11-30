package net.gotev.recycleradapterdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import net.gotev.recycleradapter.RecyclerAdapter;

import java.util.Random;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private RecyclerAdapter adapter;
    private Random random;

    private Random getRandom() {
        if (random == null) {
            random = new Random(System.currentTimeMillis());
        }
        return random;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        adapter = new RecyclerAdapter();
        adapter.setEmptyItem(new EmptyItem(getString(R.string.empty_list)));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        adapter.enableDragDrop(recyclerView);

        for (int i = 0; i < getRandom().nextInt(200) + 50; i++) {
            if (i % 2 == 0)
                adapter.add(new ExampleItem("example item " + i));
            else
                adapter.add(new TextWithButtonItem("text with button " + i));
        }
    }

    @OnClick(R.id.remove_all_items_of_a_kind)
    public void onRemoveAllItemsOfAkind() {
        adapter.removeAllItemsWithClass(ExampleItem.class);
    }

    @OnClick(R.id.remove_last_item_of_a_kind)
    public void onRemoveLastItemOfAkind() {
        adapter.removeLastItemWithClass(TextWithButtonItem.class);
    }

    @OnClick(R.id.add_item)
    public void onAddItem() {
        adapter.add(new ExampleItem("added item " + UUID.randomUUID().toString()));
    }
}
