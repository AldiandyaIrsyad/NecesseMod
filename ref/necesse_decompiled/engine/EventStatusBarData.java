package necesse.engine;

import java.awt.Color;
import java.util.LinkedList;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.util.GameMath;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.fairType.FairTypeDrawOptionsContainer;
import necesse.gfx.gameFont.FontOptions;

public class EventStatusBarData {
   public int dataBufferTime = 1000;
   private FairTypeDrawOptionsContainer displayNameDrawOptions;
   private final LinkedList<StatusAtTime> buffer = new LinkedList();

   public EventStatusBarData(GameMessage var1) {
      this.displayNameDrawOptions = new FairTypeDrawOptionsContainer(() -> {
         return (new FairType()).append((new FontOptions(16)).outline(), var1.translate()).getDrawOptions(FairType.TextAlign.CENTER);
      });
      this.displayNameDrawOptions.updateOnLanguageChange();
   }

   public EventStatusBarData append(int var1, int var2) {
      this.buffer.addFirst(new StatusAtTime(var1, var2, System.currentTimeMillis()));
      return this;
   }

   public void cleanOldData() {
      while(true) {
         if (!this.buffer.isEmpty()) {
            StatusAtTime var1 = (StatusAtTime)this.buffer.getLast();
            if (var1.time + (long)this.dataBufferTime < System.currentTimeMillis()) {
               this.buffer.removeLast();
               continue;
            }
         }

         return;
      }
   }

   public boolean hasData() {
      return !this.buffer.isEmpty();
   }

   public StatusAtTime getLatest() {
      return (StatusAtTime)this.buffer.getFirst();
   }

   public StatusAtTime getBuffered() {
      return (StatusAtTime)this.buffer.getLast();
   }

   public FairTypeDrawOptions getDisplayNameDrawOptions() {
      return this.displayNameDrawOptions.get();
   }

   public GameMessage getStatusText(StatusAtTime var1) {
      float var2 = var1.getPercent();
      float var3 = (float)((int)(var2 * 1000.0F)) / 10.0F;
      return new StaticMessage(var1.current + "/" + var1.max + " " + var3 + "%");
   }

   public Color getBufferColor() {
      return new Color(181, 80, 21, 150);
   }

   public Color getFillColor() {
      return new Color(201, 24, 24);
   }

   public static class StatusAtTime {
      public final int current;
      public final int max;
      public final long time;

      public StatusAtTime(int var1, int var2, long var3) {
         this.current = var1;
         this.max = var2;
         this.time = var3;
      }

      public float getPercent() {
         return GameMath.limit((float)this.current / (float)this.max, 0.0F, 1.0F);
      }
   }
}
