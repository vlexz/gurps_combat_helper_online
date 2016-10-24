import com.mongodb.client.result.UpdateResult
import controllers.CharlistController
import daos.CharlistDao
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

import scala.concurrent.Future


/**
  * Created by crimson on 9/26/16.
  */
class CharlistControllerSpec extends PlaySpec with Results with MockitoSugar {

  "CharlistController#POST/api/char" should {
    "send OK with recalculated charlist on request with valid charlist json" in {
      val mockCharlistDao = mock[CharlistDao]
      when(mockCharlistDao save anyObject[Charlist]) thenReturn Future(Completed())
      val fakeRequest: Request[JsValue] =
        FakeRequest[JsValue](
          POST,
          controllers.routes.CharlistController.add().url,
          FakeHeaders(),
          Json parse JsonCharlist.jsonCharlist.toString
        )
      val result: Future[Result] = new CharlistController(mockCharlistDao).add()(fakeRequest)
      assertResult(OK)(status(result)) // TODO: mock random id and timestamp
      //      assertResult(
      //        """{"_id":"","timestamp":0,"player":"","access":[],"name":"","cp":{"cp":0,"stats":0,"adv":0,"dis":0,"skills":0,"unspent":0},"description":{"age":"","height":"","weight":"","portrait":"","bio":""},"stats":{"st":{"delta":0,"base":10,"bonus":0,"cpMod":100,"cp":0},"dx":{"delta":0,"base":10,"bonus":0,"cpMod":100,"cp":0},"iq":{"delta":0,"base":10,"bonus":0,"cpMod":100,"cp":0},"ht":{"delta":0,"base":10,"bonus":0,"cpMod":100,"cp":0},"will":{"delta":0,"base":10,"bonus":0,"cpMod":100,"cp":0},"per":{"delta":0,"base":10,"bonus":0,"cpMod":100,"cp":0},"liftSt":{"delta":0,"base":10,"bonus":0,"cpMod":100,"cp":0},"strikeSt":{"delta":0,"base":10,"bonus":0,"cpMod":100,"cp":0},"hp":{"delta":0,"base":10,"bonus":0,"cpMod":100,"cp":0,"lost":0,"compromised":false,"collapsing":false},"fp":{"delta":0,"base":10,"bonus":0,"cpMod":100,"cp":0,"lost":0,"compromised":false,"collapsing":false},"basicSpeed":{"delta":0,"base":5,"bonus":0,"cpMod":100,"cp":0},"basicMove":{"delta":0,"base":5,"bonus":0,"cpMod":100,"cp":0}},"statVars":{"frightCheck":10,"vision":10,"hearing":10,"tasteSmell":10,"touch":10,"thrDmg":"1d-2","swDmg":"1d","bl":20,"combatEncumbrance":0,"travelEncumbrance":0,"combMove":4,"travMove":4,"dodge":8,"sm":0},"reactions":[],"traits":[{"name":"Skull","types":["Physical"],"category":"Advantage","ref":"","notes":"","prerequisites":[],"modifiers":[],"attrBonuses":[],"skillBonuses":[],"dmgBonuses":[],"drBonuses":[{"locations":["skull"],"perLvl":true,"front":true,"back":true,"dr":2,"ep":0,"epi":0}],"attrCostMods":[],"reactBonuses":[],"cpBase":0,"level":0,"cpPerLvl":0,"cp":0}],"skills":[],"techniques":[],"equip":{"weapons":[],"armor":[],"items":[],"frontDR":{"skull":{"dr":2,"ep":0,"epi":0},"eyes":{"dr":0,"ep":0,"epi":0},"face":{"dr":0,"ep":0,"epi":0},"neck":{"dr":0,"ep":0,"epi":0},"armLeft":{"dr":0,"ep":0,"epi":0},"armRight":{"dr":0,"ep":0,"epi":0},"handLeft":{"dr":0,"ep":0,"epi":0},"handRight":{"dr":0,"ep":0,"epi":0},"chest":{"dr":0,"ep":0,"epi":0},"vitals":{"dr":0,"ep":0,"epi":0},"abdomen":{"dr":0,"ep":0,"epi":0},"groin":{"dr":0,"ep":0,"epi":0},"legLeft":{"dr":0,"ep":0,"epi":0},"legRight":{"dr":0,"ep":0,"epi":0},"footLeft":{"dr":0,"ep":0,"epi":0},"footRight":{"dr":0,"ep":0,"epi":0}},"rearDR":{"skull":{"dr":2,"ep":0,"epi":0},"eyes":{"dr":0,"ep":0,"epi":0},"face":{"dr":0,"ep":0,"epi":0},"neck":{"dr":0,"ep":0,"epi":0},"armLeft":{"dr":0,"ep":0,"epi":0},"armRight":{"dr":0,"ep":0,"epi":0},"handLeft":{"dr":0,"ep":0,"epi":0},"handRight":{"dr":0,"ep":0,"epi":0},"chest":{"dr":0,"ep":0,"epi":0},"vitals":{"dr":0,"ep":0,"epi":0},"abdomen":{"dr":0,"ep":0,"epi":0},"groin":{"dr":0,"ep":0,"epi":0},"legLeft":{"dr":0,"ep":0,"epi":0},"legRight":{"dr":0,"ep":0,"epi":0},"footLeft":{"dr":0,"ep":0,"epi":0},"footRight":{"dr":0,"ep":0,"epi":0}},"totalDb":0,"totalCost":0,"totalCombWt":0,"totalTravWt":0},"conditions":{"unconscious":false,"mortallyWounded":false,"dead":false,"shock":0,"stunned":false,"afflictions":{"coughing":false,"drowsy":false,"drunk":false,"euphoria":false,"nauseated":false,"pain":false,"tipsy":false,"agony":false,"choking":false,"daze":false,"ecstasy":false,"hallucinating":false,"paralysis":false,"retching":false,"seizure":false,"coma":false,"heartAttack":false},"cripplingInjuries":[],"posture":"Standing","closeCombat":false,"grappled":false,"pinned":false,"sprinting":false,"mounted":false},"api":"0.2"}"""
      //      )(contentAsString(result))
    }
  }

