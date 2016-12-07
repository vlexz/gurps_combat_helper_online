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
  private val parseMod = (n: Node) => (
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
    for (b <- n \ "weapon_bonus") yield BonusDamage(// TODO: weapon master misses damage bonuses
      skill = (b \ "name").text,
      skillCompare = (b \ "name" \ "@compare").text,
      spc = (b \ "specialization").text,
      spcCompare = (b \ "specialization" \ "@compare").text,
      relSkill = Preloader parseInt (b \ "level").text,
      bonus = Preloader parseInt (b \ "amount").text),
    for (b <- n \ "dr_bonus") yield BonusDR(
      locations = Seq((b \ "location").text),
      perLvl = (b \ "amount" \ "@per_level").text == "yes",
      front = true,
      back = true,
      protection = DrSet(Preloader parseInt (b \ "amount").text)),
    for (b <- n \ "cost_reduction") yield BonusAttributeCost(
      attr = (b \ "attribute").text,
      cost = Preloader parseInt (b \ "percentage").text),
    for (b <- n \ "reaction_bonus") yield BonusReaction(// TODO: no reaction_bonus in library
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
      switch = (adv \ "name" \ "@switchability").text, // TODO: switchability is absent in gcs lib
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
          on = true,
          name = "Default",
          attrBonuses = atrBns,
          skillBonuses = sklBns,
          dmgBonuses = dmgBns,
          drBonuses = drBns,
          attrCostMods = atrCMd,
          reactBonuses = rctBns))
      } ++ (for (mod <- adv \ "modifier") yield {
        val (atrBns, sklBns, dmgBns, drBns, atrCMd, rctBns) = Preloader parseMod mod
        TraitModifier(
          on = false,
          name = (mod \ "name").text,
          ref = (mod \ "reference").text,
          notes = (mod \ "notes").text,
          affects = (mod \ "affects").text,
          costType = (mod \ "cost" \ "@type").text,
          cost = Preloader parseDouble (mod \ "cost").text,
          level = Preloader parseInt (mod \ "levels").text,
          attrBonuses = atrBns,
          skillBonuses = sklBns,
          dmgBonuses = dmgBns,
          drBonuses = drBns,
          attrCostMods = atrCMd,
          reactBonuses = rctBns)
      }) (breakOut)) toString())

  val client = MongoClient()
  Await ready(client getDatabase "gurps" getCollection "traits" insertMany seq toFuture(), 30.seconds) onSuccess {
    case Seq(_) => println("Basic traits loaded to mongo db \"gurps\" collection \"traits\".")
  }
  client close()
}