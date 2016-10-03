package models.charlist

/**
  * Created by crimson on 9/23/16.
  */
case class Charlist(
                     _id: String,
                     timestamp: Long,
                     player: String,
                     cp: Int,
                     name: String,
                     description: Description,
                     coreStats: Stats,
                     features: Seq[Feature],
                     skills: Seq[Skill],
                     techniques: Seq[Technique],
                     equip: Equipment
                   ) {
  /*thrDmg = st.value match {
    case x if x < 1 => (0, 0)
    case x if x < 11 => (1, ((x - 1) / 2).toInt - 6)
    case x => (((x - 3) / 8).toInt, ((x - 3) / 2).toInt % 4 - 1)
  }
  swDmg = st.value match {
    case x if x < 1 => (0, 0)
    case x if x < 9 => (1, ((x - 1) / 2).toInt - 5)
    case x => (((x - 5) / 4).toInt, (x - 5) % 4 - 1)
  }*/
}

/** Charlist subnamespace for json field name strings */
object CharlistFields {
  val ID = "_id"
  val TIMESTAMP = "timestamp"
  val PLAYER = "player"
  val CPTOTAL = "cp"
  val NAME = "name"
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
                  st: Stat[Int] = Stat[Int](),
                  dx: Stat[Int] = Stat[Int](),
                  iq: Stat[Int] = Stat[Int](),
                  ht: Stat[Int] = Stat[Int](),
                  will: Stat[Int] = Stat[Int](),
                  per: Stat[Int] = Stat[Int](),
                  liftSt: Stat[Int] = Stat[Int](),
                  strikeSt: Stat[Int] = Stat[Int](),
                  var thrDmg: String = "", // TODO: calculation on higher level
                  var swDmg: String = "", // TODO: calculation on higher level
                  var bl: Int = 0,
                  var encumbrance: Int = 0, // TODO: calculation on higher level
                  var defenseBonus: Int = 0, // TODO: calculation on higher level
                  reactionBonus: Int = 0, // TODO: make calculable
                  hp: Points = Points(),
                  fp: Points = Points(),
                  basicSpeed: Stat[Float] = Stat[Float](),
                  basicMove: Stat[Int] = Stat[Int](),
                  basicDodge: Stat[Int] = Stat[Int](),
                  var move: Int = 0, // TODO: calculation on higher level
                  var dodge: Int = 0, // TODO: calculation on higher level
                  sm: Int = 0
                ) {
  st.calcValue(10).calcCp(10)
  dx.calcValue(10).calcCp(20)
  iq.calcValue(10).calcCp(20)
  ht.calcValue(10).calcCp(10)
  will.calcValue(10).calcCp(5)
  per.calcValue(10).calcCp(5)
  liftSt.calcValue(st.value).calcCp(3)
  strikeSt.calcValue(st.value).calcCp(5)
  bl = math.round(liftSt.value ^ 2 / 5)
  hp.calcValue(st.value).calcCp(2)
  fp.calcValue(ht.value).calcCp(3)
  basicSpeed.calcValue((dx.value + ht.value) / 4).calcCp(20)
  basicMove.calcValue(basicSpeed.value.toInt).calcCp(5)
  basicDodge.calcValue(basicSpeed.value.toInt + 3).calcCp(15)
}

/** Charlist subcontainer for character stat storage */
case class Stat[A](
                    delta: A = 0.asInstanceOf[A],
                    bonus: A = 0.asInstanceOf[A], // TODO: make calculable
                    notes: String = "",
                    var value: A = 0.asInstanceOf[A],
                    var cp: Int = 0
                  ) {

  def calcValue(default: A) = {
    value = (default.asInstanceOf[Float] + delta.asInstanceOf[Float] + bonus.asInstanceOf[Float]).asInstanceOf[A]
    this
  }

  def calcCp(cost: Int) = {
    cp = (delta.asInstanceOf[Float] * cost).asInstanceOf[Int]
    this
  }
}

/** Charlist subcontainer for HP and FP attributes' stats */
case class Points(
                   lost: Int = 0
                 ) extends Stat[Int]

/** Charlist subcontainer for features list's element stats */
case class Feature(
                    feature: String = "",
                    cp: Int = 0
                  )

/** Charlist subcontainer for skills list's element stats, calculates its relative level */
case class Skill(
                  name: String = "",
                  attr: String = "",
                  diff: String = "",
                  defaults: Seq[String] = Seq(), // For future functionality
                  prerequisites: Seq[String] = Seq(), // For future functionality
                  bonus: Int = 0, // TODO: make calculable
                  notes: String = "",
                  cp: Int = 1,
                  var relLvl: Int = 0,
                  var lvl: Int = 0 // TODO: calculation on higher level
                ) {
  assert(SkillBaseAttributes.isValid(attr), s"invalid skill's attribute ($attr in $name)")
  assert(SkillDifficulties.isValid(diff), s"invalid skill's difficulty ($diff in $name")
  assert(cp >= 0, s"skill's CP value is negative or 0 ($cp in $name")
  relLvl = SkillDifficulties.values(diff) + (if (cp > 1) 1 else 0) + (cp / 4).toInt + bonus
}

