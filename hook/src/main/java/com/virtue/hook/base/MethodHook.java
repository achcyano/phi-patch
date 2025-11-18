package com.virtue.hook.base;

/**
 * Base class for method hooking
 * Provides before and after callbacks
 */
public abstract class MethodHook {
    
    /**
     * Called before the original method
     */
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        // Override in subclass
    }

    /**
     * Called after the original method
     */
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        // Override in subclass
    }

    public static class MethodHookParam {
        public Object thisObject;
        public Object[] args;
        public Object result;
        public Throwable throwable;
        private boolean returnEarly = false;

        public void setResult(Object result) {
            this.result = result;
            this.returnEarly = true;
            this.throwable = null;
        }

        public void setThrowable(Throwable throwable) {
            this.throwable = throwable;
            this.returnEarly = true;
            this.result = null;
        }

        public boolean hasThrowable() {
            return throwable != null;
        }

        public boolean getReturnEarly() {
            return returnEarly;
        }
    }
}
