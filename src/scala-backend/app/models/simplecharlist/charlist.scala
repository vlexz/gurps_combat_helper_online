package models.simplecharlist

/**
  * Created by crimson on 9/23/16.
  */
case class Charlist(
                     _id: String = "",
                     timestamp: String = "",
                     player: String = "",
                     access: Seq[String] = Seq(), // For future functionality
                     name: String = "New Character",
                     cp: CharacterPoints = CharacterPoints(),
                     description: Description = Description(),
                     stats: Stats = Stats(),
                     statVars: StatVars = StatVars(),
                     reactions: Seq[Reaction] = Seq(),
                     traits: Seq[Trait] = Seq(
                       Trait(name = "Skull", drBonuses = Seq(BonusDR(Seq(HitLocation.SKULL), dr = 2)))
                     ),
                     skills: Seq[Skill] = Seq(),
                     techniques: Seq[Technique] = Seq(),
                     equip: Equipment = Equipment(),
                     conditions: Conditions = Conditions(),
                     var api: String = "") {
  api = "0.1.1"

  stats.st.base = 10
  stats.dx.base = 10
  stats.iq.base = 10
  stats.ht.base = 10
  stats.will.base = 10
  stats.per.base = 10
  stats.liftSt.base = stats.st.value
  stats.strikeSt.base = stats.st.value
  stats.hp.base = stats.st.value
  stats.fp.base = stats.ht.value
  stats.basicSpeed.base = (stats.dx.value + stats.ht.value) * .25
  stats.basicMove.base = stats.basicSpeed.value.toInt
  statVars.frightCheck = math.min(13, stats.will.value)
  statVars.vision = stats.per.value
  statVars.hearing = stats.per.value
  statVars.tasteSmell = stats.per.value
  statVars.touch = stats.per.value
  stats.hp.calcCompr()
  stats.fp.calcCompr()
  statVars.bl = (stats.liftSt.value * stats.liftSt.value * .2).toInt
  statVars
    .calcEncumbrance(equip.totalCombWt, equip.totalTravWt)
    .calcMove(
      stats.basicMove.value,
      stats.basicSpeed.value.toInt + 3,
      stats.hp.collapsing || stats.fp.collapsing)

  {
    import SkillBaseAttribute._

    for (s <- skills) {
      val attrVal = s.attr match {
        case ST => stats.st.value
        case DX => stats.dx.value
        case IQ => stats.iq.value
        case HT => stats.ht.value
        case WILL => stats.will.value
        case PER => stats.per.value
      }
      s.calcLvl(attrVal, statVars.travelEncumbrance)
    }
    for (t <- techniques) {
      def sFltr(s: Skill) = s.name == t.skill && (if (t.spc != "") s.spc == t.spc else true)
      val l = skills collectFirst { case s if sFltr(s) => s.lvl } getOrElse 0
      t.calcLvl(l)
    }

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
    val dStr = (d: Int, m: Int) => s"${d}d${if (m < 0) m else if (m > 0) "+" + m else ""}"
    statVars.thrDmg = dStr(thr._1, thr._2)
    statVars.swDmg = dStr(sw._1, sw._2)
    for (weapon <- equip.weapons; mAttack <- weapon.attacksMelee) {
      mAttack.damage.calcDmg(thr, sw, (0, 0))
      for (m <- mAttack.followup ++ mAttack.linked) m.calcDmg(thr, sw, (0, 0))
    }

    stats.st.calcCp(10)
    stats.dx.calcCp(20)
    stats.iq.calcCp(20)
    stats.ht.calcCp(10)
    stats.will.calcCp(5)
    stats.per.calcCp(5)
    stats.liftSt.calcCp(3)
    stats.strikeSt.calcCp(5)
    stats.hp.calcCp(2)
    stats.fp.calcCp(3)
    stats.basicSpeed.calcCp(20)
    stats.basicMove.calcCp(5)
    cp.stats = stats.cp
    val tCp = traits map (_.cp) partition (_ > 0)
    cp.adv = tCp._1.sum
    cp.dis = tCp._2.sum
    cp.skills = skills.map(_.cp).sum + techniques.map(_.cp).sum
    cp.unspent = cp.cp - cp.skills - cp.stats - cp.adv - cp.dis
  }

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
                            var unspent: Int = 0)

