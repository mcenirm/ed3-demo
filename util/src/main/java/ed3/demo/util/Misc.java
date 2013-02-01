package ed3.demo.util;

import java.io.File;
import java.io.IOException;
import java.util.TimeZone;

public class Misc {

  public static File ensureDirectoryExists(File parentDir, String dirName, String dirDescription) throws IOException {
    File dir = new File(parentDir, dirName);
    if (!dir.isDirectory() && !dir.mkdir()) {
      throw new IOException(String.format("could not create %3$s directory \"%2$s\" under parent \"%1$s\"", parentDir, dirName, dirDescription));
    }
    return dir;
  }

  public static File ensureDirectoryExists(String dirName, String dirDescription) throws IOException {
    return ensureDirectoryExists(new File("."), dirName, dirDescription);
  }

  public static void switchToGMT() {
    TimeZone.setDefault(TimeZone.getTimeZone(""));
  }
}
