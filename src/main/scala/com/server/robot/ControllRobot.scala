package com.server.robot

import com.fazecast.jSerialComm.SerialPort

class ControllRobot {
  var serialPort = SerialPort.getCommPort("/dev/ttyUSB0")

  def config(): Unit = {
    serialPort.openPort();
    serialPort.setBaudRate(230400)
    serialPort.writeBytes("AT+CONNL".getBytes, "AT+CONNL".length)
    serialPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0)
  }

  def sendCommand(commands: String): Unit = {
    serialPort.writeBytes(commands.getBytes(), commands.length);
  }

  def controllRobot(): Unit = {

  }
}