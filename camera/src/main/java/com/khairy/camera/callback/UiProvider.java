package com.khairy.camera.callback;

import android.view.TextureView;
import android.view.View;

public interface UiProvider {
    TextureView getTextureView();
    View getShutterView();
}
