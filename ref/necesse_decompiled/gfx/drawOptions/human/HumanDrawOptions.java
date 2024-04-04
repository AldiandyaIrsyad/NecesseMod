package necesse.gfx.drawOptions.human;

import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import necesse.engine.Screen;
import necesse.entity.mobs.HumanTexture;
import necesse.entity.mobs.HumanTextureFull;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.GameSkin;
import necesse.gfx.HumanLook;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.shader.ShaderState;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.item.armorItem.HelmetArmorItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import org.lwjgl.opengl.GL11;

public class HumanDrawOptions {
   private Level level;
   private HumanLook look;
   private GameTexture headTexture;
   private GameTexture bodyTexture;
   private GameTexture leftArmsTexture;
   private GameTexture rightArmsTexture;
   private GameTexture feetTexture;
   private GameTexture hairTexture;
   private GameTexture backHairTexture;
   private List<HumanDrawOptionsGetter> behindOptions;
   private List<HumanDrawOptionsGetter> topOptions;
   private List<HumanDrawOptionsGetter> onBodyOptions;
   private boolean mirrorX;
   private boolean mirrorY;
   private ArmorItem.HairDrawMode hairMode;
   private HumanDrawOptionsGetter hatTexture;
   private int hatXOffset;
   private int hatYOffset;
   private InventoryItem helmet;
   private InventoryItem chestplate;
   private InventoryItem boots;
   private GameLight light;
   private float alpha;
   private float allAlpha;
   private float rotation;
   private int rotationX;
   private int rotationY;
   private int drawOffsetX;
   private int drawOffsetY;
   private int width;
   private int height;
   private PlayerMob player;
   private ItemAttackDrawOptions attackDrawOptions;
   private InventoryItem attackItem;
   private float attackProgress;
   private float attackDirX;
   private float attackDirY;
   private int spriteX;
   private int spriteY;
   private int spriteRes;
   private int armSpriteX;
   private int dir;
   private boolean invis;
   private GameTexture mask;
   private int maskOffsetX;
   private int maskOffsetY;
   private InventoryItem holdItem;

   public HumanDrawOptions(Level var1) {
      this.light = new GameLight(150.0F);
      this.alpha = 1.0F;
      this.allAlpha = 1.0F;
      this.width = 64;
      this.height = 64;
      this.spriteRes = 64;
      this.level = var1;
      this.behindOptions = new LinkedList();
      this.topOptions = new LinkedList();
      this.onBodyOptions = new LinkedList();
   }

   public HumanDrawOptions(Level var1, HumanLook var2, boolean var3) {
      this(var1);
      this.look = var2;
      GameSkin var4 = var2.getGameSkin(var3);
      this.headTexture = var4.getHeadTexture();
      this.bodyTexture = var4.getBodyTexture();
      this.leftArmsTexture = var4.getLeftArmsTexture();
      this.rightArmsTexture = var4.getRightArmsTexture();
      this.feetTexture = var4.getFeetTexture();
      this.hairTexture = var2.getHairTexture();
      this.backHairTexture = var2.getBackHairTexture();
   }

   public HumanDrawOptions(Level var1, HumanTexture var2) {
      this(var1);
      this.bodyTexture = var2.body;
      this.leftArmsTexture = var2.leftArms;
      this.rightArmsTexture = var2.rightArms;
   }

   public HumanDrawOptions(Level var1, HumanTextureFull var2) {
      this(var1);
      this.headTexture = var2.head;
      this.bodyTexture = var2.body;
      this.leftArmsTexture = var2.leftArms;
      this.rightArmsTexture = var2.rightArms;
      this.feetTexture = var2.feet;
      this.hairTexture = var2.hair;
      this.backHairTexture = var2.backHair;
   }

   public HumanDrawOptions headTexture(GameTexture var1) {
      this.headTexture = var1;
      return this;
   }

   public HumanDrawOptions bodyTexture(GameTexture var1) {
      this.bodyTexture = var1;
      return this;
   }

   public HumanDrawOptions leftArmsTexture(GameTexture var1) {
      this.leftArmsTexture = var1;
      return this;
   }

   public HumanDrawOptions rightArmsTexture(GameTexture var1) {
      this.rightArmsTexture = var1;
      return this;
   }

   public HumanDrawOptions feetTexture(GameTexture var1) {
      this.feetTexture = var1;
      return this;
   }

