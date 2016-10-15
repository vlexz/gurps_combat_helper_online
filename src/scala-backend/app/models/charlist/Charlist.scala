package models.charlist

/**
  * Created by crimson on 9/23/16.
  */
case class Charlist(// TODO: make recalc function in compliance with functional programming style
                    _id: String = "",
                    timestamp: Long = 0,
                    player: String = "",
                    access: Seq[String] = Seq(), // For future functionality
                    name: String = "",
                    cp: CharacterPoints = CharacterPoints(),
                    description: Description = Description(),
                    stats: Stats = Stats(),
                    statVars: StatVars = StatVars(),
                    var reactions: Seq[Reaction] = Seq(),
                    traits: Seq[Trait] = Seq(
                      Trait(name = "Skull", drBonuses = Seq(BonusDR(Seq(HitLocation.SKULL), dr = 2)))
                    ),
                    skills: Seq[Skill] = Seq(),
                    techniques: Seq[Technique] = Seq(),
                    equip: Equipment = Equipment(),
                    conditions: Conditions = Conditions(),
                    var api: String = ""
                   ) {
  api = "0.2"

  for {
    bonus <- traits.flatMap(_.drBonuses) ++ traits.flatMap(_.modifiers.flatMap(_.drBonuses))
    location <- bonus.locations
  } {
    if (bonus.front) equip.frontDR.add(bonus.dr, bonus.ep, bonus.epi, location)
    if (bonus.back) equip.rearDR.add(bonus.dr, bonus.ep, bonus.epi, location)
  }

  var fcBonus, dodgeBonus, parryBonus, blockBonus = 0

  {
    import BonusToAttribute._

    traits.foreach(stats.st.bonus += _.attrBonusValue(ST))
    traits.foreach(stats.dx.bonus += _.attrBonusValue(DX))
    traits.foreach(stats.iq.bonus += _.attrBonusValue(IQ))
    traits.foreach(stats.ht.bonus += _.attrBonusValue(HT))
    traits.foreach(stats.will.bonus += _.attrBonusValue(WILL))
    traits.foreach(stats.per.bonus += _.attrBonusValue(PER))
    traits.foreach(stats.basicSpeed.bonus += _.attrBonusValue(BASIC_SPEED))
    traits.foreach(stats.basicMove.bonus += _.attrBonusValue(BASIC_MOVE))
    traits.foreach(stats.hp.bonus += _.attrBonusValue(HP))
    traits.foreach(stats.fp.bonus += _.attrBonusValue(FP))
    traits.foreach(fcBonus += _.attrBonusValue(FC))
    traits.foreach(dodgeBonus += _.attrBonusValue(DODGE))
    traits.foreach(parryBonus += _.attrBonusValue(PARRY))
    traits.foreach(blockBonus += _.attrBonusValue(BLOCK))
    traits.foreach(stats.st.cpMod += _.attrCostMod(ST))
    traits.foreach(stats.dx.cpMod += _.attrCostMod(DX))
    traits.foreach(stats.iq.cpMod += _.attrCostMod(IQ))
    traits.foreach(stats.ht.cpMod += _.attrCostMod(HT))
    stats.st.calcStat(10, 10)
    stats.dx.calcStat(10, 20)
    stats.iq.calcStat(10, 20)
    stats.ht.calcStat(10, 10)
    stats.will.calcStat(10, 5)
    stats.per.calcStat(10, 5)
    stats.liftSt.calcStat(stats.st.value, 3)
    stats.strikeSt.calcStat(stats.st.value, 5)
    stats.hp.calcStat(stats.st.value, 2)
    stats.fp.calcStat(stats.ht.value, 3)
    stats.basicSpeed.calcStat((stats.dx.value + stats.ht.value) * .25, 20)
    stats.basicMove.calcStat(stats.basicSpeed.value.toInt, 5)
    statVars.frightCheck = math.min(13, stats.will.value + fcBonus)
    statVars.vision = stats.per.value
    traits.foreach(statVars.vision += _.attrBonusValue(VISION))
    statVars.hearing = stats.per.value
    traits.foreach(statVars.hearing += _.attrBonusValue(HEARING))
    statVars.tasteSmell = stats.per.value
    traits.foreach(statVars.tasteSmell += _.attrBonusValue(TASTE_SMELL))
    statVars.touch = stats.per.value
    traits.foreach(statVars.touch += _.attrBonusValue(TOUCH))
    statVars.sm = 0
    traits.foreach(statVars.sm += _.attrBonusValue(SM))
    statVars
      .calcBL(stats.liftSt.value)
      .calcEncumbrance(equip.totalCombWt, equip.totalTravWt)
      .calcMove(stats.basicMove.value, stats.basicSpeed.value.toInt + 3 + dodgeBonus,
        stats.hp.collapsing || stats.fp.collapsing)
  }

  for {
    skill <- skills
    trt <- traits
  } skill.bonus += trt.skillBonusValue(skill.name, skill.spc)
  skills.foreach { s =>
    s.calcLvl(s.attr match {
      case a if a == SkillBaseAttribute.ST => stats.st.value
      case a if a == SkillBaseAttribute.DX => stats.dx.value
      case a if a == SkillBaseAttribute.IQ => stats.iq.value
      case a if a == SkillBaseAttribute.HT => stats.ht.value
      case a if a == SkillBaseAttribute.WILL => stats.will.value
      case a if a == SkillBaseAttribute.PER => stats.per.value
    }, statVars.travelEncumbrance)
  }

  techniques.foreach { t =>
    t.calcLvl(
      skills
        .find(s => s.name == t.skill && (if (t.spc != "") s.spc == t.spc else true))
        .getOrElse(Skill())
        .lvl
    )
  }

  reactions =
    (traits.flatMap(_.reactBonuses) ++
      traits.flatMap(_.modifiers.flatMap(_.reactBonuses)) ++
      skills.flatMap(_.reactBonuses))
      .groupBy(_.affected)
      .map { case (group: String, react: Seq[BonusReaction]) =>
        Reaction(
          group,
          react
            .groupBy(_.freq) // TODO: inverse frequency and reputation grouping ?
            .map { case (group: Int, react: Seq[BonusReaction]) =>
            val r = react
              .groupBy(_.reputation)
              .map { case (group: Boolean, react: Seq[BonusReaction]) =>
                react
                  .reduce { (a, b) =>
                    val bonus = a.bonusValue + b.bonusValue
                    BonusReaction(
                      a.affected,
                      a.reputation,
                      perLvl = false,
                      a.freq,
                      if (group) bonus match {
                        case x if x > 4 => 4
                        case x if x < -4 => -4
                        case x => x
                      } else bonus,
                      a.notes + (if (a.notes.nonEmpty && b.notes.nonEmpty) "; " else "") + b.notes)
                  }
              }
              .reduce { (a, b) =>
                BonusReaction(
                  a.affected,
                  a.reputation,
                  perLvl = false,
                  a.freq,
                  a.bonusValue + b.bonusValue,
                  a.notes + (if (a.notes.nonEmpty && b.notes.nonEmpty) "; " else "") + b.notes
                )
              }
            ReactionMod(group, r.bonusValue, r.notes)
          }
            .toSeq
        )
      }
      .toSeq

  val thr = stats.strikeSt.value match {
    case x if x < 1 => (0, 0)
    case x if x < 11 => (1, ((x - 1) / 2) - 6)
    case x => ((x - 3) / 8, ((x - 3) / 2) % 4 - 1)
  }
  val sw = stats.strikeSt.value match {
    case x if x < 1 => (0, 0)
    case x if x < 9 => (1, ((x - 1) / 2) - 5)
    case x => ((x - 5) / 4, (x - 5) % 4 - 1)
  }
  statVars.calcDmg(thr, sw)

  for {
    weapon <- equip.weapons
    mAttack <- weapon.attacksMelee
  } {
    val bonus =
      skills
        .map { skill =>
          skill
            .dmgBonusValue(
              mAttack.skill,
              mAttack.spc,
              skills
                .find(s => s.name == mAttack.skill && (if (mAttack.spc.nonEmpty) s.spc == mAttack.spc else true))
                .getOrElse(Skill())
                .relLvl
            )
        }
        .reduceOption((a, b) => (a._1 + b._1, a._2 + b._2))
        .getOrElse((0, 0))
    mAttack.damage.calcDmg(thr, sw, bonus)
    (mAttack.followup ++ mAttack.linked).foreach(_ calcDmg(thr, sw, (0, 0)))
  }

  cp.stats = stats.cp
  traits.withFilter(_.cp >= 0).foreach(cp.adv += _.cp)
  traits.withFilter(_.cp < 0).foreach(cp.dis += _.cp)
  skills.foreach(cp.skills += _.cp)
  techniques.foreach(cp.skills += _.cp)
  cp.calcUnspent()
}

