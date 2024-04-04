package necesse.gfx.drawOptions.itemAttack;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.drawOptions.ArrayDrawOptions;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.level.maps.light.GameLight;

public class ItemAttackDrawOptions {
   private LinkedList<AttackItemSprite> itemSprites = new LinkedList();
   private boolean itemBeforeHand = true;
   private GameSprite armSprite = null;
   private GameSprite armArmorSprite = null;
   private int armPosX;
   private int armPosY;
   private int armRotateX = 8;
   private int armRotateY = 15;
   private int armLength = 14;
   private int armCenterHeight = 4;
   private int itemYOffset = 12;
   private int centerX = 32;
   private int centerY = 23;
   private float armRotationOffset = 0.0F;
   private Color armorColor;
   public final int dir;
   private float rotation;
   private Color shade;
   private GameLight light;

   private ItemAttackDrawOptions(int var1) {
      this.armorColor = Color.WHITE;
      this.rotation = 0.0F;
      this.shade = Color.WHITE;
      this.light = new GameLight(150.0F);
      this.dir = var1;
   }

   public static ItemAttackDrawOptions start(int var0) {
      return new ItemAttackDrawOptions(var0);
   }

   public AttackItemSprite itemSprite(GameSprite var1) {
      AttackItemSprite var2 = new AttackItemSprite(var1);
      if (var1 != null) {
         this.itemSprites.add(var2);
      }

      return var2;
   }

   public AttackItemSprite itemSprite(GameTexture var1, int var2, int var3, int var4) {
      return this.itemSprite(new GameSprite(var1, var2, var3, var4));
   }

   public ItemAttackDrawOptions itemAfterHand() {
      this.itemBeforeHand = false;
      return this;
   }

   public ItemAttackDrawOptions forEachItemSprite(Consumer<AttackItemSprite> var1) {
      this.itemSprites.forEach(var1);
      return this;
   }

   public ItemAttackDrawOptions offsets(int var1, int var2, int var3, int var4, int var5) {
      this.centerX = var1;
      this.centerY = var2;
      this.armLength = var3;
      this.armCenterHeight = var4;
      this.itemYOffset = var5;
      return this;
   }

   public ItemAttackDrawOptions armSprite(GameSprite var1) {
      this.armSprite = var1;
      return this;
   }

   public ItemAttackDrawOptions armSprite(GameTexture var1, int var2, int var3, int var4) {
      return this.armSprite(new GameSprite(var1, var2, var3, var4));
   }

   public ItemAttackDrawOptions armRotatePoint(int var1, int var2) {
      this.armRotateX = var1;
      this.armRotateY = var2;
      return this;
   }

   public ItemAttackDrawOptions armorSpriteAndColor(InventoryItem var1, PlayerMob var2) {
      if (var1 != null && var1.item.isArmorItem() && var1.item instanceof ChestArmorItem) {
         this.armorSprite(((ChestArmorItem)var1.item).getAttackArmSprite(var1, var2 == null ? null : var2.getLevel(), var2));
         this.armorColor(var1.item.getDrawColor(var1, var2));
      }

      return this;
   }

   public ItemAttackDrawOptions armorSprite(GameSprite var1) {
      this.armArmorSprite = var1;
      return this;
   }

   public ItemAttackDrawOptions armorSprite(GameTexture var1, int var2, int var3, int var4) {
      return this.armorSprite(new GameSprite(var1, var2, var3, var4));
   }

   public ItemAttackDrawOptions armRotationOffset(float var1) {
      this.armRotationOffset = var1;
      return this;
   }

   public ItemAttackDrawOptions armPosOffset(int var1, int var2) {
      this.armPosX = var1;
      this.armPosY = var2;
      return this;
   }

   public ItemAttackDrawOptions addArmPosOffset(int var1, int var2) {
      this.armPosX += var1;
      this.armPosY += var2;
      return this;
   }

   public ItemAttackDrawOptions armorColor(Color var1) {
      this.armorColor = var1;
      return this;
   }

   public ItemAttackDrawOptions rotation(float var1) {
      this.rotation = var1;
      return this;
   }

   public ItemAttackDrawOptions pointRotation(float var1, float var2, float var3) {
      if (this.dir == 0) {
         return this.rotation(GameMath.getAngle(new Point2D.Float(var1, var2)) + var3 + 45.0F);
      } else if (this.dir == 1) {
         return this.rotation((float)(var1 == 0.0F ? (double)(var2 < 0.0F ? -90 : 90) : Math.toDegrees(Math.atan((double)(var2 / var1)))) + var3);
      } else if (this.dir == 2) {
         return this.rotation(GameMath.getAngle(new Point2D.Float(var1, var2)) + var3 + 45.0F + 180.0F);
      } else {
         return this.dir == 3 ? this.rotation((float)(var1 == 0.0F ? (double)(var2 < 0.0F ? -90 : 90) : -Math.toDegrees(Math.atan((double)(var2 / var1)))) + var3) : this;
      }
   }

