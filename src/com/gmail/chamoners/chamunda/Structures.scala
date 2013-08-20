package com.gmail.chamoners.chamunda

import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger
import org.bukkit.Location
import org.bukkit.World
import scala.collection.JavaConverters._
import Preamble._
import com.gmail.chamoners.chamunda.Zeit._

case class Point(x: Int, z: Int) {
  def -(op: Point): Point = {
    new Point(x - op.x, z - op.z)
  }

  def +(op: Point): Point = {
    new Point(x + op.x, z + op.z)
  }

  def /(op: Int): Point = {
    new Point(x / op, z / op)
  }

  def apply(wrld: World): Location = {
    new Location(wrld, x, 0, z)
  }

  def abs(): Point = {
    new Point(math.abs(x), math.abs(z))
  }

}

case class Environment(plugin: JavaPlugin) {
  val server = plugin.getServer
  val world = server.getWorlds.get(0) //Plugin affects only Default World
  val log = Logger.getLogger("Minecraft")
  val vill = Village(Point(-149, 647), Point(-94, 711), this)
  var zeit: Zeit = calcZeit()

  def calcZeit(): Zeit = {
    val time = world.getTime()
    if (time >= Zeit.Dawn.getTime() && time < Zeit.Day.getTime()) Zeit.Dusk
    else if (time >= Zeit.Day.getTime() || time < Zeit.Dusk.getTime()) Zeit.Day
    else if (time >= Zeit.Dusk.getTime() && time < Zeit.Night.getTime()) Zeit.Dusk
    else if (time >= Zeit.Night.getTime() && time < Zeit.Dawn.getTime()) Zeit.Night
    else throw new Exception("ZeitCalc failed, shouldn't happen")
  }

  def changeZeit(newZeit: Zeit) {
    world.setTime(newZeit.getTime())
    zeit = newZeit
    server.getPluginManager().callEvent(new ZeitgeberEvent(newZeit))
  }

  def execute(ticks: Int = 0)(f: => Any) = {
    server.getScheduler().scheduleSyncRepeatingTask(plugin, f, 0, ticks)
  }

  def executeOnce(delay: Int = 0)(f: => Any) = {
    server.getScheduler().scheduleSyncDelayedTask(plugin, f, delay)
  }

  def randomPlayer = {
    val players = world.getPlayers.asScala
    players(scala.util.Random.nextInt(players.length))
  }
}

