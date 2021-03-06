package cwinter.codecraft.core.multiplayer

import java.nio.ByteBuffer

private[core] trait WebsocketClient {
  protected var onOpenCallbacks = Vector.empty[WebsocketClient => Unit]
  protected var onMessageCallbacks = Vector.empty[(WebsocketClient, ByteBuffer) => Unit]
  protected var onCloseCallbacks = Vector.empty[WebsocketClient => Unit]

  def connect(): Unit

  def sendMessage(message: ByteBuffer): Unit

  def registerOnOpen(callback: WebsocketClient => Unit): Unit = synchronized {
    onOpenCallbacks :+= callback
  }

  def registerOnMessage(callback: (WebsocketClient, ByteBuffer) => Unit): Unit = synchronized {
    onMessageCallbacks :+= callback
  }

  def registerOnClose(callback: WebsocketClient => Unit): Unit = synchronized {
    onCloseCallbacks :+= callback
  }

  protected def runOnMessageCallbacks(msg: ByteBuffer): Unit = onMessageCallbacks.foreach(_(this, msg))

  protected def runOnOpenCallbacks(): Unit = onOpenCallbacks.foreach(_(this))

  protected def runOnCloseCallbacks(): Unit = onCloseCallbacks.foreach(_(this))
}