/** Charlist subnamespace for json field name strings */
object CharlistFields {
  val ID = "_id"
  val TIMESTAMP = "timestamp"
  val PLAYER = "player"
  val NAME = "name"
}

/** Charlist subcontainer for character points */
case class CharacterPoints(
                            cp: Int = 0,
                            var stats: Int = 0,
                            var adv: Int = 0,
                            var dis: Int = 0,
                            var skills: Int = 0,
                            var unspent: Int = 0
                          ) {
  stats = 0
  adv = 0
  dis = 0
  skills = 0

  def calcUnspent() = {
    unspent = cp - (skills + stats + adv + dis)
    this
  }
}

/** Charlist subcontainer for character description */
case class Description(
                        age: String = "",
                        height: String = "",
                        weight: String = "",
                        portrait: String = "",
                        bio: String = ""
                      )

/** Charlist subcontainer for basic and secondary attributes */
case class Stats(
                  st: StatInt = StatInt(),
                  dx: StatInt = StatInt(),
                  iq: StatInt = StatInt(),
                  ht: StatInt = StatInt(),
                  will: StatInt = StatInt(),
                  per: StatInt = StatInt(),
                  liftSt: StatInt = StatInt(),
                  strikeSt: StatInt = StatInt(),
                  hp: StatPoints = StatPoints(),
                  fp: StatPoints = StatPoints(),
                  basicSpeed: StatFrac = StatFrac(),
                  basicMove: StatInt = StatInt()
                ) {
  def cp = st.cp + dx.cp + iq.cp + ht.cp + will.cp + per.cp + liftSt.cp + strikeSt.cp + hp.cp + fp.cp +
    basicSpeed.cp + basicMove.cp
}

/** Charlist subcontainer for calculated stats */
case class StatVars(
                     var frightCheck: Int = 0,
                     var vision: Int = 0,
                     var hearing: Int = 0,
                     var tasteSmell: Int = 0,
                     var touch: Int = 0,
                     var thrDmg: String = "",
                     var swDmg: String = "",
                     var bl: Int = 0,
                     var combatEncumbrance: Int = 0,
                     var travelEncumbrance: Int = 0,
                     var combMove: Int = 0,
                     var travMove: Int = 0,
                     var dodge: Int = 0,
                     var sm: Int = 0
                   ) {
  def calcDmg(thr: (Int, Int), sw: (Int, Int)) = {
    thrDmg = s"${thr._1}d${if (thr._2 < 0) thr._2 else if (thr._2 == 0) "" else "+" + thr._2}"
    swDmg = s"${sw._1}d${if (sw._2 < 0) sw._2 else if (sw._2 == 0) "" else "+" + sw._2}"
    this
  }

  def calcBL(liftSt: Int) = {
    bl = (liftSt * liftSt * .2).toInt
    this
  }

  def calcEncumbrance(combWt: Double, travWt: Double) = {
    assert(combWt <= bl * 10, s"combat encumbrance value is over 10 times basic lift ($combWt, $bl)")
    assert(travWt <= bl * 10, s"travel encumbrance value is over 10 times basic lift ($travWt, $bl)")
    combatEncumbrance = combWt / bl match {
      case x if x <= 1 => 0
      case x if x <= 2 => 1
      case x if x <= 3 => 2
      case x if x <= 6 => 3
      case x if x <= 10 => 4
    }
    travelEncumbrance = travWt / bl match {
      case x if x <= 1 => 0
      case x if x <= 2 => 1
      case x if x <= 3 => 2
      case x if x <= 6 => 3
      case x if x <= 10 => 4
    }
    this
  }

  def calcMove(basicMove: Int, basicDodge: Int, compromised: Boolean) = {
    val compr = if (compromised) .5 else 1
    def calc(enc: Int) = math.ceil((basicMove * .2 * (5 - enc) - 0.01).toInt * compr).toInt
    combMove = calc(combatEncumbrance)
    travMove = calc(travelEncumbrance)
    dodge = math.ceil((basicDodge - combatEncumbrance) * compr - 0.01).toInt
    this
  }
}

/** Charlist subcontainer for character attribute storage */
case class StatInt(
                    delta: Int = 0,
                    var base: Int = 0,
                    var bonus: Int = 0,
                    var cpMod: Int = 100,
                    var cp: Int = 0
                  ) {
  bonus = 0
  cpMod = 100

  def value = base + delta + bonus

  def calcStat(default: Int, cost: Int) = {
    base = default
    cp = math.ceil(delta * cost * math.max(.2, cpMod * .01 + 1.0) - 0.01).toInt
    this
  }
}

/** Charlist subcontainer for character attribute storage */
case class StatFrac(// TODO: try to merge with StatInt using Numeric
                    delta: Double = 0,
                    var base: Double = 0,
                    var bonus: Double = 0,
                    var cpMod: Int = 100,
                    var cp: Int = 0
                   ) {
  bonus = 0
  cpMod = 100

  def value = base + delta + bonus

  def calcStat(default: Double, cost: Int) = {
    base = default
    cp = math.ceil(delta * cost * math.max(.2, cpMod * .01 + 1.0) - 0.01).toInt
    this
  }
}

/** Charlist subcontainer for HP and FP attributes' stats */
case class StatPoints(
                       delta: Int = 0,
                       var base: Int = 0,
                       var bonus: Int = 0,
                       var cpMod: Int = 100,
                       var cp: Int = 0,
                       lost: Int = 0,
                       var compromised: Boolean = false,
                       var collapsing: Boolean = false
                     ) {
  assert(lost >= 0, s"negative points lost value ($lost)")
  bonus = 0
  cpMod = 100

  def value = base + delta + bonus

  def calcStat(default: Int, cost: Int) = {
    base = default
    this.bonus = bonus
    cp = math.ceil(delta * cost * math.max(.2, cpMod * .01 + 1.0)).toInt
    if ((base + bonus + delta) * (2.0 / 3.0) < lost) compromised = true
    if (value <= lost) collapsing = true
    this
  }
}

/** Charlist subcontainer for NPC reactions list's element stats */
case class Reaction(
                     affected: String = "",
                     modifiers: Seq[ReactionMod] = Seq()
                   )

