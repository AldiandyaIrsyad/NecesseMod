package necesse.entity.mobs.friendly.human;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameLootUtils;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.QuestMarkerOptions;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.HumanAI;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameHair;
import necesse.gfx.GameSkin;
import necesse.gfx.HumanLook;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.CountOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.light.GameLight;

public class GuardHumanMob extends HumanShop {
   private HumanLook look = new HumanLook();

   public GuardHumanMob() {
      super(2000, 700, "guard");
      this.attackCooldown = 500;
      this.attackAnimTime = 500;
      this.setSpeed(35.0F);
      this.setSwimSpeed(1.0F);
      this.canJoinAdventureParties = true;
      this.jobTypeHandler.getPriority("crafting").disabledBySettler = true;
      this.jobTypeHandler.getPriority("forestry").disabledBySettler = true;
      this.jobTypeHandler.getPriority("farming").disabledBySettler = true;
      this.equipmentInventory.setItem(1, new InventoryItem("ironchestplate"));
      this.equipmentInventory.setItem(2, new InventoryItem("ironboots"));
      this.equipmentInventory.setItem(6, new InventoryItem("ironsword"));
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new HumanAI(640, true, true, 25000), new AIMover(HumanMob.humanPathIterations));
   }

   public void setSettlerSeed(int var1) {
      this.look = new HumanLook(new GameRandom((long)(var1 + 5)));
      super.setSettlerSeed(var1);
   }

   public HumanMob.HumanGender getHumanGender() {
      return this.convertHairGender(GameHair.getHairGender(this.look.getHair()));
   }

   public float getRegenFlat() {
      return this.adventureParty.isInAdventureParty() && !this.isSettlerOnCurrentLevel() ? super.getRegenFlat() : super.getRegenFlat() * 5.0F;
   }

   public LootTable getLootTable() {
      return super.getLootTable();
   }

   public void spawnDeathParticles(float var1, float var2) {
      GameSkin var3 = this.look.getGameSkin(true);

      for(int var4 = 0; var4 < 4; ++var4) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), var3.getBodyTexture(), GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 10.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public DrawOptions getUserDrawOptions(Level var1, int var2, int var3, TickManager var4, GameCamera var5, PlayerMob var6, Consumer<HumanDrawOptions> var7) {
      GameLight var8 = var1.getLightLevel(var2 / 32, var3 / 32);
      int var9 = var5.getDrawX(var2) - 22 - 10;
      int var10 = var5.getDrawY(var3) - 44 - 7;
      Point var11 = this.getAnimSprite(var2, var3, this.dir);
      HumanDrawOptions var12 = (new HumanDrawOptions(var1, this.look, true)).helmet(this.getDisplayArmor(0, (String)null)).chestplate(this.getDisplayArmor(1, (String)null)).boots(this.getDisplayArmor(2, (String)null)).invis((Boolean)this.buffManager.getModifier(BuffModifiers.INVISIBILITY)).sprite(var11).dir(this.dir).light(var8);
      var7.accept(var12);
      DrawOptions var13 = var12.pos(var9, var10);
      DrawOptions var14 = this.getMarkerDrawOptions(var2, var3, var8, var5, 0, -45, var6);
      return () -> {
         var13.draw();
         var14.draw();
      };
   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      if (this.objectUser == null || this.objectUser.object.drawsUsers()) {
         if (this.isVisible()) {
            GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
            int var11 = var8.getDrawX(var5) - 22 - 10;
            int var12 = var8.getDrawY(var6) - 44 - 7;
            Point var13 = this.getAnimSprite(var5, var6, this.dir);
            var12 += this.getBobbing(var5, var6);
            var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
            HumanDrawOptions var14 = (new HumanDrawOptions(var4, this.look, true)).helmet(this.getDisplayArmor(0, (String)null)).chestplate(this.getDisplayArmor(1, (String)null)).boots(this.getDisplayArmor(2, (String)null)).invis((Boolean)this.buffManager.getModifier(BuffModifiers.INVISIBILITY)).sprite(var13).dir(this.dir).light(var10);
            if (this.inLiquid(var5, var6)) {
               var12 -= 10;
               var14.armSprite(2);
               var14.mask(MobRegistry.Textures.boat_mask[var13.y % 4], 0, -7);
            }

            var14 = this.setCustomItemAttackOptions(var14);
            final DrawOptions var15 = var14.pos(var11, var12);
            final TextureDrawOptionsEnd var16 = this.inLiquid(var5, var6) ? MobRegistry.Textures.steelBoat.initDraw().sprite(0, this.dir % 4, 64).light(var10).pos(var11, var12 + 7) : null;
            final DrawOptions var17 = this.getMarkerDrawOptions(var5, var6, var10, var8, 0, -45, var9);
            var1.add(new MobDrawable() {
               public void draw(TickManager var1) {
                  if (var16 != null) {
                     var16.draw();
                  }

                  var15.draw();
                  var17.draw();
               }
            });
            this.addShadowDrawables(var2, var5, var6, var10, var8);
         }
      }
   }

   public QuestMarkerOptions getMarkerOptions(PlayerMob var1) {
      return this.isTravelingHuman() ? new QuestMarkerOptions('?', QuestMarkerOptions.orangeColor) : super.getMarkerOptions(var1);
   }

   public LevelIdentifier getRecruitedToLevel(ServerClient var1) {
      return this.isTravelingHuman() && SettlementLevelData.getSettlementData(this.getLevel()) != null ? this.getLevel().getIdentifier() : null;
   }

   public List<InventoryItem> getRecruitItems(ServerClient var1) {
      if (this.isSettler()) {
         return null;
      } else {
         GameRandom var2 = new GameRandom((long)(this.getSettlerSeed() * 37));
         if (this.isTravelingHuman()) {
            return Collections.singletonList(new InventoryItem("coin", var2.getIntBetween(200, 350)));
         } else {
            LootTable var3 = new LootTable(new LootItemInterface[]{new LootItem("healthpotion", Integer.MAX_VALUE), new CountOfTicketLootItems(1, new Object[]{100, new LootItem("speedpotion", Integer.MAX_VALUE), 100, new LootItem("healthregenpotion", Integer.MAX_VALUE), 100, new LootItem("attackspeedpotion", Integer.MAX_VALUE), 100, new LootItem("battlepotion", Integer.MAX_VALUE), 100, new LootItem("resistancepotion", Integer.MAX_VALUE)})});
            ArrayList var4 = GameLootUtils.getItemsValuedAt(var2, var2.getIntBetween(200, 400), 0.20000000298023224, new LootItem("coin", Integer.MAX_VALUE));
            var4.addAll(GameLootUtils.getItemsValuedAt(var2, var2.getIntBetween(50, 100), 0.20000000298023224, var3));
            var4.sort(Comparator.comparing(InventoryItem::getBrokerValue).reversed());
            return var4;
         }
      }
   }
}