  it should {
    "send BAD_REQUEST on request with invalid charlist json format" in {
      val mockCharlistDao = mock[CharlistDao]
      when(mockCharlistDao save anyObject[Charlist]) thenReturn Future(Completed())
      val charlistController = new CharlistController(mockCharlistDao)
      val fakeRequest: Request[JsValue] =
        FakeRequest[JsValue](
          POST,
          controllers.routes.CharlistController.add().url,
          FakeHeaders(),
          Json.parse(
            """  {"_id":"","timestamp":0,"cp":262,"name":"Bjorn Masterson","description":{"age":35,"height":"6f6i","weight":"250","portrait":"somelink","bio":"longbio"},"coreStats":{"st":10,"dx":13,"iq":10,"ht":12,"will":12,"per":12,"db":0,"reaction":0,"hpMod":0,"fpMod":0,"liftMod":0,"speedMod":-0.25,"moveMod":0,"dodgeMod":1,"parryMod":0,"blockMod":0,"hpLoss":0,"fpLoss":0},"features":[{"feature":"Combat Reflexes","cp":5},{"feature":"Sorcery 2","cp":30},{"feature":"Hot Pilot 3","cp":15},{"feature":"Outdoorsman 3","cp":30},{"feature":"Accelerated Casting","cp":10},{"feature":"Military Rank 2(informal)","cp":5},{"feature":"Luck (combat -20%)","cp":12},{"feature":"Spell: Poison Bullets 2 (+2d tox)","cp":4},{"feature":"Spell: Feather Fall","cp":6},{"feature":"Spell: Smoke Grenade 10 (50m / 2y radius / 10sec","cp":5},{"feature":"Spell: Camouflage 3 (+6 to Stealth)","cp":5},{"feature":"Spell: Ballistic Shield 4 (4DR FF vs ranged)","cp":6},{"feature":"Spell: Entangle 15 (binding 15ST)","cp":4},{"feature":"Sense of Duty (crew)","cp":-5},{"feature":"Greed","cp":-15},{"feature":"Code of Honor (Soldier)","cp":-10},{"feature":"Overconfidence","cp":-5},{"feature":"Flashbacks (Mild)","cp":-5},{"feature":"Wounded","cp":-5}],"skills":[{"name":"Rifle","attr":"DX","diff":"Easy","cp":20,"mod":0},{"name":"Piloting (Lighter-than-air)","attr":"DX","diff":"Average","cp":4,"mod":3},{"name":"Acrobatics","attr":"DX","diff":"Hard","cp":4,"mod":0},{"name":"Streetwise","attr":"IQ","diff":"Average","cp":4,"mod":0}],"techniques":[{"name":"Off-hand weapon training","skill":"Rifle","diff":"Average","style":"Trench Warfare","cp":2}],"melee":[{"name":"Brawling","attacks":[{"name":"Punch","damage":{"apply":true,"attackType":"Thrusting","dmgDice":0,"dmgMod":0,"armorDiv":1,"dmgType":"cr"},"followup":{"apply":false,"attackType":"","dmgDice":0,"dmgMod":0,"armorDiv":1,"dmgType":""},"linked":{"apply":false,"attackType":"","dmgDice":0,"dmgMod":0,"armorDiv":1,"dmgType":""},"skill":"Brawling","parry":0,"block":true,"unbalanced":false,"hands":"one","reach":"C","notes":""}],"notes":"","wt":0,"cost":0}],"ranged":[{"name":"Revolver","attacks":[{"name":"Solid Bullet","damage":{"apply":true,"dmgDice":2,"diceMult":1,"dmgMod":0,"armorDiv":1,"dmgType":"pi+"},"followup":{"apply":false,"dmgDice":0,"diceMult":0,"dmgMod":0,"armorDiv":1,"dmgType":""},"fragDice":0,"linked":{"apply":false,"dmgDice":0,"diceMult":0,"dmgMod":0,"armorDiv":1,"dmgType":""},"skill":"Guns(Pistol)","acc":2,"rng":"200/500","rof":1,"rcl":3,"shots":6,"reload":"(10i)","shotsLeft":6,"st":10,"malf":17,"notes":"","shotWt":0.019999999552965164,"shotCost":3},{"name":"Poisoned Solid Bullet","damage":{"apply":true,"dmgDice":2,"diceMult":1,"dmgMod":0,"armorDiv":1,"dmgType":"pi+"},"followup":{"apply":false,"dmgDice":0,"diceMult":0,"dmgMod":0,"armorDiv":1,"dmgType":""},"fragDice":0,"linked":{"apply":true,"dmgDice":2,"diceMult":1,"dmgMod":0,"armorDiv":1,"dmgType":"tox"},"skill":"Guns(Pistol)","acc":2,"rng":"200/500","rof":1,"rcl":3,"shots":6,"reload":"(10i)","shotsLeft":0,"st":10,"malf":17,"notes":"With spell, uses regular round","shotWt":0.019999999552965164,"shotCost":3},{"name":"HP","damage":{"apply":true,"dmgDice":2,"diceMult":1,"dmgMod":0,"armorDiv":0.5,"dmgType":"pi++"},"followup":{"apply":false,"dmgDice":0,"diceMult":0,"dmgMod":0,"armorDiv":1,"dmgType":""},"fragDice":0,"linked":{"apply":false,"dmgDice":0,"diceMult":0,"dmgMod":0,"armorDiv":1,"dmgType":""},"skill":"Guns(Pistol)","acc":2,"rng":"200/500","rof":1,"rcl":3,"shots":6,"reload":"(10i)","shotsLeft":6,"st":10,"malf":17,"notes":"","shotWt":0.019999999552965164,"shotCost":3}],"bulk":-3,"notes":"","wt":2.9000000953674316,"cost":200}],"armor":[{"name":"Boots","dr":1,"ep":1,"epi":1,"soft":false,"locations":[16],"notes":"","wt":2,"cost":20}],"items":[{"name":"Revolver rounds","carried":"Combat","notes":"","wt":0.019999999552965164,"cost":1.5,"n":18},{"name":"Belt Revolver Holster","carried":"Combat","notes":"","wt":0.5,"cost":25,"n":1},{"name":"Small Backpack","carried":"Travel","notes":"","wt":3,"cost":60,"n":1}]} """
          )
        )
      val result: Future[Result] = charlistController.add().apply(fakeRequest)
      assertResult(BAD_REQUEST)(status(result))
      assertResult(Json.obj("message" -> "Invalid request body."))(contentAsJson(result))
    }
  }