/** Charlist subcontainer for NPC reaction modifiers list's element stats */
case class ReactionMod(
                        freq: Int = 16,
                        mod: Int = 0,
                        notes: String = ""
                      )

/** Charlist subcontainer for features list's element stats */
case class Trait(
                  name: String = "",
                  types: Seq[String] = Seq(TraitType.PHYSICAL),
                  category: String = TraitCategory.ADVANTAGE,
                  ref: String = "",
                  notes: String = "",
                  prerequisites: Seq[String] = Seq(), // For future functionality
                  modifiers: Seq[TraitModifier] = Seq(),
                  attrBonuses: Seq[BonusAttribute] = Seq(),
                  skillBonuses: Seq[BonusSkill] = Seq(),
                  dmgBonuses: Seq[BonusDamage] = Seq(),
                  drBonuses: Seq[BonusDR] = Seq(),
                  attrCostMods: Seq[BonusAttributeCost] = Seq(),
                  reactBonuses: Seq[BonusReaction] = Seq(),
                  cpBase: Int = 0,
                  level: Int = 0,
                  cpPerLvl: Int = 0,
                  var cp: Int = 0
                ) {
  assert(TraitType.isValid(types), s"invalid trait type string(s) (${types.mkString(",")} in $name")
  assert(TraitCategory.isValid(category), s"invalid trait category string ($category in $name)")
  assert(level >= 0, s"trait's levels value is negative ($level in $name)")
  assert(level == 0 || cpPerLvl != 0,
    s"leveled trait's CP per level value is 0 (level $level, CP/level $cpPerLvl in $name)")
  (reactBonuses ++ modifiers.flatMap(_.reactBonuses)) foreach (_ calcValue level)
  var basePoints = cpBase
  var basePercent = 100
  var baseMult: Double = 1
  modifiers
    .withFilter(_.affects == TraitModifierAffects.BASE)
    .foreach { x =>
      basePoints += x.costValue._1
      basePercent += x.costValue._2
      baseMult *= x.costValue._3
    }
  var lvlPoints = cpPerLvl
  var lvlPercent = 100
  var lvlMult: Double = 1
  modifiers
    .withFilter(_.affects == TraitModifierAffects.LEVELS)
    .foreach { x =>
      lvlPoints += x.costValue._1
      lvlPercent += x.costValue._2
      lvlMult *= x.costValue._3
    }
  var totalPoints = 0
  var totalPercent = 100
  var totalMult: Double = 1
  modifiers
    .withFilter(_.affects == TraitModifierAffects.TOTAL)
    .foreach { x =>
      totalPoints += x.costValue._1
      totalPercent += x.costValue._2
      totalMult *= x.costValue._3
    }
  cp =
    math
      .ceil {
        (basePoints * math.max(.2, basePercent * .01) * baseMult
          + level * lvlPoints * math.max(.2, lvlPercent * .01) * lvlMult
          + totalPoints) * math.max(.2, totalPercent * .01) * totalMult - 0.01
      }
      .toInt

  def attrBonusValue(a: String) = {
    var v = 0
    (attrBonuses ++ modifiers.flatMap(_.attrBonuses))
      .withFilter(_.attr == a)
      .foreach(b => v += (b.bonus * (if (b.perLvl) level else 1)))
    v
  }

  def attrCostMod(a: String) = {
    var m = 0
    (attrCostMods ++ modifiers.flatMap(_.attrCostMods))
      .withFilter(_.attr == a)
      .foreach(m += _.cost)
    m
  }

  def skillBonusValue(s: String, spc: String) = {
    var v = 0
    (skillBonuses ++ modifiers.flatMap(_.skillBonuses))
      .withFilter { b =>
        (b.skillCompare match {
          case x if x == NameCompare.IS => b.skill == s
          case x if x == NameCompare.BEGINS => b.skill.regionMatches(true, 0, s, 0, b.skill.length)
        }) && (b.spcCompare match {
          case x if x == NameCompare.ANY => true
          case x if x == NameCompare.IS => b.spc == spc
          case x if x == NameCompare.BEGINS => b.spc.regionMatches(true, 0, spc, 0, b.spc.length)
        })
      }
      .foreach(b => v += (b.bonus * (if (b.perLvl) level else 1)))
    v
  }

  def dmgBonusValue(s: String, spc: String, lvl: Int) = {
    dmgBonuses
      .withFilter { b =>
        (b.skillCompare match {
          case x if x == NameCompare.IS => b.skill == s
          case x if x == NameCompare.BEGINS => b.skill.regionMatches(true, 0, s, 0, b.skill.length)
        }) && (b.spcCompare match {
          case x if x == NameCompare.ANY => true
          case x if x == NameCompare.IS => b.spc == spc
          case x if x == NameCompare.BEGINS => b.spc.regionMatches(true, 0, spc, 0, b.spc.length)
        }) && b.relSkill <= lvl
      }
      .map(b => if (b.perDie) (b.bonus, 0) else (0, b.bonus))
      .reduceOption((a, b) => (a._1 + b._1, a._2 + b._2))
      .getOrElse((0, 0))
  } // TODO: duplicated method
}

/** Charlist subcontainer for trait modifiers list's element stats */
case class TraitModifier(
                          name: String = "",
                          ref: String = "",
                          notes: String = "",
                          attrBonuses: Seq[BonusAttribute] = Seq(),
                          skillBonuses: Seq[BonusSkill] = Seq(),
                          dmgBonuses: Seq[BonusDamage] = Seq(),
                          drBonuses: Seq[BonusDR] = Seq(), //
                          attrCostMods: Seq[BonusAttributeCost] = Seq(),
                          reactBonuses: Seq[BonusReaction] = Seq(),
                          affects: String = TraitModifierAffects.TOTAL,
                          costType: String = TraitModifierCostType.PERCENT,
                          level: Int = 0,
                          cost: Double = 0
                        ) {

  import TraitModifierCostType._

  assert(TraitModifierAffects.isValid(affects), s"invalid trait modifier's 'affects' string ($affects in $name")
  assert(TraitModifierCostType.isValid(costType), s"invalid trait modifier's cost type string ($costType in $name)")
  assert(level >= 0, s"trait modifier's level value is negative ($level in $name)")
  assert(costType != LEVEL || level != 0, s"leveled trait modifier's level value is 0 ($name)")

  def costValue: (Int, Int, Double) = costType match {
    case x if x == POINTS => (cost.toInt, 0, 1)
    case x if x == PERCENT => (0, cost.toInt, 1)
    case x if x == LEVEL => (0, cost.toInt * level, 1)
    case x if x == MULTIPLIER => (0, 0, cost)
  }
}