   public HumanDrawOptions hairTexture(GameTexture var1) {
      this.hairTexture = var1;
      return this;
   }

   public HumanDrawOptions backHairTexture(GameTexture var1) {
      this.backHairTexture = var1;
      return this;
   }

   public HumanDrawOptions hatTexture(HumanDrawOptionsGetter var1, ArmorItem.HairDrawMode var2, int var3, int var4) {
      this.hatTexture = var1;
      this.hairMode = var2;
      this.hatXOffset = var3;
      this.hatYOffset = var4;
      return this;
   }

   public HumanDrawOptions hatTexture(HumanDrawOptionsGetter var1, ArmorItem.HairDrawMode var2) {
      return this.hatTexture((HumanDrawOptionsGetter)var1, var2, 0, 0);
   }

   public HumanDrawOptions hatTexture(GameTexture var1, ArmorItem.HairDrawMode var2, int var3, int var4) {
      return this.hatTexture((var1x, var2x, var3x, var4x, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14) -> {
         return var1.initDraw().sprite(var3x, var4x, var5).light(var12).alpha(var13).size(var8, var9).mirror(var10, var11).addShaderTextureFit(var14, 1).pos(var6, var7);
      }, var2, var3, var4);
   }

   public HumanDrawOptions hatTexture(GameTexture var1, ArmorItem.HairDrawMode var2) {
      return this.hatTexture((GameTexture)var1, var2, 0, 0);
   }

   public HumanDrawOptions helmet(InventoryItem var1) {
      this.helmet = var1;
      return this;
   }

   public HumanDrawOptions chestplate(InventoryItem var1) {
      this.chestplate = var1;
      return this;
   }

   public HumanDrawOptions boots(InventoryItem var1) {
      this.boots = var1;
      return this;
   }

   public HumanDrawOptions addBehindDraw(HumanDrawOptionsGetter var1) {
      this.behindOptions.add(var1);
      return this;
   }

   public HumanDrawOptions addTopDraw(HumanDrawOptionsGetter var1) {
      this.topOptions.add(var1);
      return this;
   }

   public HumanDrawOptions addOnBodyDraw(HumanDrawOptionsGetter var1) {
      this.onBodyOptions.add(var1);
      return this;
   }

   public HumanDrawOptions mirrorX(boolean var1) {
      this.mirrorX = var1;
      return this;
   }

   public HumanDrawOptions mirrorY(boolean var1) {
      this.mirrorY = var1;
      return this;
   }

   public HumanDrawOptions alpha(float var1) {
      this.alpha = var1;
      return this;
   }

   public HumanDrawOptions allAlpha(float var1) {
      this.allAlpha = var1;
      return this;
   }

   public HumanDrawOptions rotate(float var1, int var2, int var3) {
      this.rotation = var1;
      this.rotationX = var2;
      this.rotationY = var3;
      return this;
   }

   public HumanDrawOptions light(GameLight var1) {
      this.light = var1;
      return this;
   }

   public HumanDrawOptions addDrawOffset(int var1, int var2) {
      this.drawOffsetX = var1;
      this.drawOffsetY = var2;
      return this;
   }

   public HumanDrawOptions drawOffset(int var1, int var2) {
      this.drawOffsetX += var1;
      this.drawOffsetY += var2;
      return this;
   }

   public HumanDrawOptions size(int var1, int var2) {
      this.width = var1;
      this.height = var2;
      return this;
   }

   public HumanDrawOptions player(PlayerMob var1) {
      this.player = var1;
      return this;
   }

   public HumanDrawOptions itemAttack(InventoryItem var1, PlayerMob var2, float var3, float var4, float var5) {
      this.attackDrawOptions = null;
      this.attackItem = var1;
      this.player = var2;
      this.attackProgress = var3;
      this.attackDirX = var4;
      this.attackDirY = var5;
      return this;
   }

   public HumanDrawOptions attackAnim(ItemAttackDrawOptions var1, float var2) {
      this.attackItem = null;
      this.attackDrawOptions = var1;
      this.attackProgress = var2;
      return this;
   }

   public HumanDrawOptions mask(GameTexture var1, int var2, int var3) {
      this.mask = var1;
      this.maskOffsetX = var2;
      this.maskOffsetY = var3;
      return this;
   }

