import models.charlist.{Armor, ArmorComponent, Charlist, DrSet}
import play.api.libs.json.Writes

import scala.xml.XML

/**
  * Created by crimson on 12/13/16.
  */
class ArmorParser(filePath: String) extends Parser[Armor] {
  println("Parsing armor elements...")
  override val seq: Seq[Armor] =
    for (arm <- (XML load (getClass getResourceAsStream filePath)) \ "equipment" if (arm \ "dr_bonus").nonEmpty)
      yield Armor(
        name = (arm \ "description").text + "/TL" + (arm \ "tech_level").text,
        components = for (c <- arm \ "dr_bonus") yield ArmorComponent(
          protection = DrSet(
            parseInt((c \ "amount").text),
            parseInt((c \ "ep").text),
            parseInt((c \ "epi").text)),
          locations = c \ "location" map (_.text)),
        lc = parseInt((arm \ "legality_class").text),
        tl = parseInt((arm \ "tech_level").text),
        wt = parseDouble((arm \ "weight").text.replace(" lb", "")),
        cost = parseDouble((arm \ "value").text))
  // TODO: fields missing (rigdity, ep, epi, front/back, category)
  override val tjs: Writes[Armor] = Charlist.armorElementFormat
}
