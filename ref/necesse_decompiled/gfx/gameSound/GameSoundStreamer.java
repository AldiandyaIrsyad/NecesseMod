package necesse.gfx.gameSound;

public abstract class GameSoundStreamer {
   public GameSoundStreamer() {
   }

   public abstract int getLengthInSamples();

   public abstract int getSampleRate();

   public abstract float getLengthInSeconds();

   public abstract boolean isDone(int var1);

   public abstract boolean isWorking();

   public abstract void getNextBuffers(int var1, int var2, BufferHandler var3);

   public abstract void disposeBuffer(int var1);

   public abstract void dispose();

   public interface BufferHandler {
      void handle(int var1, int var2, int var3);
   }
}