   public HumanDrawOptions mask(GameTexture var1) {
      return this.mask(var1, 0, 0);
   }

   public HumanDrawOptions invis(boolean var1) {
      this.invis = var1;
      return this;
   }

   public HumanDrawOptions sprite(int var1, int var2, int var3) {
      this.spriteX = var1;
      this.spriteY = var2;
      this.armSpriteX = var1;
      this.spriteRes = var3;
      return this;
   }

   public HumanDrawOptions sprite(int var1, int var2) {
      return this.sprite(var1, var2, this.spriteRes);
   }

   public HumanDrawOptions armSprite(int var1) {
      this.armSpriteX = var1;
      return this;
   }

   public HumanDrawOptions holdItem(InventoryItem var1) {
      this.armSprite(this.dir != 1 && this.dir != 3 ? 4 : 2);
      this.holdItem = var1;
      return this;
   }

   public HumanDrawOptions sprite(Point var1, int var2) {
      return this.sprite(var1.x, var1.y, var2);
   }

   public HumanDrawOptions sprite(Point var1) {
      return this.sprite(var1.x, var1.y);
   }

   public HumanDrawOptions dir(int var1) {
      this.dir = var1;
      return this;
   }

   public boolean isAttacking() {
      return this.attackDrawOptions != null || this.attackItem != null;
   }

   public float getAttackProgress() {
      return this.attackProgress;
   }

