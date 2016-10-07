import controllers.CharlistController
import models.charlist._
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.mongodb.scala.Completed
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test.{FakeHeaders, FakeRequest}
import services.CharlistService

import scala.concurrent.Future


/**
  * Created by crimson on 9/26/16.
  */
class CharlistControllerSpec extends PlaySpec with Results with MockitoSugar {

  "CharlistController#POST/api/char/" should {

    "send OK with charlist json on request with valid charlist json" in {
      val mockCharlistService = mock[CharlistService]
      /*val charlist = Charlist(
        player = "vlex",
        name = "Bjorn Masterson",
        cp = 262,
        description = Description("35", "6f6i", "250", "somelink", "longbio"),
        stats = Stats(dx = StatInt(3), ht = StatInt(2), will = StatInt(2), per = StatInt(2),
          basicSpeed = StatFrac(-0.25)),
        traits = Seq[Trait](Trait("Combat Reflexes", 5), Trait("Sorcery 2", 30), Trait("Hot Pilot 3", 15)),
        skills = Seq[Skill](Skill(name = "Guns(Pistol", attr = SkillBaseAttributes.DX,
          diff = SkillDifficulties.AVERAGE, cp = 4)),
        techniques = Seq[Technique](Technique(name = "Off-hand weapon training", skill = "Guns(Pistol)",
          diff = TechniqueDifficulties.HARD, style = "Trench Warfare", defLvl = -4, cp = 5)),
        equip = Equipment(
          weapons = Seq[Weapon](
            Weapon(name = "Brawling", carried = ItemCarryingStates.READY,
              attacksMelee = Seq[MeleeAttack](
                MeleeAttack(name = "Punch", available = true, damage = MeleeDamage(attackType = AttackType.THRUSTING),
                  followup = Seq[MeleeDamage](MeleeDamage(dmgDice = 4, dmgType = DamageType.BURNING)),
                  linked = Seq[MeleeDamage](MeleeDamage(dmgDice = 2, dmgType = DamageType.TOXIC)), skill = "Brawling",
                  st = 1, reach = "C")), bulk = -1, hp = 4, hpLeft = 4),
            Weapon(name = "Revolver", carried = ItemCarryingStates.READY,
              attacksRanged = Seq[RangedAttack](
                RangedAttack(name = "LE", available = true, damage = RangedDamage(dmgDice = 1, dmgMod = 2,
                  armorDiv = 0.5, dmgType = DamageType.PIERCING_LARGE), followup = Seq[RangedDamage](
                  RangedDamage(dmgDice = 1, dmgMod = 1, dmgType = DamageType.CRUSHING_EXPLOSION, fragDice = 1)),
                  linked = Seq[RangedDamage](RangedDamage(dmgDice = 2, dmgType = DamageType.TOXIC)),
                  skill = "Guns(Pistol)", acc = 2, rng = "50/200", shots = RangedShots(shots = 6, reload = "(3i)",
                    shotsLoaded = 6, shotsCarried = 30, shotWt = 0.1, shotCost = 2), st = 9, malf = 17)),
              offHand = true, bulk = -2, dr = 7, hp = 10, hpLeft = 10, lc = 4, tl = 5, wt = 4.1, cost = 350)
          ),
          armor = Seq[Armor](
            Armor(name = "Boots", carried = ItemCarryingStates.EQUIPPED, dr = 1, ep = 1, epi = 1,
            locations = Seq[String](HitLocations.FEET), hp = 5, hpLeft = 5, tl = 5, wt = 2, cost = 50)
          ),
          items = Seq[Item](Item(name = "Belt Revolver Holster", carried = ItemCarryingStates.EQUIPPED, dr = 1, hp = 5,
            hpLeft = 5, tl = 5, wt = 0.5, cost = 75), Item(name = "Small Backpack", carried = ItemCarryingStates.TRAVEL,
            hp = 5, hpLeft = 5, tl = 5, wt = 3, cost = 60)
          )
        )
      )*/
      when(mockCharlistService save anyObject[Charlist]) thenReturn Future(Completed())
      val charlistController = new CharlistController(mockCharlistService)
      val fakeRequest: Request[JsValue] =
        FakeRequest[JsValue](
          POST,
          controllers.routes.CharlistController.add().url,
          FakeHeaders(),
          //                    Json.toJson(charlist)
          Json.parse("""  {"_id":"","timestamp":0,"player":"vlex","access":[],"name":"Bjorn Masterson","cp":262,"cpTotal":154,"description":{"age":"35","height":"6f6i","weight":"250","portrait":"somelink","bio":"longbio"},"stats":{"st":{"delta":0,"bonus":0,"notes":"","value":10,"cp":0},"dx":{"delta":3,"bonus":0,"notes":"","value":13,"cp":60},"iq":{"delta":0,"bonus":0,"notes":"","value":10,"cp":0},"ht":{"delta":2,"bonus":0,"notes":"","value":12,"cp":20},"will":{"delta":2,"bonus":0,"notes":"","value":12,"cp":10},"per":{"delta":2,"bonus":0,"notes":"","value":12,"cp":10},"liftSt":{"delta":0,"bonus":0,"notes":"","value":10,"cp":0},"strikeSt":{"delta":0,"bonus":0,"notes":"","value":10,"cp":0},"thrDmg":"1d-2","swDmg":"1d","bl":20,"combatEncumbrance":0,"travelEncumbrance":0,"hp":{"delta":0,"bonus":0,"notes":"","value":10,"cp":0,"lost":0,"compromised":false,"collapsing":false},"fp":{"delta":0,"bonus":0,"notes":"","value":12,"cp":0,"lost":0,"compromised":false,"collapsing":false},"basicSpeed":{"delta":-0.25,"bonus":0,"notes":"","value":6,"cp":-5},"basicMove":{"delta":0,"bonus":0,"notes":"","value":6,"cp":0},"basicDodge":{"delta":0,"bonus":0,"notes":"","value":9,"cp":0},"combMove":5,"travMove":5,"dodge":9,"sm":0},"traits":[{"name":"Combat Reflexes","cp":5},{"name":"Sorcery 2","cp":30},{"name":"Hot Pilot 3","cp":15}],"skills":[{"name":"Guns(Pistol","attr":"DX","diff":"Average","defaults":[],"prerequisites":[],"bonus":0,"notes":"","cp":4,"relLvl":1,"lvl":14}],"techniques":[{"name":"Off-hand weapon training","skill":"Guns(Pistol)","diff":"Hard","style":"Trench Warfare","defLvl":-4,"maxLvl":0,"notes":"","cp":5,"relLvl":0,"lvl":0}],"equip":{"weapons":[{"name":"Brawling","carried":"Ready","attacksMelee":[{"name":"Punch","available":true,"damage":{"attackType":"thr","dmgDice":0,"dmgMod":0,"armorDiv":1,"dmgType":"cr","dmgString":"1d-2 cr"},"followup":[{"attackType":"","dmgDice":4,"dmgMod":0,"armorDiv":1,"dmgType":"burn","dmgString":"4d burn"}],"linked":[{"attackType":"","dmgDice":2,"dmgMod":0,"armorDiv":1,"dmgType":"tox","dmgString":"2d tox"}],"skill":"Brawling","parry":0,"parryType":"","parryString":"0","st":1,"hands":"","reach":"C","notes":""}],"attacksRanged":[],"grips":[],"offHand":false,"bulk":-1,"block":false,"db":0,"dr":0,"hp":4,"hpLeft":4,"broken":false,"lc":5,"tl":0,"notes":"","wt":0,"cost":0,"totalWt":0,"totalCost":0},{"name":"Revolver","carried":"Ready","attacksMelee":[],"attacksRanged":[{"name":"LE","available":true,"damage":{"dmgDice":1,"diceMult":1,"dmgMod":2,"armorDiv":0.5,"dmgType":"pi+","fragDice":0,"dmgString":"1d+2(0.5) pi+"},"followup":[{"dmgDice":1,"diceMult":1,"dmgMod":1,"armorDiv":1,"dmgType":"cr ex","fragDice":1,"dmgString":"1d+1 cr ex [1d]"}],"linked":[{"dmgDice":2,"diceMult":1,"dmgMod":0,"armorDiv":1,"dmgType":"tox","fragDice":0,"dmgString":"2d tox"}],"skill":"Guns(Pistol)","acc":2,"accMod":0,"rng":"50/200","rof":{"rof":1,"rofMult":1,"rofFA":false,"rofJet":false,"rofString":"1"},"rcl":2,"shots":{"shots":6,"reload":"(3i)","shotsLoaded":6,"shotsCarried":30,"shotWt":0.1,"shotCost":2,"shotsString":"6/6(3i) 30","totalWt":3.6,"totalCost":72},"st":9,"hands":"","malf":17,"notes":""}],"grips":[],"offHand":true,"bulk":-2,"block":false,"db":0,"dr":7,"hp":10,"hpLeft":10,"broken":false,"lc":4,"tl":5,"notes":"","wt":4.1,"cost":350,"totalWt":7.699999999999999,"totalCost":422}],"armor":[{"name":"Boots","carried":"Equipped","db":0,"dr":1,"ep":1,"epi":1,"front":true,"back":true,"drType":"hard","locations":["feet"],"hp":5,"hpLeft":5,"broken":false,"lc":5,"tl":5,"notes":"","wt":2,"cost":50}],"items":[{"name":"Belt Revolver Holster","carried":"Equipped","dr":1,"hp":5,"hpLeft":5,"broken":false,"lc":5,"tl":5,"notes":"","wt":0.5,"cost":75,"n":1,"totalWt":0.5,"totalCost":75},{"name":"Small Backpack","carried":"Travel","dr":0,"hp":5,"hpLeft":5,"broken":false,"lc":5,"tl":5,"notes":"","wt":3,"cost":60,"n":1,"totalWt":3,"totalCost":60}],"frontDR":{"skull":{"dr":0,"ep":0,"epi":0},"eyes":{"dr":0,"ep":0,"epi":0},"face":{"dr":0,"ep":0,"epi":0},"neck":{"dr":0,"ep":0,"epi":0},"armLeft":{"dr":0,"ep":0,"epi":0},"armRight":{"dr":0,"ep":0,"epi":0},"handLeft":{"dr":0,"ep":0,"epi":0},"handRight":{"dr":0,"ep":0,"epi":0},"chest":{"dr":0,"ep":0,"epi":0},"vitals":{"dr":0,"ep":0,"epi":0},"abdomen":{"dr":0,"ep":0,"epi":0},"groin":{"dr":0,"ep":0,"epi":0},"legLeft":{"dr":0,"ep":0,"epi":0},"legRight":{"dr":0,"ep":0,"epi":0},"footLeft":{"dr":1,"ep":1,"epi":1},"footRight":{"dr":0,"ep":0,"epi":0}},"rearDR":{"skull":{"dr":0,"ep":0,"epi":0},"eyes":{"dr":0,"ep":0,"epi":0},"face":{"dr":0,"ep":0,"epi":0},"neck":{"dr":0,"ep":0,"epi":0},"armLeft":{"dr":0,"ep":0,"epi":0},"armRight":{"dr":0,"ep":0,"epi":0},"handLeft":{"dr":0,"ep":0,"epi":0},"handRight":{"dr":0,"ep":0,"epi":0},"chest":{"dr":0,"ep":0,"epi":0},"vitals":{"dr":0,"ep":0,"epi":0},"abdomen":{"dr":0,"ep":0,"epi":0},"groin":{"dr":0,"ep":0,"epi":0},"legLeft":{"dr":0,"ep":0,"epi":0},"legRight":{"dr":0,"ep":0,"epi":0},"footLeft":{"dr":1,"ep":1,"epi":1},"footRight":{"dr":0,"ep":0,"epi":0}},"totalDb":0,"totalCost":607,"totalCombWt":10.2,"totalTravWt":13.2},"conditions":{"unconscious":false,"mortallyWounded":false,"dead":false,"shock":0,"stunned":false,"afflictions":{"coughing":false,"drowsy":false,"drunk":false,"euphoria":false,"nauseated":false,"pain":false,"tipsy":false,"agony":false,"choking":false,"daze":false,"ecstasy":false,"hallucinating":false,"paralysis":false,"retching":false,"seizure":false,"coma":false,"heartAttack":false},"cripplingInjuries":[],"posture":"Standing","closeCombat":false,"grappled":false,"pinned":false,"sprinting":false,"mounted":false}} """)
        )
      val result: Future[Result] = charlistController.add()(fakeRequest)
      assertResult(OK)(status(result))
    }
  }