  it should {
    "send BAD_REQUEST with assertion hint on request with invalid charlist data" in {
      val mockCharlistDao = mock[CharlistDao]
      when(mockCharlistDao save anyObject[Charlist]) thenReturn Future(Completed())
      val charlistController = new CharlistController(mockCharlistDao)
      val fakeRequest: Request[JsValue] =
        FakeRequest[JsValue](
          POST,
          controllers.routes.CharlistController.add().url,
          FakeHeaders(),
          Json.parse(
            """  {"_id":"","timestamp":0,"player":"vlex","access":[],"name":"Bjorn Masterson","cp":262,"cpTotal":164,"description":{"age":"35","height":"6f6i","weight":"250","portrait":"somelink","bio":"longbio"},"stats":{"st":{"base":10,"delta":0,"bonus":0,"cpMod":100,"cp":0,"notes":""},"dx":{"base":10,"delta":3,"bonus":0,"cpMod":100,"cp":60,"notes":""},"iq":{"base":10,"delta":0,"bonus":0,"cpMod":100,"cp":0,"notes":""},"ht":{"base":10,"delta":2,"bonus":0,"cpMod":100,"cp":20,"notes":""},"will":{"base":10,"delta":2,"bonus":0,"cpMod":100,"cp":10,"notes":""},"per":{"base":10,"delta":2,"bonus":0,"cpMod":100,"cp":10,"notes":""},"liftSt":{"base":10,"delta":0,"bonus":0,"cpMod":100,"cp":0,"notes":""},"strikeSt":{"base":10,"delta":0,"bonus":0,"cpMod":100,"cp":0,"notes":""},"thrDmg":"1d-2","swDmg":"1d","bl":20,"combatEncumbrance":0,"travelEncumbrance":0,"hp":{"base":10,"delta":0,"bonus":0,"cpMod":100,"cp":0,"notes":"","lost":-1,"compromised":false,"collapsing":false},"fp":{"base":12,"delta":0,"bonus":0,"cpMod":100,"cp":0,"notes":"","lost":0,"compromised":false,"collapsing":false},"basicSpeed":{"base":6.25,"delta":-0.25,"bonus":0,"cpMod":100,"cp":-5,"notes":""},"basicMove":{"base":6,"delta":0,"bonus":0,"cpMod":100,"cp":0,"notes":""},"basicDodge":{"base":9,"delta":0,"bonus":1,"cpMod":100,"cp":0,"notes":""},"combMove":6,"travMove":6,"dodge":10,"sm":0},"traits":[{"name":"Combat Reflexes","cp":15},{"name":"Sorcery 2","cp":30},{"name":"Hot Pilot 3","cp":15}],"skills":[{"name":"Guns(Pistol","attr":"DX","diff":"Average","defaults":[],"prerequisites":[],"bonus":0,"notes":"","cp":4,"relLvl":1,"lvl":14}],"techniques":[{"name":"Off-hand weapon training","skill":"Guns(Pistol)","diff":"Hard","style":"Trench Warfare","defLvl":-4,"maxLvl":0,"notes":"","cp":5,"relLvl":0,"lvl":0}],"equip":{"weapons":[{"name":"Brawling","carried":"Ready","attacksMelee":[{"name":"Punch","available":true,"damage":{"attackType":"thr","dmgDice":0,"dmgMod":0,"armorDiv":1,"dmgType":"cr","dmgString":"1d-2 cr"},"followup":[{"attackType":"","dmgDice":4,"dmgMod":0,"armorDiv":1,"dmgType":"burn","dmgString":"4d burn"}],"linked":[{"attackType":"","dmgDice":2,"dmgMod":0,"armorDiv":1,"dmgType":"tox","dmgString":"2d tox"}],"skill":"Brawling","parry":0,"parryType":"","parryString":"0","st":1,"hands":"","reach":"C","notes":""}],"attacksRanged":[],"grips":[],"offHand":false,"bulk":-1,"block":false,"db":0,"dr":0,"hp":4,"hpLeft":4,"broken":false,"lc":5,"tl":0,"notes":"","wt":0,"cost":0,"totalWt":0,"totalCost":0},{"name":"Revolver","carried":"Ready","attacksMelee":[],"attacksRanged":[{"name":"LE","available":true,"damage":{"dmgDice":1,"diceMult":1,"dmgMod":2,"armorDiv":0.5,"dmgType":"pi+","fragDice":0,"dmgString":"1d+2(0.5) pi+"},"followup":[{"dmgDice":1,"diceMult":1,"dmgMod":1,"armorDiv":1,"dmgType":"cr ex","fragDice":1,"dmgString":"1d+1 cr ex [1d]"}],"linked":[{"dmgDice":2,"diceMult":1,"dmgMod":0,"armorDiv":1,"dmgType":"tox","fragDice":0,"dmgString":"2d tox"}],"skill":"Guns(Pistol)","acc":2,"accMod":0,"rng":"50/200","rof":{"rof":1,"rofMult":1,"rofFA":false,"rofJet":false,"rofString":"1"},"rcl":2,"shots":{"shots":6,"reload":"(3i)","shotsLoaded":6,"shotsCarried":30,"shotWt":0.1,"shotCost":2,"shotsString":"6/6(3i) 30","totalWt":3.6,"totalCost":72},"st":9,"hands":"","malf":17,"notes":""}],"grips":[],"offHand":true,"bulk":-2,"block":false,"db":0,"dr":7,"hp":10,"hpLeft":10,"broken":false,"lc":4,"tl":5,"notes":"","wt":4.1,"cost":350,"totalWt":7.699999999999999,"totalCost":422}],"armor":[{"name":"Boots","carried":"Equipped","db":0,"dr":1,"ep":1,"epi":1,"front":true,"back":true,"drType":"hard","locations":["feet"],"hp":5,"hpLeft":5,"broken":false,"lc":5,"tl":5,"notes":"","wt":2,"cost":50}],"items":[{"name":"Belt Revolver Holster","carried":"Equipped","dr":1,"hp":5,"hpLeft":5,"broken":false,"lc":5,"tl":5,"notes":"","wt":0.5,"cost":75,"n":1,"totalWt":0.5,"totalCost":75},{"name":"Small Backpack","carried":"Travel","dr":0,"hp":5,"hpLeft":5,"broken":false,"lc":5,"tl":5,"notes":"","wt":3,"cost":60,"n":1,"totalWt":3,"totalCost":60}],"frontDR":{"skull":{"dr":0,"ep":0,"epi":0},"eyes":{"dr":0,"ep":0,"epi":0},"face":{"dr":0,"ep":0,"epi":0},"neck":{"dr":0,"ep":0,"epi":0},"armLeft":{"dr":0,"ep":0,"epi":0},"armRight":{"dr":0,"ep":0,"epi":0},"handLeft":{"dr":0,"ep":0,"epi":0},"handRight":{"dr":0,"ep":0,"epi":0},"chest":{"dr":0,"ep":0,"epi":0},"vitals":{"dr":0,"ep":0,"epi":0},"abdomen":{"dr":0,"ep":0,"epi":0},"groin":{"dr":0,"ep":0,"epi":0},"legLeft":{"dr":0,"ep":0,"epi":0},"legRight":{"dr":0,"ep":0,"epi":0},"footLeft":{"dr":1,"ep":1,"epi":1},"footRight":{"dr":0,"ep":0,"epi":0}},"rearDR":{"skull":{"dr":0,"ep":0,"epi":0},"eyes":{"dr":0,"ep":0,"epi":0},"face":{"dr":0,"ep":0,"epi":0},"neck":{"dr":0,"ep":0,"epi":0},"armLeft":{"dr":0,"ep":0,"epi":0},"armRight":{"dr":0,"ep":0,"epi":0},"handLeft":{"dr":0,"ep":0,"epi":0},"handRight":{"dr":0,"ep":0,"epi":0},"chest":{"dr":0,"ep":0,"epi":0},"vitals":{"dr":0,"ep":0,"epi":0},"abdomen":{"dr":0,"ep":0,"epi":0},"groin":{"dr":0,"ep":0,"epi":0},"legLeft":{"dr":0,"ep":0,"epi":0},"legRight":{"dr":0,"ep":0,"epi":0},"footLeft":{"dr":1,"ep":1,"epi":1},"footRight":{"dr":0,"ep":0,"epi":0}},"totalDb":0,"totalCost":607,"totalCombWt":10.2,"totalTravWt":13.2},"conditions":{"unconscious":false,"mortallyWounded":false,"dead":false,"shock":0,"stunned":false,"afflictions":{"coughing":false,"drowsy":false,"drunk":false,"euphoria":false,"nauseated":false,"pain":false,"tipsy":false,"agony":false,"choking":false,"daze":false,"ecstasy":false,"hallucinating":false,"paralysis":false,"retching":false,"seizure":false,"coma":false,"heartAttack":false},"cripplingInjuries":[],"posture":"Standing","closeCombat":false,"grappled":false,"pinned":false,"sprinting":false,"mounted":false},"api":"0.1"} """
          )
        )
      val result: Future[Result] = charlistController.add().apply(fakeRequest)
      assertResult(BAD_REQUEST)(status(result))
      assertResult(Json.obj(
        "message" -> "Charlist assertion failed: negative points lost value (-1)"
      ))(contentAsJson(result))
    }
  }