/** Charlist subcontainer for character description */
case class Description(
                        age: String = "",
                        height: String = "",
                        weight: String = "",
                        portrait: String = "",
                        bio: String = "")

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
                  basicSpeed: StatDouble = StatDouble(),
                  basicMove: StatInt = StatInt()) {
  def cp: Int = Seq(st, dx, iq, ht, will, per, liftSt, strikeSt, hp, fp, basicSpeed, basicMove).foldLeft(0)(_ + _.cp)
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
                     sm: Int = 0) {

  import Charlist.rndUp

  def calcEncumbrance(combWt: Double, travWt: Double): StatVars = {
    assert(combWt <= bl * 10, s"combat encumbrance value is over 10 times basic lift ($combWt, $bl)")
    assert(travWt <= bl * 10, s"travel encumbrance value is over 10 times basic lift ($travWt, $bl)")
    val encLvl = (x: Double) => x match {
      case _ if x <= 1 => 0
      case _ if x <= 2 => 1
      case _ if x <= 3 => 2
      case _ if x <= 6 => 3
      case _ if x <= 10 => 4
    }
    combatEncumbrance = encLvl(combWt / bl)
    travelEncumbrance = encLvl(travWt / bl)
    this
  }

  def calcMove(basicMove: Int, basicDodge: Int, compromised: Boolean): StatVars = {
    val compr = if (compromised) .5 else 1
    def calc(enc: Int) = rndUp((basicMove * .2 * (5 - enc)).toInt * compr)
    combMove = calc(combatEncumbrance)
    travMove = calc(travelEncumbrance)
    dodge = rndUp((basicDodge - combatEncumbrance) * compr)
    this
  }
}

/** Charlist subcontainer for character attribute storage */
sealed abstract class Stat[A <: AnyVal](implicit x: scala.math.Numeric[A]) {

  import Charlist.rndUp
  import x._

  val delta: A
  var base: A
  val bonus: A
  val cpMod: Int
  var cp: Int

  def value: A = delta + base + bonus

  def calcCp(cost: Int): Stat[A] = {
    cp = rndUp(delta.toDouble * cost * math.max(.2, cpMod * .01))
    this
  }
}

case class StatInt(
                    delta: Int = 0,
                    var base: Int = 0,
                    bonus: Int = 0,
                    cpMod: Int = 100,
                    var cp: Int = 0)
  extends Stat[Int]

case class StatDouble(
                       delta: Double = 0,
                       var base: Double = 0,
                       bonus: Double = 0,
                       cpMod: Int = 100,
                       var cp: Int = 0)
  extends Stat[Double]

/** Charlist subcontainer for HP and FP attributes' stats */
case class StatPoints(
                       delta: Int = 0,
                       var base: Int = 0,
                       bonus: Int = 0,
                       cpMod: Int = 100,
                       var cp: Int = 0,
                       var lost: Int = 0,
                       var compromised: Boolean = false,
                       var collapsing: Boolean = false)
  extends Stat[Int] {
  if (lost < 0) lost = 0

  def calcCompr(): StatPoints = {
    compromised = value * (2.0 / 3.0) < lost
    collapsing = value <= lost
    this
  }
}

/** Charlist subcontainer for NPC reactions list's element stats */
case class Reaction(affected: String = "", modifiers: Seq[ReactionMod] = Seq())

/** Charlist subcontainer for NPC reaction modifiers list's element stats */
case class ReactionMod(freq: Int = 16, mod: Int = 0, notes: String = "")

/** Charlist subcontainer for features list's element stats */
case class Trait(
                  name: String = "",
                  types: Seq[String] = Seq(),
                  category: String = "",
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
                  cp: Int = 0)

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
                          affects: String = "",
                          costType: String = "",
                          level: Int = 0,
                          cost: Double = 0)

