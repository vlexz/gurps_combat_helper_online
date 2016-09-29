package models

import play.api.libs.json.Json

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
                           melee: Seq[CharlistMelee],
                           ranged: Seq[CharlistRanged],
                           armor: Seq[CharlistArmor],
                           items: Seq[CharlistItem]
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

  case class CharlistMelee(
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

  case class CharlistRanged(
                             name: String,
                             attacks: Seq[CharlistRangedAttack],
                             bulk: Int,
                             notes: String,
                             wt: Float,
                             cost: Double
                           )

  case class CharlistRangedAttack(
                                   name: String,
                                   damage: CharlistRangedDamage,
                                   followup: CharlistRangedDamage,
                                   linked: CharlistRangedDamage,
                                   skill: String,
                                   acc: Int,
                                   rng: String,
                                   rof: Int,
                                   rofMult: Int,
                                   rofFA: Boolean,
                                   rcl: Int,
                                   shots: Int,
                                   reload: String,
                                   shotsLeft: Int,
                                   st: Int,
                                   malf: Int,
                                   notes: String,
                                   shotWt: Float,
                                   shotCost: Double
                                 ) {
    assert(acc >= 0, s"negative accuracy value ($name)")
    assert(rof > 0, s"negative or 0 ROF ($name)")
    assert(rofMult > 0, s"negative or 0 RoF multiplier ($name)")
    assert(rcl > 0, s"negative or 0 Recoil ($name)")
    assert(shots > 0, s"negative or 0 shots number ($name)")
    assert(shotsLeft >= 0 && shotsLeft <= shots, s"shots left value is out of bounds ($name)")
    assert(st > 0, s"negative min ST value ($name)")
    assert(malf > 18 && malf < 3, s"malfunction value is out of bounds ($name)")
    assert(shotWt < 0, s"negative weight per shot value ($name)")
    assert(shotCost < 0, s"negative cost per shot value ($name)")
    val rofString =
  }

  case class CharlistRangedDamage(
                                   apply: Boolean,
                                   dmgDice: Int,
                                   diceMult: Int,
                                   dmgMod: Int,
                                   armorDiv: Float,
                                   dmgType: String,
                                   fragDice: Int
                                 ) {
    assert(dmgDice >= 0, s"negative damage dice value")
    assert(diceMult >= 0, s"negative damage dice multiplier")
    assert(CharlistArmorDivisors.contains(armorDiv), s"invalid armor divisor value")
    assert(CharlistDamageType.isValid(dmgType), s"invalid damage type $dmgType")
    assert(fragDice >= 0, s"negative fragmentation damage dice value")
    val dmgString =
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

  case class CharlistArmor(
                            name: String,
                            carried: String,
                            dr: Int,
                            ep: Int,
                            epi: Int,
                            soft: Boolean,
                            locations: Seq[Int],
                            notes: String,
                            wt: Float,
                            cost: Double
                          ) {
    assert(CharlistItemState.isValid(carried), s"invalid item carry state ($name)")
    assert(dr >= 0, s"negative armor DR value ($name)")
    assert(ep >= 0, s"negative armor EP value ($name)")
    assert(epi >= 0, s"negative armor EPi value ($name)")
    assert(wt >= 0, s"negative item weight ($name)")
    assert(cost >= 0, s"negative item cost ($name)")
  }

  case class CharlistItem(
                           name: String,
                           carried: String,
                           notes: String,
                           wt: Float,
                           cost: Double,
                           n: Int,
                           var totalWt: Float,
                           var totalCost: Double
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

  implicit val itemFormat = Json.format[CharlistItem]
  implicit val armorFormat = Json.format[CharlistArmor]
  implicit val rangedDamageFormat = Json.format[CharlistRangedDamage]
  implicit val rangedAttackFormat = Json.format[CharlistRangedAttack]
  implicit val rangedFormat = Json.format[CharlistRanged]
  implicit val meleeDamageFormat = Json.format[CharlistMeleeDamage]
  implicit val meleeAttackFormat = Json.format[CharlistMeleeAttack]
  implicit val meleeFormat = Json.format[CharlistMelee]
  implicit val techniqueFormat = Json.format[CharlistTechnique]
  implicit val skillFormat = Json.format[CharlistSkill]
  implicit val featuresFormat = Json.format[CharlistFeature]
  implicit val statsFormat = Json.format[CharlistStats]
  implicit val descriptionFormat = Json.format[CharlistDescription]
  implicit val charlistFormat = Json.format[CharlistData]

}