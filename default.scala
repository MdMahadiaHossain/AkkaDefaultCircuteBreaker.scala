import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import akka.actor.SuppressedDeadLetter
import akka.pattern.CircuitBreaker
import akka.actor.ActorLogging
import akka.compat.Future
import scala.concurrent.Future
import akka.pattern.pipe

object Main2 extends App {

  val system = ActorSystem.apply("supervisonSystem")

  val ciurcitBreakerRef = system.actorOf(Props[CiurcitBreaker])

  import scala.concurrent.duration._
  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val timeout: Timeout = 10.second

  1 to 100 foreach { e =>
    (ciurcitBreakerRef ? e).mapTo[String].foreach { s =>
      println(s)
    }
  }
  Thread.sleep(10000)
  100 to 200 foreach { e =>
    (ciurcitBreakerRef ? e).mapTo[String].foreach { s =>
      println(s)
    }
  }
  Thread.sleep(61000)
  300 to 400 foreach { e =>
    (ciurcitBreakerRef ? e).mapTo[String].foreach { s =>
      println(s)
    }
  }
  Thread.sleep(10000)
  400 to 500 foreach { e =>
    (ciurcitBreakerRef ? e).mapTo[String].foreach { s =>
      println(s)
    }
  }

}

case class Message(sender: ActorRef, msg: Int)

object Open

class CiurcitBreaker extends Actor with ActorLogging {

  import scala.concurrent.duration._
  import scala.concurrent.ExecutionContext.Implicits.global

  val workerActorActorRef =
    context.actorOf(Props[WorkerActor].withMailbox("my-mailbox"))

  val breaker =
    new CircuitBreaker(
      context.system.scheduler,
      maxFailures = 5,
      callTimeout = 3.seconds,
      resetTimeout = 1.minute
    ).onOpen(notifyMeOnOpen()).onOpen(notifyMeOnHalfOpen()).onClose(notifyMeOnCloss())

  def notifyMeOnOpen(): Unit =
    println(
      "My CircuitBreaker is now open, and will not close for one minute"
    )

  def notifyMeOnHalfOpen(): Unit =
    println(
      "My CircuitBreaker is now half open"
    )

  def notifyMeOnCloss(): Unit =
    println(
      "My CircuitBreaker is now clossed"
    )

  override def receive: Actor.Receive = {
    case msg: Int if breaker.isClosed =>
      implicit val timeout: Timeout = 10.second
      val res: Future[String] =
        (workerActorActorRef ? Message(self, msg)).mapTo[String]

      breaker.withCircuitBreaker(res).pipeTo(sender())

    case msg: Int if breaker.isHalfOpen =>
      implicit val timeout: Timeout = 10.second
      val res: Future[String] =
        (workerActorActorRef ? Message(self, msg)).mapTo[String]

      breaker.withCircuitBreaker(res).pipeTo(sender())

    case msg: Int if breaker.isOpen =>
      sender() ! "Server loaded " + msg

  }

}

class WorkerActor extends Actor {

  override def receive: Actor.Receive = {
    case m: Message =>
      println("worker got " + m.msg)
      sender() ! m.msg.toString()
      Thread.sleep(3000)

  }

}


application.conf

my-mailbox{
  mailbox-type = "akka.dispatch.NonBlockingBoundedMailbox"
  mailbox-capacity = 10
}

akka {
  loglevel = "DEBUG"
}

result==============

