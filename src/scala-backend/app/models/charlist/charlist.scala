package models.charlist

import scala.collection.breakOut
import scalaz.Scalaz._

/**
  * Created by crimson on 9/23/16.
  */
case class Charlist(// TODO: maybe make recalc functions in compliance with functional programming style
                    _id: String = "",
                    timestamp: String = "",
                    player: String = "",
                    access: Seq[String] = Seq(), // For future functionality
                    name: String = "New Character",
                    cp: CharacterPoints = CharacterPoints(),
                    description: Description = Description(),
                    stats: Stats = Stats(),
                    statVars: StatVars = StatVars(),
                    var damageResistance: DamageResistance = DamageResistance(),
                    var reactions: Seq[Reaction] = Seq(),
                    traits: Seq[Trait] = Seq(
                      Trait(name = "Skull", modifiers = Seq(TraitModifier(drBonuses = Seq(
                        BonusDR(Seq(HitLocation.SKULL), protection = DrSet(2))))))),
                    skills: Seq[Skill] = Seq(),
                    techniques: Seq[Technique] = Seq(),
                    equip: Equipment = Equipment(),
                    currentStats: StatsCurrent = StatsCurrent(),
                    wounds: Seq[Wound] = Seq(),
                    conditions: Conditions = Conditions(),
                    var api: String = "") {
  api = "0.3.0"

  private val foldSum = { (a: String, b: Seq[(String, Int)]) => (a, b.foldLeft(0)(_ + _._2)) }
  {
    import BonusToAttribute._
    import HitLocation._

    val tdr: Seq[(String, DrSet, DrSet)] = for {
      t <- traits
      b <- t.modifiers withFilter (_.on) flatMap (_.drBonuses)
      l <- b.locations flatMap locMap
    } yield (l, if (b.front) b.protection else DrSet(), if (b.back) b.protection else DrSet())
    val adr: Seq[(String, DrSet, DrSet)] = for {
      a <- equip.armor
      l <- a.locations flatMap locMap
    } yield (l, if (a.front) a.protection else DrSet(), if (a.back) a.protection else DrSet())
    val drMap: Map[String, HitLocationDR] = {
      for {(a, b) <- (tdr ++ adr) groupBy (_._1)}
        yield (a, HitLocationDR(b.foldLeft(DrSet())(_ |+| _._2), b.foldLeft(DrSet())(_ |+| _._3)))
    } withDefaultValue HitLocationDR(DrSet(), DrSet())
    damageResistance = DamageResistance(drMap(SKULL), drMap(EYES), drMap(FACE), drMap(NECK), drMap(ARM_LEFT),
      drMap(ARM_RIGHT), drMap(HAND_LEFT), drMap(HAND_RIGHT), drMap(CHEST), drMap(VITALS), drMap(ABDOMEN), drMap(GROIN),
      drMap(LEG_LEFT), drMap(LEG_RIGHT), drMap(FOOT_LEFT), drMap(FOOT_RIGHT))

    val bonuses = traits flatMap (_.attrBonusValues) groupBy (_._1) map foldSum.tupled withDefaultValue 0
    stats.st.base = 10
    stats.dx.base = 10
    stats.iq.base = 10
    stats.ht.base = 10
    stats.will.base = 10
    stats.per.base = 10
    stats.st.bonus = bonuses(ST)
    stats.dx.bonus = bonuses(DX)
    stats.iq.bonus = bonuses(IQ)
    stats.ht.bonus = bonuses(HT)
    stats.will.bonus = bonuses(WILL)
    stats.per.bonus = bonuses(PER)
    stats.basicSpeed.bonus = bonuses(BASIC_SPEED)
    stats.basicMove.bonus = bonuses(BASIC_MOVE)
    stats.hp.bonus = bonuses(HP)
    stats.fp.bonus = bonuses(FP)
    statVars.sm = bonuses(SM)
    stats.liftSt.base = stats.st.value
    stats.strikeSt.base = stats.st.value
    stats.hp.base = stats.st.value
    stats.fp.base = stats.ht.value
    stats.basicSpeed.base = (stats.dx.value + stats.ht.value) * .25
    stats.basicMove.base = stats.basicSpeed.value.toInt
    statVars.frightCheck = math.min(13, stats.will.value + bonuses(FC))
    statVars.vision = stats.per.value + bonuses(VISION)
    statVars.hearing = stats.per.value + bonuses(HEARING)
    statVars.tasteSmell = stats.per.value + bonuses(TASTE_SMELL)
    statVars.touch = stats.per.value + bonuses(TOUCH)
    statVars.bl = (stats.liftSt.value * stats.liftSt.value * .2).toInt
    statVars.calc(equip.totalCombWt, equip.totalTravWt, stats.basicMove.value,
      stats.basicSpeed.value.toInt + 3 + bonuses(DODGE))
    currentStats.calc(stats.hp.value, stats.fp.value, statVars.combMove, statVars.dodge)
  }
  {
    import SkillBaseAttribute._

    for (s <- skills) {
      s.bonus = traits.foldLeft(0)(_ + _.skillBonusValue(s.name, s.spc))
      val attrVal = s.attr match {
        case ST => stats.st.value
        case DX => stats.dx.value
        case IQ => stats.iq.value
        case HT => stats.ht.value
        case WILL => stats.will.value
        case PER => stats.per.value
      }
      s.calcLvl(attrVal, statVars.cEnc)
    }
    for (t <- techniques) {
      def sFltr(s: Skill) = s.name == t.skill && (if (t.spc != "") s.spc == t.spc else true)
      val l = skills collectFirst { case s if sFltr(s) => s.lvl } getOrElse 0
      t.calcLvl(l)
    }

    reactions = (for {(g, r) <- traits.flatMap(_.reactBonuses) ++ skills.flatMap(_.reactBonuses) groupBy (_.affected)}
      yield Reaction(
        g,
        (for {(group2, react2) <- r groupBy (_.freq)}
          yield {
            val bonusReaction = (for {(group3, react3) <- react2 groupBy (_.reputation)}
              yield react3.fold(BonusReaction())(if (group3) _ |^| _ else _ |+| _))
              .fold(BonusReaction())(_ |+| _)
            ReactionMod(group2, bonusReaction.bVal, bonusReaction.notes)
          }) (breakOut))) (breakOut)

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
    for (w <- equip.weapons; a <- w.attacksMelee) {
      def sFltr(s: Skill) = s.name == a.skill && (if (a.spc != "") s.spc == a.spc else true)
      val lvl = skills collectFirst { case s if sFltr(s) => s.relLvl } getOrElse -4
      val bns = (skills ++ traits.flatMap(_.modifiers)).foldLeft(0, 0)(_ |+| _.dmgBVal(a.skill, a.spc, lvl))
      a.damage.calcDmg(thr, sw, bns)
      for (m <- a.followup ++ a.linked) m.calcDmg(thr, sw, (0, 0))
    }

    val costMods = traits flatMap (_.attrCostModValues) groupBy (_._1) map foldSum.tupled withDefaultValue 0
    stats.st.cpMod = 100 + costMods(ST)
    stats.dx.cpMod = 100 + costMods(DX)
    stats.iq.cpMod = 100 + costMods(IQ)
    stats.ht.cpMod = 100 + costMods(HT)
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
                  hp: StatInt = StatInt(),
                  fp: StatInt = StatInt(),
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
                     var combatEncumbrance: String = "",
                     var travelEncumbrance: String = "",
                     var combMove: Int = 0,
                     var travMove: Int = 0,
                     var dodge: Int = 0,
                     var sm: Int = 0) {
  var cEnc = 0
  var tEnc = 0

  def calc(combWt: Double, travWt: Double, bm: Int, bd: Int): StatVars = {
    assert(combWt <= bl * 10, s"combat encumbrance value is over 10 times basic lift ($combWt, $bl)")
    assert(travWt <= bl * 10, s"travel encumbrance value is over 10 times basic lift ($travWt, $bl)")
    val encLvl = (x: Double) => x match {
      case _ if x <= 1 => 0
      case _ if x <= 2 => 1
      case _ if x <= 3 => 2
      case _ if x <= 6 => 3
      case _ if x <= 10 => 4
    }
    cEnc = encLvl(combWt / bl)
    tEnc = encLvl(travWt / bl)
    combMove = (bm * .2 * (5 - cEnc)).toInt
    travMove = (bm * .2 * (5 - tEnc)).toInt
    dodge = bd - cEnc
    combatEncumbrance = cEnc.toString
    travelEncumbrance = tEnc.toString
    this
  }
}

/** */
case class StatsCurrent(// TODO: for party screen
                        var hpLost: Int = 0,
                        var fpLost: Int = 0,
                        var reeling: Boolean = false,
                        var tired: Boolean = false,
                        var collapsing: Boolean = false,
                        var fallingAslp: Boolean = false,
                        var vision: Int = 0,
                        var hearing: Int = 0,
                        var move: Int = 0,
                        var dodge: Int = 0) {
  if (hpLost < 0) hpLost = 0
  if (fpLost < 0) fpLost = 0

  import Charlist.rndUp

  def calc(hp: Int, fp: Int, mov: Int, ddg: Int): StatsCurrent = {
    reeling = hp * (2.0 / 3.0) < hpLost
    tired = fp * (2.0 / 3.0) < fpLost
    collapsing = hp <= hpLost
    fallingAslp = fp <= fpLost
    val m = if (collapsing || fallingAslp) .5 else 1.0
    move = rndUp(mov * m)
    dodge = rndUp(ddg * m)
    this
  }
}

/** Charlist subcontainer for character attribute storage */
sealed abstract class Stat[A <: AnyVal](implicit x: scala.math.Numeric[A]) {

  import Charlist.rndUp
  import x._

  val delta: A
  var base: A
  var bonus: A
  var cpMod: Int
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
                    var bonus: Int = 0,
                    var cpMod: Int = 100,
                    var cp: Int = 0)
  extends Stat[Int]

case class StatDouble(
                       delta: Double = 0,
                       var base: Double = 0,
                       var bonus: Double = 0,
                       var cpMod: Int = 100,
                       var cp: Int = 0)
  extends Stat[Double]

/** Charlist subcontainer for NPC reactions list's element stats */
case class Reaction(affected: String = "", modifiers: Seq[ReactionMod] = Seq())

/** Charlist subcontainer for NPC reaction modifiers list's element stats */
case class ReactionMod(freq: Int = 16, mod: Int = 0, notes: String = "")

/** Parent uniting all case classes carrying a bonus to melee damage */
sealed abstract class DamageBonusing {
  val dmgBonuses: Seq[BonusDamage]
  val on: Boolean

  def dmgBVal(s: String, spc: String, lvl: Int): (Int, Int) = {
    import NameCompare.{ANY, _}
    val skillFit = (b: BonusDamage) => b.skillCompare match {
      case IS => b.skill == s
      case BEGINS => b.skill.regionMatches(true, 0, s, 0, b.skill.length)
    }
    val spcFit = (b: BonusDamage) => b.spcCompare match {
      case ANY => true
      case IS => b.spc == spc
      case BEGINS => b.spc.regionMatches(true, 0, spc, 0, b.spc.length)
    }
    val fit = (b: BonusDamage) => skillFit(b) && spcFit(b) && b.relSkill <= lvl
    dmgBonuses.collect { case b if on && fit(b) => if (b.perDie) (b.bonus, 0) else (0, b.bonus) } .fold(0, 0)(_ |+| _)
  }
}

/** Charlist subcontainer for features list's element stats */
case class Trait(
                  name: String = "",
                  var types: Seq[String] = Seq(TraitType.PHYSICAL),
                  var category: String = TraitCategory.ADVANTAGE,
                  ref: String = "",
                  notes: String = "",
                  prerequisites: Seq[String] = Seq(), // For future functionality
                  modifiers: Seq[TraitModifier] = Seq(),
                  cpBase: Int = 0,
                  var level: Int = 0,
                  cpPerLvl: Int = 0,
                  var cp: Int = 0) {
  if (TraitType canBe types) () else types = Seq(TraitType.PHYSICAL)
  if (TraitCategory canBe category) () else category = TraitCategory.ADVANTAGE
  if (level < 0) level = 0

  import Charlist.rndUp
  import TraitModifierAffects._

  private case class MCost(pts: Int = 0, pct: Int = 0, mlt: Double = 1) {
    def |+|(that: (Int, Int, Double)): MCost = MCost(pts + that._1, pct + that._2, mlt * that._3)
  }

  private val cpMods: Map[String, MCost] = (for {(a, b) <- modifiers filter (_.on) groupBy (_.affects)}
    yield (a, b.foldLeft(MCost())(_ |+| _.costVal))) withDefaultValue MCost()
  private val MCost(bPts, bPct, bMlt) = cpMods(BASE)
  private val MCost(lPts, lPct, lMlt) = cpMods(LEVELS)
  private val MCost(tPts, tPct, tMlt) = cpMods(TOTAL)
  cp = rndUp(((bPts + cpBase) * math.max(.2, bPct * .01 + 1) * bMlt
    + level * (lPts + cpPerLvl) * math.max(.2, lPct * .01 + 1)
    * lMlt + tPts) * math.max(.2, tPct * .01 + 1) * tMlt)

  val attrBonusValues: Seq[(String, Int)] =
    for (b <- modifiers withFilter (_.on) flatMap (_.attrBonuses))
      yield (b.attr, b.bonus * (if (b.perLvl) level else 1))
  val skillBonusValue = (s: String, spc: String) => {
    import NameCompare.{ANY, _}
    val skillFit = (b: BonusSkill) => b.skillCompare match {
      case IS => b.skill == s
      case BEGINS => b.skill.regionMatches(true, 0, s, 0, b.skill.length)
    }
    val spcFit = (b: BonusSkill) => b.spcCompare match {
      case ANY => true
      case IS => b.spc == spc
      case BEGINS => b.spc.regionMatches(true, 0, spc, 0, b.spc.length)
    }
    val fit: PartialFunction[BonusSkill, Int] = {
      case b: BonusSkill if skillFit(b) && spcFit(b) => b.bonus * (if (b.perLvl) level else 1)
    }
    modifiers.withFilter(_.on).flatMap(_.skillBonuses).collect(fit).sum
  }
  val attrCostModValues: Seq[(String, Int)] =
    for (m <- modifiers withFilter (_.on) flatMap (_.attrCostMods)) yield (m.attr, m.cost)
  val reactBonuses: Seq[BonusReaction] =
    for (b <- modifiers withFilter (_.on) flatMap (_.reactBonuses)) yield b.calcValue(level)
}

/** Charlist subcontainer for trait modifiers list's element stats */
case class TraitModifier(
                          on: Boolean = true,
                          name: String = "",
                          ref: String = "",
                          notes: String = "",
                          attrBonuses: Seq[BonusAttribute] = Seq(),
                          skillBonuses: Seq[BonusSkill] = Seq(),
                          dmgBonuses: Seq[BonusDamage] = Seq(),
                          drBonuses: Seq[BonusDR] = Seq(),
                          attrCostMods: Seq[BonusAttributeCost] = Seq(),
                          reactBonuses: Seq[BonusReaction] = Seq(),
                          var affects: String = TraitModifierAffects.TOTAL,
                          var costType: String = TraitModifierCostType.PERCENT,
                          var level: Int = 0,
                          cost: Double = 0)
  extends DamageBonusing {
  if (TraitModifierAffects canBe affects) () else affects = TraitModifierAffects.TOTAL
  if (TraitModifierCostType canBe costType) () else costType = TraitModifierCostType.PERCENT
  if (level < 0) level = 0

  import TraitModifierCostType._

  val costVal: (Int, Int, Double) = costType match {
    case POINTS => (cost.toInt, 0, 1)
    case PERCENT => (0, cost.toInt, 1)
    case LEVEL => (0, cost.toInt * level, 1)
    case MULTIPLIER => (0, 0, cost)
  }
}

/** Charlist subcontainer for skills list's element stats, calculates its relative level */
case class Skill(
                  name: String = "",
                  spc: String = "",
                  var tl: Int = 0,
                  var skillString: String = "",
                  var attr: String = SkillBaseAttribute.DX,
                  var diff: String = SkillDifficulty.EASY,
                  defaults: Seq[String] = Seq(), // For future functionality
                  prerequisites: Seq[String] = Seq(), // For future functionality
                  dmgBonuses: Seq[BonusDamage] = Seq(),
                  reactBonuses: Seq[BonusReaction] = Seq(),
                  encumbr: Boolean = false,
                  var bonus: Int = 0,
                  categories: Seq[String] = Seq(),
                  notes: String = "",
                  var cp: Int = 1,
                  relLvl: Int = 0,
                  var lvl: Int = 0)
  extends DamageBonusing {
  if (tl < 0) tl = 0 else if (tl > 12) tl = 12
  if (SkillBaseAttribute canBe attr) () else attr = SkillBaseAttribute.DX
  if (SkillDifficulty canBe diff) () else diff = SkillDifficulty.EASY
  skillString = name + (if (tl != 0) s"/TL$tl" else "") + (if (spc != "") s" ($spc)" else "")
  val on = true

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
                      var maxLvl: Int = 0,
                      notes: String = "",
                      var cp: Int = 0,
                      var relLvl: Int = 0,
                      var lvl: Int = 0) {
  if (SkillDifficulty techniqueCanBe diff) () else diff = SkillDifficulty.AVERAGE
  if (maxLvl <= defLvl) maxLvl = defLvl + 1
  if (relLvl < defLvl) relLvl = defLvl else if (relLvl > maxLvl) relLvl = maxLvl
  tchString = s"$name ($skill${if (spc != "") " (" + spc + ")"})"
  cp = relLvl - defLvl + (if (diff == SkillDifficulty.HARD && relLvl > defLvl) 1 else 0)

  def calcLvl(skill: Int): Unit = lvl = skill + relLvl
}

/** Charlist subcontainer for attribute bonuses list's element stats */
case class BonusAttribute(var attr: String = BonusToAttribute.ST, perLvl: Boolean = false, bonus: Int = 0) {
  if (BonusToAttribute canBe attr) () else attr = BonusToAttribute.ST
}

/** Charlist subcontainer for skill bonuses list's element stats */
case class BonusSkill(
                       skill: String = "",
                       var skillCompare: String = NameCompare.IS,
                       spc: String = "",
                       var spcCompare: String = NameCompare.ANY,
                       perLvl: Boolean = false,
                       bonus: Int = 0) {
  if (NameCompare canBe skillCompare) () else skillCompare = NameCompare.IS
  if (NameCompare canBe spcCompare) () else spcCompare = NameCompare.ANY
  if (spc != "" && spcCompare == NameCompare.ANY) spcCompare = NameCompare.IS
}

/** Charlist subcontainer for damage bonuses list's element stats */
case class BonusDamage(
                        skill: String = "",
                        var skillCompare: String = NameCompare.IS,
                        spc: String = "",
                        var spcCompare: String = NameCompare.ANY,
                        relSkill: Int = 0,
                        perDie: Boolean = false,
                        bonus: Int = 0) {
  if (NameCompare canBe skillCompare) () else skillCompare = NameCompare.IS
  if (NameCompare canBe spcCompare) () else spcCompare = NameCompare.ANY
  if (spc != "" && spcCompare == NameCompare.ANY) spcCompare = NameCompare.IS
}

/** Charlist subcontainer for DR bonuses list's element stats */
case class BonusDR(
                    var locations: Seq[String] = Seq(HitLocation.SKIN),
                    perLvl: Boolean = false,
                    front: Boolean = true,
                    back: Boolean = true,
                    var protection: DrSet = DrSet()) {
  if (HitLocation canBe locations) () else locations = Seq(HitLocation.SKIN)
}

/** Charlist subcontainer for attribute cost modifiers list's element stats */
case class BonusAttributeCost(var attr: String = SkillBaseAttribute.ST, cost: Int = 0) {
  if (SkillBaseAttribute canBe attr) () else attr = SkillBaseAttribute.ST
}

/** Charlist subcontainer for reaction bonuses list's element stats */
case class BonusReaction(
                          affected: String = "",
                          reputation: Boolean = false,
                          perLvl: Boolean = false,
                          var freq: Int = 16,
                          bonus: Int = 0,
                          notes: String = "") {
  if (ReactionFrequency canBe freq) () else freq = 16
  var bVal: Int = bonus
  private val noteSum = (n: String) => Seq(this.notes, n) filterNot (_.isEmpty) mkString "; "

  def calcValue(lvl: Int): BonusReaction = {
    if (perLvl) bVal *= lvl
    this
  }

  def |+|(that: BonusReaction): BonusReaction = this copy(bonus = this.bVal + that.bVal, notes = noteSum(that.notes))

  def |^|(that: BonusReaction): BonusReaction =
    this copy(bonus = math.max(math.min(this.bVal + that.bVal, 4), -4), notes = noteSum(that.notes))
}

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
                      var totalDb: Int = 0,
                      var totalCost: Double = 0,
                      var totalCombWt: Double = 0,
                      var totalTravWt: Double = 0,
                      combat: Boolean = false) {

  import ItemState._

  totalCost = (weapons.filter(!_.innate) ++ armor ++ items).foldLeft(0.0)(_ + _.totalCost)

  private def weight(f: String => Boolean) =
    (for {p <- weapons.filter(!_.innate) ++ armor ++ items; if f(p.state)} yield p.totalWt).sum

  totalCombWt = weight(Set(READY, EQUIPPED, COMBAT))
  totalTravWt = totalCombWt + weight(_ == TRAVEL)
  private val equip = (p: Possession) => Set(READY, EQUIPPED)(p.state) && !p.broken
  totalDb = (weapons.withFilter(equip).flatMap(_.blocks map (_.db)) ++ armor.withFilter(equip).map(_.db)).sum
}

