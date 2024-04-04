package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import java.awt.geom.Point2D;
import necesse.engine.Screen;
import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketBlinkScepter;
import necesse.engine.network.packet.PacketForceOfWind;
import necesse.engine.sound.SoundEffect;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.gfx.GameResources;
import necesse.level.maps.Level;

public class BlinkScepterTrinketBuff extends TrinketBuff implements BuffAbility {
   public BlinkScepterTrinketBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
   }

   public void runAbility(PlayerMob var1, ActiveBuff var2, Packet var3) {
      Level var4 = var1.getLevel();
      short var5 = 224;
      Point2D.Float var6 = PacketBlinkScepter.getMobDir(var1);
      PacketBlinkScepter.applyToPlayer(var4, var1, var6.x, var6.y, (float)var5);
      PacketForceOfWind.addCooldownStack(var1, 5.0F, false);
      if (var4.isClient()) {
         Screen.playSound(GameResources.swoosh2, SoundEffect.effect(var1).volume(0.5F));
      }

   }

   public boolean canRunAbility(PlayerMob var1, ActiveBuff var2, Packet var3) {
      return !var2.owner.isRiding() && !PacketForceOfWind.isOnCooldown(var2.owner);
   }
}
