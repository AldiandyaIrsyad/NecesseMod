package necesse.entity.mobs.friendly.critters.caveling;

import java.awt.Color;
import java.awt.Point;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.explosionEvent.FlamelingsModifierSmokePuffLevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.HumanTexture;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.leaves.EmptyAINode;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.EvilsProtectorAttack1Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.light.GameLight;

public class Flameling extends CavelingMob {
   public int frameCounter = 2;
   public float frameSpeed;
   float itemYHover = 0.0F;
   boolean itemHoveringDown = false;
   float startTime;
   int fireBallCounter;
   float shootInterval = 222.0F;

   public Flameling() {
      super(200, 40);
      this.isStatic = true;
      this.setTeam(-2);
   }

   public void init() {
      super.init();
      this.texture = MobRegistry.Textures.flameling;
      this.popParticleColor = new Color(227, 122, 30);
      this.ai = new BehaviourTreeAI(this, new EmptyAINode());
      this.isRock = false;
      if (this.item == null) {
         this.item = new InventoryItem("flamelingorb", 1);
      }

      this.startTime = (float)this.getLevel().getTime();
      if (this.isClient()) {
         FlamelingsModifierSmokePuffLevelEvent var1 = new FlamelingsModifierSmokePuffLevelEvent((float)this.getX(), (float)this.getY(), 20, new GameDamage(0.0F), false, 0, this);
         this.getLevel().entityManager.addLevelEventHidden(var1);
      }

   }

   public LootTable getLootTable() {
      return super.getLootTable();
   }

   public boolean canBeHit(Attacker var1) {
      return false;
   }

   public void tickMovement(float var1) {
      super.tickMovement(var1);
      this.frameSpeed += var1;

      while(this.frameSpeed > 200.0F) {
         if (this.frameCounter == 4) {
            this.frameCounter = 1;
         } else {
            ++this.frameCounter;
         }

         this.frameSpeed = 0.0F;
         if (this.isClient()) {
            this.getLevel().entityManager.addParticle(this.x + (float)(GameRandom.globalRandom.nextGaussian() * 4.0), this.y - 4.0F, (new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL})).next()).sprite(GameResources.particles.sprite(0, 0, 8)).sizeFades(15, 10).rotates().movesConstant(0.0F, 15.0F).flameColor(30.0F).fadesAlphaTime(100, 500).height(50.0F).givesLight(75.0F, 0.5F).lifeTime(800);
         }
      }

      if (this.itemYHover < 5.0F & !this.itemHoveringDown) {
         this.itemYHover += var1 / 100.0F;
         if (this.itemYHover >= 5.0F) {
            this.itemHoveringDown = true;
         }
      } else if (this.itemYHover > -5.0F & this.itemHoveringDown) {
         this.itemYHover -= var1 / 100.0F;
         if (this.itemYHover <= -5.0F) {
            this.itemHoveringDown = false;
         }
      }

   }

   public void serverTick() {
      super.serverTick();
      if (this.readyToShoot()) {
         this.spawnFireBall((float)(this.fireBallCounter * 20));
         ++this.fireBallCounter;
         if (this.fireBallCounter > 18) {
            this.remove();
         }
      }

   }

   public boolean readyToShoot() {
      return (float)this.getLevel().getTime() >= this.startTime + this.shootInterval * (float)this.fireBallCounter;
   }

   public void spawnFireBall(float var1) {
      GameDamage var2 = new GameDamage(100.0F);
      EvilsProtectorAttack1Projectile var3 = new EvilsProtectorAttack1Projectile(this.x, this.y - 30.0F + this.itemYHover, var1, 70.0F, 800, var2, this);
      var3.setLevel(this.getLevel());
      var3.moveDist(60.0);
      this.getLevel().entityManager.projectiles.add(var3);
      Screen.playSound(GameResources.magicbolt2, SoundEffect.effect(this).pitch(GameRandom.globalRandom.getFloatBetween(0.8F, 1.2F)));
   }

   public float getOutgoingDamageModifier() {
      float var1 = super.getOutgoingDamageModifier();
      if (this.getLevel() != null) {
         var1 *= (Float)this.getLevel().buffManager.getModifier(LevelModifiers.ENEMY_DAMAGE);
      }

      return var1;
   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 32;
      int var12 = var8.getDrawY(var6) - 48;
      HumanTexture var13 = this.texture != null ? this.texture : MobRegistry.Textures.stoneCaveling;
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      Point var14 = new Point(0, 2);
      var14.x = this.frameCounter;
      var12 += this.getBobbing(var5, var6);
      final TextureDrawOptionsEnd var15 = var13.rightArms.initDraw().sprite(var14.x, var14.y, 64).pos(var11, var12);
      final TextureDrawOptionsEnd var16 = var13.body.initDraw().sprite(var14.x, var14.y, 64).pos(var11, var12);
      final TextureDrawOptionsEnd var17;
      if (this.item != null) {
         var17 = this.item.item.getItemSprite(this.item, var9).initDraw().light(var10.minLevelCopy(150.0F)).mirror(var14.y < 2, false).size(32).posMiddle(var11 + 32 - 2, var12 + (int)this.itemYHover);
      } else {
         var17 = null;
      }

      final TextureDrawOptionsEnd var18 = var13.leftArms.initDraw().sprite(var14.x, var14.y, 64).pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var15.draw();
            var16.draw();
            if (var17 != null) {
               var17.draw();
            }

            var18.draw();
         }
      });
      TextureDrawOptionsEnd var19 = MobRegistry.Textures.caveling_shadow.initDraw().sprite(var14.x, var14.y, 64).light(var10).pos(var11, var12);
      var2.add((var1x) -> {
         var19.draw();
      });
   }

   public void spawnRemoveParticles(float var1, float var2) {
      super.spawnRemoveParticles(var1, var2);
      FlamelingsModifierSmokePuffLevelEvent var3 = new FlamelingsModifierSmokePuffLevelEvent((float)this.getX(), (float)this.getY(), 20, new GameDamage(0.0F), false, 0, this);
      this.getLevel().entityManager.addLevelEventHidden(var3);
   }
}
