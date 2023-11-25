package com.example.notes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

//this is used to adapt data from firebase and displaying it in views
public class NoteAdapter extends FirestoreRecyclerAdapter<Note, NoteAdapter.NoteViewHolder> {
    Context context;

    //options - define how FirebaseUI should handle and display FireStore query results within a RecyclerView
    // here it contain the detail to map the firebase query result to Note class obj(specified in MainActivity while creation of options.
    public NoteAdapter(@NonNull FirestoreRecyclerOptions<Note> options, android.content.Context context) {
        super(options);
        this.context=context;
    }
    //it is also invoked implicity after creating view.
    //this method is primarily used for setting text to the new view created by onCreateViewHolder.
    //holder.title,holder.content,holder.timestamp - these are used to set text
    @Override
    protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull Note note) {
        holder.title.setText(note.title);
        holder.content.setText(note.content);
        holder.timestamp.setText(Utility.Timetostring(note.timestamp));

        //if the item view is clicked then it will be entered into the edit mode
        holder.itemView.setOnClickListener(v->{

            //intent serves passing mechanisms, data passing to the other page etc
            Intent intent = new Intent(context,NoteDetailActivity.class);

            intent.putExtra("title",note.title);

            intent.putExtra("content",note.content);

            //retrives document id which is unique
            //snapshot - static picture of particular set of data
            //snapshots contain many data. we are getting single data which is specified by the position by using getsnapshot(position)
            String docId = this.getSnapshots().getSnapshot(position).getId();
            intent.putExtra("docId",docId);

            context.startActivity(intent);

        });
    }

    @NonNull
    @Override
    //it is automatically called. it is used to create new view to add into existing views similar to parent. The xml is given for specifying how to create view. returing and creating the NoteViewHolder object.
    //while creating it calls the constructor. The constructor assign Textview values for title,content, timestamp.
    //Through this vars we can set Text to the Elements to the new view
    //primary aim is to create a new view
    //LayoutInflater is used to convert the recycler view layout to view obj.
    // ViewGroup parent - contains the existing views
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_note_item,parent,false);
        return new NoteViewHolder(view);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder{
        TextView title,content,timestamp;
        //called by NoteViewHolder
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.note_title_text_view);
            content = itemView.findViewById(R.id.note_content_text_view);
            timestamp = itemView.findViewById(R.id.note_timestamp_text_view);
        }
    }
}