/** Charlist subcontainer for skills list's element stats, calculates its relative level */
case class Skill(
                  name: String = "",
                  spc: String = "",
                  tl: Int = 0,
                  var skillString: String = "",
                  attr: String = SkillBaseAttribute.DX,
                  diff: String = SkillDifficulty.EASY,
                  defaults: Seq[String] = Seq(), // For future functionality
                  prerequisites: Seq[String] = Seq(), // For future functionality
                  dmgBonuses: Seq[BonusDamage] = Seq(),
                  reactBonuses: Seq[BonusReaction] = Seq(),
                  encumbr: Boolean = false,
                  var bonus: Int = 0,
                  categories: Seq[String] = Seq(),
                  notes: String = "",
                  cp: Int = 1,
                  var relLvl: Int = 0,
                  var lvl: Int = 0
                ) {
  assert(tl >= 0 && tl < 13, s"skill's TL value out of bounds ($tl in $name)")
  assert(SkillBaseAttribute.isValid(attr), s"invalid skill's attribute ($attr in $name)")
  assert(SkillDifficulty.isValid(diff), s"invalid skill's difficulty ($diff in $name")
  assert(cp > 0, s"skill's CP value is negative or 0 ($cp in $name")
  skillString = name + (if (tl != 0) s"/TL$tl" else "") + (if (spc != "") s" ($spc)" else "")
  relLvl = SkillDifficulty.values(diff) + (if (cp > 1) 1 else 0) + (cp / 4) + bonus

  def calcLvl(attrVal: Int, enc: Int) = {
    lvl = attrVal + relLvl - (if (encumbr) enc else 0)
    this
  }

  def dmgBonusValue(s: String, spc: String, lvl: Int) = {
    dmgBonuses
      .withFilter { b =>
        (b.skillCompare match {
          case x if x == NameCompare.IS => b.skill == s
          case x if x == NameCompare.BEGINS => b.skill.regionMatches(true, 0, s, 0, b.skill.length)
        }) && (b.spcCompare match {
          case x if x == NameCompare.ANY => true
          case x if x == NameCompare.IS => b.spc == spc
          case x if x == NameCompare.BEGINS => b.spc.regionMatches(true, 0, spc, 0, b.spc.length)
        }) && b.relSkill <= lvl
      }
      .map(b => if (b.perDie) (b.bonus, 0) else (0, b.bonus))
      .reduceOption((a, b) => (a._1 + b._1, a._2 + b._2))
      .getOrElse((0, 0))
  } // TODO: duplicated method
}

/** Charlist subcontainer for techniques list's element stats, calculates its relative level */
case class Technique(
                      name: String = "",
                      skill: String = "",
                      spc: String = "",
                      var tchString: String = "",
                      diff: String = TechniqueDifficulty.AVERAGE,
                      style: String = "",
                      defLvl: Int = 0,
                      maxLvl: Int = 0,
                      notes: String = "",
                      cp: Int = 0,
                      var relLvl: Int = 0,
                      var lvl: Int = 0
                    ) {
  assert(TechniqueDifficulty.isValid(diff), s"invalid technique's difficulty string ($diff in $name)")
  assert(cp >= 0 && cp <= (maxLvl - defLvl + (if (diff == TechniqueDifficulty.HARD) 1 else 0)),
    s"technique's cp value out of bounds ($cp in $name")
  tchString = name + " (" + skill + (if (spc != "") " (" + spc + ")") + ")"
  relLvl = math.max(cp + defLvl - (if (diff == TechniqueDifficulty.HARD) 1 else 0), defLvl)

  def calcLvl(skill: Int) = {
    lvl = skill + relLvl
    this
  }
}

/** Charlist subcontainer for attribute bonuses list's element stats */
case class BonusAttribute(
                           attr: String = "",
                           perLvl: Boolean = true,
                           bonus: Int = 0
                         ) {
  assert(BonusToAttribute isValid attr, s"invalid attribute string ($attr)")
}

/** Charlist subcontainer for skill bonuses list's element stats */
case class BonusSkill(
                       skill: String = "",
                       skillCompare: String = NameCompare.IS,
                       spc: String = "",
                       spcCompare: String = NameCompare.ANY,
                       perLvl: Boolean = true,
                       bonus: Int = 0
                     ) {
  assert(NameCompare isValid skillCompare, s"invalid skill bonus' skill name comparation string ($skillCompare)")
  assert(skill != "", s"empty skill bonus' skill name string")
  assert(NameCompare isValid spcCompare,
    s"invalid skill bonus' skill specialization comparation string ($spcCompare)")
  assert(spc != "" || spcCompare == NameCompare.ANY,
    s"invalid skill bonus' comparation with empty skill specialization string ($spcCompare)")
}

/** Charlist subcontainer for damage bonuses list's element stats */
case class BonusDamage(
                        skill: String = "",
                        skillCompare: String = NameCompare.IS,
                        spc: String = "",
                        spcCompare: String = NameCompare.ANY,
                        relSkill: Int = 0,
                        perDie: Boolean = false,
                        bonus: Int = 0
                      ) {
  assert(NameCompare isValid skillCompare, s"invalid damage bonus' skill name comparation string ($skillCompare)")
  assert(skill != "", s"empty damage bonus' skill name string")
  assert(NameCompare isValid spcCompare,
    s"invalid damage bonus' skill specialization comparation string ($spcCompare)")
  assert(spc != "" || spcCompare == NameCompare.ANY,
    s"invalid damage bonus' comparation with empty skill specialization string ($spcCompare)")
}

/** Charlist subcontainer for DR bonuses list's element stats */
case class BonusDR(
                    locations: Seq[String] = Seq(),
                    perLvl: Boolean = true,
                    front: Boolean = true,
                    back: Boolean = true,
                    dr: Int = 0,
                    ep: Int = 0,
                    epi: Int = 0
                  ) {
  assert(HitLocation isValid locations, s"invalid DR bonus' locations string(s) (${locations mkString ","})")
  assert(dr >= 0, s"negative DR bonus' DR value ($dr)")
  assert(ep >= 0, s"negative DR bonus' EP value ($ep)")
  assert(epi >= 0, s"negative DR bonus' EPi value ($epi)")
}

/** Charlist subcontainer for attribute cost modifiers list's element stats */
case class BonusAttributeCost(
                               attr: String = "",
                               cost: Int = 0
                             ) {
  assert(SkillBaseAttribute isValid attr, s"invalid attribute string ($attr)")
}

/** Charlist subcontainer for reaction bonuses list's element stats */
case class BonusReaction(
                          affected: String = "",
                          reputation: Boolean = false,
                          perLvl: Boolean = false,
                          freq: Int = 16,
                          bonus: Int = 0,
                          notes: String = ""
                        ) {
  assert(ReactionFrequency isValid freq, s"invalid reaction bonus' frequency value ($freq)")
  var bonusValue = bonus

  def calcValue(lvl: Int) = {
    if (perLvl) bonusValue *= lvl
    this
  }
}

/** Charlist subnamespace for trait type strings and validation method */
object TraitType {
  val MENTAL = "Mental"
  val PHYSICAL = "Physical"
  val SOCIAL = "Social"
  val MUNDANE = "Mundane"
  val EXOTIC = "Exotic"
  val SUPER = "Supernatural"

  def isValid(t: Seq[String]) = t forall Set(MENTAL, PHYSICAL, SOCIAL, MUNDANE, EXOTIC, SUPER).contains
}

/** Charlist subnamespaces for trait category strings and validation method */
object TraitCategory {
  val ADVANTAGE = "Advantage"
  val DISADVANTAGE = "Disadvantage"
  val PERK = "Perk"
  val QUIRK = "Quirk"
  val LANGUAGE = "Language"
  val TALENT = "Talent"

  def isValid(c: String) = Set(ADVANTAGE, DISADVANTAGE, PERK, QUIRK, LANGUAGE, TALENT) contains c
}

/** Charlist subnamespace for trait modifier cost effect strings and validation method */
object TraitModifierAffects {
  val TOTAL = "total"
  val BASE = "base"
  val LEVELS = "levels"

  def isValid(a: String) = Set(TOTAL, BASE, LEVELS) contains a
}

