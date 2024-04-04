package necesse.entity.mobs.buffs.staticBuffs;

import java.io.FileNotFoundException;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.MovementTickBuff;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTexture.GameTexture;

public class StaminaBuff extends Buff implements MovementTickBuff {
   protected GameTexture cooldownTexture;

   public StaminaBuff() {
      this.isPassive = true;
      this.shouldSave = true;
      this.overrideSync = true;
      this.canCancel = false;
      this.isImportant = true;
   }

   public void loadTextures() {
      super.loadTextures();

      try {
         this.cooldownTexture = GameTexture.fromFileRaw("buffs/" + this.getStringID() + "_cooldown");
      } catch (FileNotFoundException var2) {
         this.cooldownTexture = GameTexture.fromFile("buffs/unknown");
      }

   }

   public void drawIcon(int var1, int var2, ActiveBuff var3) {
      GNDItemMap var4 = var3.getGndData();
      boolean var5 = var4.getBoolean("onCooldown");
      if (var5) {
         this.cooldownTexture.initDraw().size(32, 32).draw(var1, var2);
      } else {
         this.iconTexture.initDraw().size(32, 32).draw(var1, var2);
      }

      float var6 = var4.getFloat("stamina");
      var6 = GameMath.limit(var6, 0.0F, 1.0F);
      int var7 = (int)(Math.abs(var6 - 1.0F) * 100.0F);
      var7 = Math.max(var7, var5 ? 0 : 1);
      String var8 = var7 + "%";
      int var9 = FontManager.bit.getWidthCeil(var8, durationFontOptions);
      FontManager.bit.drawString((float)(var1 + 16 - var9 / 2), (float)(var2 + 30), var8, durationFontOptions);
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
   }

   public void tickMovement(ActiveBuff var1, float var2) {
      long var3 = var1.owner.getWorldEntity().getTime();
      GNDItemMap var5 = var1.getGndData();
      boolean var6 = var5.getBoolean("onCooldown");
      float var7 = var5.getFloat("stamina");
      if (var7 <= 0.0F) {
         var5.setBoolean("onCooldown", false);
         var1.remove();
      } else {
         long var8 = var5.getLong("lastUsageTime");
         long var10 = var3 - var8;
         int var12 = var6 ? 800 : 200;
         if (var10 <= -1000L || var10 >= (long)var12) {
            long var13 = 3000L;
            float var15 = var2 / (float)var13;
            var15 *= (Float)var1.owner.buffManager.getModifier(BuffModifiers.STAMINA_REGEN);
            float var16 = (Float)var1.owner.buffManager.getModifier(BuffModifiers.STAMINA_CAPACITY);
            if (var16 != 0.0F) {
               var15 *= 1.0F / var16;
            } else {
               var15 = 1.0F;
            }

            var7 -= var15;
            if (var7 <= 0.0F) {
               var5.setFloat("stamina", 0.0F);
               var5.setBoolean("onCooldown", false);
               var1.remove();
            } else {
               var5.setFloat("stamina", var7);
            }
         }
      }

   }

   public boolean shouldDrawDuration(ActiveBuff var1) {
      return true;
   }

   public static float getCurrentStamina(Mob var0) {
      ActiveBuff var1 = var0.buffManager.getBuff(BuffRegistry.STAMINA_BUFF);
      return var1 != null ? var1.getGndData().getFloat("stamina") : 0.0F;
   }

   public static void setCurrentStamina(Mob var0, float var1) {
      ActiveBuff var2 = var0.buffManager.getBuff(BuffRegistry.STAMINA_BUFF);
      if (var2 == null) {
         var2 = var0.buffManager.addBuff(new ActiveBuff(BuffRegistry.STAMINA_BUFF, var0, 0.0F, (Attacker)null), false);
      }

      var2.getGndData().setFloat("stamina", var1);
   }

