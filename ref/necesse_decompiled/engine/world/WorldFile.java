package necesse.engine.world;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NotDirectoryException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Spliterator;
import java.util.function.Consumer;
import necesse.engine.util.GameUtils;

public class WorldFile {
   protected final Path path;

   protected WorldFile(Path var1) {
      this.path = var1;
   }

   public boolean exists() {
      return Files.exists(this.path, new LinkOption[0]);
   }

   public boolean isDirectory() {
      return Files.isDirectory(this.path, new LinkOption[0]);
   }

   public Iterator<String> iterateFilesInDirectory() throws IOException {
      if (this.exists() && !this.isDirectory()) {
         throw new NotDirectoryException(this.path.toString());
      } else {
         return !this.exists() ? Collections.emptyIterator() : GameUtils.mapIterator(Files.newDirectoryStream(this.path).iterator(), (var0) -> {
            return var0.getFileName().toString();
         });
      }
   }

   public Iterable<String> getFilesInDirectory() throws IOException {
      Iterator var1 = this.iterateFilesInDirectory();
      return () -> {
         return var1;
      };
   }

   public Iterator<WorldFile> iteratePathsInDirectory() throws IOException {
      if (this.exists() && !this.isDirectory()) {
         throw new NotDirectoryException(this.path.toString());
      } else {
         return !this.exists() ? Collections.emptyIterator() : GameUtils.mapIterator(Files.newDirectoryStream(this.path).iterator(), WorldFile::new);
      }
   }

   public Iterable<WorldFile> getPathsInDirectory() throws IOException {
      Iterator var1 = this.iteratePathsInDirectory();
      return () -> {
         return var1;
      };
   }

   public void copyTo(WorldFile var1, CopyOption... var2) throws IOException {
      this.copyTo(var1.path, var2);
   }

   public void copyTo(Path var1, CopyOption... var2) throws IOException {
      if (this.isDirectory()) {
         Files.createDirectory(var1);
         Iterator var3 = this.getPathsInDirectory().iterator();

         while(var3.hasNext()) {
            WorldFile var4 = (WorldFile)var3.next();
            var4.copyTo(var1.resolve(var4.getFileName().toString()), var2);
         }
      } else {
         Files.copy(this.path, var1, var2);
      }

   }

   public void moveTo(WorldFile var1, CopyOption... var2) throws IOException {
      this.moveTo(var1.path, var2);
   }

   public void moveTo(Path var1, CopyOption... var2) throws IOException {
      LinkedList var3 = new LinkedList();
      this.moveTo(var1, var3, var2);

      while(!var3.isEmpty()) {
         Path var4 = (Path)var3.removeLast();
         Files.delete(var4);
      }

   }

   private void moveTo(Path var1, LinkedList<Path> var2, CopyOption... var3) throws IOException {
      if (this.isDirectory()) {
         Files.createDirectory(var1);
         var2.add(this.path);
         Iterator var4 = this.getPathsInDirectory().iterator();

         while(var4.hasNext()) {
            WorldFile var5 = (WorldFile)var4.next();
            var5.moveTo(var1.resolve(var5.getFileName().toString()), var2, var3);
         }
      } else {
         Files.move(this.path, var1, var3);
      }

   }

   public BufferedWriter writer(Charset var1, boolean var2) throws IOException {
      if (!var2 && this.exists()) {
         return null;
      } else {
         Path var3 = this.path.getParent();
         if (var3 != null) {
            Files.createDirectories(var3);
         }

         return Files.newBufferedWriter(this.path, var1);
      }
   }

   public BufferedWriter writer(boolean var1) throws IOException {
      return this.writer(StandardCharsets.UTF_8, var1);
   }

   public void write(byte[] var1, boolean var2) throws IOException {
      if (var2 || !Files.exists(this.path, new LinkOption[0])) {
         Path var3 = this.path.getParent();
         if (var3 != null) {
            Files.createDirectories(var3);
         }

         Files.write(this.path, var1, new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE});
      }
   }

   public void write(byte[] var1) throws IOException {
      this.write(var1, true);
   }

   public byte[] read() throws IOException {
      return Files.readAllBytes(this.path);
   }

   public BufferedReader reader(Charset var1) throws IOException {
      return Files.newBufferedReader(this.path, var1);
   }

   public BufferedReader reader() throws IOException {
      return this.reader(StandardCharsets.UTF_8);
   }

   public InputStream inputStream(OpenOption... var1) throws IOException {
      return Files.newInputStream(this.path, var1);
   }

   public OutputStream outputStream(OpenOption... var1) throws IOException {
      return Files.newOutputStream(this.path, var1);
   }

   public boolean delete() throws IOException {
      return Files.deleteIfExists(this.path);
   }

   public FileSystem getFileSystem() {
      return this.path.getFileSystem();
   }

   public boolean isAbsolute() {
      return this.path.isAbsolute();
   }

   public Path getRoot() {
      return this.path.getRoot();
   }

   public Path getFileName() {
      return this.path.getFileName();
   }

   public Path getParent() {
      return this.path.getParent();
   }

   public int getNameCount() {
      return this.path.getNameCount();
   }

   public Path getName(int var1) {
      return this.path.getName(var1);
   }

   public Path subpath(int var1, int var2) {
      return this.path.subpath(var1, var2);
   }

   public boolean startsWith(Path var1) {
      return this.path.startsWith(var1);
   }

   public boolean startsWith(String var1) {
      return this.path.startsWith(var1);
   }

   public boolean endsWith(Path var1) {
      return this.path.endsWith(var1);
   }

   public boolean endsWith(String var1) {
      return this.path.endsWith(var1);
   }

   public Path normalize() {
      return this.path.normalize();
   }

   public Path resolve(Path var1) {
      return this.path.resolve(var1);
   }

   public Path resolve(String var1) {
      return this.path.resolve(var1);
   }

   public Path resolveSibling(Path var1) {
      return this.path.resolveSibling(var1);
   }

   public Path resolveSibling(String var1) {
      return this.path.resolveSibling(var1);
   }

   public Path relativize(Path var1) {
      return this.path.relativize(var1);
   }

   public URI toUri() {
      return this.path.toUri();
   }

   public Path toAbsolutePath() {
      return this.path.toAbsolutePath();
   }

   public Path toRealPath(LinkOption... var1) throws IOException {
      return this.path.toRealPath(var1);
   }

   public File toFile() {
      return this.path.toFile();
   }

   public WatchKey register(WatchService var1, WatchEvent.Kind<?>[] var2, WatchEvent.Modifier... var3) throws IOException {
      return this.path.register(var1, var2, var3);
   }

   public WatchKey register(WatchService var1, WatchEvent.Kind<?>... var2) throws IOException {
      return this.path.register(var1, var2);
   }

   public Iterator<Path> iterator() {
      return this.path.iterator();
   }

   public int compareTo(Path var1) {
      return this.path.compareTo(var1);
   }

   public void forEach(Consumer<? super Path> var1) {
      this.path.forEach(var1);
   }

   public Spliterator<Path> spliterator() {
      return this.path.spliterator();
   }

   public String toString() {
      return this.path.toString();
   }
}
