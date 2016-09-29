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

  case class CharlistEquipment(
                                melee: Seq[CharlistMeleeWeapon],
                                ranged: Seq[CharlistRangedWeapon],
                                armor: Seq[CharlistArmorElement],
                                items: Seq[CharlistItem],
                                var totalCost: Double,
                                var totalCombWt: Float,
                                var totalTravWt: Float
                              ) {

    import CharlistItemState._

    totalCost = 0
    items.foreach(totalCost += _.totalCost)
    totalCombWt = 0
    items
      .filter(item => Set(READY, EQUIPPED, COMBAT).contains(item.carried))
      .foreach(totalCombWt += _.totalWt)
    totalTravWt = totalCombWt
    items
      .filter(_.carried == TRAVEL)
      .foreach(totalTravWt += _.totalWt)
  }

  case class CharlistMeleeWeapon(
                                  name: String,
                                  attacks: Seq[CharlistMeleeAttack],
                                  notes: String,
                                  wt: Float,
                                  cost: Double
                                )

  case class CharlistMeleeAttack(
                                  name: String,
                                  damage: CharlistMeleeDamage,
                                  followup: CharlistMeleeDamage,
                                  linked: CharlistMeleeDamage,
                                  skill: String,
                                  parry: Int,
                                  block: Boolean,
                                  unbalanced: Boolean,
                                  hands: String,
                                  reach: String,
                                  notes: String
                                )

  case class CharlistMeleeDamage(
                                  apply: Boolean,
                                  attackType: String,
                                  dmgDice: Int,
                                  dmgMod: Int,
                                  armorDiv: Float,
                                  dmgType: String
                                )

  case class CharlistRangedWeapon(
                                   name: String = "",
                                   attacks: Seq[CharlistRangedAttack] = Seq(),
                                   bulk: Int = 0,
                                   notes: String = "",
                                   wt: Float = 0,
                                   cost: Double = 0,
                                   var totalWt: Float = 0,
                                   var totalCost: Double = 0
                                 ) {
    assert(bulk > 0, s"positive bulk value ($name)")
    assert(wt < 0, s"negative weapon weight value ($name)")
    assert(cost < 0, s"negative weapon cost value ($name)")
    totalWt = wt
    attacks.foreach(totalWt += _.shots.totalWt)
    totalCost = cost
    attacks.foreach(totalCost += _.shots.totalCost)
  }

  case class CharlistRangedAttack(
                                   name: String = "",
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
    assert(rcl > 0, s"negative or 0 Recoil ($name)")
    assert(st > 0, s"negative min ST value ($name)")
    assert(malf > 18 && malf < 3, s"malfunction value is out of bounds ($name)")
  }

  case class CharlistRangedDamage(
                                   dmgDice: Int = 0,
                                   diceMult: Int = 1,
                                   dmgMod: Int = 0,
                                   armorDiv: Float = 1,
                                   dmgType: String = CharlistDamageType.CRUSHING,
                                   fragDice: Int = 0,
                                   var dmgString: String = ""
                                 ) {
    assert(dmgDice >= 0, s"negative damage dice value")
    assert(diceMult >= 0, s"negative damage dice multiplier")
    assert(CharlistArmorDivisors.contains(armorDiv), s"invalid armor divisor value")
    assert(CharlistDamageType.isValid(dmgType), s"invalid damage type $dmgType")
    assert(fragDice >= 0, s"negative fragmentation damage dice value")
    dmgString =
      (if (dmgType == CharlistDamageType.AFFLICTION) "HT"
      else s"${dmgDice}d${if (diceMult != 1) "x" + diceMult else ""}") +
        s"${"" + (if (dmgMod > 0) "+" else "") + (if (dmgMod != 0) dmgMod else "")}" +
        s"${if (armorDiv != 1) "(" + armorDiv + ")" else ""} $dmgType " +
        s"${if (fragDice > 0) "[" + fragDice + "d]" else ""}"
  }

  val CharlistArmorDivisors = Set(0.1, 0.2, 0.5, 1, 2, 3, 5, 10, 100)

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

  case class CharlistRangedRoF(
                                rof: Int = 1,
                                rofMult: Int = 1,
                                rofFA: Boolean = false,
                                rofJet: Boolean = false,
                                var rofString: String = ""
                              ) {
    assert(rof > 0, s"negative or 0 ROF")
    assert(rofMult > 0, s"negative or 0 RoF multiplier")
    rofString = s"$rof${if (rofMult != 1) "x" + rofMult else ""}${if (rofFA) "!" else if (rofJet) " Jet" else ""}"
  }

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
    assert(shots >= 0, s"negative shots number")
    assert(shotsLoaded >= 0 && shotsLoaded <= shots, s"shots left value is out of bounds")
    assert(shotsCarried >= 0, s"negative shots carried number")
    assert(shotWt < 0, s"negative weight per shot value")
    assert(shotCost < 0, s"negative cost per shot value")
    shotsString = s"${if (shots != 0) "" + shotsLoaded + "/" + shots else ""}$reload " +
      s"${if (shotsCarried != 0) shotsCarried else ""}"
    totalWt = (shotsCarried + shotsLoaded) * shotWt
    totalCost = (shotsCarried + shotsLoaded) * shotCost
  }

  case class CharlistArmorElement(
                                   name: String = "",
                                   carried: String = CharlistItemState.EQUIPPED,
                                   dr: Int = 0,
                                   ep: Int = 0,
                                   epi: Int = 0,
                                   front: Boolean = true,
                                   back: Boolean = true,
                                   soft: Boolean = false,
                                   locations: Seq[String] = Seq(),
                                   notes: String = "",
                                   wt: Float = 0,
                                   cost: Double = 0
                                 ) {
    assert(CharlistItemState.isValid(carried), s"invalid armor item carrying state ($name)")
    assert(dr >= 0, s"negative armor DR value ($name)")
    assert(ep >= 0, s"negative armor EP value ($name)")
    assert(epi >= 0, s"negative armor EPi value ($name)")
    assert(wt >= 0, s"negative item weight ($name)")
    assert(cost >= 0, s"negative item cost ($name)")
    assert(CharlistHitLocations.isValid(locations), s"invalid armor locations string set ($name)")
  }

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

  case class CharlistItem(
                           name: String = "",
                           carried: String = CharlistItemState.STASH,
                           notes: String = "",
                           wt: Float = 0,
                           cost: Double = 0,
                           n: Int = 1,
                           var totalWt: Float = 0,
                           var totalCost: Double = 0
                         ) {
    assert(CharlistItemState.isValid(carried), s"invalid item carry state ($name)")
    assert(wt >= 0, s"negative item weight ($name)")
    assert(cost >= 0, s"negative item cost ($name)")
    assert(n >= 0, s"negative item quantity ($name)")
    totalWt = wt * n
    totalCost = cost * n
  }

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
  implicit val armorElementFormat = Json.format[CharlistArmorElement]
  implicit val rangedShotsFormat = Json.format[CharlistRangedShots]
  implicit val rangedRoFFormat = Json.format[CharlistRangedRoF]
  implicit val rangedDamageFormat = Json.format[CharlistRangedDamage]
  implicit val rangedAttackFormat = Json.format[CharlistRangedAttack]
  implicit val rangedFormat = Json.format[CharlistRangedWeapon]
  implicit val meleeDamageFormat = Json.format[CharlistMeleeDamage]
  implicit val meleeAttackFormat = Json.format[CharlistMeleeAttack]
  implicit val meleeFormat = Json.format[CharlistMeleeWeapon]
  implicit val equipmentFormat = Json.format[CharlistEquipment]
  implicit val techniqueFormat = Json.format[CharlistTechnique]
  implicit val skillFormat = Json.format[CharlistSkill]
  implicit val featuresFormat = Json.format[CharlistFeature]
  implicit val statsFormat = Json.format[CharlistStats]
  implicit val descriptionFormat = Json.format[CharlistDescription]
  implicit val charlistFormat = Json.format[CharlistData]

}