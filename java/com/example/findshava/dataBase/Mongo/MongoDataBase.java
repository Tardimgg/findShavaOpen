package com.example.findshava.dataBase.Mongo;

import android.util.Log;

import com.example.findshava.customClass.SafeRunnable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.lang.NonNull;
import com.mongodb.lang.Nullable;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;
import com.mongodb.stitch.core.services.mongodb.remote.ChangeEvent;
import com.mongodb.stitch.core.services.mongodb.remote.sync.ChangeEventListener;
import com.mongodb.stitch.core.services.mongodb.remote.sync.DefaultSyncConflictResolvers;
import com.mongodb.stitch.core.services.mongodb.remote.sync.ErrorListener;
import com.mongodb.stitch.core.services.mongodb.remote.sync.SyncDeleteResult;
import com.mongodb.stitch.core.services.mongodb.remote.sync.SyncInsertOneResult;

import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MongoDataBase {

    private volatile StitchAppClient client =
            Stitch.initializeDefaultAppClient("your code");

    private volatile RemoteMongoClient mongoClient =
            client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");

    private volatile RemoteMongoCollection<Document> coll =
            mongoClient.getDatabase("your db").getCollection("your collection");

    //private volatile GridFSBucket bucket = GridFSBuckets.create(mongoClient.getDatabase("location"))

    private volatile ArrayList<Document> info;
    private static volatile MongoDataBase dataBase;
    private boolean readyBD = false;


    private MongoDataBase() {
        new SafeRunnable(() -> {
            Log.i("MongoDataBase", "create new MongoDataBase");
            Log.e("start login", "test");
            client.getAuth().loginWithCredential(new AnonymousCredential()).addOnCompleteListener((task -> {
                if (!task.isSuccessful()) {
                    Log.e("STITCH", "Login failed!");
                } else {
                    coll.sync().configure(DefaultSyncConflictResolvers.remoteWins(), new MongoDataBase.MyUpdateListener(), new MongoDataBase.MyErrorListener()).addOnCompleteListener(task1 -> Log.i("end sync", "test"));
                    Log.i("STITCH", "Login successful");
                    MongoDataBase.this.readyBD = true;
                }
            }
            ));
            Log.e("end loader", "test");
        }).run();

    }

    static MongoDataBase getInstance() {
        if (MongoDataBase.dataBase == null) {
            MongoDataBase.dataBase = new MongoDataBase();
        }
        Log.i("MongoDataBase", "return instance");
        return MongoDataBase.dataBase;
    }

    private class MyErrorListener implements ErrorListener {
        @Override
        public void onError(BsonValue documentId, Exception error) {
            Log.e("Stitch", error.getLocalizedMessage() == null ? "error" : error.getLocalizedMessage());
            Set<BsonValue> docsThatNeedToBeFixed = coll.sync().getPausedDocumentIds().getResult();
            if (docsThatNeedToBeFixed != null) {
                for (BsonValue doc_id : docsThatNeedToBeFixed) {
                    // Add your logic to inform the user.
                    // When errors have been resolved, call
                    coll.sync().resumeSyncForDocument(doc_id);
                }
            }
            // refresh the app view, etc.
        }
    }

    private class MyUpdateListener implements ChangeEventListener<Document> {
        @Override
        public void onEvent(final BsonValue documentId, final ChangeEvent<Document> event) {
            if (!event.hasUncommittedWrites()) {
                // Custom actions can go here
            }
            // refresh the app view, etc.
        }
    }

    void getAll(@Nullable GettingInfoListener listener) {
        if (this.readyBD) {
            getPr(new Document(), new Document(), listener);
        } else {
            if (listener != null) {
                listener.addOnCompleteListener(null);
            }
        }

    }

    void get(@NonNull Bson filter, @Nullable GettingInfoListener listener) {
        if (this.readyBD) {
            getPr(filter, new Document(), listener);
        } else {
            if (listener != null) {
                listener.addOnCompleteListener(null);
            }
        }
    }

    void get(@NonNull Bson filter, @NonNull Bson projection, @Nullable GettingInfoListener listener) {
        if (this.readyBD) {
            getPr(filter, projection, listener);
        } else {
            if (listener != null) {
                listener.addOnCompleteListener(null);
            }
        }
    }

    private void getPr(Bson filter, Bson projection, @Nullable GettingInfoListener listener) {
        new SafeRunnable(() -> {
            List<Document> answer = Collections.synchronizedList(new ArrayList<>());
            assert coll != null;
            OnCompleteListener<Void> onComplete = new OnCompleteListener<Void>() {

                private volatile int countComplete = 0;

                @Override
                public synchronized void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                    this.countComplete++;
                    if (task.isSuccessful()) {
                        Log.i("db", "file was received successfully");
                    } else {
                        Log.e("db", "Error getting the file" +
                                (task.getException() == null ? "null" : task.getException()));
                    }
                    if (listener != null && this.countComplete == 2) {
                        listener.addOnCompleteListener(answer);
                    }
                }
            };
            coll.sync().find(filter).projection(projection).forEach(answer::add).addOnCompleteListener(onComplete);
            coll.find(filter).projection(projection).forEach(answer::add).addOnCompleteListener(onComplete);
        }).run();
    }

    void update(@NonNull Bson filter, @NonNull Bson document) {
        if (this.readyBD) {
            new SafeRunnable(() ->
                    coll.sync().find(filter).first().addOnCompleteListener(new OnCompleteListener<Document>() {
                        @Override
                        public void onComplete(@androidx.annotation.NonNull Task<Document> task) {
                            OnCompleteListener onComplete = (voidTask) -> {
                                if (voidTask.isSuccessful()) {
                                    Log.i("db", "file was updated successfully");
                                } else {
                                    Log.e("db", "Error update an element" +
                                            (voidTask.getException() == null ? "null" : voidTask.getException().toString()));
                                }
                            };
                            if (task.getResult() != null) {
                                coll.sync().updateOne(filter, document).addOnCompleteListener(onComplete);
                            } else {
                                coll.updateOne(filter, document).addOnCompleteListener(onComplete);
                            }
                        }
                    })
            ).run();
        }

    }

    void delete(@NonNull Bson filter) {
        if (this.readyBD) {
            new SafeRunnable(() -> coll.sync().deleteOne(filter).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.i("db", "file was deleted successfully");
                } else {
                    Log.e("db", "Error deleting an element" +
                            (task.getException() == null ? "null" : task.getException().toString()));
                }
            })).run();
        }
    }

    void add(@NonNull Document document) {
        if (this.readyBD) {
            new SafeRunnable(() -> {
                final Task<SyncInsertOneResult> res = coll.sync().insertOne(document);
                res.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.println(Log.INFO, "db", "file was added successfully");
                    } else {
                        Log.e("db", "Error adding item" +
                                (task.getException() == null ? "null" : task.getException().toString()));
                    }
                });
            }).run();
        }

    }

    void save(@NonNull Document document) {
        if (this.readyBD) {

            coll.find(document).first().addOnCompleteListener((Task<Document> task) -> {
                if (task.getResult() != null) {
                    coll.sync().insertOne(task.getResult()).addOnCompleteListener((Task<SyncInsertOneResult> saveTask) -> {
                        if (saveTask.isSuccessful()) {
                            Log.println(Log.INFO, "db", "file was saved successfully");
                        } else {
                            Log.e("db", "Error saving item" +
                                    (saveTask.getException() == null ? "null" : saveTask.getException().toString()));
                        }
                    });
                }
            });
        }
    }

    void exit() {
        try {
            client.close();
        } catch (IOException e) {
            Log.e("Exception close bd", e.getMessage() == null ? "error exit MongoDataBase" : e.getMessage());
        }
    }

    @Override
    protected void finalize() throws Throwable {
        exit();
        super.finalize();
    }

    interface GettingInfoListener {

        void addOnCompleteListener(@Nullable List<Document> info);
    }
}
