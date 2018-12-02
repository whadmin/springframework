/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.ResolvableType;

/**
 * 由可以管理许多* {@link ApplicationListener}对象并向其发布事件的对象实现的接口。
 *
 * {@link org.springframework.context.ApplicationEventPublisher}，通常是
 * Spring {@link org.springframework.context.ApplicationContext}，可以使用
 * ApplicationEventMulticaster作为实际发布事件的委托。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 */
public interface ApplicationEventMulticaster {

	/**
	 * 添加一个侦听器以通知所有事件。 ,
	 * @param listener 监听器添加侦听器
	 */
	void addApplicationListener(ApplicationListener<?> listener);

	/**
	 * 添加一个监听器bean以通知所有事件。
	 * @param listenerBeanName 要添加的侦听器bean的名称
	 */
	void addApplicationListenerBean(String listenerBeanName);

	/**
	 * 从侦听器列表中删除侦听器。
	 * @param listener 要删除的监听器
	 */
	void removeApplicationListener(ApplicationListener<?> listener);

	/**
	 * 从侦听器列表中删除侦听器bean。
	 * @param listenerBeanName 要删除的侦听器bean的名称
	 */
	void removeApplicationListenerBean(String listenerBeanName);

	/**
	 * 删除在此多播程序中注册的所有侦听器。 ,
	 * <p>在删除调用之后，多播器将在事件通知中不执行任何操作
	 * 直到注册新的侦听器。
	 */
	void removeAllListeners();

	/**
	 * 将给定的应用程序事件多播到适当的侦听器。 ,
	 * <p>如果可能，请考虑使用{@link #multicastEvent（ApplicationEvent，ResolvableType）}
	 *，因为它为基于泛型的事件提供了更好的支持。
	 * @param event the event to multicast
	 */
	void multicastEvent(ApplicationEvent event);

	/**
	 * 将给定的应用程序事件多播到适当的侦听器。 ,
	 * <p>如果{@code eventType}为{@code null}，则基于{@code event}实例构建*的默认类型。
	 * @param event the event to multicast
	 * @param eventType the type of event (can be null)
	 * @since 4.2
	 */
	void multicastEvent(ApplicationEvent event, ResolvableType eventType);

}