  it should {

    "send BAD_REQUEST on request with invalid charlist json" in {
      val mockCharlistService = mock[CharlistService]
      when(mockCharlistService save anyObject[Charlist]) thenReturn Future(Completed())
      val charlistController = new CharlistController(mockCharlistService)
      val fakeRequest: Request[JsValue] =
        FakeRequest[JsValue](
          POST,
          controllers.routes.CharlistController.add().url,
          FakeHeaders(),
          Json.parse("""  {"_id":"","timestamp":0,"cp":262,"name":"Bjorn Masterson","description":{"age":35,"height":"6f6i","weight":"250","portrait":"somelink","bio":"longbio"},"coreStats":{"st":10,"dx":13,"iq":10,"ht":12,"will":12,"per":12,"db":0,"reaction":0,"hpMod":0,"fpMod":0,"liftMod":0,"speedMod":-0.25,"moveMod":0,"dodgeMod":1,"parryMod":0,"blockMod":0,"hpLoss":0,"fpLoss":0},"features":[{"feature":"Combat Reflexes","cp":5},{"feature":"Sorcery 2","cp":30},{"feature":"Hot Pilot 3","cp":15},{"feature":"Outdoorsman 3","cp":30},{"feature":"Accelerated Casting","cp":10},{"feature":"Military Rank 2(informal)","cp":5},{"feature":"Luck (combat -20%)","cp":12},{"feature":"Spell: Poison Bullets 2 (+2d tox)","cp":4},{"feature":"Spell: Feather Fall","cp":6},{"feature":"Spell: Smoke Grenade 10 (50m / 2y radius / 10sec","cp":5},{"feature":"Spell: Camouflage 3 (+6 to Stealth)","cp":5},{"feature":"Spell: Ballistic Shield 4 (4DR FF vs ranged)","cp":6},{"feature":"Spell: Entangle 15 (binding 15ST)","cp":4},{"feature":"Sense of Duty (crew)","cp":-5},{"feature":"Greed","cp":-15},{"feature":"Code of Honor (Soldier)","cp":-10},{"feature":"Overconfidence","cp":-5},{"feature":"Flashbacks (Mild)","cp":-5},{"feature":"Wounded","cp":-5}],"skills":[{"name":"Rifle","attr":"DX","diff":"Easy","cp":20,"mod":0},{"name":"Piloting (Lighter-than-air)","attr":"DX","diff":"Average","cp":4,"mod":3},{"name":"Acrobatics","attr":"DX","diff":"Hard","cp":4,"mod":0},{"name":"Streetwise","attr":"IQ","diff":"Average","cp":4,"mod":0}],"techniques":[{"name":"Off-hand weapon training","skill":"Rifle","diff":"Average","style":"Trench Warfare","cp":2}],"melee":[{"name":"Brawling","attacks":[{"name":"Punch","damage":{"apply":true,"attackType":"Thrusting","dmgDice":0,"dmgMod":0,"armorDiv":1,"dmgType":"cr"},"followup":{"apply":false,"attackType":"","dmgDice":0,"dmgMod":0,"armorDiv":1,"dmgType":""},"linked":{"apply":false,"attackType":"","dmgDice":0,"dmgMod":0,"armorDiv":1,"dmgType":""},"skill":"Brawling","parry":0,"block":true,"unbalanced":false,"hands":"one","reach":"C","notes":""}],"notes":"","wt":0,"cost":0}],"ranged":[{"name":"Revolver","attacks":[{"name":"Solid Bullet","damage":{"apply":true,"dmgDice":2,"diceMult":1,"dmgMod":0,"armorDiv":1,"dmgType":"pi+"},"followup":{"apply":false,"dmgDice":0,"diceMult":0,"dmgMod":0,"armorDiv":1,"dmgType":""},"fragDice":0,"linked":{"apply":false,"dmgDice":0,"diceMult":0,"dmgMod":0,"armorDiv":1,"dmgType":""},"skill":"Guns(Pistol)","acc":2,"rng":"200/500","rof":1,"rcl":3,"shots":6,"reload":"(10i)","shotsLeft":6,"st":10,"malf":17,"notes":"","shotWt":0.019999999552965164,"shotCost":3},{"name":"Poisoned Solid Bullet","damage":{"apply":true,"dmgDice":2,"diceMult":1,"dmgMod":0,"armorDiv":1,"dmgType":"pi+"},"followup":{"apply":false,"dmgDice":0,"diceMult":0,"dmgMod":0,"armorDiv":1,"dmgType":""},"fragDice":0,"linked":{"apply":true,"dmgDice":2,"diceMult":1,"dmgMod":0,"armorDiv":1,"dmgType":"tox"},"skill":"Guns(Pistol)","acc":2,"rng":"200/500","rof":1,"rcl":3,"shots":6,"reload":"(10i)","shotsLeft":0,"st":10,"malf":17,"notes":"With spell, uses regular round","shotWt":0.019999999552965164,"shotCost":3},{"name":"HP","damage":{"apply":true,"dmgDice":2,"diceMult":1,"dmgMod":0,"armorDiv":0.5,"dmgType":"pi++"},"followup":{"apply":false,"dmgDice":0,"diceMult":0,"dmgMod":0,"armorDiv":1,"dmgType":""},"fragDice":0,"linked":{"apply":false,"dmgDice":0,"diceMult":0,"dmgMod":0,"armorDiv":1,"dmgType":""},"skill":"Guns(Pistol)","acc":2,"rng":"200/500","rof":1,"rcl":3,"shots":6,"reload":"(10i)","shotsLeft":6,"st":10,"malf":17,"notes":"","shotWt":0.019999999552965164,"shotCost":3}],"bulk":-3,"notes":"","wt":2.9000000953674316,"cost":200}],"armor":[{"name":"Boots","dr":1,"ep":1,"epi":1,"soft":false,"locations":[16],"notes":"","wt":2,"cost":20}],"items":[{"name":"Revolver rounds","carried":"Combat","notes":"","wt":0.019999999552965164,"cost":1.5,"n":18},{"name":"Belt Revolver Holster","carried":"Combat","notes":"","wt":0.5,"cost":25,"n":1},{"name":"Small Backpack","carried":"Travel","notes":"","wt":3,"cost":60,"n":1}]} """)
        )
      val result: Future[Result] = charlistController.add().apply(fakeRequest)
      assertResult(BAD_REQUEST)(status(result))
      //      assertResult(Json.obj("message" -> "", "cause" -> ""))(contentAsJson(result))
    }
  }

