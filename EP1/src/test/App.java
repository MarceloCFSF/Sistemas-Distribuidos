package test;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App {
  public static Set<String> listFiles() {
    return Stream.of(new File("/home/marcelocfsf/Downloads").listFiles())
      .filter(file -> !file.isDirectory())
      .map(File::getName)
      .collect(Collectors.toSet());
  }

  public static Set<String> listFilesUsingDirectoryStream() throws IOException {
    Set<String> fileSet = new HashSet<>();
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("/home/marcelocfsf/Downloads"))) {
        for (Path path : stream) {
            if (!Files.isDirectory(path)) {
                fileSet.add(path.getFileName()
                    .toString());
            }
        }
    }
    return fileSet;
  }

  public static void main(String[] args) {

    try {
      System.out.println(listFiles().toString());
    } catch (Exception e) {
      System.err.println(e);
    }
  }
}
