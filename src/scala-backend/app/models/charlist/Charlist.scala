package models.charlist

/**
  * Created by crimson on 9/23/16.
  */
case class Charlist(
                     _id: String = "",
                     timestamp: Long = 0,
                     player: String = "",
                     name: String = "",
                     cp: Int = 0,
                     var cpTotal: Int = 0,
                     description: Description = Description(),
                     stats: Stats = Stats(),
                     traits: Seq[Trait] = Seq(),
                     skills: Seq[Skill] = Seq(),
                     techniques: Seq[Technique] = Seq(),
                     equip: Equipment = Equipment(),
                     conditions: Conditions = Conditions()
                   ) {
  //noinspection ScalaRedundantConversion
  val thr = stats.strikeSt.value match {
    case x if x < 1 => (0, 0)
    case x if x < 11 => (1, ((x - 1) / 2).toInt - 6)
    case x => (((x - 3) / 8).toInt, ((x - 3) / 2).toInt % 4 - 1)
  }
  //noinspection ScalaRedundantConversion
  val sw = stats.strikeSt.value match {
    case x if x < 1 => (0, 0)
    case x if x < 9 => (1, ((x - 1) / 2).toInt - 5)
    case x => (((x - 5) / 4).toInt, (x - 5) % 4 - 1)
  }
  stats.calcDmg(thr, sw)
  stats.calcEncumbrance(equip.totalCombWt, equip.totalTravWt)
  skills.foreach(s => s.calcLvl(s.attr match {
    case a if a == SkillBaseAttributes.ST => stats.st.value
    case a if a == SkillBaseAttributes.DX => stats.dx.value
    case a if a == SkillBaseAttributes.IQ => stats.iq.value
    case a if a == SkillBaseAttributes.HT => stats.ht.value
    case a if a == SkillBaseAttributes.WILL => stats.will.value
    case a if a == SkillBaseAttributes.PER => stats.per.value
  }))
  techniques.foreach(t => t.calcLvl(skills.find(_.name == t.skill).getOrElse(Skill()).lvl))
  cpTotal = 0
  cpTotal += stats.cp
  traits.foreach(cpTotal += _.cp)
  skills.foreach(cpTotal += _.cp)
  techniques.foreach(cpTotal += _.cp)
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
                  st: StatInt = StatInt(),
                  dx: StatInt = StatInt(),
                  iq: StatInt = StatInt(),
                  ht: StatInt = StatInt(),
                  will: StatInt = StatInt(),
                  per: StatInt = StatInt(),
                  liftSt: StatInt = StatInt(),
                  strikeSt: StatInt = StatInt(),
                  var thrDmg: String = "",
                  var swDmg: String = "",
                  var bl: Int = 0,
                  var combatEncumbrance: Int = 0,
                  var travelEncumbrance: Int = 0,
                  hp: StatPoints = StatPoints(),
                  fp: StatPoints = StatPoints(),
                  basicSpeed: StatFrac = StatFrac(),
                  basicMove: StatInt = StatInt(),
                  basicDodge: StatInt = StatInt(),
                  var combMove: Int = 0,
                  var travMove: Int = 0,
                  var dodge: Int = 0,
                  sm: Int = 0
                ) {
  st.calcStat(10, 10)
  dx.calcStat(10, 20)
  iq.calcStat(10, 20)
  ht.calcStat(10, 10)
  will.calcStat(10, 5)
  per.calcStat(10, 5)
  liftSt.calcStat(st.value, 3)
  strikeSt.calcStat(st.value, 5)
  bl = math.round(liftSt.value ^ 2 / 5)
  hp.calcStat(st.value, 2)
  fp.calcStat(ht.value, 3)
  basicSpeed.calcStat((dx.value + ht.value) / 4, 20)
  //noinspection ScalaRedundantConversion
  basicMove.calcStat(basicSpeed.value.toInt, 5)
  //noinspection ScalaRedundantConversion
  basicDodge.calcStat(basicSpeed.value.toInt + 3, 15)

  def calcDmg(thr: (Int, Int), sw: (Int, Int)) = {
    thrDmg = s"${thr._1}d${if (thr._2 < 0) thr._2 else if (thr._2 == 0) "" else "+" + thr._2}"
    swDmg = s"${sw._1}d${if (sw._2 < 0) sw._2 else if (sw._2 == 0) "" else "+" + sw._2}"
    this
  }

  def calcEncumbrance(combWt: Float, travWt: Float) = {
    assert(combWt < bl * 10, s"combat encumbrance value is over 10 times basic lift ($combWt, $bl)")
    assert(travWt < bl * 10, s"travel encumbrance value is over 10 times basic lift ($travWt, $bl)")
    combatEncumbrance = combWt / bl match {
      case x if x < 1 => 0
      case x if x < 2 => 1
      case x if x < 3 => 2
      case x if x < 6 => 3
      case x if x < 10 => 4
    }
    travelEncumbrance = travWt / bl match {
      case x if x < 1 => 0
      case x if x < 2 => 1
      case x if x < 3 => 2
      case x if x < 6 => 3
      case x if x < 10 => 4
    }
    combMove =
      math
        .ceil(
          basicMove.value / 5 * (5 - combatEncumbrance) / (if (hp.compromised || fp.compromised) 2 else 1)
        )
        .toInt
    travMove =
      math
        .ceil(
          basicMove.value / 5 * (5 - travelEncumbrance) / (if (hp.compromised || fp.compromised) 2 else 1)
        )
        .toInt
    dodge =
      math
        .ceil(
          (basicDodge.value - combatEncumbrance) / (if (hp.compromised || fp.compromised) 2 else 1)
        )
        .toInt
    this
  }

  def cp = st.cp + dx.cp + iq.cp + ht.cp + will.cp + per.cp + liftSt.cp + strikeSt.cp + hp.cp + fp.cp +
    basicSpeed.cp + basicMove.cp + basicDodge.cp
}

