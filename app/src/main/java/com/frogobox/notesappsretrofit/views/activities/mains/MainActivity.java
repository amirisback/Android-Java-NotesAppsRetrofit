package com.frogobox.notesappsretrofit.views.activities.mains;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.frogobox.notesappsretrofit.R;
import com.frogobox.notesappsretrofit.models.Note;
import com.frogobox.notesappsretrofit.presenters.MainPresenter;
import com.frogobox.notesappsretrofit.views.activities.editors.EditorActivity;
import com.frogobox.notesappsretrofit.views.adapters.NotesRecyclerViewAdapter;
import com.frogobox.notesappsretrofit.views.interfaces.ItemClickListener;
import com.frogobox.notesappsretrofit.views.interfaces.MainView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MainView {

    private static final int INTENT_EDIT = 200;
    private static final int INTENT_ADD = 100;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private MainPresenter presenter;
    private NotesRecyclerViewAdapter adapter;
    private ItemClickListener itemClickListener;

    private List<Note> note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        recyclerView = findViewById(R.id.recycler_views);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        FloatingActionButton fab = findViewById(R.id.add_notes);
        fab.setOnClickListener(view -> {
            Intent i = new Intent(this, EditorActivity.class);
            startActivityForResult(i, INTENT_ADD);
        });

        presenter = new MainPresenter(this);
        presenter.getData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getData();
            }
        });

        itemClickListener = (
                (view, position) -> {

                    Note data = note.get(position);
                    Intent intentData = new Intent(MainActivity.this, EditorActivity.class);
                    intentData.putExtra(EditorActivity.EXTRA_DATA, data);
                    startActivityForResult(intentData, INTENT_EDIT);
                }

                );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == INTENT_ADD && resultCode == RESULT_OK) {
            presenter.getData();
        } else if (requestCode == INTENT_EDIT && resultCode == RESULT_OK) {
            presenter.getData();
        }



    }

    @Override
    public void showLoading() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onGetResult(List<Note> notes) {
        adapter = new NotesRecyclerViewAdapter(this, notes, itemClickListener);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        note = notes;

    }

    @Override
    public void onErrorLoading(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
