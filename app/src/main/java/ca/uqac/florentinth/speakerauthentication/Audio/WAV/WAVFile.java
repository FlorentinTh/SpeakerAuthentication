package ca.uqac.florentinth.speakerauthentication.Audio.WAV;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by FlorentinTh on 11/12/2015.
 */
public class WAVFile {

    private final static int BUFFER_SIZE = 4096, FMT_CHUNK_ID = 0x20746D66, DATA_CHUNK_ID =
            0x61746164, RIFF_CHUNK_ID = 0x46464952, RIFF_TYPE_ID = 0x45564157;

    private File file;
    private IOState ioState;
    private FileOutputStream outputStream;
    private FileInputStream inputStream;

    private int bytesPerSample, channelNumber, blockAlign, validBits, bufferPointer, bytesRead;
    private double floatScale, floatOffset;
    private boolean wordAlignAdjust;
    private long frameNumber, sampleRate, frameCounter;
    private byte[] buffer;

    private WAVFile() {
        buffer = new byte[BUFFER_SIZE];
    }

    public static WAVFile createWAVFile(File file, int channelNumber, long frameNumber, int
            validBits, long sampleRate) throws IOException, WAVFileException {

        WAVFile WAVFile = new WAVFile();
        WAVFile.file = file;
        WAVFile.channelNumber = channelNumber;
        WAVFile.frameNumber = frameNumber;
        WAVFile.sampleRate = sampleRate;
        WAVFile.bytesPerSample = (validBits + 7) / 8;
        WAVFile.blockAlign = WAVFile.bytesPerSample * channelNumber;
        WAVFile.validBits = validBits;

        if(channelNumber < 1 || channelNumber > 65535) {
            throw new WAVFileException("Illegal number of channels, " + "valid range 1 to 65536");
        }

        if(frameNumber < 0) {
            throw new WAVFileException("Number of frames must be positive");
        }

        if(validBits < 2 || validBits > 65535) {
            throw new WAVFileException("Illegal number of valid bits, valid range 2 to 65536");
        }

        if(sampleRate < 0) {
            throw new WAVFileException("Sample rate must be positive");
        }

        WAVFile.outputStream = new FileOutputStream(file);

        long dataChunkSize = WAVFile.blockAlign * frameNumber;
        long mainChunkSize = 4 +     // Riff Type
                8 +        // Format ID and size
                16 +    // Format data
                8 +    // Data ID and size
                dataChunkSize;

        if(dataChunkSize % 2 == 1) {
            mainChunkSize += 1;
            WAVFile.wordAlignAdjust = true;
        } else {
            WAVFile.wordAlignAdjust = false;
        }

        putLittleEndian(RIFF_CHUNK_ID, WAVFile.buffer, 0, 4);
        putLittleEndian(mainChunkSize, WAVFile.buffer, 4, 4);
        putLittleEndian(RIFF_TYPE_ID, WAVFile.buffer, 8, 4);

        WAVFile.outputStream.write(WAVFile.buffer, 0, 12);

        long averageBytesPerSecond = sampleRate * WAVFile.blockAlign;

        putLittleEndian(FMT_CHUNK_ID, WAVFile.buffer, 0, 4);
        putLittleEndian(16, WAVFile.buffer, 4, 4);
        putLittleEndian(1, WAVFile.buffer, 8, 2);
        putLittleEndian(channelNumber, WAVFile.buffer, 10, 2);
        putLittleEndian(sampleRate, WAVFile.buffer, 12, 4);
        putLittleEndian(averageBytesPerSecond, WAVFile.buffer, 16, 4);
        putLittleEndian(WAVFile.blockAlign, WAVFile.buffer, 20, 2);
        putLittleEndian(validBits, WAVFile.buffer, 22, 2);

        WAVFile.outputStream.write(WAVFile.buffer, 0, 24);

        putLittleEndian(DATA_CHUNK_ID, WAVFile.buffer, 0, 4);
        putLittleEndian(dataChunkSize, WAVFile.buffer, 4, 4);

        WAVFile.outputStream.write(WAVFile.buffer, 0, 8);

        if(WAVFile.validBits > 8) {
            WAVFile.floatOffset = 0;
            WAVFile.floatScale = Long.MAX_VALUE >> (64 - WAVFile.validBits);
        } else {
            WAVFile.floatOffset = 1;
            WAVFile.floatScale = 0.5 * ((1 << WAVFile.validBits) - 1);
        }

        WAVFile.bufferPointer = 0;
        WAVFile.bytesRead = 0;
        WAVFile.frameCounter = 0;
        WAVFile.ioState = IOState.WRITING;

        return WAVFile;
    }

