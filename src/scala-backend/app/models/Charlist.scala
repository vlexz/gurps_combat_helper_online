package models

/**
  * Created by crimson on 9/23/16.
  */
object Charlist {

  case class CharlistData(
                           _id: String,
                           timestamp: Long,
                           player: String,
                           cp: Int,
                           name: String,
                           description: CharlistDescription,
                           coreStats: CharlistStats,
                           features: Seq[CharlistFeature],
                           skills: Seq[CharlistSkill],
                           techniques: Seq[CharlistTechnique],
                           equip: CharlistEquipment
                         )

  object CharlistFields {
    val ID = "_id"
    val TIMESTAMP = "timestamp"
    val PLAYER = "player"
    val CPTOTAL = "cp"
    val NAME = "name"
  }

  case class CharlistDescription(
                                  age: Int,
                                  height: String,
                                  weight: String,
                                  portrait: String,
                                  bio: String
                                )

  case class CharlistStats(
                            st: Int,
                            dx: Int,
                            iq: Int,
                            ht: Int,
                            will: Int,
                            per: Int,
                            db: Int,
                            reaction: Int,
                            hpMod: Int,
                            fpMod: Int,
                            liftMod: Int,
                            speedMod: Float,
                            moveMod: Int,
                            dodgeMod: Int,
                            parryMod: Int,
                            blockMod: Int,
                            hpLoss: Int,
                            fpLoss: Int
                          )

  case class CharlistFeature(
                              feature: String,
                              cp: Int
                            )

  case class CharlistSkill(
                            name: String,
                            attr: String,
                            diff: String,
                            cp: Int,
                            mod: Int
                          )

  case class CharlistTechnique(
                                name: String,
                                skill: String,
                                diff: String,
                                style: String,
                                cp: Int
                              )

