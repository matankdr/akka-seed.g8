package $package$

import $package$.Game.Game
import $package$.config.BaseSettings

object Game extends Enumeration {
  type Game = Value
  val Rock, Paper, Scissors, Invalid = Value
}

final case class Config(player: String = "", game: Game = Game.Invalid, age: Int = -1)

final case class Params(player: String, game: Game, age: Option[Int])

object CommandLineParser {
  private[this] lazy val applicationSettings = BaseSettings

  private[this] implicit val entityRead: scopt.Read[Game.Value] =
    scopt.Read.reads(Game withName _)

  private[this] val parser = new scopt.OptionParser[Config]("$package$") {
    head(applicationSettings.name)

    opt[String]('p', "player")
      .required()
      .action((x, c) => c.copy(player = x))
      .text("player required")

    opt[Game]('g', "game")
      .required()
      .action((x, c) => c.copy(game = x))
      .text("game required")

    opt[Int]('a', "age")
      .action((x, c) => c.copy(age = x))
      .text("optional age")

  }

  protected[this] def parseAge(age: Int): Option[Int] = age match {
    case value if value < 0 => None
    case _ => Some(age)
  }

  def parse(args: Seq[String]): Either[String, Params] =
    parser.parse(args, Config()) match {
      case Some(config) =>
        Right(Params(config.player, config.game, parseAge(config.age)))
      case None =>
        Left(s"invalid command line arguments")
    }

}
