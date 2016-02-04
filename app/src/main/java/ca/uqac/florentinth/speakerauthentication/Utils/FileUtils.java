package ca.uqac.florentinth.speakerauthentication.Utils;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import ca.uqac.florentinth.speakerauthentication.Config.Folders;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

/**
 * Created by FlorentinTh on 11/12/2015.
 */
public abstract class FileUtils {

    public static void initFolders() {
        String path = Environment.getExternalStorageDirectory().getPath();
        File[] folders = new File[] {
                new File(path, Folders.getInstance().getBaseLogs()),
                new File(path, Folders.getInstance().getAudioChunks()),
                new File(path, Folders.getInstance().getRawAudioFiles()),
                new File(path, Folders.getInstance().getTrainingGeneratedDataset()),
                new File(path, Folders.getInstance().getTestingGeneratedDataset()),
                new File(path, Folders.getInstance().getTrainedModel()),
                new File(path, Folders.getInstance().getTmpFolder()),
                new File(path, Folders.getInstance().getTrashTesting()),
                new File(path, Folders.getInstance().getTrashAudioRaw()),
                new File(path, Folders.getInstance().getTrashAudioChunk())};

        for(int i = 0; i < folders.length; i++) {
            if(!folders[i].exists()) {
                folders[i].mkdirs();
            }
        }
    }

    public static String getSampleTMPFile(String sampleName) {
        String path = Environment.getExternalStorageDirectory().getPath();
        File tmp = new File(path, Folders.getInstance().getTmpFolder());
        return tmp.getAbsolutePath() + "/" + sampleName + ".raw";
    }

    public static String getSampleRawFile(String sampleName) {
        String path = Environment.getExternalStorageDirectory().getPath();
        File file = new File(path, Folders.getInstance().getRawAudioFiles());
        return file.getAbsolutePath() + "/" + sampleName + ".wav";
    }

    public static void deleteTMPFile(String sampleName) {
        File file = new File(getSampleTMPFile(sampleName));
        file.delete();
    }


    public static String removeChunkNumber(String val) {
        return val.substring(0, val.lastIndexOf("-"));
    }

    public static String removeFileExtension(String val) {
        return val.split("\\.")[0];
    }

    public static void CSVToARFF(File input, File output) throws IOException {
        CSVLoader csvDataset = new CSVLoader();
        csvDataset.setSource(input);
        Instances arffDataset = csvDataset.getDataSet();
        ArffSaver saver = new ArffSaver();
        saver.setInstances(arffDataset);
        saver.setFile(output);
        saver.writeBatch();
    }

    public static void moveTrash(File source, File destination) throws IOException {
        File[] files = source.listFiles();

        FileInputStream inputStream;
        FileOutputStream outputStream;

        for(int i = 0; i < files.length; i++) {
            inputStream = new FileInputStream(files[i]);
            outputStream = new FileOutputStream(new File(destination + "/" + files[i].getName()));

            byte[] buffer = new byte[1024];

            int length;
            while((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

            files[i].delete();
        }
    }
}
