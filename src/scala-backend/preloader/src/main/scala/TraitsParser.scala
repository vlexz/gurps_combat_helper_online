import models.charlist._
import play.api.libs.json.Writes

import scala.collection.breakOut
import scala.collection.immutable.Seq
import scala.xml.{Node, XML}

/**
  * Created by crimson on 12/8/16.
  */
class TraitsParser(filePath: String) extends Parser[Trait](filePath) {
  private def parseTraitMod(n: Node) = ( // TODO: weapon parser missing
    for (b <- n \ "attribute_bonus") yield BonusAttribute(
      attr = (b \ "attribute").text,
      bonus = this parseDouble (b \ "amount").text,
      perLvl = (b \ "amount" \ "@per_level").text == "yes"),
    for (b <- n \ "skill_bonus") yield BonusSkill(
      skill = (b \ "name").text,
      skillCompare = (b \ "name" \ "@compare").text,
      spc = (b \ "specialization").text,
      spcCompare = (b \ "specialization" \ "@compare").text,
      perLvl = (b \ "amount" \ "@per_level").text == "yes",
      bonus = this parseInt (b \ "amount").text),
    for (b <- n \ "weapon_bonus") yield BonusDamage(
      skill = (b \ "name").text,
      skillCompare = (b \ "name" \ "@compare").text,
      spc = (b \ "specialization").text,
      spcCompare = (b \ "specialization" \ "@compare").text,
      relSkill = this parseInt (b \ "level").text,
      perDie = (b \ "amount" \ "@per_die").text == "yes",
      bonus = this parseInt (b \ "amount").text),
    for (b <- n \ "dr_bonus") yield BonusDR(
      locations = Seq((b \ "location").text),
      perLvl = (b \ "amount" \ "@per_level").text == "yes",
      protection = DrSet(this parseInt (b \ "amount").text)),
    for (b <- n \ "cost_reduction") yield BonusAttributeCost(
      attr = (b \ "attribute").text,
      cost = this parseInt (b \ "percentage").text),
    for (b <- n \ "reaction_bonus") yield BonusReaction(
      affected = (b \ "affected").text,
      reputation = (b \ "affected" \ "@reputation").text == "yes",
      perLvl = (b \ "amount" \ "@per_level").text == "yes",
      freq = this parseInt (b \ "frequency").text,
      bonus = this parseInt (b \ "amount").text,
      notes = (b \ "notes").text))

  println("Parsing traits...")
  val seq: Seq[Trait] =
    for (adv <- (XML load (getClass getResourceAsStream filePath)) \ "advantage") yield Trait(
      name = (adv \ "name").text,
      types = (adv \ "type").text split ", " flatMap (_ split "/"),
      category = adv \ "categories" \ "category" map (_.text) match {
        case x if x contains "Advantage" => "Advantage"
        case x if x contains "Disadvantage" => "Disadvantage"
        case x => x.headOption getOrElse ""
      },
      switch = (adv \ "name" \ "@switchability").text,
      ref = (adv \ "reference").text,
      notes = (adv \ "notes").text,
      active = false,
      cpBase = this parseInt (adv \ "base_points").text,
      level = this parseInt (adv \ "levels").text,
      cpPerLvl = this parseInt (adv \ "points_per_level").text,
      modifiers = {
        val (atrBns, sklBns, dmgBns, drBns, atrCMd, rctBns) = this parseTraitMod adv
        if ((atrBns :: sklBns :: dmgBns :: drBns :: atrCMd :: rctBns :: Nil) forall (_.isEmpty)) Seq()
        else Seq(TraitModifier(
          name = "Default",
          attrBonuses = atrBns,
          skillBonuses = sklBns,
          dmgBonuses = dmgBns,
          drBonuses = drBns,
          attrCostMods = atrCMd,
          reactBonuses = rctBns))
      } ++
        ((adv \ "cr") flatMap { _ =>
          TraitModifier(on = false,
            name = "CR 6",
            ref = "BS121",
            costType = TraitModifierCostType.MULTIPLIER,
            cost = 2.0) +:
            TraitModifier(
              on = false,
              name = "CR 9",
              ref = "BS121",
              costType = TraitModifierCostType.MULTIPLIER,
              cost = 1.5) +:
            TraitModifier(
              name = "CR 12",
              ref = "BS121",
              costType = TraitModifierCostType.MULTIPLIER,
              cost = 1.0) +:
            TraitModifier(
              on = false,
              name = "CR 15",
              ref = "BS121",
              costType = TraitModifierCostType.MULTIPLIER,
              cost = 0.5) +: Nil
        }) ++
        (for (mod <- adv \ "modifier") yield {
          val (atrBns, sklBns, dmgBns, drBns, atrCMd, rctBns) = this parseTraitMod mod
          TraitModifier(
            on = false,
            name = (mod \ "name").text,
            ref = (mod \ "reference").text,
            notes = (mod \ "notes").text,
            level = this parseInt (mod \ "levels").text,
            attrBonuses = atrBns,
            skillBonuses = sklBns,
            dmgBonuses = dmgBns,
            drBonuses = drBns,
            attrCostMods = atrCMd,
            affects = (mod \ "affects").text,
            costType = (mod \ "cost" \ "@type").text,
            cost = this parseDouble (mod \ "cost").text,
            reactBonuses = rctBns)
        }) (breakOut))
  override val tjs: Writes[Trait] = models.charlist.Charlist.traitFormat
}