  "CharlistController#GET/api/char" should {
    "send OK with default charlist" in {
      val mockCharlistDao = mock[CharlistDao]
      when(mockCharlistDao save anyObject[Charlist]) thenReturn Future(Completed())
      val fakeRequest: Request[AnyContent] =
        FakeRequest(
          GET,
          controllers.routes.CharlistController.create().url
        )
      val result: Future[Result] =
        new CharlistController(mockCharlistDao)
          .create()
          .apply(fakeRequest)
      assertResult(OK)(status(result))
      assertResult(Json.toJson(Charlist()).toString)(contentAsString(result))
    }
  }

  "CharlistController#GET/api/chars" should {
    "send OK with charlist headers list on request" in {
      val mockCharlistDao = mock[CharlistDao]
      when(mockCharlistDao.find) thenReturn Future(Seq[JsObject]())
      val fakeRequest: Request[AnyContent] =
        FakeRequest(
          GET,
          controllers.routes.CharlistController.list().url
        )
      val result: Future[Result] =
        new CharlistController(mockCharlistDao)
          .list
          .apply(fakeRequest)
      assertResult(OK)(status(result))
      assertResult(Json toJson Seq[JsObject]())(contentAsJson(result))
    }
  }

  "CharlistController#GET/api/char/:id" should {
    "send OK with charlist on request with valid id" in {
      val mockCharlistDao = mock[CharlistDao]
      when(mockCharlistDao find "123") thenReturn Future(Json toJson JsonCharlist.charlist)
      val fakeRequest: Request[AnyContent] =
        FakeRequest(
          GET,
          controllers.routes.CharlistController.get("123").url
        )
      val result: Future[Result] = new CharlistController(mockCharlistDao).get("123")(fakeRequest)
      assertResult(OK)(status(result))
      assertResult(JsonCharlist.jsonCharlist.toString)(contentAsString(result))
    }
  }

