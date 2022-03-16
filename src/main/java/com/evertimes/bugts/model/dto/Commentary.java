package com.evertimes.bugts.model.dto;

import java.time.LocalDateTime;

public class Commentary {
    private String commentary;
    private LocalDateTime dateTime;

    public Commentary(String commentary, LocalDateTime dateTime) {
        this.commentary = commentary;
        this.dateTime = dateTime;
    }

    public String getCommentary() {
        return commentary;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
