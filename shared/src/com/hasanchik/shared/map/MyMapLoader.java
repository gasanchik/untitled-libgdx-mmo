package com.hasanchik.shared.map;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class MyMapLoader extends AsynchronousAssetLoader<MyMap, MyMapLoader.MyMapParameters> {
    static public class MyMapParameters extends AssetLoaderParameters<MyMap> {
    }

    //TODO: implement this class
    public MyMapLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, MyMapParameters parameter) {
        return null;
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, MyMapParameters parameter) {

    }

    @Override
    public MyMap loadSync(AssetManager manager, String fileName, FileHandle file, MyMapParameters parameter) {
        return null;
    }
}