   public ItemAttackDrawOptions pointRotation(float var1, float var2) {
      return this.pointRotation(var1, var2, 0.0F);
   }

   public ItemAttackDrawOptions swingRotation(float var1, float var2, float var3) {
      return this.rotation(getSwingRotation(var1, var2, var3) - 90.0F);
   }

   public static float getSwingRotation(float var0, float var1, float var2) {
      return var2 + var0 * var1;
   }

   public ItemAttackDrawOptions swingRotation(float var1) {
      return this.swingRotation(var1, 150.0F, 0.0F);
   }

   public ItemAttackDrawOptions swingRotationInv(float var1, float var2, float var3) {
      return this.rotation(getSwingRotationInv(var1, var2, var3) - 90.0F);
   }

   public static float getSwingRotationInv(float var0, float var1, float var2) {
      return var2 + Math.abs(var0 * var1 - var1);
   }

   public ItemAttackDrawOptions swingRotationInv(float var1) {
      return this.swingRotationInv(var1, 150.0F, 0.0F);
   }

   public ItemAttackDrawOptions shade(Color var1) {
      this.shade = var1;
      return this;
   }

   public ItemAttackDrawOptions light(GameLight var1) {
      this.light = var1;
      return this;
   }

   public DrawOptions posThrust(int var1, int var2, float var3, float var4, float var5) {
      int var6 = (int)((double)var3 * Math.sin((double)(var5 * 4.0F)) * 15.0) - (int)(var3 * 5.0F);
      int var7 = (int)((double)var4 * Math.sin((double)(var5 * 4.0F)) * 15.0) - (int)(var4 * 5.0F);
      Iterator var8 = this.itemSprites.iterator();

      while(var8.hasNext()) {
         AttackItemSprite var9 = (AttackItemSprite)var8.next();
         var9.rotationOffset = 45.0F;
      }

      return this.pos(var1 + var6, var2 + var7);
   }

