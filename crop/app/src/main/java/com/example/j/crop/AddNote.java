package com.example.j.crop;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.lang.ref.WeakReference;

/**
 * Created by J on 4/9/2018.
 * (AddNoteActivity)
 */

public class AddNote extends AppCompatActivity {

    // private AppDatabase mDb;
    // private TextView notes;
    /*database*/
    private AppDatabase plantDatabase;
    private Note note;
    private boolean update;

    private TextInputEditText et_title, et_content;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);

        //Intent intent = getIntent();

    et_title = findViewById(R.id.et_title);
    et_content = findViewById(R.id.et_content);

    plantDatabase = AppDatabase.getInstance(AddNote.this);

    final AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "notedb.db").build();


    Button button = findViewById(R.id.but_save);

        if ( (note = (Note) getIntent().getSerializableExtra("note"))!=null ){
        getSupportActionBar().setTitle("Update Note");
        update = true;
        button.setText("Update");
        et_title.setText(note.getTitle());
        et_content.setText(note.getContent());
    }


    //AppDatabase.databaseFunc().updateNote(note);

    //upload note
        button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // fetch data and create note object
            if (update) {
                note.setContent(et_content.getText().toString());
                note.setTitle(et_title.getText().toString());
                db.databaseFunc().updateNote(note);
                setResult(note,2);
            } else {
                note = new Note(et_content.getText().toString(), et_title.getText().toString());
                new InsertTask(AddNote.this, note).execute();
            }
        }});


/*
        final EditText et = (EditText) findViewById(R.id.editText);
        //display the notes from Database
        notes = findViewById(R.id.notes);
        */


}


    private void setResult(Note note, int flag){
        setResult(flag,new Intent().putExtra("note",note));
        finish();
    }

//display the notes from Database
// notes = findViewById(R.id.notes);


private static class InsertTask extends AsyncTask<Void, Void, Boolean> {

    private WeakReference<AddNote> activityReference;
    private Note note;

    // only retain a weak reference to the activity
    InsertTask(AddNote context, Note note) {
        activityReference = new WeakReference<>(context);
        this.note = note;
    }

    // doInBackground methods runs on a worker thread
    @Override
    protected Boolean doInBackground(Void... objs) {
        long j = activityReference.get().plantDatabase.databaseFunc().insertNote(note);
        note.setNote_id(j);
        Log.e("ID ", "doInBackground: "+j );
        return true;
    }

    // onPostExecute runs on main thread
    @Override
    protected void onPostExecute(Boolean bool) {
        if (bool) {
            activityReference.get().setResult(note, 1);
            activityReference.get().finish();
        }
    }
}
}