    public static WAVFile openWAVFile(File file) throws IOException, WAVFileException {

        WAVFile WAVFile = new WAVFile();
        WAVFile.file = file;

        WAVFile.inputStream = new FileInputStream(file);

        int bytesRead = WAVFile.inputStream.read(WAVFile.buffer, 0, 12);

        if(bytesRead != 12) {
            throw new WAVFileException("Not enough wav file bytes for header");
        }

        long riffChunkID = getLittleEndian(WAVFile.buffer, 0, 4);
        long chunkSize = getLittleEndian(WAVFile.buffer, 4, 4);
        long riffTypeID = getLittleEndian(WAVFile.buffer, 8, 4);

        if(riffChunkID != RIFF_CHUNK_ID) {
            throw new WAVFileException("Invalid Wav Header data, incorrect riff chunk ID");
        }

        if(riffTypeID != RIFF_TYPE_ID) {
            throw new WAVFileException("Invalid Wav Header data, incorrect riff type ID");
        }

        if(file.length() != chunkSize + 8) {
            throw new WAVFileException("Header chunk size (" + chunkSize + ") does not match file" +
                    " size (" + file.length() + ")");
        }

        boolean foundFormat = false;
        boolean foundData = false;

        while(true) {
            bytesRead = WAVFile.inputStream.read(WAVFile.buffer, 0, 8);

            if(bytesRead == -1) {
                throw new WAVFileException("Reached end of file without finding format chunk");
            }

            if(bytesRead != 8) {
                throw new WAVFileException("Could not read chunk header");
            }

            long chunkID = getLittleEndian(WAVFile.buffer, 0, 4);
            chunkSize = getLittleEndian(WAVFile.buffer, 4, 4);

            long numChunkBytes = ((chunkSize % 2) == 1) ? chunkSize + 1 : chunkSize;

            if(chunkID == FMT_CHUNK_ID) {
                foundFormat = true;

                bytesRead = WAVFile.inputStream.read(WAVFile.buffer, 0, 16);

                int compressionCode = (int) getLittleEndian(WAVFile.buffer, 0, 2);

                if(compressionCode != 1) {
                    throw new WAVFileException("Compression Code " + compressionCode + " not " +
                            "supported");
                }

                WAVFile.channelNumber = (int) getLittleEndian(WAVFile.buffer, 2, 2);
                WAVFile.sampleRate = getLittleEndian(WAVFile.buffer, 4, 4);
                WAVFile.blockAlign = (int) getLittleEndian(WAVFile.buffer, 12, 2);
                WAVFile.validBits = (int) getLittleEndian(WAVFile.buffer, 14, 2);

                if(WAVFile.channelNumber == 0) {
                    throw new WAVFileException("Number of channels specified in header is " +
                            "equal to zero");
                }

                if(WAVFile.blockAlign == 0) {
                    throw new WAVFileException("Block Align specified in header is equal to zero");
                }

                if(WAVFile.validBits < 2) {
                    throw new WAVFileException("Valid Bits specified in header is less than 2");
                }

                if(WAVFile.validBits > 64) {
                    throw new WAVFileException("Valid Bits specified in header is greater than "
                            + "64, this is greater than a long can hold");
                }

                WAVFile.bytesPerSample = (WAVFile.validBits + 7) / 8;

                if(WAVFile.bytesPerSample * WAVFile.channelNumber != WAVFile.blockAlign) {
                    throw new WAVFileException("Block Align does not agree with bytes required "
                            + "for validBits and number of channels");
                }

                numChunkBytes -= 16;

                if(numChunkBytes > 0) {
                    WAVFile.inputStream.skip(numChunkBytes);
                }
            } else if(chunkID == DATA_CHUNK_ID) {

                if(foundFormat == false) {
                    throw new WAVFileException("Data chunk found before Format chunk");
                }

                if(chunkSize % WAVFile.blockAlign != 0) {
                    throw new WAVFileException("Data Chunk size is not multiple of Block Align");
                }

                WAVFile.frameNumber = chunkSize / WAVFile.blockAlign;
                foundData = true;

                break;
            } else {
                WAVFile.inputStream.skip(numChunkBytes);
            }
        }

        if(foundData == false) {
            throw new WAVFileException("Did not find a data chunk");
        }

        if(WAVFile.validBits > 8) {
            WAVFile.floatOffset = 0;
            WAVFile.floatScale = 1 << (WAVFile.validBits - 1);
        } else {
            WAVFile.floatOffset = -1;
            WAVFile.floatScale = 0.5 * ((1 << WAVFile.validBits) - 1);
        }

        WAVFile.bufferPointer = 0;
        WAVFile.bytesRead = 0;
        WAVFile.frameCounter = 0;
        WAVFile.ioState = IOState.READING;

        return WAVFile;
    }

