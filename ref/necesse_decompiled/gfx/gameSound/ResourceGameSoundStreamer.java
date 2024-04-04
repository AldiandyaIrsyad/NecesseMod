package necesse.gfx.gameSound;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import necesse.engine.Screen;
import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisAlloc;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryUtil;

public class ResourceGameSoundStreamer extends GameSoundStreamer {
   private final AtomicBoolean isWorking;
   private final AtomicBoolean isDisposed;
   private final GameSound sound;
   private long decoder = 0L;
   private IntBuffer errorBuffer = null;
   private STBVorbisInfo info = null;
   private boolean initialized;
   private final ByteBuffer inputBytes;
   private final LinkedList<ShortBuffer> sampleBuffers = new LinkedList();
   private final LinkedList<Integer> buffers = new LinkedList();
   private int channels;
   private int sampleRate;
   private int sampleLength;
   private float lengthSeconds;

   public ResourceGameSoundStreamer(GameSound var1, ByteBuffer var2) {
      this.sound = var1;
      this.inputBytes = var2;
      this.isWorking = new AtomicBoolean(false);
      this.isDisposed = new AtomicBoolean(false);
      synchronized(this.inputBytes) {
         this.init();
      }
   }

   public int getLengthInSamples() {
      return this.sampleLength;
   }

   public int getSampleRate() {
      return this.sampleRate;
   }

   public float getLengthInSeconds() {
      return this.lengthSeconds;
   }

   public boolean isDone(int var1) {
      return this.initialized && this.sampleLength <= var1;
   }

   public boolean isWorking() {
      return this.isWorking.get();
   }

   private void init() {
      this.initialized = true;
      if (this.errorBuffer != null) {
         MemoryUtil.memFree(this.errorBuffer);
      }

      if (this.info != null) {
         this.info.free();
      }

      this.errorBuffer = MemoryUtil.memAllocInt(1);
      this.info = STBVorbisInfo.malloc();
      synchronized(this.inputBytes) {
         this.decoder = STBVorbis.stb_vorbis_open_memory(this.inputBytes, this.errorBuffer, (STBVorbisAlloc)null);
         if (this.decoder == 0L) {
            System.err.println("Error creating decoder for " + this.sound.path + ": " + this.errorBuffer.get(0));
         } else {
            STBVorbis.stb_vorbis_get_info(this.decoder, this.info);
            this.channels = this.info.channels();
            this.sampleRate = this.info.sample_rate();
            this.sampleLength = STBVorbis.stb_vorbis_stream_length_in_samples(this.decoder);
            this.lengthSeconds = (float)this.sampleLength / (float)this.sampleRate;
         }
      }
   }

   public void getNextBuffers(int var1, int var2, GameSoundStreamer.BufferHandler var3) {
      if (!this.isWorking()) {
         this.isWorking.set(true);
         Screen.musicStreamer.submit(() -> {
            ShortBuffer var4 = null;

            try {
               synchronized(this.inputBytes) {
                  if (!this.initialized) {
                     this.init();
                  }

                  var4 = MemoryUtil.memAllocShort(var2 * this.channels);
                  this.sampleBuffers.add(var4);
                  if (this.decoder == 0L || !STBVorbis.stb_vorbis_seek(this.decoder, var1)) {
                     return;
                  }

                  int var6 = STBVorbis.stb_vorbis_get_samples_short_interleaved(this.decoder, this.channels, var4);
                  if ((long)this.errorBuffer.get(0) != 0L) {
                     System.err.println("Error getting audio samples for " + this.sound.path + " - " + this.errorBuffer.get(0));
                  } else {
                     var4.position(0);
                     if (this.isDisposed.get()) {
                        return;
                     }

                     int var7 = AL10.alGenBuffers();
                     AL10.alBufferData(var7, this.channels > 1 ? 4355 : 4353, var4, this.sampleRate);
                     this.buffers.add(var7);
                     var3.handle(var7, var1, var6);
                     return;
                  }
               }
            } finally {
               this.isWorking.set(false);
            }

         });
      }
   }

   public void disposeBuffer(int var1) {
   }

   public void dispose() {
      synchronized(this.inputBytes) {
         this.isDisposed.set(true);
         this.buffers.forEach(AL10::alDeleteBuffers);
         if (this.decoder != 0L) {
            STBVorbis.stb_vorbis_close(this.decoder);
            this.decoder = 0L;
         }

         if (this.errorBuffer != null) {
            MemoryUtil.memFree(this.errorBuffer);
            this.errorBuffer = null;
         }

         if (this.info != null) {
            this.info.free();
            this.info = null;
         }

         Iterator var2 = this.sampleBuffers.iterator();

         while(var2.hasNext()) {
            ShortBuffer var3 = (ShortBuffer)var2.next();
            MemoryUtil.memFree(var3);
         }

         this.sampleBuffers.clear();
      }
   }
}
