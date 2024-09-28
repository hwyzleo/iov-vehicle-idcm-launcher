package net.hwyz.iov.idcm.launcher.observer

/**
 * 用户交互观察者接口
 *
 * @author hwyz_leo
 */
interface InteractionObserver<T> {

    /**
     * 通知用户交互状态变更
     */
    fun noticeInteractionState(data: T)

}