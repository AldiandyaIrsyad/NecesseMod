package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import java.awt.geom.Point2D;
import necesse.engine.Screen;
import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketForceOfWind;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.gfx.GameResources;
import necesse.level.maps.Level;

public class ForceOfWindTrinketBuff extends TrinketBuff implements BuffAbility {
   public ForceOfWindTrinketBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
   }

   public void runAbility(PlayerMob var1, ActiveBuff var2, Packet var3) {
      Level var4 = var1.getLevel();
      short var5 = 150;
      Point2D.Float var6 = PacketForceOfWind.getMobDir(var1);
      PacketForceOfWind.applyToPlayer(var4, var1, var6.x, var6.y, (float)var5);
      PacketForceOfWind.addCooldownStack(var1, 3.0F, false);
      var1.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, var1, 0.15F, (Attacker)null), false);
      var1.buffManager.forceUpdateBuffs();
      if (var4.isClient()) {
         Screen.playSound(GameResources.swoosh, SoundEffect.effect(var1).volume(0.5F).pitch(1.7F));
      }

   }

   public boolean canRunAbility(PlayerMob var1, ActiveBuff var2, Packet var3) {
      return !var2.owner.isRiding() && !PacketForceOfWind.isOnCooldown(var2.owner);
   }
}
