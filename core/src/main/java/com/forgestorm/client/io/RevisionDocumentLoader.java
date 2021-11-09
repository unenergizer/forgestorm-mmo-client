package com.forgestorm.client.io;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import java.io.FileNotFoundException;
import java.util.Scanner;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.forgestorm.client.util.Log.println;

public class RevisionDocumentLoader extends AsynchronousAssetLoader<RevisionDocumentLoader.RevisionDocumentWrapper, RevisionDocumentLoader.RevisionDocumentParameter> {

    static class RevisionDocumentParameter extends AssetLoaderParameters<RevisionDocumentWrapper> {
    }

    private RevisionDocumentWrapper revisionDocumentWrapper = null;

    RevisionDocumentLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, RevisionDocumentParameter parameter) {
        revisionDocumentWrapper = null;

        int revisionNumber = -2;
        try {
            Scanner myReader = new Scanner(file.file());
            revisionNumber = myReader.nextInt();
            println(getClass(), "RevisionNumber: " + revisionNumber);
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (revisionNumber == -2) throw new RuntimeException("RevisionNumber is not properly loading...");
        revisionDocumentWrapper = new RevisionDocumentWrapper(revisionNumber);
    }

    @Override
    public RevisionDocumentWrapper loadSync(AssetManager manager, String fileName, FileHandle file, RevisionDocumentParameter parameter) {
        return revisionDocumentWrapper;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, RevisionDocumentParameter parameter) {
        return null;
    }

    @Getter
    @AllArgsConstructor
    public static class RevisionDocumentWrapper {
        private final int revisionNumber;
    }
}
