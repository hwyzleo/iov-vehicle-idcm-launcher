package net.hwyz.iov.idcm.launcher.observer

/**
 * 用户交互主题
 *
 * @author hwyz_leo
 */
interface InteractionSubject<T> {

    /**
     * 注册观察者
     */
    fun attach(observer: InteractionObserver<T>)

    /**
     * 注销观察者
     */
    fun detach(observer: InteractionObserver<T>)

    /**
     * 通知观察者
     */
    fun notify(data: T)

}