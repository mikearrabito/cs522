package edu.stevens.cs522.chatserver.databases;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

import edu.stevens.cs522.chatserver.entities.Peer;

@Dao
public abstract class PeerDAO {

    /**
     * Get all peers in the database.
     *
     * @return All peers stored
     */
    @Query("select * from Peer")
    public abstract LiveData<List<Peer>> fetchAllPeers();

    /**
     * Get a single peer record (may be used in later assignments)
     *
     * @param peerId
     * @return Peer record with given id
     */
    @Query("select * from Peer where id = :peerId")
    public abstract ListenableFuture<Peer> fetchPeer(long peerId);

    /**
     * Get the database primary key for a peer, based on chat name.
     *
     * @param name of peer
     * @return id for peer
     */
    @Query("select id from Peer where name = :name")
    protected abstract long getPeerId(String name);

    /**
     * Insert a peer and return their primary key (must not already be in database)
     *
     * @param peer to insert
     * @return primary key of inserted peer
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract long insert(Peer peer);

    /**
     * Update the metadata for a peer (GPS coordinates, last seen)
     *
     * @param peer to update
     */
    @Update
    protected abstract void update(Peer peer);

    /**
     * @param peer to upsert
     * @return primary key of upserted peer
     */
    @Transaction
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