/** Charlist subcontainer for skills list's element stats, calculates its relative level */
case class Skill(
                  name: String = "",
                  spc: String = "",
                  var tl: Int = 0,
                  skillString: String = "",
                  var attr: String = SkillBaseAttribute.DX,
                  var diff: String = SkillDifficulty.EASY,
                  defaults: Seq[String] = Seq(), // For future functionality
                  prerequisites: Seq[String] = Seq(), // For future functionality
                  dmgBonuses: Seq[BonusDamage] = Seq(),
                  reactBonuses: Seq[BonusReaction] = Seq(),
                  encumbr: Boolean = false,
                  bonus: Int = 0,
                  categories: Seq[String] = Seq(),
                  notes: String = "",
                  var cp: Int = 0,
                  relLvl: Int = 0,
                  var lvl: Int = 0) {
  if (tl < 0) tl = 0 else if (tl > 12) tl = 12
  if (SkillBaseAttribute canBe attr) () else attr = SkillBaseAttribute.DX
  if (SkillDifficulty canBe diff) () else diff = SkillDifficulty.EASY

  def calcLvl(attrVal: Int, enc: Int): Skill = {
    val l = relLvl - (SkillDifficulty values diff) + 1
    cp = l * 4 - (if (l > 2) 8 else if (l > 1) 6 else if (l > 0) 3 else l * 4)
    lvl = attrVal + relLvl - (if (encumbr) enc else 0) + bonus
    this
  }
}

/** Charlist subcontainer for techniques list's element stats, calculates its relative level */
case class Technique(
                      name: String = "",
                      skill: String = "",
                      spc: String = "",
                      var tchString: String = "",
                      var diff: String = SkillDifficulty.AVERAGE,
                      style: String = "",
                      defLvl: Int = 0,
                      var maxLvl: Int = 100,
                      notes: String = "",
                      var cp: Int = 0,
                      var relLvl: Int = 0,
                      var lvl: Int = 0) {
  if (SkillDifficulty techniqueCanBe diff) () else diff = SkillDifficulty.AVERAGE
  if (maxLvl < defLvl) maxLvl = defLvl
  if (relLvl < defLvl) relLvl = defLvl else if (relLvl > maxLvl) relLvl = maxLvl
  tchString = s"$name ($skill${if (spc != "") " (" + spc + ")"})"
  cp = relLvl - defLvl + (if (diff == SkillDifficulty.HARD && relLvl > defLvl) 1 else 0)

  def calcLvl(skill: Int): Technique = {
    lvl = skill + relLvl
    this
  }
}

/** Charlist subcontainer for attribute bonuses list's element stats */
case class BonusAttribute(attr: String = "", perLvl: Boolean = true, bonus: Int = 0)

/** Charlist subcontainer for skill bonuses list's element stats */
case class BonusSkill(
                       skill: String = "",
                       skillCompare: String = "",
                       spc: String = "",
                       spcCompare: String = "",
                       perLvl: Boolean = true,
                       bonus: Int = 0)

/** Charlist subcontainer for damage bonuses list's element stats */
case class BonusDamage(
                        skill: String = "",
                        skillCompare: String = "",
                        spc: String = "",
                        spcCompare: String = "",
                        relSkill: Int = 0,
                        perDie: Boolean = false,
                        bonus: Int = 0)

/** Charlist subcontainer for DR bonuses list's element stats */
case class BonusDR(
                    locations: Seq[String] = Seq(),
                    perLvl: Boolean = true,
                    front: Boolean = true,
                    back: Boolean = true,
                    dr: Int = 0,
                    ep: Int = 0,
                    epi: Int = 0)

/** Charlist subcontainer for attribute cost modifiers list's element stats */
case class BonusAttributeCost(attr: String = "", cost: Int = 0)

/** Charlist subcontainer for reaction bonuses list's element stats */
case class BonusReaction(
                          affected: String = "",
                          reputation: Boolean = false,
                          perLvl: Boolean = false,
                          freq: Int = 16,
                          bonus: Int = 0,
                          notes: String = "")