  it should {
    "send NOT_FOUND on request with invalid id" in {
      val mockCharlistDao = mock[CharlistDao]
      when(mockCharlistDao find "123") thenThrow new NoSuchElementException()
      val fakeRequest: Request[AnyContent] =
        FakeRequest(
          GET,
          controllers.routes.CharlistController.get("123").url
        )
      val result: Future[Result] =
        new CharlistController(mockCharlistDao)
          .get("123")
          .apply(fakeRequest)
      assertResult(NOT_FOUND)(status(result))
    }
  }

  "CharlistController#PATCH/api/char/:id" should {
    "send OK with updated charlist on request with valid id and update json" in {
      val mockCharlistDao = mock[CharlistDao]
      when(mockCharlistDao find "123") thenReturn Future(JsonCharlist.jsonCharlist)
      when(mockCharlistDao update anyObject[Charlist]) thenReturn Future(UpdateResult.unacknowledged)
      val fakeRequest: Request[JsValue] =
        FakeRequest[JsValue](
          PATCH,
          controllers.routes.CharlistController.update("123").url,
          FakeHeaders(),
          Json.parse("""{"stats": {"st":{"delta":10}}}""")
        )
      val result: Future[Result] = new CharlistController(mockCharlistDao).update("123")(fakeRequest)
      val ch = JsonCharlist.charlist
      assertResult(OK)(status(result))
      assertResult(
        Json.toJson(ch.copy(_id = "123", stats = ch.stats.copy(st = ch.stats.st.copy(delta = 10)))).toString
      )(contentAsString(result))
    }
  }

  "CharlistController#PUT/api/char/:id" should {}

  "CharlistController#DELETE/api/char/:id" should {}

}
