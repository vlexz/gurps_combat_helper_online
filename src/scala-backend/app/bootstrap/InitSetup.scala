package bootstrap

import com.google.inject.Inject
import com.mongodb.client.model.IndexOptions
import play.Logger
import services.Mongo


class InitSetup @Inject()(mongo: Mongo) {

  def preMessages(): Unit = {
    Logger.info("Bootstrapping application..")
  }

  def defineIndexes(): Unit = {
    val options: IndexOptions = new IndexOptions().unique(true)
//    mongo.db.getCollection("user").createIndex(ascending("username"), options).subscribe((s: String) => {
//      Logger.info("username index created on User collection")
//    })
    //    mongo.db.getCollection("user").createIndex(ascending("email"), options).subscribe((s: String) => {
    //      Logger.info("email index created on User collection")
    //    })
  }

  preMessages()
  defineIndexes()

}