/** Charlist subnamespace for skill difficulties strings and validation method */
object SkillDifficulties {
  val EASY = "Easy"
  val AVERAGE = "Average"
  val HARD = "Hard"
  val VERY_HARD = "Very Hard"

  val values: Map[String, Int] = Map(EASY -> 0, AVERAGE -> -1, HARD -> -2, VERY_HARD -> -3)

  def isValid(d: String) = Set(EASY, AVERAGE, HARD, VERY_HARD).contains(d)
}

/** Charlist subnamespace for base skill attributes and validation method */
object SkillBaseAttributes {
  val ST = "ST"
  val IQ = "IQ"
  val DX = "DX"
  val HT = "HT"
  val WILL = "Will"
  val PER = "Per"

  def isValid(a: String) = Set(ST, IQ, DX, HT, WILL, PER).contains(a)
}

/** Charlist subcontainer for techniques list's element stats, calculates its relative level */
case class Technique(
                      name: String = "",
                      skill: String = "",
                      diff: String = TechniqueDifficulties.AVERAGE,
                      style: String = "",
                      defLvl: Int = 0,
                      maxLvl: Int = 0,
                      notes: String = "",
                      cp: Int = 0,
                      var relLvl: Int = 0,
                      var lvl: Int = 0 // TODO: calculation on higher level
                    ) {
  assert(TechniqueDifficulties.isValid(diff), s"invalid technique's difficulty string ($diff in $name)")
  assert(cp >= 0 && cp <= (maxLvl - defLvl + (if (diff == TechniqueDifficulties.HARD) 1 else 0)),
    s"technique's cp value out of bounds ($cp in $name")
  relLvl = math.max(cp + defLvl - (if (diff == TechniqueDifficulties.HARD) 1 else 0), defLvl)
}

/** Charlist subnamespace for technique difficulties strings and validation method */
object TechniqueDifficulties {
  val AVERAGE = "Average"
  val HARD = "Hard"

  def isValid(d: String) = Set(AVERAGE, HARD).contains(d)
}

/** Charlist subcontainer for character possessions, calculates total weights and cost and holds armor, weapons, and all
  * possessions items subcontainers. */
case class Equipment(
                      weapons: Seq[Weapon] = Seq(),
                      armor: Seq[Armor] = Seq(),
                      items: Seq[Item] = Seq(),
                      var totalCost: Double = 0,
                      var totalCombWt: Float = 0,
                      var totalTravWt: Float = 0
                    ) {

  import ItemState._

  totalCost = 0
  weapons.foreach(totalCost += _.totalCost)
  armor.foreach(totalCost += _.cost)
  items.foreach(totalCost += _.totalCost)
  totalCombWt = 0
  val comb = Set(READY, EQUIPPED, COMBAT)
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
}

/** Charlist subcontainer for weapons list's element, calculates weapon weight and cost including ammunition if applicable, holds
  * all its stats and attacks it can make as subcontainers. */
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
                   lc: Int = 0,
                   tl: Int = 0,
                   notes: String = "",
                   wt: Float = 0,
                   cost: Double = 0,
                   var totalWt: Float = 0,
                   var totalCost: Double = 0
                 ) {
  assert(ItemState.isValid(carried), s"invalid weapon's carrying state ($carried in $name)")
  assert(bulk <= 0, s"positive bulk value ($bulk in $name)")
  assert(db >= 0 && db < 4, s"defense bonus value out of bounds ($db in $name)")
  assert(dr >= 0, s"negative item's DR value ($dr in $name)")
  assert(hp > 0, s"negative or 0 item's HP value ($hp in $name)")
  assert(hpLeft >= 0 && hpLeft <= hp, s"item's current HP value out of bounds ($hpLeft in $name)")
  assert(lc < 6 && lc >= 0, s"legality class value out of bounds ($lc in $name)")
  assert(tl >= 0 && tl < 13, s"tech level value out of bounds ($tl in $name)")
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
                        parry: Int = 0,
                        parryType: String = "",
                        var parryString: String = "",
                        st: Int = 10,
                        hands: String = "",
                        reach: String = "",
                        notes: String = ""
                      ) {
  parryString = if (parryType == "No") parryType else "" + parry + parryType
  assert(st > 0, s"negative or 0 min ST value ($st in $name)")
}