    private static long getLittleEndian(byte[] buffer, int position, int byteNumber) {
        byteNumber--;
        position += byteNumber;

        long val = buffer[position] & 0xFF;

        for(int i = 0; i < byteNumber; i++) {
            val = (val << 8) + (buffer[--position] & 0xFF);
        }

        return val;
    }

    private static void putLittleEndian(long value, byte[] buffer, int position, int byteNumber) {
        for(int i = 0; i < byteNumber; i++) {
            buffer[position] = (byte) (value & 0xFF);
            value >>= 8;
            position++;
        }
    }

    public int getChannelNumber() {
        return channelNumber;
    }

    public long getFrameNumber() {
        return frameNumber;
    }

    public long getSampleRate() {
        return sampleRate;
    }

    public int getValidBits() {
        return validBits;
    }

    private void writeSample(long value) throws IOException {
        for(int i = 0; i < bytesPerSample; i++) {
            if(bufferPointer == BUFFER_SIZE) {
                outputStream.write(buffer, 0, BUFFER_SIZE);
                bufferPointer = 0;
            }

            buffer[bufferPointer] = (byte) (value & 0xFF);
            value >>= 8;
            bufferPointer++;
        }
    }

    private long readSample() throws IOException, WAVFileException {
        long value = 0;

        for(int i = 0; i < bytesPerSample; i++) {
            if(bufferPointer == bytesRead) {
                int read = inputStream.read(buffer, 0, BUFFER_SIZE);

                if(read == -1) {
                    throw new WAVFileException("Not enough data available");
                }

                bytesRead = read;
                bufferPointer = 0;
            }

            int tmpValue = buffer[bufferPointer];

            if(i < bytesPerSample - 1 || bytesPerSample == 1) {
                tmpValue &= 0xFF;
            }

            value += tmpValue << (i * 8);

            bufferPointer++;
        }

        return value;
    }

    public int readFrames(double[] sampleBuffer, int frameNumberToRead) throws IOException,
            WAVFileException {
        return readFrames(sampleBuffer, 0, frameNumberToRead);
    }

    public int readFrames(double[] sampleBuffer, int offset, int frameNumberToRead) throws
            IOException, WAVFileException {
        if(ioState != IOState.READING) {
            throw new IOException("Cannot read from WAVFile instance");
        }

        for(int i = 0; i < frameNumberToRead; i++) {
            if(frameCounter == frameNumber) {
                return i;
            }

            for(int j = 0; j < channelNumber; j++) {
                sampleBuffer[offset] = floatOffset + (double) readSample() / floatScale;
                offset++;
            }

            frameCounter++;
        }

        return frameNumberToRead;
    }

    public int writeFrames(double[] sampleBuffer, int frameNumberToWrite) throws IOException,
            WAVFileException {
        return writeFrames(sampleBuffer, 0, frameNumberToWrite);
    }

    public int writeFrames(double[] sampleBuffer, int offset, int frameNumberToWrite) throws
            IOException, WAVFileException {
        if(ioState != IOState.WRITING) {
            throw new IOException("Cannot write to WAVFile instance");
        }

        for(int i = 0; i < frameNumberToWrite; i++) {
            if(frameCounter == frameNumber) {
                return i;
            }

            for(int j = 0; j < channelNumber; j++) {
                writeSample((long) (floatScale * (floatOffset + sampleBuffer[offset])));
                offset++;
            }

            frameCounter++;
        }

        return frameNumberToWrite;
    }

    public void close() throws IOException {
        if(inputStream != null) {
            inputStream.close();
            inputStream = null;
        }

        if(outputStream != null) {
            if(bufferPointer > 0) {
                outputStream.write(buffer, 0, bufferPointer);
            }

            if(wordAlignAdjust) {
                outputStream.write(0);
            }

            outputStream.close();
            outputStream = null;
        }

        ioState = IOState.CLOSED;
    }

    private enum IOState {
        READING,
        WRITING,
        CLOSED
    }
}