   public static boolean canStartStaminaUsage(Mob var0) {
      ActiveBuff var1 = var0.buffManager.getBuff(BuffRegistry.STAMINA_BUFF);
      if (var1 != null) {
         GNDItemMap var2 = var1.getGndData();
         if (var2.getBoolean("onCooldown")) {
            return false;
         } else {
            return var2.getFloat("stamina") < 1.0F;
         }
      } else {
         return true;
      }
   }

   public static void writeStaminaData(Mob var0, PacketWriter var1) {
      ActiveBuff var2 = var0.buffManager.getBuff(BuffRegistry.STAMINA_BUFF);
      if (var2 != null) {
         var1.putNextBoolean(true);
         var2.getGndData().writePacket(var1);
      } else {
         var1.putNextBoolean(false);
      }

   }

   public static void readStaminaData(Mob var0, PacketReader var1) {
      boolean var2 = var1.getNextBoolean();
      if (!var2) {
         var0.buffManager.removeBuff(BuffRegistry.STAMINA_BUFF, false);
      } else {
         ActiveBuff var3 = var0.buffManager.getBuff(BuffRegistry.STAMINA_BUFF);
         if (var3 == null) {
            var3 = var0.buffManager.addBuff(new ActiveBuff(BuffRegistry.STAMINA_BUFF, var0, 0.0F, (Attacker)null), false);
         }

         var3.getGndData().readPacket(var1);
      }

   }

   public static void keepStaminaUsage(Mob var0) {
      ActiveBuff var1 = var0.buffManager.getBuff(BuffRegistry.STAMINA_BUFF);
      if (var1 != null) {
         long var2 = var0.getWorldEntity().getTime();
         var1.getGndData().setLong("lastUsageTime", var2);
      }

   }

   public static float getStamina(Mob var0) {
      ActiveBuff var1 = var0.buffManager.getBuff(BuffRegistry.STAMINA_BUFF);
      if (var1 == null) {
         return 0.0F;
      } else {
         GNDItemMap var2 = var1.getGndData();
         return var2.getFloat("stamina");
      }
   }

   public static void setStamina(Mob var0, float var1, boolean var2, boolean var3) {
      ActiveBuff var4 = var0.buffManager.getBuff(BuffRegistry.STAMINA_BUFF);
      if (var4 == null) {
         var4 = var0.buffManager.addBuff(new ActiveBuff(BuffRegistry.STAMINA_BUFF, var0, 1.0F, (Attacker)null), false);
      }

      GNDItemMap var5 = var4.getGndData();
      var5.setFloat("stamina", var1);
      if (var2) {
         var5.setLong("lastUsageTime", var0.getWorldEntity().getTime());
      }

      if (var1 >= 1.0F || var3) {
         var5.setBoolean("onCooldown", true);
      }

   }

   public static boolean useStaminaAndGetValid(Mob var0, float var1) {
      var1 *= (Float)var0.buffManager.getModifier(BuffModifiers.STAMINA_USAGE);
      float var2 = (Float)var0.buffManager.getModifier(BuffModifiers.STAMINA_CAPACITY);
      if (var2 != 0.0F) {
         var1 *= 1.0F / var2;
      } else {
         var1 = 1.0F;
      }

      if (var1 > 0.0F) {
         long var3 = var0.getWorldEntity().getTime();
         ActiveBuff var5 = var0.buffManager.getBuff(BuffRegistry.STAMINA_BUFF);
         if (var5 == null) {
            var5 = var0.buffManager.addBuff(new ActiveBuff(BuffRegistry.STAMINA_BUFF, var0, 1.0F, (Attacker)null), false);
         }

         GNDItemMap var6 = var5.getGndData();
         float var7 = var6.getFloat("stamina");
         var7 = Math.min(var7 + var1, 1.0F);
         var6.setFloat("stamina", var7);
         var6.setLong("lastUsageTime", var3);
         if (var7 >= 1.0F) {
            var6.setBoolean("onCooldown", true);
            return false;
         } else {
            return true;
         }
      } else {
         return true;
      }
   }
}
