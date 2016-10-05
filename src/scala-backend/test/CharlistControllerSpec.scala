import controllers.CharlistController
import models.charlist._
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.mongodb.scala.Completed
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Request, Result, Results}
import play.api.test.Helpers._
import play.api.test.{FakeHeaders, FakeRequest}
import services.CharlistService

import scala.concurrent.Future


/**
  * Created by crimson on 9/26/16.
  */
class CharlistControllerSpec extends PlaySpec with Results with MockitoSugar {

  "CharlistController#post" should {

    "send OK on request with valid charlist json" in {
      val mockCharlistService = mock[CharlistService]
      val charlist =
        Charlist(
          "",
          0,
          "vlex",
          "Bjorn Masterson",
          262, 0,
          Description("35", "6f6i", "250", "somelink", "longbio"),
          Stats(StatInt(), StatInt(3), StatInt(), StatInt(2), StatInt(2), StatInt(2), StatInt(), StatInt(), "", "", 0,
            0, 0, StatPoints(), StatPoints(), StatFrac(-0.25f), StatInt(), StatInt(), 0, 0, 0, 0),
          Seq[Feature](Feature("Combat Reflexes", 5), Feature("Sorcery 2", 30), Feature("Hot Pilot 3", 15)),
          Seq[Skill](Skill()),
          Seq[Technique](Technique("Off-hand weapon training", "Rifle", "Average", "Trench Warfare", 2)),
          Equipment(
            Seq[Weapon](Weapon("Brawling", ItemStates.READY, Seq[MeleeAttack](
              MeleeAttack("Punch", true, MeleeDamage(AttackType.THRUSTING, 0, 0, 1, DamageType.CRUSHING, ""),
                Seq[MeleeDamage](MeleeDamage(AttackType.WEAPON, 4, 0, 1, DamageType.BURNING)),
                Seq[MeleeDamage](MeleeDamage(AttackType.WEAPON, 2, 0, 1, DamageType.TOXIC)),
                "Brawling", 0, "", "", 1, "", "C", "")
            ), Seq[RangedAttack](), Seq[String](), false, -1, false, 0, 0, 4, 4, 4, 0, "", 0, 0, 0, 0),
              Weapon("Revolver", ItemStates.READY, Seq[MeleeAttack](), Seq[RangedAttack](
                RangedAttack("LE", true, RangedDamage(1, 1, 2, 0.5f, DamageType.PIERCING_LARGE, 0, ""),
                  Seq[RangedDamage](RangedDamage(1, 1, 1, 1, DamageType.CRUSHING_EXPLOSION, 1, "")),
                  Seq[RangedDamage](RangedDamage(2, 1, 0, 1, DamageType.TOXIC, 0, "")), "Guns(Pistol)", 2, 0, "50/200",
                  RangedRoF(1, 1), 2, RangedShots(6, "(3i)", 6, 30, 0.1f, 2, "", 0, 0), 9, "", 17, "")),
                Seq[String](), true, -2, false, 0, 7, 10, 10, 4, 5, "", 4.1f, 350, 0, 0
              )), Seq[Armor](Armor("Boots", ItemStates.EQUIPPED, 0, 1, 1, 1, true, true, false, Seq[String]()))
          ), Seq(CharlistArmor("Boots", 1, 1, 1, false, Seq(16), "", 2, 20)), Seq(CharlistItem("Revolver rounds", "Combat", "", 0.02f, 1.5, 18), CharlistItem("Belt Revolver Holster", "Combat", "", 0.5f, 25, 1), CharlistItem("Small Backpack", "Travel", "", 3, 60, 1)))
      when(mockCharlistService save anyObject[Charlist]) thenReturn Future(Completed())
      val charlistController = new CharlistController(mockCharlistService)
      val fakeRequest: Request[JsValue] = FakeRequest[JsValue](POST, controllers.routes.CharlistController.add().url, FakeHeaders(), Json.parse("""  {"_id":"","timestamp":0,"player":"vlex","cp":262,"name":"Bjorn Masterson","description":{"age":35,"height":"6f6i","weight":"250","portrait":"somelink","bio":"longbio"},"coreStats":{"st":10,"dx":13,"iq":10,"ht":12,"will":12,"per":12,"db":0,"reaction":0,"hpMod":0,"fpMod":0,"liftMod":0,"speedMod":-0.25,"moveMod":0,"dodgeMod":1,"parryMod":0,"blockMod":0,"hpLoss":0,"fpLoss":0},"features":[{"feature":"Combat Reflexes","cp":5},{"feature":"Sorcery 2","cp":30},{"feature":"Hot Pilot 3","cp":15},{"feature":"Outdoorsman 3","cp":30},{"feature":"Accelerated Casting","cp":10},{"feature":"Military Rank 2(informal)","cp":5},{"feature":"Luck (combat -20%)","cp":12},{"feature":"Spell: Poison Bullets 2 (+2d tox)","cp":4},{"feature":"Spell: Feather Fall","cp":6},{"feature":"Spell: Smoke Grenade 10 (50m / 2y radius / 10sec","cp":5},{"feature":"Spell: Camouflage 3 (+6 to Stealth)","cp":5},{"feature":"Spell: Ballistic Shield 4 (4DR FF vs ranged)","cp":6},{"feature":"Spell: Entangle 15 (binding 15ST)","cp":4},{"feature":"Sense of Duty (crew)","cp":-5},{"feature":"Greed","cp":-15},{"feature":"Code of Honor (Soldier)","cp":-10},{"feature":"Overconfidence","cp":-5},{"feature":"Flashbacks (Mild)","cp":-5},{"feature":"Wounded","cp":-5}],"skills":[{"name":"Rifle","attr":"DX","diff":"Easy","cp":20,"mod":0},{"name":"Piloting (Lighter-than-air)","attr":"DX","diff":"Average","cp":4,"mod":3},{"name":"Acrobatics","attr":"DX","diff":"Hard","cp":4,"mod":0},{"name":"Streetwise","attr":"IQ","diff":"Average","cp":4,"mod":0}],"techniques":[{"name":"Off-hand weapon training","skill":"Rifle","diff":"Average","style":"Trench Warfare","cp":2}],"melee":[{"name":"Brawling","attacks":[{"name":"Punch","damage":{"apply":true,"attackType":"Thrusting","dmgDice":0,"dmgMod":0,"armorDiv":1,"dmgType":"cr"},"followup":{"apply":false,"attackType":"","dmgDice":0,"dmgMod":0,"armorDiv":1,"dmgType":""},"linked":{"apply":false,"attackType":"","dmgDice":0,"dmgMod":0,"armorDiv":1,"dmgType":""},"skill":"Brawling","parry":0,"block":true,"unbalanced":false,"hands":"one","reach":"C","notes":""}],"notes":"","wt":0,"cost":0}],"ranged":[{"name":"Revolver","attacks":[{"name":"Solid Bullet","damage":{"apply":true,"dmgDice":2,"diceMult":1,"dmgMod":0,"armorDiv":1,"dmgType":"pi+"},"followup":{"apply":false,"dmgDice":0,"diceMult":0,"dmgMod":0,"armorDiv":1,"dmgType":""},"fragDice":0,"linked":{"apply":false,"dmgDice":0,"diceMult":0,"dmgMod":0,"armorDiv":1,"dmgType":""},"skill":"Guns(Pistol)","acc":2,"rng":"200/500","rof":1,"rcl":3,"shots":6,"reload":"(10i)","shotsLeft":6,"st":10,"malf":17,"notes":"","shotWt":0.019999999552965164,"shotCost":3},{"name":"Poisoned Solid Bullet","damage":{"apply":true,"dmgDice":2,"diceMult":1,"dmgMod":0,"armorDiv":1,"dmgType":"pi+"},"followup":{"apply":false,"dmgDice":0,"diceMult":0,"dmgMod":0,"armorDiv":1,"dmgType":""},"fragDice":0,"linked":{"apply":true,"dmgDice":2,"diceMult":1,"dmgMod":0,"armorDiv":1,"dmgType":"tox"},"skill":"Guns(Pistol)","acc":2,"rng":"200/500","rof":1,"rcl":3,"shots":6,"reload":"(10i)","shotsLeft":0,"st":10,"malf":17,"notes":"With spell, uses regular round","shotWt":0.019999999552965164,"shotCost":3},{"name":"HP","damage":{"apply":true,"dmgDice":2,"diceMult":1,"dmgMod":0,"armorDiv":0.5,"dmgType":"pi++"},"followup":{"apply":false,"dmgDice":0,"diceMult":0,"dmgMod":0,"armorDiv":1,"dmgType":""},"fragDice":0,"linked":{"apply":false,"dmgDice":0,"diceMult":0,"dmgMod":0,"armorDiv":1,"dmgType":""},"skill":"Guns(Pistol)","acc":2,"rng":"200/500","rof":1,"rcl":3,"shots":6,"reload":"(10i)","shotsLeft":6,"st":10,"malf":17,"notes":"","shotWt":0.019999999552965164,"shotCost":3}],"bulk":-3,"notes":"","wt":2.9000000953674316,"cost":200}],"armor":[{"name":"Boots","dr":1,"ep":1,"epi":1,"soft":false,"locations":[16],"notes":"","wt":2,"cost":20}],"items":[{"name":"Revolver rounds","carried":"Combat","notes":"","wt":0.019999999552965164,"cost":1.5,"n":18},{"name":"Belt Revolver Holster","carried":"Combat","notes":"","wt":0.5,"cost":25,"n":1},{"name":"Small Backpack","carried":"Travel","notes":"","wt":3,"cost":60,"n":1}]} """))
      val result: Future[Result] = charlistController.add()(fakeRequest)
      //      assertResult("ok")(contentAsString(result))
      assertResult(OK)(status(result))
      assertResult(Json.obj("success" -> "The operation completed successfully"))(contentAsJson(result))
    }
  }

