package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {

    NoteAdapter noteAdapter;
    FloatingActionButton addNoteBtn;
    RecyclerView recyclerView;
    ImageButton menuBtn;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addNoteBtn = findViewById(R.id.add_note_btn);
        recyclerView = findViewById(R.id.recycler_view);
        menuBtn = findViewById(R.id.note_menu_btn);

        addNoteBtn.setOnClickListener(v-> startActivity(new Intent(MainActivity.this,NoteDetailActivity.class)));
        menuBtn.setOnClickListener(v-> showmenu());
        setupRecyclerView();
    }
    void showmenu(){
        //popup will be shown in the mainActivity under button menuBtn
        PopupMenu popupMenu = new PopupMenu(MainActivity.this,menuBtn);

        //getting the menu and adding the logout button into it
        popupMenu.getMenu().add("Logout");

        popupMenu.show();

        //whenever they click on any menu this will enabled
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getTitle()=="Logout"){
                    //removing or invalidating the session(added in utility) from the local device. when we open app, it will directs to login page
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });

    }
    void setupRecyclerView(){
        //writing a query to retrive the notes of the current user in the order of descending order of timestamp
        Query query = Utility.getCollectionReferenceForNotes().orderBy("timestamp", Query.Direction.DESCENDING);

        //FirestoreRecyclerOption.Builder()n- is user to create option which contain how the firebase  should display the documents
        //here the query and class is passed that the setquery will execute the query and the results are stored in class
        //<Note> - denotes that the fetched is of type Note obj
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>().setQuery(query,Note.class).build();

        //setting the recyclerview to linear layout(views are arranged in linear)
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //creating an obj for NoteAdapter it calls constructor
        noteAdapter =  new NoteAdapter(options,this);

        //by using setadapter the recyclerview is linked to noteAdpter
        //that the view creation and the data handling will be done by noteAdapter
        recyclerView.setAdapter(noteAdapter);
    }

    @Override

    //this is invoked when the page is started
    //by startListening method, we are trying to listen for the changes in the documents if there is any change then it is updated in the views
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    //this is invoked when the page is stopped
    protected void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }

    @Override

    //this is invoked when the page is resumed after a pause
    //check for updates after resuming in the documents which may be happened in pause state
    protected void onResume() {
        super.onResume();
        noteAdapter.notifyDataSetChanged();
    }
}