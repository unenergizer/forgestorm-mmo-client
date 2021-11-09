package com.forgestorm.client.game.audio;

import lombok.Data;

@Data
public class AudioData {
    private int audioId;
    private String fileName;
    private String description;
    private AudioType audioType;
}