sealed abstract class Possession {
  var state: String
  val broken: Boolean

  def totalCost: Double

  def totalWt: Double
}

/** Charlist subcontainer for weapons list's element, calculates weapon weight and cost including ammunition if
  * applicable, holds all its stats and attacks it can make as subcontainers. */
case class Weapon(
                   name: String = "",
                   var state: String = ItemState.STASH,
                   innate: Boolean = false,
                   attacksMelee: Seq[MeleeAttack] = Seq(),
                   attacksRanged: Seq[RangedAttack] = Seq(),
                   blocks: Seq[BlockDefence] = Seq(),
                   var grips: Seq[String] = Seq(),
                   offHand: Boolean = false,
                   var bulk: Int = 0,
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
  if (ItemState canBe state) () else state = ItemState.STASH
  if (bulk > 0) bulk = 0
  if (dr < 0) dr = 0
  if (hp < 0) hp = 0
  if (hpLeft < 0) hpLeft = 0 else if (hpLeft > hp) hpLeft = hp
  if (lc > 5) lc = 5 else if (lc < 0) lc = 0
  if (tl < 0) tl = 0 else if (tl > 12) tl = 12
  if (wt < 0) wt = 0
  if (cost < 0) cost = 0
  grips = (attacksMelee.map(_.grip) ++ attacksRanged.map(_.grip) ++ blocks.map(_.grip)).distinct
  totalWt = wt + attacksRanged.map(_.shots.totalWt).sum
  totalCost = cost + attacksRanged.map(_.shots.totalCost).sum
}

