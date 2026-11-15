import org.scalatest.funsuite.AnyFunSuite

class StatusBotSpec extends AnyFunSuite {
  test("report has all five sections") {
    val r = StatusBot.buildReport("finished login; working on API; blocked on token; need tests")
    List("Executive Summary", "Completed Milestones", "Work In Progress",
         "Roadblocks and Risks", "Action Items").foreach(s => assert(r.contains(s)))
  }
}
