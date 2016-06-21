package com.example.singleton.compute

import com.example.singleton.cache.CacheContainerComponent
import com.example.singleton.{UserRecord, Action, User}

trait ComputeComponent {
  /**
    * Function for calculate spent time between event
    *
    * @param user case class of user
    * @param action event
    * @return user record
    */
  def compute(user: User, action: Action): UserRecord
}



trait ComputeComponentImpl extends ComputeComponent {
  this: CacheContainerComponent
    with DecisionPolicyComponent =>
  /**
    * Function for calculate spent time between event
    *
    * @param user case class of user
    * @param action event
    * @return user record
    */
  override def compute(user: User, action: Action) = {
    val record = cache.get(user)

    record.lastAction match {
      case None =>
        val newRecord = record.copy(lastAction = Some(action))
        cache.put(user, newRecord)
        newRecord
      case Some(lastAction) =>
        val newRecord = policy.difference(lastAction, action) match {
          case Store(value) => UserRecord(record.spentTime + value, record.n + 1)
          case Continue(newLastAction) => record.copy(lastAction = Some(newLastAction))
          case Skip() => record
        }
        cache.put(user, newRecord)
        newRecord
    }
  }
}