/** Charlist subcontainer for melee attack type's stats, holds damage stats subcontainers and produces parry stat
  * string. */
case class MeleeAttack(
                        name: String = "",
                        available: Boolean = true,
                        grip: String = "",
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
                         available: Boolean = true,
                         grip: String = "",
                         damage: RangedDamage = RangedDamage(),
                         followup: Seq[RangedDamage] = Seq[RangedDamage](),
                         linked: Seq[RangedDamage] = Seq[RangedDamage](),
                         skill: String = "",
                         spc: String = "",
                         jet: Boolean = false,
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
  if (jet) rof.rofString = "Jet"
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
                      var rofString: String = "") {
  if (rof <= 0) rof = 1
  if (rofMult <= 0) rofMult = 1
  rofString = s"$rof${if (rofMult != 1) "x" + rofMult else ""}${if (rofFA) "!" else ""}"
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

/** */
case class BlockDefence(
                         name: String = "",
                         available: Boolean = true,
                         grip: String = "",
                         skill: String = "",
                         spc: String = "",
                         var db: Int = 1,
                         notes: String = "") {
  if (db < 1) db = 1
}

/** Charlist subcontainer for armor list's element, holds its stats */
case class Armor(
                 name: String = "",
                 var state: String = ItemState.EQUIPPED,
                 var db: Int = 0,
                 var protection: DrSet = DrSet(),
                 var front: Boolean = true,
                 back: Boolean = true,
                 var drType: String = DrType.HARD,
                 var locations: Seq[String] = Seq(HitLocation.CHEST),
                 var hp: Int = 1,
                 var hpLeft: Int = 1,
                 broken: Boolean = false,
                 var lc: Int = 5,
                 var tl: Int = 0,
                 notes: String = "",
                 var wt: Double = 0,
                 var cost: Double = 0)
  extends Possession {
  if (ItemState canBe state) () else state = ItemState.EQUIPPED
  if (db < 0) db = 0 else if (db > 3) db = 3
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
  val locMap: PartialFunction[String, Seq[String]] = {
    case HEAD => Seq(SKULL, FACE)
    case LEGS => Seq(LEG_RIGHT, LEG_LEFT)
    case ARMS => Seq(ARM_RIGHT, ARM_LEFT)
    case TORSO => Seq(CHEST, ABDOMEN, VITALS)
    case HANDS => Seq(HAND_RIGHT, HAND_LEFT)
    case FEET => Seq(FOOT_RIGHT, FOOT_LEFT)
    case SKIN => Seq(SKULL, FACE, NECK, ARM_LEFT, ARM_RIGHT, HAND_LEFT, HAND_RIGHT, CHEST, VITALS, ABDOMEN, GROIN,
      LEG_LEFT, LEG_RIGHT, FOOT_RIGHT, FOOT_LEFT)
    case BODY => Seq(SKULL, EYES, FACE, NECK, ARM_LEFT, ARM_RIGHT, HAND_RIGHT, HAND_LEFT, CHEST, VITALS, ABDOMEN,
      GROIN, LEG_LEFT, LEG_RIGHT, FOOT_LEFT, FOOT_RIGHT)
    case x: String => Seq(x)
  }
  val woundCanBe = (loc: String) => Set(EYES, SKULL, FACE, NECK, LEG_LEFT, LEG_RIGHT, ARM_LEFT, ARM_RIGHT, CHEST,
    VITALS, ABDOMEN, GROIN, HAND_LEFT, HAND_RIGHT, FOOT_LEFT, FOOT_RIGHT)(loc)
  val canBe = (loc: Seq[String]) => loc forall Set(EYES, SKULL, FACE, HEAD, NECK, LEG_LEFT, LEG_RIGHT, LEGS,
    ARM_LEFT, ARM_RIGHT, ARMS, CHEST, VITALS, ABDOMEN, GROIN, TORSO, HANDS, HAND_LEFT, HAND_RIGHT, FEET, FOOT_LEFT,
    FOOT_RIGHT, SKIN, BODY)
}

/** Charlist subcontainer for items list's element, holds its stats and calculates element's total weight and cost */
case class Item(
                 name: String = "",
                 var state: String = ItemState.STASH,
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
  if (ItemState canBe state) () else state = ItemState.STASH
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
case class DamageResistance(
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
                             footRight: HitLocationDR = HitLocationDR())

/** Charlist subcontainer for hit location DR stats, holds its calculation method */
case class HitLocationDR(front: DrSet = DrSet(), rear: DrSet = DrSet())

/** */
case class DrSet(var dr: Int = 0, var ep: Int = 0, var epi: Int = 0) {
  if (dr < 0) dr = 0
  if (ep < 0) ep = 0
  if (epi < 0) epi = 0

  def |+|(that: DrSet): DrSet = DrSet(this.dr + that.dr, this.ep + that.ep, this.epi + that.epi)

  def *(x: Int): DrSet = DrSet(this.dr * x, this.ep * x, this.epi * x)
}

/** */
case class Wound(
                  var location: String = HitLocation.CHEST,
                  var dType: String = DamageType.CRUSHING,
                  var points: Int = 1,
                  firstAid: Boolean = false,
                  bleeding: Boolean = false,
                  crippling: Boolean = false,
                  lasting: Boolean = false) {
  if (HitLocation woundCanBe location) () else location = HitLocation.CHEST
  if (DamageType canBe dType) () else dType = DamageType.CRUSHING
  if (points < 1) points = 1
}

/** Charlist subcontainer for conditions switches */
case class Conditions(
                       unconscious: Boolean = false,
                       mortallyWounded: Boolean = false,
                       dead: Boolean = false,
                       var shock: Int = 0,
                       stunned: Boolean = false,
                       afflictions: Afflictions = Afflictions(),
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
  implicit val woundFormat = Json.format[Wound]
  implicit val itemFormat = Json.format[Item]
  implicit val drSetFormat = Json.format[DrSet]
  implicit val armorElementFormat = Json.format[Armor]
  implicit val blockDefenceFormat = Json.format[BlockDefence]
  implicit val rangedShotsFormat = Json.format[RangedShots]
  implicit val rangedRoFFormat = Json.format[RangedRoF]
  implicit val rangedDamageFormat = Json.format[RangedDamage]
  implicit val rangedAttackFormat = Json.format[RangedAttack]
  implicit val meleeDamageFormat = Json.format[MeleeDamage]
  implicit val meleeAttackFormat = Json.format[MeleeAttack]
  implicit val weaponFormat = Json.format[Weapon]
  implicit val hitLocationFormat = Json.format[HitLocationDR]
  implicit val damageResistanceTotalFormat = Json.format[DamageResistance]
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
  implicit val statsCurrentFormat = Json.format[StatsCurrent]
  implicit val statVarsFormat = Json.format[StatVars]
  implicit val statsFormat = Json.format[Stats]
  implicit val descriptionFormat = Json.format[Description]
  implicit val characterPointsFormat = Json.format[CharacterPoints]
  implicit val charlistFormat = Json.format[Charlist]
}