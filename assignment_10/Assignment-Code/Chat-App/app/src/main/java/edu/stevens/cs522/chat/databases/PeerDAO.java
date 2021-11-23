package edu.stevens.cs522.chat.databases;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

import edu.stevens.cs522.chat.entities.Peer;

@Dao
public abstract class PeerDAO {

    /**
     * Get all peers in the database.
     *
     * @return
     */
    @Query("SELECT * FROM Peer")
    public abstract LiveData<List<Peer>> fetchAllPeers();

    /**
     * Get a single peer record (may be used in later assignments)
     *
     * @param peerId
     * @return
     */
    @Query("SELECT * FROM Peer WHERE id=:peerId")
    public abstract ListenableFuture<Peer> fetchPeer(long peerId);

    /**
     * Get the database primary key for a peer, based on chat name.
     *
     * @param name
     * @return
     */
    @Query("SELECT id FROM Peer WHERE name=:name")
    protected abstract long getPeerId(String name);

    /**
     * Insert a peer and return their primary key (must not already be in database)
     *
     * @param peer
     * @return
     */
    @Insert
    public abstract long insert(Peer peer);

    /**
     * Update the metadata for a peer (GPS coordinates, last seen)
     *
     * @param peer
     */
    @Update
    protected abstract void update(Peer peer);

    @Transaction
    /**
     * Add a peer record if it does not already exist;
     * update information if it is already defined.
     * This operation must be transactional, to avoid race condition
     * between search and insert
     */
    public long upsert(Peer peer) {
        long id = getPeerId(peer.name);
        if (id == 0) {
            id = insert(peer);
        } else {
            peer.id = id;
            update(peer);
        }
        return id;
    }
}
