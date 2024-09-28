package net.hwyz.iov.idcm.launcher.observer

/**
 * 用户交互观察者实现类
 *
 * @author hwyz_leo
 */
class InteractionObservable<T> : InteractionSubject<T> {

    private val observers = arrayListOf<InteractionObserver<T>>()

    override fun attach(observer: InteractionObserver<T>) {
        if (observers.contains(observer).not()) {
            observers.add(observer)
        }
    }

    override fun detach(observer: InteractionObserver<T>) {
        observers.remove(observer)
    }

    override fun notify(data: T) {
        observers.forEach {
            it.noticeInteractionState(data)
        }
    }

}