   public DrawOptions pos(int var1, int var2) {
      var1 += this.drawOffsetX;
      var2 += this.drawOffsetY;
      DrawOptionsList var3 = new DrawOptionsList();
      DrawOptionsList var4 = new DrawOptionsList();
      DrawOptionsList var5 = new DrawOptionsList();
      DrawOptionsList var6 = new DrawOptionsList();
      if (this.hatTexture != null) {
         if (this.headTexture != null && !this.invis) {
            var4.add(this.headTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addShaderTextureFit(this.mask, 1).pos(var1, var2));
         }

         if (this.look != null && !this.invis) {
            var4.add(this.look.getEyesDrawOptions(var1, var2, this.spriteX, this.spriteY, this.width, this.height, this.mirrorX, this.mirrorY, this.alpha, this.light, this.mask));
         }

         switch (this.hairMode) {
            case NO_HAIR:
               var5.add(this.hatTexture.getDrawOptions(this.player, this.dir, this.spriteX, this.spriteY, this.spriteRes, var1 + this.hatXOffset, var2 + this.hatYOffset, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
               break;
            case UNDER_HAIR:
               var5.add(this.hatTexture.getDrawOptions(this.player, this.dir, this.spriteX, this.spriteY, this.spriteRes, var1 + this.hatXOffset, var2 + this.hatYOffset, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
               if (this.hairTexture != null && !this.invis) {
                  var5.add(this.hairTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addShaderTextureFit(this.mask, 1).pos(var1, var2));
               }

               if (this.backHairTexture != null && !this.invis) {
                  var3.add(this.backHairTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addShaderTextureFit(this.mask, 1).pos(var1, var2));
               }
               break;
            case OVER_HAIR:
               if (this.hairTexture != null && !this.invis) {
                  var5.add(this.hairTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addShaderTextureFit(this.mask, 1).pos(var1, var2));
               }

               if (this.backHairTexture != null && !this.invis) {
                  var3.add(this.backHairTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addShaderTextureFit(this.mask, 1).pos(var1, var2));
               }

               var5.add(this.hatTexture.getDrawOptions(this.player, this.dir, this.spriteX, this.spriteY, this.spriteRes, var1 + this.hatXOffset, var2 + this.hatYOffset, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
         }
      } else if (this.helmet != null && this.helmet.item.isArmorItem()) {
         ((ArmorItem)this.helmet.item).addExtraDrawOptions(this, this.helmet);
         if (((ArmorItem)this.helmet.item).drawBodyPart(this.helmet, this.player)) {
            if (this.headTexture != null && !this.invis) {
               var4.add(this.headTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addShaderTextureFit(this.mask, 1).pos(var1, var2));
            }

            if (this.look != null && !this.invis) {
               var4.add(this.look.getEyesDrawOptions(var1, var2, this.spriteX, this.spriteY, this.width, this.height, this.mirrorX, this.mirrorY, this.alpha, this.light, this.mask));
            }
         }

         ArmorItem.HairDrawMode var7 = ((ArmorItem)this.helmet.item).hairDrawOptions;
         switch (var7) {
            case NO_HAIR:
               var5.add(((ArmorItem)this.helmet.item).getArmorDrawOptions(this.helmet, this.level, this.player, this.spriteX, this.spriteY, this.spriteRes, var1, var2, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
               break;
            case UNDER_HAIR:
               var5.add(((ArmorItem)this.helmet.item).getArmorDrawOptions(this.helmet, this.level, this.player, this.spriteX, this.spriteY, this.spriteRes, var1, var2, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
               if (this.hairTexture != null && !this.invis) {
                  var5.add(this.hairTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addShaderTextureFit(this.mask, 1).pos(var1, var2));
               }

               if (this.backHairTexture != null && !this.invis) {
                  var3.add(this.backHairTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addShaderTextureFit(this.mask, 1).pos(var1, var2));
               }
               break;
            case OVER_HAIR:
               if (this.hairTexture != null && !this.invis) {
                  var5.add(this.hairTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addShaderTextureFit(this.mask, 1).pos(var1, var2));
               }

               if (this.backHairTexture != null && !this.invis) {
                  var3.add(this.backHairTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addShaderTextureFit(this.mask, 1).pos(var1, var2));
               }

               var5.add(((ArmorItem)this.helmet.item).getArmorDrawOptions(this.helmet, this.level, this.player, this.spriteX, this.spriteY, this.spriteRes, var1, var2, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
         }

         if (this.helmet.item instanceof HelmetArmorItem) {
            var6.add(((HelmetArmorItem)this.helmet.item).getHeadArmorBackDrawOptions(this.helmet, this.player, this.spriteX, this.spriteY, var1, var2, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
         }
      } else {
         if (this.backHairTexture != null && !this.invis) {
            var3.add(this.backHairTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addShaderTextureFit(this.mask, 1).pos(var1, var2));
         }

         if (this.headTexture != null && !this.invis) {
            var4.add(this.headTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addShaderTextureFit(this.mask, 1).pos(var1, var2));
         }

         if (this.look != null && !this.invis) {
            var4.add(this.look.getEyesDrawOptions(var1, var2, this.spriteX, this.spriteY, this.width, this.height, this.mirrorX, this.mirrorY, this.alpha, this.light, this.mask));
         }

         if (this.hairTexture != null && !this.invis) {
            var5.add(this.hairTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addShaderTextureFit(this.mask, 1).pos(var1, var2));
         }
      }

      DrawOptionsList var21 = new DrawOptionsList();
      DrawOptionsList var8 = new DrawOptionsList();
      DrawOptionsList var9 = new DrawOptionsList();
      DrawOptionsList var10 = new DrawOptionsList();
      DrawOptionsList var11 = new DrawOptionsList();
      boolean var12 = false;
      boolean var13 = true;
      boolean var14 = true;
      DrawOptions var15;
      if (this.attackDrawOptions != null) {
         if (this.chestplate != null && this.chestplate.item.isArmorItem()) {
            ((ArmorItem)this.chestplate.item).addExtraDrawOptions(this, this.chestplate);
            if (((ArmorItem)this.chestplate.item).drawBodyPart(this.chestplate, this.player) && this.bodyTexture != null && !this.invis) {
               var21.add(this.bodyTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addShaderTextureFit(this.mask, 1).pos(var1, var2));
            }

            var8.add(((ArmorItem)this.chestplate.item).getArmorDrawOptions(this.chestplate, this.level, this.player, this.spriteX, this.spriteY, this.spriteRes, var1, var2, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
         } else if (this.bodyTexture != null && !this.invis) {
            var21.add(this.bodyTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addShaderTextureFit(this.mask, 1).pos(var1, var2));
         }

         var15 = this.attackDrawOptions.light(this.light).pos(var1, var2);
         if (this.dir != 3) {
            var10.add(var15);
            var14 = false;
         } else {
            var9.add(var15);
            var13 = false;
         }
      } else if (this.attackItem != null) {
         if (this.chestplate != null && this.chestplate.item.isArmorItem()) {
            ((ArmorItem)this.chestplate.item).addExtraDrawOptions(this, this.chestplate);
            if (((ArmorItem)this.chestplate.item).drawBodyPart(this.chestplate, this.player) && this.bodyTexture != null && !this.invis) {
               var21.add(this.bodyTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addShaderTextureFit(this.mask, 1).pos(var1, var2));
            }

            var8.add(((ArmorItem)this.chestplate.item).getArmorDrawOptions(this.chestplate, this.level, this.player, this.spriteX, this.spriteY, this.spriteRes, var1, var2, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
         } else if (this.bodyTexture != null && !this.invis) {
            var21.add(this.bodyTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addShaderTextureFit(this.mask, 1).pos(var1, var2));
         }

         var15 = this.attackItem.getAttackDrawOptions(this.level, this.player, this.dir, this.attackDirX, this.attackDirY, !this.invis && this.bodyTexture != null ? new GameSprite(this.bodyTexture, 0, 8, this.spriteRes / 2) : null, this.chestplate, this.attackProgress, var1, var2, this.light);
         if (this.dir != 3) {
            var10.add(var15);
            var14 = false;
         } else {
            var9.add(var15);
            var13 = false;
         }
      }

      if (var13 || var14) {
         boolean var22 = this.isSpriteXOffset(this.armSpriteX);
         boolean var16 = this.isSpriteXOffset(this.spriteX);
         int var17 = 0;
         if (var22 != var16) {
            var17 = var22 ? -2 : 2;
         }

         if (this.chestplate != null && this.chestplate.item.isArmorItem()) {
            ((ArmorItem)this.chestplate.item).addExtraDrawOptions(this, this.chestplate);
            if (((ArmorItem)this.chestplate.item).drawBodyPart(this.chestplate, this.player)) {
               if (this.bodyTexture != null && !this.invis) {
                  var21.add(this.bodyTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addShaderTextureFit(this.mask, 1).pos(var1, var2));
               }

               if (var13 && this.leftArmsTexture != null && !this.invis) {
                  var9.add(this.leftArmsTexture.initDraw().sprite(this.armSpriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addShaderTextureFit(this.mask, 1).pos(var1, var2 + var17));
               }

               if (var14 && this.rightArmsTexture != null && !this.invis) {
                  var10.add(this.rightArmsTexture.initDraw().sprite(this.armSpriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addShaderTextureFit(this.mask, 1).pos(var1, var2 + var17));
               }
            }

            var8.add(((ArmorItem)this.chestplate.item).getArmorDrawOptions(this.chestplate, this.level, this.player, this.spriteX, this.spriteY, this.spriteRes, var1, var2, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
            if (this.chestplate.item instanceof ChestArmorItem) {
               if (var13) {
                  var9.add(((ChestArmorItem)this.chestplate.item).getArmorLeftArmsDrawOptions(this.chestplate, this.level, this.player, this.armSpriteX, this.spriteY, this.spriteRes, var1, var2 + var17, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
               }

               if (var14) {
                  var10.add(((ChestArmorItem)this.chestplate.item).getArmorRightArmsDrawOptions(this.chestplate, this.level, this.player, this.armSpriteX, this.spriteY, this.spriteRes, var1, var2 + var17, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
               }
            }
         } else {
            if (this.bodyTexture != null && !this.invis) {
               var21.add(this.bodyTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addShaderTextureFit(this.mask, 1).pos(var1, var2));
            }

            if (var13 && this.leftArmsTexture != null && !this.invis) {
               var9.add(this.leftArmsTexture.initDraw().sprite(this.armSpriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addShaderTextureFit(this.mask, 1).pos(var1, var2 + var17));
            }

            if (var14 && this.rightArmsTexture != null && !this.invis) {
               var10.add(this.rightArmsTexture.initDraw().sprite(this.armSpriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addShaderTextureFit(this.mask, 1).pos(var1, var2 + var17));
            }
         }

         if (this.holdItem != null && var13 && var14) {
            var11.add(this.holdItem.item.getHoldItemDrawOptions(this.holdItem, this.player, this.spriteX, this.spriteY, var1, var2, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
            var12 = this.holdItem.item.holdItemInFrontOfArms(this.holdItem, this.player, this.spriteX, this.spriteY, var1, var2, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask);
         }
      }

      DrawOptionsList var23 = new DrawOptionsList();
      DrawOptionsList var24 = new DrawOptionsList();
      if (this.boots != null && this.boots.item.isArmorItem()) {
         ((ArmorItem)this.boots.item).addExtraDrawOptions(this, this.boots);
         if (((ArmorItem)this.boots.item).drawBodyPart(this.boots, this.player) && this.feetTexture != null && !this.invis) {
            var23.add(this.feetTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addShaderTextureFit(this.mask, 1).pos(var1, var2));
         }

         var24.add(((ArmorItem)this.boots.item).getArmorDrawOptions(this.boots, this.level, this.player, this.spriteX, this.spriteY, this.spriteRes, var1, var2, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
      } else if (this.feetTexture != null && !this.invis) {
         var23.add(this.feetTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addShaderTextureFit(this.mask, 1).pos(var1, var2));
      }

      Iterator var28 = this.behindOptions.iterator();

      while(var28.hasNext()) {
         HumanDrawOptionsGetter var18 = (HumanDrawOptionsGetter)var28.next();
         var3.add(var18.getDrawOptions(this.player, this.dir, this.spriteX, this.spriteY, this.spriteRes, var1, var2, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
      }

      DrawOptionsList var30 = new DrawOptionsList();
      Iterator var25 = this.onBodyOptions.iterator();

      while(var25.hasNext()) {
         HumanDrawOptionsGetter var19 = (HumanDrawOptionsGetter)var25.next();
         var30.add(var19.getDrawOptions(this.player, this.dir, this.spriteX, this.spriteY, this.spriteRes, var1, var2, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
      }

      DrawOptionsList var26 = new DrawOptionsList();
      Iterator var27 = this.topOptions.iterator();

      while(var27.hasNext()) {
         HumanDrawOptionsGetter var20 = (HumanDrawOptionsGetter)var27.next();
         var26.add(var20.getDrawOptions(this.player, this.dir, this.spriteX, this.spriteY, this.spriteRes, var1, var2, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
      }

      ShaderState var29 = this.mask == null ? null : new ShaderState() {
         public void use() {
            GameResources.edgeMaskShader.use(HumanDrawOptions.this.maskOffsetX, HumanDrawOptions.this.maskOffsetY);
         }

         public void stop() {
            GameResources.edgeMaskShader.stop();
         }
      };
      return getArmorDrawOptions(this.isAttacking(), this.dir, var3, var4, var5, var6, var21, var8, var23, var24, var9, var10, var11.isEmpty() ? null : var11, var12, var30, var26, this.allAlpha, this.rotation, var1 + this.rotationX, var2 + this.rotationY, var29);
   }

   private boolean isSpriteXOffset(int var1) {
      return var1 == 1 || var1 == 3;
   }

   public void draw(int var1, int var2) {
      this.pos(var1, var2).draw();
   }

   public static void addArmorDrawOptions(DrawOptionsList var0, boolean var1, int var2, DrawOptions var3, DrawOptions var4, DrawOptions var5, DrawOptions var6, DrawOptions var7, DrawOptions var8, DrawOptions var9, DrawOptions var10, DrawOptions var11, DrawOptions var12, DrawOptions var13, boolean var14, DrawOptions var15, DrawOptions var16, ShaderState var17) {
      if (var17 != null) {
         Objects.requireNonNull(var17);
         var0.add(var17::use);
      }

      if (var3 != null) {
         var0.add(var3);
      }

      if (var2 == 0) {
         if (var13 != null && var14) {
            var0.add(var13);
         }

         if (var6 != null) {
            var0.add(var6);
         }

         if (var11 != null) {
            var0.add(var11);
         }

         if (var1) {
            if (var17 != null) {
               Objects.requireNonNull(var17);
               var0.add(var17::stop);
            }

            if (var12 != null) {
               var0.add(var12);
            }

            if (var17 != null) {
               Objects.requireNonNull(var17);
               var0.add(var17::use);
            }
         } else if (var12 != null) {
            var0.add(var12);
         }

         if (var13 != null && !var14) {
            var0.add(var13);
         }

         if (var9 != null) {
            var0.add(var9);
         }

         if (var10 != null) {
            var0.add(var10);
         }

         if (var7 != null) {
            var0.add(var7);
         }

         if (var4 != null) {
            var0.add(var4);
         }

         if (var15 != null) {
            var0.add(var15);
         }

         if (var8 != null) {
            var0.add(var8);
         }

         if (var5 != null) {
            var0.add(var5);
         }
      } else {
         if (var9 != null) {
            var0.add(var9);
         }

         if (var10 != null) {
            var0.add(var10);
         }

         if (var6 != null) {
            var0.add(var6);
         }

         if (var2 == 1 && var11 != null) {
            var0.add(var11);
         } else if (var2 == 3 && var12 != null) {
            var0.add(var12);
         }

         if (var7 != null) {
            var0.add(var7);
         }

         if (var13 != null) {
            if (var8 != null) {
               var0.add(var8);
            }

            if (var4 != null) {
               var0.add(var4);
            }

            if (var5 != null) {
               var0.add(var5);
            }

            if (!var1) {
               if (!var14) {
                  var0.add(var13);
               }

               if (var2 == 1 && var12 != null) {
                  var0.add(var12);
               } else if (var2 == 3 && var11 != null) {
                  var0.add(var11);
               } else if (var2 == 2) {
                  if (var11 != null) {
                     var0.add(var11);
                  }

                  if (var12 != null) {
                     var0.add(var12);
                  }
               }

               if (var14) {
                  var0.add(var13);
               }
            }
         } else {
            if (var8 != null) {
               var0.add(var8);
            }

            if (!var1) {
               if (var2 == 1 && var12 != null) {
                  var0.add(var12);
               } else if (var2 == 3 && var11 != null) {
                  var0.add(var11);
               } else if (var2 == 2) {
                  if (var11 != null) {
                     var0.add(var11);
                  }

                  if (var12 != null) {
                     var0.add(var12);
                  }
               }
            }

            if (var4 != null) {
               var0.add(var4);
            }

            if (var5 != null) {
               var0.add(var5);
            }
         }

         if (var15 != null) {
            var0.add(var15);
         }

         if (var1) {
            if (var2 == 1 && var12 != null) {
               if (var17 != null) {
                  Objects.requireNonNull(var17);
                  var0.add(var17::stop);
               }

               var0.add(var12);
               if (var17 != null) {
                  Objects.requireNonNull(var17);
                  var0.add(var17::use);
               }
            } else if (var2 == 3 && var11 != null) {
               if (var17 != null) {
                  Objects.requireNonNull(var17);
                  var0.add(var17::stop);
               }

               var0.add(var11);
               if (var17 != null) {
                  Objects.requireNonNull(var17);
                  var0.add(var17::use);
               }
            } else if (var2 == 2) {
               if (var11 != null) {
                  var0.add(var11);
               }

               if (var17 != null) {
                  Objects.requireNonNull(var17);
                  var0.add(var17::stop);
               }

               if (var12 != null) {
                  var0.add(var12);
               }

               if (var17 != null) {
                  Objects.requireNonNull(var17);
                  var0.add(var17::use);
               }
            }
         }
      }

      if (var16 != null) {
         var0.add(var16);
      }

      if (var17 != null) {
         Objects.requireNonNull(var17);
         var0.add(var17::stop);
      }

   }

   public static DrawOptionsList getArmorDrawOptions(boolean var0, int var1, DrawOptions var2, DrawOptions var3, DrawOptions var4, DrawOptions var5, DrawOptions var6, DrawOptions var7, DrawOptions var8, DrawOptions var9, DrawOptions var10, DrawOptions var11, DrawOptions var12, boolean var13, DrawOptions var14, DrawOptions var15, final float var16, final float var17, final int var18, final int var19, ShaderState var20) {
      DrawOptionsList var21 = new DrawOptionsList() {
         public void draw() {
            if (var16 == 1.0F && var17 == 0.0F) {
               super.draw();
            } else {
               Screen.applyDraw(() -> {
                  super.draw();
               }, () -> {
                  GL11.glTranslatef((float)var18, (float)var19, 0.0F);
                  GL11.glRotatef(var17, 0.0F, 0.0F, 1.0F);
                  GL11.glTranslatef((float)(-var18), (float)(-var19), 0.0F);
                  GL11.glColor4f(1.0F, 1.0F, 1.0F, var16);
               });
               GL11.glLoadIdentity();
            }

         }
      };
      addArmorDrawOptions(var21, var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var20);
      return var21;
   }

   @FunctionalInterface
   public interface HumanDrawOptionsGetter {
      DrawOptions getDrawOptions(PlayerMob var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, boolean var10, boolean var11, GameLight var12, float var13, GameTexture var14);
   }
}
