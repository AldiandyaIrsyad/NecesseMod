# Uneiro's Vanilla RougeLike Upgrade Mod (WIP)

## About 
This mod focuses on balanced between classes in higher difficulties. 

## Status
- [ ] Change armor damage reduction formula from `0.5*armor` to `armor/(armor + 100) * 100`
- [ ] Change armor penetration formula to account for armor DR change
- [ ] Rework resilience to gray health
- [ ] Added Cursed Mage Classes (New weapon, armor, and trinket)
- [ ] Added Rogue Classes (New weapon, armor, and trinket)
- [ ] 

## 

## Argument

### Armor

Vanilla damage reduction is using this formula

```
damageReduction = max(0, (armor - armorPen) * 0.5)
```

This make it useless in higher difficulty. 

Let's say you get hit 100 damage and have 100 armor

| Difficulty | Damage | Damage Multiplier | Damage Multiplied| Damage Taken |
|------------|--------|-------------------|------------------|--------------|
| Casual     | 100    | 0.5               | 50               | 1            |
| Adventure  | 100    | 0.7               | 70               | 20           |
| Classic    | 100    | 1.0               | 100              | 50           |
| Hard       | 100    | 1.3               | 130              | 80           |
| Brutal     | 100    | 2.0               | 200              | 150          |

Even though brutal should take 2x damage from Classic. It actually gave 3x the damage. Making armor less effective in higher difficulty

This mod use percentage to calculate damage reduction with the following formula

```
damageReduction = armor / (armor + 100) * 100
```

| Difficulty | Damage | Damage Multiplier | Damage Multiplied| Damage Taken |
|------------|--------|-------------------|------------------|--------------|
| Casual     | 100    | 0.5               | 50               | 33           |
| Adventure  | 100    | 0.7               | 70               | 46           |
| Classic    | 100    | 1.0               | 100              | 66           |
| Hard       | 100    | 1.3               | 130              | 86           |
| Brutal     | 100    | 2.0               | 200              | 133          |

Now the brutal difficulty has exactly twice the damage as Classic regardless of armor value. Making armor more useful in higher difficulty

This also nerf armor effectiveness in Casual, where having high enough armor can make you practically invincible. 

### Armor Penetration

### Resilience to Gray Health Rework

Note: Unimplemented, but planned




## Reference

### Armor
necesse\entity\mobs\gameDamageType\DamageType.class
necesse.entity.mobs.gameDamageType 

```Java
   public float getDamageReduction(Mob target, Attacker attacker, GameDamage damage) {
      float armorPen = damage.armorPen;
      if (attacker != null) {
         Mob attackOwner = attacker.getAttackOwner();
         if (attackOwner != null) {
            armorPen = (float)((int)((armorPen + (float)(Integer)attackOwner.buffManager.getModifier(BuffModifiers.ARMOR_PEN_FLAT)) * (Float)attackOwner.buffManager.getModifier(BuffModifiers.ARMOR_PEN)));
         }
      }

      return getDamageReduction(target.getArmorAfterPen(armorPen));
   }


   public static float getDamageReduction(float armor) {
      return armor * 0.5F;
   }
```

mobBeforeHitCalculatedEvent
```JAVA
   public int getExpectedHealth() {
      return this.prevented ? this.target.getHealth() : Math.max(this.target.getHealth() - this.damage, 0);
   }
   ```