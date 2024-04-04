package necesse.entity.mobs.friendly.human.humanShop;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import necesse.engine.AbstractMusicList;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.seasons.GameSeasons;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameMusic;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.SettlementQuestTier;
import necesse.level.maps.levelData.villageShops.ShopItem;
import necesse.level.maps.levelData.villageShops.VillageShopsData;
import necesse.level.maps.light.GameLight;

public class TravelingMerchantMob extends HumanShop {
   public TravelingMerchantMob() {
      super(500, 200, (String)null);
      this.attackCooldown = 500;
      this.attackAnimTime = 500;
      this.setSwimSpeed(1.0F);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-14, -12, 28, 24);
      this.selectBox = new Rectangle(-14, -41, 28, 48);
      this.equipmentInventory.setItem(6, new InventoryItem("ironsword"));
   }

   public void init() {
      this.isTravelingHuman = true;
      super.init();
   }

   public LootTable getLootTable() {
      return super.getLootTable();
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.travelingMerchant.body, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 10.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public HumanMob.HumanGender getHumanGender() {
      return HumanMob.HumanGender.MALE;
   }

   public void drawOnMap(TickManager var1, int var2, int var3) {
      MobRegistry.Textures.travelingMerchant.body.initDraw().sprite(5, 8, 32).size(16, 16).draw(var2 - 8, var3 - 8);
   }

   public DrawOptions getUserDrawOptions(Level var1, int var2, int var3, TickManager var4, GameCamera var5, PlayerMob var6, Consumer<HumanDrawOptions> var7) {
      GameLight var8 = var1.getLightLevel(var2 / 32, var3 / 32);
      int var9 = var5.getDrawX(var2) - 22 - 10;
      int var10 = var5.getDrawY(var3) - 44 - 7;
      Point var11 = this.getAnimSprite(var2, var3, this.dir);
      HumanDrawOptions var12 = (new HumanDrawOptions(var1, MobRegistry.Textures.travelingMerchant)).helmet(this.getDisplayArmor(0, (String)null)).chestplate(this.getDisplayArmor(1, "merchantshirt")).boots(this.getDisplayArmor(2, "merchantboots")).invis((Boolean)this.buffManager.getModifier(BuffModifiers.INVISIBILITY)).sprite(var11).dir(this.dir).light(var8);
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
            HumanDrawOptions var14 = (new HumanDrawOptions(var4, MobRegistry.Textures.travelingMerchant)).helmet(this.getDisplayArmor(0, (String)null)).chestplate(this.getDisplayArmor(1, "merchantshirt")).boots(this.getDisplayArmor(2, "merchantboots")).invis((Boolean)this.buffManager.getModifier(BuffModifiers.INVISIBILITY)).sprite(var13).dir(this.dir).light(var10);
            if (this.inLiquid(var5, var6)) {
               var12 -= 10;
               var14.armSprite(2);
               var14.mask(MobRegistry.Textures.boat_mask[var13.y % 4], 0, -7);
            }

            var14 = this.setCustomItemAttackOptions(var14);
            final DrawOptions var15 = var14.pos(var11, var12);
            final TextureDrawOptionsEnd var16 = this.inLiquid(var5, var6) ? MobRegistry.Textures.woodBoat.initDraw().sprite(0, this.dir % 4, 64).light(var10).pos(var11, var12 + 7) : null;
            var1.add(new MobDrawable() {
               public void draw(TickManager var1) {
                  if (var16 != null) {
                     var16.draw();
                  }

                  var15.draw();
               }
            });
            this.addShadowDrawables(var2, var5, var6, var10, var8);
         }
      }
   }

   protected ArrayList<GameMessage> getMessages(ServerClient var1) {
      return this.getLocalMessages("tmerchanttalk", 7);
   }

   public long getShopSeed() {
      return this.isTravelingHuman() ? (long)this.getUniqueID() : super.getShopSeed();
   }

   public ArrayList<ShopItem> getShopItems(VillageShopsData var1, ServerClient var2) {
      ArrayList var3 = new ArrayList();
      GameRandom var4 = new GameRandom(this.getShopSeed() + 5L);
      SettlementLevelData var5 = SettlementLevelData.getSettlementData(this.getLevel());
      conditionSection(var4, GameSeasons.isChristmas(), (var1x) -> {
         var3.add(ShopItem.item("theeldersjinglejamvinyl", var1x.getIntBetween(250, 350)));
         var3.add(ShopItem.item("christmastree", var1x.getIntBetween(600, 800)));
         var3.add(ShopItem.item("christmaswreath", var1x.getIntBetween(120, 150)));
         var3.add(ShopItem.item("snowlauncher", var1x.getIntBetween(600, 700)));
         var3.add(ShopItem.item("greenwrappingpaper", var1x.getIntBetween(20, 40)));
         var3.add(ShopItem.item("bluewrappingpaper", var1x.getIntBetween(20, 40)));
         var3.add(ShopItem.item("redwrappingpaper", var1x.getIntBetween(20, 40)));
         var3.add(ShopItem.item("yellowwrappingpaper", var1x.getIntBetween(20, 40)));
      });
      var3.add(ShopItem.item("rope", var4.getIntBetween(150, 200)));
      var3.add(ShopItem.item("piratemap", var4.getIntBetween(180, 220)));
      var3.add(ShopItem.item("brainonastick", var4.getIntBetween(800, 1200)));
      conditionSection(var4, var4.getChance(4), (var1x) -> {
         var3.add(ShopItem.item("binoculars", var1x.getIntBetween(200, 300)));
      });
      conditionSection(var4, var4.getChance(4), (var1x) -> {
         var3.add(ShopItem.item("recipebook", var1x.getIntBetween(500, 800)));
      });
      var3.add(ShopItem.item("potionpouch", var4.getIntBetween(1000, 1200)));
      conditionSection(var4, var2.characterStats().mob_kills.getKills("piratecaptain") > 0, (var1x) -> {
         var3.add(ShopItem.item("foolsgambit", var1x.getIntBetween(1200, 1600)));
         var3.add(ShopItem.item("ninjasmark", var1x.getIntBetween(1200, 1600)));
      });
      conditionSection(var4, var5 != null, (var2x) -> {
         conditionSection(var2x, var5.getQuestTiersCompleted() > SettlementQuestTier.getTierIndex("evilsprotector"), (var1) -> {
            var3.add(ShopItem.item("ammopouch", var1.getIntBetween(400, 500)));
         });
         conditionSection(var2x, var5.getQuestTiersCompleted() > SettlementQuestTier.getTierIndex("queenspider"), (var1) -> {
            var3.add(ShopItem.item("lunchbox", var1.getIntBetween(500, 600)));
         });
         conditionSection(var2x, var5.getQuestTiersCompleted() > SettlementQuestTier.getTierIndex("voidwizard"), (var1) -> {
            var3.add(ShopItem.item("voidpouch", var1.getIntBetween(600, 800)));
         });
         conditionSection(var2x, var5.getQuestTiersCompleted() > SettlementQuestTier.getTierIndex("ancientvulture"), (var1) -> {
            var3.add(ShopItem.item("recallflask", var1.getIntBetween(1000, 1200)));
         });
         conditionSection(var2x, var5.getQuestTiersCompleted() > SettlementQuestTier.getTierIndex("piratecaptain"), (var1) -> {
            var3.add(ShopItem.item("coinpouch", var1.getIntBetween(1200, 1500)));
         });
         conditionSection(var2x, var5.getQuestTiersCompleted() > SettlementQuestTier.getTierIndex("reaper"), (var1) -> {
            var3.add(ShopItem.item("hoverboard", var1.getIntBetween(1500, 1800)));
         });
         conditionSection(var2x, var5.getQuestTiersCompleted() > SettlementQuestTier.getTierIndex("cryoqueen"), (var1) -> {
            var3.add(ShopItem.item("bannerstand", var1.getIntBetween(250, 350)));
         });
         conditionSection(var2x, var5.getQuestTiersCompleted() > SettlementQuestTier.getTierIndex("pestwarden"), (var1) -> {
            var3.add(ShopItem.item("portalflask", var1.getIntBetween(1600, 2400)));
         });
         conditionSection(var2x, var5.getQuestTiersCompleted() > SettlementQuestTier.getTierIndex("sageandgrit"), (var1) -> {
            var3.add(ShopItem.item("blinkscepter", var1.getIntBetween(1700, 2400)));
         });
         conditionSection(var2x, var5.getQuestTiersCompleted() > SettlementQuestTier.getTierIndex("fallenwizard"), (var1) -> {
            var3.add(ShopItem.item("voidbag", var1.getIntBetween(1900, 2600)));
         });
      });
      conditionSection(var4, var4.getChance(4), (var3x) -> {
         var3.add(ShopItem.item("importedcow", var3x.getIntBetween(200, 300)));
         var3.add(ShopItem.item("importedsheep", var3x.getIntBetween(200, 300)));
         conditionSection(var4, var2.characterStats().mob_kills.getKills("piratecaptain") > 0, (var1) -> {
            var3.add(ShopItem.item("importedpig", var1.getIntBetween(500, 600)));
         });
      });
      TicketSystemList var6 = new TicketSystemList();
      var6.addObject(100, (var1x) -> {
         var3.add(ShopItem.item("jumpingball", var1x.getIntBetween(600, 800)));
      });
      var6.addObject(50, (var1x) -> {
         var3.add(ShopItem.item("weticicle", var1x.getIntBetween(350, 650)));
      });
      var6.addObject(100, (var1x) -> {
         var3.add(ShopItem.item("sunglasses", var1x.getIntBetween(300, 600)));
      });
      var6.addObject(100, (var1x) -> {
         var3.add(ShopItem.item("hulahat", var1x.getIntBetween(200, 400)));
         var3.add(ShopItem.item("hulaskirtwithtop", var1x.getIntBetween(200, 400)));
         var3.add(ShopItem.item("hulaskirt", var1x.getIntBetween(200, 400)));
      });
      var6.addObject(100, (var1x) -> {
         var3.add(ShopItem.item("swimsuit", var1x.getIntBetween(200, 400)));
         var3.add(ShopItem.item("swimtrunks", var1x.getIntBetween(200, 400)));
      });
      var6.addObject(100, (var1x) -> {
         var3.add(ShopItem.item("snowhood", var1x.getIntBetween(200, 400)));
         var3.add(ShopItem.item("snowcloak", var1x.getIntBetween(200, 400)));
         var3.add(ShopItem.item("snowboots", var1x.getIntBetween(200, 400)));
      });
      var6.addObject(100, (var1x) -> {
         var3.add(ShopItem.item("sailorhat", var1x.getIntBetween(200, 400)));
         var3.add(ShopItem.item("sailorshirt", var1x.getIntBetween(200, 400)));
         var3.add(ShopItem.item("sailorshoes", var1x.getIntBetween(200, 400)));
      });
      var6.addObject(50, (var1x) -> {
         var3.add(ShopItem.item("jesterhat", var1x.getIntBetween(200, 400)));
         var3.add(ShopItem.item("jestershirt", var1x.getIntBetween(200, 400)));
         var3.add(ShopItem.item("jesterboots", var1x.getIntBetween(200, 400)));
      });
      var6.addObject(50, (var1x) -> {
         var3.add(ShopItem.item("spacehelmet", var1x.getIntBetween(200, 400)));
         var3.add(ShopItem.item("spacesuit", var1x.getIntBetween(200, 400)));
         var3.add(ShopItem.item("spaceboots", var1x.getIntBetween(200, 400)));
      });
      GameRandom var7 = var4.nextSeeded();
      ((Consumer)var6.getRandomObject(var7)).accept(var7);
      conditionSection(var4, var4.getChance(4), (var3x) -> {
         var3.add(ShopItem.item("musicplayer", var3x.getIntBetween(800, 1200)));
         var3.add(ShopItem.item("portablemusicplayer", var3x.getIntBetween(800, 1200)));
         var3.add(ShopItem.item("adventurebeginsvinyl", var3x.getIntBetween(75, 125)));
         var3.add(ShopItem.item("homevinyl", var3x.getIntBetween(75, 125)));
         AbstractMusicList var4 = this.getLevel().biome.getLevelMusic(this.getLevel(), var2.playerMob);
         Iterator var5 = var4.getMusicInList().iterator();

         while(var5.hasNext()) {
            GameMusic var6 = (GameMusic)var5.next();
            String var7 = var6.getStringID() + "vinyl";
            if (ItemRegistry.itemExists(var7)) {
               var3.add(ShopItem.item(var7, var3x.getIntBetween(75, 125)));
            }
         }

      });
      var3.add(ShopItem.item("mapfragment", var4.getIntBetween(-40, -80)));
      return var3;
   }
}
