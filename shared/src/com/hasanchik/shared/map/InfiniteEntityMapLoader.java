package com.hasanchik.shared.map;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class InfiniteEntityMapLoader extends AsynchronousAssetLoader<InfiniteEntityMap, InfiniteEntityMapLoader.InfiniteEntityMapParameters> {
    static public class InfiniteEntityMapParameters extends AssetLoaderParameters<InfiniteEntityMap> {
    }

    //TODO: implement this class
    public InfiniteEntityMapLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, InfiniteEntityMapParameters parameter) {
        return null;
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, InfiniteEntityMapParameters parameter) {

    }

    @Override
    public InfiniteEntityMap loadSync(AssetManager manager, String fileName, FileHandle file, InfiniteEntityMapParameters parameter) {
        return null;
    }
}
