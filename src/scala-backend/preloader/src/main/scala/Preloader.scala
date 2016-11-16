import models.charlist._
import models.charlist.Charlist.traitFormat
import org.mongodb.scala._
import play.api.libs.json.Json

import scala.collection.breakOut
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.xml.XML

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
  val seq =
    for (adv <- (XML load (getClass getResourceAsStream "/adv.xml")) \ "advantage") yield Document(Json toJson Trait(
      name = (adv \ "name").text,
      types = (adv \ "type").text split ", " flatMap (_ split "/"),
      category = (adv \ "categories" \ "category").map(_.text) match {
        case x if x contains "Advantage" => "Advantage"
        case x if x contains "Disadvantage" => "Disadvantage"
        case x => x.headOption getOrElse ""
      },
      switch = "", // TODO: switchability is absent in gcs lib
      ref = (adv \ "reference").text,
      notes = (adv \ "notes").text,
      active = false,
      cpBase = parseInt((adv \ "base_points").text),
      level = parseInt((adv \ "levels").text),
      cpPerLvl = parseInt((adv \ "points_per_level").text),
      modifiers = {
        val atrBns = for (b <- adv \ "attribute_bonus") yield BonusAttribute(
          attr = (b \ "attribute").text,
          bonus = parseDouble((b \ "amount").text),
          perLvl = (b \ "amount" \ "@per_level").text == "yes")
        val sklBns = for (b <- adv \ "skill_bonus") yield BonusSkill(
          skill = (b \ "name").text,
          skillCompare = (b \ "name" \ "@compare").text,
          spc = (b \ "specialization").text,
          spcCompare = (b \ "specialization" \ "@compare").text,
          perLvl = (b \ "amount" \ "@per_level").text == "yes",
          bonus = parseInt((b \ "amount").text))
        val dmgBns = for (b <- adv \ "weapon_bonus") yield BonusDamage(// TODO: weapon master misses damage bonuses
          skill = (b \ "name").text,
          skillCompare = (b \ "name" \ "@compare").text,
          spc = (b \ "specialization").text,
          spcCompare = (b \ "specialization" \ "@compare").text,
          relSkill = parseInt((b \ "level").text),
          bonus = parseInt((b \ "amount").text))
        val drBns = for (b <- adv \ "dr_bonus") yield BonusDR(
          locations = Seq((b \ "location").text),
          perLvl = (b \ "amount" \ "@per_level").text == "yes",
          front = true,
          back = true,
          protection = DrSet(parseInt((b \ "amount").text)))
        val atrCMd = for (b <- adv \ "cost_reduction") yield BonusAttributeCost(
          attr = (b \ "attribute").text,
          cost = parseInt((b \ "percentage").text))
        val rctBns = for (b <- adv \ "reaction_bonus") yield BonusReaction(// TODO: no reaction_bonus in library
          affected = (b \ "affected").text,
          reputation = (b \ "affected" \ "@reputation").text == "yes",
          perLvl = (b \ "amount" \ "@per_level").text == "yes",
          freq = parseInt((b \ "frequency").text),
          bonus = parseInt((b \ "amount").text),
          notes = (b \ "notes").text)
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
      } ++ (for (mod <- adv \ "modifier") yield TraitModifier(
        on = false,
        name = (mod \ "name").text,
        ref = (mod \ "reference").text,
        notes = (mod \ "notes").text,
        affects = (mod \ "affects").text,
        costType = (mod \ "cost" \ "@type").text,
        cost = parseDouble((mod \ "cost").text),
        level = parseInt((mod \ "levels").text),
        attrBonuses = for (b <- mod \ "attribute_bonus") yield BonusAttribute(
          attr = (b \ "attribute").text,
          bonus = parseInt((b \ "amount").text),
          perLvl = (b \ "amount" \ "@per_level").text == "yes"),
        skillBonuses = for (b <- mod \ "skill_bonus") yield BonusSkill(
          skill = (b \ "name").text,
          skillCompare = (b \ "name" \ "@compare").text,
          spc = (b \ "specialization").text,
          spcCompare = (b \ "specialization" \ "@compare").text,
          perLvl = (b \ "amount" \ "@per_level").text == "yes",
          bonus = parseInt((b \ "amount").text)),
        dmgBonuses = for (b <- mod \ "weapon_bonus") yield BonusDamage(
          skill = (b \ "name").text,
          skillCompare = (b \ "name" \ "@compare").text,
          spc = (b \ "specialization").text,
          spcCompare = (b \ "specialization" \ "@compare").text,
          relSkill = parseInt((b \ "level").text),
          bonus = parseInt((b \ "amount").text)),
        drBonuses = for (b <- mod \ "dr_bonus") yield BonusDR(
          locations = Seq((b \ "location").text),
          perLvl = (b \ "amount" \ "@per_level").text == "yes",
          front = true,
          back = true,
          protection = DrSet(parseInt((b \ "amount").text))),
        attrCostMods = for (b <- mod \ "cost_reduction") yield BonusAttributeCost(
          attr = (b \ "attribute").text,
          cost = parseInt((b \ "percentage").text)),
        reactBonuses = for (b <- mod \ "reaction_bonus") yield BonusReaction(
          affected = (b \ "affected").text,
          reputation = (b \ "affected" \ "@reputation").text == "yes",
          perLvl = (b \ "amount" \ "@per_level").text == "yes",
          freq = parseInt((b \ "frequency").text),
          bonus = parseInt((b \ "amount").text),
          notes = (b \ "notes").text))) (breakOut)) toString())

  val client: MongoClient = MongoClient()
  Await.ready(client getDatabase "gurps" getCollection "traits" insertMany seq toFuture(), 30.seconds)
  client close()
}