/** Charlist subnamespace for trait type strings and validation method */
object TraitType {
  val MENTAL = "Mental"
  val PHYSICAL = "Physical"
  val SOCIAL = "Social"
  val MUNDANE = "Mundane"
  val EXOTIC = "Exotic"
  val SUPER = "Supernatural"
  val canBe = (t: Seq[String]) => t forall Set(MENTAL, PHYSICAL, SOCIAL, MUNDANE, EXOTIC, SUPER)
}

/** Charlist subnamespaces for trait category strings and validation method */
object TraitCategory {
  val ADVANTAGE = "Advantage"
  val DISADVANTAGE = "Disadvantage"
  val PERK = "Perk"
  val QUIRK = "Quirk"
  val LANGUAGE = "Language"
  val TALENT = "Talent"
  val canBe = (c: String) => Set(ADVANTAGE, DISADVANTAGE, PERK, QUIRK, LANGUAGE, TALENT)(c)
}

/** Charlist subnamespace for trait modifier cost effect strings and validation method */
object TraitModifierAffects {
  val TOTAL = "total"
  val BASE = "base"
  val LEVELS = "levels"
  val canBe = (a: String) => Set(TOTAL, BASE, LEVELS)(a)
}

/** Charlist subnamespace for trait modifier cost type strings and validation method */
object TraitModifierCostType {
  val PERCENT = "percent"
  val LEVEL = "percent per level"
  val POINTS = "points"
  val MULTIPLIER = "multiplier"
  val canBe = (t: String) => Set(PERCENT, LEVEL, POINTS, MULTIPLIER)(t)
}

/** Charlist subnamespace for skill difficulties strings and validation method */
object SkillDifficulty {
  val EASY = "E"
  val AVERAGE = "A"
  val HARD = "H"
  val VERY_HARD = "VH"
  val WOW = "W"
  val values: Map[String, Int] = Map(EASY -> 0, AVERAGE -> -1, HARD -> -2, VERY_HARD -> -3, WOW -> -4)
  val canBe = (d: String) => Set(EASY, AVERAGE, HARD, VERY_HARD, WOW)(d)
  val techniqueCanBe = (d: String) => Set(AVERAGE, HARD)(d)
}

/** Charlist subnamespace for base skill attributes and validation method */
object SkillBaseAttribute {
  val ST = "ST"
  val IQ = "IQ"
  val DX = "DX"
  val HT = "HT"
  val WILL = "Will"
  val PER = "Per"
  val canBe = (a: String) => Set(ST, IQ, DX, HT, WILL, PER)(a)
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
  val canBe = (a: String) => Set(ST, DX, IQ, HT, WILL, FC, PER, VISION, HEARING, TASTE_SMELL, TOUCH, DODGE, PARRY,
    BLOCK, BASIC_SPEED, BASIC_MOVE, HP, FP, SM)(a)
}

/** Charlist subnamespace for stat name comparison strings and validation method */
object NameCompare {
  val ANY = "any"
  val IS = "is"
  val BEGINS = "begins with"
  val canBe = (s: String) => Set(ANY, IS, BEGINS)(s)
}

/** Charlist subnamespace for reaction bonus' recognition frequency values and validation method */
object ReactionFrequency {
  val ALLWAYS = 16
  val OFTEN = 13
  val SOMETIMES = 10
  val OCCASIONALLY = 7
  val values: Map[Int, Double] = Map(ALLWAYS -> 1.0, OFTEN -> (2.0 / 3.0), SOMETIMES -> .5, OCCASIONALLY -> (1.0 / 3.0))
  val canBe = (freq: Int) => Set(ALLWAYS, OFTEN, SOMETIMES, OCCASIONALLY)(freq)
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
                      var totalTravWt: Double = 0) {

  import ItemState._

  totalCost = (weapons ++ armor ++ items).foldLeft(0.0)(_ + _.totalCost)

  private def weight(f: String => Boolean) =
    (for {p <- weapons ++ armor ++ items; if f(p.carried)} yield p.totalWt).sum

  totalCombWt = weight(Set(READY, EQUIPPED, COMBAT))
  totalTravWt = totalCombWt + weight(_ == TRAVEL)
  private val equip = (p: Possession) => Set(READY, EQUIPPED)(p.carried) && !p.broken
  totalDb = (weapons.withFilter(equip).map(_.db) ++ armor.withFilter(equip).map(_.db)).sum
  for (a <- armor; l <- a.locations) {
    if (a.front) frontDR.add(a.dr, a.ep, a.epi, l)
    if (a.back) rearDR.add(a.dr, a.ep, a.epi, l)
  }
}