  it should {

    "send BAD_REQUEST on request with invalid charlist json" in {
      val mockCharlistService = mock[CharlistService]
      when(mockCharlistService save anyObject[Charlist]) thenReturn Future(Completed())
      val charlistController = new CharlistController(mockCharlistService)
      val fakeRequest: Request[JsValue] = FakeRequest[JsValue](POST, controllers.routes.CharlistController.add().url, FakeHeaders(), Json.parse("""  {"_id":"","timestamp":0,"cp":262,"name":"Bjorn Masterson","description":{"age":35,"height":"6f6i","weight":"250","portrait":"somelink","bio":"longbio"},"coreStats":{"st":10,"dx":13,"iq":10,"ht":12,"will":12,"per":12,"db":0,"reaction":0,"hpMod":0,"fpMod":0,"liftMod":0,"speedMod":-0.25,"moveMod":0,"dodgeMod":1,"parryMod":0,"blockMod":0,"hpLoss":0,"fpLoss":0},"features":[{"feature":"Combat Reflexes","cp":5},{"feature":"Sorcery 2","cp":30},{"feature":"Hot Pilot 3","cp":15},{"feature":"Outdoorsman 3","cp":30},{"feature":"Accelerated Casting","cp":10},{"feature":"Military Rank 2(informal)","cp":5},{"feature":"Luck (combat -20%)","cp":12},{"feature":"Spell: Poison Bullets 2 (+2d tox)","cp":4},{"feature":"Spell: Feather Fall","cp":6},{"feature":"Spell: Smoke Grenade 10 (50m / 2y radius / 10sec","cp":5},{"feature":"Spell: Camouflage 3 (+6 to Stealth)","cp":5},{"feature":"Spell: Ballistic Shield 4 (4DR FF vs ranged)","cp":6},{"feature":"Spell: Entangle 15 (binding 15ST)","cp":4},{"feature":"Sense of Duty (crew)","cp":-5},{"feature":"Greed","cp":-15},{"feature":"Code of Honor (Soldier)","cp":-10},{"feature":"Overconfidence","cp":-5},{"feature":"Flashbacks (Mild)","cp":-5},{"feature":"Wounded","cp":-5}],"skills":[{"name":"Rifle","attr":"DX","diff":"Easy","cp":20,"mod":0},{"name":"Piloting (Lighter-than-air)","attr":"DX","diff":"Average","cp":4,"mod":3},{"name":"Acrobatics","attr":"DX","diff":"Hard","cp":4,"mod":0},{"name":"Streetwise","attr":"IQ","diff":"Average","cp":4,"mod":0}],"techniques":[{"name":"Off-hand weapon training","skill":"Rifle","diff":"Average","style":"Trench Warfare","cp":2}],"melee":[{"name":"Brawling","attacks":[{"name":"Punch","damage":{"apply":true,"attackType":"Thrusting","dmgDice":0,"dmgMod":0,"armorDiv":1,"dmgType":"cr"},"followup":{"apply":false,"attackType":"","dmgDice":0,"dmgMod":0,"armorDiv":1,"dmgType":""},"linked":{"apply":false,"attackType":"","dmgDice":0,"dmgMod":0,"armorDiv":1,"dmgType":""},"skill":"Brawling","parry":0,"block":true,"unbalanced":false,"hands":"one","reach":"C","notes":""}],"notes":"","wt":0,"cost":0}],"ranged":[{"name":"Revolver","attacks":[{"name":"Solid Bullet","damage":{"apply":true,"dmgDice":2,"diceMult":1,"dmgMod":0,"armorDiv":1,"dmgType":"pi+"},"followup":{"apply":false,"dmgDice":0,"diceMult":0,"dmgMod":0,"armorDiv":1,"dmgType":""},"fragDice":0,"linked":{"apply":false,"dmgDice":0,"diceMult":0,"dmgMod":0,"armorDiv":1,"dmgType":""},"skill":"Guns(Pistol)","acc":2,"rng":"200/500","rof":1,"rcl":3,"shots":6,"reload":"(10i)","shotsLeft":6,"st":10,"malf":17,"notes":"","shotWt":0.019999999552965164,"shotCost":3},{"name":"Poisoned Solid Bullet","damage":{"apply":true,"dmgDice":2,"diceMult":1,"dmgMod":0,"armorDiv":1,"dmgType":"pi+"},"followup":{"apply":false,"dmgDice":0,"diceMult":0,"dmgMod":0,"armorDiv":1,"dmgType":""},"fragDice":0,"linked":{"apply":true,"dmgDice":2,"diceMult":1,"dmgMod":0,"armorDiv":1,"dmgType":"tox"},"skill":"Guns(Pistol)","acc":2,"rng":"200/500","rof":1,"rcl":3,"shots":6,"reload":"(10i)","shotsLeft":0,"st":10,"malf":17,"notes":"With spell, uses regular round","shotWt":0.019999999552965164,"shotCost":3},{"name":"HP","damage":{"apply":true,"dmgDice":2,"diceMult":1,"dmgMod":0,"armorDiv":0.5,"dmgType":"pi++"},"followup":{"apply":false,"dmgDice":0,"diceMult":0,"dmgMod":0,"armorDiv":1,"dmgType":""},"fragDice":0,"linked":{"apply":false,"dmgDice":0,"diceMult":0,"dmgMod":0,"armorDiv":1,"dmgType":""},"skill":"Guns(Pistol)","acc":2,"rng":"200/500","rof":1,"rcl":3,"shots":6,"reload":"(10i)","shotsLeft":6,"st":10,"malf":17,"notes":"","shotWt":0.019999999552965164,"shotCost":3}],"bulk":-3,"notes":"","wt":2.9000000953674316,"cost":200}],"armor":[{"name":"Boots","dr":1,"ep":1,"epi":1,"soft":false,"locations":[16],"notes":"","wt":2,"cost":20}],"items":[{"name":"Revolver rounds","carried":"Combat","notes":"","wt":0.019999999552965164,"cost":1.5,"n":18},{"name":"Belt Revolver Holster","carried":"Combat","notes":"","wt":0.5,"cost":25,"n":1},{"name":"Small Backpack","carried":"Travel","notes":"","wt":3,"cost":60,"n":1}]} """))
      val result: Future[Result] = charlistController.add()(fakeRequest)
      assertResult(BAD_REQUEST)(status(result))
      //      assertResult(Json.obj("message" -> "", "cause" -> ""))(contentAsJson(result))
    }
  }
}