/** Charlist subcontainer for character attribute storage */
case class StatInt(
                    delta: Int = 0,
                    bonus: Int = 0,
                    notes: String = "",
                    var value: Int = 0,
                    var cp: Int = 0
                  ) {

  def calcStat(default: Int, cost: Int) = {
    value = default + delta + bonus
    cp = delta * cost
    this
  }
}

/** Charlist subcontainer for character attribute storage */
case class StatFrac(
                     delta: Float = 0,
                     bonus: Float = 0,
                     notes: String = "",
                     var value: Float = 0,
                     var cp: Int = 0
                   ) {

  def calcStat(default: Float, cost: Int) = {
    value = default + delta + bonus
    cp = (delta * cost).toInt
    this
  }
}

/** Charlist subcontainer for HP and FP attributes' stats */
case class StatPoints(
                       delta: Int = 0,
                       bonus: Int = 0,
                       notes: String = "",
                       var value: Int = 0,
                       var cp: Int = 0,
                       lost: Int = 0,
                       var compromised: Boolean = false,
                       var collapsing: Boolean = false
                     ) {
  def calcStat(default: Int, cost: Int) = {
    value = default + delta + bonus
    cp = delta * cost
    if (value * 2 / 3 < lost) compromised = true
    if (value <= lost) collapsing = true
    this
  }
}

/** Charlist subcontainer for features list's element stats */
case class Trait(
                  name: String = "",
                  cp: Int = 0
                  )

/** Charlist subcontainer for skills list's element stats, calculates its relative level */
case class Skill(
                  name: String = "",
                  attr: String = SkillBaseAttributes.DX,
                  diff: String = SkillDifficulties.EASY,
                  defaults: Seq[String] = Seq(), // For future functionality
                  prerequisites: Seq[String] = Seq(), // For future functionality
                  bonus: Int = 0,
                  notes: String = "",
                  cp: Int = 1,
                  var relLvl: Int = 0,
                  var lvl: Int = 0
                ) {
  assert(SkillBaseAttributes.isValid(attr), s"invalid skill's attribute ($attr in $name)")
  assert(SkillDifficulties.isValid(diff), s"invalid skill's difficulty ($diff in $name")
  assert(cp >= 0, s"skill's CP value is negative or 0 ($cp in $name")
  //noinspection ScalaRedundantConversion
  relLvl = SkillDifficulties.values(diff) + (if (cp > 1) 1 else 0) + (cp / 4).toInt + bonus

  def calcLvl(attrVal: Int) = {
    lvl = attrVal + relLvl
    this
  }
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
                      var lvl: Int = 0
                    ) {
  assert(TechniqueDifficulties.isValid(diff), s"invalid technique's difficulty string ($diff in $name)")
  assert(cp >= 0 && cp <= (maxLvl - defLvl + (if (diff == TechniqueDifficulties.HARD) 1 else 0)),
    s"technique's cp value out of bounds ($cp in $name")
  relLvl = math.max(cp + defLvl - (if (diff == TechniqueDifficulties.HARD) 1 else 0), defLvl)

  def calcLvl(skill: Int) = {
    lvl = skill + relLvl
    this
  }
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
                      var totalTravWt: Float = 0,
                      var totalDb: Int = 0
                    ) {

  import ItemCarryingStates._

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
    .withFilter(item => equip.contains(item.carried))
    .foreach(totalDb += _.db)
  armor
    .withFilter(item => equip.contains(item.carried))
    .foreach(totalDb += _.db)
}

