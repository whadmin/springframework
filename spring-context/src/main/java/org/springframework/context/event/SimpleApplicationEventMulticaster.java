/*
 * Copyright 2002-2018 the original author or authors.
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

import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.util.ErrorHandler;

/**
 * 简单实现{@link ApplicationEventMulticaster}接口。
 *
 * <p>将所有事件多播到所有已注册的侦听器，将其留给侦听器以忽略他们不感??兴趣的事件。*侦听器通常会对传入的事件对象执行相应的{@code instanceof} *检查。
 *
 * <p>默认情况下，在调用线程中调用所有侦听器。 , *这允许恶意侦听器阻止整个应用程序的危险，*但增加了最小的开销。, 指定备用任务执行程序以使*侦听器在不同的线程中执行，例如从线程池中执行。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @see #setTaskExecutor
 */
public class SimpleApplicationEventMulticaster extends AbstractApplicationEventMulticaster {

	private Executor taskExecutor;

	private ErrorHandler errorHandler;


	/**
	 * 创建一个新的SimpleApplicationEventMulticaster。
	 */
	public SimpleApplicationEventMulticaster() {
	}

	/**
	 * 为给定内部容器bean工厂创建一个新的SimpleApplicationEventMulticaster。
	 */
	public SimpleApplicationEventMulticaster(BeanFactory beanFactory) {
		setBeanFactory(beanFactory);
	}


	/**
	 * 设置自定义执行程序（通常是{@link org.springframework.core.task.TaskExecutor}）*以异步通知调用每个侦听器。
	 * <p>默认等效于{@link org.springframework.core.task.SyncTaskExecutor}，
	 * 在调用线程中同步执行所有侦听器。 ,
	 * <p>考虑在此指定一个异步任务执行器，以便在所有侦听器都被执行之前不阻塞*调用者。, 但是，请注意异步*执行不会参与调用者的线程上下文（类加载器，*事务关联），除非TaskExecutor明确支持此操作。
	 * @see org.springframework.core.task.SyncTaskExecutor
	 * @see org.springframework.core.task.SimpleAsyncTaskExecutor
	 */
	public void setTaskExecutor(Executor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	/**
	 * 返回当前任务执行程序。
	 */
	protected Executor getTaskExecutor() {
		return this.taskExecutor;
	}

	/**
	 * 如果从侦听器抛出异常*，请设置{@link ErrorHandler}以进行调用。
	 * @since 4.1
	 */
	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	/**
	 * 返回此多播器的当前错误处理程序。{@link ErrorHandler}
	 * @since 4.1
	 */
	protected ErrorHandler getErrorHandler() {
		return this.errorHandler;
	}


	/**
	 * 发布事件
	 */
	@Override
	public void multicastEvent(ApplicationEvent event) {
		multicastEvent(event, resolveDefaultEventType(event));
	}


	/**
	 * 发布事件
	 */
	@Override
	public void multicastEvent(final ApplicationEvent event, ResolvableType eventType) {
		ResolvableType type = (eventType != null ? eventType : resolveDefaultEventType(event));
		for (final ApplicationListener<?> listener : getApplicationListeners(event, type)) {
			Executor executor = getTaskExecutor();
			if (executor != null) {
				executor.execute(new Runnable() {
					@Override
					public void run() {
						invokeListener(listener, event);
					}
				});
			}
			else {
				invokeListener(listener, event);
			}
		}
	}

	/**
	 *  获取事件的类型
	 * */
	private ResolvableType resolveDefaultEventType(ApplicationEvent event) {
		return ResolvableType.forInstance(event);
	}

	/**
	 * 使用给定的事件调用给定的侦听器。
	 * @param listener the ApplicationListener to invoke
	 * @param event the current event to propagate
	 * @since 4.1
	 */
	protected void invokeListener(ApplicationListener<?> listener, ApplicationEvent event) {
		ErrorHandler errorHandler = getErrorHandler();
		if (errorHandler != null) {
			try {
				doInvokeListener(listener, event);
			}
			catch (Throwable err) {
				errorHandler.handleError(err);
			}
		}
		else {
			doInvokeListener(listener, event);
		}
	}

	private void doInvokeListener(ApplicationListener listener, ApplicationEvent event) {
		try {
			listener.onApplicationEvent(event);
		}
		catch (ClassCastException ex) {
			String msg = ex.getMessage();
			if (msg == null || matchesClassCastMessage(msg, event.getClass())) {
				// Possibly a lambda-defined listener which we could not resolve the generic event type for
				// -> let's suppress the exception and just log a debug message.
				Log logger = LogFactory.getLog(getClass());
				if (logger.isDebugEnabled()) {
					logger.debug("Non-matching event type for listener: " + listener, ex);
				}
			}
			else {
				throw ex;
			}
		}
	}

	private boolean matchesClassCastMessage(String classCastMessage, Class<?> eventClass) {
		// On Java 8, the message starts with the class name: "java.lang.String cannot be cast..."
		if (classCastMessage.startsWith(eventClass.getName())) {
			return true;
		}
		// On Java 11, the message starts with "class ..." a.k.a. Class.toString()
		if (classCastMessage.startsWith(eventClass.toString())) {
			return true;
		}
		// On Java 9, the message used to contain the module name: "java.base/java.lang.String cannot be cast..."
		int moduleSeparatorIndex = classCastMessage.indexOf('/');
		if (moduleSeparatorIndex != -1 && classCastMessage.startsWith(eventClass.getName(), moduleSeparatorIndex + 1)) {
			return true;
		}
		// Assuming an unrelated class cast failure...
		return false;
	}

}
