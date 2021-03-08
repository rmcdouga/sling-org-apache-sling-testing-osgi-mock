/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.testing.mock.osgi.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Collects list of context plugins.
 */
@ProviderType
public final class ContextPlugins {

    private final @NotNull List<ContextPlugin<? extends OsgiContextImpl>> plugins = new ArrayList<>();

    /**
     * Start with empty list.
     */
    public ContextPlugins() {
        // empty list
    }

    /**
     * Start with some callbacks.
     * @param <T> context type
     * @param afterSetUpCallback Allows the application to register an own callback function that is called after the built-in setup rules are executed.
     */
    public <T extends OsgiContextImpl> ContextPlugins(@NotNull final ContextCallback<T> afterSetUpCallback) {
        addAfterSetUpCallback(afterSetUpCallback);
    }

    /**
     * Start with some callbacks.
     * @param <U> context type
     * @param <V> context type
     * @param afterSetUpCallback Allows the application to register an own callback function that is called after the built-in setup rules are executed.
     * @param beforeTearDownCallback Allows the application to register an own callback function that is called before the built-in teardown rules are executed.
     */
    public <U extends OsgiContextImpl, V extends OsgiContextImpl> ContextPlugins(
            @NotNull final ContextCallback<U> afterSetUpCallback,
            @NotNull final ContextCallback<V> beforeTearDownCallback) {
        addAfterSetUpCallback(afterSetUpCallback);
        addBeforeTearDownCallback(beforeTearDownCallback);
    }

    /**
     * Add plugin
     * @param <T> context type
     * @param plugin Plugin
     */
    @SuppressWarnings("unused")
    @SafeVarargs
    public final <T extends OsgiContextImpl> void addPlugin(@NotNull ContextPlugin<T> @NotNull ... plugin) {
        for (final ContextPlugin<T> item : plugin) {
            if (item == null) {
                continue;
            }
            plugins.add(item);
        }
    }

    /**
     * Add callback
     * @param <T> context type
     * @param beforeSetUpCallback Allows the application to register an own callback function that is called before the built-in setup rules are executed.
     */
    @SuppressWarnings("unused")
    @SafeVarargs
    public final <T extends OsgiContextImpl> void addBeforeSetUpCallback(@NotNull final ContextCallback<T> @NotNull ... beforeSetUpCallback) {
        for (final ContextCallback<T> item : beforeSetUpCallback) {
            if (item == null) {
                continue;
            }
            plugins.add(new AbstractContextPlugin<T>() {
                @Override
                public void beforeSetUp(@NotNull T context) throws Exception {
                    item.execute(context);
                }
                @Override
                public String toString() {
                    return item.toString();
                }
            });
        }
    }

    /**
     * Add callback
     * @param <T> context type
     * @param afterSetUpCallback Allows the application to register an own callback function that is called after the built-in setup rules are executed.
     */
    @SuppressWarnings("unused")
    @SafeVarargs
    public final <T extends OsgiContextImpl> void addAfterSetUpCallback(@NotNull final ContextCallback<T> @NotNull ... afterSetUpCallback) {
        for (final ContextCallback<T> item : afterSetUpCallback) {
            if (item == null) {
                continue;
            }
            plugins.add(new AbstractContextPlugin<T>() {
                @Override
                public void afterSetUp(@NotNull T context) throws Exception {
                    item.execute(context);
                }
                @Override
                public String toString() {
                    return item.toString();
                }
            });
        }
    }

    /**
     * Add callback
     * @param <T> context type
     * @param beforeTearDownCallback Allows the application to register an own callback function that is called before the built-in teardown rules are executed.
     */
    @SuppressWarnings("unused")
    @SafeVarargs
    public final <T extends OsgiContextImpl> void addBeforeTearDownCallback(@NotNull final ContextCallback<T> @NotNull ... beforeTearDownCallback) {
        for (final ContextCallback<T> item : beforeTearDownCallback) {
            if (item == null) {
                continue;
            }
            plugins.add(new AbstractContextPlugin<T>() {
                @Override
                public void beforeTearDown(@NotNull T context) throws Exception {
                    item.execute(context);
                }
                @Override
                public String toString() {
                    return item.toString();
                }
            });
        }
    }

    /**
     * Add callback
     * @param <T> context type
     * @param afterTearDownCallback Allows the application to register an own callback function that is after before the built-in teardown rules are executed.
     */
    @SuppressWarnings("unused")
    @SafeVarargs
    public final <T extends OsgiContextImpl> void addAfterTearDownCallback(@NotNull final ContextCallback<T> @NotNull ... afterTearDownCallback) {
        for (final ContextCallback<T> item : afterTearDownCallback) {
            if (item == null) {
                continue;
            }
            plugins.add(new AbstractContextPlugin<T>() {
                @Override
                public void afterTearDown(@NotNull T context) throws Exception {
                    item.execute(context);
                }
                @Override
                public String toString() {
                    return item.toString();
                }
            });
        }
    }

    /**
     * @return All plugins
     */
    public @NotNull Collection<ContextPlugin<? extends OsgiContextImpl>> getPlugins() {
        return plugins;
    }

    /**
     * Execute all before setup callbacks.
     * @param <T> context type
     * @param context Context
     */
    @SuppressWarnings("unchecked")
    public <T extends OsgiContextImpl> void executeBeforeSetUpCallback(@NotNull final T context) {
        for (ContextPlugin plugin : plugins) {
            try {
                plugin.beforeSetUp(context);
            }
            catch (Throwable ex) {
                throw new RuntimeException("Before setup failed (" + plugin.toString() + "): " + ex.getMessage(), ex);
            }
        }
    }

    /**
     * Execute all after setup callbacks.
     * @param <T> context type
     * @param context Context
     */
    @SuppressWarnings("unchecked")
    public <T extends OsgiContextImpl> void executeAfterSetUpCallback(@NotNull final T context) {
        for (ContextPlugin plugin : plugins) {
            try {
                plugin.afterSetUp(context);
            }
            catch (Throwable ex) {
                throw new RuntimeException("After setup failed (" + plugin.toString() + "): " + ex.getMessage(), ex);
            }
        }
    }

    /**
     * Execute all before teardown callbacks.
     * @param <T> context type
     * @param context Context
     */
    @SuppressWarnings("unchecked")
    public <T extends OsgiContextImpl> void executeBeforeTearDownCallback(@NotNull final T context) {
        for (ContextPlugin plugin : plugins) {
            try {
                plugin.beforeTearDown(context);
            }
            catch (Throwable ex) {
                throw new RuntimeException("Before teardown failed (" + plugin.toString() + "): " + ex.getMessage(), ex);
            }
        }
    }

    /**
     * Execute all after teardown callbacks.
     * @param <T> context type
     * @param context Context
     */
    @SuppressWarnings("unchecked")
    public <T extends OsgiContextImpl> void executeAfterTearDownCallback(@NotNull final T context) {
        for (ContextPlugin plugin : plugins) {
            try {
                plugin.afterTearDown(context);
            }
            catch (Throwable ex) {
                throw new RuntimeException("After teardown failed (" + plugin.toString() + "): " + ex.getMessage(), ex);
            }
        }
    }

}