/** Charlist subcontainer for melee damage stat, holds damage string, calculated on core stats level */
case class MeleeDamage(
                        attackType: String = "",
                        dmgDice: Int = 0,
                        dmgMod: Int = 0,
                        armorDiv: Float = 1,
                        dmgType: String = DamageType.CRUSHING,
                        var dmgString: String = "" // TODO: calculation on higher level
                      ) {
  assert(AttackType.isValid(attackType), s"invalid melee attack type ($attackType)")
  assert(dmgDice >= 0, s"melee damage's dice value is negative ($dmgDice)")
  assert(ArmorDivisors.isValid(armorDiv), s"invalid armor divisor value ($armorDiv)")
  assert(DamageType.isValid(dmgType), s"invalid damage type ($dmgType)")
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
  assert(acc >= 0, s"negative accuracy value ($acc in $name)")
  assert(accMod >= 0, s"negative scope bonus value ($accMod in $name)")
  assert(rcl > 0, s"negative or 0 recoil ($rcl in $name)")
  assert(st > 0, s"negative or 0 min ST value ($st in $name)")
  assert(malf < 18 && malf > 3, s"ranged attack's malfunction value is out of bounds ($malf in $name)")
}

/** Charlist subcontainer for ranged damage stat, calculates damage string */
case class RangedDamage(
                         dmgDice: Int = 0,
                         diceMult: Int = 1,
                         dmgMod: Int = 0,
                         armorDiv: Float = 1,
                         dmgType: String = DamageType.CRUSHING,
                         fragDice: Int = 0,
                         var dmgString: String = ""
                       ) {
  assert(dmgDice >= 0, s"damage's dice value is negative ($dmgDice)")
  assert(diceMult > 0, s"damage's dice multiplier is negative or 0 ($diceMult)")
  assert(ArmorDivisors.isValid(armorDiv), s"invalid armor divisor value ($armorDiv)")
  assert(DamageType.isValid(dmgType), s"invalid damage type ($dmgType)")
  assert(fragDice >= 0, s"negative fragmentation damage's dice value ($fragDice)")
  dmgString = if (dmgType == DamageType.SPECIAL) "spec."
  else
    (if (dmgType == DamageType.AFFLICTION) "HT"
    else s"${dmgDice}d${if (diceMult != 1) "x" + diceMult else ""}") +
      s"${"" + (if (dmgMod > 0) "+" else "") + (if (dmgMod != 0) dmgMod else "")}" +
      s"${if (armorDiv != 1) "(" + armorDiv + ")" else ""} $dmgType " +
      s"${if (fragDice > 0) "[" + fragDice + "d]" else ""}"
}

/** Charlist subnamespace that holds armor divisors validation method */
object ArmorDivisors {
  def isValid(div: Float) = Set(0.1, 0.2, 0.5, 1, 2, 3, 5, 10, 100).contains(div)
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
  assert(rof > 0, s"negative or 0 RoF ($rof)")
  assert(rofMult > 0, s"negative or 0 RoF's multiplier ($rofMult)")
  assert(!(rofJet && rofFA), s"invalid RoF's type: full auto and jet (true & true)")
  rofString = s"$rof${if (rofMult != 1) "x" + rofMult else ""}${if (rofFA) "!" else if (rofJet) " Jet" else ""}"
}

/** Charlist subcontainer for ranged attack shots stat, producing shots string and calculating carried ammunition
  * cost and weight */
case class RangedShots(
                        shots: Int = 1,
                        reload: String = "",
                        shotsLoaded: Int = 0,
                        shotsCarried: Int = 0,
                        shotWt: Float = 0,
                        shotCost: Double = 0,
                        var shotsString: String = "",
                        var totalWt: Float = 0,
                        var totalCost: Double = 0
                      ) {
  assert(shots >= 0, s"negative shots' number ($shots)")
  assert(shotsLoaded >= 0 && shotsLoaded <= shots, s"shots left value out of bounds ($shotsLoaded)")
  assert(shotsCarried >= 0, s"negative shots carried value ($shotsCarried)")
  assert(shotWt >= 0, s"negative weight per shot value ($shotWt)")
  assert(shotCost >= 0, s"negative cost per shot value ($shotCost)")
  shotsString = s"${if (shots != 0) "" + shotsLoaded + "/" + shots else ""}$reload " +
    s"${if (shotsCarried != 0) shotsCarried else ""}"
  totalWt = (shotsCarried + shotsLoaded) * shotWt
  totalCost = (shotsCarried + shotsLoaded) * shotCost
}

