package necesse.gfx.gameSound;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.GameLoadingScreen;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.util.GameUtils;
import necesse.gfx.res.ResourceEncoder;
import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisAlloc;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class GameSound {
   private static HashMap<String, GameSound> loadedSounds = new HashMap();
   private static final int LOADING_THREADS = 10;
   private static ThreadPoolExecutor loadingExecutor;
   private Future<VorbisSamples> getSamplesLoading;
   private boolean isMusic;
   private boolean foundInFile;
   private float volumeModifier = 1.0F;
   public final String path;
   public final long cooldown;
   private ByteBuffer inputBytes;
   private VorbisInfo info;
   private VorbisSamples samples;

   private static ThreadFactory defaultThreadFactory() {
      AtomicInteger var0 = new AtomicInteger(0);
      return (var1) -> {
         return new Thread((ThreadGroup)null, var1, "sound-loader-" + var0.incrementAndGet());
      };
   }

   public static void startLoaderThreads() {
      if (loadingExecutor == null) {
         loadingExecutor = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque(), defaultThreadFactory());
      }
   }

   public static void endLoaderThreads() {
      Iterator var0 = loadedSounds.values().iterator();

      while(var0.hasNext()) {
         GameSound var1 = (GameSound)var0.next();
         var1.waitForDoneLoading();
      }

      loadingExecutor.shutdown();
      loadingExecutor = null;
   }

   public static GameSound fromFileRaw(String var0, int var1, boolean var2) throws IOException {
      GameSound var3 = (GameSound)loadedSounds.get(var0);
      if (var3 == null) {
         var3 = new GameSound(var0, var1, var2);
         loadedSounds.put(var0, var3);
      }

      return var3;
   }

   public static GameSound fromFile(String var0, int var1, boolean var2) {
      Objects.requireNonNull(var0);

      try {
         return fromFileRaw(var0, var1, var2);
      } catch (IOException var4) {
         System.err.println("Could not find sound file " + var0);
         var4.printStackTrace();
         return null;
      }
   }

   public static GameSound fromFile(String var0, int var1) {
      return fromFile(var0, var1, false);
   }

   public static GameSound fromFile(String var0) {
      return fromFile(var0, 0);
   }

   public static GameSound fromFileMusic(String var0) {
      return fromFile(var0, 0, true);
   }

   public static void deleteSounds() {
      if (loadingExecutor != null) {
         loadingExecutor.shutdownNow();
      }

      Iterator var0 = loadedSounds.values().iterator();

      while(var0.hasNext()) {
         GameSound var1 = (GameSound)var0.next();
         var1.delete();
      }

      loadedSounds.clear();
   }

   private GameSound(String var1, int var2, boolean var3) throws IOException {
      var1 = GameUtils.formatFileExtension(var1, "ogg");
      this.path = var1;
      this.cooldown = (long)var2;
      this.isMusic = var3;
      GameLoadingScreen.drawLoadingSub("sound/" + var1);
      File var5 = new File(GlobalData.rootPath() + "res/sound/" + var1);
      byte[] var4;
      if (var5.exists()) {
         var4 = GameUtils.loadByteFile(var5);
         this.foundInFile = true;
      } else {
         try {
            var4 = ResourceEncoder.getResourceBytes("sound/" + var1);
            this.foundInFile = true;
         } catch (FileNotFoundException var13) {
            var4 = GameUtils.loadByteFile(var5);
         }
      }

      if (var3) {
         this.inputBytes = MemoryUtil.memAlloc(var4.length);
         this.inputBytes.put(var4);
         this.inputBytes.position(0);
         MemoryStack var6 = MemoryStack.stackPush();

         try {
            IntBuffer var7 = var6.mallocInt(1);
            STBVorbisInfo var8 = STBVorbisInfo.malloc(var6);
            long var9 = STBVorbis.stb_vorbis_open_memory(this.inputBytes, var7, (STBVorbisAlloc)null);
            if (var9 != 0L) {
               int var11 = STBVorbis.stb_vorbis_stream_length_in_samples(var9);
               STBVorbis.stb_vorbis_get_info(var9, var8);
               this.info = new VorbisInfo(var11, var8.channels(), var8.sample_rate());
               STBVorbis.stb_vorbis_close(var9);
            } else {
               System.err.println("Error creating decoder for " + var1 + ": " + var7.get(0));
            }
         } catch (Throwable var14) {
            if (var6 != null) {
               try {
                  var6.close();
               } catch (Throwable var12) {
                  var14.addSuppressed(var12);
               }
            }

            throw var14;
         }

         if (var6 != null) {
            var6.close();
         }
      } else {
         Callable var15 = () -> {
            return decodeVorbisSamples(this, var4);
         };
         if (loadingExecutor != null) {
            this.getSamplesLoading = loadingExecutor.submit(var15);
         } else {
            FutureTask var16 = new FutureTask(var15);
            this.getSamplesLoading = var16;
            var16.run();
            this.waitForDoneLoading();
         }
      }

   }

   private static VorbisSamples decodeVorbisSamples(GameSound var0, byte[] var1) {
      long var2 = 0L;
      STBVorbisInfo var4 = null;
      IntBuffer var5 = null;
      ByteBuffer var6 = null;

      VorbisSamples var12;
      try {
         var5 = MemoryUtil.memAllocInt(1);
         var4 = STBVorbisInfo.malloc();
         var6 = MemoryUtil.memAlloc(var1.length);
         var6.put(var1);
         var6.position(0);
         var2 = STBVorbis.stb_vorbis_open_memory(var6, var5, (STBVorbisAlloc)null);
         if (var2 == 0L) {
            System.err.println("Error creating decoder for " + var0.path + ": " + var5.get(0));
            VorbisSamples var16 = new VorbisSamples(MemoryUtil.memAllocShort(0), new VorbisInfo(0, 1, 0));
            return var16;
         }

         STBVorbis.stb_vorbis_get_info(var2, var4);
         int var7 = var4.channels();
         int var8 = var4.sample_rate();
         int var9 = STBVorbis.stb_vorbis_stream_length_in_samples(var2);
         ShortBuffer var10 = MemoryUtil.memAllocShort(var9 * var7);
         int var11 = STBVorbis.stb_vorbis_get_samples_short_interleaved(var2, var7, var10);
         var10.position(0);
         var12 = new VorbisSamples(var10, new VorbisInfo(var11, var7, var8));
      } finally {
         MemoryUtil.memFree(var5);
         if (var4 != null) {
            var4.free();
         }

         MemoryUtil.memFree(var6);
         if (var2 != 0L) {
            STBVorbis.stb_vorbis_close(var2);
         }

      }

      return var12;
   }

   public void waitForDoneLoading() {
      if (this.getSamplesLoading != null) {
         GameLoadingScreen.drawLoadingSub("sound/" + this.path);

         try {
            this.samples = (VorbisSamples)this.getSamplesLoading.get();
         } catch (InterruptedException var2) {
            this.samples = new VorbisSamples(MemoryUtil.memAllocShort(0), new VorbisInfo(0, 1, 0));
         } catch (ExecutionException var3) {
            throw new RuntimeException(var3);
         }

         if (this.samples.info.channels > 1 && !this.isMusic) {
            GameLog.warn.println(this.path + " audio file is not mono, so positional audio will not work.");
         }

         this.info = this.samples.info;
         if (!this.foundInFile && !GlobalData.isDevMode()) {
            GameLog.warn.println("sound/" + this.path + " was not found in resource file.");
         }
      }

      this.getSamplesLoading = null;
   }

   public void delete() {
      if (this.samples != null) {
         MemoryUtil.memFree(this.samples.samples);
      }

      if (this.inputBytes != null) {
         MemoryUtil.memFree(this.inputBytes);
      }

   }

   public GameSound setVolumeModifier(float var1) {
      this.volumeModifier = var1;
      return this;
   }

   public float getVolumeModifier() {
      return this.volumeModifier;
   }

   public long getLengthInMillis() {
      return this.info != null ? this.info.lengthMillis : -1L;
   }

   public float getLengthInSeconds() {
      return this.info != null ? this.info.lengthSeconds : -1.0F;
   }

   public boolean isMusic() {
      return this.isMusic;
   }

   public GameSoundStreamer getStreamer() {
      return (GameSoundStreamer)(this.samples != null ? new SampleGameSoundStreamer(this.samples) : new ResourceGameSoundStreamer(this, this.inputBytes));
   }

   public static float getBufferSeconds(int var0) {
      int var1 = AL10.alGetBufferi(var0, 8193);
      int var2 = AL10.alGetBufferi(var0, 8195);
      int var3 = AL10.alGetBufferi(var0, 8196);
      int var4 = AL10.alGetBufferi(var0, 8194);
      int var5 = var3 / (var4 / 8) / var2;
      return (float)var5 / (float)var1;
   }

   private static String queryALError() {
      int var0 = AL10.alGetError();
      return var0 == 0 ? null : AL10.alGetString(var0);
   }

   private static void queryAndPrintALError(String var0) {
      String var1 = queryALError();
      if (var1 != null) {
         System.err.println(var0 + "AL error: " + var1);
      }

   }

   public static class VorbisInfo {
      public final int totalSamples;
      public final int channels;
      public final int sampleRate;
      public final long lengthMillis;
      public final float lengthSeconds;

      public VorbisInfo(int var1, int var2, int var3) {
         this.totalSamples = var1;
         this.channels = var2;
         this.sampleRate = var3;
         if (var3 != 0) {
            this.lengthMillis = (long)((double)var1 / (double)var3 * 1000.0);
            this.lengthSeconds = (float)((double)var1 / (double)var3);
         } else {
            this.lengthMillis = 0L;
            this.lengthSeconds = 0.0F;
         }

      }
   }

   public static class VorbisSamples {
      public final ShortBuffer samples;
      public final VorbisInfo info;

      public VorbisSamples(ShortBuffer var1, VorbisInfo var2) {
         this.samples = var1;
         this.info = var2;
      }
   }
}
