package necesse.entity.mobs.friendly.human.humanShop;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameLootUtils;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.QuestMarkerOptions;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.CountOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.villageShops.ShopItem;
import necesse.level.maps.levelData.villageShops.VillageShopsData;
import necesse.level.maps.light.GameLight;

public class FarmerHumanMob extends HumanShop {
   public FarmerHumanMob() {
      super(500, 200, "farmer");
      this.setSwimSpeed(1.0F);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-14, -12, 28, 24);
      this.selectBox = new Rectangle(-14, -41, 28, 48);
      this.jobTypeHandler.getPriority("fertilize").disabledBySettler = false;
      this.equipmentInventory.setItem(6, new InventoryItem("copperpitchfork"));
   }

   public LootTable getLootTable() {
      return super.getLootTable();
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.farmer.body, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 10.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public DrawOptions getUserDrawOptions(Level var1, int var2, int var3, TickManager var4, GameCamera var5, PlayerMob var6, Consumer<HumanDrawOptions> var7) {
      GameLight var8 = var1.getLightLevel(var2 / 32, var3 / 32);
      int var9 = var5.getDrawX(var2) - 22 - 10;
      int var10 = var5.getDrawY(var3) - 44 - 7;
      Point var11 = this.getAnimSprite(var2, var3, this.dir);
      HumanDrawOptions var12 = (new HumanDrawOptions(var1, MobRegistry.Textures.farmer)).helmet(this.getDisplayArmor(0, "farmerhat")).chestplate(this.getDisplayArmor(1, "farmershirt")).boots(this.getDisplayArmor(2, "farmershoes")).invis((Boolean)this.buffManager.getModifier(BuffModifiers.INVISIBILITY)).sprite(var11).dir(this.dir).light(var8);
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
            HumanDrawOptions var14 = (new HumanDrawOptions(var4, MobRegistry.Textures.farmer)).helmet(this.getDisplayArmor(0, "farmerhat")).chestplate(this.getDisplayArmor(1, "farmershirt")).boots(this.getDisplayArmor(2, "farmershoes")).invis((Boolean)this.buffManager.getModifier(BuffModifiers.INVISIBILITY)).sprite(var13).dir(this.dir).holdItem(new InventoryItem("farmerpitchfork")).light(var10);
            if (this.inLiquid(var5, var6)) {
               var12 -= 10;
               var14.armSprite(2);
               var14.mask(MobRegistry.Textures.boat_mask[var13.y % 4], 0, -7);
            }

            var14 = this.setCustomItemAttackOptions(var14);
            final DrawOptions var15 = var14.pos(var11, var12);
            final TextureDrawOptionsEnd var16 = this.inLiquid(var5, var6) ? MobRegistry.Textures.woodBoat.initDraw().sprite(0, this.dir % 4, 64).light(var10).pos(var11, var12 + 7) : null;
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

   public HumanMob.HumanGender getHumanGender() {
      return HumanMob.HumanGender.MALE;
   }

   protected ArrayList<GameMessage> getMessages(ServerClient var1) {
      return this.getLocalMessages("farmertalk", 4);
   }

   public LevelIdentifier getRecruitedToLevel(ServerClient var1) {
      return this.isTravelingHuman() && SettlementLevelData.getSettlementData(this.getLevel()) != null ? this.getLevel().getIdentifier() : null;
   }

   public List<InventoryItem> getRecruitItems(ServerClient var1) {
      if (this.isSettler()) {
         return null;
      } else {
         GameRandom var2 = new GameRandom((long)(this.getSettlerSeed() * 83));
         if (this.isTravelingHuman()) {
            return Collections.singletonList(new InventoryItem("coin", var2.getIntBetween(250, 400)));
         } else {
            CountOfTicketLootItems var3 = new CountOfTicketLootItems(var2.getIntBetween(2, 3), new Object[]{100, new LootItem("wheat", Integer.MAX_VALUE), 100, new LootItem("sunflower", Integer.MAX_VALUE), 100, new LootItem("fertilizer", Integer.MAX_VALUE), 500, new LootItem("coin", Integer.MAX_VALUE)});
            int var4 = var2.getIntBetween(300, 500);
            ArrayList var5 = GameLootUtils.getItemsValuedAt(var2, var4, 0.20000000298023224, var3);
            var5.sort(Comparator.comparing(InventoryItem::getBrokerValue).reversed());
            return var5;
         }
      }
   }

   public ArrayList<ShopItem> getShopItems(VillageShopsData var1, ServerClient var2) {
      if (this.isTravelingHuman()) {
         return null;
      } else {
         ArrayList var3 = new ArrayList();
         GameRandom var4 = new GameRandom(this.getShopSeed() + 5L);
         var3.add(ShopItem.item("rope", this.getRandomHappinessPrice(var4, 100, 190, 30)));
         conditionSection(var4, var4.nextBoolean(), (var2x) -> {
            var3.add(ShopItem.item("farmerhat", this.getRandomHappinessPrice(var2x, 85, 160, 30)));
         });
         conditionSection(var4, var4.nextBoolean(), (var2x) -> {
            var3.add(ShopItem.item("farmershirt", this.getRandomHappinessPrice(var2x, 100, 200, 30)));
         });
         conditionSection(var4, var4.nextBoolean(), (var2x) -> {
            var3.add(ShopItem.item("farmershoes", this.getRandomHappinessPrice(var2x, 70, 150, 30)));
         });
         conditionSection(var4, var2.characterStats().mob_kills.getKills("voidwizard") > 0, (var2x) -> {
            var3.add(ShopItem.item("farmingscythe", this.getRandomHappinessPrice(var2x, 650, 1000, 100)));
         });
         ShopItem.addStockedItem(var3, var1, "fertilizer", this.getRandomHappinessPrice(var4, 4, 12, 2));
         ShopItem.addStockedItem(var3, var1, "wheatseed", this.getRandomHappinessPrice(var4, 20, 50, 8));
         ShopItem.addStockedItem(var3, var1, "cornseed", this.getRandomHappinessPrice(var4, 20, 50, 8));
         ShopItem.addStockedItem(var3, var1, "tomatoseed", this.getRandomHappinessPrice(var4, 20, 50, 8));
         ShopItem.addStockedItem(var3, var1, "cabbageseed", this.getRandomHappinessPrice(var4, 20, 50, 8));
         conditionSection(var4, var2.characterStats().mob_kills.getKills("queenspider") > 0, (var3x) -> {
            ShopItem.addStockedItem(var3, var1, "chilipepperseed", this.getRandomHappinessPrice(var3x, 25, 55, 8));
            ShopItem.addStockedItem(var3, var1, "sugarbeetseed", this.getRandomHappinessPrice(var3x, 25, 55, 8));
         });
         conditionSection(var4, var2.characterStats().mob_kills.getKills("swampguardian") > 0, (var3x) -> {
            ShopItem.addStockedItem(var3, var1, "eggplantseed", this.getRandomHappinessPrice(var3x, 35, 65, 8));
            ShopItem.addStockedItem(var3, var1, "potatoseed", this.getRandomHappinessPrice(var3x, 35, 65, 8));
         });
         conditionSection(var4, var2.characterStats().mob_kills.getKills("ancientvulture") > 0, (var3x) -> {
            ShopItem.addStockedItem(var3, var1, "riceseed", this.getRandomHappinessPrice(var3x, 45, 75, 8));
            ShopItem.addStockedItem(var3, var1, "carrotseed", this.getRandomHappinessPrice(var3x, 45, 75, 8));
         });
         conditionSection(var4, var2.characterStats().mob_kills.getKills("piratecaptain") > 0, (var3x) -> {
            ShopItem.addStockedItem(var3, var1, "blueberrysapling", this.getRandomHappinessPrice(var3x, 250, 500, 50));
            ShopItem.addStockedItem(var3, var1, "blackberrysapling", this.getRandomHappinessPrice(var3x, 250, 500, 50));
            ShopItem.addStockedItem(var3, var1, "applesapling", this.getRandomHappinessPrice(var3x, 500, 1000, 100));
            ShopItem.addStockedItem(var3, var1, "coconutsapling", this.getRandomHappinessPrice(var3x, 500, 1000, 100));
         });
         conditionSection(var4, var2.characterStats().mob_kills.getKills("reaper") > 0, (var3x) -> {
            ShopItem.addStockedItem(var3, var1, "onionseed", this.getRandomHappinessPrice(var3x, 55, 85, 8));
            ShopItem.addStockedItem(var3, var1, "pumpkinseed", this.getRandomHappinessPrice(var3x, 55, 85, 8));
         });
         conditionSection(var4, var2.characterStats().mob_kills.getKills("cryoqueen") > 0, (var3x) -> {
            ShopItem.addStockedItem(var3, var1, "strawberryseed", this.getRandomHappinessPrice(var3x, 65, 95, 8));
         });
         conditionSection(var4, var2.characterStats().mob_kills.getKills("sageandgrit") > 0, (var3x) -> {
            ShopItem.addStockedItem(var3, var1, "coffeebeans", this.getRandomHappinessPrice(var3x, 75, 105, 8));
            ShopItem.addStockedItem(var3, var1, "lemonsapling", this.getRandomHappinessPrice(var3x, 1000, 1600, 100));
            ShopItem.addStockedItem(var3, var1, "bananasapling", this.getRandomHappinessPrice(var3x, 1000, 1600, 100));
         });
         ShopItem.addStockedItem(var3, var1, "wheat", this.getRandomHappinessPrice(var4, -10, -2, 2));
         ShopItem.addStockedItem(var3, var1, "sunflower", this.getRandomHappinessPrice(var4, -12, -3, 2));
         return var3;
      }
   }
}
