package com.forgestorm.client.game.world.entities;

import com.forgestorm.client.ClientMain;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Monster extends AiEntity {
    public Monster(ClientMain clientMain) {
        super(clientMain);
    }
}
