package necesse.entity.objectEntity;

import java.awt.Color;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.maps.Level;

public class CompostBinObjectEntity extends ProcessingTechInventoryObjectEntity {
   public CompostBinObjectEntity(Level var1, int var2, int var3) {
      super(var1, "compostbin", var2, var3, 2, 2, RecipeTechRegistry.COMPOST_BIN);
   }

   public void clientTick() {
      super.clientTick();
      if (this.isProcessing() && GameRandom.globalRandom.nextInt(10) == 0) {
         int var1 = 24 + GameRandom.globalRandom.nextInt(16);
         this.getLevel().entityManager.addParticle((float)(this.getX() * 32 + GameRandom.globalRandom.nextInt(32)), (float)(this.getY() * 32 + 32), Particle.GType.COSMETIC).color(new Color(150, 150, 150)).heightMoves((float)var1, (float)(var1 + 20)).lifeTime(1000);
      }

   }

   public int getProcessTime() {
      return 45000;
   }

   public void onMouseHover(PlayerMob var1, boolean var2) {
      super.onMouseHover(var1, var2);
      StringTooltips var3 = new StringTooltips(this.getObject().getDisplayName());
      if (this.isProcessing()) {
         var3.add(Localization.translate("ui", "composting"));
      } else {
         var3.add(Localization.translate("ui", "needcompost"));
      }

      Screen.addTooltip(var3, TooltipLocation.INTERACT_FOCUS);
   }
}