sealed abstract class Possession {
  var carried: String
  val broken: Boolean

  def totalCost: Double

  def totalWt: Double
}

/** Charlist subcontainer for weapons list's element, calculates weapon weight and cost including ammunition if
  * applicable, holds all its stats and attacks it can make as subcontainers. */
case class Weapon(
                   name: String = "",
                   var carried: String = ItemState.STASH,
                   attacksMelee: Seq[MeleeAttack] = Seq(),
                   attacksRanged: Seq[RangedAttack] = Seq(),
                   grips: Seq[String] = Seq(), // For future functionality
                   offHand: Boolean = false, // For future functionality
                   var bulk: Int = 0,
                   block: Boolean = false,
                   db: Int = 0,
                   var dr: Int = 0,
                   var hp: Int = 1,
                   var hpLeft: Int = 1,
                   broken: Boolean = false,
                   var lc: Int = 5,
                   var tl: Int = 0,
                   notes: String = "",
                   var wt: Double = 0,
                   var cost: Double = 0,
                   var totalWt: Double = 0,
                   var totalCost: Double = 0)
  extends Possession {
  if (ItemState canBe carried) () else carried = ItemState.STASH
  if (bulk > 0) bulk = 0
  if (dr < 0) dr = 0
  if (hp < 0) hp = 0
  if (hpLeft < 0) hpLeft = 0 else if (hpLeft > hp) hpLeft = hp
  if (lc > 5) lc = 5 else if (lc < 0) lc = 0
  if (tl < 0) tl = 0 else if (tl > 12) tl = 12
  if (wt < 0) wt = 0
  if (cost < 0) cost = 0
  totalWt = wt + attacksRanged.map(_.shots.totalWt).sum
  totalCost = cost + attacksRanged.map(_.shots.totalCost).sum
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
                        var st: Int = 10,
                        hands: String = "",
                        reach: String = "",
                        notes: String = "") {
  parryString = if (parryType == "No") parryType else "" + parry + parryType
  if (st <= 0) st = 1
}

/** Charlist subcontainer for melee damage stat, holds damage string, calculated on core stats level */
case class MeleeDamage(
                        var attackType: String = AttackType.WEAPON,
                        var dmgDice: Int = 0,
                        dmgMod: Int = 0,
                        var armorDiv: Double = 1,
                        var dmgType: String = DamageType.CRUSHING,
                        var dmgString: String = "") {
  if (AttackType canBe attackType) () else attackType = AttackType.WEAPON
  if (dmgDice < 0) dmgDice = 0
  if (ArmorDivisor canBe armorDiv) () else armorDiv = 1
  if (DamageType canBe dmgType) () else dmgType = DamageType.CRUSHING

  def calcDmg(thr: (Int, Int), sw: (Int, Int), bonus: (Int, Int)): MeleeDamage = {
    import AttackType._
    val (mDice, mMod) = attackType match {
      case THRUSTING => thr
      case SWINGING => sw
      case WEAPON => (0, 0)
    }
    var dice = dmgDice + mDice
    var mod = dmgMod + mMod + bonus._1 * dice + bonus._2
    if (mod > 0) {
      dice += (mod / 3.5).toInt
      mod = (mod % 3.5).toInt
    }
    assert(dice > 0 || mod >= 0, s"invalid weapon's melee damage stats — resulting dice $dice, resulting mod $mod")
    import DamageType._
    dmgString = dmgType match {
      case SPECIAL => dmgType
      case AFFLICTION => s"HT${if (dmgMod > 0) "+" + dmgMod else if (dmgMod < 0) dmgMod else ""}"
      case _ => s"${dice}d${if (mod > 0) "+" + mod else if (mod < 0) mod else ""}" +
        s"${if (armorDiv < 1) "(" + armorDiv + ")" else if (armorDiv > 1) "(" + armorDiv.toInt + ")" else ""} $dmgType"
    }
    this
  }
}

