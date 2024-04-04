package necesse.inventory.item.questItem;

import necesse.engine.localization.message.GameMessage;
import necesse.entity.particle.CirclingFishParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.matItem.FishItemInterface;
import necesse.level.maps.Level;

public class QuestFishItem extends QuestItem implements FishItemInterface {
   public GameTexture circlingFishTexture;

   public QuestFishItem(int var1, GameMessage var2) {
      super(var1, var2);
   }

   public QuestFishItem(GameMessage var1) {
      super(var1);
   }

   public QuestFishItem() {
   }

   public void loadTextures() {
      super.loadTextures();
      this.circlingFishTexture = GameTexture.fromFile("particles/circlingfish");
   }

   public Particle getParticle(Level var1, int var2, int var3, int var4) {
      return new CirclingFishParticle(var1, (float)var2, (float)var3, (long)var4, this.circlingFishTexture, 60);
   }
}