/** Charlist subnamespace for trait modifier cost type strings and validation method */
object TraitModifierCostType {
  val PERCENT = "percent"
  val LEVEL = "percent per level"
  val POINTS = "points"
  val MULTIPLIER = "multiplier"

  def isValid(t: String) = Set(PERCENT, LEVEL, POINTS, MULTIPLIER) contains t
}

/** Charlist subnamespace for skill difficulties strings and validation method */
object SkillDifficulty {
  val EASY = "E"
  val AVERAGE = "A"
  val HARD = "H"
  val VERY_HARD = "VH"
  val WOW = "W"

  val values: Map[String, Int] = Map(EASY -> 0, AVERAGE -> -1, HARD -> -2, VERY_HARD -> -3, WOW -> -4)

  def isValid(d: String) = Set(EASY, AVERAGE, HARD, VERY_HARD, WOW) contains d
}

/** Charlist subnamespace for base skill attributes and validation method */
object SkillBaseAttribute {
  val ST = "ST"
  val IQ = "IQ"
  val DX = "DX"
  val HT = "HT"
  val WILL = "Will"
  val PER = "Per"

  def isValid(a: String) = Set(ST, IQ, DX, HT, WILL, PER) contains a
}

/** Charlist subnamespace for technique difficulty strings and validation method */
object TechniqueDifficulty {
  val AVERAGE = "A"
  val HARD = "H"

  def isValid(d: String) = Set(AVERAGE, HARD) contains d
}

/** Charlist subnamespace for attribute strings and validation method */
object BonusToAttribute {
  val ST = "st"
  val DX = "dx"
  val IQ = "iq"
  val HT = "ht"
  val WILL = "will"
  val FC = "fright checks"
  val PER = "per"
  val VISION = "vision"
  val HEARING = "hearing"
  val TASTE_SMELL = "taste/smell"
  val TOUCH = "touch"
  val DODGE = "dodge"
  val PARRY = "parry"
  val BLOCK = "block"
  val BASIC_SPEED = "basic speed"
  val BASIC_MOVE = "basic move"
  val HP = "hp"
  val FP = "fp"
  val SM = "sm"

  def isValid(a: String) = Set(ST, DX, IQ, HT, WILL, FC, PER, VISION, HEARING, TASTE_SMELL, TOUCH, DODGE, PARRY, BLOCK,
    BASIC_SPEED, BASIC_MOVE, HP, FP, SM) contains a
}

/** Charlist subnamespace for stat name comparison strings and validation method */
object NameCompare {
  val ANY = "any"
  val IS = "is"
  val BEGINS = "begins with"

  def isValid(s: String) = Set(ANY, IS, BEGINS) contains s
}

/** Charlist subnamespace for reaction bonus' recognition frequency values and validation method */
object ReactionFrequency {
  val ALLWAYS = 16
  val OFTEN = 13
  val SOMETIMES = 10
  val OCCASIONALLY = 7

  val values: Map[Int, Double] = Map(ALLWAYS -> 1.0, OFTEN -> (2.0 / 3.0), SOMETIMES -> .5, OCCASIONALLY -> (1.0 / 3.0))

  def isValid(freq: Int) = Set(ALLWAYS, OFTEN, SOMETIMES, OCCASIONALLY) contains freq
}

/** Charlist subcontainer for character possessions, calculates total weights and cost and holds armor, weapons, and all
  * possessions items subcontainers. */
case class Equipment(
                      weapons: Seq[Weapon] = Seq(),
                      armor: Seq[Armor] = Seq(),
                      items: Seq[Item] = Seq(),
                      frontDR: DamageResistanceTotal = DamageResistanceTotal(),
                      rearDR: DamageResistanceTotal = DamageResistanceTotal(),
                      var totalDb: Int = 0,
                      var totalCost: Double = 0,
                      var totalCombWt: Double = 0,
                      var totalTravWt: Double = 0
                    ) {

  import ItemState._

  totalCost = 0
  weapons.foreach(totalCost += _.totalCost)
  armor.foreach(totalCost += _.cost)
  items.foreach(totalCost += _.totalCost)
  val comb = Set(READY, EQUIPPED, COMBAT)
  val equip = Set(READY, EQUIPPED)
  totalCombWt = 0
  weapons
    .withFilter(item => comb.contains(item.carried))
    .foreach(totalCombWt += _.totalWt)
  armor
    .withFilter(item => comb.contains(item.carried))
    .foreach(totalCombWt += _.wt)
  items
    .withFilter(item => comb.contains(item.carried))
    .foreach(totalCombWt += _.totalWt)
  totalTravWt = totalCombWt
  weapons
    .withFilter(_.carried == TRAVEL)
    .foreach(totalTravWt += _.totalWt)
  armor
    .withFilter(_.carried == TRAVEL)
    .foreach(totalTravWt += _.wt)
  items
    .withFilter(_.carried == TRAVEL)
    .foreach(totalTravWt += _.totalWt)
  totalDb = 0
  weapons
    .withFilter(item => equip.contains(item.carried) && !item.broken)
    .foreach(totalDb += _.db)
  armor
    .withFilter(item => equip.contains(item.carried) && !item.broken)
    .foreach(totalDb += _.db)
  armor
    .withFilter(_.front)
    .foreach(item => item.locations.foreach(frontDR.add(item.dr, item.ep, item.epi, _)))
  armor
    .withFilter(_.back)
    .foreach(item => item.locations.foreach(rearDR.add(item.dr, item.ep, item.epi, _)))
}

/** Charlist subcontainer for weapons list's element, calculates weapon weight and cost including ammunition if
  * applicable, holds all its stats and attacks it can make as subcontainers. */
case class Weapon(
                   name: String = "",
                   carried: String = ItemState.STASH,
                   attacksMelee: Seq[MeleeAttack] = Seq(),
                   attacksRanged: Seq[RangedAttack] = Seq(),
                   grips: Seq[String] = Seq(), // For future functionality
                   offHand: Boolean = false, // For future functionality
                   bulk: Int = 0,
                   block: Boolean = false,
                   db: Int = 0,
                   dr: Int = 0,
                   hp: Int = 1,
                   hpLeft: Int = 1,
                   broken: Boolean = false,
                   lc: Int = 5,
                   tl: Int = 0,
                   notes: String = "",
                   wt: Double = 0,
                   cost: Double = 0,
                   var totalWt: Double = 0,
                   var totalCost: Double = 0
                 ) {
  assert(ItemState.isValid(carried), s"invalid weapon's carrying state ($carried in $name)")
  assert(bulk <= 0, s"positive bulk value ($bulk in $name)")
  assert(db >= 0 && db < 4, s"weapon's defense bonus value out of bounds ($db in $name)")
  assert(dr >= 0, s"negative weapon's DR value ($dr in $name)")
  assert(hp >= 0, s"negative weapon's HP value ($hp in $name)")
  assert(hpLeft >= 0 && hpLeft <= hp, s"weapon's current HP value out of bounds ($hpLeft in $name)")
  assert(lc < 6 && lc >= 0, s"weapon's legality class value out of bounds ($lc in $name)")
  assert(tl >= 0 && tl < 13, s"weapon's tech level value out of bounds ($tl in $name)")
  assert(wt >= 0, s"negative weapon's weight value ($wt in $name)")
  assert(cost >= 0, s"negative weapon's cost value ($cost in $name)")
  totalWt = wt
  attacksRanged.foreach(totalWt += _.shots.totalWt)
  totalCost = cost
  attacksRanged.foreach(totalCost += _.shots.totalCost)
}

