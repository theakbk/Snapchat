package com.example.snapchat.repo;

import android.graphics.Bitmap;

import com.example.snapchat.TaskListener;
import com.example.snapchat.Updatable;
import com.example.snapchat.model.Note;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
//import com.joneikholm.listview21spring.TaskListener;
//import com.joneikholm.listview21spring.Updatable;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Repo {
/*    static {
        FirebaseApp.initializeApp();
    }*/

    //singleton
    private static Repo repo = new Repo();

    //DATABASE
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();


    public List<Note> notes = new ArrayList<>(); // you could use Note, instead of String

    private final String NOTES = "Snaps";
    private Updatable activity;

    public static Repo r(){
        return repo;
    }

    private Repo()
    {
        startListener();
    }


    public void setup(Updatable a){
        activity = a;
//        startListener();
    }

    public Note getNoteById(String id){
        for(Note note : notes){
            if(note.getId().equals(id)){
                return note;
            }
        }
        return null;
    }

    public void startListener(){
        db.collection(NOTES).addSnapshotListener((values, error) ->{
            System.out.println("Firebase data " + values.size());
            notes.clear();
            for(DocumentSnapshot snap: values.getDocuments()){
                Object title = snap.get("title");
                if(title != null){
                    notes.add(new Note(snap.getId(),title.toString()));
                }
                System.out.println("Snap: " + snap.toString());
            }
            // have a reference to MainActivity, and call a update()
            activity.update(null);
        });
    }

    //Add note to firebase
    public Note addNote(String text) {
        // insert a new note with "new note"
        DocumentReference ref = db.collection(NOTES).document();
        Map<String,String> map = new HashMap<>();
        map.put("title", text);
        ref.set(map); // will replace any previous value.
        //db.collection("notes").add(map); // short version
        System.out.println("Done inserting new document " + ref.getId());
        Note n = new Note(ref.getId(), text);
        notes.add(n);
        return n;
    }

    //Upload manipulated picture to bitmap
    public void addNoteAndImage(String text, Bitmap bitmap)
    {
        Note note = addNote(text);
        System.out.println("uploadBitmap called " + bitmap.getByteCount());
        StorageReference ref = storage.getReference(note.getId());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        ref.putBytes(baos.toByteArray()).addOnCompleteListener(snap -> {
            System.out.println("OK to upload " + snap);
        }).addOnFailureListener(exception -> {
            System.out.println("failure to upload " + exception);
        });


    }


    //delete Note from firebase database - called after deleting the picture - important!
    public void deleteNote(String id){
        DocumentReference docRef = db.collection(NOTES).document(id);
        docRef.delete();
    }




    public void downloadBitmap(String id, TaskListener taskListener)
    {
        StorageReference ref = storage.getReference(id);
        int max = 1024 * 1024; // you are free to set the limit here
        ref.getBytes(max).addOnSuccessListener(bytes -> {
            taskListener.receive(bytes);
            System.out.println("Download OK");
        }).addOnFailureListener(ex -> {
            System.out.println("error in download " + ex);
        });

    }

    //Important method
    //The concept of snapchat is to send a picture and and delete it after the receiver has seen it.
    //Which is why i chose this method as one of the important ones.
    //This method deletes a picture, associated with a note. Its important to delete the picture first, because if
    //the notes gets deleted, the id for the picture is gone.
    //The method makes a reference to firebase storage, to be able to access the pictures id.
    //storageReference has a delete-method of its own so its quite simple.
    //and after deleting the picture, i delete the actual note.
    //To see the imidiate change on the listview, I make sure to delete the note from the arraylist also.
    // Rather then waiting for the list to be filled up with the new elements from firebase
    public void deleteById(String id)
    {
        StorageReference ref = storage.getReference(id);
        if (ref!=null)
        {
            ref.delete();
            notes.remove(id);
        }
        deleteNote(id);

        //This would be much faster on a hashmap, but because I have an ArrayList, this is how I choose to delete
        for(Note n:notes)
        {
            if(n.getId().equals(id))
            {
                notes.remove(n);
            }
        }


    }


    //ChatListAdaptor interface
    public int size()
    {
        return notes.size();
    }

    public Note get(int i)
    {
        return notes.get(i);

    }

    /*public void updateNoteAndImage(Note note, Bitmap bitmap) {
        updateNote(note);
        System.out.println("uploadBitmap called " + bitmap.getByteCount());
        StorageReference ref = storage.getReference(note.getId());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        ref.putBytes(baos.toByteArray()).addOnCompleteListener(snap -> {
            System.out.println("OK to upload " + snap);
        }).addOnFailureListener(exception -> {
            System.out.println("failure to upload " + exception);
        });
    }

    public void updateNote(Note note) {
        DocumentReference ref = db.collection(NOTES).document(note.getId());
        Map<String,String> map = new HashMap<>();
        map.put("title", note.getText());
        ref.set(map); // will replace any previous value.
        //ref.update("key", "value"); // for updating single values, instead of the whole document.
        System.out.println("Done updating document " + ref.getId());
    }

     */
}