worker got 1
1
worker got 2
2
My CircuitBreaker is now open, and will not close for one minute
My CircuitBreaker is now half open
worker got 3
worker got 4
Server loaded 100
Server loaded 101
Server loaded 102
Server loaded 103
Server loaded 104
Server loaded 105
Server loaded 106
Server loaded 108
Server loaded 109
Server loaded 110
Server loaded 111
Server loaded 112
Server loaded 113
Server loaded 114
Server loaded 115
Server loaded 116
Server loaded 117
Server loaded 118
Server loaded 119
Server loaded 120
Server loaded 121
Server loaded 122
Server loaded 123
Server loaded 124
Server loaded 125
Server loaded 126
Server loaded 127
Server loaded 128
Server loaded 129
Server loaded 130
Server loaded 131
Server loaded 132
Server loaded 133
Server loaded 134
Server loaded 135
Server loaded 136
Server loaded 137
Server loaded 138
Server loaded 139
Server loaded 107
Server loaded 140
Server loaded 141
Server loaded 142
Server loaded 143
Server loaded 144
Server loaded 145
Server loaded 146
Server loaded 147
Server loaded 148
Server loaded 149
Server loaded 150
Server loaded 151
Server loaded 152
Server loaded 153
Server loaded 154
Server loaded 155
Server loaded 156
Server loaded 157
Server loaded 158
Server loaded 159
Server loaded 160
Server loaded 161
Server loaded 162
Server loaded 163
Server loaded 164
Server loaded 165
Server loaded 166
Server loaded 167
Server loaded 168
Server loaded 169
Server loaded 170
Server loaded 171
Server loaded 172
Server loaded 173
Server loaded 174
Server loaded 175
Server loaded 176
Server loaded 177
Server loaded 178
Server loaded 179
Server loaded 180
Server loaded 181
Server loaded 182
Server loaded 183
Server loaded 184
Server loaded 185
Server loaded 188
Server loaded 190
Server loaded 191
Server loaded 192
Server loaded 193
Server loaded 194
Server loaded 195
Server loaded 196
Server loaded 197
Server loaded 198
Server loaded 199
Server loaded 200
Server loaded 187
Server loaded 186
Server loaded 189
worker got 5
worker got 6
worker got 7
worker got 8
worker got 9
worker got 10
worker got 11
worker got 300
My CircuitBreaker is now clossed
300
worker got 301
301
My CircuitBreaker is now open, and will not close for one minute
My CircuitBreaker is now half open
worker got 302
worker got 303
Server loaded 400
Server loaded 401
Server loaded 402
Server loaded 403
Server loaded 404
Server loaded 405
Server loaded 406
Server loaded 408
Server loaded 409
Server loaded 410
Server loaded 411
Server loaded 412
Server loaded 413
Server loaded 414
Server loaded 415
Server loaded 416
Server loaded 417
Server loaded 418
Server loaded 419
Server loaded 420
Server loaded 421
Server loaded 422
Server loaded 423
Server loaded 424
Server loaded 425
Server loaded 426
Server loaded 427
Server loaded 428
Server loaded 429
Server loaded 430
Server loaded 407
Server loaded 431
Server loaded 432
Server loaded 433
Server loaded 434
Server loaded 435
Server loaded 436
Server loaded 437
Server loaded 438
Server loaded 439
Server loaded 440
Server loaded 441
Server loaded 442
Server loaded 443
Server loaded 444
Server loaded 445
Server loaded 446
Server loaded 447
Server loaded 448
Server loaded 449
Server loaded 450
Server loaded 451
Server loaded 452
Server loaded 453
Server loaded 457
Server loaded 458
Server loaded 459
Server loaded 460
Server loaded 461
Server loaded 462
Server loaded 463
Server loaded 464
Server loaded 465
Server loaded 466
Server loaded 467
Server loaded 468
Server loaded 469
Server loaded 470
Server loaded 471
Server loaded 472
Server loaded 473
Server loaded 474
Server loaded 475
Server loaded 476
Server loaded 477
Server loaded 478
Server loaded 479
Server loaded 480
Server loaded 481
Server loaded 482
Server loaded 483
Server loaded 484
Server loaded 485
Server loaded 486
Server loaded 487
Server loaded 488
Server loaded 489
Server loaded 490
Server loaded 491
Server loaded 492
Server loaded 493
Server loaded 494
Server loaded 495
Server loaded 496
Server loaded 497
Server loaded 498
Server loaded 499
Server loaded 500
Server loaded 455
Server loaded 456
Server loaded 454
worker got 304
worker got 305
worker got 306
worker got 307
worker got 308
worker got 309
worker got 310



##### Not work with dead-letters
