package com.hsuk.video.data.entity;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

public class EntityListener {
    private static String APP_IP;

    static {
        try {
            APP_IP = Inet4Address.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            // Ignore exception
            APP_IP = "hsuk-corps";
        }
    }

    public static String getAppIp() {
        return APP_IP;
    }

    @PrePersist
    protected void prePersist(AbstractEntity abstractEntity) {
        abstractEntity.createdAt = LocalDateTime.now();
        abstractEntity.createdBy = APP_IP;
    }

    @PreUpdate
    protected void onUpdate(AbstractEntity abstractEntity) {
        abstractEntity.modifiedAt = LocalDateTime.now();
        abstractEntity.modifiedBy = APP_IP;
    }
}
