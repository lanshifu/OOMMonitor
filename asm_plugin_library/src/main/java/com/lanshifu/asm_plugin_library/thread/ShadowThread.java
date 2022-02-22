package com.lanshifu.asm_plugin_library.thread;

import android.util.Log;

/**
 * @author johnsonlee
 */
public class ShadowThread extends Thread {

    private Runnable mRunnable;
    /**
     * {@code U+200B}: Zero-Width Space
     */
    static final String MARK = "\u200B";

    public static Thread newThread(final String prefix) {
        return new Thread(prefix);
    }

    public static Thread newThread(final Runnable target, final String prefix) {
        return new Thread(target, prefix);
    }

    public static Thread newThread(final ThreadGroup group, final Runnable target, final String prefix) {
        return new Thread(group, target, prefix);
    }

    public static Thread newThread(final String name, final String prefix) {
        return new Thread(makeThreadName(name, prefix));
    }

    public static Thread newThread(final ThreadGroup group, final String name, final String prefix) {
        return new Thread(group, makeThreadName(name, prefix));
    }

    public static Thread newThread(final Runnable target, final String name, final String prefix) {
        return new Thread(target, makeThreadName(name, prefix));
    }

    public static Thread newThread(final ThreadGroup group, final Runnable target, final String name, final String prefix) {
        return new Thread(group, target, makeThreadName(name, prefix));
    }

    public static Thread newThread(final ThreadGroup group, final Runnable target, final String name, final long stackSize, final String prefix) {
        return new Thread(group, target, makeThreadName(name, prefix), stackSize);
    }

    public static Thread setThreadName(final Thread t, final String prefix) {
        t.setName(makeThreadName(t.getName(), prefix));
        return t;
    }

    public static String makeThreadName(final String name) {
        return name == null ? "" : name.startsWith(MARK) ? name : (MARK + name);
    }

    public static String makeThreadName(final String name, final String prefix) {
        return name == null ? prefix : (name.startsWith(MARK) ? name : (prefix + "#" + name));
    }

    /**
     * Initialize {@code Thread} with new name, this constructor is used by {@code ThreadTransformer} for renaming
     *
     * @param prefix the new name
     */
    public ShadowThread(final String prefix) {
        super(makeThreadName(prefix));
    }

    /**
     * Initialize {@code Thread} with new name, this constructor is used by {@code ThreadTransformer} for renaming
     *
     * @param target the object whose {@code run} method is invoked when this thread is started.
     *               If {@code null}, this thread's run method is invoked.
     * @param prefix the new name
     */
    public ShadowThread(final Runnable target, final String prefix) {
        super(target, makeThreadName(prefix));
    }

    /**
     * Initialize {@code Thread} with new name, this constructor is used by {@code ThreadTransformer} for renaming
     *
     * @param group  the thread group
     * @param target the object whose {@code run} method is invoked when this thread is started.
     *               If {@code null}, this thread's run method is invoked.
     * @param prefix the new name
     */
    public ShadowThread(final ThreadGroup group, final Runnable target, final String prefix) {
        super(group, target, makeThreadName(prefix));
    }

    /**
     * Initialize {@code Thread} with new name, this constructor is used by {@code ThreadTransformer} for renaming
     *
     * @param name   the original name
     * @param prefix the prefix of new name
     */
    public ShadowThread(final String name, final String prefix) {
        super(makeThreadName(name, prefix));
    }

    /**
     * Initialize {@code Thread} with new name, this constructor is used by {@code ThreadTransformer} for renaming
     *
     * @param group  the thread group
     * @param name   the original name
     * @param prefix the prefix of new name
     */
    public ShadowThread(final ThreadGroup group, final String name, final String prefix) {
        super(group, makeThreadName(name, prefix));
    }

    /**
     * Initialize {@code Thread} with new name, this constructor is used by {@code ThreadTransformer} for renaming
     *
     * @param target the object whose {@code run} method is invoked when this thread is started.
     *               If {@code null}, this thread's run method is invoked.
     * @param name   the original name
     * @param prefix the prefix of new name
     */
    public ShadowThread(final Runnable target, final String name, final String prefix) {
        super(target, makeThreadName(name, prefix));
    }

    /**
     * Initialize {@code Thread} with new name, this constructor is used by {@code ThreadTransformer} for renaming
     *
     * @param group  the thread group
     * @param target the object whose {@code run} method is invoked when this thread is started.
     *               If {@code null}, this thread's run method is invoked.
     * @param name   the original name
     * @param prefix the prefix of new name
     */
    public ShadowThread(final ThreadGroup group, final Runnable target, final String name, final String prefix) {
        super(group, target, makeThreadName(name, prefix));
    }

    /**
     * Initialize {@code Thread} with new name, this constructor is used by {@code ThreadTransformer} for renaming
     *
     * @param group     the thread group
     * @param target    the object whose {@code run} method is invoked when this thread is started.
     *                  If {@code null}, this thread's run method is invoked.
     * @param name      the original name
     * @param stackSize the desired stack size for the new thread, or zero to indicate that this parameter is to be ignored.
     * @param prefix    the prefix of new name
     */
    public ShadowThread(final ThreadGroup group, final Runnable target, final String name, final long stackSize, final String prefix) {
        super(group, target, makeThreadName(name, prefix), stackSize);
    }


    @Override
    public synchronized void start() {
        Log.i("ShadowThread", "start,name="+ getName());
        CustomThreadPool.THREAD_POOL_EXECUTOR.execute(new MyRunnable(getName()));
    }

    class MyRunnable implements Runnable {

        String name;
        public MyRunnable(String name){
            this.name = name;
        }

        @Override
        public void run() {
            try {
                ShadowThread.this.run();
                Log.d("ShadowThread","run name="+name);
            } catch (Exception e) {
                setName(name);
                Log.w("ShadowThread","name="+name+",exception:"+ e.getMessage());
                e.printStackTrace();
                RuntimeException exception = new RuntimeException("threadName="+name+",exception:"+ e.getMessage());
                exception.setStackTrace(e.getStackTrace());
                throw exception;
            }
        }
    }
}
