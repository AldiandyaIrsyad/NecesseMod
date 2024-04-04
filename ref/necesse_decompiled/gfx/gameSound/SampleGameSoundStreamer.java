package necesse.gfx.gameSound;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import necesse.engine.Screen;
import org.lwjgl.openal.AL10;

public class SampleGameSoundStreamer extends GameSoundStreamer {
   private final AtomicBoolean isWorking;
   public final GameSound.VorbisSamples data;
   private final LinkedList<Integer> buffers = new LinkedList();

   public SampleGameSoundStreamer(GameSound.VorbisSamples var1) {
      this.data = var1;
      this.isWorking = new AtomicBoolean(false);
   }

   public int getLengthInSamples() {
      return this.data.info.totalSamples;
   }

   public int getSampleRate() {
      return this.data.info.sampleRate;
   }

   public float getLengthInSeconds() {
      return this.data.info.lengthSeconds;
   }

   public boolean isDone(int var1) {
      return var1 >= this.data.info.totalSamples * this.data.info.channels;
   }

   public boolean isWorking() {
      return this.isWorking.get();
   }

   public void getNextBuffers(int var1, int var2, GameSoundStreamer.BufferHandler var3) {
      if (!this.isWorking()) {
         this.isWorking.set(true);
         Screen.musicStreamer.submit(() -> {
            try {
               synchronized(this.data) {
                  int var5 = Math.min(this.data.info.totalSamples - var1, var2);
                  int var6 = var5 * this.data.info.channels;
                  int var7 = 1048576;
                  int var8 = var1;
                  int var9 = var6 / var7 + 1;
                  int[] var10 = new int[var9];
                  AL10.alGenBuffers(var10);
                  int var11 = 0;
                  int[] var12 = var10;
                  int var13 = var10.length;

                  for(int var14 = 0; var14 < var13; ++var14) {
                     int var15 = var12[var14];
                     int var16 = Math.min(var7, var6 - var11);
                     short[] var17 = new short[var16];
                     int var18 = var8;
                     this.data.samples.position(var8 * this.data.info.channels);
                     this.data.samples.get(var17);
                     var8 += var16 / this.data.info.channels;
                     var11 += var16;
                     AL10.alBufferData(var15, this.data.info.channels > 1 ? 4355 : 4353, var17, this.data.info.sampleRate);
                     this.buffers.add(var15);
                     var3.handle(var15, var18, var8);
                  }

               }
            } finally {
               this.isWorking.set(false);
            }
         });
      }
   }

   public void disposeBuffer(int var1) {
      synchronized(this.data) {
         if (this.buffers.contains(var1)) {
            AL10.alDeleteBuffers(var1);
            this.buffers.remove(var1);
         }

      }
   }

   public void dispose() {
      synchronized(this.data) {
         this.buffers.forEach(AL10::alDeleteBuffers);
      }
   }
}
