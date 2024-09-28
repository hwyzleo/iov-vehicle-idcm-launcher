package net.hwyz.iov.idcm.launcher.observer

/**
 * 用户交互观察者对象
 *
 * @author hwyz_leo
 */
object InteractionObserverObject {

    private val interactionObservable = InteractionObservable<Interaction>()
    private var isPause = true

    /**
     * 添加监听对象.
     */
    fun attach(observer: InteractionObserver<Interaction>) {
        interactionObservable.attach(observer)
    }

    /**
     * 取消监听对象.
     */
    fun detach(observer: InteractionObserver<Interaction>) {
        interactionObservable.detach(observer)
    }

    /**
     * 通知监听.
     */
    fun notify(data: Interaction) {
        if (data == Interaction.SLIDE_START) {
            isPause = false
            interactionObservable.notify(data)
        } else if (isPause.not()) {
            interactionObservable.notify(data)
            isPause = true
        }
    }

}