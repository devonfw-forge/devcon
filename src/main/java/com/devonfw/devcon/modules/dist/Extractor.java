package com.devonfw.devcon.modules.dist;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import net.sf.sevenzipjbinding.ExtractAskMode;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IArchiveExtractCallback;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

import com.devonfw.devcon.output.OutputConsole;
import com.devonfw.devcon.output.SpinningCursor;

/**
 * This class includes the extracting functionality for uncompress .7z and .zip files
 *
 * @author pparrado
 */
public class Extractor {

  /**
   * Method that extracts a compressed file and stores the uncompressed files in a given path
   *
   * @param file the file to extract
   * @param extractPath the path to extract the files
   * @throws SevenZipException
   * @throws IOException
   * @throws InterruptedException
   */
  public static void extract(String file, String extractPath) throws SevenZipException, IOException,
      InterruptedException {

    OutputConsole out = new OutputConsole();
    Thread thread;
    SpinningCursor spin;

    out.status("extracting file...");
    // start showing spinningCursor
    spin = new SpinningCursor();
    thread = new Thread(spin);
    thread.start();

    IInArchive inArchive = null;
    RandomAccessFile randomAccessFile = null;
    try {
      randomAccessFile = new RandomAccessFile(new File(file), "r");
      inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile));
      inArchive.extract(null, false, new MyExtractCallback(inArchive, extractPath));
    } finally {
      if (inArchive != null) {
        inArchive.close();
      }
      if (randomAccessFile != null) {
        randomAccessFile.close();
      }

      // end spinningCursor
      if (thread != null) {
        spin.terminate();
        thread.join();
      }
      out.status("File successfuly extracted.");
    }
  }

  private static class MyExtractCallback implements IArchiveExtractCallback {
    private final IInArchive inArchive;

    private final String extractPath;

    OutputConsole out = new OutputConsole();

    // boolean firstNode = true;

    public MyExtractCallback(IInArchive inArchive, String extractPath) {

      this.inArchive = inArchive;
      this.extractPath = extractPath.endsWith(File.separator) ? extractPath : extractPath + File.separator;
    }

    // @Override
    public ISequentialOutStream getStream(final int index, ExtractAskMode extractAskMode) throws SevenZipException {

      return new ISequentialOutStream() {
        // @Override
        public int write(byte[] data) throws SevenZipException {

          String filePath = MyExtractCallback.this.inArchive.getStringProperty(index, PropID.PATH);
          FileOutputStream fos = null;

          try {
            File path = new File(MyExtractCallback.this.extractPath + filePath);

            // // ---------------
            // if (MyExtractCallback.this.firstNode && path.getParentFile().exists()) {
            // MyExtractCallback.this.firstNode = false;
            // throw new Exception("Directory " + path.getParentFile() + " already exists.");
            // }
            // MyExtractCallback.this.firstNode = false;
            // // ---------------

            if (!path.getParentFile().exists()) {
              path.getParentFile().mkdirs();
            }

            if (!path.exists()) {
              path.createNewFile();
            }
            fos = new FileOutputStream(path, true);
            fos.write(data);

          } catch (IOException e) {
            // logger.error("IOException while extracting "+filePath, e);
            MyExtractCallback.this.out.showError("IOException while extracting " + filePath);
          } catch (Exception e) {
            MyExtractCallback.this.out.showError(e.getMessage());
            System.exit(0);
          } finally {

            try {
              if (fos != null) {
                fos.flush();
                fos.close();
              }
            } catch (IOException e) {
              // logger.error("Could not close FileOutputStream", e);
              MyExtractCallback.this.out.showError("Could not close FileOutputStream");
            }
          }
          return data.length;
        }
      };
    }

    // @Override
    public void prepareOperation(ExtractAskMode extractAskMode) throws SevenZipException {

    }

    // @Override
    public void setOperationResult(ExtractOperationResult extractOperationResult) throws SevenZipException {

    }

    // @Override
    public void setCompleted(long completeValue) throws SevenZipException {

    }

    // @Override
    public void setTotal(long total) throws SevenZipException {

    }
  }
}