/** Charlist subcontainer for melee attack type's stats, holds damage stats subcontainers and produces parry stat
  * string. */
case class MeleeAttack(
                        name: String = "",
                        available: Boolean = false,
                        damage: MeleeDamage = MeleeDamage(),
                        followup: Seq[MeleeDamage] = Seq(),
                        linked: Seq[MeleeDamage] = Seq(),
                        skill: String = "",
                        spc: String = "",
                        parry: Int = 0,
                        parryType: String = "",
                        var parryString: String = "",
                        st: Int = 10,
                        hands: String = "",
                        reach: String = "",
                        notes: String = ""
                      ) {
  parryString = if (parryType == "No") parryType else "" + parry + parryType
  assert(st > 0, s"negative or 0 melee attack's min ST value ($st in $name)")
}

/** Charlist subcontainer for melee damage stat, holds damage string, calculated on core stats level */
case class MeleeDamage(
                        attackType: String = "",
                        dmgDice: Int = 0,
                        dmgMod: Int = 0,
                        armorDiv: Double = 1,
                        dmgType: String = DamageType.CRUSHING,
                        var dmgString: String = ""
                      ) {
  assert(AttackType.isValid(attackType), s"invalid melee damage's attack type ($attackType)")
  assert(dmgDice >= 0, s"melee damage's dice value is negative ($dmgDice)")
  assert(ArmorDivisor.isValid(armorDiv), s"invalid melee damage's armor divisor value ($armorDiv)")
  assert(DamageType.isValid(dmgType), s"invalid melee damage type ($dmgType)")

  def calcDmg(thr: (Int, Int), sw: (Int, Int), bonus: (Int, Int)) = {
    import AttackType._
    val dmg = attackType match {
      case x if x == THRUSTING => thr
      case x if x == SWINGING => sw
      case x if x == WEAPON => (0, 0)
    }
    var dice = dmgDice + dmg._1
    var mod = dmgMod + dmg._2 + bonus._1 * dice + bonus._2
    if (mod > 0) {
      dice += (mod / 3.5).toInt
      mod = (mod % 3.5).toInt
    }
    assert(dice > 0 || mod >= 0, s"invalid weapon's melee damage stats — resulting dice $dice, resulting mod $mod")
    import DamageType._
    dmgString = dmgType match {
      case t if t == SPECIAL => t
      case t if t == AFFLICTION => s"HT${if (dmgMod > 0) "+" + dmgMod else if (dmgMod < 0) dmgMod else ""}"
      case _ => "" + dice + "d" + (
        if (mod > 0) "+" + mod
        else if (mod < 0) "" + mod
        else ""
        ) + (
        if (armorDiv < 1) "(" + armorDiv + ")"
        else if (armorDiv > 1) "(" + armorDiv.toInt + ")"
        else ""
        ) + " " + dmgType
    }
    this
  }
}

/** Charlist subnamespace that holds melee attack types strings and validation method */
object AttackType {
  val THRUSTING = "thr"
  val SWINGING = "sw"
  val WEAPON = ""

  def isValid(s: String) = Set(THRUSTING, SWINGING, WEAPON).contains(s)
}

/** Charlist subcontainer for ranged attack's stats, holds damage, RoF, and shots subcontainers */
case class RangedAttack(
                         name: String = "",
                         available: Boolean = false, // For future functionality
                         damage: RangedDamage = RangedDamage(),
                         followup: Seq[RangedDamage] = Seq[RangedDamage](),
                         linked: Seq[RangedDamage] = Seq[RangedDamage](),
                         skill: String = "",
                         spc: String = "",
                         acc: Int = 0,
                         accMod: Int = 0,
                         rng: String = "",
                         rof: RangedRoF = RangedRoF(),
                         rcl: Int = 2,
                         shots: RangedShots = RangedShots(),
                         st: Int = 10,
                         hands: String = "",
                         malf: Int = 18,
                         notes: String = ""
                       ) {
  assert(acc >= 0, s"negative ranged attack's accuracy value ($acc in $name)")
  assert(accMod >= 0, s"negative ranged attack's scope bonus value ($accMod in $name)")
  assert(rcl > 0, s"negative or 0 ranged attack's recoil ($rcl in $name)")
  assert(st > 0, s"negative or 0 ranged attack's min ST value ($st in $name)")
  assert(malf < 19 && malf > 3, s"ranged attack's malfunction value is out of bounds ($malf in $name)")
}

/** Charlist subcontainer for ranged damage stat, calculates damage string */
case class RangedDamage(
                         dmgDice: Int = 0,
                         diceMult: Int = 1,
                         dmgMod: Int = 0,
                         armorDiv: Double = 1,
                         dmgType: String = DamageType.CRUSHING,
                         fragDice: Int = 0,
                         var dmgString: String = ""
                       ) {
  assert(dmgDice >= 0, s"ranged damage's dice value is negative ($dmgDice)")
  assert(diceMult > 0, s"ranged damage's dice multiplier is negative or 0 ($diceMult)")
  assert(ArmorDivisor.isValid(armorDiv), s"invalid ranged damage's armor divisor value ($armorDiv)")
  assert(DamageType.isValid(dmgType), s"invalid ranged damage type ($dmgType)")
  assert(fragDice >= 0, s"negative ranged damage's fragmentation dice value ($fragDice)")

  import DamageType._

  dmgString = dmgType match {
    case t if t == SPECIAL => t
    case t if t == AFFLICTION => s"HT${if (dmgMod > 0) "+" + dmgMod else if (dmgMod < 0) dmgMod else ""}"
    case _ => "" + dmgDice + "d" + (
      if (diceMult != 1) "x" + diceMult else ""
      ) + (
      if (dmgMod > 0) "+" + dmgMod else if (dmgMod < 0) "" + dmgMod else ""
      ) + (
      if (armorDiv < 1) "(" + armorDiv + ")" else if (armorDiv > 1) "(" + armorDiv.toInt + ")" else ""
      ) + " " + dmgType + (
      if (fragDice > 0) " [" + fragDice + "d]" else ""
      )
  }
}

/** Charlist subnamespace that holds armor divisors validation method */
object ArmorDivisor {
  def isValid(div: Double) = Set(0.1, 0.2, 0.5, 1, 2, 3, 5, 10, 100).contains(div)
}

/** Charlist subnamespace that holds damage types strings and validation method */
object DamageType {
  val CRUSHING = "cr"
  val CRUSHING_EXPLOSION = "cr ex"
  val CUTTING = "cut"
  val IMPALING = "imp"
  val PIERCING_SMALL = "pi-"
  val PIERCING = "pi"
  val PIERCING_LARGE = "pi+"
  val PIERCING_HUGE = "pi++"
  val BURNING = "burn"
  val BURNING_EXPLOSION = "burn ex"
  val TOXIC = "tox"
  val CORROSION = "cor"
  val AFFLICTION = "aff"
  val FATIGUE = "fat"
  val SPECIAL = "spec."

  def isValid(key: String) = Set(CRUSHING, CRUSHING_EXPLOSION, CUTTING, IMPALING, PIERCING_SMALL, PIERCING,
    PIERCING_LARGE, PIERCING_HUGE, BURNING, BURNING_EXPLOSION, TOXIC, CORROSION, AFFLICTION, FATIGUE, SPECIAL)
    .contains(key)
}

