package ca.uqac.florentinth.speakerauthentication.Dataset;

import android.os.Environment;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.uqac.florentinth.speakerauthentication.Config.Features;
import ca.uqac.florentinth.speakerauthentication.Config.Folders;
import ca.uqac.florentinth.speakerauthentication.Models.VoiceSample;

/**
 * Created by FlorentinTh on 11/12/2015.
 */
public class CSVWriter {

    //    private static final String TEXT_TO_READ_SOURCE = "data.json";
    private static final String LINE_SEPARATOR = "\n";

    private File location;
    private List<VoiceSample> voiceSamples;
    private FileWriter fileWriter;
    private CSVPrinter csvPrinter;

    public CSVWriter(String location, List<VoiceSample> voiceSamples) throws IOException {
        this.location = new File(Environment.getExternalStorageDirectory().getPath(), location);
        this.voiceSamples = voiceSamples;
    }

    private void openCSVFile(File location) throws IOException {
        fileWriter = new FileWriter(location, true);
        csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT.withRecordSeparator
                (LINE_SEPARATOR));
    }

    private void closeCSVFile() throws IOException {
        fileWriter.flush();
        fileWriter.close();
        csvPrinter.close();
    }

    public void writeCSVFileHeader() throws IOException {
//    public void writeCSVFileHeader(boolean training) throws IOException {
        File[] files = location.listFiles();

        if(files.length == 0) {
            openCSVFile(new File(location.getPath() + "/" + Folders.getInstance()
                    .getDatasetFileNameCSV()));
            int numberFeatures = Features.getInstance().getNumberFeatures();
            String[] headerValues = new String[numberFeatures + 2];

            for(int i = 0; i < numberFeatures; i++) {
                headerValues[i] = "a" + i;
            }

            headerValues[numberFeatures] = "class";
            csvPrinter.printRecord(headerValues);

//            if(training) {
//                String json = JSONUtils.parseJSONFile(App.getAppContext(), TEXT_TO_READ_SOURCE);
//                List<VoiceSample> voiceSamples = JSONUtils.JSONToVoiceSampleObject(json);
//
//                for (VoiceSample sample : voiceSamples) {
//                    List<String> record = new ArrayList();
//                    double[] voiceFeatures = sample.getVoiceFeatures();
//
//                    for (int i = 0; i < voiceFeatures.length; i++) {
//                        record.add(String.valueOf(voiceFeatures[i]));
//                    }
//
//                    record.add(sample.getClassName());
//                    csvPrinter.printRecord(record);
//                }
//            }

            closeCSVFile();
        }
    }

    public void writeCSVFileContent() throws IOException {
        //public void writeCSVFileContent(boolean training) throws IOException {
        File[] files = location.listFiles();

        if(files.length > 0) {
            if(files[0].isFile() && files[0].getName().equals(Folders.getInstance()
                    .getDatasetFileNameCSV())) {
                openCSVFile(files[0]);

                for(VoiceSample sample : voiceSamples) {
                    List<String> record = new ArrayList();
                    double[] voiceFeatures = sample.getVoiceFeatures();

                    for(int i = 0; i < voiceFeatures.length; i++) {
                        record.add(String.valueOf(voiceFeatures[i]));
                    }

                    record.add(sample.getClassName());
                    csvPrinter.printRecord(record);
                }

                closeCSVFile();
            } else {
                throw new IOException("File: " + Folders.getInstance().getDatasetFileNameCSV() +
                        " seems does not exist.");
            }
        } else {
            writeCSVFileHeader();
//            writeCSVFileHeader(training);
        }
    }
}
