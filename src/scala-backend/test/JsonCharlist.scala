import models.charlist._
import play.api.libs.json.Json

/**
  * Created for testing by crimson on 10/18/16.
  */
object JsonCharlist {
  val charlist = Charlist(
    player = "vlex",
    name = "Bjorn Masterson",
    cp = CharacterPoints(cp = 265),
    description = Description("35", "6f6i", "250", "somelink", "longbio"),
    stats = Stats(
      dx = StatInt(delta = 3),
      ht = StatInt(delta = 2),
      will = StatInt(delta = 2),
      per = StatInt(delta = 2),
      basicSpeed = StatFrac(delta = -0.25)
    ),
    traits = Seq[Trait](
      Trait(
        name = "Combat Reflexes",
        types = Seq(TraitType.MENTAL, TraitType.MUNDANE),
        category = TraitCategory.ADVANTAGE,
        ref = "B43",
        attrBonuses = Seq(
          BonusAttribute(BonusToAttribute.DODGE, perLvl = false, bonus = 1),
          BonusAttribute(BonusToAttribute.PARRY, perLvl = false, bonus = 1),
          BonusAttribute(BonusToAttribute.BLOCK, perLvl = false, bonus = 1),
          BonusAttribute(BonusToAttribute.FC, perLvl = false, bonus = 2)
        ),
        skillBonuses = Seq(BonusSkill(skill = "Fast-Draw", perLvl = false, bonus = 1)),
        cpBase = 15
      ),
      Trait(
        name = "Sorcery",
        types = Seq(TraitType.MENTAL, TraitType.SUPER),
        category = TraitCategory.ADVANTAGE,
        cpBase = 10,
        level = 2,
        cpPerLvl = 10
      ),
      Trait(
        name = "Hot Pilot",
        types = Seq(TraitType.MENTAL, TraitType.MUNDANE),
        category = TraitCategory.TALENT,
        skillBonuses = Seq(
          BonusSkill(skill = "Piloting", bonus = 1),
          BonusSkill(skill = "Navigation", spc = "Air", spcCompare = NameCompare.IS, bonus = 1)
        ),
        reactBonuses = Seq(BonusReaction(affected = "Airshipmen", perLvl = true, bonus = 1)),
        ref = "B89",
        level = 3,
        cpPerLvl = 5
      ),
      Trait(
        name = "Positive Reputation: Airshipmen",
        types = Seq(TraitType.SOCIAL, TraitType.MUNDANE),
        category = TraitCategory.ADVANTAGE,
        ref = "B26",
        modifiers = Seq(
          TraitModifier(name = "Group size: Large", costType = TraitModifierCostType.MULTIPLIER, cost = .5),
          TraitModifier(
            name = "Frequency of recognition: Sometimes",
            reactBonuses = Seq(
              BonusReaction(affected = "Airshipmen", reputation = true, perLvl = true, freq = 10, bonus = 1)
            ),
            affects = TraitModifierAffects.TOTAL,
            costType = TraitModifierCostType.MULTIPLIER,
            cost = .5
          )
        ),
        level = 2,
        cpPerLvl = 5
      ),
      Trait(
        name = "Weapon Master (Knife)",
        types = Seq(TraitType.MENTAL, TraitType.MUNDANE),
        category = TraitCategory.ADVANTAGE,
        ref = "B99",
        modifiers = Seq(
          TraitModifier(
            name = "One specific weapon",
            dmgBonuses = Seq(
              BonusDamage(skill = "Knife", relSkill = 1, perDie = true, bonus = 1),
              BonusDamage(skill = "Knife", relSkill = 2, perDie = true, bonus = 1)
            ),
            affects = TraitModifierAffects.TOTAL,
            costType = TraitModifierCostType.POINTS,
            cost = 20)
        )
      ),
      Trait(name = "Skull", drBonuses = Seq(BonusDR(Seq(HitLocation.SKULL), dr = 2))),
      Trait(
        name = "Gigantism",
        types = Seq(TraitType.PHYSICAL, TraitType.MUNDANE),
        category = TraitCategory.ADVANTAGE,
        ref = "B20",
        attrBonuses = Seq(
          BonusAttribute(BonusToAttribute.SM, perLvl = false, bonus = 1),
          BonusAttribute(BonusToAttribute.BASIC_MOVE, perLvl = false, bonus = 1)
        ),
        skillBonuses = Seq(
          BonusSkill(skill = "Disguise", perLvl = false, bonus = -2),
          BonusSkill(skill = "Shadowing", perLvl = false, bonus = -2)),
        attrCostMods = Seq(BonusAttributeCost(attr = SkillBaseAttribute.ST, cost = -10))
      )
    ),
    skills = Seq[Skill](
      Skill(name = "Guns", spc = "Pistol", diff = SkillDifficulty.AVERAGE, cp = 4, tl = 5),
      Skill(
        name = "Brawling",
        dmgBonuses = Seq(
          BonusDamage(skill = "Brawling", relSkill = 1, perDie = true, bonus = 1),
          BonusDamage(skill = "Brawling", relSkill = 2, perDie = true, bonus = 1)
        ),
        cp = 4
      ),
      Skill(name = "Piloting", spc = "Lighter-Than-Air", tl = 5, diff = SkillDifficulty.AVERAGE, cp = 8),
      Skill(
        name = "Navigation",
        spc = "Air",
        attr = SkillBaseAttribute.IQ,
        tl = 5,
        diff = SkillDifficulty.AVERAGE,
        cp = 2
      ),
      Skill(
        name = "Navigation",
        spc = "Land",
        attr = SkillBaseAttribute.IQ,
        tl = 5,
        diff = SkillDifficulty.AVERAGE,
        cp = 2
      ),
      Skill(name = "Knife", cp = 8)
    ),
    techniques = Seq[Technique](
      Technique(
        name = "Off-hand weapon training",
        skill = "Guns",
        spc = "Pistol",
        diff = SkillDifficulty.HARD,
        style = "Trench Warfare",
        defLvl = -4,
        cp = 5
      )
    ),
    equip = Equipment(
      weapons = Seq[Weapon](
        Weapon(
          name = "Brawling",
          carried = ItemState.READY,
          attacksMelee = Seq[MeleeAttack](
            MeleeAttack(
              name = "Punch",
              available = true,
              damage = MeleeDamage(attackType = AttackType.THRUSTING, dmgMod = -1),
              followup = Seq[MeleeDamage](MeleeDamage(dmgDice = 4, dmgType = DamageType.BURNING)),
              linked = Seq[MeleeDamage](MeleeDamage(dmgDice = 2, dmgType = DamageType.TOXIC)),
              skill = "Brawling",
              st = 1,
              reach = "C"
            ),
            MeleeAttack(
              name = "Kick in Boots",
              available = true,
              damage = MeleeDamage(attackType = AttackType.THRUSTING),
              skill = "Brawling",
              parryType = "No",
              reach = "1"
            )
          ),
          hp = 4,
          hpLeft = 4
        ),
        Weapon(
          name = "Revolver",
          carried = ItemState.READY,
          attacksRanged = Seq[RangedAttack](
            RangedAttack(
              name = "LE",
              available = true,
              damage = RangedDamage(dmgDice = 1, dmgMod = 2, armorDiv = 0.5, dmgType = DamageType.PIERCING_LARGE),
              followup = Seq[RangedDamage](
                RangedDamage(dmgDice = 1, dmgMod = 1, dmgType = DamageType.CRUSHING_EXPLOSION, fragDice = 1)
              ),
              linked = Seq[RangedDamage](RangedDamage(dmgDice = 2, dmgType = DamageType.TOXIC)),
              skill = "Guns",
              spc = "Pistol",
              acc = 2,
              rng = "50/200",
              shots = RangedShots(
                shots = 6,
                reload = "(3i)",
                shotsLoaded = 6,
                shotsCarried = 30,
                shotWt = 0.1,
                shotCost = 2
              ),
              st = 9,
              malf = 17
            )
          ),
          offHand = true,
          bulk = -2,
          dr = 7,
          hp = 10,
          hpLeft = 10,
          lc = 4,
          tl = 5,
          wt = 4.1,
          cost = 350
        ),
        Weapon(
          name = "Knife",
          attacksMelee = Seq(
            MeleeAttack(
              name = "swing",
              damage = MeleeDamage(attackType = AttackType.SWINGING, dmgType = DamageType.CUTTING),
              skill = "Knife",
              parry = -1,
              st = 6,
              reach = "C,1"),
            MeleeAttack(
              name = "Thrust",
              damage = MeleeDamage(attackType = AttackType.THRUSTING, dmgType = DamageType.IMPALING),
              skill = "Knife",
              parry = -1,
              st = 6,
              reach = "C")
          ),
          bulk = -1,
          dr = 7,
          hp = 5,
          hpLeft = 5,
          wt = 1.0,
          cost = 50
        )
      ),
      armor = Seq[Armor](
        Armor(
          name = "Boots",
          carried = ItemState.EQUIPPED,
          dr = 1,
          ep = 1,
          epi = 1,
          locations = Seq[String](HitLocation.FEET),
          hp = 5,
          hpLeft = 5,
          tl = 5,
          wt = 2,
          cost = 50
        )
      ),
      items = Seq[Item](
        Item(name = "Holster", carried = ItemState.EQUIPPED, dr = 1, hp = 5, hpLeft = 5, tl = 5, wt = 0.5, cost = 75),
        Item(name = "Small Backpack", carried = ItemState.TRAVEL, hp = 5, hpLeft = 5, tl = 5, wt = 3, cost = 60)
      )
    )
  )
  val jsonCharlist = Json toJson charlist
}

object JsonPrinter extends App {
  println(Json prettyPrint JsonCharlist.jsonCharlist)
}