  "CharlistController#GET/api/char" should {

    "send OK with default charlist" in {
      val mockCharlistService = mock[CharlistService]
      when(mockCharlistService save anyObject[Charlist]) thenReturn Future(Completed())
      val fakeRequest: Request[AnyContent] =
        FakeRequest(
          GET,
          controllers.routes.CharlistController.create().url
        )
      val result: Future[Result] =
        new CharlistController(mockCharlistService)
          .create()
          .apply(fakeRequest)
      assertResult(OK)(status(result))
      assertResult(
        """{"_id":"","timestamp":0,"player":"","access":[],"name":"","cp":0,"cpTotal":0,"description":{"age":"","height":"","weight":"","portrait":"","bio":""},"stats":{"st":{"delta":0,"bonus":0,"notes":"","value":10,"cp":0},"dx":{"delta":0,"bonus":0,"notes":"","value":10,"cp":0},"iq":{"delta":0,"bonus":0,"notes":"","value":10,"cp":0},"ht":{"delta":0,"bonus":0,"notes":"","value":10,"cp":0},"will":{"delta":0,"bonus":0,"notes":"","value":10,"cp":0},"per":{"delta":0,"bonus":0,"notes":"","value":10,"cp":0},"liftSt":{"delta":0,"bonus":0,"notes":"","value":10,"cp":0},"strikeSt":{"delta":0,"bonus":0,"notes":"","value":10,"cp":0},"thrDmg":"1d-2","swDmg":"1d","bl":20,"combatEncumbrance":0,"travelEncumbrance":0,"hp":{"delta":0,"bonus":0,"notes":"","value":10,"cp":0,"lost":0,"compromised":false,"collapsing":false},"fp":{"delta":0,"bonus":0,"notes":"","value":10,"cp":0,"lost":0,"compromised":false,"collapsing":false},"basicSpeed":{"delta":0,"bonus":0,"notes":"","value":5,"cp":0},"basicMove":{"delta":0,"bonus":0,"notes":"","value":5,"cp":0},"basicDodge":{"delta":0,"bonus":0,"notes":"","value":8,"cp":0},"combMove":5,"travMove":5,"dodge":8,"sm":0},"traits":[],"skills":[],"techniques":[],"equip":{"weapons":[],"armor":[{"name":"Skull","carried":"Equipped","db":0,"dr":2,"ep":0,"epi":0,"front":true,"back":true,"drType":"tough skin","locations":["skull"],"hp":0,"hpLeft":0,"broken":false,"lc":5,"tl":0,"notes":"","wt":0,"cost":0}],"items":[],"frontDR":{"skull":{"dr":2,"ep":0,"epi":0},"eyes":{"dr":0,"ep":0,"epi":0},"face":{"dr":0,"ep":0,"epi":0},"neck":{"dr":0,"ep":0,"epi":0},"armLeft":{"dr":0,"ep":0,"epi":0},"armRight":{"dr":0,"ep":0,"epi":0},"handLeft":{"dr":0,"ep":0,"epi":0},"handRight":{"dr":0,"ep":0,"epi":0},"chest":{"dr":0,"ep":0,"epi":0},"vitals":{"dr":0,"ep":0,"epi":0},"abdomen":{"dr":0,"ep":0,"epi":0},"groin":{"dr":0,"ep":0,"epi":0},"legLeft":{"dr":0,"ep":0,"epi":0},"legRight":{"dr":0,"ep":0,"epi":0},"footLeft":{"dr":0,"ep":0,"epi":0},"footRight":{"dr":0,"ep":0,"epi":0}},"rearDR":{"skull":{"dr":2,"ep":0,"epi":0},"eyes":{"dr":0,"ep":0,"epi":0},"face":{"dr":0,"ep":0,"epi":0},"neck":{"dr":0,"ep":0,"epi":0},"armLeft":{"dr":0,"ep":0,"epi":0},"armRight":{"dr":0,"ep":0,"epi":0},"handLeft":{"dr":0,"ep":0,"epi":0},"handRight":{"dr":0,"ep":0,"epi":0},"chest":{"dr":0,"ep":0,"epi":0},"vitals":{"dr":0,"ep":0,"epi":0},"abdomen":{"dr":0,"ep":0,"epi":0},"groin":{"dr":0,"ep":0,"epi":0},"legLeft":{"dr":0,"ep":0,"epi":0},"legRight":{"dr":0,"ep":0,"epi":0},"footLeft":{"dr":0,"ep":0,"epi":0},"footRight":{"dr":0,"ep":0,"epi":0}},"totalDb":0,"totalCost":0,"totalCombWt":0,"totalTravWt":0},"conditions":{"unconscious":false,"mortallyWounded":false,"dead":false,"shock":0,"stunned":false,"afflictions":{"coughing":false,"drowsy":false,"drunk":false,"euphoria":false,"nauseated":false,"pain":false,"tipsy":false,"agony":false,"choking":false,"daze":false,"ecstasy":false,"hallucinating":false,"paralysis":false,"retching":false,"seizure":false,"coma":false,"heartAttack":false},"cripplingInjuries":[],"posture":"Standing","closeCombat":false,"grappled":false,"pinned":false,"sprinting":false,"mounted":false}}"""
      )(contentAsString(result))
    }
  }

  "CharlistController#GET/api/chars" should {

    "send OK on request" in {
      val mockCharlistService = mock[CharlistService]
      when(mockCharlistService.find) thenReturn Future(Seq[JsObject]())
      val fakeRequest: Request[AnyContent] =
        FakeRequest(
          GET,
          controllers.routes.CharlistController.list().url
        )
      val result: Future[Result] =
        new CharlistController(mockCharlistService)
          .list
          .apply(fakeRequest)
      assertResult(OK)(status(result))
      assertResult(Json toJson Seq[JsObject]())(contentAsJson(result))
    }
  }


}
