package necesse.entity.mobs;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameUtils;

public class MobHealthScaling {
   private Mob owner;
   private float playerPercentStart;
   private float playerPercentDec;
   private float partyPercentStart;
   private float partyPercentDec;
   private int startMaxHealth;
   private int healthIncrease;
   private int lastPlayers;
   private int lastPartyMembers;

   public MobHealthScaling(Mob var1, float var2, float var3, float var4, float var5) {
      this.owner = var1;
      this.startMaxHealth = var1.getMaxHealthFlat();
      this.playerPercentStart = var2;
      this.playerPercentDec = var3;
      this.partyPercentStart = var4;
      this.partyPercentDec = var5;
   }

   public MobHealthScaling(Mob var1) {
      this(var1, 0.8F, 0.04F, 0.2F, 0.02F);
   }

   public void serverTick() {
      if (this.owner != null && this.owner.getLevel() != null) {
         if (this.owner.isServer()) {
            int var1 = this.owner.getLevel().presentPlayers;
            int var2 = this.owner.getLevel().presentAdventurePartyMembers;
            if (this.lastPlayers < var1 || this.lastPartyMembers != var2 || this.startMaxHealth != this.owner.getMaxHealthFlat() || var1 != this.lastPlayers && this.owner.getHealth() >= this.owner.getMaxHealth() && !this.owner.isInCombat()) {
               if (this.owner.getHealth() < this.owner.getMaxHealth()) {
                  this.lastPlayers = Math.max(this.lastPlayers, var1);
               } else {
                  this.lastPlayers = var1;
               }

               this.lastPartyMembers = var2;
               this.startMaxHealth = this.owner.getMaxHealthFlat();
               this.updateHealthIncrease(true);
               this.owner.sendHealthPacket(false);
            }

         }
      }
   }

   public void setupHealthPacket(PacketWriter var1, boolean var2) {
      var1.putNextByteUnsigned(this.lastPlayers);
      var1.putNextShortUnsigned(this.lastPartyMembers);
   }

   public void applyHealthPacket(PacketReader var1, boolean var2) {
      this.lastPlayers = var1.getNextByteUnsigned();
      this.lastPartyMembers = var1.getNextShortUnsigned();
      this.updateHealthIncrease(true);
   }

   public void updatedMaxHealth() {
      this.startMaxHealth = this.owner.getMaxHealthFlat();
      this.updateHealthIncrease(false);
   }

   private void updateHealthIncrease(boolean var1) {
      float var2 = this.owner.getHealthPercent();
      this.healthIncrease = Math.round((float)this.startMaxHealth * (GameUtils.getMultiplayerScaling(this.lastPlayers, this.playerPercentStart, this.playerPercentDec) - 1.0F + (GameUtils.getMultiplayerScaling(this.lastPartyMembers + 1, Integer.MAX_VALUE, this.partyPercentStart, this.partyPercentDec) - 1.0F)));
      if (var1) {
         int var3 = (int)((float)this.owner.getMaxHealth() * var2);
         this.owner.setHealthHidden(var3);
      }

   }

   public int getHealthIncrease() {
      return this.healthIncrease;
   }
}
