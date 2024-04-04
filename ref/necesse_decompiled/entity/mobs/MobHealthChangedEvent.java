package necesse.entity.mobs;

public class MobHealthChangedEvent extends MobGenericEvent {
   public final int lastHealth;
   public final int currentHealth;
   public final boolean fromUpdatePacket;

   public MobHealthChangedEvent(int var1, int var2, boolean var3) {
      this.lastHealth = var1;
      this.currentHealth = var2;
      this.fromUpdatePacket = var3;
   }
}
