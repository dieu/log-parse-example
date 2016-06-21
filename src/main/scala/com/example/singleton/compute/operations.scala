package com.example.singleton.compute

import com.example.singleton.Action

trait Operation

case class Store(val value: Long) extends Operation
case class Continue(val action: Action) extends Operation
case class Skip() extends Operation