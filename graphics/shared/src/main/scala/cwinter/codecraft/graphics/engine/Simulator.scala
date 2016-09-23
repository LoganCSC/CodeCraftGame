package cwinter.codecraft.graphics.engine

import cwinter.codecraft.util.maths.Vector2

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


private[codecraft] trait Simulator {
  private[this] val framequeue = mutable.Queue.empty[Seq[ModelDescriptor[_]]]
  @volatile private[this] var running = false
  private[this] var paused = false
  protected var tFrameCompleted = System.nanoTime()
  private[this] var targetFPS = 60
  @volatile private[this] var t = -1
  protected def frameMillis = 1000.0 / targetFPS
  protected var stopped = false
  protected var exceptionHandler: Option[Throwable => _] = None
  protected var _measuredFramerate: Int = 0
  protected var _nanoTimeLastMeasurement: Long = 0
  @volatile private var currentlyUpdating = false
  protected[codecraft] var debug = new Debug
  var graphicsEnabled: Boolean = true

  /** Runs the game until the program is terminated. */
  def run(): Unit = synchronized { runInContext() }

  private[codecraft] def runInContext()(implicit ec: ExecutionContext): Unit = {
    require(!running, "Simulator.run() must only be called once.")
    running = true
    Future {
      while (!stopped && gameStatus == Running) {
        if (!paused) performUpdate()
        if (t % framelimitPeriod == 1 || framelimitPeriod == 1) limitFramerate()
      }
    }(ec)
  }

  private def limitFramerate(): Unit = {
    val nanos = System.nanoTime()
    val dt = nanos - tFrameCompleted
    val sleepMillis = framelimitPeriod * frameMillis - dt / 1000000
    if (sleepMillis > 0) Thread.sleep(sleepMillis.toInt)
    tFrameCompleted = System.nanoTime()
  }

  private def performUpdate(): Unit = {
    recomputeGraphicsState()
    t += 1
    try {
      measureFPS()
      update()
    } catch {
      case e: Throwable =>
        exceptionHandler.foreach(_(e))
        if (stopped) return
        e.printStackTrace()
        paused = true
    }
    debug.swapBuffers()
  }

  private[codecraft] def performAsyncUpdate()(implicit ec: ExecutionContext): Future[Unit] = {
    assert(!currentlyUpdating)
    currentlyUpdating = true
    recomputeGraphicsState()
    t += 1
    measureFPS()
    val updateFuture = asyncUpdate()

    // optimization that prevents JavaScript from sometimes skipping an animation frame (in single player)
    if (updateFuture.isCompleted) currentlyUpdating = false

    updateFuture.andThen {
      case Success(_) =>
        currentlyUpdating = false
        debug.swapBuffers()
      case Failure(e) =>
        exceptionHandler.foreach(_(e))
        if (stopped) return Future.successful(Unit)
        e.printStackTrace()
        paused = true
        currentlyUpdating = false
        debug.swapBuffers()
    }
  }

  /** Will run the game for `steps` timesteps. */
  def run(steps: Int): Unit = {
    for (i <- 0 until steps) {
      if (!paused) {
        performUpdate()
        if (stopped || gameStatus != Running) return
      }
    }
  }

  private def measureFPS(): Unit = {
    if (timestep % 60 == 0 && !isPaused) {
      val nanoTimeNow = System.nanoTime()
      _measuredFramerate = (60 * 1000 * 1000 * 1000L / (nanoTimeNow - _nanoTimeLastMeasurement)).toInt
      _nanoTimeLastMeasurement = nanoTimeNow
    }
  }

  private def recomputeGraphicsState(): Unit = framequeue.synchronized {
    if (gameStatus == Running && graphicsEnabled) {
      framequeue.enqueue(debug.debugObjects ++ computeWorldState)
      if (framequeue.size > maxFrameQueueSize) framequeue.dequeue()
    }
  }

  /** Performs one timestep. */
  protected def update(): Unit

  /** Asynchronously performs one timestep.
    * Returns a future which completes once all changes have taken effect.
    */
  protected def asyncUpdate()(implicit ec: ExecutionContext): Future[Unit]

  /** Returns the game's status
    */
  protected def gameStatus: Status

  /** Returns the current timestep. */
  def timestep: Int = t

  /** Pauses or resumes the game as applicable. */
  def togglePause(): Unit = paused = !paused

  /** Sets the target framerate to the given value.
    *
    * @param value The new framerate target.
    */
  def framerateTarget_=(value: Int): Unit = {
    require(value > 0)
    targetFPS = value
  }

  /** Returns the target framerate in frames per second. */
  def framerateTarget: Int = targetFPS

  /** Returns the number of ticks per second measure over the last 60 tick interval. */
  def measuredFramerate: Int = _measuredFramerate

  /** Returns true if the game is currently paused. */
  def isPaused: Boolean = paused

  /** Returns the initial camera position in the game world. */
  def initialCameraPos: Vector2 = Vector2.Null

  /** Terminates any running game loops. */
  def terminate(): Unit = stopped = true

  private[codecraft] def isCurrentlyUpdating: Boolean = currentlyUpdating

  private[codecraft] def onException(callback: Throwable => _): Unit = {
    exceptionHandler = Some(callback)
  }

  private[codecraft] def dequeueFrame(): Seq[ModelDescriptor[_]] = framequeue.synchronized {
    if (framequeue.size > frameQueueThreshold) framequeue.dequeue()
    if (framequeue.size > 1) framequeue.dequeue()
    if (framequeue.isEmpty) Seq.empty else framequeue.front
  }
  private[codecraft] def computeWorldState: Seq[ModelDescriptor[_]]
  private[codecraft] def handleKeypress(keychar: Char): Unit = ()
  private[codecraft] def additionalInfoText: String = ""
  private[codecraft] def textModels: Iterable[TextModel] = debug.textModels

  private[codecraft] def frameQueueThreshold: Int
  private[codecraft] def maxFrameQueueSize: Int
  private[codecraft] def framelimitPeriod: Int

  protected sealed trait Status
  protected case object Running extends Status
  protected case class Stopped(reason: String) extends Status
  protected case class Crashed(exception: Throwable) extends Status

  def forceGL2: Boolean = false
}

