package necesse.level.maps.levelData.settlementData.settler;

import java.awt.Color;
import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.GenericHumanMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class GenericSettler extends Settler {
   public GenericSettler() {
      super("human");
   }

   public DrawOptions getSettlerFaceDrawOptions(int var1, int var2, Color var3, Mob var4) {
      if (var4 != null && var4 instanceof GenericHumanMob) {
         GenericHumanMob var5 = (GenericHumanMob)var4;
         GameTexture var6 = var5.look.getGameSkin(true).getBodyTexture();
         GameTexture var7 = var5.look.getHairTexture();
         GameTexture var8 = var5.look.getBackHairTexture();
         TextureDrawOptionsEnd var9;
         if (var8 != null) {
            var9 = var8.initDraw().sprite(0, 2, 64).pos(var1 - 16 - 16, var2 - 8 - 16);
         } else {
            var9 = null;
         }

         TextureDrawOptionsEnd var10 = var6.initDraw().sprite(0, 9, 32).pos(var1 - 16, var2 - 16);
         TextureDrawOptionsEnd var11;
         if (var7 != null) {
            var11 = var7.initDraw().sprite(0, 2, 64).pos(var1 - 16 - 16, var2 - 8 - 16);
         } else {
            var11 = null;
         }

         return () -> {
            if (var9 != null) {
               var9.draw();
            }

            var10.draw();
            if (var11 != null) {
               var11.draw();
            }

         };
      } else {
         return super.getSettlerFaceDrawOptions(var1, var2, var3, var4);
      }
   }

   public GameMessage getAcquireTip() {
      return new LocalMessage("settlement", "foundinvillagetip");
   }

   public void addNewRecruitSettler(SettlementLevelData var1, boolean var2, TicketSystemList<Supplier<HumanMob>> var3) {
      var3.addObject(var2 ? 100 : 50, this.getNewRecruitMob(var1));
   }
}
