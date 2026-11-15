import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands

object StatusBot extends ListenerAdapter {

  def buildReport(raw: String): String = {
    val bullets = raw.split("[\\n;]").map(_.trim.stripPrefix("-").trim).filter(_.nonEmpty).toList
    def pick(words: String*) =
      bullets.filter(b => words.exists(w => b.toLowerCase.contains(w)))
    val done    = pick("finished", "completed", "done", "shipped")
    val blocked = pick("blocked", "risk", "issue", "stuck")
    val action  = pick("need", "next", "will", "plan", "todo")
    val wip     = bullets.filterNot(b => (done ++ blocked ++ action).contains(b))
    def sec(title: String, items: List[String]) =
      s"**$title**\n" + (if (items.isEmpty) "- None" else items.map("- " + _).mkString("\n"))
    List(
      s"**Executive Summary**\n${done.size} completed, ${wip.size} in progress, ${blocked.size} roadblocks.",
      sec("Completed Milestones", done),
      sec("Work In Progress", wip),
      sec("Roadblocks and Risks", blocked),
      sec("Action Items", action)
    ).mkString("\n\n")
  }

  override def onSlashCommandInteraction(e: SlashCommandInteractionEvent): Unit =
    if (e.getName == "statusreport") {
      val raw = Option(e.getOption("bullets")).map(_.getAsString).getOrElse("")
      if (raw.trim.isEmpty) e.reply("EMPTY_INPUT: Please enter your bullet points.").setEphemeral(true).queue()
      else e.reply(buildReport(raw)).queue()
    }

  def main(args: Array[String]): Unit = {
    val token = sys.env.getOrElse("DISCORD_BOT_TOKEN", sys.error("DISCORD_BOT_TOKEN not set"))
    val jda = JDABuilder.createLight(token).addEventListeners(this).build().awaitReady()
    jda.updateCommands().addCommands(
      Commands.slash("statusreport", "Generate a weekly status report")
        .addOption(OptionType.STRING, "bullets", "Rough bullets, separated by ; or new lines", false)
    ).queue()
    println("Bot connected")
  }
}