/** Charlist subnamespace that holds melee attack types strings and validation method */
object AttackType {
  val THRUSTING = "thr"
  val SWINGING = "sw"
  val WEAPON = ""
  val canBe = (s: String) => Set(THRUSTING, SWINGING, WEAPON)(s)
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
                         var acc: Int = 0,
                         var accMod: Int = 0,
                         rng: String = "",
                         rof: RangedRoF = RangedRoF(),
                         var rcl: Int = 2,
                         shots: RangedShots = RangedShots(),
                         var st: Int = 10,
                         hands: String = "",
                         var malf: Int = 18,
                         notes: String = "") {
  if (acc < 0) acc = 0
  if (accMod < 0) accMod = 0
  if (rcl <= 0) rcl = 1
  if (st <= 0) st = 1
  if (malf > 18) malf = 18 else if (malf < 4) malf = 4
}

/** Charlist subcontainer for ranged damage stat, calculates damage string */
case class RangedDamage(
                         var dmgDice: Int = 0,
                         var diceMult: Int = 1,
                         dmgMod: Int = 0,
                         var armorDiv: Double = 1,
                         var dmgType: String = DamageType.CRUSHING,
                         var fragDice: Int = 0,
                         var dmgString: String = "") {
  if (dmgDice < 0) dmgDice = 0
  if (diceMult <= 0) diceMult = 1
  if (ArmorDivisor canBe armorDiv) () else armorDiv = 1
  if (DamageType canBe dmgType) () else dmgType = DamageType.CRUSHING
  if (fragDice < 0) fragDice = 0

  import DamageType._

  dmgString = dmgType match {
    case SPECIAL => dmgType
    case AFFLICTION => s"HT${if (dmgMod > 0) "+" + dmgMod else if (dmgMod < 0) dmgMod else ""}"
    case _ => s"${dmgDice}d${if (diceMult != 1) "x" + diceMult else ""}" +
      s"${if (dmgMod > 0) "+" + dmgMod else if (dmgMod < 0) dmgMod else ""}" +
      s"${if (armorDiv < 1) "(" + armorDiv + ")" else if (armorDiv > 1) "(" + armorDiv.toInt + ")" else ""}" +
      s" $dmgType${if (fragDice > 0) " [" + fragDice + "d]" else ""}"
  }
}

/** Charlist subnamespace that holds armor divisors validation method */
object ArmorDivisor {
  val canBe = (div: Double) => Set(0.1, 0.2, 0.5, 1, 2, 3, 5, 10, 100)(div)
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
  val canBe = (key: String) => Set(CRUSHING, CRUSHING_EXPLOSION, CUTTING, IMPALING, PIERCING_SMALL, PIERCING,
    PIERCING_LARGE, PIERCING_HUGE, BURNING, BURNING_EXPLOSION, TOXIC, CORROSION, AFFLICTION, FATIGUE, SPECIAL)(key)
}

/** Charlist subcontainer for ranged attack's RoF stat, producing RoF string */
case class RangedRoF(
                      var rof: Int = 1,
                      var rofMult: Int = 1,
                      rofFA: Boolean = false,
                      rofJet: Boolean = false,
                      var rofString: String = "") {
  if (rof <= 0) rof = 1
  if (rofMult <= 0) rofMult = 1
  rofString = s"$rof${if (rofMult != 1) "x" + rofMult else ""}${if (rofFA) "!" else if (rofJet) " Jet" else ""}"
}

/** Charlist subcontainer for ranged attack shots stat, producing shots string and calculating carried ammunition
  * cost and weight */
