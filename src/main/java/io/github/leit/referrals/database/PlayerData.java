package io.github.leit.referrals.database;

import java.util.UUID;

public class PlayerData {
    private UUID playerUUID;
    private int isReferred;
    private UUID referredBy;
    private int playersReferred;

    public PlayerData(UUID playerUUID, int isReferred, UUID referredBy, int playersReferred) {
        this.playerUUID = playerUUID;
        this.isReferred = isReferred;
        this.referredBy = referredBy;
        this.playersReferred = playersReferred;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public int getIsReferred() {
        return isReferred;
    }

    public int getPlayersReferred() {
        return playersReferred;
    }

    public UUID getReferredBy() {
        return referredBy;
    }

    public void setIsReferred(int isReferred) {
        this.isReferred = isReferred;
    }

    public void setPlayersReferred(int playersReferred) {
        this.playersReferred = playersReferred;
    }

    public void setReferredBy(UUID referredBy) {
        this.referredBy = referredBy;
    }
}