   public DrawOptions pos(int var1, int var2) {
      ArrayList var3 = new ArrayList();
      TextureDrawOptionsEnd var4;
      TextureDrawOptionsEnd var5;
      Iterator var6;
      AttackItemSprite var7;
      if (this.dir == 0) {
         var4 = this.armSprite == null ? null : this.armSprite.initDraw().colorMult(this.shade).light(this.light).rotate(this.rotation + this.armRotationOffset - 45.0F, this.armRotateX - this.armPosX, this.armRotateY - this.armPosY).mirrorX().pos(var1 + 25 + 4 + this.armPosX, var2 + 23 + this.armPosY);
         var5 = this.armArmorSprite == null ? null : this.armArmorSprite.initDraw().color(this.armorColor).colorMult(this.shade).light(this.light).rotate(this.rotation + this.armRotationOffset - 45.0F, this.armRotateX - this.armPosX, this.armRotateY - this.armPosY).mirrorX().pos(var1 + 25 + 4 + this.armPosX, var2 + 23 + this.armPosY);
         if (!this.itemBeforeHand) {
            if (var4 != null) {
               var3.add(var4);
            }

            if (var5 != null) {
               var3.add(var5);
            }
         }

         var6 = this.itemSprites.iterator();

         while(var6.hasNext()) {
            var7 = (AttackItemSprite)var6.next();
            if (var7.rawCoords) {
               var3.add(var7.sprite.initDraw().color(var7.color).colorMult(this.shade).light(var7.getLight(this.light)).rotate(this.rotation + var7.rotationOffset - 45.0F, var7.rotateX, -var7.rotateY + var7.height).mirrorX().size(var7.width, var7.height).pos(var1 + 47 - 14 - var7.rotateX + 4, var2 + 35 + 5 + var7.rotateY - var7.height));
            } else {
               var3.add(var7.sprite.initDraw().color(var7.color).colorMult(this.shade).light(var7.getLight(this.light)).rotate(this.rotation + var7.rotationOffset - 45.0F, -14 + var7.rotateX, 3 - var7.rotateY + var7.height).mirrorX().size(var7.width, var7.height).pos(var1 + 47 - var7.rotateX + 4, var2 + 35 + var7.rotateY - var7.height));
            }
         }

         if (this.itemBeforeHand) {
            if (var4 != null) {
               var3.add(var4);
            }

            if (var5 != null) {
               var3.add(var5);
            }
         }
      } else if (this.dir == 1) {
         var4 = this.armSprite == null ? null : this.armSprite.initDraw().colorMult(this.shade).light(this.light).rotate(this.rotation + this.armRotationOffset, this.armRotateX - this.armPosX, this.armRotateY - this.armPosY).mirrorX().pos(var1 + this.centerX - this.armRotateX + this.armPosX, var2 + this.centerY + this.armPosY);
         var5 = this.armArmorSprite == null ? null : this.armArmorSprite.initDraw().color(this.armorColor).colorMult(this.shade).light(this.light).rotate(this.rotation + this.armRotationOffset, this.armRotateX - this.armPosX, this.armRotateY - this.armPosY).mirrorX().pos(var1 + this.centerX - this.armRotateX + this.armPosX, var2 + this.centerY + this.armPosY);
         if (!this.itemBeforeHand) {
            if (var4 != null) {
               var3.add(var4);
            }

            if (var5 != null) {
               var3.add(var5);
            }
         }

         var6 = this.itemSprites.iterator();

         while(var6.hasNext()) {
            var7 = (AttackItemSprite)var6.next();
            if (var7.rawCoords) {
               var3.add(var7.sprite.initDraw().color(var7.color).colorMult(this.shade).light(var7.getLight(this.light)).rotate(this.rotation + var7.rotationOffset, var7.rotateX, -var7.rotateY + var7.height).mirrorX().size(var7.width, var7.height).pos(var1 + this.centerX - var7.rotateX, var2 + this.centerY + this.itemYOffset + this.armCenterHeight + var7.rotateY - var7.height));
            } else {
               var3.add(var7.sprite.initDraw().color(var7.color).colorMult(this.shade).light(var7.getLight(this.light)).rotate(this.rotation + var7.rotationOffset, -this.armLength + var7.rotateX, this.armCenterHeight - var7.rotateY + var7.height).mirrorX().size(var7.width, var7.height).pos(var1 + this.centerX + this.armLength - var7.rotateX, var2 + this.centerY + this.itemYOffset + var7.rotateY - var7.height));
            }
         }

         if (this.itemBeforeHand) {
            if (var4 != null) {
               var3.add(var4);
            }

            if (var5 != null) {
               var3.add(var5);
            }
         }
      } else if (this.dir == 2) {
         var4 = this.armSprite == null ? null : this.armSprite.initDraw().colorMult(this.shade).light(this.light).rotate(this.rotation + this.armRotationOffset + 180.0F - 60.0F, this.armRotateX - this.armPosX, this.armRotateY + 1 + this.armPosY).mirrorX().pos(var1 + 25 + 4 - 12 + this.armPosX, var2 + 22 - this.armPosY);
         var5 = this.armArmorSprite == null ? null : this.armArmorSprite.initDraw().color(this.armorColor).colorMult(this.shade).light(this.light).rotate(this.rotation + this.armRotationOffset + 180.0F - 60.0F, this.armRotateX + this.armPosX, this.armRotateY + 1 + this.armPosY).mirrorX().pos(var1 + 25 + 4 - 12 + this.armPosX, var2 + 22 - this.armPosY);
         if (!this.itemBeforeHand) {
            if (var4 != null) {
               var3.add(var4);
            }

            if (var5 != null) {
               var3.add(var5);
            }
         }

         var6 = this.itemSprites.iterator();

         while(var6.hasNext()) {
            var7 = (AttackItemSprite)var6.next();
            if (var7.rawCoords) {
               var3.add(var7.sprite.initDraw().color(var7.color).colorMult(this.shade).light(var7.getLight(this.light)).rotate(this.rotation + var7.rotationOffset + 180.0F - 60.0F, var7.rotateX, -var7.rotateY + var7.height + 1).mirrorX().size(var7.width, var7.height).pos(var1 + 47 - 14 - var7.rotateX + 4 - 12, var2 + 35 + 5 + var7.rotateY - var7.height - 2));
            } else {
               var3.add(var7.sprite.initDraw().color(var7.color).colorMult(this.shade).light(var7.getLight(this.light)).rotate(this.rotation + var7.rotationOffset + 180.0F - 60.0F, -14 + var7.rotateX + 2, 3 - var7.rotateY + var7.height + 1).addRotation(15.0F, 0, var7.height).mirrorX().size(var7.width, var7.height).pos(var1 + 47 - var7.rotateX + 4 - 12, var2 + 35 + var7.rotateY - var7.height));
            }
         }

         if (this.itemBeforeHand) {
            if (var4 != null) {
               var3.add(var4);
            }

            if (var5 != null) {
               var3.add(var5);
            }
         }
      } else if (this.dir == 3) {
         var4 = this.armSprite == null ? null : this.armSprite.initDraw().colorMult(this.shade).light(this.light).rotate(-this.rotation - this.armRotationOffset, -this.armRotateX + this.armSprite.spriteWidth + this.armPosX, this.armRotateY - this.armPosY).pos(var1 + this.centerX - this.armSprite.width + this.armRotateX - this.armPosX, var2 + this.centerY + this.armPosY);
         var5 = this.armArmorSprite == null ? null : this.armArmorSprite.initDraw().color(this.armorColor).colorMult(this.shade).light(this.light).rotate(-this.rotation - this.armRotationOffset, -this.armRotateX + this.armArmorSprite.spriteHeight + this.armPosX, this.armRotateY - this.armPosY).pos(var1 + this.centerX - (this.armSprite == null ? 0 : this.armSprite.width) + this.armRotateX - this.armPosX, var2 + this.centerY + this.armPosY);
         if (!this.itemBeforeHand) {
            if (var4 != null) {
               var3.add(var4);
            }

            if (var5 != null) {
               var3.add(var5);
            }
         }

         var6 = this.itemSprites.iterator();

         while(var6.hasNext()) {
            var7 = (AttackItemSprite)var6.next();
            if (var7.rawCoords) {
               var3.add(var7.sprite.initDraw().color(var7.color).colorMult(this.shade).light(var7.getLight(this.light)).rotate(-this.rotation - var7.rotationOffset, -var7.rotateX + var7.width, -var7.rotateY + var7.height).size(var7.width, var7.height).pos(var1 + this.centerX + var7.rotateX - var7.width, var2 + this.centerY + this.itemYOffset + this.armCenterHeight + var7.rotateY - var7.height));
            } else {
               var3.add(var7.sprite.initDraw().color(var7.color).colorMult(this.shade).light(var7.getLight(this.light)).rotate(-this.rotation - var7.rotationOffset, this.armLength - var7.rotateX + var7.width, this.armCenterHeight - var7.rotateY + var7.height).size(var7.width, var7.height).pos(var1 + this.centerX - this.armLength + var7.rotateX - var7.width, var2 + this.centerY + this.itemYOffset + var7.rotateY - var7.height));
            }
         }

         if (this.itemBeforeHand) {
            if (var4 != null) {
               var3.add(var4);
            }

            if (var5 != null) {
               var3.add(var5);
            }
         }
      }

      return new ArrayDrawOptions(var3);
   }

