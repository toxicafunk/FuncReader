package com.neophyte

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

object AsyncFuture {
  // Some type aliases, just for getting more meaningful method signatures:
  type CoffeeBeans = String
  type GroundCoffee = String
  case class Water(temperature: Int)
  type Milk = String
  type FrothedMilk = String
  type Espresso = String
  type Cappuccino = String

  def combine(espresso: Espresso, frothedMilk: FrothedMilk): Cappuccino = "cappuccino"

  // some exceptions for things that might go wrong in the individual steps
  // (we'll need some of them later, use the others when experimenting
  // with the code):
  case class GrindingException(msg: String) extends Exception(msg)
  case class FrothingException(msg: String) extends Exception(msg)
  case class WaterBoilingException(msg: String) extends Exception(msg)
  case class BrewingException(msg: String) extends Exception(msg)


  def grind(beans: CoffeeBeans): Future[GroundCoffee] = Future {
    println("start grinding...")
    Thread.sleep(Random.nextInt(2000))
    if (beans == "baked beans") throw GrindingException("are you joking?")
    println("finished grinding...")
    s"ground coffee of $beans"
  }

  def heatWater(water: Water): Future[Water] = Future {
    println("heating the water now")
    Thread.sleep(Random.nextInt(2000))
    println("hot, it's hot!")
    water.copy(temperature = 85)
  }

  def frothMilk(milk: Milk): Future[FrothedMilk] = Future {
    println("milk frothing system engaged!")
    Thread.sleep(Random.nextInt(2000))
    println("shutting down milk frothing system")
    s"frothed $milk"
  }

  def brew(coffee: GroundCoffee, heatedWater: Water): Future[Espresso] = Future {
    println("happy brewing :)")
    Thread.sleep(Random.nextInt(2000))
    println("it's brewed!")
    "espresso"
  }

  /*grind("arabica beans").onSuccess { case ground =>
    println("okay, got my ground coffee")
  }*/
  /*grind("baked beans").onComplete {
    case Success(ground) => println(s"got my $ground")
    case Failure(ex) => println("This grinder needs a replacement, seriously!")
  }*/

  val temperatureOkay: Future[Boolean] = heatWater(Water(25)).map { water =>
    println("we're in the future!")
    (80 to 85).contains(water.temperature)
  }

  def temperatureOkay(water: Water): Future[Boolean] = Future {
    (80 to 85).contains(water.temperature)
  }

  val acceptable: Future[Boolean] = for {
    heatedWater <- heatWater(Water(25))
    okay <- temperatureOkay(heatedWater)
  } yield okay

  def prepareCappuccinoSequentially(): Future[Cappuccino] = {
    for {
      ground <- grind("arabica beans")
      water <- heatWater(Water(20))
      foam <- frothMilk("milk")
      espresso <- brew(ground, water)
    } yield combine(espresso, foam)
  }

  def prepareCappuccino(): Future[Cappuccino] = {
    val groundCoffee = grind("arabica beans")
    val heatedWater = heatWater(Water(20))
    val frothedMilk = frothMilk("milk")
    for {
      ground <- groundCoffee
      water <- heatedWater
      foam <- frothedMilk
      espresso <- brew(ground, water)
    } yield combine(espresso, foam)
  }

  def main(args: Array[String]) {
    prepareCappuccinoSequentially()
    println(">Async now")
    prepareCappuccino()
  }

}