/** Charlist subcontainer for weapons list's element, calculates weapon weight and cost including ammunition if
  * applicable, holds all its stats and attacks it can make as subcontainers. */
case class Weapon(
                   name: String = "",
                   carried: String = ItemCarryingStates.STASH,
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
  assert(ItemCarryingStates.isValid(carried), s"invalid weapon's carrying state ($carried in $name)")
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
                        var dmgString: String = ""
                      ) {
  assert(AttackType.isValid(attackType), s"invalid melee attack type ($attackType)")
  assert(dmgDice >= 0, s"melee damage's dice value is negative ($dmgDice)")
  assert(ArmorDivisors.isValid(armorDiv), s"invalid armor divisor value ($armorDiv)")
  assert(DamageType.isValid(dmgType), s"invalid damage type ($dmgType)")

  def calcDmg(thr: (Int, Int), sw: (Int, Int)) = {
    import AttackType._
    var dice = dmgDice
    var mod = dmgMod
    if (attackType == THRUSTING) {
      dice += thr._1
      mod += thr._2
    } else if (attackType == SWINGING) {
      dice += sw._1
      mod += sw._2
    }
    if (mod > 0) {
      dice += (mod / 3.5).toInt
      mod = (mod % 3.5).toInt
    }
    assert(dice > 0 || mod >= 0, s"invalid weapon melee damage stats")
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
                  carried: String = ItemCarryingStates.EQUIPPED,
                  db: Int = 0,
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
  assert(ItemCarryingStates.isValid(carried), s"invalid armor's carrying state ($carried in $name)")
  assert(db >= 0 && db < 4, s"defense bonus value out of bounds ($db in $name)")
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
  val HEAD = "head"
  val NECK = "face"
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

  def isValid(loc: Seq[String]) = {
    val all = Set(EYES, SKULL, FACE, HEAD, NECK, LEG_LEFT, LEG_RIGHT, LEGS, ARM_LEFT, ARM_RIGHT, ARMS, CHEST, VITALS,
      ABDOMEN, GROIN, TORSO, HANDS, HAND_LEFT, HAND_RIGHT, FEET, FOOT_LEFT, FOOT_RIGHT)
    loc.forall(all.contains)
  }
}

/** Charlist subcontainer for items list's element, holds its stats and calculates element's total weight and cost */
case class Item(
                 name: String = "",
                 carried: String = ItemCarryingStates.STASH,
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
  assert(ItemCarryingStates.isValid(carried), s"invalid item's carrying state ($carried in $name)")
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
object ItemCarryingStates {
  val READY = "Ready"
  val EQUIPPED = "Equipped"
  val COMBAT = "Combat"
  val TRAVEL = "Travel"
  val STASH = "Stash"

  def isValid(key: String) = Set(READY, EQUIPPED, COMBAT, TRAVEL, STASH).contains(key)
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
                       posture: String = Postures.STANDING,
                       closeCombat: Boolean = false,
                       grappled: Boolean = false,
                       pinned: Boolean = false,
                       sprinting: Boolean = false,
                       mounted: Boolean = false
                     ) {
  assert(shock >= 0 && shock <= 4, s"shock value out of bounds ($shock)")
  assert(Postures.isValid(posture), s"invalid posture string ($posture)")
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
object Postures {
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
  implicit val meleeFormat = Json.format[Weapon]
  implicit val equipmentFormat = Json.format[Equipment]
  implicit val techniqueFormat = Json.format[Technique]
  implicit val skillFormat = Json.format[Skill]
  implicit val featuresFormat = Json.format[Trait]
  implicit val statIntFormat = Json.format[StatInt]
  implicit val statFracFormat = Json.format[StatFrac]
  implicit val statPointsFormat = Json.format[StatPoints]
  implicit val statsFormat = Json.format[Stats]
  implicit val descriptionFormat = Json.format[Description]
  implicit val charlistFormat = Json.format[Charlist]

}
