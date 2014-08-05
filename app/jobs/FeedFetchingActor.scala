package jobs

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorSystem
import akka.actor.ActorRef
import scala.concurrent.duration._

class FeedFetchingActor extends Actor {

  def receive = {
    case "FETCH_DATA" => {
      val result = FetchFeeds.asyncFetch
    }

    case _ => throw new Exception("Unknown message")
  }

}

object FeedFetchScheduler {

  val system = ActorSystem("FeedActorSystem")
  val fetchingActor = system.actorOf(Props[FeedFetchingActor], "FeedFetchingActor")

  def startJob = {
    // Use the system's dispatcher as the current ExecutionContext
    import system.dispatcher

    // After 0 milliseconds repeating every 6 hours
    val cancellable = system.scheduler.schedule(0 milliseconds, 6 hours) {
      play.Logger.debug(s" >>> Launching Fetching @ [ ${new java.util.Date()} ]")
      fetchingActor ! "FETCH_DATA"
    }

  }

}
