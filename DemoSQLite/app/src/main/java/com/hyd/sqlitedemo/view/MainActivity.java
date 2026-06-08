package com.hyd.sqlitedemo.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hyd.sqlitedemo.R;
import com.hyd.sqlitedemo.database.DatabaseHelper;
import com.hyd.sqlitedemo.database.model.Note;
import com.hyd.sqlitedemo.utils.MyDividerItemDecoration;
import com.hyd.sqlitedemo.utils.RecyclerTouchListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hayden
 * @create 2021/8/12 12:35 下午
 */

public class MainActivity extends AppCompatActivity {
    private NotesAdapter mAdapter;
    private List<Note> notesList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private TextView noNotesView;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        coordinatorLayout = findViewById(R.id.coordinator_layout);
        recyclerView = findViewById(R.id.recycler_view);
        noNotesView = findViewById(R.id.empty_notes_view);

        db = new DatabaseHelper(this);

        notesList.addAll(db.getAllNotes());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoteDialog(false, null, -1);
            }
        });

        mAdapter = new NotesAdapter(this, notesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        toggleEmptyNotes();

        /**
         * 长按 RecyclerView item，
         * 打开带有选择编辑和删除选项的对话框
         */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));
    }

    /**
     * 在数据库中插入新笔记并刷新列表
     */
    private void createNote(String note) {
        // 在数据库中插入笔记并获取新插入的笔记 ID
        long id = db.insertNote(note);

        // 获得从数据库新插入的笔记
        Note n = db.getNote(id);

        if (n != null) {
            // 将新笔记添加到数组列表的 0 位置
            notesList.add(0, n);

            // 刷新列表
            mAdapter.notifyDataSetChanged();

            toggleEmptyNotes();
        }
    }

    /**
     * 更新数据库中的注释并按其位置更新列表中的项目
     */
    private void updateNote(String note, int position) {
        Note n = notesList.get(position);
        // 更新笔记文本
        n.setNote(note);

        // 更新数据库中的笔记
        db.updateNote(n);

        // refreshing the list
        notesList.set(position, n);
        mAdapter.notifyItemChanged(position);

        toggleEmptyNotes();
    }

    /**
     * Deleting note from SQLite and removing the
     * item from the list by its position
     */
    private void deleteNote(int position) {
        // deleting the note from db
        db.deleteNote(notesList.get(position));

        // removing the note from the list
        notesList.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptyNotes();
    }

    /**
     * 打开带有编辑 - 删除选项的对话框
     * Edit - 0
     * Delete - 0
     */
    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"编辑", "删除"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showNoteDialog(true, notesList.get(position), position);
                } else {
                    deleteNote(position);
                }
            }
        });
        builder.show();
    }


    /**
     * 显示带有 EditText 选项的提示框以输入编辑注释。当 shouldUpdate=true 时，它会自动显示旧笔记并将按钮文本更改为UPDATE
     */
    private void showNoteDialog(final boolean shouldUpdate, final Note note, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.note_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputNote = view.findViewById(R.id.note);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));

        if (shouldUpdate && note != null) {
            inputNote.setText(note.getNote());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "更新" : "保存", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 未输入文本时显示吐司消息
                if (TextUtils.isEmpty(inputNote.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter note!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // 检查用户是否更新笔记
                if (shouldUpdate && note != null) {
                    // 通过它的 id 更新笔记
                    updateNote(inputNote.getText().toString(), position);
                } else {
                    // 创建新笔记
                    createNote(inputNote.getText().toString());
                    test();
                }
            }
        });
    }

    private void test() {
        File file = MainActivity.this.getFilesDir();

        // 在 app 私有路径下创建文件夹
        File newFolder = new File(file, "my_folder");
        if (!newFolder.exists()) {
            boolean created = newFolder.mkdirs();
            if (created) {
                Toast.makeText(this, "文件夹创建成功：" + newFolder.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "文件夹创建失败", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "文件夹已存在", Toast.LENGTH_SHORT).show();
        }

        // 或者使用缓存目录
        File cacheFolder = new File(getCacheDir(), "cache_folder");
        cacheFolder.mkdirs();

        // 获取外部存储的私有目录
        File externalFilesDir = getExternalFilesDir(null);
        File externalFolder = new File(externalFilesDir, "external_folder");
        externalFolder.mkdirs();
    }

    /**
     * 切换列表和空笔记视图
     */
    private void toggleEmptyNotes() {
        // 你可以检查 notesList.size() > 0

        if (db.getNotesCount() > 0) {
            noNotesView.setVisibility(View.GONE);
        } else {
            noNotesView.setVisibility(View.VISIBLE);
        }
    }
}