/** Charlist subcontainer for ranged attack's RoF stat, producing RoF string */
case class RangedRoF(
                      rof: Int = 1,
                      rofMult: Int = 1,
                      rofFA: Boolean = false,
                      rofJet: Boolean = false,
                      var rofString: String = ""
                    ) {
  assert(rof > 0, s"negative or 0 ranged attack's RoF ($rof)")
  assert(rofMult > 0, s"negative or 0 ranged attack's RoF multiplier ($rofMult)")
  assert(!(rofJet && rofFA), s"invalid ranged attack's RoF type: full auto with jet (true & true)")
  rofString = s"$rof${if (rofMult != 1) "x" + rofMult else ""}${if (rofFA) "!" else if (rofJet) " Jet" else ""}"
}

/** Charlist subcontainer for ranged attack shots stat, producing shots string and calculating carried ammunition
  * cost and weight */
case class RangedShots(
                        shots: Int = 1,
                        reload: String = "",
                        shotsLoaded: Int = 0,
                        shotsCarried: Int = 0,
                        shotWt: Double = 0,
                        shotCost: Double = 0,
                        var shotsString: String = "",
                        var totalWt: Double = 0,
                        var totalCost: Double = 0
                      ) {
  assert(shots >= 0, s"negative ranged attack's shots number ($shots)")
  assert(shotsLoaded >= 0 && shotsLoaded <= shots, s"ranged attack's shots left value out of bounds ($shotsLoaded)")
  assert(shotsCarried >= 0, s"negative ranged attack's shots carried value ($shotsCarried)")
  assert(shotWt >= 0, s"negative ranged attack's weight per shot value ($shotWt)")
  assert(shotCost >= 0, s"negative ranged attack's cost per shot value ($shotCost)")
  shotsString = s"${if (shots != 0) "" + shotsLoaded + "/" + shots else ""}$reload " +
    s"${if (shotsCarried != 0) shotsCarried else ""}"
  totalWt = (shotsCarried + shotsLoaded) * shotWt
  totalCost = (shotsCarried + shotsLoaded) * shotCost
}

/** Charlist subcontainer for armor list's element, holds its stats */
case class Armor(
                  name: String = "",
                  carried: String = ItemState.EQUIPPED,
                  db: Int = 0,
                  dr: Int = 0,
                  ep: Int = 0,
                  epi: Int = 0,
                  front: Boolean = true,
                  back: Boolean = true,
                  drType: String = DrType.HARD,
                  locations: Seq[String] = Seq(),
                  hp: Int = 1,
                  hpLeft: Int = 1,
                  broken: Boolean = false,
                  lc: Int = 5,
                  tl: Int = 0,
                  notes: String = "",
                  wt: Double = 0,
                  cost: Double = 0
                ) {
  assert(ItemState.isValid(carried), s"invalid armor's carrying state ($carried in $name)")
  assert(db >= 0 && db < 4, s"armor's defense bonus value out of bounds ($db in $name)")
  assert(dr >= 0, s"negative armor's DR value ($dr in $name)")
  assert(ep >= 0, s"negative armor's EP value ($ep in $name)")
  assert(epi >= 0, s"negative armor's EPi value ($epi in $name)")
  assert(front || back, s"armor covers neither front nor back (false & false in $name)")
  assert(DrType.isValid(drType), s"invalid armor's DR type string ($drType in $name)")
  assert(HitLocation.isValid(locations), s"invalid armor's locations string set ($locations in $name)")
  assert(hp >= 0, s"negative armor's HP value ($hp in $name)")
  assert(hpLeft >= 0 && hpLeft <= hp, s"armor's current HP value out of bounds ($hpLeft in $name)")
  assert(lc < 6 && lc >= 0, s"armor's legality class value out of bounds ($lc in $name)")
  assert(tl >= 0 && tl < 13, s"armor's tech level value out of bounds ($tl in $name)")
  assert(wt >= 0, s"negative armor's weight ($wt in $name)")
  assert(cost >= 0, s"negative armors's cost ($cost in $name)")
}

/** Charlist subnamespace that holds DR types strings and validation method */
object DrType {
  val HARD = "hard"
  val SOFT = "soft"
  val FIELD = "force field"
  val SKIN = "tough skin"

  def isValid(dr: String) = Set(HARD, SOFT, FIELD, SKIN).contains(dr)
}

/** Charlist subnamespace that holds hit locations strings and validation method */
object HitLocation {
  val EYES = "eyes"
  val SKULL = "skull"
  val FACE = "face"
  val HEAD = "head"
  val NECK = "neck"
  val LEG_RIGHT = "right leg"
  val LEG_LEFT = "left leg"
  val LEGS = "legs"
  val ARM_RIGHT = "right arm"
  val ARM_LEFT = "left arm"
  val ARMS = "arms"
  val CHEST = "chest"
  val VITALS = "vitals"
  val ABDOMEN = "abdomen"
  val GROIN = "groin"
  val TORSO = "torso"
  val HANDS = "hands"
  val HAND_LEFT = "left hand"
  val HAND_RIGHT = "right hand"
  val FEET = "feet"
  val FOOT_RIGHT = "right foot"
  val FOOT_LEFT = "left foot"
  val SKIN = "skin"
  val BODY = "body"

  def isValid(loc: Seq[String]) = {
    val all = Set(EYES, SKULL, FACE, HEAD, NECK, LEG_LEFT, LEG_RIGHT, LEGS, ARM_LEFT, ARM_RIGHT, ARMS, CHEST, VITALS,
      ABDOMEN, GROIN, TORSO, HANDS, HAND_LEFT, HAND_RIGHT, FEET, FOOT_LEFT, FOOT_RIGHT, SKIN, BODY)
    loc.forall(all.contains)
  }
}

/** Charlist subcontainer for items list's element, holds its stats and calculates element's total weight and cost */
case class Item(
                 name: String = "",
                 carried: String = ItemState.STASH,
                 dr: Int = 0,
                 hp: Int = 1,
                 hpLeft: Int = 1,
                 broken: Boolean = false,
                 lc: Int = 5,
                 tl: Int = 0,
                 notes: String = "",
                 wt: Double = 0,
                 cost: Double = 0,
                 n: Int = 1,
                 var totalWt: Double = 0,
                 var totalCost: Double = 0
               ) {
  assert(ItemState.isValid(carried), s"invalid item's carrying state ($carried in $name)")
  assert(dr >= 0, s"negative item's DR value ($dr in $name)")
  assert(hp >= 0, s"negative item's HP value ($hp in $name)")
  assert(hpLeft >= 0 && hpLeft <= hp, s"item's current HP value out of bounds ($hpLeft in $name)")
  assert(lc < 6 && lc >= 0, s"item's legality class value out of bounds ($lc in $name)")
  assert(tl >= 0 && tl < 13, s"item's tech level value out of bounds ($tl in $name)")
  assert(wt >= 0, s"negative item's weight ($wt in $name)")
  assert(cost >= 0, s"negative item's cost ($cost in $name)")
  assert(n >= 0, s"negative item's quantity ($n in $name)")
  totalWt = wt * n
  totalCost = cost * n
}

/** Charlist subnamespace that holds item carrying states strings and validation method */
object ItemState {
  val READY = "Ready"
  val EQUIPPED = "Equipped"
  val COMBAT = "Combat"
  val TRAVEL = "Travel"
  val STASH = "Stash"

  def isValid(key: String) = Set(READY, EQUIPPED, COMBAT, TRAVEL, STASH).contains(key)
}

