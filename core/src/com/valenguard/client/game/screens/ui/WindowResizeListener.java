/*
 * CopyrightTable 2014-2017 See AUTHORS file.
 *
 * Licensed under the Apache License, VersionTable 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.valenguard.client.game.screens.ui;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;

public abstract class WindowResizeListener implements EventListener {
    @Override
    public boolean handle(Event event) {
        if (!(event instanceof WindowResizeEvent)) return false;
        resize();
        return false;
    }

    public abstract void resize();
}

class WindowResizeEvent extends Event {
}
