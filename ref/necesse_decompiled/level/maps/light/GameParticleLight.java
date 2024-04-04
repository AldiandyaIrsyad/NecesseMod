package necesse.level.maps.light;

public class GameParticleLight {
   protected GameLight light;
   protected long endTime;

   public GameParticleLight(GameLight var1) {
      this.light = var1;
   }

   public void updateLevel(long var1) {
      if (this.endTime == 0L) {
         this.light.setLevel(0.0F);
      } else {
         long var3 = this.endTime - var1;
         if (var3 < 0L) {
            this.light.setLevel(0.0F);
         } else {
            float var5 = (float)var3 / 750.0F;
            this.light.setLevel((float)((int)(255.0F * var5)));
         }
      }
   }
}