case class RangedShots(
                        var shots: Int = 1,
                        reload: String = "",
                        var shotsLoaded: Int = 0,
                        var shotsCarried: Int = 0,
                        var shotWt: Double = 0,
                        var shotCost: Double = 0,
                        var shotsString: String = "",
                        var totalWt: Double = 0,
                        var totalCost: Double = 0) {
  if (shots < 0) shots = 0
  if (shotsLoaded < 0) shotsLoaded = 0 else if (shotsLoaded > shots) shotsLoaded = shots
  if (shotsCarried < 0) shotsCarried = 0
  if (shotWt < 0) shotWt = 0
  if (shotCost < 0) shotCost = 0
  shotsString = s"${if (shots != 0) "" + shotsLoaded + "/" + shots else ""}$reload " +
    s"${if (shotsCarried != 0) shotsCarried else ""}"
  totalWt = (shotsCarried + shotsLoaded) * shotWt
  totalCost = (shotsCarried + shotsLoaded) * shotCost
}

/** Charlist subcontainer for armor list's element, holds its stats */
case class Armor(
                  name: String = "",
                  var carried: String = ItemState.EQUIPPED,
                  var db: Int = 0,
                  var dr: Int = 0,
                  var ep: Int = 0,
                  var epi: Int = 0,
                  var front: Boolean = true,
                  back: Boolean = true,
                  var drType: String = DrType.HARD,
                  var locations: Seq[String] = Seq(),
                  var hp: Int = 1,
                  var hpLeft: Int = 1,
                  broken: Boolean = false,
                  var lc: Int = 5,
                  var tl: Int = 0,
                  notes: String = "",
                  var wt: Double = 0,
                  var cost: Double = 0)
  extends Possession {
  if (ItemState canBe carried) () else carried = ItemState.EQUIPPED
  if (db < 0) db = 0 else if (db > 3) db = 3
  if (dr < 0) dr = 0
  if (ep < 0) ep = 0
  if (epi < 0) epi = 0
  if (!front && !back) front = true
  if (DrType canBe drType) () else drType = DrType.HARD
  if (HitLocation canBe locations) () else locations = Seq(HitLocation.CHEST)
  if (hp < 0) hp = 0
  if (hpLeft < 0) hpLeft = 0 else if (hpLeft > hp) hpLeft = hp
  if (lc > 5) lc = 5 else if (lc < 0) lc = 0
  if (tl < 0) tl = 0 else if (tl > 12) tl = 12
  if (wt < 0) wt = 0
  if (cost < 0) cost = 0

  override def totalCost: Double = cost

  override def totalWt: Double = wt
}

/** Charlist subnamespace that holds DR types strings and validation method */
object DrType {
  val HARD = "hard"
  val SOFT = "soft"
  val FIELD = "force field"
  val SKIN = "tough skin"
  val canBe = (dr: String) => Set(HARD, SOFT, FIELD, SKIN)(dr)
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
  val canBe = (loc: Seq[String]) => loc forall Set(EYES, SKULL, FACE, HEAD, NECK, LEG_LEFT, LEG_RIGHT, LEGS,
    ARM_LEFT, ARM_RIGHT, ARMS, CHEST, VITALS, ABDOMEN, GROIN, TORSO, HANDS, HAND_LEFT, HAND_RIGHT, FEET, FOOT_LEFT,
    FOOT_RIGHT, SKIN, BODY)
}