  /** Charlist subcontainer for character possessions, calculates total weights and cost and holds armor, weapons, and all
    * possessions items subcontainers. */
  case class CharlistEquipment(
                                weapons: Seq[CharlistWeapon],
                                armor: Seq[CharlistArmor],
                                items: Seq[CharlistItem],
                                var totalCost: Double,
                                var totalCombWt: Float,
                                var totalTravWt: Float
                              ) {

    import CharlistItemState._

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
  case class CharlistWeapon(
                             name: String = "",
                             carried: String = CharlistItemState.STASH,
                             attacksMelee: Seq[CharlistMeleeAttack] = Seq(),
                             attacksRanged: Seq[CharlistRangedAttack] = Seq(),
                             grips: Seq[String] = Seq(), // For future functionality
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
    assert(CharlistItemState.isValid(carried), s"invalid weapon's carrying state ($name)")
    assert(bulk <= 0, s"positive bulk value ($name)")
    assert(db >= 0 && db < 4, s"defense bonus value out of bounds ($name)")
    assert(dr >= 0, s"negative item's DR value ($name)")
    assert(hp > 0, s"negative or 0 item's HP value ($name)")
    assert(hpLeft >= 0 && hpLeft <= hp, s"item's current HP value out of bounds ($name)")
    assert(lc < 6 && lc >= 0, s"legality class value out of bounds ($name)")
    assert(tl >= 0 && tl < 13, s"tech level value out of bounds ($name)")
    assert(wt >= 0, s"negative weapon's weight value ($name)")
    assert(cost >= 0, s"negative weapon's cost value ($name)")
    totalWt = wt
    attacksRanged.foreach(totalWt += _.shots.totalWt)
    totalCost = cost
    attacksRanged.foreach(totalCost += _.shots.totalCost)
  }

  /** Charlist subcontainer for melee attack type's stats, holds damage stats subcontainers and produces parry stat
    * string. */
  case class CharlistMeleeAttack(
                                  name: String = "",
                                  available: Boolean = false,
                                  damage: CharlistMeleeDamage = CharlistMeleeDamage(),
                                  followup: Seq[CharlistMeleeDamage] = Seq(),
                                  linked: Seq[CharlistMeleeDamage] = Seq(),
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
    assert(st > 0, s"negative or 0 min ST value ($name)")
  }

  /** Charlist subcontainer for melee damage stat, holds damage string, calculated on core stats level */
  case class CharlistMeleeDamage(
                                  attackType: String = "",
                                  dmgDice: Int = 0,
                                  dmgMod: Int = 0,
                                  armorDiv: Float = 1,
                                  dmgType: String = CharlistDamageType.CRUSHING,
                                  var dmgString: String = ""
                                ) {
    assert(CharlistAttackType.isValid(attackType), s"invalid melee attack type")
    assert(dmgDice >= 0, s"melee damage's dice value is negative")
    assert(CharlistArmorDivisors.contains(armorDiv), s"invalid armor divisor value")
    assert(CharlistDamageType.isValid(dmgType), s"invalid damage type")
  }

  /** Charlist subnamespace that holds melee attack types strings and validation method */
  object CharlistAttackType {
    val THRUSTING = "thr"
    val SWINGING = "sw"
    val WEAPON = ""

    def isValid(s: String) = Set(THRUSTING, SWINGING, WEAPON).contains(s)
  }

  /** Charlist subcontainer for ranged attack's stats, holds damage, RoF, and shots subcontainers */
  case class CharlistRangedAttack(
                                   name: String = "",
                                   available: Boolean = false, // For future functionality
                                   damage: CharlistRangedDamage = CharlistRangedDamage(),
                                   followup: Seq[CharlistRangedDamage] = Seq[CharlistRangedDamage](),
                                   linked: Seq[CharlistRangedDamage] = Seq[CharlistRangedDamage](),
                                   skill: String = "",
                                   acc: Int = 0,
                                   accMod: Int = 0,
                                   rng: String = "",
                                   rof: CharlistRangedRoF = CharlistRangedRoF(),
                                   rcl: Int = 2,
                                   shots: CharlistRangedShots = CharlistRangedShots(),
                                   st: Int = 10,
                                   hands: String = "",
                                   malf: Int = 18,
                                   notes: String = ""
                                 ) {
    assert(acc >= 0, s"negative accuracy value ($name)")
    assert(accMod >= 0, s"negative scope bonus value ($name)")
    assert(rcl > 0, s"negative or 0 recoil ($name)")
    assert(st > 0, s"negative or 0 min ST value ($name)")
    assert(malf < 18 && malf > 3, s"ranged attack's malfunction value is out of bounds ($name)")
  }

  /** Charlist subcontainer for ranged damage stat, calculates damage string */
  case class CharlistRangedDamage(
                                   dmgDice: Int = 0,
                                   diceMult: Int = 1,
                                   dmgMod: Int = 0,
                                   armorDiv: Float = 1,
                                   dmgType: String = CharlistDamageType.CRUSHING,
                                   fragDice: Int = 0,
                                   var dmgString: String = ""
                                 ) {
    assert(dmgDice >= 0, s"damage's dice value is negative")
    assert(diceMult > 0, s"damage's dice multiplier is negative or 0")
    assert(CharlistArmorDivisors.contains(armorDiv), s"invalid armor divisor value")
    assert(CharlistDamageType.isValid(dmgType), s"invalid damage type $dmgType")
    assert(fragDice >= 0, s"negative fragmentation damage's dice value")
    dmgString = if (dmgType == CharlistDamageType.SPECIAL) "spec."
    else
      (if (dmgType == CharlistDamageType.AFFLICTION) "HT"
      else s"${dmgDice}d${if (diceMult != 1) "x" + diceMult else ""}") +
        s"${"" + (if (dmgMod > 0) "+" else "") + (if (dmgMod != 0) dmgMod else "")}" +
        s"${if (armorDiv != 1) "(" + armorDiv + ")" else ""} $dmgType " +
        s"${if (fragDice > 0) "[" + fragDice + "d]" else ""}"
  }

  val CharlistArmorDivisors = Set(0.1, 0.2, 0.5, 1, 2, 3, 5, 10, 100)

  /** Charlist subnamespace that holds damage types strings and validation method */
  object CharlistDamageType {
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
  case class CharlistRangedRoF(
                                rof: Int = 1,
                                rofMult: Int = 1,
                                rofFA: Boolean = false,
                                rofJet: Boolean = false,
                                var rofString: String = ""
                              ) {
    assert(rof > 0, s"negative or 0 RoF")
    assert(rofMult > 0, s"negative or 0 RoF's multiplier")
    assert(!(rofJet && rofFA), s"invalid RoF's type: full auto and jet")
    rofString = s"$rof${if (rofMult != 1) "x" + rofMult else ""}${if (rofFA) "!" else if (rofJet) " Jet" else ""}"
  }

  /** Charlist subcontainer for ranged attack shots stat, producing shots string and calculating carried ammunition
    * cost and weight */
  case class CharlistRangedShots(
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
    assert(shots >= 0, s"negative shots' number")
    assert(shotsLoaded >= 0 && shotsLoaded <= shots, s"shots left value out of bounds")
    assert(shotsCarried >= 0, s"negative shots carried value")
    assert(shotWt >= 0, s"negative weight per shot value")
    assert(shotCost >= 0, s"negative cost per shot value")
    shotsString = s"${if (shots != 0) "" + shotsLoaded + "/" + shots else ""}$reload " +
      s"${if (shotsCarried != 0) shotsCarried else ""}"
    totalWt = (shotsCarried + shotsLoaded) * shotWt
    totalCost = (shotsCarried + shotsLoaded) * shotCost
  }

  /** Charlist subcontainer for armor list's element, holds its stats */
  case class CharlistArmor(
                            name: String = "",
                            carried: String = CharlistItemState.EQUIPPED,
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
    assert(CharlistItemState.isValid(carried), s"invalid armor's carrying state ($name)")
    assert(dr >= 0, s"negative armor's DR value ($name)")
    assert(ep >= 0, s"negative armor's EP value ($name)")
    assert(epi >= 0, s"negative armor's EPi value ($name)")
    assert(front || back, s"armor covers neither front nor back ($name)")
    assert(CharlistHitLocations.isValid(locations), s"invalid armor's locations string set ($name)")
    assert(hp >= 0, s"negative armor's HP value ($name)")
    assert(hpLeft >= 0 && hpLeft <= hp, s"armor's current HP value out of bounds ($name)")
    assert(lc < 6 && lc >= 0, s"legality class value out of bounds ($name)")
    assert(tl >= 0 && tl < 13, s"tech level value out of bounds ($name)")
    assert(wt >= 0, s"negative item's weight ($name)")
    assert(cost >= 0, s"negative item's cost ($name)")
  }

  /** Charlist subnamespace that holds hit locations strings and validation method */
  object CharlistHitLocations {
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
  case class CharlistItem(
                           name: String = "",
                           carried: String = CharlistItemState.STASH,
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
    assert(CharlistItemState.isValid(carried), s"invalid item's carrying state ($name)")
    assert(dr >= 0, s"negative item's DR value ($name)")
    assert(hp >= 0, s"negative item's HP value ($name)")
    assert(hpLeft >= 0 && hpLeft <= hp, s"item's current HP value out of bounds ($name)")
    assert(lc < 6 && lc >= 0, s"legality class value out of bounds ($name)")
    assert(tl >= 0 && tl < 13, s"tech level value out of bounds ($name)")
    assert(wt >= 0, s"negative item's weight ($name)")
    assert(cost >= 0, s"negative item's cost ($name)")
    assert(n >= 0, s"negative item's quantity ($name)")
    totalWt = wt * n
    totalCost = cost * n
  }

  /** Charlist subnamespace that holds item carrying states strings and validation method */
  object CharlistItemState {
    val READY = "Ready"
    val EQUIPPED = "Equipped"
    val COMBAT = "Combat"
    val TRAVEL = "Travel"
    val STASH = "Stash"

    def isValid(key: String) = Set(READY, EQUIPPED, COMBAT, TRAVEL, STASH).contains(key)
  }

  import play.api.libs.json.Json

  implicit val itemFormat = Json.format[CharlistItem]
  implicit val armorElementFormat = Json.format[CharlistArmor]
  implicit val rangedShotsFormat = Json.format[CharlistRangedShots]
  implicit val rangedRoFFormat = Json.format[CharlistRangedRoF]
  implicit val rangedDamageFormat = Json.format[CharlistRangedDamage]
  implicit val rangedAttackFormat = Json.format[CharlistRangedAttack]
  implicit val meleeDamageFormat = Json.format[CharlistMeleeDamage]
  implicit val meleeAttackFormat = Json.format[CharlistMeleeAttack]
  implicit val meleeFormat = Json.format[CharlistWeapon]
  implicit val equipmentFormat = Json.format[CharlistEquipment]
  implicit val techniqueFormat = Json.format[CharlistTechnique]
  implicit val skillFormat = Json.format[CharlistSkill]
  implicit val featuresFormat = Json.format[CharlistFeature]
  implicit val statsFormat = Json.format[CharlistStats]
  implicit val descriptionFormat = Json.format[CharlistDescription]
  implicit val charlistFormat = Json.format[CharlistData]

}