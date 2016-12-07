import models.charlist.Charlist.traitFormat
import models.charlist._
import org.mongodb.scala._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json

import scala.collection.breakOut
import scala.collection.immutable.Seq
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.xml.{Node, XML}

/**
  * Created by crimson on 11/15/16.
  */
object Preloader extends App {
  private val parseInt: (String => Int) = {
    case "" => 0
    case x => x.toInt
  }
  private val parseDouble: (String => Double) = {
    case "" => 0.0
    case x => x.toDouble
  }
  private val parseMod = (n: Node) => ( // TODO: no weapon parser
    for (b <- n \ "attribute_bonus") yield BonusAttribute(
      attr = (b \ "attribute").text,
      bonus = Preloader parseDouble (b \ "amount").text,
      perLvl = (b \ "amount" \ "@per_level").text == "yes"),
    for (b <- n \ "skill_bonus") yield BonusSkill(
      skill = (b \ "name").text,
      skillCompare = (b \ "name" \ "@compare").text,
      spc = (b \ "specialization").text,
      spcCompare = (b \ "specialization" \ "@compare").text,
      perLvl = (b \ "amount" \ "@per_level").text == "yes",
      bonus = Preloader parseInt (b \ "amount").text),
    for (b <- n \ "weapon_bonus") yield BonusDamage(
      skill = (b \ "name").text,
      skillCompare = (b \ "name" \ "@compare").text,
      spc = (b \ "specialization").text,
      spcCompare = (b \ "specialization" \ "@compare").text,
      relSkill = Preloader parseInt (b \ "level").text,
      perDie = (b \ "amount" \ "@per_die").text == "yes",
      bonus = Preloader parseInt (b \ "amount").text),
    for (b <- n \ "dr_bonus") yield BonusDR(
      locations = Seq((b \ "location").text),
      perLvl = (b \ "amount" \ "@per_level").text == "yes",
      protection = DrSet(Preloader parseInt (b \ "amount").text)),
    for (b <- n \ "cost_reduction") yield BonusAttributeCost(
      attr = (b \ "attribute").text,
      cost = Preloader parseInt (b \ "percentage").text),
    for (b <- n \ "reaction_bonus") yield BonusReaction(
      affected = (b \ "affected").text,
      reputation = (b \ "affected" \ "@reputation").text == "yes",
      perLvl = (b \ "amount" \ "@per_level").text == "yes",
      freq = Preloader parseInt (b \ "frequency").text,
      bonus = Preloader parseInt (b \ "amount").text,
      notes = (b \ "notes").text))
  val seq: Seq[Document] =
    for (adv <- (XML load (getClass getResourceAsStream "/adv.xml")) \ "advantage") yield Document(Json toJson Trait(
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
      cpBase = Preloader parseInt (adv \ "base_points").text,
      level = Preloader parseInt (adv \ "levels").text,
      cpPerLvl = Preloader parseInt (adv \ "points_per_level").text,
      modifiers = {
        val (atrBns, sklBns, dmgBns, drBns, atrCMd, rctBns) = Preloader parseMod adv
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
          val (atrBns, sklBns, dmgBns, drBns, atrCMd, rctBns) = Preloader parseMod mod
          TraitModifier(
            on = false,
            name = (mod \ "name").text,
            ref = (mod \ "reference").text,
            notes = (mod \ "notes").text,
            level = Preloader parseInt (mod \ "levels").text,
            attrBonuses = atrBns,
            skillBonuses = sklBns,
            dmgBonuses = dmgBns,
            drBonuses = drBns,
            attrCostMods = atrCMd,
            affects = (mod \ "affects").text,
            costType = (mod \ "cost" \ "@type").text,
            cost = Preloader parseDouble (mod \ "cost").text,
            reactBonuses = rctBns)
        }) (breakOut)) toString())

  println("Opening connection...")
  val client = MongoClient()
  println("Loading basic traits...")
  Await ready(client getDatabase "gurps" getCollection "traits" insertMany seq toFuture(), 30.seconds) onSuccess {
    case Seq(_) => println("Basic traits loaded to mongo db \"gurps\" collection \"traits\".")
  }
  client close()
}