/** Charlist subcontainer for armor list's element, holds its stats */
case class Armor(
                  name: String = "",
                  carried: String = ItemState.EQUIPPED,
                  dr: Int = 0,
                  ep: Int = 0,
                  epi: Int = 0,
                  front: Boolean = true,
                  back: Boolean = true,
                  soft: Boolean = false,
                  locations: Seq[String] = Seq(),
                  hp: Int = 1,
                  hpLeft: Int = 1,
                  lc: Int = 0,
                  tl: Int = 0,
                  notes: String = "",
                  wt: Float = 0,
                  cost: Double = 0
                ) {
  assert(ItemState.isValid(carried), s"invalid armor's carrying state ($carried in $name)")
  assert(dr >= 0, s"negative armor's DR value ($dr in $name)")
  assert(ep >= 0, s"negative armor's EP value ($ep in $name)")
  assert(epi >= 0, s"negative armor's EPi value ($epi in $name)")
  assert(front || back, s"armor covers neither front nor back (false & false in $name)")
  assert(HitLocations.isValid(locations), s"invalid armor's locations string set ($locations in $name)")
  assert(hp >= 0, s"negative armor's HP value ($hp in $name)")
  assert(hpLeft >= 0 && hpLeft <= hp, s"armor's current HP value out of bounds ($hpLeft in $name)")
  assert(lc < 6 && lc >= 0, s"legality class value out of bounds ($lc in $name)")
  assert(tl >= 0 && tl < 13, s"tech level value out of bounds ($tl in $name)")
  assert(wt >= 0, s"negative item's weight ($wt in $name)")
  assert(cost >= 0, s"negative item's cost ($cost in $name)")
}

/** Charlist subnamespace that holds hit locations strings and validation method */
object HitLocations {
  val EYES = "eyes"
  val SKULL = "skull"
  val FACE = "face"
  val NECK = "face"
  val LEG_RIGHT = "right leg"
  val LEG_LEFT = "left leg"
  val LEGS = "legs"
  val ARM_RIGHT = "right arm"
  val ARM_LEFT = "left arm"
  val ARMS = "arms"
  val TORSO = "torso"
  val VITALS = "vitals"
  val ABDOMEN = "abdomen"
  val GROIN = "groin"
  val HANDS = "hands"
  val HAND_LEFT = "left hand"
  val HAND_RIGHT = "right hand"
  val FEET = "feet"
  val FOOT_RIGHT = "right foot"
  val FOOT_LEFT = "left foot"

  def isValid(loc: Seq[String]) = {
    val all = Set(EYES, SKULL, FACE, NECK, LEG_LEFT, LEG_RIGHT, LEGS, ARM_LEFT, ARM_RIGHT, ARMS, TORSO, VITALS,
      ABDOMEN, GROIN, HANDS, HAND_LEFT, HAND_RIGHT, FEET, FOOT_LEFT, FOOT_RIGHT)
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
                 lc: Int = 0,
                 tl: Int = 0,
                 notes: String = "",
                 wt: Float = 0,
                 cost: Double = 0,
                 n: Int = 1,
                 var totalWt: Float = 0,
                 var totalCost: Double = 0
               ) {
  assert(ItemState.isValid(carried), s"invalid item's carrying state ($carried in $name)")
  assert(dr >= 0, s"negative item's DR value ($dr in $name)")
  assert(hp >= 0, s"negative item's HP value ($hp in $name)")
  assert(hpLeft >= 0 && hpLeft <= hp, s"item's current HP value out of bounds ($hpLeft in $name)")
  assert(lc < 6 && lc >= 0, s"legality class value out of bounds ($lc in $name)")
  assert(tl >= 0 && tl < 13, s"tech level value out of bounds ($tl in $name)")
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

object Charlist {

  import play.api.libs.json.Json

  implicit val pairFormat = Json.format[(Int, Int)]
  // TODO: try with tuples
  implicit val itemFormat = Json.format[Item]
  implicit val armorElementFormat = Json.format[Armor]
  implicit val rangedShotsFormat = Json.format[RangedShots]
  implicit val rangedRoFFormat = Json.format[RangedRoF]
  implicit val rangedDamageFormat = Json.format[RangedDamage]
  implicit val rangedAttackFormat = Json.format[RangedAttack]
  implicit val meleeDamageFormat = Json.format[MeleeDamage]
  implicit val meleeAttackFormat = Json.format[MeleeAttack]
  implicit val meleeFormat = Json.format[Weapon]
  implicit val equipmentFormat = Json.format[Equipment]
  implicit val techniqueFormat = Json.format[Technique]
  implicit val skillFormat = Json.format[Skill]
  implicit val featuresFormat = Json.format[Feature]
  implicit val statIntFormat = Json.format[Stat[Int]]
  implicit val statFloatFormat = Json.format[Stat[Float]]
  implicit val statsFormat = Json.format[Stats]
  implicit val descriptionFormat = Json.format[Description]
  implicit val charlistFormat = Json.format[Charlist]

}