/** Charlist subcontainer for total DR coverage stats, holds its calculation method */
case class DamageResistanceTotal(
                                  skull: HitLocationDR = HitLocationDR(),
                                  eyes: HitLocationDR = HitLocationDR(),
                                  face: HitLocationDR = HitLocationDR(),
                                  neck: HitLocationDR = HitLocationDR(),
                                  armLeft: HitLocationDR = HitLocationDR(),
                                  armRight: HitLocationDR = HitLocationDR(),
                                  handLeft: HitLocationDR = HitLocationDR(),
                                  handRight: HitLocationDR = HitLocationDR(),
                                  chest: HitLocationDR = HitLocationDR(),
                                  vitals: HitLocationDR = HitLocationDR(),
                                  abdomen: HitLocationDR = HitLocationDR(),
                                  groin: HitLocationDR = HitLocationDR(),
                                  legLeft: HitLocationDR = HitLocationDR(),
                                  legRight: HitLocationDR = HitLocationDR(),
                                  footLeft: HitLocationDR = HitLocationDR(),
                                  footRight: HitLocationDR = HitLocationDR()
                                ) {
  def add(dr: Int, ep: Int, epi: Int, loc: String) = {
    import HitLocation._
    val locs = loc match {
      case s if s == EYES => Seq(eyes)
      case s if s == SKULL => Seq(skull)
      case s if s == FACE => Seq(face)
      case s if s == HEAD => Seq(skull, face)
      case s if s == NECK => Seq(neck)
      case s if s == LEG_RIGHT => Seq(legRight)
      case s if s == LEG_LEFT => Seq(legLeft)
      case s if s == LEGS => Seq(legLeft, legRight)
      case s if s == ARM_RIGHT => Seq(armRight)
      case s if s == ARM_LEFT => Seq(armLeft)
      case s if s == ARMS => Seq(armLeft, armRight)
      case s if s == CHEST => Seq(chest)
      case s if s == VITALS => Seq(vitals)
      case s if s == ABDOMEN => Seq(abdomen)
      case s if s == GROIN => Seq(groin)
      case s if s == TORSO => Seq(chest, abdomen, vitals)
      case s if s == HANDS => Seq(handLeft, handRight)
      case s if s == HAND_LEFT => Seq(handLeft)
      case s if s == HAND_RIGHT => Seq(handRight)
      case s if s == FEET => Seq(footLeft, footRight)
      case s if s == FOOT_LEFT => Seq(footLeft)
      case s if s == FOOT_RIGHT => Seq(footRight)
      case s if s == SKIN => Seq(skull, face, neck, armLeft, armRight, handLeft, handRight, chest, vitals, abdomen,
        groin, legLeft, legRight, footLeft, footRight)
      case s if s == BODY => Seq(skull, eyes, face, neck, armLeft, armRight, handRight, handLeft, chest, vitals,
        abdomen, groin, legRight, legLeft, footRight, footLeft)
    }
    locs.foreach(_.increment(dr, ep, epi))
    this
  }
}

/** Charlist subcontainer for hit location DR stats, holds its calculation method */
case class HitLocationDR(
                          var dr: Int = 0,
                          var ep: Int = 0,
                          var epi: Int = 0
                        ) {
  dr = 0
  ep = 0
  epi = 0

  def increment(drDelta: Int, epDelta: Int, epiDelta: Int) = {
    dr += drDelta
    ep += epDelta
    epi += epiDelta
    this
  }
}

/** Charlist subcontainer for conditions switches */
case class Conditions(
                       unconscious: Boolean = false,
                       mortallyWounded: Boolean = false,
                       dead: Boolean = false,
                       shock: Int = 0,
                       stunned: Boolean = false,
                       afflictions: Afflictions = Afflictions(),
                       cripplingInjuries: Seq[String] = Seq(),
                       posture: String = Posture.STANDING,
                       closeCombat: Boolean = false,
                       grappled: Boolean = false,
                       pinned: Boolean = false,
                       sprinting: Boolean = false,
                       mounted: Boolean = false
                     ) {
  assert(shock >= 0 && shock <= 8, s"shock value out of bounds ($shock)")
  assert(Posture.isValid(posture), s"invalid posture string ($posture)")
}

/** Charlist subcontainer for afflictions switches */
case class Afflictions(
                        coughing: Boolean = false,
                        drowsy: Boolean = false,
                        drunk: Boolean = false,
                        euphoria: Boolean = false,
                        nauseated: Boolean = false,
                        pain: Boolean = false,
                        tipsy: Boolean = false,
                        agony: Boolean = false,
                        choking: Boolean = false,
                        daze: Boolean = false,
                        ecstasy: Boolean = false,
                        hallucinating: Boolean = false,
                        paralysis: Boolean = false,
                        retching: Boolean = false,
                        seizure: Boolean = false,
                        coma: Boolean = false,
                        heartAttack: Boolean = false
                      )

/** Charlist subnamespace for posture strings and validation method */
object Posture {
  val STANDING = "Standing"
  val CROUCHING = "Crouching"
  val SITTING = "Sitting"
  val KNEELING = "Kneeling"
  val CRAWLING = "Crawling"
  val LYING_PRONE = "Prone"
  val LYING_FACE_UP = "On Back"

  def isValid(posture: String) =
    Set(STANDING, CROUCHING, SITTING, KNEELING, CRAWLING, LYING_PRONE, LYING_FACE_UP)
      .contains(posture)
}

object Charlist {

  import play.api.libs.json.Json

  implicit val afflictionsFormat = Json.format[Afflictions]
  implicit val conditionsFormat = Json.format[Conditions]
  implicit val itemFormat = Json.format[Item]
  implicit val armorElementFormat = Json.format[Armor]
  implicit val rangedShotsFormat = Json.format[RangedShots]
  implicit val rangedRoFFormat = Json.format[RangedRoF]
  implicit val rangedDamageFormat = Json.format[RangedDamage]
  implicit val rangedAttackFormat = Json.format[RangedAttack]
  implicit val meleeDamageFormat = Json.format[MeleeDamage]
  implicit val meleeAttackFormat = Json.format[MeleeAttack]
  implicit val weaponFormat = Json.format[Weapon]
  implicit val hitLocationFormat = Json.format[HitLocationDR]
  implicit val damageResistanceTotalFormat = Json.format[DamageResistanceTotal]
  implicit val equipmentFormat = Json.format[Equipment]
  implicit val bonusReactionFormat = Json.format[BonusReaction]
  implicit val bonusAttributeCostFormat = Json.format[BonusAttributeCost]
  implicit val bonusDRFormat = Json.format[BonusDR]
  implicit val bonusDamageFormat = Json.format[BonusDamage]
  implicit val bonusSkillFormat = Json.format[BonusSkill]
  implicit val bonusAttributeFormat = Json.format[BonusAttribute]
  implicit val techniqueFormat = Json.format[Technique]
  implicit val skillFormat = Json.format[Skill]
  implicit val traitModifierFormat = Json.format[TraitModifier]
  implicit val traitFormat = Json.format[Trait]
  implicit val reactionModFormat = Json.format[ReactionMod]
  implicit val reactionFormat = Json.format[Reaction]
  implicit val statIntFormat = Json.format[StatInt]
  implicit val statFracFormat = Json.format[StatFrac]
  implicit val statPointsFormat = Json.format[StatPoints]
  implicit val statVarsFormat = Json.format[StatVars]
  implicit val statsFormat = Json.format[Stats]
  implicit val descriptionFormat = Json.format[Description]
  implicit val characterPointsFormat = Json.format[CharacterPoints]
  implicit val charlistFormat = Json.format[Charlist]

}