   public void draw(int var1, int var2) {
      this.pos(var1, var2).draw();
   }

   public class AttackItemSprite {
      private GameSprite sprite;
      private int width;
      private int height;
      private int rotateX;
      private int rotateY;
      private boolean rawCoords;
      private float rotationOffset;
      private Color color;
      private int minDrawLightLevel;

      public AttackItemSprite(GameSprite var2) {
         this.sprite = var2;
         if (var2 != null) {
            this.width = var2.width;
            this.height = var2.height;
         }

         this.rotateX = 0;
         this.rotateY = 0;
         this.rotationOffset = 0.0F;
         this.color = new Color(1.0F, 1.0F, 1.0F);
      }

      public AttackItemSprite itemSize(int var1, int var2) {
         this.width = var1;
         this.height = var2;
         return this;
      }

      public AttackItemSprite itemRotatePoint(int var1, int var2) {
         this.rotateX = var1;
         this.rotateY = var2;
         return this;
      }

      public AttackItemSprite itemRawCoords() {
         this.rawCoords = true;
         return this;
      }

      public AttackItemSprite itemRotateOffset(float var1) {
         this.rotationOffset = var1;
         return this;
      }

      public AttackItemSprite itemRotateOffsetAdd(float var1) {
         this.rotationOffset += var1;
         return this;
      }

      public AttackItemSprite itemColor(Color var1) {
         this.color = var1;
         return this;
      }

      public AttackItemSprite itemMinDrawLight(int var1) {
         this.minDrawLightLevel = var1;
         return this;
      }

      public ItemAttackDrawOptions itemEnd() {
         return ItemAttackDrawOptions.this;
      }

      private GameLight getLight(GameLight var1) {
         return this.minDrawLightLevel > 0 ? var1.minLevelCopy((float)this.minDrawLightLevel) : var1;
      }
   }
}
