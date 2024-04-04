package necesse.entity.objectEntity;

import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.TrainingDummyMob;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.maps.Level;

public class TrainingDummyObjectEntity extends ObjectEntity {
   private int dummyMobID = -1;

   public TrainingDummyObjectEntity(Level var1, int var2, int var3) {
      super(var1, "trainingdummy", var2, var3);
   }

   public void setupContentPacket(PacketWriter var1) {
      if (this.dummyMobID == -1) {
         this.generateMobID();
      }

      var1.putNextInt(this.dummyMobID);
   }

   public void applyContentPacket(PacketReader var1) {
      this.dummyMobID = var1.getNextInt();
   }

   public void clientTick() {
      super.clientTick();
      TrainingDummyMob var1 = this.getMob();
      if (var1 != null) {
         var1.keepAlive(this);
      }

   }

   public void serverTick() {
      super.serverTick();
      TrainingDummyMob var1 = this.getMob();
      if (var1 == null) {
         var1 = this.generateMobID();
         this.markDirty();
      }

      var1.keepAlive(this);
   }

   private TrainingDummyMob generateMobID() {
      TrainingDummyMob var1 = this.getMob();
      if (var1 != null) {
         var1.remove();
      }

      TrainingDummyMob var2 = new TrainingDummyMob();
      this.getLevel().entityManager.addMob(var2, (float)(this.getX() * 32 + 16), (float)(this.getY() * 32 + 16));
      this.dummyMobID = var2.getUniqueID();
      return var2;
   }

   private TrainingDummyMob getMob() {
      if (this.dummyMobID == -1) {
         return null;
      } else {
         Mob var1 = (Mob)this.getLevel().entityManager.mobs.get(this.dummyMobID, false);
         return var1 != null ? (TrainingDummyMob)var1 : null;
      }
   }

   public void remove() {
      super.remove();
      TrainingDummyMob var1 = this.getMob();
      if (var1 != null) {
         var1.remove();
      }

   }

   public void onMouseHover(PlayerMob var1, boolean var2) {
      super.onMouseHover(var1, var2);
      if (var2) {
         Screen.addTooltip(new StringTooltips("MobID: " + this.dummyMobID), TooltipLocation.INTERACT_FOCUS);
      }

   }
}