/** Charlist subcontainer for items list's element, holds its stats and calculates element's total weight and cost */
case class Item(
                 name: String = "",
                 var carried: String = ItemState.STASH,
                 var dr: Int = 0,
                 var hp: Int = 1,
                 var hpLeft: Int = 1,
                 broken: Boolean = false,
                 var lc: Int = 5,
                 var tl: Int = 0,
                 notes: String = "",
                 var wt: Double = 0,
                 var cost: Double = 0,
                 var n: Int = 1,
                 var totalWt: Double = 0,
                 var totalCost: Double = 0)
  extends Possession {
  if (ItemState canBe carried) () else carried = ItemState.STASH
  if (dr < 0) dr = 0
  if (hp < 0) hp = 0
  if (hpLeft < 0) hpLeft = 0 else if (hpLeft > hp) hpLeft = hp
  if (lc > 5) lc = 5 else if (lc < 0) lc = 0
  if (tl < 0) tl = 0 else if (tl > 12) tl = 12
  if (wt < 0) wt = 0
  if (cost < 0) cost = 0
  if (n < 0) n = 0
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
  val canBe = (key: String) => Set(READY, EQUIPPED, COMBAT, TRAVEL, STASH)(key)
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
                                  footRight: HitLocationDR = HitLocationDR()) {
  def add(dr: Int, ep: Int, epi: Int, loc: String): DamageResistanceTotal = {
    import HitLocation._
    for (location <- loc match {
      case EYES => Seq(eyes)
      case SKULL => Seq(skull)
      case FACE => Seq(face)
      case HEAD => Seq(skull, face)
      case NECK => Seq(neck)
      case LEG_RIGHT => Seq(legRight)
      case LEG_LEFT => Seq(legLeft)
      case LEGS => Seq(legLeft, legRight)
      case ARM_RIGHT => Seq(armRight)
      case ARM_LEFT => Seq(armLeft)
      case ARMS => Seq(armLeft, armRight)
      case CHEST => Seq(chest)
      case VITALS => Seq(vitals)
      case ABDOMEN => Seq(abdomen)
      case GROIN => Seq(groin)
      case TORSO => Seq(chest, abdomen, vitals)
      case HANDS => Seq(handLeft, handRight)
      case HAND_LEFT => Seq(handLeft)
      case HAND_RIGHT => Seq(handRight)
      case FEET => Seq(footLeft, footRight)
      case FOOT_LEFT => Seq(footLeft)
      case FOOT_RIGHT => Seq(footRight)
      case SKIN => Seq(skull, face, neck, armLeft, armRight, handLeft, handRight, chest, vitals, abdomen, groin,
        legLeft, legRight, footLeft, footRight)
      case BODY => Seq(skull, eyes, face, neck, armLeft, armRight, handRight, handLeft, chest, vitals, abdomen, groin,
        legRight, legLeft, footRight, footLeft)
    }) {
      location.dr += dr
      location.ep += ep
      location.epi += epi
    }
    this
  }
}

/** Charlist subcontainer for hit location DR stats, holds its calculation method */
case class HitLocationDR(var dr: Int = 0, var ep: Int = 0, var epi: Int = 0) {
  dr = 0
  ep = 0
  epi = 0
}

/** Charlist subcontainer for conditions switches */
case class Conditions(
                       unconscious: Boolean = false,
                       mortallyWounded: Boolean = false,
                       dead: Boolean = false,
                       var shock: Int = 0,
                       stunned: Boolean = false,
                       afflictions: Afflictions = Afflictions(),
                       cripplingInjuries: Seq[String] = Seq(),
                       var posture: String = Posture.STANDING,
                       closeCombat: Boolean = false,
                       grappled: Boolean = false,
                       pinned: Boolean = false,
                       sprinting: Boolean = false,
                       mounted: Boolean = false) {
  if (shock < 0) shock = 0 else if (shock > 8) shock = 8
  if (Posture canBe posture) () else posture = Posture.STANDING
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
                        heartAttack: Boolean = false)

/** Charlist subnamespace for posture strings and validation method */
object Posture {
  val STANDING = "Standing"
  val CROUCHING = "Crouching"
  val SITTING = "Sitting"
  val KNEELING = "Kneeling"
  val CRAWLING = "Crawling"
  val LYING_PRONE = "Prone"
  val LYING_FACE_UP = "On Back"
  val canBe = (posture: String) =>
    Set(STANDING, CROUCHING, SITTING, KNEELING, CRAWLING, LYING_PRONE, LYING_FACE_UP)(posture)
}

object Charlist {

  import play.api.libs.json.Json

  val rndUp = (x: Double) => math.ceil(x - 0.01).toInt
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
  implicit val statDoubleFormat = Json.format[StatDouble]
  implicit val statPointsFormat = Json.format[StatPoints]
  implicit val statVarsFormat = Json.format[StatVars]
  implicit val statsFormat = Json.format[Stats]
  implicit val descriptionFormat = Json.format[Description]
  implicit val characterPointsFormat = Json.format[CharacterPoints]
  implicit val charlistFormat = Json.format[Charlist]
}