package com.hasanchik.game.interfaces.callbacks;

import com.hasanchik.shared.interfaces.callback.Callback;

@FunctionalInterface
public interface ServerConnectedCallback extends Callback {
     void call(boolean success);
}
